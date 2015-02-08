package compiler.components.intermeditate_rep;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class BasicBlock {
	private List<Integer> instructions;
	private List<BasicBlock> dominatees;
	private List<BasicBlock> dominators;
	private List<BasicBlock> controlFlow;
	public Integer blockNumber;
	public static int BLOCK_NUM = 0;
	
	public BasicBlock() {
		instructions = new LinkedList<Integer>();
		dominatees = new LinkedList<BasicBlock>();
		dominators = new LinkedList<BasicBlock>();
		controlFlow = new LinkedList<BasicBlock>();
	}

	/**
	 * Adds an instruction to the list in sequential order
	 * @param instruction
	 */
	public void addInstruction(Integer instructionNumber) {
		instructions.add(instructionNumber);
	}

	/**
	 * Add a basic block that dominates this block
	 * @param dominator
	 */
	public void addDominator(BasicBlock dominator) {
		dominators.add(dominator);
	}

	/**
	 * Add a basic block that this block dominates
	 * @param dominatee
	 */
	public void addDominatee(BasicBlock dominatee) {
		dominatees.add(dominatee);
	}

	public void addControlFlow(BasicBlock to){
		controlFlow.add(to);
	}

	public List<Integer> getInstructions() {
		return instructions;
	}

	public List<BasicBlock> getDominatees() {
		return dominatees;
	}

	public List<BasicBlock> getDominators() {
		return dominators;
	}

	public List<BasicBlock> getControlFlow() {
		return controlFlow;
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
			s = String.format("\n\t%d\t%s", instNum, programInstructions.get(instNum).toString());
			sb.append(s);
		}

		sb.append("Controls:");
		for(BasicBlock bb: controlFlow) {
			s = String.format("\n\t%d", bb.blockNumber);
			sb.append(s);
		}

		return sb.toString();
	}

	public String toString(){
		return blockNumber.toString();
	}

	public boolean isEmpty() {
		if(instructions.size() == 0 && dominators.size() == 0) {
			return true;
		}
		return false;
	}
}