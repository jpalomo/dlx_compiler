package compiler.components.optimization;

import static compiler.components.parser.Instruction.BRANCH_INST;
import static compiler.components.parser.Instruction.OP.*;
import static compiler.components.intermediate_rep.Result.ResultEnum.*;
import static compiler.components.intermediate_rep.Result.ResultEnum.INSTR;
import static compiler.components.intermediate_rep.Result.EMPTY_RESULT; 
import static compiler.components.parser.Instruction.OP.CP_CONST;
import static compiler.components.parser.Instruction.OP.CMP;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.ImmutableSet;

import compiler.components.intermediate_rep.BasicBlock;
import compiler.components.intermediate_rep.Result;
import compiler.components.parser.Function;
import compiler.components.parser.Instruction;
import compiler.components.parser.Parser;
import compiler.components.parser.Instruction.OP;

public class Optimizer {

	static Logger LOGGER = LoggerFactory.getLogger(Optimizer.class);

	private Map<Integer, Instruction> optimizedInstructions;
	private Parser parser;
	private List<Integer> phiInstructions;

	public Optimizer(Parser parser, List<Integer> phiInstructionNumbers, Map<Integer, Instruction> orig) throws OptimizationException {
		this.optimizedInstructions = new HashMap<Integer, Instruction>(orig);
		this.phiInstructions = new ArrayList<Integer>(phiInstructionNumbers);
		this.parser = parser;
	}

	public void optimize(boolean performCopyProp, boolean performCommonSubExprElim) throws OptimizationException {

		if(performCopyProp) {
			copyPropagation(parser.root);
		}

		if(performCommonSubExprElim) {
			commonSubexpressionElimination(parser.root);
		}

		
		//perform CP on all function blocks
		for(Function f : parser.functionList) {
			if(f.hasBlocks) { 
				if(performCopyProp) {
					copyPropagation(f.beginBlockForFunction);
				}

				if(performCommonSubExprElim) {
					commonSubexpressionElimination(f.beginBlockForFunction);
				}
			}
		}
		
		//instructions may reference PHIS as variables (e.g. c_10), we will update all instructions to 
		//reference the instruction number (10)
		updateAllInstructionsThatReferencePhis();
		Instruction.programInstructions = this.optimizedInstructions;
	}

	private void updateAllInstructionsThatReferencePhis() {
		for(Instruction instruction : optimizedInstructions.values()) {
			if(instruction.leftOperand.type.equals(VARIABLE)) {
				Result instrResult = new Result(INSTR);
				instrResult.instrNum = instruction.leftOperand.getVariableIndex();
				instruction.leftOperand = instrResult;
			}

			if(instruction.rightOperand.type.equals(VARIABLE)) {
				Result instrResult = new Result(INSTR);
				instrResult.instrNum = instruction.rightOperand.getVariableIndex();
				instruction.rightOperand = instrResult;
			}
		}
	}


	/**
	 * This method performs a DFS on the dominator tree looking for values to propagate down the tree.
	 * Specifically, it looks for move instructions.  Since all variables need to be initialized, all 
	 * variables will have values associated with them.  The algorithm is not optimal but is straightforward:
	 * 
	 * 1.  Look for a MOVE instruction
	 * 2.  Propagate the left hand operand of the move down the dominator through the block (e.g. MOVE #1 a_2)
	 * 		-This will move the constant into all a_2 variables in the block.
	 * 3.  Propagate the value (e.g. #1) through all the dominated blocks in a DFS manner.
	 * 4.  Propagate the value through the PHI instructions.  
	 * 		-Since this block may be connected to the block containing the PHI (join block), the value needs to be
	 * 		pushed to the PHI.  We can be certain that this value does not cross the PHI instruction, so if
	 * 		we process all the PHIs and don't find a replacement, this block was not part connected to a join block.
	 * 
	 * @param startBlock
	 * @throws OptimizationException
	 */
	private void copyPropagation(BasicBlock startBlock) throws OptimizationException {
		List<Integer> blockInstructions = startBlock.getInstructions();
		
		for(Integer instructionNo : blockInstructions) {
			//get the instruction corresponding to the current instruction we are processing
			Instruction currentInstruction = optimizedInstructions.get(instructionNo);

			if(currentInstruction.op.equals(MOVE)) {
				Result valueToPropagate = currentInstruction.leftOperand;
				Result valueToReplace = currentInstruction.rightOperand;

				//if were moving a constant into a variable, we will update the instruction
				if(currentInstruction.leftOperand.type.equals(CONSTANT)) {
					//we do not want to propagate the constant, but the instruction
					valueToPropagate = new Result(INSTR);
					valueToPropagate.instrNum = currentInstruction.instNum;
					currentInstruction.op = CP_CONST;
					currentInstruction.rightOperand = EMPTY_RESULT;
					LOGGER.debug("CP - Found assignment of constant to variable, update instruction {} and propoate an instruction", currentInstruction.instNum, valueToPropagate);
				}
				else{
					//remove the instruction from the block and the global instructions
					LOGGER.debug("CP - Removing instruction {} because it was propagated.", currentInstruction);
					removeInstruction(startBlock, instructionNo); 
				}

				//push the value down the current block
				propagateValueDownBlock(startBlock, currentInstruction.instNum, valueToReplace, valueToPropagate);

				//Go through blocks that this block dominates and update the values
				propagateValueDownDominatorTree(startBlock, valueToReplace, valueToPropagate);

				//update all the phis that may reference this variable
				propagateValueThroughPhis(valueToReplace, valueToPropagate);
			}
		}

		//now process the rest of the dominated blocks looking for moves
        for (BasicBlock dominatee : startBlock.dominatees) {
        	copyPropagation(dominatee);
        }
	}

	private void removeInstruction(BasicBlock startBlock, Integer instructionNo) {
		LOGGER.debug("Removing instruction number: {} from block number: {}",instructionNo, startBlock.blockNumber);
		startBlock.removeInstruction(instructionNo);
		optimizedInstructions.remove(instructionNo);
	}

	/**
	 * This is a simple utility method that helps update the current block where the MOVE instruction was found.
	 * It goes through all the current blocks instructions, first looking for the current MOVE instruction's 
	 * instruction number.  Once it has found that instruction, it propagates the value down.
	 */
	private void propagateValueDownBlock(BasicBlock currentBlock, int startInstructionNo, Result valueToReplace, Result valueToPropagate) {
		for(int i = 0; i < currentBlock.getInstructions().size(); i++) {
			int blockInstructionNo = currentBlock.getInstructions().get(i);

			//only want to update instructions that come after the startInstructionNo
			if(blockInstructionNo <= startInstructionNo) {
				continue;
			}

			Instruction currentInstruction = optimizedInstructions.get(blockInstructionNo);
			updateInstructionValues(currentInstruction, valueToReplace, valueToPropagate); 
		}
	}
	
	/**
	 * This method propagates the value down the dominator tree using DFS.
	 */
	private void propagateValueDownDominatorTree(BasicBlock root, Result valueToReplace, Result valueToPropagate) {
		for (BasicBlock dominatee : root.dominatees) {
			for (Integer instructionNo : dominatee.getInstructions()) {
				Instruction blockInstruction = optimizedInstructions.get(instructionNo);
				updateInstructionValues(blockInstruction, valueToReplace, valueToPropagate);
			}
			propagateValueDownDominatorTree(dominatee, valueToReplace, valueToPropagate);
		}
	}
	
	/**
	 * Since some nested blocks do not dominate the join blocks, we need to go through all the 
	 * phi's and see if we can propagate the value to the phi for further optimization.  We can guarantee 
	 * that the value being propagated will not appear after the phi.
	 */
	private void propagateValueThroughPhis(Result valueToReplace, Result valueToPropagate) {
		for(int i = 0; i < phiInstructions.size(); i++) {
			int phiInstructionNo = phiInstructions.get(i);
			Instruction phiInstruction = optimizedInstructions.get(phiInstructionNo);
			if(phiInstruction == null) { //removed this instruction
				continue;
			}
			updateInstructionValues(phiInstruction, valueToReplace, valueToPropagate); 
		}
	} 

	/**
	 * Simple utility method to help update the left or the right operand of an instruction when propagating.
	 */
	private void updateInstructionValues(Instruction instruction,  Result valueToReplace, Result valueToPropagate) {
		Result previousOperand = null;

		Instruction checkInstruction = optimizedInstructions.get(valueToPropagate.instrNum);
/*		if(checkInstruction.op.equals(CP_CONST) && instruction.op.equals(PHI)){

			if(instruction.leftOperand.equals(valueToReplace)) {
				previousOperand = Result.clone(instruction.leftOperand);
				instruction.leftOperand = Result.clone(checkInstruction.leftOperand);
				LOGGER.debug("Updated left operand of phi instruction number {} from {} to {}", instruction.instNum, previousOperand, instruction.leftOperand);
			}

			if(instruction.rightOperand.equals(valueToReplace)) {
				previousOperand = Result.clone(instruction.rightOperand); 
				instruction.rightOperand = Result.clone(checkInstruction.leftOperand);
				LOGGER.debug("Updated right operand of phi instruction number {} from {} to {}", instruction.instNum, previousOperand, instruction.rightOperand);
			}	
			return;
		}*/
		if(instruction.leftOperand.equals(valueToReplace)) {
			previousOperand = Result.clone(instruction.leftOperand);
			instruction.leftOperand = Result.clone(valueToPropagate);
			LOGGER.debug("Updated left operand of instruction number {} from {} to {}", instruction.instNum, previousOperand, instruction.leftOperand);
		}

		if(instruction.rightOperand.equals(valueToReplace)) {
			previousOperand = Result.clone(instruction.rightOperand); 
			instruction.rightOperand = Result.clone(valueToPropagate);
			LOGGER.debug("Updated right operand of instruction number {} from {} to {}", instruction.instNum, previousOperand, instruction.rightOperand);
		}	
	}


	/**************************************************************/
	/**
	 * Set of all instruction operations to skip when performing CSE
	 */
	private static final Set<OP> SKIP_SET = ImmutableSet.<OP>builder()
			.addAll(BRANCH_INST)
			.add(CMP)
			.add(SAVE_STATUS)
			.add(PHI)
			.build();

	private void commonSubexpressionElimination(BasicBlock startBlock) {
		List<Integer> blockInstructions = startBlock.getInstructions();

		for(int i : blockInstructions) {
			//get the current instruction
			Instruction currentInstruction = optimizedInstructions.get(i);
	
			if(currentInstruction == null || SKIP_SET.contains(currentInstruction.op) ) {
				continue;
			}
	
			propagateExpressionDownBlock(startBlock, currentInstruction);
			propagateExpressionDownDominatorTree(startBlock, currentInstruction);
		}

		for (BasicBlock child : startBlock.dominatees) {
			commonSubexpressionElimination(child);
		}
	}

	private void propagateExpressionDownBlock(BasicBlock currentBlock, Instruction currentInstruction) {
		LOGGER.debug("CSE - Propagating instruction {} expression down block: {}.", currentInstruction, currentBlock.blockNumber);
		for(int i = 0; i < currentBlock.getInstructions().size(); i++) {
			int blockInstructionNo = currentBlock.getInstructions().get(i);
	
			//only want to update instructions that come after the startInstructionNo
			if(blockInstructionNo <= currentInstruction.instNum) {
				continue;
			}
	
			Instruction instructionToUpdate = optimizedInstructions.get(blockInstructionNo);
			updateInstructionExpressions(currentBlock, currentInstruction, instructionToUpdate); 
		}
	}

	private void propagateExpressionDownDominatorTree(BasicBlock root, Instruction instructionToPropagate) {
		for (BasicBlock dominatee : root.dominatees) {
			for (Integer instructionNo : dominatee.getInstructions()) {
				Instruction blockInstruction = optimizedInstructions.get(instructionNo);
				updateInstructionExpressions(dominatee, instructionToPropagate, blockInstruction);
			}
			propagateExpressionDownDominatorTree(dominatee, instructionToPropagate);
		}
	}

	private void updateInstructionExpressions(BasicBlock startBlock, Instruction propagatedInstruction, Instruction instructionToUpdate) {
		if(propagatedInstruction.equals(instructionToUpdate)) {
			LOGGER.debug("CSE - Instruction {} is a common subexression with instruction number: {}", instructionToUpdate, propagatedInstruction.instNum);
			
			//remove the instruction and update all references to this instruction to point to the current instruction
			Result valueToReplace = new Result(INSTR);
			valueToReplace.instrNum = instructionToUpdate.instNum;

			Result valueToPropagate = new Result(INSTR);
			valueToPropagate.instrNum = propagatedInstruction.instNum;

			removeInstruction(startBlock, instructionToUpdate.instNum);

			//push the value down the current block
			propagateValueDownBlock(startBlock, instructionToUpdate.instNum, valueToReplace, valueToPropagate);

			//Go through blocks that this block dominates and update the values
			propagateValueDownDominatorTree(startBlock, valueToReplace, valueToPropagate);

			//update all the phis that may reference this variable
			propagateValueThroughPhis(valueToReplace, valueToPropagate);
		}
	} 
} 