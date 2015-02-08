package com.palomo.pl241.test;

import java.io.File;

import org.junit.Test;

import compiler.components.parser.Parser;
import compiler.components.parser.ParsingException;

public class TestParserVCG {

	@Test
	public void parseAll() {
		File folder = new File("src/test/resources/");
		File[] listOfFiles = folder.listFiles();

		for (int i = 0; i < listOfFiles.length; i++) {
			if(listOfFiles[i].getName().startsWith(".")) continue;
			System.out.println("Testing file: " + listOfFiles[i].getName());
			if (listOfFiles[i].isFile()) {
				Parser parser = new Parser(listOfFiles[i].getPath());
				try{
					parser.parse();
				}
				catch(ParsingException pe) {
					System.err.println("Could not parse file: " + listOfFiles[i].getName());
				}
			} else if (listOfFiles[i].isDirectory()) {
				//do nothing
			}
		}
	}
	
	@Test
	public void testSimpleIF_CFG() throws ParsingException {
		Parser parser = new Parser("src/test/resources/unit_tests/if_else.txt");
		parser.parse();
		parser.printInstructions();

	}

	@Test
	public void test001() throws ParsingException {
		Parser parser = new Parser("src/test/resources/test001.txt");
		parser.parse();
		parser.printInstructions();
	}

	@Test
	public void test007() throws ParsingException {
		Parser parser = new Parser("src/test/resources/test007.txt");
		parser.parse();
		// parser.printControlFlowToFile("test007");
	}

	@Test
	/* While loop */
	public void test008() throws ParsingException {
		Parser parser = new Parser("src/test/resources/test008.txt");
		parser.parse();
		// parser.printControlFlowToFile("test008");
		parser.printInstructions();
	}

	@Test
	/* Nested if Else */
	public void test009() throws ParsingException {
		Parser parser = new Parser("src/test/resources/test009.txt");
		parser.parse();
		// parser.printControlFlowToFile("test009");
		parser.printInstructions();
	}

	@Test
	public void testArray() throws ParsingException {
		Parser parser = new Parser(
				"src/test/resources/unit_tests/test_array.txt");
		parser.parse();
		// parser.printControlFlowToFile("test_array");
	}

	@Test
	public void funcDecl() throws ParsingException {
		Parser parser = new Parser("src/test/resources/unit_tests/funcDecl.txt");
		parser.parse();
		parser.printInstructions();
	}

	@Test
	public void testIfNoElse() throws ParsingException {
		Parser parser = new Parser(
				"src/test/resources/unit_tests/if_no_else.txt");
		parser.parse();
		// parser.printControlFlowToFile("if_no_else");
		parser.printInstructions();
	}

	@Test
	public void testIfElse() throws ParsingException {
		Parser parser = new Parser("src/test/resources/unit_tests/if_else.txt");
		parser.parse();
		// parser.printControlFlowToFile("if_else");
		parser.printInstructions();
	}

	@Test
	public void testIfIf() throws ParsingException {
		Parser parser = new Parser("src/test/resources/unit_tests/if_if.txt");
		parser.parse();
		// parser.printControlFlowToFile("if_if");
		parser.printInstructions();
	}

}