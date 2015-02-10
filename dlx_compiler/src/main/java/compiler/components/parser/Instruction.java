package compiler.components.parser;

import static compiler.components.intermeditate_rep.Result.EMPTY_RESULT;

import java.util.EnumSet;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import compiler.components.intermeditate_rep.BasicBlock;
import compiler.components.intermeditate_rep.Result;
import compiler.components.intermeditate_rep.Result.ResultEnum;

public class Instruction {
	static Logger LOGGER = LoggerFactory.getLogger(Instruction.class);

	public static enum OP {
		NEG, ADD, SUB, MUL, DIV, CMP, ADDA, LOAD, STORE, MOVE, PHI, END, BRA, BNE, BEQ, BLE, BLT, BGE, BGT, READ, WRITE, WLN, CALL, RETURN, POP, PUSH, SAVE_STATUS, MEM,
	}

	EnumSet<OP> BRANCH_INST = EnumSet.of( OP.BRA, OP.BNE, OP.BEQ, OP.BLE, OP.BLT, OP.BGE, OP.BGT);


	/** Map holding the instruction number to the actual instruction */
	public static Map<Integer, Instruction> programInstructions = new HashMap<Integer, Instruction>();

	/** The program instruction counter */
	public static Integer PC = 1;

	/****************************************Instruction Class Definition Begin****************************************/
	OP op;
	Result leftOperand;
	Result rightOperand;
	Integer instNum;

	public Instruction(OP operation, Result leftOperand, Result rightOperand) {
		this.op = operation;
		this.leftOperand = leftOperand;
		this.rightOperand = rightOperand;
		this.instNum = PC++;
	}

	public Instruction() { //TODO arguemtns needs to be added
		
	}

	public Instruction(OP op){
		this.op = op;
	}

	/***************************************Instruction Class Definition End******************************************/

	public static Result emitAssignmentInstruction(Result r1, Result r2) {

		Result result = new Result(ResultEnum.INSTR);
		Instruction assignInst;

	    if(r1.arrayExprs.size() > 0) {
	       assignInst = new Instruction(OP.STORE, r1, r2);  //store to an array
	    }
	    else {
	       assignInst = new Instruction(OP.MOVE, r1, r2);
	    }
	    
	    addInstruction(assignInst);

		return result;
	}

	public static void fixUp(Integer instructionNum) {
		Instruction fixUpInst = programInstructions.get(instructionNum);
		int newLocation = instructionNum + (PC - instructionNum);

		Result newResult = new Result();
		newResult.type = ResultEnum.INSTR;
		newResult.instrNum = newLocation;

		if (fixUpInst.op.equals(OP.BRA)) {
			fixUpInst.leftOperand = newResult;
		} else {
			fixUpInst.rightOperand = newResult;
		}
	}

	public static void fixLink(Result follow) {
		int instrNum = follow.fixUp;
		int nextInstrNum = 0;

		while (instrNum != 0) {
			Instruction fixupInstr = programInstructions.get(instrNum);
			nextInstrNum = fixupInstr.leftOperand.instrNum;
			fixUp(instrNum);
			instrNum = nextInstrNum;
		}
	}

	public static Result combineArithmetic(Result r1, String op, Result r2) {
		Result result;
		if (r1.type == ResultEnum.CONSTANT && r2.type == ResultEnum.CONSTANT) {
			result = new Result(ResultEnum.CONSTANT);
			if (op.equals("*")) {
				result.constValue = r1.constValue * r2.constValue;
			} else if (op.equals("/")) {
				result.constValue = r1.constValue / r2.constValue;
			} else if (op.equals("+")) {
				result.constValue = r1.constValue + r2.constValue;
			} else if (op.equals("-")) {
				result.constValue = r1.constValue - r2.constValue;
			}
		} else {
			result = new Result(ResultEnum.INSTR);

			OP operator;
			if (op.equals("*") || op.equals("/")) {
				operator = op.equals("*") ? OP.MUL : OP.DIV;
			} else {
				operator = op.equals("+") ? OP.ADD : OP.SUB;
			}

			Instruction instruction = new Instruction(operator, r1, r2);
			result.instrNum = instruction.instNum;

			addInstruction(instruction);
		}
		return result;
	}

	public static Result combineRelation(Result r1, String relationalOp, Result r2) {
		
		Result result = new Result(ResultEnum.CONDITION);
		result.conditionValue = relationalOp;

		Instruction instruction = new Instruction(OP.CMP, r1, r2);
		addInstruction(instruction);

		// create the branch instruction that will need to be fixed up
/*		OP op = invertRelop(relationalOp);
		Instruction branchInst = new Instruction(op, EMPTY_RESULT, EMPTY_RESULT);
		addInstruction(branchInst);*/
		
		result.instrNum = instruction.instNum;


		return result;
	}

	public static void createConditionalJumpFwd(Result x) {
		OP invertedRelop = invertRelop(x.conditionValue);

		Result r = new Result(ResultEnum.INSTR);
		r.instrNum = PC - 1;  //do the branch comparison on the previous instruction

		Instruction condJumpFwdInstr = new Instruction(invertedRelop, r, EMPTY_RESULT);

		addInstruction(condJumpFwdInstr);
		x.fixUp = PC - 1;
	}

	public static void createFwdJumpLink(Result x) {
		String location = String.valueOf(x.fixUp);

		Instruction fwdJumpInst = new Instruction(OP.BRA, EMPTY_RESULT, EMPTY_RESULT);

		Result r = new Result(ResultEnum.INSTR);
		r.instrNum = x.fixUp;

		fwdJumpInst.leftOperand = r;

		addInstruction(fwdJumpInst);
		x.fixUp = PC - 1;
	}

	public static void createFunctionCall(Function function, List<Result> funcArguments, Map<String, Variable> scopedSymbols) {

	    //Go through each on of the function arguments and if it is an array, load it, 
	    //otherwise push it on the stack as a variable or constant
	    Result loadResult;  
	    for(Result arg: funcArguments) {
	       if(arg.arrayExprs.size() > 0) {
	           Variable var = scopedSymbols.get(arg.varValue);
	           loadArrayIndex(var, arg);
	       }
	       Result instResult = new Result(ResultEnum.INSTR);
	       instResult.instrNum = PC -1;
           Instruction push = new Instruction(OP.PUSH, instResult, Result.EMPTY_RESULT);
           addInstruction(push);
	    }
	    
		Instruction saveStatus = new Instruction(OP.SAVE_STATUS, EMPTY_RESULT, EMPTY_RESULT);
		addInstruction(saveStatus);

		Instruction funcCallInst = new Instruction(OP.CALL, function, Result.EMPTY_RESULT);
		addInstruction(funcCallInst);
		// TODO fix this so that it can take in any kind of expression for
		// funcParams //particularly pushing a poppings on to the stack
	}

	public void printDominatorGraph(BasicBlock beginBlock, String fileName) {
/*		VCGWriter vcgwriter = new VCGWriter(fileName + "_dom.vcg", programInstructions);
		vcgwriter.emitBeginBasicBlock(beginBlock, true, false);
		vcgwriter.close();*/
	}

	public void printControlFlow(BasicBlock beginBlock, String fileName) {
/*		VCGWriter vcgwriter = new VCGWriter(fileName + "_cfg.vcg", programInstructions);
		vcgwriter.emitBeginBasicBlock(beginBlock, false, true);
		vcgwriter.close();*/
	}

	public static void printInstructions() {
		for(int i= 1; i <= programInstructions.entrySet().size(); i++) {
			System.out.println(programInstructions.get(i).toString());
		}
	}

	private static OP invertRelop(String relop) {
		if (relop.equals("<")) {
			return OP.BGE;
		} 
		else if (relop.equals(">")) {
			return OP.BLE;
		}
		else if (relop.equals("<=")) {
			return OP.BGT;
		}
		else if (relop.equals(">=")) {
			return OP.BLT;
		}
		else if (relop.equals("==")) {
			return OP.BNE;
		}
		else if (relop.equals("!=")) {
			return OP.BEQ;
		}
		return null;
	}

	/**
	 * Returns a formatted string of the instruction: format = 1d:%6s %4s %4s
	 */
	public String toString() {
		String left = leftOperand.toString();
		String right = rightOperand.toString();

		/*  		if(op.equals(OP.BRA)){
			left = "[" + left + "]";
		}
		
	    if(leftOperand.type.equals(ResultEnum.INSTR)){
			left = "(" + left + ")";
		}

		if(rightOperand.type.equals(ResultEnum.INSTR)){
		    right = "(" + right + ")"; 
		}
		
		else if(rightOperand.type.equals(ResultEnum.CONSTANT)){
			right = "#" + right;
		}*/


		String s = String.format("%-1d: %-6s %4s %4s", instNum, op.toString(), left, right); 
		return s;
	}

	public static void createReturn(Result result) {
		Instruction returnInst = new Instruction(OP.RETURN, result, EMPTY_RESULT);
		addInstruction(returnInst);
	}

	public static void addInstruction(Instruction instruction){
		programInstructions.put(instruction.instNum, instruction);
		LOGGER.info(instruction.toString());

	}



	/***Following instructions handle arrays ***/
 
    /**
     * Using row major order : 
     * BaseAddress + Width * (row)(NoColinArray) + col
     * @param arrayVar
     * @param exprResult
     * @return
     */
    public static Result loadArrayIndex(Variable arrayVar, Result exprResult) {
        List<Integer> arrayDims = arrayVar.getArrayDimSize();

     /*   Result arrayResult = new Result(ResultEnum.VARIABLE);
        arrayResult.varValue = arrayVar.varIdentifier;*/

        Result constant = new Result(ResultEnum.CONSTANT);
        constant.constValue = 4;

        Result framePoint = new Result(ResultEnum.VARIABLE);
        framePoint.varValue = "FP";

        List<Result> arrayIndicies = exprResult.arrayExprs;
        if(arrayDims.size() == 2) {  //we have a two d array
            Result numofColumn = new Result(ResultEnum.CONSTANT);
            numofColumn.constValue = arrayDims.get(1);

            Result columnToGet = arrayIndicies.get(0);
            Instruction colOffset = new Instruction(OP.MUL, columnToGet, numofColumn);  //add the final offsetbb
            addInstruction(colOffset);  

            Result colOffsetResult = new Result(ResultEnum.INSTR);
            colOffsetResult.instrNum = colOffset.instNum;
            Instruction load = new Instruction(OP.ADD, columnToGet, colOffsetResult);  //add the final offset
            addInstruction(load);  

        }
        else if (arrayDims.size() == 3){
            //do some other stuff here for 3d arrays (didnt see any arrays greater than 4d) 
        }
        else {
            //single dimension array
            Instruction load = new Instruction(OP.MUL, constant, arrayIndicies.get(0));  //add the final offset
            addInstruction(load);  
        }

        Instruction addFP = new Instruction(OP.ADD, framePoint, exprResult);
        addInstruction(addFP);
        
        Instruction adda = loadArray();
        
        Result result = new Result(ResultEnum.INSTR);
        result.instrNum = adda.instNum;
        return result;
    } 
    
    private static Instruction loadArray() {
        //create the adda
        Result inst1 = new Result(ResultEnum.INSTR);
        inst1.instrNum = PC - 1;
        Result inst2  = new Result(ResultEnum.INSTR);
        inst2.instrNum = PC - 2;
        Instruction adda = new Instruction(OP.ADDA, inst1, inst2); 
        addInstruction(adda);

        //create the load
        Result inst3 = new Result(ResultEnum.INSTR);
        inst3.instrNum = PC - 1;
        Instruction load = new Instruction(OP.LOAD, inst3, Result.EMPTY_RESULT);
        addInstruction(load);
        
        return load;
    }

    public static void endProgram() {
        Instruction end = new Instruction(OP.END, EMPTY_RESULT, EMPTY_RESULT);
        addInstruction(end); 
    }

    public static void createBackJump(int backJumpInstruction) {
        Result backJumpResult = new Result(ResultEnum.INSTR);
        backJumpResult.instrNum = PC - backJumpInstruction;
        Instruction backJump = new Instruction(OP.BRA, backJumpResult, Result.EMPTY_RESULT);
        addInstruction(backJump);
    }
}