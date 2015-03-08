package compiler.components.codegen;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import compiler.components.intermediate_rep.Result.ResultEnum;
import compiler.components.parser.Instruction;
import compiler.components.parser.Instruction.OP;

public class DLXCodeGen {

	static Logger LOGGER = LoggerFactory.getLogger(DLXCodeGen.class);

	LinkedList<Integer> instructionWords;
	Map<Integer, Instruction> programInstructions; // IR form of the program
													// instructions
	List<String> mnemonics;

	public DLXCodeGen(Map<Integer, Instruction> programInstructions) {
		this.programInstructions = programInstructions;
		instructionWords = new LinkedList<Integer>();
		mnemonics = Arrays.asList(DLXSimulator.mnemo);
	}

	public int[] getInstructionWords() {
		int[] instructionWordsAsArray = new int[instructionWords.size()];

		for (int i = 0; i < instructionWords.size(); i++) {
			instructionWordsAsArray[i] = instructionWords.get(i);
		}
		return instructionWordsAsArray;
	}

	public void generateAssembly() {
		for (int i = 1; i <= programInstructions.entrySet().size(); i++) {
			Instruction instruction = programInstructions.get(i);
			if (instruction == null) {
				continue; // we remove instructions at times, so this may be
							// null
			}
			LOGGER.debug("Generating assembly for instruction: {}", instruction);
			decode(instruction);
		}
	}

	private void decode(Instruction instruction) {
		OP instrOP = instruction.op;

        int op = 0;
        int arg1 = 0;
        int arg2 = 0;
        int arg3 = 0;
        int machineInstruction = 0;
		switch (instrOP) {
		case MUL:
			if (instruction.leftOperand.type.equals(ResultEnum.CONSTANT)) {
				op = mnemonics.indexOf("MULI");
				arg1 = instruction.registerNum; //destination for the instruction
				arg2 = instruction.rightOperand.registerNum; //register operand
				arg3 = instruction.leftOperand.constValue; //constant
				machineInstruction = DLXSimulator.assemble(op, arg1, arg2, arg3);
				instructionWords.add(machineInstruction);
				break;
			}
		case ADD:
			break;
		case ADDA:
			break;
		case BEQ:
			break;
		case BGE:
			break;
		case BGT:
			break;
		case BLE:
			break;
		case BLT:
			break;
		case BNE:
			break;
		case BRA:
			break;
		case CALL:
			break;
		case CMP:
			break;
		case CP_CONST:
			break;
		case CP_INS:
			break;
		case DIV:
			break;
		case END:
			break;
		case LOAD:
			break;
		case MEM:
			break;
		case MOVE:
			break;
		case NEG:
			break;
		case PHI:
			break;
		case POP:
			break;
		case PUSH:
			break;
		case READ:
			break;
		case RETURN:
			break;
		case SAVE_STATUS:
			break;
		case STORE:
			break;
		case SUB:
			break;
		case WLN:
			break;
		case WRITE:
			break;
		default:
			break;
		}
	}

}
