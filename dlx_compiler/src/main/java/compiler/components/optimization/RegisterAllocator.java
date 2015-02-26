package compiler.components.optimization;

import static compiler.components.intermediate_rep.BasicBlock.BlockType.*;
import static compiler.components.intermediate_rep.BasicBlock.BlockType.IF_JOIN;
import static compiler.components.intermediate_rep.BasicBlock.BlockType.WHILE_FOLLOW;

import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Map;
import java.util.Queue;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import compiler.components.intermediate_rep.BasicBlock;
import compiler.components.parser.Instruction;

public class RegisterAllocator {
	static Logger LOGGER = LoggerFactory.getLogger(RegisterAllocator.class);

	public static Map<Integer, Integer> frequencies = new HashMap<Integer, Integer>();
	private Queue<BasicBlock> blocksToProcess = new LinkedList<BasicBlock>();

	Map<Integer, Instruction> programInstructions;
	BasicBlock leafBlock;
	Set<Integer> processedBlocks;

	public RegisterAllocator(Map<Integer, Instruction> programInstructions) {
		processedBlocks = new HashSet<Integer>();
		this.programInstructions = programInstructions;
	}

	public void buildGraphAndAllocate(BasicBlock rootBlock) {
		depthFirstToLeaf(rootBlock);
		blocksToProcess.add(leafBlock);
		while(!blocksToProcess.isEmpty()) {
			calculateLivenessBottomUp(blocksToProcess.remove());
		}
	}
	
	public void depthFirstToLeaf(BasicBlock root) {
		for(BasicBlock child : root.controlFlow) {
			if(child.blockType.equals(FUNCTION)) {
				continue;
			}

			if(child.controlFlow.isEmpty()) {
				LOGGER.debug("Found leaf of control flow graph: {}", child.blockNumber);
				leafBlock = child;
				break;
			}

			depthFirstToLeaf(child);
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
			if(child.liveSet == null) {
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
				currentBlock.calculateLiveSet(programInstructions);
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
				currentBlock.calculateLiveSet(programInstructions);
			}
			else {
				//previously computed the block as an if body, but it was actually nested and should be a
				//program block (e.g. if header)
				currentBlock.blockType = PROGRAM;
			}
		}
		else if(currentBlock.blockType.equals(IF_JOIN)) {
			initializeLiveSet(currentBlock);
			currentBlock.calculateLiveSet(programInstructions);
		}
		else if(currentBlock.blockType.equals(WHILE_FOLLOW)) {

		}
		else if(currentBlock.blockType.equals(WHILE_JOIN)) {
			
		}

		if(currentBlock.blockType.equals(PROGRAM)) {
			initializeLiveSet(currentBlock);
			currentBlock.calculateLiveSet(programInstructions);
		}

			
	}

	private void initializeLiveSet(BasicBlock currentBlock) {
		currentBlock.liveSet = new HashSet<Integer>();
		for(BasicBlock child : currentBlock.controlFlow) {
			currentBlock.liveSet.addAll(child.liveSet);
		}
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
