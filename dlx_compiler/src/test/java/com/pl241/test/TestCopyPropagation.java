package com.pl241.test;

import java.io.IOException;
import java.util.HashMap;

import org.junit.Before;
import org.junit.Test;

import compiler.components.intermediate_rep.VCGWriter;
import compiler.components.optimization.CopyPropagation;
import compiler.components.parser.Function;
import compiler.components.parser.Instruction;
import compiler.components.parser.Parser;
import compiler.components.parser.ParsingException;

public class TestCopyPropagation {
	private static String VCG_OUTPUT_DIR = "src/test/resources/opvcg/";
	private static final boolean RUN_XVCG = true;
	private static final boolean PRINT_INSTRUCTIONS = false;

	@Before
	public void setup() {
		Instruction.programInstructions = new HashMap<Integer, Instruction>();
		Instruction.PC = 1;
	}

	public void printDom(Parser parser, String fileName){
		if(PRINT_INSTRUCTIONS){
			parser.printInstructions();
		}

		VCGWriter vcg = new VCGWriter(VCG_OUTPUT_DIR + fileName, Instruction.programInstructions);
		vcg.emitDominatorGraph(parser.currentBlock);
		for(Function f : parser.functionList) {
			if(f.hasBlocks) { 
				vcg.emitDominatorGraph(f.beginBlockForFunction);
			}
		}
		vcg.close();
	}

	public void runXVCG(String fileName) throws IOException{
		if(RUN_XVCG){
			Runtime.getRuntime().exec("/usr/local/bin/xvcg " + VCG_OUTPUT_DIR + fileName); 
		}
	}
	
	@Test //passing with cfg
	public void test001() throws ParsingException, IOException{
		Parser parser = new Parser("src/test/resources/test001.txt"); 
		parser.parse();
		parser.printInstructions();
		CopyPropagation propagator = new CopyPropagation(parser, parser.getProgramInstructions());
		propagator.printTable();
		printDom(parser, "test001.txt.vcg");
		runXVCG("test001.txt.vcg"); 
	}

	@Test  
	public void test002() throws ParsingException, IOException{
		Parser parser = new Parser("src/test/resources/test002.txt");
		parser.parse();
		parser.printInstructions();
		CopyPropagation propagator = new CopyPropagation(parser, parser.getProgramInstructions());
		propagator.printTable();
		printDom(parser, "test002.txt.vcg");
		runXVCG("test002.txt.vcg");
	}

	@Test   
	public void test003() throws ParsingException, IOException{
		Parser parser = new Parser("src/test/resources/test003.txt"); 
		parser.parse();
		parser.printInstructions();
		CopyPropagation propagator = new CopyPropagation(parser, parser.getProgramInstructions());
		propagator.printTable();
		printDom(parser, "test003.txt.vcg");
		runXVCG("test003.txt.vcg");
	}
	
	@Test  
	public void test004() throws ParsingException, IOException{
		Parser parser = new Parser("src/test/resources/test004.txt");
		parser.parse();
		parser.printInstructions();
		CopyPropagation propagator = new CopyPropagation(parser, parser.getProgramInstructions());
		propagator.printTable();
		printDom(parser, "test004.txt.vcg");
		runXVCG("test004.txt.vcg");
	}
	
	@Test //passing with cfg
	public void test005() throws ParsingException, IOException{
		Parser parser = new Parser("src/test/resources/test005.txt");
		parser.parse();
		parser.printInstructions();
		CopyPropagation propagator = new CopyPropagation(parser, parser.getProgramInstructions());
		propagator.printTable();
		printDom(parser, "test005.txt.vcg");
		runXVCG("test005.txt.vcg");

	}
	
	@Test  //passing with cfg
	public void test006() throws ParsingException, IOException{
		Parser parser = new Parser("src/test/resources/test006.txt");
		parser.parse();
		parser.printInstructions();
		CopyPropagation propagator = new CopyPropagation(parser, parser.getProgramInstructions());
		propagator.printTable();
		printDom(parser, "test006.txt.vcg");
		runXVCG("test006.txt.vcg");
	}

	@Test //passing with cfg 
	public void test007() throws ParsingException, IOException{
		Parser parser = new Parser("src/test/resources/test007.txt"); 
		parser.parse();
		parser.printInstructions();
		CopyPropagation propagator = new CopyPropagation(parser, parser.getProgramInstructions());
		propagator.printTable();
		printDom(parser, "test007.txt.vcg");
		runXVCG("test007.txt.vcg");
	}

	@Test  //TODO simple while test not working
	public void test008() throws ParsingException, IOException{
		Parser parser = new Parser("src/test/resources/test008.txt"); 
		parser.parse();
		parser.printInstructions();
		CopyPropagation propagator = new CopyPropagation(parser, parser.getProgramInstructions());
		propagator.printTable();
		printDom(parser, "test008.txt.vcg");
		runXVCG("test008.txt.vcg");
	}

	@Test //TODO ifs are generating empty blocks 
	public void test009() throws ParsingException, IOException{
		Parser parser = new Parser("src/test/resources/test009.txt"); 
		parser.parse();
		parser.printInstructions();
		CopyPropagation propagator = new CopyPropagation(parser, parser.getProgramInstructions());
		propagator.printTable();
		printDom(parser, "test009.txt.vcg");
		runXVCG("test009.txt.vcg");
	}

	@Test  //passing with cfg
	public void test010() throws ParsingException, IOException{
		Parser parser = new Parser("src/test/resources/test010.txt"); 
		parser.parse();
		parser.printInstructions();
		CopyPropagation propagator = new CopyPropagation(parser, parser.getProgramInstructions());
		propagator.printTable();
		printDom(parser, "test010.txt.vcg");
		runXVCG("test010.txt.vcg");

	}

	@Test   
	public void test011() throws ParsingException, IOException{
		Parser parser = new Parser("src/test/resources/test011.txt"); 
		parser.parse();
		parser.printInstructions();
		CopyPropagation propagator = new CopyPropagation(parser, parser.getProgramInstructions());
		propagator.printTable();
		printDom(parser, "test011.txt.vcg");
		runXVCG("test011.txt.vcg");
	}

	@Test //passing with cfg 
	public void test012() throws ParsingException, IOException{
		Parser parser = new Parser("src/test/resources/test012.txt"); 
		parser.parse();
		parser.printInstructions();
		CopyPropagation propagator = new CopyPropagation(parser, parser.getProgramInstructions());
		propagator.printTable();
		printDom(parser, "test012.txt.vcg");
		runXVCG("test012.txt.vcg");
	}

	@Test 
	public void test013() throws ParsingException, IOException{
		Parser parser = new Parser("src/test/resources/test013.txt"); 
		parser.parse();
		parser.printInstructions();
		CopyPropagation propagator = new CopyPropagation(parser, parser.getProgramInstructions());
		propagator.printTable();
		printDom(parser, "test013.txt.vcg");
		runXVCG("test013.txt.vcg");
	}

	@Test 
	public void test014() throws ParsingException, IOException{
		Parser parser = new Parser("src/test/resources/test014.txt"); 
		parser.parse();
		parser.printInstructions();
		CopyPropagation propagator = new CopyPropagation(parser, parser.getProgramInstructions());
		propagator.printTable();
		printDom(parser, "test014.txt.vcg");
		runXVCG("test014.txt.vcg");
	}

	@Test 
	public void test015() throws ParsingException, IOException{
		Parser parser = new Parser("src/test/resources/test015.txt"); 
		parser.parse();
		parser.printInstructions();
		CopyPropagation propagator = new CopyPropagation(parser, parser.getProgramInstructions());
		propagator.printTable();
		printDom(parser, "test015.txt.vcg");
		runXVCG("test015.txt.vcg");
	}

	@Test
	public void test016() throws ParsingException, IOException{
		Parser parser = new Parser("src/test/resources/test016.txt"); 
		parser.parse();
		parser.printInstructions();
		CopyPropagation propagator = new CopyPropagation(parser, parser.getProgramInstructions());
		propagator.printTable();
		printDom(parser, "test016.txt.vcg");
		runXVCG("test016.txt.vcg");
	}

	@Test  
	public void test017() throws ParsingException, IOException{
		Parser parser = new Parser("src/test/resources/test017.txt"); 
		parser.parse();
		CopyPropagation propagator = new CopyPropagation(parser, parser.getProgramInstructions());
		propagator.printTable();
		printDom(parser, "test017.txt.vcg");
		runXVCG("test017.txt.vcg");
	}

	@Test  
	public void test018() throws ParsingException, IOException{
		Parser parser = new Parser("src/test/resources/test018.txt"); 
		parser.parse();
		parser.printInstructions();
		CopyPropagation propagator = new CopyPropagation(parser, parser.getProgramInstructions());
		propagator.printTable();
		printDom(parser, "test018.txt.vcg");
		runXVCG("test018.txt.vcg");
	}

	@Test 
	public void test019() throws ParsingException, IOException{
		Parser parser = new Parser("src/test/resources/test019.txt"); 
		parser.parse();
		parser.printInstructions();
		CopyPropagation propagator = new CopyPropagation(parser, parser.getProgramInstructions());
		propagator.printTable();
		printDom(parser, "test019.txt.vcg");
		runXVCG("test019.txt.vcg");
	}

	@Test
	public void test020() throws ParsingException, IOException {
		Parser parser = new Parser("src/test/resources/test020.txt");
		parser.parse();
		parser.printInstructions();
		CopyPropagation propagator = new CopyPropagation(parser, parser.getProgramInstructions());
		propagator.printTable();
		printDom(parser, "test020.txt.vcg");
		runXVCG("test020.txt.vcg");
	}

	@Test  
	public void test021() throws ParsingException, IOException{
		Parser parser = new Parser("src/test/resources/test021.txt"); 
		parser.parse();
		parser.printInstructions();
		CopyPropagation propagator = new CopyPropagation(parser, parser.getProgramInstructions());
		propagator.printTable();
		printDom(parser, "test021.txt.vcg");
		runXVCG("test021.txt.vcg");
	}

	@Test  
	public void test022() throws ParsingException, IOException{
		Parser parser = new Parser("src/test/resources/test022.txt"); 
		parser.parse();
		parser.printInstructions();
		CopyPropagation propagator = new CopyPropagation(parser, parser.getProgramInstructions());
		propagator.printTable();
		printDom(parser, "test022.txt.vcg");
		runXVCG("test022.txt.vcg");
	}

	@Test  
	public void test023() throws ParsingException, IOException{
		Parser parser = new Parser("src/test/resources/test023.txt"); 
		parser.parse();
		parser.printInstructions();
		CopyPropagation propagator = new CopyPropagation(parser, parser.getProgramInstructions());
		propagator.printTable();
		printDom(parser, "test023.txt.vcg");
		runXVCG("test023.txt.vcg");
	}
	
	@Test   
	public void test024() throws ParsingException, IOException{
		Parser parser = new Parser("src/test/resources/test024.txt"); 
		parser.parse();
		parser.printInstructions();
		CopyPropagation propagator = new CopyPropagation(parser, parser.getProgramInstructions());
		propagator.printTable();
		printDom(parser, "test024.txt.vcg");
		runXVCG("test024.txt.vcg");
	}
	
	@Test  
	public void test025() throws ParsingException, IOException{
		Parser parser = new Parser("src/test/resources/test025.txt"); 
		parser.parse();
		parser.printInstructions();
		CopyPropagation propagator = new CopyPropagation(parser, parser.getProgramInstructions());
		propagator.printTable();
		printDom(parser, "test025.txt.vcg");
		runXVCG("test025.txt.vcg");
	}
	
	@Test   
	public void test026() throws ParsingException, IOException{
		Parser parser = new Parser("src/test/resources/test026.txt"); 
		parser.parse();
		parser.printInstructions();
		CopyPropagation propagator = new CopyPropagation(parser, parser.getProgramInstructions());
		propagator.printTable();
		printDom(parser, "test026.txt.vcg");
		runXVCG("test026.txt.vcg");
	}
	
	@Test //TODO Null pointer exception being thrown in the if statement  
	public void test027() throws ParsingException, IOException{
		Parser parser = new Parser("src/test/resources/test027.txt"); 
		parser.parse();
		parser.printInstructions();
		CopyPropagation propagator = new CopyPropagation(parser, parser.getProgramInstructions());
		propagator.printTable();
		printDom(parser, "test027.txt.vcg");
		runXVCG("test027.txt.vcg");
	}
	
	@Test   
	public void test028() throws ParsingException, IOException{
		Parser parser = new Parser("src/test/resources/test028.txt"); 
		parser.parse();
		parser.printInstructions();
		CopyPropagation propagator = new CopyPropagation(parser, parser.getProgramInstructions());
		propagator.printTable();
		printDom(parser, "test028.txt.vcg");
		runXVCG("test028.txt.vcg");
	}
	
	@Test  
	public void test029() throws ParsingException, IOException{
		Parser parser = new Parser("src/test/resources/test029.txt"); 
		parser.parse();
		parser.printInstructions();
		CopyPropagation propagator = new CopyPropagation(parser, parser.getProgramInstructions());
		propagator.printTable();
		printDom(parser, "test029.txt.vcg");
		runXVCG("test029.txt.vcg");
	}
	
	@Test  
	public void test030() throws ParsingException, IOException{
		Parser parser = new Parser("src/test/resources/test030.txt"); 
		parser.parse();
		parser.printInstructions();
		CopyPropagation propagator = new CopyPropagation(parser, parser.getProgramInstructions());
		propagator.printTable();
		printDom(parser, "test030.txt.vcg");
		runXVCG("test030.txt.vcg");
	}
	
	@Test
	public void test031() throws ParsingException, IOException{
		Parser parser = new Parser("src/test/resources/test031.txt"); 
		parser.parse();
		parser.printInstructions();
		CopyPropagation propagator = new CopyPropagation(parser, parser.getProgramInstructions());
		propagator.printTable();
		printDom(parser, "test031.txt.vcg");
		runXVCG("test031.txt.vcg");
	}
	
	@Test  
	public void arrayIfElse() throws ParsingException, IOException{
		Parser parser = new Parser("src/test/resources/unit_tests/array_if_else.txt"); 
		parser.parse();
		CopyPropagation propagator = new CopyPropagation(parser, parser.getProgramInstructions());
		propagator.printTable();
		printDom(parser, "array_if_else.txt.vcg");
		runXVCG("array_if_else.txt.vcg");
	}

}
