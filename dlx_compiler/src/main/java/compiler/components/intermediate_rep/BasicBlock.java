package compiler.components.intermediate_rep;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import compiler.components.parser.Instruction;

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
	public List<Integer> instructions;
	public List<Integer> phiInstructions;

	public List<BasicBlock> controlFlow;
	public List<BasicBlock> dominatees;
	public boolean isFunctionBlock = false;

	public Integer blockNumber;
	public static int BLOCK_NUM = 0;
	
	public BasicBlock() {
		instructions = new LinkedList<Integer>();
		phiInstructions = new LinkedList<Integer>();
		controlFlow = new LinkedList<BasicBlock>();
		dominatees = new LinkedList<BasicBlock>();

		this.blockNumber = BLOCK_NUM++;
	}

	public BasicBlock(boolean isFunctionBlock) {
		this();
		this.isFunctionBlock = isFunctionBlock;
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

	/**
	 * Prints the instructions  
	 * @param programInstructions
	 * @return
	 */
	public String toString(Map<Integer, Instruction> programInstructions) {
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
	}

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