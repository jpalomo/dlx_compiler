package compiler.components.intermediate_rep;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import compiler.components.parser.Instruction;

public class BasicBlock {
	private List<Integer> instructions;
	private List<BasicBlock> controlFlow;
	public Integer blockNumber;
	public static int BLOCK_NUM = 0;
	
	public BasicBlock() {
		instructions = new LinkedList<Integer>();
		controlFlow = new LinkedList<BasicBlock>();
		this.blockNumber = BLOCK_NUM++;
	}

	/**
	 * Adds an instruction to the list in sequential order
	 * @param instruction
	 */
	public void addInstruction(Integer instructionNumber) {
		instructions.add(instructionNumber);
	}

	public void addControlFlow(BasicBlock to){
		controlFlow.add(to);
	}

	public List<Integer> getInstructions() {
		return instructions;
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