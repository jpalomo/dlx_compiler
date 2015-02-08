package compiler.components.lex;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Preconditions;

public class Scanner
{
	static Logger LOGGER = LoggerFactory.getLogger(Scanner.class);

	private FileReader fileReader;

	public Token token;
	public char inputSym = ' '; //holds the character from file reader
	public int lineNum = 0;
	public int charPos = 0;

	public Scanner(String fileName) {
		Preconditions.checkArgument(StringUtils.isNotEmpty(fileName), "the input file loction cannot be empty or null");
		fileReader = new FileReader(fileName);
		lineNum++;
	} 

	public Token nextToken() {
		while(Character.isWhitespace(inputSym)) {
			if(inputSym == (char)10){
				lineNum++;
				charPos = 0;
			}
			next();
		} 
		
		if(inputSym == FileReader.EOF) {
			LOGGER.debug("Found EOF token");
			token = Token.EOF_TOKEN;
			charPos--;  //do not want to count EOF as a character
		}
		else if(inputSym == FileReader.ERROR) {  //error 
			LOGGER.debug("Found ERROR token");
			token = Token.ERR_TOKEN;
		} 
		else if(isChar()){
			identifierOrKeyword(); 
		} 
		else if(Character.isDigit(inputSym)) {
			number();
		}
		else {
			relOpOrKeyword();
		}
		return token;
	}

	public void error(String errorMsg) {
		fileReader.error(errorMsg);
	}
	
	private void identifierOrKeyword() {
		StringBuilder lexeme = new StringBuilder();
		lexeme.append(inputSym);
		next();	//consume the character
		while(isChar() || isNumber()) {
			lexeme.append(inputSym);
			next();
		} 
        LOGGER.info("Identifier/Keyword Lexeme: " + lexeme.toString()); 
		token = Token.INDENTIFIER_OR_KEYWORD(lexeme.toString());
	}

	private boolean isChar() {
		if((inputSym >= 'a' && inputSym <= 'z') || (inputSym >= 'A' && inputSym <= 'Z')){
			return true;
		}
		return false;
	}

	private void number() {
		StringBuilder lexeme = new StringBuilder();
		lexeme.append(inputSym);
		next();	//consume the character
		while(isNumber()) {
			lexeme.append(inputSym);
			next();
		} 
        LOGGER.info("Number Lexeme: " + lexeme.toString()); 
		token = Token.NUMBER(lexeme.toString()); 
	}

	private boolean isNumber() {
		if(inputSym >= '0' && inputSym <= '9'){
			return true;
		}
		return false;
	}

	private void relOpOrKeyword() {
		StringBuilder lexeme = new StringBuilder();
		lexeme.append(inputSym);
		next();	//consume the character

		if(lexeme.toString().equalsIgnoreCase("/") && inputSym == '/') { //comment
			throwAwayLine();
			nextToken(); 
			return;
		} 
		else if(inputSym == '=' || inputSym == '-' ) {  //either relop or becomes
			lexeme.append(inputSym);
			next();
		} 
		
		token = Token.RELOP_OR_KEYWORD(lexeme.toString()); 
        LOGGER.info("RelOp or Keyword Lexeme: " + token.getLexeme()); 
	}

	/*
	 * Has the file reader return the current symbol to be consumed.
	 */
	private void next() {
		inputSym = fileReader.getSym();
		charPos++;
		LOGGER.trace("Retrieved: " + inputSym); 
	} 

	/*
	 * Read to the end of the line looking for the newline or EOR or ERROR symbols
	 */
	private void throwAwayLine() {
		do {
			next();
		}while(inputSym != '\n' && inputSym != FileReader.EOF && inputSym != FileReader.ERROR); 
		next();//toss the newline if it was there otherwise an error or EOF is present
	}
}