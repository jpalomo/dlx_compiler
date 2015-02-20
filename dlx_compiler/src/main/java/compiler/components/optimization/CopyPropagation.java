package compiler.components.optimization;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import compiler.components.intermediate_rep.BasicBlock;
import compiler.components.intermediate_rep.Result;
import compiler.components.intermediate_rep.Result.ResultEnum;
import compiler.components.parser.Function;
import compiler.components.parser.Instruction;
import compiler.components.parser.Parser;

public class CopyPropagation {

	static Logger LOGGER = LoggerFactory.getLogger(CopyPropagation.class);

	Map<String, Integer> variableTable = new HashMap<String, Integer>();
	Map<Integer, Instruction> originalInstructions;

	public CopyPropagation(Parser p, Map<Integer, Instruction> orig) throws OptimizationException {
		originalInstructions = new HashMap<Integer, Instruction>(orig);
		BasicBlock root = p.currentBlock;
		depthFirstPropagation(root);
		for(Function f : p.functionList) {
			if(f.hasBlocks) { 
				depthFirstPropagation(f.beginBlockForFunction);
			}
		}
		Instruction.programInstructions = originalInstructions;

	}

	public void depthFirstPropagation(BasicBlock b) throws OptimizationException {

		List<Integer> blockInstructions = b.getInstructions();
		for (int i = 1; i <= blockInstructions.size(); i++) {

			Instruction currentIns = originalInstructions.get(blockInstructions.get(i-1));

			if (currentIns.op.equals(Instruction.OP.MOVE)) {
				movEncountered(b, i);
			} 
			else if (currentIns.op.equals(Instruction.OP.PHI)) {
				phiEncountered(b, i);
			}
			else {
				otherEncountered(b, i);
			}
		}
		for (BasicBlock d : b.dominatees) {
			variableTable = new HashMap<String, Integer>();
			depthFirstPropagation(d);
		}
	}
	
	public void movEncountered(BasicBlock b, int insIndex) throws OptimizationException {
		LOGGER.debug("###Enter moveEncountered###");
		
		// if a move instruction is encountered then we need to add any variables that aren't already in the variableTable in there 
		// and then check all instructions below this one and replace any instances

		List<Integer> blockInstructions = b.getInstructions();
		Instruction currentIns = originalInstructions.get(blockInstructions.get(insIndex-1));		
		
		if ((currentIns.leftOperand.type.equals(Result.ResultEnum.VARIABLE) && !(variableTable.containsKey(currentIns.leftOperand.varValue)) && !b.isFunctionBlock)) {
			throw new OptimizationException("assigning unitilized vairable to another vairable." + currentIns.leftOperand.getVarNameWithoutIndex() + "->" + currentIns.rightOperand.varValue); 
		}
		
		if (currentIns.rightOperand.type.equals(ResultEnum.VARIABLE)) {
			LOGGER.debug("varValue: " + currentIns.rightOperand.varValue.trim());

			if (variableTable.containsKey(currentIns.leftOperand.varValue)) {
				variableTable.put(currentIns.rightOperand.varValue, variableTable.get(currentIns.leftOperand.varValue));
				LOGGER.debug("adding left operand varvalue: " + currentIns.rightOperand.varValue + " with value: " + variableTable.get(currentIns.leftOperand.varValue));
			} else {
				variableTable.put(currentIns.rightOperand.varValue, insIndex);
				LOGGER.debug("adding right operand varvalue: " + currentIns.rightOperand.varValue + " with value: " + (insIndex));
			}
			String varToReplace = currentIns.rightOperand.varValue;
			replaceVars(b, insIndex, varToReplace);
		}

		LOGGER.debug("###Exit moveEncountered###");
	}
	
	public void phiEncountered(BasicBlock b, int insIndex) {
		LOGGER.debug("###Enter phiEncountered###");
		List<Integer> blockInstructions = b.getInstructions();
		Instruction currentIns = originalInstructions.get(blockInstructions.get(insIndex-1));
		
		String varToReplace = null;
		if (!variableTable.containsKey(currentIns.rightOperand.varValue) && currentIns.rightOperand.type.equals(ResultEnum.VARIABLE)) {
			variableTable.put(currentIns.rightOperand.varValue, (currentIns.instNum));
			varToReplace = currentIns.rightOperand.varValue;
			LOGGER.debug("adding right operand varvalue: " + currentIns.rightOperand.varValue + " with value: " + currentIns.instNum );
		} else if (!variableTable.containsKey(currentIns.leftOperand.varValue)  && currentIns.leftOperand.type.equals(ResultEnum.VARIABLE)) {
			variableTable.put(currentIns.leftOperand.varValue, (currentIns.instNum));
			varToReplace = currentIns.leftOperand.varValue;
			LOGGER.debug("adding left operand varvalue: " + currentIns.leftOperand.varValue + " with value: " + (currentIns.instNum));
		}
		
		if (varToReplace != null) {
			replaceVars(b, insIndex, varToReplace);
		}
		
		LOGGER.debug("###Exit phiEncountered###");
	}
	
	public void otherEncountered(BasicBlock b, int insIndex) throws OptimizationException {
		// means an instruction other than a move was encountered. in this case just look to see if any operands within this operations
		// should be replaced with instructions in the variableTable
		LOGGER.debug("###Enter otherEncountered###");

		List<Integer> blockInstructions = b.getInstructions();
		Instruction currentIns = originalInstructions.get(blockInstructions.get(insIndex - 1));

		if ((currentIns.leftOperand.type.equals(Result.ResultEnum.VARIABLE) && !(variableTable.containsKey(currentIns.leftOperand.varValue)) && !b.isFunctionBlock)) {
			throw new OptimizationException("assigning unitilized vairable to another vairable." + currentIns.leftOperand.getVarNameWithoutIndex() + "->" + currentIns.rightOperand.varValue); 
		}
		if (currentIns.leftOperand.type.equals(ResultEnum.VARIABLE)) {
			// check if its in the variableTable and then replace
			if (variableTable.containsKey(currentIns.leftOperand.varValue)) {
				String varValue = currentIns.leftOperand.varValue;
				//currentIns.leftOperand.instrNum = variableTable.get(currentIns.leftOperand.varValue);
				//currentIns.leftOperand.type = ResultEnum.INSTR;
				currentIns.leftOperand =  createInstrResult(currentIns.leftOperand.varValue);
				LOGGER.debug("replacing left operand varvalue: " + varValue + " with value: " + currentIns.leftOperand.instrNum);
			}
		}

		if (currentIns.rightOperand.type.equals(ResultEnum.VARIABLE)) {
			// check if its in the variableTable and then replace
			if (variableTable.containsKey(currentIns.rightOperand.varValue)) {
				/*currentIns.rightOperand.instrNum = variableTable.get(currentIns.rightOperand.varValue);
				currentIns.rightOperand.type = ResultEnum.INSTR;*/
				String varValue = currentIns.rightOperand.varValue;

				currentIns.rightOperand =  createInstrResult(currentIns.rightOperand.varValue); 
				LOGGER.debug("replacing right operand varvalue: " + varValue + " with value: " + currentIns.rightOperand.instrNum);
			}
		}
		LOGGER.debug("###Exit otherEncountered###");

	}

	private Result createInstrResult(String variable) {
		Result result = new Result(ResultEnum.INSTR);
		result.instrNum = variableTable.get(variable);
		return result; 
	}

	public void replaceVars(BasicBlock b, int insIndex, String varToReplace) {
		// replace in a depth first search manner when

		List<Integer> blockInstructions = b.getInstructions();

		// for the remaining instructions inside the same basic block
		for (int j = insIndex; j < blockInstructions.size(); j++) {

			Instruction currentIns2 = originalInstructions.get(blockInstructions.get(j));

			LOGGER.debug("instruction operator: " + currentIns2.op);
			LOGGER.debug("instruction number: " + currentIns2.instNum);



			if (currentIns2.leftOperand.varValue.equals(varToReplace)) {
				currentIns2.leftOperand.type = ResultEnum.INSTR;
				currentIns2.leftOperand.instrNum = variableTable.get(varToReplace);
				LOGGER.debug("replacing left operand varvalue: " + currentIns2.leftOperand.varValue + " with value: " + currentIns2.leftOperand.instrNum);

			}

			if (currentIns2.rightOperand.varValue.equals(varToReplace)) {
				currentIns2.rightOperand.type = ResultEnum.INSTR;
				currentIns2.rightOperand.instrNum = variableTable.get(varToReplace);
				LOGGER.debug("replacing right operand varvalue: " + currentIns2.rightOperand.varValue + " with value: " + currentIns2.rightOperand.instrNum);

			}

		}

		for (BasicBlock d : b.dominatees) {
			replaceVars(d, 0, varToReplace);
		}

	}

	public void printTable() {
		for(String s: variableTable.keySet()) {
			System.out.println(s + ":" + variableTable.get(s));
		}
	}
}
