package compiler.components.optimization;

import org.apache.log4j.Logger;

import compiler.components.parser.ParsingException;

public class OptimizationException extends ParsingException {
	Logger LOGGER = Logger.getLogger(OptimizationException.class);
	private static final long serialVersionUID = 1L;

	public OptimizationException() {
		System.err.println("Syntax error. Did not recieve an expected token.");
		LOGGER.error("Syntax error. Did not recieve an expected token.");
	}

	public OptimizationException(String error) {
		System.err.println(error);
		LOGGER.error(error);
	}
}