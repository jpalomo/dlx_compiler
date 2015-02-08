package compiler.components.parser;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import compiler.components.intermeditate_rep.BasicBlock;
import compiler.components.intermeditate_rep.Result;
import compiler.components.intermeditate_rep.Result.ResultEnum;
import compiler.components.lex.Scanner;
import compiler.components.lex.Token;
import compiler.components.lex.Token.Kind;
import compiler.components.parser.Variable.VarType;

/**
 * Implementation of a top-down recursive descent parser.
 *
 * @author John Palomo, 60206611
 */
public class Parser {
	static Logger LOGGER = LoggerFactory.getLogger(Parser.class);
	public Token currentToken;

	public Map<String, Variable> glblSymbolTable = new HashMap<String, Variable>();
	public static Map<String, Function> functionSymbolTable = new HashMap<String, Function>();

	static {
		// add the predefined functions to the symbol table
		functionSymbolTable.put("InputNum", new Function("read", new ArrayList<String>()));
		functionSymbolTable.put("OutputNum", new Function("write", new ArrayList<String>()));
		functionSymbolTable.put("OutputNewLine", new Function("wln", new ArrayList<String>()));
	}

	private Scanner scanner;

	public Parser(String fileName) {
		scanner = new Scanner(fileName);
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
				statSequence();
				expect(Kind.END);
			} else {
				throw new ParsingException();
			}
		}
		expect(Kind.PERIOD);
		Instruction.endProgram();
		return;
	}

	/** varDecl = typeDecl ident { ',' ident } ';' */
	private void varDecl(Map<String, Variable> symbolTable) throws ParsingException {

		List<Integer> arrayDims = typeDecl(); 
		String ident = ident();

		VarType declType;
		Variable variable;
		if(arrayDims.size() > 1) {
			declType = VarType.ARRAY;
			variable = new Variable(ident, arrayDims, VarType.ARRAY);
		}
		else {
			declType = VarType.VAR;
			variable = new Variable(ident, arrayDims, VarType.VAR, 0);
		}

		addVarToSymbolTable(symbolTable, variable);

		while (accept(Kind.COMMA)) {
			eatToken(); // eat the comma
			ident = ident();
			if(declType.equals(VarType.VAR)) {
				variable = new Variable(ident, arrayDims, declType, 0);
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

		Function function = new Function(funcName, formalParams);
		addFunctionToFunctionTable(function);
		
		expect(Kind.SEMI_COL);

		funcBody();

		expect(Kind.SEMI_COL); 
	}

	/** formalParam = '(' [ident { ',' ident }] ')' */
	private List<String> formalParam() throws ParsingException {
		expect(Kind.OPN_PAREN);

		List<String> params = new ArrayList<String>();
		
		if (accept(Kind.IDENTIFIER)) {
			ident();
			while (accept(Kind.COMMA)) {
				eatToken(); // eat the comma
				ident();
			}
		}

		expect(Kind.CLS_PAREN);
		return params;
	}

	/** funcBody = { varDecl } '{' [statSequence] '}' */
	private void funcBody() throws ParsingException {
		//initialize a local symbol table with the globals in it
		Map<String, Variable> localSymbolsTable = new HashMap<String, Variable>(glblSymbolTable);

		while (accept(Kind.VAR) || accept(Kind.ARRAY)) {
			varDecl(localSymbolsTable);
		} 

		expect(Kind.BEGIN);

		if (accept(Kind.LET) || accept(Kind.CALL) || accept(Kind.IF) || accept(Kind.WHILE) || accept(Kind.RETURN)) {
			statSequence();
		}

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
				number();
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

		String ident = designator();
		Result designator = new Result(ResultEnum.VARIABLE);
		designator.varValue = ident;

		expect(Kind.BECOMES);

		Result exprResult = expression();

		designator = Instruction.emitAssignmentInstruction(designator, exprResult);
		return designator;
	}

	/** funcCall = 'call' ident [ '(' [expression { ',' expression } ] ')' ] */
	private void funcCall() throws ParsingException {
		expect(Kind.CALL);

		String funcIdent = ident();
		
		Result funcParams = null;

		if (accept(Kind.OPN_PAREN)) {
			eatToken(); // eat the open paren
			funcParams = expression();

			while (accept(Kind.COMMA)) {
				eatToken(); // eat the comma
				funcParams = expression();
			}
			expect(Kind.CLS_PAREN);
		}

		Function function = functionSymbolTable.get(funcIdent);

		Instruction.createFunctionCall(function, funcParams);

	}

	/** ifStatement = 'if' relation 'then' statSequence [ 'else' statSequence ] 'fi' */
	private Result ifStatement() throws ParsingException {
		expect(Kind.IF);
		
		Result follow = new Result();
		follow.fixUp = 0;

		Result relation = relation();
		Instruction.createConditionalJumpFwd(relation);  //will set relations fixup to PC -1

		expect(Kind.THEN); 

		statSequence(); // parse 'then' block

		if (accept(Kind.ELSE)) {

			eatToken(); // eat the else
			Instruction.createFwdJumpLink(follow);
			Instruction.fixUp(relation.fixUp);

			statSequence();
		} else {
			Instruction.fixUp(relation.fixUp);
		}
		Instruction.fixLink(follow);
		
		expect(Kind.FI);

		return relation;
	}

	/** whileStatement = 'while' relation 'do' statSequence 'od' */
	private Result whileStatement() throws ParsingException {
		expect(Kind.WHILE);
		Result relation = relation();
		expect(Kind.DO);
		statSequence();
		expect(Kind.OD);

		return null;
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
	private String designator() throws ParsingException {
		String x = ident();
		while (accept(Kind.OPN_BRACK)) {
			eatToken(); // eat the open bracket
			expression();
			expect(Kind.CLS_BRACK);
		}
		return x;
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

		Result result;
  		if (accept(Kind.IDENTIFIER)) {
			String x = designator();
			result = new Result(ResultEnum.VARIABLE);
			result.varValue = x;
			return result;
		} else if (accept(Kind.NUMBER)) {
			int x = number();
			result = new Result(ResultEnum.CONSTANT);
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
			 funcCall();
			 return null;  //TODO handle function calls
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

	private void addDomInfo(BasicBlock dominator, BasicBlock dominatee) {
		dominator.addDominatee(dominatee);
		dominatee.addDominator(dominator);
	}

	private void addVarToSymbolTable(Map<String, Variable> symbolTable, Variable varToAdd) throws ParsingException{
		if(symbolTable.containsKey(varToAdd.getVarIdentifier())){
			throw new ParsingException("symbol re-declared:" + varToAdd.getVarIdentifier() + " on line number : " + getLineNum());
		}
		symbolTable.put(varToAdd.getVarIdentifier(), varToAdd); 
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
}