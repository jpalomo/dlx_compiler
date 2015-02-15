package compiler.components.optimization;

import java.util.HashMap;
import java.util.Map;

import compiler.components.intermediate_rep.BasicBlock;
import compiler.components.intermediate_rep.Result;
import compiler.components.intermediate_rep.Result.ResultEnum;
import compiler.components.parser.Instruction;
import compiler.components.parser.Instruction.InstType;
import compiler.components.parser.Parser;

public class CopyPropagation {

	Map<Integer, Instruction> originalInstructions;
	Map<Integer, Instruction> cseInstructions;
	Map<Integer, BasicBlock> blockMap;

	public CopyPropagation(Parser parser){
		//copy all the instructions to a local copy
		originalInstructions = new HashMap<Integer, Instruction>(parser.getProgramInstructions());

		cseInstructions = new HashMap<Integer, Instruction>(parser.getProgramInstructions());

		blockMap = new HashMap<Integer, BasicBlock>(parser.blockMap); 
	}

	public void optimize(){
		for(int i =1; i <= originalInstructions.size(); i++) {
			Instruction instr = originalInstructions.get(i);
			if(instr.op.equals(Instruction.OP.MOVE)){
				if(instr.leftOperand.type.equals(ResultEnum.VARIABLE)){
					if(instr.rightOperand.type.equals(ResultEnum.VARIABLE)){
						//this instruction is the same as the assignment of the left operand 
						Instruction inst = cseInstructions.get(instr.leftOperand.getVariableIndex());
						int num = getInstNum(inst);
						cseInstructions.put(i, Instruction.CSEInstruction(num)); 
					}
				}
				else if(instr.rightOperand.equals(ResultEnum.VARIABLE)){
					if(instr.type.equals(ResultEnum.VARIABLE)){
						//this instruction is the same as the assignment of the left operand 
						Instruction inst = cseInstructions.get(instr.leftOperand.getVariableIndex());
						int num = getInstNum(inst);
						cseInstructions.put(i, Instruction.CSEInstruction(num)); 
					}
				}
			}
			else if(Instruction.ARITHMETIC_INST.contains(instr.op)) {
				if(instr.leftOperand.type.equals(ResultEnum.VARIABLE)) {
					Instruction inst = cseInstructions.get(instr.leftOperand.getVariableIndex());
					int num = getInstNum(inst);

					Result result1 = new Result(ResultEnum.INSTR);
					result1.instrNum = num;

					if(instr.rightOperand.equals(ResultEnum.VARIABLE)) {
						Instruction inst2 = cseInstructions.get(instr.leftOperand.getVariableIndex());
						int num2 = getInstNum(inst2);
						Result result2 = new Result(ResultEnum.INSTR);
						result2.instrNum = num2;
						
						inst.leftOperand = result1;
						inst.rightOperand = result2;

						cseInstructions.put(i, inst);

					}
					else {

						inst.leftOperand = result1;
						cseInstructions.put(i, inst);
					}


				}
			}
		}
	}

	private int getInstNum(Instruction inst) {
		int num = inst.instNum;

		while(inst.type.equals(InstType.CP)) {
			inst = cseInstructions.get(inst.instNum);
			num = inst.instNum;
		} 
		return num;
	}
}
