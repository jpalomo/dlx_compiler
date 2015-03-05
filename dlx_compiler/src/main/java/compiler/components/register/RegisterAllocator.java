package compiler.components.register;

import static compiler.components.intermediate_rep.BasicBlock.BlockType.ELSE_BODY;
import static compiler.components.intermediate_rep.BasicBlock.BlockType.FUNCTION;
import static compiler.components.intermediate_rep.BasicBlock.BlockType.IF_BODY;
import static compiler.components.intermediate_rep.BasicBlock.BlockType.IF_JOIN;
import static compiler.components.intermediate_rep.BasicBlock.BlockType.PROGRAM;
import static compiler.components.intermediate_rep.BasicBlock.BlockType.WHILE_FOLLOW;
import static compiler.components.intermediate_rep.BasicBlock.BlockType.WHILE_JOIN;
import static compiler.components.intermediate_rep.BasicBlock.BlockType.WHILE_BODY;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import compiler.components.intermediate_rep.BasicBlock;
import compiler.components.parser.Instruction;
import compiler.components.parser.Instruction.OP;

public class RegisterAllocator {
	static Logger LOGGER = LoggerFactory.getLogger(RegisterAllocator.class);

	public static Map<Integer, Integer> frequencies = new HashMap<Integer, Integer>();
	private Queue<BasicBlock> blocksToProcess = new LinkedList<BasicBlock>();

	Map<Integer, Instruction> programInstructions;
	List<Integer> phiInstructionNumbers;

	BasicBlock leafBlock;
	Set<Integer> processedBlocks;
	public InterferenceGraph IGraph;
	
	Set<BasicBlock> visited = new HashSet<BasicBlock>();

	public RegisterAllocator(Map<Integer, Instruction> programInstructions, List<Integer> phiInstructionNumbers) {
		processedBlocks = new HashSet<Integer>();
		this.programInstructions = programInstructions;
		this.phiInstructionNumbers = phiInstructionNumbers;
		this.IGraph = new InterferenceGraph(frequencies);
	}

	public void buildGraphAndAllocate(BasicBlock rootBlock) {
		leafBlock = rootBlock;
		depthFirstToLeaf(rootBlock);
		blocksToProcess.add(leafBlock);
		while(!blocksToProcess.isEmpty()) {
			calculateLivenessBottomUp(blocksToProcess.remove());
		}
		IGraph.intepretCosts(frequencies);
		buildClusters();
		colorGraph(IGraph);
		removePhis(IGraph);
	}
	
	private void buildClusters() {
		
		for(int phiInstructionNum : phiInstructionNumbers) {
			Instruction phiInstruction = programInstructions.get(phiInstructionNum);

			SuperNode cluster = new SuperNode();
			
			List<INode> clusterNodes = new ArrayList<INode>();
			
			Instruction inst1 = programInstructions.get(phiInstruction.leftOperand.instrNum);
			Instruction inst2 = programInstructions.get(phiInstruction.rightOperand.instrNum);
			
			if(IGraph.getNode(phiInstructionNum) != null) {
				clusterNodes.add(IGraph.getNode(phiInstructionNum));
			} else {
				clusterNodes.add(new INode(phiInstructionNum));
			}

			if(!inst1.op.equals(OP.CP_CONST)) {
				INode node;
				if(IGraph.nodesToClusters.containsKey(inst1.instNum)) {
					node = IGraph.nodesToClusters.get(inst1.instNum);
				} else {
					node = IGraph.getNode(inst1.instNum);
				}
			
				if(node != null) {
					//check interference with phi
					boolean interfere = false;
					for(int i : node.neighbors) {
						if (i == phiInstructionNum) {
							interfere = true;;
							break;
						}
					}

					if(!interfere) {
						// add node to cluster
						clusterNodes.add(node);
						// remove node from the graph if it doesn't interfere
						IGraph.updateNeighbors(node, cluster);
						IGraph.removeFromGraph(node);
					} 
				}
			}

			if(! inst2.op.equals(OP.CP_CONST)) {
				// check if the node is in a cluster and assign the cluster to node
				INode node;
				if(IGraph.nodesToClusters.containsKey(inst2.instNum)) {
					node = IGraph.nodesToClusters.get(inst2.instNum);
				} else {
					node = IGraph.getNode(inst2.instNum);
				}
				if(node != null) {
					//check interference with phi
					boolean interfere = false;
					for(int i : node.neighbors) {
						if (i == phiInstructionNum) {
							interfere = true;
							break;
						}
					}

					if(!interfere) {
						// add node to cluster
						clusterNodes.add(node);
						// remove node from the graph if it doesn't interfere
						IGraph.updateNeighbors(node, cluster);
						IGraph.removeFromGraph(node);
					} 
				}
			}
			
			//if clusternodes.size() > 1, create a new superNode and add all specific ties needed
			if (clusterNodes.size() > 1) {
				
				cluster.internalNodes.addAll(clusterNodes);
				
				
				for(INode i : cluster.internalNodes) {
					IGraph.nodesToClusters.put(i.nodeNumber, cluster);
				}
				
				// remove the phiInstruction number node and replace with the cluster
				IGraph.removeFromGraph(clusterNodes.get(0));
				IGraph.updateNeighbors(clusterNodes.get(0), cluster);
				
				// add all the neighbors/edges to the supernode class of all the clusternodes within it
				for (INode i : clusterNodes) {
					for(int j : i.neighbors) {
						
						if(IGraph.nodesToClusters.containsKey(j)) {
							cluster.neighbors.add(IGraph.nodesToClusters.get(j).nodeNumber);
						} 
						else {
							if(!cluster.neighbors.contains(j) && j != cluster.nodeNumber) {
								cluster.neighbors.add(j);
							}
						}
					}
				}
				
				// add cluster to the interference graph
				IGraph.addNodeToGraph(cluster);
			}
		}
	}
	
	public void colorGraph(InterferenceGraph ig) {
		// choose arbitrary node with fewer than N colors, else get lowest cost node (findeNodeWithEdgesLess does both searches for us already)
		int N = 8;
		INode x = ig.findNodeWithEdgesLess(N);
		// check if x is a cluster and update its neighbors
		if(x.nodeNumber < 0) {
			// then its a cluster and need to add the neighbors of each node within it to the temp list 
			// which is used to rebuilt the graph again later
			for(SuperNode s : ig.nodesToClusters.values()) {
				if(s.nodeNumber == x.nodeNumber) {
					x.internalNodes = s.internalNodes;
					break;
				}
			}
			for(INode n : x.internalNodes) {
				for(int i : n.neighbors) {
					if(i != x.nodeNumber && !x.neighbors.contains(i)) {
						x.neighbors.add(i);
					}
				}
			}
		}
		List<Integer> temp = new ArrayList<Integer>(x.neighbors);
		
		// remove x from the graph
		ig.removeFromGraphTemp(x);
		
		// if graph is not empty, recursively call
		if(!ig.isGraphEmpty()) {
			colorGraph(ig);
		}
		// add x back to g, and re-add its edges as well
		ig.addNodeToGraph(x);
		
		// choose a color for x that is different from its neighbors
		assignColor(x);
	}
	
	public void assignColor(INode x) {
		// finds and assigns a color/register to x based on the registers assigned to its neighbors
		int color = 1;	// analogous to register number
		List<Integer> neighborRegs = new ArrayList<Integer>();
		for(int i = 0; i < x.neighbors.size(); i++) {
			INode temp = IGraph.getNode(x.neighbors.get(i));
			if (temp != null && color == temp.registerNumber && !neighborRegs.contains(color)) {
				neighborRegs.add(color);
				color++;
				i = -1;
			}
		}
		
		LOGGER.debug("Assigning register {} to node {}", color, x.nodeNumber);
		
		x.registerNumber = color;
		
	}

	public void depthFirstToLeaf(BasicBlock root) {
		visited.add(root);
		for(BasicBlock child : root.controlFlow) {
			if(child.blockType.equals(FUNCTION)) {
				continue;
			}

			if(child.controlFlow.isEmpty()) {
				LOGGER.debug("Found leaf of control flow graph: {}", child.blockNumber);
				leafBlock = child;
				break;
			}
			if (!visited.contains(child)) {
				depthFirstToLeaf(child);
				visited.add(child);
			}
			
		} 
	}

	private void removePhis(InterferenceGraph IGraph) {
		
		for(int p : phiInstructionNumbers) {
			
			Instruction phiIns = programInstructions.get(p);
			
			Instruction inst1 = programInstructions.get(phiIns.leftOperand.instrNum);
			Instruction inst2 = programInstructions.get(phiIns.rightOperand.instrNum);
			
			// phi   : (1)    (2)		
			// node1 : node2 node3
			
			INode node1 = IGraph.getNode(phiIns.instNum);
			
			if(!inst1.op.equals(OP.CP_CONST)) {
				INode node2 = IGraph.getNode(inst1.instNum);
			}
			
			if(!inst2.op.equals(OP.CP_CONST)) {
				INode node3 = IGraph.getNode(inst2.instNum);
			}
			
			// CONTINUE HERE
			// check the register value assigned to each node
			
		}
		
		
	}
	
	private void calculateLivenessBottomUp(BasicBlock currentBlock) {
		//add all the parents to the queue
		for(BasicBlock parent : currentBlock.parents) {
			boolean added = processedBlocks.add(parent.blockNumber);

			if(added) {
				blocksToProcess.add(parent);
				LOGGER.debug("Added block number: {} to queue to be processed", parent.blockNumber);
			}
		}

		for(BasicBlock child : currentBlock.controlFlow) {
			if(child.liveSet == null && !blocksToProcess.contains(child)) {
				//All children need to have their live sets calculated, go to the back
				//of the queue and wait for them to be processed
				LOGGER.debug("Got to block number {}, but children had not been processed.  Adding to queue.", currentBlock.blockNumber); 
				blocksToProcess.add(currentBlock);
				return;
			}
		}

		if(currentBlock.blockType.equals(IF_BODY)) {
			if (currentBlock.controlFlow.size() <= 1) {
				calculateLive3(currentBlock, true); 
				currentBlock.calculateLiveSet(programInstructions, IGraph);
			}
			else {
				//previously computed the block as an if body, but it was actually nested and should be a
				//program block (e.g. if header)
				currentBlock.blockType = PROGRAM;
			}
		}
		else if(currentBlock.blockType.equals(ELSE_BODY)) {
			if( currentBlock.controlFlow.size() <= 1) {
				calculateLive3(currentBlock, false); 
				currentBlock.calculateLiveSet(programInstructions, IGraph);
			}
			else {
				//previously computed the block as an if body, but it was actually nested and should be a
				//program block (e.g. if header)
				currentBlock.blockType = PROGRAM;
			}
		}
		else if(currentBlock.blockType.equals(IF_JOIN)) {
			initializeLiveSet(currentBlock);
			currentBlock.calculateLiveSet(programInstructions, IGraph);
		}
		else if(currentBlock.blockType.equals(WHILE_FOLLOW)) {
			// while follow blocks don't do much and should be treated as regular program blocks, in both nested and non-nested whiles
			initializeLiveSet(currentBlock);
			currentBlock.calculateLiveSet(programInstructions, IGraph);

		}
		else if(currentBlock.blockType.equals(WHILE_JOIN)) {
			// all comments consistent with the example he gave in class about how to calculate live ranges for whiles
			
			// identify while body block
			BasicBlock whileBody = getWhileBodyBlock(currentBlock);
			// identify while follow block
			BasicBlock whileFollow = getWhileFollowBlock(currentBlock);
			
			// live0 + live1 (live1 is empty here)
			currentBlock.liveSet = new HashSet<Integer>();
			// it is possible that the whileFollow liveset is empty, just an if to account for that
			if(whileFollow.liveSet != null) {
				currentBlock.liveSet.addAll(whileFollow.liveSet);
			}
			
			// go up lines in whilejoin block and update live ranges to get live2
			currentBlock.calculateLiveSet(programInstructions, IGraph);
			
			// go up lines in whleBody to get live1'
			whileBody.liveSet = new HashSet<Integer>();
			
			// calculate live1'
			// live2 + right side of phis ({y})
			initializeLiveSet(whileBody);
			whileBody.liveSet.addAll(currentBlock.liveSet);
			whileBody.liveSet.addAll(currentBlock.getRightPhis(programInstructions));
			whileBody.calculateLiveSet(programInstructions, IGraph);
			
			// live1' + live0
			currentBlock.liveSet.clear();
			currentBlock.liveSet.addAll(whileBody.liveSet);
			currentBlock.liveSet.addAll(whileFollow.liveSet);
			
			// calculate live2'
			currentBlock.calculateLiveSet(programInstructions, IGraph);
			
			// live2' - phiInstructionNumbers + leftSideOfPhi
			currentBlock.liveSet.removeAll(currentBlock.getPhiInstructionNumbers());
			currentBlock.liveSet.addAll(currentBlock.getLeftPhis(programInstructions));
			
		}

		if(currentBlock.blockType.equals(PROGRAM)) {
			initializeLiveSet(currentBlock);
			currentBlock.calculateLiveSet(programInstructions, IGraph);
		}

			
	}

	private void initializeLiveSet(BasicBlock currentBlock) {
		currentBlock.liveSet = new HashSet<Integer>();
		for(BasicBlock child : currentBlock.controlFlow) {
			currentBlock.liveSet.addAll(child.liveSet);
		}
	}
	
	private BasicBlock getWhileBodyBlock(BasicBlock whileJoin) {
		BasicBlock whileBody = null;
		for(BasicBlock child : whileJoin.controlFlow) {
			if (child.blockType.equals(WHILE_BODY)) {
				whileBody = child;
				break;
			}
		}
		
		return whileBody;
	}
	
	private BasicBlock getWhileFollowBlock(BasicBlock whileJoin) {
		BasicBlock whileFollow = null;
		for(BasicBlock child : whileJoin.controlFlow) {
			if (child.blockType.equals(WHILE_BODY)) {
				whileFollow = child;
				break;
			}
		}
		
		return whileFollow;
	}

	private void calculateLive3(BasicBlock currentBlock, boolean isLeft) {
		BasicBlock joinBlock = currentBlock.controlFlow.get(0);
		//add join's live set to current live set
		Set<Integer> live1 = new HashSet<Integer>(joinBlock.liveSet);
		live1.removeAll(joinBlock.getPhiInstructionNumbers());
		if(isLeft) {
			live1.addAll(joinBlock.getLeftPhis(programInstructions));
		}
		else {
			live1.addAll(joinBlock.getRightPhis(programInstructions));
		}
		currentBlock.liveSet = live1;
		IGraph.addEdges(currentBlock.liveSet);
	}
	
	public static void updateFrequency(int value) {
		
		if(!frequencies.containsKey(value)) {
			frequencies.put(value, 1);
			LOGGER.debug("Put new entry in frequencies table for: {} ", value);
		} 
		else {
			int temp = frequencies.get(value) + 1;
			frequencies.put(value, temp);
			LOGGER.debug("Updated frequency for key: {}  to: {}", value, temp);
		}
	}
}
