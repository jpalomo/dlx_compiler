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
import compiler.components.parser.Function;
import compiler.components.parser.Instruction;
import compiler.components.parser.Instruction.OP;
import compiler.components.parser.Parser;

//TODO make sure all fucntions have their variables initialized

public class CommonSubexpressionElimination {
	static Logger LOGGER = LoggerFactory
			.getLogger(CommonSubexpressionElimination.class);

	public Map<Integer, Instruction> originalInstructions;

	public CommonSubexpressionElimination(Parser p, Map<Integer, Instruction> orig) {
		originalInstructions = new HashMap<Integer, Instruction>(orig);
		HashMap<Integer, Instruction> deletedToPass;
		BasicBlock root = p.currentBlock;
		eliminateMoves(root);
		deletedToPass = subexpressionElimination(root, new HashMap<OP, Set<Instruction>>(), new HashMap<Integer, Instruction>());
		eliminateCPIns(root, deletedToPass);
		deletedToPass = subexpressionElimination(root, new HashMap<OP, Set<Instruction>>(), new HashMap<Integer, Instruction>());
		for (Function f : p.functionList) {
			if (f.hasBlocks) {
				eliminateMoves(f.beginBlockForFunction);
				deletedToPass = subexpressionElimination( f.beginBlockForFunction, new HashMap<Instruction.OP, Set<Instruction>>(), new HashMap<Integer, Instruction>()); eliminateCPIns(f.beginBlockForFunction, deletedToPass);
				eliminateCPIns(f.beginBlockForFunction, deletedToPass);
				deletedToPass = subexpressionElimination( f.beginBlockForFunction, new HashMap<Instruction.OP, Set<Instruction>>(), new HashMap<Integer, Instruction>()); eliminateCPIns(f.beginBlockForFunction, deletedToPass);
			}
		}
		Instruction.programInstructions = originalInstructions;
	}

	private HashMap<Integer, Instruction> subexpressionElimination(BasicBlock b, Map<OP, Set<Instruction>> uniqueInstrMap, HashMap<Integer, Instruction> deleted) {
		List<Integer> blockInstructions = b.getInstructions();

		for (int i = 1; i <= blockInstructions.size(); i++) {

			Instruction currentIns = originalInstructions.get(blockInstructions .get(i - 1));

			addToUniqueSet(b, uniqueInstrMap, deleted, currentIns);
		}
		for (BasicBlock d : b.dominatees) {
			subexpressionElimination(d, new HashMap<OP, Set<Instruction>>(uniqueInstrMap), deleted);
		}
		return deleted;
	}

	private void addToUniqueSet(BasicBlock b, Map<OP, Set<Instruction>> uniqueInstrMap, HashMap<Integer, Instruction> deleted, Instruction currentIns) {
		if (uniqueInstrMap.containsKey(currentIns.op)) {
			Set<Instruction> uniqueSet = uniqueInstrMap.get(currentIns.op);

			if (uniqueSet.isEmpty()) {
				uniqueSet.add(currentIns);
			} else {
				Iterator<Instruction> iter = uniqueSet.iterator();
				boolean add = true;
				while (iter.hasNext()) {

					Instruction temp = iter.next();
					if (currentIns.equals(temp)) {
						add = false;
						deleted.put(currentIns.instNum, temp);
						b.removeInstruction(currentIns.instNum);
						LOGGER.debug( "removed instruction {}, from block {}.", currentIns, b.blockNumber);
						break;
					}
				}

				if (add) {
					uniqueSet.add(currentIns);
					LOGGER.debug("added instruction {}, from block {}.", currentIns, b.blockNumber);
				}
			}
		} else {
			Set<Instruction> newSet = new HashSet<Instruction>();
			newSet.add(currentIns);
			uniqueInstrMap.put(currentIns.op, newSet);
			LOGGER.debug("added instruction {}, from block {}.", currentIns, b.blockNumber);
		}
	}

	private void eliminateCPIns(BasicBlock root, HashMap<Integer, Instruction> deletedIns) {
		List<Integer> blockInstructions = root.getInstructions();

		Iterator<Integer> iter = blockInstructions.iterator();
		while (iter.hasNext()) {

			Instruction currentIns = originalInstructions.get(iter.next());

			if (currentIns.op.equals(Instruction.OP.CP_INS)) {
				deletedIns.put(currentIns.instNum, originalInstructions.get(currentIns.leftOperand.instrNum));
				originalInstructions.remove(currentIns.instNum);
				root.removeInstruction(currentIns.instNum);
				LOGGER.debug("Removed instruction number " + currentIns.instNum);
				continue;
			}

			if (currentIns.rightOperand.type.equals(ResultEnum.INSTR)) {
				if (currentIns.op.equals(Instruction.OP.CP_INS)) {
					while (deletedIns .containsKey(currentIns.rightOperand.instrNum)) {
						int temp = currentIns.rightOperand.instrNum;
						currentIns.rightOperand.instrNum = deletedIns .get(currentIns.rightOperand.instrNum).leftOperand.instrNum; 
						LOGGER.debug( "Updated right operand {} with {} in instruction {}", temp, currentIns.rightOperand.instrNum, currentIns);
					}
				} else {
					while (deletedIns.containsKey(currentIns.rightOperand.instrNum)) {
						int temp = currentIns.rightOperand.instrNum;
						currentIns.rightOperand.instrNum = deletedIns .get(currentIns.rightOperand.instrNum).instNum;
						LOGGER.debug( "Updated right operand {} with {} in instruction {}", temp, currentIns.rightOperand.instrNum, currentIns);
					}

				}

			}

			if (currentIns.leftOperand.type.equals(ResultEnum.INSTR)) {
				if (currentIns.op.equals(Instruction.OP.CP_INS)) {
					while (deletedIns.containsKey(currentIns.leftOperand.instrNum)) {
						int temp = currentIns.leftOperand.instrNum;
						currentIns.leftOperand.instrNum = deletedIns.get(currentIns.leftOperand.instrNum).leftOperand.instrNum;
						LOGGER.debug( "Updated left operand {} with {} in instruction {}", temp, currentIns.leftOperand.instrNum, currentIns);
					}
				} else {
					while (deletedIns.containsKey(currentIns.leftOperand.instrNum)) {
						System.out.println("In here and updaing for block number: " + currentIns.instNum);
						int temp = currentIns.leftOperand.instrNum;
						currentIns.leftOperand.instrNum = deletedIns.get(currentIns.leftOperand.instrNum).instNum; 
						LOGGER.debug( "Updated left operand {} with {} in instruction {}", temp, currentIns.leftOperand.instrNum, currentIns);
					}
				}
			}
		}

		for (BasicBlock b : root.dominatees) {
			eliminateCPIns(b, deletedIns);
		}

	}

	private void eliminateMoves(BasicBlock b) {

		List<Integer> blockInstructions = b.getInstructions();
		for (int i = 1; i <= blockInstructions.size(); i++) {

			Instruction currentIns = originalInstructions.get(blockInstructions .get(i - 1));

			if (currentIns.op.equals(OP.MOVE)) {
				if (currentIns.leftOperand.type.equals((ResultEnum.INSTR))) {
					currentIns.op = OP.CP_INS;
					currentIns.rightOperand = Result.EMPTY_RESULT;
				} else if (currentIns.leftOperand.type
						.equals((ResultEnum.CONSTANT))) {
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