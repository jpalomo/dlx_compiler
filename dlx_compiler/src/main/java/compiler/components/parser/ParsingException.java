package compiler.components.parser;

import org.apache.log4j.Logger;

import compiler.components.lex.Token;
import compiler.components.lex.Token.Kind;

public class ParsingException extends Exception {
	Logger LOGGER = Logger.getLogger(ParsingException.class);
	private static final long serialVersionUID = 1L;

	public ParsingException() {
		System.err.println("Syntax error. Did not recieve an expected token.");
		LOGGER.error("Syntax error. Did not recieve an expected token.");
	}

	public ParsingException(Kind expected, Token currentToken) {
		System.err.println("Syntax error.  Expected: " + expected.name()
				+ " but recieved " + currentToken.kind.name());
		LOGGER.error("Syntax error.  Expected: " + expected.name()
				+ " but recieved " + currentToken.kind.name());
		System.exit(1);
	}

	public ParsingException(String error) {
		System.err.println(error);
		LOGGER.error(error);
	}
}