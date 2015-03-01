package compiler.components.intermediate_rep;

import static compiler.components.intermediate_rep.Result.ResultEnum.CONSTANT;
import static compiler.components.intermediate_rep.Result.ResultEnum.INSTR;

import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Joiner;

import compiler.components.intermediate_rep.Result.ResultEnum;
import compiler.components.parser.Instruction;
import compiler.components.parser.Instruction.OP;
import compiler.components.register.InterferenceGraph;
import compiler.components.register.INode;
import compiler.components.register.RegisterAllocator;

/**
 * This class represents a basic block in our parser.
 * Basic blocks are used to model the control flow of the
 * program.  Edges between basic blocks are represented by 
 * the list of basic blocks that the current block contains.
 * <p> 
 * For example, in the {@link BasicBlock#controlFlow} list,
 * each basic block in this is a destination.  So, from this 
 * we can derive the fact that there exists an edge from the current
 * block to the block(s) in the list. 
 * </p> * 
 * 
 * @author John Palomo and Cameron Chitsaz
 *
 */
public class BasicBlock {
	static Logger LOGGER = LoggerFactory.getLogger(BasicBlock.class);

	public BlockType blockType;

	public List<Integer> instructions;
	public List<Integer> phiInstructions;

	public List<BasicBlock> controlFlow;
	public List<BasicBlock> dominatees;
	public List<BasicBlock> parents;

	public enum BlockType {
		FUNCTION() , WHILE_JOIN(), IF_JOIN(), PROGRAM(), WHILE_FOLLOW(), IF_BODY(), ELSE_BODY()
	}

	/**
	 *	Contains all the variables that are live for this block 
	 */
	public Set<Integer> liveSet;

	public Integer blockNumber;
	public static int BLOCK_NUM = 0;
	
	public BasicBlock() {
		instructions = new LinkedList<Integer>();
		phiInstructions = new LinkedList<Integer>();

		controlFlow = new LinkedList<BasicBlock>();
		dominatees = new LinkedList<BasicBlock>();

		parents = new LinkedList<BasicBlock>();

		this.blockNumber = BLOCK_NUM++;
	}

	public BasicBlock(BlockType blockType) {
		this();
		this.blockType = blockType;
	}

	/**
	 * Adds an instruction to the list in sequential order
	 * 
	 * @param instruction 
	 */
	public void addInstruction(Integer instructionNumber) {
		instructions.add(instructionNumber);
	}

	public void addPhiInstruction(Integer phiInstructionNum){
		phiInstructions.add(phiInstructionNum);
	}

	public List<Integer> getInstructions() {
		List<Integer> allInstructions = new LinkedList<Integer>(phiInstructions);
		allInstructions.addAll(instructions);
		return allInstructions; 
	}

	public void removeInstruction(int instrToRemove) {
		if(phiInstructions.size() > 0) {
			if(phiInstructions.contains(instrToRemove)) {
				phiInstructions.remove(new Integer(instrToRemove));
				return;
			}
		}
		if(instructions.size() > 0) {
			if(instructions.contains(instrToRemove)) {
				instructions.remove(new Integer(instrToRemove));
				return;
			}
		}
	}

	public void calculateLiveSet(Map<Integer, Instruction> programInstructions, InterferenceGraph IGraph) {
		//start from the bottom of the instruction list
		Joiner joiner = Joiner.on(",").skipNulls();
		joiner.join(liveSet);
		LOGGER.debug("Liveset for block number: {} before processing: {}", blockNumber, joiner.join(liveSet));
		
		for(int i = instructions.size()-1; i >=0 ; i--) {
			int instructionNo = instructions.get(i);
			Instruction instruction = programInstructions.get(instructionNo);

			if(instruction.op.equals(OP.END) || instruction.op.equals(OP.BRA) ||instruction.op.equals(OP.CP_CONST) || instruction.op.equals(OP.PUSH) || instruction.op.equals(OP.CALL)) {
				updateLiveSet(instruction.instNum);
				continue;
			}
			if(instruction.leftOperand.type != CONSTANT ) {
				//add it to the live set
				if(instruction.leftOperand.type.equals(INSTR)) {
					liveSet.add(instruction.leftOperand.instrNum);
					RegisterAllocator.updateFrequency(instruction.leftOperand.instrNum);
				}
				else {
					liveSet.add(instruction.leftOperand.getVariableIndex());
					RegisterAllocator.updateFrequency(instruction.leftOperand.getVariableIndex());
				}
			}

			if(instruction.rightOperand.type != CONSTANT) {
				//add it to the live set
				if(instruction.rightOperand.type.equals(INSTR)) {
					liveSet.add(instruction.rightOperand.instrNum);
					RegisterAllocator.updateFrequency(instruction.rightOperand.instrNum);
				}
				else {
					liveSet.add(instruction.rightOperand.getVariableIndex());
					RegisterAllocator.updateFrequency(instruction.rightOperand.getVariableIndex());
				}
			}
			
			updateLiveSet(instruction.instNum);
			
			IGraph.addEdges(liveSet);
		} 
		
		joiner.join(liveSet);
		LOGGER.debug("Liveset for block number: {} after processing: {}", blockNumber, joiner.join(liveSet));
	}

	/**
	 * Removes all values from the live set that are greater than the current instruction number.
	 * Since those values are 
	 * 
	 * @param instNum
	 */
	private void updateLiveSet(int instNum) {
		Set<Integer> copySet = new HashSet<Integer>(liveSet);
		for(int instruction : copySet) {
			if(instruction >= instNum) {
				liveSet.remove(instruction);
			}
		} 
	}

	public Set<Integer> getLeftPhis(Map<Integer, Instruction> programInstructions) {
		Set<Integer> leftPhis = new HashSet<Integer>();
		for(int i : phiInstructions) {
			Instruction phi = programInstructions.get(i);
			if(phi.leftOperand.type.equals(ResultEnum.VARIABLE)) {
				leftPhis.add(phi.leftOperand.getVariableIndex());
			}
			else {
				leftPhis.add(phi.leftOperand.instrNum);
			}
		}
		return leftPhis;
	}

	public Set<Integer> getRightPhis(Map<Integer, Instruction> programInstructions) {
		Set<Integer> rightPhis = new HashSet<Integer>();
		for(int i : phiInstructions) {
			Instruction phi = programInstructions.get(i);
			if(phi.rightOperand.type.equals(ResultEnum.VARIABLE)) {
				rightPhis.add(phi.rightOperand.getVariableIndex());
			}
			else {
				rightPhis.add(phi.rightOperand.instrNum);
			}
		}
		return rightPhis;
	}

	public Set<Integer> getPhiInstructionNumbers() {
		Set<Integer> phiInstructionNums = new HashSet<Integer>();
		for(int i : phiInstructions) {
			phiInstructionNums.add(i);
		}
		return phiInstructionNums;
	}

	/**
	 * Adds an 'edge' from this block to the block
	 * parameter.
	 * 
	 * @param to the destination block 
	 */
	public void addControlFlow(BasicBlock to){
		controlFlow.add(to);
	}

	public void addDominatee(BasicBlock dom){
		dominatees.add(dom);
	}

	public void addParent(BasicBlock parent) {
		parents.add(parent);
	}

	/**
	 * Prints the instructions  
	 * @param programInstructions
	 * @return
	 */
	/*public String toString(Map<Integer, Instruction> programInstructions) {
		StringBuilder sb = new StringBuilder();
		sb.append("BlockNo: " + blockNumber);
		String s; 

		sb.append("Instructions:");
		for(Integer instNum: instructions) {
			s = String.format("\n %d=%s", instNum, programInstructions.get(instNum).toString());
			sb.append(s);
		}

		sb.append("\nControls:");
		for(BasicBlock bb: controlFlow) {
			s = String.format(" %d", bb.blockNumber);
			sb.append(s);
		}

		sb.append("\nDominates:");
		for(BasicBlock bb: dominatees) {
			s = String.format("%d", bb.blockNumber);
			sb.append(s);
		} 
		return sb.toString();
	}*/

	public String toString(){
		StringBuilder sb = new StringBuilder();
		for(BasicBlock bb:  controlFlow) {
			sb.append(bb.blockNumber);
			sb.append(",");
		}

		return blockNumber.toString() + " has control to: " + sb.toString();
	}

	public boolean isEmpty() {
		if(instructions.size() == 0 ) {
			return true;
		}
		return false;
	}
}