package compiler.components.parser;

import static compiler.components.intermediate_rep.Result.EMPTY_RESULT;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import compiler.components.intermediate_rep.BasicBlock;
import compiler.components.intermediate_rep.Result;
import static compiler.components.intermediate_rep.Result.ResultEnum.*;

public class Instruction {
	static Logger LOGGER = LoggerFactory.getLogger(Instruction.class);

	public static enum OP {
		CP_CONST, CP_INS, NEG, ADD, SUB, MUL, DIV, CMP, ADDA, LOAD, STORE, MOVE, PHI, END, BRA, BNE, BEQ, BLE, BLT, BGE, BGT, READ, WRITE, WLN, CALL, RETURN, POP, PUSH, SAVE_STATUS, MEM,
	}

	//TODO Remove this set
	public static EnumSet<OP> BRANCH_INST = EnumSet.of( OP.BRA, OP.BNE, OP.BEQ, OP.BLE, OP.BLT, OP.BGE, OP.BGT);
	public static EnumSet<OP> ARITHMETIC_INST = EnumSet.of(OP.ADD, OP.DIV, OP.MUL, OP.SUB);

	public static List<String> predefined = new ArrayList<String>();
	static{
		// add the predefined functions to the symbol table 
		predefined.add("read");
		predefined.add("write");
		predefined.add("writeNL");
	}

	/** Map holding the instruction number to the actual instruction */
	public static Map<Integer, Instruction> programInstructions = new HashMap<Integer, Instruction>();
	public static List<Integer> phiInstructionNumbers = new ArrayList<Integer>();
	
	/** The program instruction counter */
	public static Integer PC = 1;

	public static Parser parser;

	/****************************************Instruction Class Definition Begin****************************************/
	public OP op;
	public Result leftOperand;
	public Result rightOperand;
	public Integer instNum;
	public Integer blockNumber;
	public InstType type;
	public Integer registerNum = 1;

	public enum InstType{
		IC(), CP();
	}

	public Instruction(OP operation, Result leftOperand, Result rightOperand) {
		this.op = operation;
		this.leftOperand = leftOperand;
		this.rightOperand = rightOperand;
		this.instNum = PC++;
		this.type = InstType.IC;
	}

	private Instruction() { //TODO arguemtns needs to be added
		
	}

	public static Instruction CSEInstruction(int instrNumber){
		Instruction inst = new Instruction();
		inst.type = InstType.CP;
		inst.instNum = instrNumber;
		return inst;
	} 

	public Instruction(OP op){
		this.op = op;
		this.instNum = PC++;
		this.type = InstType.IC;
	}

	/**
	 * Returns a formatted string of the instruction: format = 1d:%6s %4s %4s
	 */
	public String toString() {
		if(type.equals(InstType.CP)){
			return "=(" + instNum + ")";
		}

		String left = leftOperand.toString();

        String right = rightOperand.toString();

		String s = String.format("%-1d: %-6s %4s %4s", instNum, op.toString(), left, right); 
		return s;
	} 

	@Override
	public boolean equals(Object o) {
		if(o instanceof Instruction) {

			Instruction ins = (Instruction) o;

			if (this.op.equals(ins.op)) {

				if (this.leftOperand.equals(ins.leftOperand)) {
					return this.rightOperand.equals(ins.rightOperand);
				}

				if (this.leftOperand.equals(ins.rightOperand)) {
					return this.rightOperand.equals(ins.leftOperand);
				}
			}
		}
		return false;
	}


	/***************************************Instruction Class Definition End
	 * @throws ParsingException ******************************************/

	public static Result emitAssignmentInstruction(Result r1, Result r2) throws ParsingException {

		Result result = new Result(INSTR);
		Instruction assignInst;

	    if(r1.arrayExprs.size() > 0) {
	       assignInst = new Instruction(OP.STORE, Result.clone(r1), Result.clone(r2));  //store to an array
	    }
	    else {
	    	if(r1.type.equals(VARIABLE) && ((r2.type.equals(CONSTANT) || r2.type.equals(INSTR)))) {
	    		//we want the assignment with a vairable and constant to be of the form
	    		//MOVE const/instr Variable
	    		assignInst = new Instruction(OP.MOVE, Result.clone(r2), Result.clone(r1));
	    		if(r1.type.equals(VARIABLE) && r1.arrayExprs.size()  < 1) { //not an array
	    			generatePhi(Result.clone(r1));
	    		}
	    	}
	    	else {
	    		assignInst = new Instruction(OP.MOVE, Result.clone(r1), Result.clone(r2));
	    		if(r2.type.equals(VARIABLE) && r2.arrayExprs.size()  < 1) { //not an array
	    			generatePhi(Result.clone(r2));
	    		}	    	
	    	}
	    }
	    
	    addInstruction(assignInst);

		return result;
	}

	private static void generatePhi(Result varToInsert) throws ParsingException {
		if(parser.joinBlockStack.size() < 1){
			return;  //not in a if or while 
		}
		
		String currentVarWithoutIndex = varToInsert.getVarNameWithoutIndex(); 
		BasicBlock currentJoinBlock = parser.joinBlockStack.peek();  //get the current join block to generate phi instruction in

		List<Integer> allInstructions = currentJoinBlock.getInstructions();

		Instruction instruction;
		for(int instNum: allInstructions) {
			
			instruction = programInstructions.get(instNum);
			if(instruction.op.equals(OP.PHI)){
				if(instruction.leftOperand.getVarNameWithoutIndex().equals(currentVarWithoutIndex)){
					if(parser.comingFromLeft) {
						instruction.leftOperand = varToInsert;
						return;
					}
					instruction.rightOperand = varToInsert;
					return;
				}
			} 
		}

		Instruction phiInstruction = new Instruction(OP.PHI);
		
		if(parser.comingFromLeft) {
			Variable var = parser.getCurrentVarName(varToInsert.getVarNameWithoutIndex());
			phiInstruction.leftOperand = varToInsert;
			
			Result prev = new Result(VARIABLE);
			prev.varValue = var.getPreviousSSAVar();
			phiInstruction.rightOperand = prev;
		}
		else {
			Variable var = parser.getCurrentVarName(varToInsert.getVarNameWithoutIndex());
			phiInstruction.rightOperand = varToInsert;
			
			Result prev = new Result(VARIABLE);
			prev.varValue = var.getPreviousSSAVar();
			phiInstruction.leftOperand = prev;
		}
		addPhiInstruction(phiInstruction);
	}

	public static void fixUp(Integer instructionNum) {
		Instruction fixUpInst = programInstructions.get(instructionNum);
		int newLocation = instructionNum + (PC - instructionNum);

		Result newResult = new Result();
		newResult.type = INSTR;
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
		if (r1.type == CONSTANT && r2.type == CONSTANT) {
			result = new Result(CONSTANT);
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
			result = new Result(INSTR);

			OP operator;
			if (op.equals("*") || op.equals("/")) {
				operator = op.equals("*") ? OP.MUL : OP.DIV;
			} else {
				operator = op.equals("+") ? OP.ADD : OP.SUB;
			}

			Instruction instruction = new Instruction(operator, Result.clone(r2), Result.clone(r1));
			result.instrNum = instruction.instNum;

			addInstruction(instruction);
		}
		return result;
	}

	public static Result combineRelation(Result r1, String relationalOp, Result r2) {
		
		Result result = new Result(CONDITION);
		result.conditionValue = relationalOp;

		Instruction instruction = new Instruction(OP.CMP, Result.clone(r1), Result.clone(r2));
		addInstruction(instruction);

		result.instrNum = instruction.instNum;


		return result;
	}

	public static void createConditionalJumpFwd(Result x) {
		OP invertedRelop = invertRelop(x.conditionValue);

		Result r = new Result(INSTR);
		r.instrNum = PC - 1;  //do the branch comparison on the previous instruction

		Instruction condJumpFwdInstr = new Instruction(invertedRelop, Result.clone(r), EMPTY_RESULT);

		addInstruction(condJumpFwdInstr);
		x.fixUp = PC - 1;
	}

	public static void createFwdJumpLink(Result x) {
		String location = String.valueOf(x.fixUp);

		Instruction fwdJumpInst = new Instruction(OP.BRA, EMPTY_RESULT, EMPTY_RESULT);

		Result r = new Result(INSTR);
		r.instrNum = x.fixUp;

		fwdJumpInst.leftOperand = r;

		addInstruction(fwdJumpInst);
		x.fixUp = PC - 1;
	}

	public static void createFunctionCall(Function function, List<Result> funcArguments, Map<String, Variable> scopedSymbols) throws ParsingException {

	    //Go through each on of the function arguments and if it is an array, load it, 
	    //otherwise push it on the stack as a variable or constant
		Result arg2 = null;
	    for(Result arg: funcArguments) {
	           arg2 = Result.clone(arg);
	       if(arg.arrayExprs.size() > 0) {
	           Variable var = scopedSymbols.get(arg.varValue);
	           loadArrayIndex(var, arg);
		       Result instResult = new Result(INSTR);
		       instResult.instrNum = PC -1;
	           Instruction push = new Instruction(OP.PUSH, Result.clone(instResult), Result.EMPTY_RESULT);
	           addInstruction(push);
	       }
	       else  if(!predefined.contains(function.funcName)){
	           Instruction push = new Instruction(OP.PUSH, Result.clone(arg), Result.EMPTY_RESULT); 
	           addInstruction(push);
	       }

	    }
	    
	    if(!predefined.contains(function.funcName)) {
	    	Instruction saveStatus = new Instruction(OP.SAVE_STATUS, EMPTY_RESULT, EMPTY_RESULT);
	    	addInstruction(saveStatus);
			Instruction funcCallInst = new Instruction(OP.CALL, function, Result.EMPTY_RESULT);
			addInstruction(funcCallInst);

	    }
	    else {
	    	OP op = OP.WRITE;

	    	if(function.funcName.equals("read")) {
	    		op = OP.READ;
	    	}
	    	else if(function.funcName.equals("writeNL")) {
	    		op = OP.WLN;
	    	}
			Instruction funcCallInst = new Instruction(op, arg2, Result.EMPTY_RESULT);
			addInstruction(funcCallInst);

	    }

		// TODO fix this so that it can take in any kind of expression for
		// funcParams //particularly pushing a poppings on to the stack
	}

	public static void printInstructions() {
		for(int i= 1; i <= programInstructions.entrySet().size(); i++) {
			Instruction instruction = programInstructions.get(i);
			if(instruction == null) {
				continue; //we remove instructions at times, so this may be null
			}
			System.out.println(instruction);
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


	public static void createReturn(Result result) {
		Instruction returnInst = new Instruction(OP.RETURN, Result.clone(result), EMPTY_RESULT);
		addInstruction(returnInst);
	}

	public static void addInstruction(Instruction instruction){
		programInstructions.put(instruction.instNum, instruction);
		BasicBlock basicBlock = parser.blockStack.peek();
		basicBlock.addInstruction(instruction.instNum);
		instruction.blockNumber = basicBlock.blockNumber;
		LOGGER.trace(instruction.toString());
	}

	/**
	 * Adds the phi instruction to the basic block, global instruction map,
	 * and the global phi instruction numbers list.
	 * 
	 * @param instruction
	 */
	public static void addPhiInstruction(Instruction instruction){
		programInstructions.put(instruction.instNum, instruction);
		phiInstructionNumbers.add(instruction.instNum);
		
		BasicBlock joinBlock = parser.joinBlockStack.peek();
		joinBlock.addPhiInstruction(instruction.instNum);

		propogatePhi(instruction);

		instruction.blockNumber = joinBlock.blockNumber;
		LOGGER.trace(instruction.toString());
	}
	
	
	private static void propogatePhi(Instruction phiInstruction) {

		if(parser.joinBlockStack.size() > 1) {
			BasicBlock outerMostJoin = parser.joinBlockStack.firstElement();
			for (int i = 0; i < outerMostJoin.phiInstructions.size(); i++) {
				Instruction outerPhi = programInstructions.get(outerMostJoin.phiInstructions.get(i));
				
				if(parser.comingFromLeft) {
					if(outerPhi.leftOperand.getVarNameWithoutIndex().equals(phiInstruction.leftOperand.getVarNameWithoutIndex())) {
						outerPhi.leftOperand.varValue = new String(outerPhi.leftOperand.getVarNameWithoutIndex() + "_" + phiInstruction.instNum);
						return;
					}
				} 
				else if (!parser.comingFromLeft) {
					if(outerPhi.rightOperand.getVarNameWithoutIndex().equals(phiInstruction.rightOperand.getVarNameWithoutIndex())) {
						outerPhi.rightOperand.varValue = new String(outerPhi.rightOperand.getVarNameWithoutIndex() + "_" + phiInstruction.instNum);
                        return;
					}
				} 
				
 
			}
		
			Instruction newPhiToAdd = new Instruction(OP.PHI);
			newPhiToAdd.leftOperand = Result.clone(phiInstruction.leftOperand);
			if (parser.comingFromLeft) newPhiToAdd.leftOperand.varValue = newPhiToAdd.leftOperand.getVarNameWithoutIndex() + "_" + phiInstruction.instNum;
			newPhiToAdd.rightOperand = Result.clone(phiInstruction.rightOperand);
			if (!parser.comingFromLeft) newPhiToAdd.rightOperand.varValue = newPhiToAdd.rightOperand.getVarNameWithoutIndex() + "_" + phiInstruction.instNum;
			newPhiToAdd.blockNumber = outerMostJoin.blockNumber;
			phiInstructionNumbers.add(newPhiToAdd.instNum);
			outerMostJoin.addPhiInstruction(newPhiToAdd.instNum);
			programInstructions.put(newPhiToAdd.instNum, newPhiToAdd);
		}
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

        Result framePoint = new Result(VARIABLE);
        framePoint.varValue = "FP";
       	
        Result constant = new Result(CONSTANT);
        
       	List<Result> arrayIndicies = exprResult.arrayExprs;
        
       	if(arrayDims.size() > 1) {  //we have a two or more d array
        	//get the num of columns in the array declaration
            Result numofColumn = new Result(CONSTANT);
            numofColumn.constValue = arrayDims.get(1);
            
            // do all the muls first
            for (int i = arrayDims.size()-1; i >= 0; i--) {
                Result c = new Result(CONSTANT);
                c.constValue = (int) Math.pow((double) 2, (double)(i+2));
            	
            	Result rowToRetrieve = arrayIndicies.get(arrayDims.size()-1-i);
                Instruction rowOffset = new Instruction(OP.MUL, Result.clone(rowToRetrieve), c);
                addInstruction(rowOffset);
            }
            
            // do all adds for the muls
            for (int i = 0; i < arrayDims.size()-1; i++) { 
            	Result c = new Result(INSTR);
            	c.instrNum = PC - arrayDims.size() + i;
            	Result d = new Result(INSTR);
            	d.instrNum = PC - arrayDims.size() + i + 1;
            	Instruction addOfMuls = new Instruction(OP.ADD, Result.clone(c), Result.clone(d));
                addInstruction(addOfMuls);
            }
        
       	}
        else {
            //single dimension array
        	constant.constValue = 4;
            Instruction load = new Instruction(OP.MUL, Result.clone(constant), Result.clone(arrayIndicies.get(0)));  //add the final offset
            addInstruction(load);  
        }

        //reference the base address of the array
        Instruction addFP = new Instruction(OP.ADD, Result.clone(framePoint),Result.clone(exprResult));
        addInstruction(addFP);
        
        Instruction adda = loadArray();
        
        Result result = new Result(INSTR);
        result.instrNum = adda.instNum;
        return result;
    } 
    
    private static Instruction loadArray() {
        //create the adda
        Result inst1 = new Result(INSTR);
        inst1.instrNum = PC - 2;
        Result inst2  = new Result(INSTR);
        inst2.instrNum = PC - 1;
        Instruction adda = new Instruction(OP.ADDA, Result.clone(inst1), Result.clone(inst2)); 
        addInstruction(adda);

        //create the load
        Result inst3 = new Result(INSTR);
        inst3.instrNum = PC - 1;
        Instruction load = new Instruction(OP.LOAD, Result.clone(inst3), Result.EMPTY_RESULT);
        addInstruction(load);
        
        return load;
    }

    public static void endProgram() {
        Instruction end = new Instruction(OP.END, EMPTY_RESULT, EMPTY_RESULT);
        addInstruction(end); 
    }

    public static void createBackJump(int backJumpInstruction) {
        Result backJumpResult = new Result(INSTR);
        backJumpResult.instrNum = backJumpInstruction;
        Instruction backJump = new Instruction(OP.BRA, Result.clone(backJumpResult), Result.EMPTY_RESULT);
        addInstruction(backJump);
    }
}