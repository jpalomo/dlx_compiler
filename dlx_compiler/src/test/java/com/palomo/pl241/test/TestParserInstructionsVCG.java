package com.palomo.pl241.test;

import java.io.IOException;
import java.util.HashMap;

import org.junit.Before;
import org.junit.Test;

import compiler.components.intermediate_rep.VCGWriter;
import compiler.components.parser.Instruction;
import compiler.components.parser.Parser;
import compiler.components.parser.ParsingException;

public class TestParserInstructionsVCG {
	private static String VCG_OUTPUT_DIR = "src/test/resources/vcg/";
	private static final boolean RUN_XVCG = true;
	private static final boolean PRINT_INSTRUCTIONS = true;

	@Before
	public void setup() {
		Instruction.programInstructions = new HashMap<Integer, Instruction>();
		Instruction.PC = 1;
	}

	public void printCFG(Parser parser, String fileName){
		if(PRINT_INSTRUCTIONS){
			parser.printInstructions();
		}

		VCGWriter vcg = new VCGWriter(VCG_OUTPUT_DIR + fileName, Instruction.programInstructions);
		vcg.emitBeginBasicBlock(parser.currentBlock, true, true);
		vcg.close();
	}

	public void runXVCG(String fileName) throws IOException{
		if(RUN_XVCG){
			Runtime.getRuntime().exec("/usr/local/bin/xvcg " + VCG_OUTPUT_DIR + fileName); 
		}
	}
	
	
	@Test //passing
	public void test001() throws ParsingException, IOException{
		Parser parser = new Parser("src/test/resources/test001.txt"); 
		parser.parse();
		parser.printInstructions();
		printCFG(parser, "test001.txt.vcg");
		runXVCG("test001.txt.vcg"); 
		
	}

	@Test  //passing as of 2/9
	public void test002() throws ParsingException{
		Parser parser = new Parser("src/test/resources/test002.txt"); 
		parser.parse();
		parser.printInstructions();

	}

	@Test   //passing as of 2/9
	public void test003() throws ParsingException{
		Parser parser = new Parser("src/test/resources/test003.txt"); 
		parser.parse();
		parser.printInstructions();

	}
	
	@Test  //passing as of 2/9
	public void test004() throws ParsingException{
		Parser parser = new Parser("src/test/resources/test004.txt"); 
		parser.parse();
		parser.printInstructions();

	}
	
	@Test //passing as of 2/9
	public void test005() throws ParsingException{
		Parser parser = new Parser("src/test/resources/test005.txt"); 
		parser.parse();
		parser.printInstructions();

	}
	
	@Test  //passing as of 2/9
	public void test006() throws ParsingException, IOException{
		Parser parser = new Parser("src/test/resources/test006.txt"); 
		parser.parse();
		printCFG(parser, "test006.txt.vcg");
		runXVCG("test006.txt.vcg"); 
	}

	@Test  //passing as of 2/9
	public void test007() throws ParsingException{
		Parser parser = new Parser("src/test/resources/test007.txt"); 
		parser.parse();
		parser.printInstructions();

	}

	@Test //passsing as of 2/9 
	public void test008() throws ParsingException, IOException{
		Parser parser = new Parser("src/test/resources/test008.txt"); 
		parser.parse();
		printCFG(parser, "test008.txt.vcg");
		runXVCG("test008.txt.vcg"); 
	}

	@Test  //passing as of 2/9
	public void test009() throws ParsingException{
		Parser parser = new Parser("src/test/resources/test009.txt"); 
		parser.parse();
		parser.printInstructions();

	}

	@Test  //passing as of 2/9
	public void test010() throws ParsingException, IOException{
		Parser parser = new Parser("src/test/resources/test010.txt"); 
		parser.parse();
		printCFG(parser, "test010.txt.vcg");
		runXVCG("test010.txt.vcg");;

	}

	@Test   //passing as of 2/9
	public void test011() throws ParsingException{
		Parser parser = new Parser("src/test/resources/test011.txt"); 
		parser.parse();
		parser.printInstructions();

	}

	@Test  //passing as of 2/9
	public void test012() throws ParsingException{
		Parser parser = new Parser("src/test/resources/test012.txt"); 
		parser.parse();
		parser.printInstructions();

	}

	@Test //passing as of 2/9
	public void test014() throws ParsingException, IOException{
		Parser parser = new Parser("src/test/resources/test014.txt"); 
		parser.parse();
		printCFG(parser, "test014.txt.vcg");
		runXVCG("test014.txt.vcg");

	}

	@Test //passing as of 2/9
	public void test015() throws ParsingException{
		Parser parser = new Parser("src/test/resources/test015.txt"); 
		parser.parse();
		parser.printInstructions();

	}

	@Test
	public void test016() throws ParsingException{
		Parser parser = new Parser("src/test/resources/test016.txt"); 
		parser.parse();
		parser.printInstructions();

	}

	@Test  //passing as of 2/9
	public void test017() throws ParsingException{
		Parser parser = new Parser("src/test/resources/test017.txt"); 
		parser.parse();
		parser.printInstructions();

	}

	@Test  //passing as of 2/9
	public void test018() throws ParsingException{
		Parser parser = new Parser("src/test/resources/test018.txt"); 
		parser.parse();
		parser.printInstructions();

	}

	@Test //passing as of 2/9
	public void test019() throws ParsingException{
		Parser parser = new Parser("src/test/resources/test019.txt"); 
		parser.parse();
		parser.printInstructions();

	}

	@Test
	public void test020() throws ParsingException{
		Parser parser = new Parser("src/test/resources/test020.txt"); 
		parser.parse();
		parser.printInstructions();

	}

	@Test  //passing as of 2/9
	public void test021() throws ParsingException{
		Parser parser = new Parser("src/test/resources/test021.txt"); 
		parser.parse();
		parser.printInstructions();

	}

	@Test  //passing as of 2/9
	public void test022() throws ParsingException{
		Parser parser = new Parser("src/test/resources/test022.txt"); 
		parser.parse();
		parser.printInstructions();

	}

	@Test  //passing as of 2/9
	public void test023() throws ParsingException{
		Parser parser = new Parser("src/test/resources/test023.txt"); 
		parser.parse();
		parser.printInstructions();

	}
	
	@Test   //passing as of 2/9
	public void test024() throws ParsingException{
		Parser parser = new Parser("src/test/resources/test024.txt"); 
		parser.parse();
		parser.printInstructions();

	}
	
	@Test  //passing as of 2/9
	public void test025() throws ParsingException{
		Parser parser = new Parser("src/test/resources/test025.txt"); 
		parser.parse();
		parser.printInstructions();

	}
	
	@Test   //passing as of 2/9
	public void test026() throws ParsingException{
		Parser parser = new Parser("src/test/resources/test026.txt"); 
		parser.parse();
		parser.printInstructions();

	}
	
	@Test   //passing as of 2/9
	public void test027() throws ParsingException{
		Parser parser = new Parser("src/test/resources/test027.txt"); 
		parser.parse();
		parser.printInstructions();

	}
	
	@Test   //passing as of 2/9
	public void test028() throws ParsingException{
		Parser parser = new Parser("src/test/resources/test028.txt"); 
		parser.parse();
		parser.printInstructions();

	}
	
	@Test  //passing as of 2/9
	public void test029() throws ParsingException{
		Parser parser = new Parser("src/test/resources/test029.txt"); 
		parser.parse();
		parser.printInstructions();

	}
	
	@Test  //passing as of 2/9
	public void test030() throws ParsingException{
		Parser parser = new Parser("src/test/resources/test030.txt"); 
		parser.parse();
		parser.printInstructions();

	}
	
	@Test
	public void test031() throws ParsingException{
		Parser parser = new Parser("src/test/resources/test031.txt"); 
		parser.parse();
		parser.printInstructions();
	}


	/*****Simple Unit Tests Begin
	 * @throws IOException ********************/

	@Test
	public void testIfElse() throws ParsingException, IOException {
		Parser parser = new Parser("src/test/resources/unit_tests/if_else.txt"); 
		parser.parse(); 
		printCFG(parser, "if_else.txt.vcg");
		runXVCG("if_else.txt.vcg");
	}

	@Test
	public void testIfIf() throws ParsingException, IOException {
		Parser parser = new Parser("src/test/resources/unit_tests/if_if.txt"); 
		parser.parse(); 
		printCFG(parser, "if_if.txt.vcg");
		runXVCG("if_if.txt.vcg");
	}

	@Test
	public void testIfElseIfElseIfElse() throws ParsingException, IOException {
		Parser parser = new Parser("src/test/resources/unit_tests/if_else_if_else_else.txt"); 
		parser.parse(); 
		printCFG(parser, "if_else_if_else_else.txt.vcg");
		runXVCG("if_else_if_else_else.txt.vcg");
	}

	@Test  //passing as of 2/9
	public void testNestedWhile() throws ParsingException, IOException{
		Parser parser = new Parser("src/test/resources/unit_tests/nested_while.txt"); 
		parser.parse();
		printCFG(parser, "nested_while.txt.vcg");
		runXVCG("nested_while.txt.vcg");;

	}

	@Test
	public void testLoad1DArray() throws ParsingException {
        Parser parser = new Parser("src/test/resources/unit_tests/test_1d_array.txt"); 
        parser.parse(); 
        parser.printInstructions();
    }
	
	@Test
	public void testLoad2DArray() throws ParsingException {
        Parser parser = new Parser("src/test/resources/unit_tests/test_2d_array.txt"); 
        parser.parse(); 
        parser.printInstructions();
    }

}
