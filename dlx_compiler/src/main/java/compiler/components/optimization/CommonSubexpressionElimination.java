package compiler.components.optimization;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import compiler.components.intermediate_rep.BasicBlock;
import compiler.components.intermediate_rep.Result;
import compiler.components.intermediate_rep.Result.ResultEnum;
import compiler.components.parser.Instruction;
import compiler.components.parser.Instruction.OP;

//TODO REMOVE ALL CP INSTRUCTIONS
//TODO change the test cases to ensure vairables are being initialized

public class CommonSubexpressionElimination {
	static Logger LOGGER = LoggerFactory.getLogger(CommonSubexpressionElimination.class);

	public Map<Integer, Instruction> originalInstructions;
	//public Map<OP, Set<Instruction>> uniqueInstrMap = new HashMap<OP, Set<Instruction>>();



	public CommonSubexpressionElimination(BasicBlock root,  Map<Integer, Instruction> orig) {
		originalInstructions = new HashMap<Integer, Instruction>(orig);
		eliminateMoves(root);
		subexpressionElimination(root, new HashMap<OP, Set<Instruction>>());
		Instruction.programInstructions = originalInstructions; 
	} 

	private void subexpressionElimination(BasicBlock b, Map<OP, Set<Instruction>> uniqueInstrMap) {
		List<Integer> blockInstructions = b.getInstructions();
		for (int i = 1; i <= blockInstructions.size(); i++) {

			Instruction currentIns = originalInstructions.get(blockInstructions.get(i-1));
			
			if(uniqueInstrMap.containsKey(currentIns.op)) {
				Set<Instruction> uniqueSet = uniqueInstrMap.get(currentIns.op);

				if(uniqueSet.isEmpty()) {
					uniqueSet.add(currentIns);
				}
				else {
					Iterator<Instruction> iter = uniqueSet.iterator();
					boolean add = true;
					while(iter.hasNext()) {

						if(currentIns.equals(iter.next())) {
							add = false;
							b.removeInstruction(currentIns.instNum);
							LOGGER.debug("removed instruction {}, from block {}.", currentIns, b.blockNumber);
							break;
						}
					}

					if(add) {
						uniqueSet.add(currentIns);
					}
				}
			}
			else {
				Set<Instruction> newSet = new HashSet<Instruction>();
				newSet.add(currentIns);
				uniqueInstrMap.put(currentIns.op, newSet);
			} 
		}
		for (BasicBlock d : b.dominatees) {
			subexpressionElimination(d, uniqueInstrMap); 
		}		
	}

	private void eliminateMoves(BasicBlock b) {

		List<Integer> blockInstructions = b.getInstructions();
		for (int i = 1; i <= blockInstructions.size(); i++) {

			Instruction currentIns = originalInstructions.get(blockInstructions.get(i-1));
			
			if(currentIns.op.equals(OP.MOVE)) {
				if(currentIns.leftOperand.type.equals((ResultEnum.INSTR))){
					currentIns.op = OP.CP_INS;
					currentIns.rightOperand = Result.EMPTY_RESULT;
				}
				else if(currentIns.leftOperand.type.equals((ResultEnum.CONSTANT))){
					currentIns.op = OP.CP_CONST;
					currentIns.rightOperand = Result.EMPTY_RESULT; 
				}
			}

		}
		for (BasicBlock d : b.dominatees) {
			eliminateMoves(d);
			
		}
	}
}