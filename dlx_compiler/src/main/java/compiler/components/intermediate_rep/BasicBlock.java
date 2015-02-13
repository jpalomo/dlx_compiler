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
	public List<BasicBlock> controlFlow;
	public List<BasicBlock> dominatees;

	public Integer blockNumber;
	public static int BLOCK_NUM = 0;
	
	public BasicBlock() {
		instructions = new LinkedList<Integer>();
		controlFlow = new LinkedList<BasicBlock>();
		dominatees = new LinkedList<BasicBlock>();
		this.blockNumber = BLOCK_NUM++;
	}

	/**
	 * Adds an instruction to the list in sequential order
	 * 
	 * @param instruction 
	 */
	public void addInstruction(Integer instructionNumber) {
		instructions.add(instructionNumber);
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