package compiler.components.optimization;

import static compiler.components.parser.Instruction.OP.MOVE;
import static compiler.components.intermediate_rep.Result.ResultEnum.CONSTANT;
import static compiler.components.intermediate_rep.Result.ResultEnum.INSTR;
import static compiler.components.intermediate_rep.Result.EMPTY_RESULT; 
import static compiler.components.parser.Instruction.OP.CP_CONST;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import compiler.components.intermediate_rep.BasicBlock;
import compiler.components.intermediate_rep.Result;
import compiler.components.parser.Function;
import compiler.components.parser.Instruction;
import compiler.components.parser.Parser;

public class CopyPropagation {

	static Logger LOGGER = LoggerFactory.getLogger(CopyPropagation.class);

	private Map<Integer, Instruction> copyPropInstructions;

	private List<Integer> phiInstructions;

	public CopyPropagation(Parser parser, List<Integer> phiInstructionNumbers, Map<Integer, Instruction> orig) throws OptimizationException {
		copyPropInstructions = new HashMap<Integer, Instruction>(orig);
		phiInstructions = new ArrayList<Integer>(phiInstructionNumbers);
		depthFirstPropagation(parser.root);

		//perform CP on all function blocks
		for(Function f : parser.functionList) {
			if(f.hasBlocks) { 
				depthFirstPropagation(f.beginBlockForFunction);
			}
		}

		//update the global instructions to reflect the copy propagation performed
		Instruction.programInstructions = copyPropInstructions;
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
	private void depthFirstPropagation(BasicBlock startBlock) throws OptimizationException {

		List<Integer> blockInstructions = startBlock.getInstructions();
		for(Integer instructionNo : blockInstructions) {
			//get the instruction corresponding to the current instruction we are processing
			Instruction currentInstruction = copyPropInstructions.get(instructionNo);

			if(currentInstruction.op.equals(MOVE)) {
				Result valueToPropogate = currentInstruction.leftOperand;
				Result valueToReplace = currentInstruction.rightOperand;

				//if were moving a constant into a variable, we will update the instruction
				if(currentInstruction.leftOperand.type.equals(CONSTANT)) {
					//we do not want to propagate the constant, but the instruction
					valueToPropogate = new Result(INSTR);
					valueToPropogate.instrNum = currentInstruction.instNum;
					currentInstruction.op = CP_CONST;
					currentInstruction.rightOperand = EMPTY_RESULT;
					LOGGER.debug("Found assignment of constant to variable, update instruction {} and propoate an instruction", currentInstruction.instNum, valueToPropogate);
				}
				else{
					//remove the instruction from the block and the global instructions
					LOGGER.debug("Removing instruction {} because it was propagated.", currentInstruction);
					startBlock.removeInstruction(instructionNo);
					copyPropInstructions.remove(instructionNo); 
				}

				//push the value down the current block
				propagateValueDownBlock(startBlock, currentInstruction.instNum, valueToReplace, valueToPropogate);

				//Go through blocks that this block dominates and update the values
				propagateValueDownDominatorTree(startBlock, valueToReplace, valueToPropogate);

				//update all the phis that may reference this variable
				propagateValueThroughPhis(valueToReplace, valueToPropogate);
			}
		}

		//now process the rest of the dominated blocks looking for moves
        for (BasicBlock dominatee : startBlock.dominatees) {
			depthFirstPropagation(dominatee);
        }
	}

	/**
	 * This is a simple utility method that helps update the current block where the MOVE instruction was found.
	 * It goes through all the current blocks instructions, first looking for the current MOVE instruction's 
	 * instruction number.  Once it has found that instruction, it propagates the value down.
	 */
	private void propagateValueDownBlock(BasicBlock currentBlock, int startInstructionNo, Result valueToReplace, Result valueToPropogate) {
		for(int i = 0; i < currentBlock.getInstructions().size(); i++) {
			int blockInstructionNo = currentBlock.getInstructions().get(i);

			//only want to update instructions that come after the startInstructionNo
			if(blockInstructionNo <= startInstructionNo) {
				continue;
			}

			Instruction currentInstruction = copyPropInstructions.get(blockInstructionNo);
			updateInstruction(currentInstruction, valueToReplace, valueToPropogate); 
		}
	}
	
	/**
	 * This method propagates the value down the dominator tree using DFS.
	 */
	private void propagateValueDownDominatorTree(BasicBlock root, Result valueToReplace, Result valueToPropogate) {
		for (BasicBlock dominatee : root.dominatees) {
			for (Integer instructionNo : dominatee.getInstructions()) {
				Instruction blockInstruction = copyPropInstructions.get(instructionNo);
				updateInstruction(blockInstruction, valueToReplace, valueToPropogate);
			}
			propagateValueDownDominatorTree(dominatee, valueToReplace, valueToPropogate);
		}
	}
	
	/**
	 * Since some nested blocks do not dominate the join blocks, we need to go through all the 
	 * phi's and see if we can propagate the value to the phi for further optimization.  We can guarantee 
	 * that the value being propagated will not appear after the phi.
	 */
	private void propagateValueThroughPhis(Result valueToReplace, Result valueToPropogate) {
		for(int i = 0; i < phiInstructions.size(); i++) {
			int phiInstructionNo = phiInstructions.get(i);
			Instruction phiInstruction = copyPropInstructions.get(phiInstructionNo);
			updateInstruction(phiInstruction, valueToReplace, valueToPropogate); 
		}
	} 

	/**
	 * Simple utility method to help update the left or the right operand of an instruction when propagating.
	 */
	private void updateInstruction(Instruction instruction,  Result valueToReplace, Result valueToPropogate) {
		Result previousOperand = null;
		if(instruction.leftOperand.equals(valueToReplace)) {
			previousOperand = Result.clone(instruction.leftOperand);
			instruction.leftOperand = Result.clone(valueToPropogate);
			LOGGER.debug("Updated left operand of instruction number {} from {} to {}", instruction.instNum, previousOperand, instruction.leftOperand);
		}

		if(instruction.rightOperand.equals(valueToReplace)) {
			previousOperand = Result.clone(instruction.rightOperand); 
			instruction.rightOperand = Result.clone(valueToPropogate);
			LOGGER.debug("Updated right operand of instruction number {} from {} to {}", instruction.instNum, previousOperand, instruction.rightOperand);
		}	
	}
} 