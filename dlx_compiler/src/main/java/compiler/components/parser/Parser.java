package compiler.components.parser;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Stack;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import compiler.components.intermediate_rep.BasicBlock;
import compiler.components.intermediate_rep.Result;
import compiler.components.intermediate_rep.Result.ResultEnum;
import compiler.components.lex.Scanner;
import compiler.components.lex.Token;
import compiler.components.lex.Token.Kind;
import compiler.components.parser.Variable.VarType;

//TODO different colored arrows for functions
/**
 * Implementation of a top-down recursive descent parser.
 *
 * @author John Palomo, 60206611
 */
public class Parser {
	static Logger LOGGER = LoggerFactory.getLogger(Parser.class);
	
	public Token currentToken;
	public Map<String, Variable> glblSymbolTable;
	public Map<String, Function> functionSymbolTable;
	public Map<String, Variable> symbols;
	private Scanner scanner;

	public BasicBlock currentBlock;
	public Stack<BasicBlock> blockStack = new Stack<BasicBlock>();
	public Stack<BasicBlock> joinBlockStack = new Stack<BasicBlock>();
	public Stack<BasicBlock> whileFollowStack = new Stack<BasicBlock>();

	public Stack<Integer> stackDepth = new Stack<Integer>();

	public boolean inLoop = false;
	public BasicBlock loopHeader = null;


	public Map<Integer, BasicBlock> blockMap = new HashMap<Integer, BasicBlock>();

	public boolean comingFromLeft = true;

	public static List<String> predefined = new ArrayList<String>();
	static{
		predefined.add("InputNum");
		predefined.add("OutputNum");
		predefined.add("OutputNewLine");
	} 

	public Parser(String fileName) {
		scanner = new Scanner(fileName);
		glblSymbolTable = new HashMap<String, Variable>();
		functionSymbolTable = new HashMap<String, Function>();

		// add the predefined functions to the symbol table 
		functionSymbolTable.put("InputNum", new Function("read", new ArrayList<String>(), glblSymbolTable));
		functionSymbolTable.put("OutputNum", new Function("write", new ArrayList<String>(), glblSymbolTable));
		functionSymbolTable.put("OutputNewLine", new Function("wln", new ArrayList<String>(), glblSymbolTable));

		currentBlock = createBasicBlock();
		Instruction.parser = this;
	}

	public Parser parse() throws ParsingException {
		eatToken(); // get the first token
		computation(); 
		return this;
	}

	/** computation = 'main' {varDecl} {funcDecl} '{' statSequence '}' '.' */
	private void computation() throws ParsingException {
		expect(Kind.MAIN);

		while (currentToken.kind != Kind.EOF && currentToken.kind != Kind.ERROR && currentToken.kind != Kind.PERIOD) {

			if (accept(Kind.VAR) || accept(Kind.ARRAY)) { 
				varDecl(glblSymbolTable); //try and add the declaration to the global table
			} 
			else if (accept(Kind.FUNCTION) || accept(Kind.PROCEDURE)) {
				funcDecl(); // TODO add to symbole table?
			}
			else if (accept(Kind.BEGIN)) {
				eatToken(); // eat the open brace
				currentBlock = createBasicBlock();
				blockStack.push(currentBlock);
				statSequence();
				expect(Kind.END);
			} else {
				throw new ParsingException();
			}
		}
		expect(Kind.PERIOD);
		Instruction.endProgram();
		blockStack.pop();
		
		return;
	}

	/** varDecl = typeDecl ident { ',' ident } ';' */
	private void varDecl(Map<String, Variable> symbolTable) throws ParsingException {

		List<Integer> arrayDims = typeDecl(); 
		String ident = ident();

		VarType declType;
		Variable variable;
		if(arrayDims.size() > 0) {
			declType = VarType.ARRAY;
			variable = new Variable(ident, arrayDims, VarType.ARRAY);
		}
		else {
			declType = VarType.VAR;
			variable = new Variable(ident, arrayDims, VarType.VAR);
		}

		addVarToSymbolTable(symbolTable, variable);

		while (accept(Kind.COMMA)) {
			eatToken(); // eat the comma
			ident = ident();
			if(declType.equals(VarType.VAR)) {
				variable = new Variable(ident, arrayDims, declType);
			}
			else {
				variable = new Variable(ident, arrayDims, declType);
			}
			addVarToSymbolTable(symbolTable, variable);
		}
		
		expect(Kind.SEMI_COL);
	}

	/** funcDecl = ('function' | 'procedure') ident [formalParam] ';' funcBody ';' */
	private void funcDecl() throws ParsingException {
		eatToken(); // eat function or procedure token

		String funcName = ident();
		List<String> formalParams = formalParam();

		BasicBlock functionBlock = createBasicBlock();  //create a new block for function code
		Function function = new Function(funcName, formalParams, glblSymbolTable, functionBlock);
		blockStack.push(functionBlock); //push the block onto the stack so that code gets generated in this block

		addFunctionToFunctionTable(function);
		
		expect(Kind.SEMI_COL);

		funcBody(function);

		expect(Kind.SEMI_COL); 
		blockStack.pop();  //resotre the stack
	}

	/** formalParam = '(' [ident { ',' ident }] ')' */
	private List<String> formalParam() throws ParsingException {
		expect(Kind.OPN_PAREN);

		List<String> params = new ArrayList<String>();
		
		if (accept(Kind.IDENTIFIER)) {
			params.add(ident());
			while (accept(Kind.COMMA)) {
				eatToken(); // eat the comma
				params.add(ident());
			}
		}

		expect(Kind.CLS_PAREN);
		return params;
	}

	/** funcBody = { varDecl } '{' [statSequence] '}' */
	private void funcBody(Function function) throws ParsingException {
		//initialize a local symbol table with the globals in it
		Map<String, Variable> localSymbolsTable = function.localSymbols; 

		while (accept(Kind.VAR) || accept(Kind.ARRAY)) {
			varDecl(localSymbolsTable);
		} 

		//set the symbols for other functions to find
		symbols = localSymbolsTable;

		expect(Kind.BEGIN);

		if (accept(Kind.LET) || accept(Kind.CALL) || accept(Kind.IF) || accept(Kind.WHILE) || accept(Kind.RETURN)) {
			statSequence();
		}

		symbols = null; //remove the symbols from the scope
		expect(Kind.END);
	}

	/** typeDecl = 'var' | 'array' '[' number ']' { '[' number ']' } */
	private List<Integer> typeDecl() throws ParsingException {
		List<Integer> arrayDims = new ArrayList<Integer>();

		if (currentToken.kind == Kind.VAR) {
			eatToken(); // eat the var token
		} else if (currentToken.kind == Kind.ARRAY) {
			eatToken(); // eat the array token
			expect(Kind.OPN_BRACK);
			arrayDims.add(number());
			expect(Kind.CLS_BRACK);

			while (accept(Kind.OPN_BRACK)) {
				eatToken(); // eat the open bracket
				arrayDims.add(number());
				expect(Kind.CLS_BRACK);
			}
		}
		return arrayDims;
	}

	/** statSequence = statement { ';' statement } */
	private Result statSequence() throws ParsingException {
		Result x = statement();
		while (accept(Kind.SEMI_COL)) {
			eatToken(); // eat the semicolon
			x = statement();
		}
		return x;
	}

	/** statement = assignment | funcCall | ifStatement | whileStatement | returnStatement */
	private Result statement() throws ParsingException {
		Result result = null;
		if (accept(Kind.LET)) {
			result = assignment();
		} else if (accept(Kind.CALL)) {
			funcCall();
		} else if (accept(Kind.IF)) {
			result = ifStatement();
		} else if (accept(Kind.WHILE)) {
			result = whileStatement();
		} else if (accept(Kind.RETURN)) {
			result = returnStatement();
		} else {
			throw new ParsingException();
		}

		return result;
	}

	/** assignment = 'let' designator '<-' expression */
	private Result assignment() throws ParsingException {
		expect(Kind.LET);

		Result desResult = designator();
		String ident = desResult.varValue;

		expect(Kind.BECOMES);

		Result assignmentResult = expression();
		if(assignmentResult.arrayExprs.size() > 0) { //we are dereferencing an array
		    Variable var = getCurrentVarName(assignmentResult.varValue);
		    assignmentResult = Instruction.loadArrayIndex(var, assignmentResult);
		}

		//do this after because we want to get the correct variable value for the assignment result,
		//if we do this before, we update the vairable before we assign it
        Result designator;
        Variable currentVar;
        if(desResult.arrayExprs.size() > 0) {
           currentVar = getCurrentVarName(ident);
           designator = Instruction.loadArrayIndex(currentVar, desResult); 
        }
        else{
            currentVar = getCurrentVarName(ident);
            updateSSAVarSymbol(currentVar);
            designator = new Result(ResultEnum.VARIABLE);
            designator.varValue = currentVar.getAsSSAVar();  //if variable ...increment ssa index
        }
		
		designator = Instruction.emitAssignmentInstruction(assignmentResult, designator);
		return designator;
	}

	/** funcCall = 'call' ident [ '(' [expression { ',' expression } ] ')' ] */
	private Result funcCall() throws ParsingException {
		expect(Kind.CALL);

		String funcIdent = ident();
		

		List<Result> funcParams = new ArrayList<Result>();
		if (accept(Kind.OPN_PAREN)) {
			eatToken(); // eat the open paren
			if(accept(Kind.CLS_PAREN)){  //no arguments
			    eatToken();  //the paren
			}else{ 
			    
                funcParams.add(expression());  //add the first param to the result
                
                while (accept(Kind.COMMA)) {
                	eatToken(); // eat the comma
                	funcParams.add(expression());
                }
                expect(Kind.CLS_PAREN);
			}
		}

		Function function = functionSymbolTable.get(funcIdent);

		Map<String, Variable> scopeVars = getCurrentScopeSymbols();
		Instruction.createFunctionCall(function, funcParams, scopeVars);
		
		Result funcCallResult = new Result(ResultEnum.INSTR);
		funcCallResult.instrNum = Instruction.PC - 1;

		if(!predefined.contains(funcIdent)){
			addControlFlow(blockStack.peek(), function.beginBlockForFunction);  //predifined functions dont have code block
		}
		return funcCallResult;
	}

	/** ifStatement = 'if' relation 'then' statSequence [ 'else' statSequence ] 'fi' */
	private Result ifStatement() throws ParsingException {
		expect(Kind.IF);

		BasicBlock joinBlock = createBasicBlock();
		joinBlockStack.push(joinBlock);

		BasicBlock mainDominator = blockStack.peek();
		addDominatee(mainDominator, joinBlock);

		Result follow = new Result();
		follow.fixUp = 0; 

		Result relation = relation();
		Instruction.createConditionalJumpFwd(relation);  //will set relations fixup to PC -1

		expect(Kind.THEN); 

		BasicBlock ifBodyBlock = createBasicBlock();
		addControlFlow(blockStack.peek(), ifBodyBlock);  //current block -> ifbodyblock

		blockStack.push(ifBodyBlock);
		statSequence(); // parse 'then' block

        addDominatee(mainDominator, blockStack.peek());
		if (accept(Kind.ELSE)) {
			comingFromLeft = false;
			eatToken(); // eat the else

			addControlFlow(blockStack.peek() , joinBlock);

			Instruction.createFwdJumpLink(follow); 
			Instruction.fixUp(relation.fixUp);
			blockStack.pop(); //pop the ifbody off the stack, were done with it

			BasicBlock elseBodyBlock = createBasicBlock();
			addControlFlow(blockStack.peek(), elseBodyBlock); //current block -> elsebodyblock 
			addDominatee(mainDominator, elseBodyBlock);

			blockStack.push(elseBodyBlock);
			statSequence();

			comingFromLeft = true; 
		} else {
			Instruction.fixUp(relation.fixUp);
			addControlFlow(blockStack.pop(), joinBlock); 
		}

		Instruction.fixLink(follow);

		if(joinBlockStack.size() > 0) {
			addControlFlow(blockStack.pop(), joinBlockStack.peek());
		}
		//push the join block for our current block
		blockStack.push(joinBlockStack.pop());
		
		expect(Kind.FI);

		return relation;
	}

	/** whileStatement = 'while' relation 'do' statSequence 'od' */
	private Result whileStatement() throws ParsingException {
		expect(Kind.WHILE);

		//save the cmp instruction number
		int cmpInstructionNum = Instruction.PC; //want the value to the relation instruction to come back to
		
		/*
		 * the following instructions should be generated in the current block
		 * that has instructions in it.
		 */
		BasicBlock incomingBlock = createBasicBlock();

		stackDepth.push(1);
		if(stackDepth.size() < 1) {
			inLoop = true;
			loopHeader = incomingBlock;
		}
		
		addControlFlow(blockStack.pop(), incomingBlock);  //remove the previous block and add control flow from it to the new block
		joinBlockStack.push(incomingBlock);
		blockStack.push(incomingBlock);
		
		Result relation = relation();
		Instruction.createConditionalJumpFwd(relation);
		
		expect(Kind.DO);

		comingFromLeft = false;
		BasicBlock whileBodyBlock = createBasicBlock();  
		blockStack.push(whileBodyBlock);
		statSequence();
		joinBlockStack.push(whileBodyBlock);
		
		Instruction.createBackJump(cmpInstructionNum);  //branch back to the comparison in the while
		
		expect(Kind.OD);

		Instruction.fixUp(relation.fixUp);  //fix up branching when relation is false (jump over the while)
		
		whileBodyBlock = joinBlockStack.pop();  //done generating instructions for the body
		addDominatee(loopHeader, whileBodyBlock);
		if(joinBlockStack.size() - 1 > 0) {
			addControlFlow(incomingBlock, whileBodyBlock); 
			addControlFlow(blockStack.peek(), joinBlockStack.peek());
		}
		else {
			addControlFlow(incomingBlock, whileBodyBlock); 
			addControlFlow(whileBodyBlock, incomingBlock);
			
		}

		/*Create the new block for instructions after the while loop */
		BasicBlock followBlock = createBasicBlock();
		addControlFlow(incomingBlock, followBlock);
		blockStack.push(followBlock);

		joinBlockStack.pop();

		if(stackDepth.size() == 1) {
			addDominatee(loopHeader, followBlock);
		}
		stackDepth.pop();


		//For the sake of not returning null, we create an instruction result
		Result result = new Result(ResultEnum.INSTR);
		result.instrNum = Instruction.PC-1;
		comingFromLeft = true;
		return result;
	}

	/**
	 * returnStatement = 'return' [ expression ]
	 * 
	 * @throws ParsingException
	 */
	private Result returnStatement() throws ParsingException {
		expect(Kind.RETURN);

		Result result;
		if (accept(Kind.IDENTIFIER) || accept(Kind.NUMBER)) {
			 result = expression();
		} else if (accept(Kind.OPN_PAREN) || accept(Kind.FUNCTION)
				|| accept(Kind.PROCEDURE)) { // identifier is not)
			eatToken(); // eat the paren, 'function', or 'procedure' token
			result = expression();
		}
		else{
			throw new ParsingException("Did not find the correct token in return statement: " + getLineNum());
		}

		Instruction.createReturn(result);

		return result;
	}

	/** designator = ident { '[' expression ']' } */
	private Result designator() throws ParsingException {
		String ident = ident();
		Variable var = getCurrentVarName(ident);
		
		Result result = new Result(ResultEnum.VARIABLE); 
		result.varValue = var.varIdentifier;

		while (accept(Kind.OPN_BRACK)) {
			eatToken(); // eat the open bracket
			Result expr = expression();
			result.arrayExprs.add(expr);
			expect(Kind.CLS_BRACK);
		}

		return result;
	}

	/** expression = term { ('+' | '-') term } */
	private Result expression() throws ParsingException {
		Result result1 = term();
		if (accept(Kind.PLUS) || accept(Kind.MINUS)) {
			do {
				String op = currentToken.getLexeme();
				eatToken(); // eat the operator

				Result result2 = term();

				result1 = Instruction.combineArithmetic(result1, op, result2);
			} while (accept(Kind.PLUS) || accept(Kind.MINUS));
		}
		return result1;
	}

	/** term = factor { ('*' | '/') factor } */
	private Result term() throws ParsingException {
		Result result1 = factor();
		if (accept(Kind.TIMES) || accept(Kind.DIV)) {
			do {
				String op = currentToken.getLexeme();

				eatToken(); // eat the times or div;

				Result result2 = factor();

				result1 = Instruction.combineArithmetic(result1, op, result2);

			} while (accept(Kind.TIMES) || accept(Kind.DIV));
		}
		return result1;
	}

	/** relation = expression relOp expression */
	private Result relation() throws ParsingException {
		Result leftExpr = expression();

		String op = currentToken.getLexeme();
		eatToken();
		Result rightExpr = expression();

		Result x = Instruction.combineRelation(leftExpr, op, rightExpr);

		return x;
	}

	/** factor = designator | number | '(' expression ')' | funcCall */
	private Result factor() throws ParsingException {

		Result result = new Result();
  		if (accept(Kind.IDENTIFIER)) {
			result = designator();  //get the original desingator variable name
			Variable currentVar = getCurrentVarName(result.varValue);  //get the current ssa var
			result.varValue = result.varValue;  
			if(currentVar.isVar){
				result.varValue = currentVar.getAsSSAVar();  //we dont use ssa on arrays, only vars
			}
			return result;
		} else if (accept(Kind.NUMBER)) {
			int x = number();
			result.type = ResultEnum.CONSTANT;
			result.constValue = x;
			return result;
		}
		else if(accept(Kind.OPN_PAREN)) {
			eatToken();
			result = expression();
			expect(Kind.CLS_PAREN);
			return result;
		}
		else if(accept(Kind.CALL)) {
			 result = funcCall();
			 return result;
		} 
		else { 
			throw new ParsingException();
		}
	}

	/** ident = letter { letter | digit } */
	private String ident() {
		LOGGER.debug("Identifier found: " + currentToken.getLexeme());
        String ident = currentToken.getLexeme();
		eatToken(); // eat the identifier
		return ident;
	}

	/** number = digit {digit} */
	private int number() {
		LOGGER.debug("Number found: " + currentToken.getLexeme());
		int num = Integer.valueOf(currentToken.getLexeme());
		eatToken(); // eat the number
		return num;
	}

	/**
	 * Determines if the current token matches the expected token. If it does,
	 * we eat the token, otherwise the token was not what we expected during
	 * parsing and throw and error. kind the expected token kind that should be
	 * accepted
	 * 
	 */
	private void expect(Kind kind) throws ParsingException {
		if (currentToken.kind != kind) {
			throw new ParsingException(kind, currentToken);
		}
		eatToken();
	}

	/** Determines whether the current token's kind matches the formal parameter kind */
	private boolean accept(Kind kind) {
		if (currentToken.kind == kind) {
			return true;
		}
		return false;
	}

	/** Eats the current token and gets the next token */
  	private void eatToken() {
		currentToken = scanner.nextToken();
	}

	public int getLineNum() {
		return scanner.lineNum;
	}

	/** adds a control flow entry from 'from' block to 'to' block */
	private void addControlFlow(BasicBlock from, BasicBlock to) {
		from.addControlFlow(to);
	}

	private void addDominatee(BasicBlock from, BasicBlock to) {
		//from.addDominatee(to);
	}

	private void addVarToSymbolTable(Map<String, Variable> symbolTable, Variable varToAdd) throws ParsingException{
		if(symbolTable.containsKey(varToAdd.getVarIdentifier())){
			throw new ParsingException("symbol re-declared:" + varToAdd.getVarIdentifier() + " on line number : " + getLineNum());
		}
		symbolTable.put(varToAdd.getVarIdentifier(), varToAdd); 
	}

	private void updateSSAVarSymbol(Variable varToAdd) throws ParsingException{
		String origVarName = varToAdd.varIdentifier;
		varToAdd.previousSSAIndex = varToAdd.ssaIndex;
		varToAdd.ssaIndex = Instruction.PC;
		if (symbols != null) {
			for (String var : symbols.keySet()) {
				if (origVarName.equals(var)) {
					symbols.put(origVarName, varToAdd);
				}
			}
		}

		if (glblSymbolTable != null) {
			for (String var : glblSymbolTable.keySet()) {
				if (origVarName.equals(var)) {
					glblSymbolTable.put(origVarName, varToAdd);
				}
			}
		} 
	} 

	private void addFunctionToFunctionTable(Function function) throws ParsingException{
		if(functionSymbolTable.containsKey(function.funcName)) {
			throw new ParsingException("function is already declared:" + function.funcName + " on line number : " + getLineNum());
		}
		functionSymbolTable.put(function.funcName, function); 
	}
	/**  instructs the parser to print the control flow of the program in vcg format */

	public void printInstructions(){
		Instruction.printInstructions();
	}

	public Map<Integer, Instruction> getProgramInstructions(){
		return Instruction.programInstructions;
	}

	public Variable getCurrentVarName(String ident) throws ParsingException {
	    if(symbols != null) {
	        for(String var : symbols.keySet()) {
	            if(ident.equals(var)){
	                return symbols.get(ident);
	            }
	        }
	    }

	    if(glblSymbolTable != null) {
	        for (String var : glblSymbolTable.keySet()) {
	            if(ident.equals(var)) {
	                return glblSymbolTable.get(ident);
	            }
	        }
	    }

	    throw new ParsingException("variable: " + ident + " was not found to be declared.");
	}

	public BasicBlock createBasicBlock() {
		BasicBlock bb = new BasicBlock(); 
		blockMap.put(bb.blockNumber, bb);
		return bb;
	}

	public Map<String, Variable> getCurrentScopeSymbols() {
	    if(symbols != null) {
	        return symbols;
	    }
	    return glblSymbolTable;
	}

	public BasicBlock getCurrentBlock() {
		return blockStack.get(blockStack.size()-1);
	}
}