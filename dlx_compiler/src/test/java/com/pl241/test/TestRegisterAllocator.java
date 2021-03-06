package com.pl241.test;

import java.io.IOException;
import java.util.HashMap;

import org.junit.Before;
import org.junit.Test;

import compiler.components.intermediate_rep.InterferenceGraphWriter;
import compiler.components.intermediate_rep.VCGWriter;
import compiler.components.optimization.Optimizer;
import compiler.components.parser.Function;
import compiler.components.parser.Instruction;
import compiler.components.parser.Parser;
import compiler.components.parser.ParsingException;
import compiler.components.register.InterferenceGraph;
import compiler.components.register.RegisterAllocator;

public class TestRegisterAllocator {
	private static String VCG_OUTPUT_DIR = "src/test/resources/reg/";
	private static final boolean RUN_XVCG = true;
	private static final boolean PRINT_INSTRUCTIONS = false;
	private static final boolean PRINT_DOM = true;
	private static final boolean PRINT_CFG = true;


	@Before
	public void setup() {
		Instruction.programInstructions = new HashMap<Integer, Instruction>();
		Instruction.PC = 1;
	}

	public void print(Parser parser, String fileName) throws IOException{
		if(PRINT_INSTRUCTIONS){
			parser.printInstructions();
		}

		if(PRINT_DOM) {
			String fullFileName = VCG_OUTPUT_DIR + "dom_" + fileName;
			VCGWriter vcg = new VCGWriter(fullFileName , Instruction.programInstructions);
			vcg.emitDominatorGraph(parser.root);
			for(Function f : parser.functionList) {
				if(f.hasBlocks) { 
					vcg.emitDominatorGraph(f.beginBlockForFunction);
				}
			}
			vcg.close();
			
			runXVCG(fullFileName);
		}

		if(PRINT_CFG) {
			String fullFileName = VCG_OUTPUT_DIR + "cfg_" + fileName;
			VCGWriter vcg = new VCGWriter(fullFileName, Instruction.programInstructions);
			vcg.emitControlFlowGraph(parser.root);
			vcg.close();

			runXVCG(fullFileName);
		}
	}

	private void printInterferenceGraph(String fileName, InterferenceGraph ig) throws IOException {
		if(PRINT_CFG) {
			String fullFileName = VCG_OUTPUT_DIR + "int_" + fileName;
			InterferenceGraphWriter vcg = new InterferenceGraphWriter(fullFileName, Instruction.programInstructions);
			vcg.emitInterferenceGraph(ig);
			vcg.close();

			runXVCG(fullFileName);
		}
	}
	
	public void runXVCG(String fileName) throws IOException{
		if(RUN_XVCG){
			Runtime.getRuntime().exec("/usr/local/bin/xvcg " + fileName); 
		}
	}
	
	@Test //passing with cfg
	public void test001() throws ParsingException, IOException{
		Parser parser = new Parser("src/test/resources/test001.txt"); 
		parser.parse();
		Optimizer optimizer = new Optimizer(parser, parser.getPhiInstructionNumbers(), parser.getProgramInstructions());;
		optimizer.optimize(true, true);
		RegisterAllocator regAlloc = new RegisterAllocator(Instruction.programInstructions, Instruction.phiInstructionNumbers, parser.blockMap, parser.blockStack);
		regAlloc.buildGraphAndAllocate(parser.root);
		//print(parser, "test001.txt.vcg");
		printInterferenceGraph("test001.txt.vcg", regAlloc.IGraph);
		//runXVCG("test001.txt.vcg"); 
	}

	@Test  
	public void test002() throws ParsingException, IOException{
		Parser parser = new Parser("src/test/resources/test002.txt"); 
		parser.parse();
		Optimizer optimizer = new Optimizer(parser, parser.getPhiInstructionNumbers(), parser.getProgramInstructions());;
		optimizer.optimize(true, true);
		RegisterAllocator regAlloc = new RegisterAllocator(Instruction.programInstructions, Instruction.phiInstructionNumbers, parser.blockMap, parser.blockStack);
		regAlloc.buildGraphAndAllocate(parser.root);
		//print(parser, "test002.txt.vcg");
		printInterferenceGraph("test002.txt.vcg", regAlloc.IGraph);
		//runXVCG("test002.txt.vcg"); 
	}

	@Test   
	public void test003() throws ParsingException, IOException{
		Parser parser = new Parser("src/test/resources/test003.txt"); 
		parser.parse();
		Optimizer optimizer = new Optimizer(parser, parser.getPhiInstructionNumbers(), parser.getProgramInstructions());;
		optimizer.optimize(true, true);
		RegisterAllocator regAlloc = new RegisterAllocator(Instruction.programInstructions, Instruction.phiInstructionNumbers, parser.blockMap, parser.blockStack);
		regAlloc.buildGraphAndAllocate(parser.root); 
		//print(parser, "test003.txt.vcg");
		printInterferenceGraph("test003.txt.vcg", regAlloc.IGraph);
		//runXVCG("test003.txt.vcg"); 
	}
	
	@Test  
	public void test004() throws ParsingException, IOException{
		Parser parser = new Parser("src/test/resources/test004.txt"); 
		parser.parse();
		Optimizer optimizer = new Optimizer(parser, parser.getPhiInstructionNumbers(), parser.getProgramInstructions());;
		optimizer.optimize(true, true);
		RegisterAllocator regAlloc = new RegisterAllocator(Instruction.programInstructions, Instruction.phiInstructionNumbers, parser.blockMap, parser.blockStack);
		regAlloc.buildGraphAndAllocate(parser.root); 
		//print(parser, "test004.txt.vcg");
		printInterferenceGraph("test004.txt.vcg", regAlloc.IGraph);
		//runXVCG("test004.txt.vcg"); 
	}
	
	@Test //passing with cfg
	public void test005() throws ParsingException, IOException{
		Parser parser = new Parser("src/test/resources/test005.txt"); 
		parser.parse();
		Optimizer optimizer = new Optimizer(parser, parser.getPhiInstructionNumbers(), parser.getProgramInstructions());;
		optimizer.optimize(true, true);
		RegisterAllocator regAlloc = new RegisterAllocator(Instruction.programInstructions, Instruction.phiInstructionNumbers, parser.blockMap, parser.blockStack);
		regAlloc.buildGraphAndAllocate(parser.root);
		//print(parser, "test005.txt.vcg");
		printInterferenceGraph("test005.txt.vcg", regAlloc.IGraph);
		//runXVCG("test005.txt.vcg"); 

	}
	
	@Test  //passing with cfg
	public void test006() throws ParsingException, IOException{
		Parser parser = new Parser("src/test/resources/test006.txt"); 
		parser.parse();
		Optimizer optimizer = new Optimizer(parser, parser.getPhiInstructionNumbers(), parser.getProgramInstructions());;
		optimizer.optimize(true, true);
		RegisterAllocator regAlloc = new RegisterAllocator(Instruction.programInstructions, Instruction.phiInstructionNumbers, parser.blockMap, parser.blockStack);
		regAlloc.buildGraphAndAllocate(parser.root);
		//print(parser, "test006.txt.vcg");
		printInterferenceGraph("test006.txt.vcg", regAlloc.IGraph);
		//runXVCG("test006.txt.vcg"); 
	}

	@Test //passing with cfg 
	public void test007() throws ParsingException, IOException{
		Parser parser = new Parser("src/test/resources/test007.txt"); 
		parser.parse();
		Optimizer optimizer = new Optimizer(parser, parser.getPhiInstructionNumbers(), parser.getProgramInstructions());;
		optimizer.optimize(true, true);
		RegisterAllocator regAlloc = new RegisterAllocator(Instruction.programInstructions, Instruction.phiInstructionNumbers, parser.blockMap, parser.blockStack);
		regAlloc.buildGraphAndAllocate(parser.root);
		//print(parser, "test007.txt.vcg");
		printInterferenceGraph("test007.txt.vcg", regAlloc.IGraph);
		//runXVCG("test007.txt.vcg"); 
	}

	@Test  //TODO simple while test not working
	public void test008() throws ParsingException, IOException{
		Parser parser = new Parser("src/test/resources/test008.txt"); 
		parser.parse();
		Optimizer optimizer = new Optimizer(parser, parser.getPhiInstructionNumbers(), parser.getProgramInstructions());;
		optimizer.optimize(true, true);
		RegisterAllocator regAlloc = new RegisterAllocator(Instruction.programInstructions, Instruction.phiInstructionNumbers, parser.blockMap, parser.blockStack);
		regAlloc.buildGraphAndAllocate(parser.root);
		print(parser, "test008.txt.vcg");
		printInterferenceGraph("test008.txt.vcg", regAlloc.IGraph);
		runXVCG("test008.txt.vcg"); 
	}

	@Test //TODO ifs are generating empty blocks 
	public void test009() throws ParsingException, IOException{
		Parser parser = new Parser("src/test/resources/test009.txt"); 
		parser.parse();
		Optimizer optimizer = new Optimizer(parser, parser.getPhiInstructionNumbers(), parser.getProgramInstructions());;
		optimizer.optimize(true, true);
		RegisterAllocator regAlloc = new RegisterAllocator(Instruction.programInstructions, Instruction.phiInstructionNumbers, parser.blockMap, parser.blockStack);
		regAlloc.buildGraphAndAllocate(parser.root);
		//print(parser, "test009.txt.vcg");
		printInterferenceGraph("test009.txt.vcg", regAlloc.IGraph);
		//runXVCG("test009.txt.vcg"); 
	}

	@Test  //passing with cfg
	public void test010() throws ParsingException, IOException{
		Parser parser = new Parser("src/test/resources/test010.txt"); 
		parser.parse();
		Optimizer optimizer = new Optimizer(parser, parser.getPhiInstructionNumbers(), parser.getProgramInstructions());;
		optimizer.optimize(true, true);
		RegisterAllocator regAlloc = new RegisterAllocator(Instruction.programInstructions, Instruction.phiInstructionNumbers, parser.blockMap, parser.blockStack);
		regAlloc.buildGraphAndAllocate(parser.root);
		print(parser, "test010.txt.vcg");
		printInterferenceGraph("test010.txt.vcg", regAlloc.IGraph);
		runXVCG("test010.txt.vcg"); 

	}

	@Test   
	public void test011() throws ParsingException, IOException{
		Parser parser = new Parser("src/test/resources/test011.txt"); 
		parser.parse();
		Optimizer optimizer = new Optimizer(parser, parser.getPhiInstructionNumbers(), parser.getProgramInstructions());;
		optimizer.optimize(true, true);
		RegisterAllocator regAlloc = new RegisterAllocator(Instruction.programInstructions, Instruction.phiInstructionNumbers, parser.blockMap, parser.blockStack);
		regAlloc.buildGraphAndAllocate(parser.root);
		//print(parser, "test011.txt.vcg");
		printInterferenceGraph("test011.txt.vcg", regAlloc.IGraph);
		//runXVCG("test011.txt.vcg");  
	}

	@Test //passing with cfg 
	public void test012() throws ParsingException, IOException{
		Parser parser = new Parser("src/test/resources/test012.txt"); 
		parser.parse();
		Optimizer optimizer = new Optimizer(parser, parser.getPhiInstructionNumbers(), parser.getProgramInstructions());;
		optimizer.optimize(true, true);
		RegisterAllocator regAlloc = new RegisterAllocator(Instruction.programInstructions, Instruction.phiInstructionNumbers, parser.blockMap, parser.blockStack);
		regAlloc.buildGraphAndAllocate(parser.root);
		//print(parser, "test012.txt.vcg");
		printInterferenceGraph("test012.txt.vcg", regAlloc.IGraph);
		//runXVCG("test012.txt.vcg"); 
	}

	@Test 
	public void test013() throws ParsingException, IOException{
		Parser parser = new Parser("src/test/resources/test013.txt"); 
		parser.parse();
		Optimizer optimizer = new Optimizer(parser, parser.getPhiInstructionNumbers(), parser.getProgramInstructions());;
		optimizer.optimize(true, true);
		RegisterAllocator regAlloc = new RegisterAllocator(Instruction.programInstructions, Instruction.phiInstructionNumbers, parser.blockMap, parser.blockStack);
		regAlloc.buildGraphAndAllocate(parser.root);
		//print(parser, "test013.txt.vcg");
		printInterferenceGraph("test013.txt.vcg", regAlloc.IGraph);
		//runXVCG("test013.txt.vcg"); 
	}

	@Test 
	public void test014() throws ParsingException, IOException{
		Parser parser = new Parser("src/test/resources/test014.txt"); 
		parser.parse();
		Optimizer optimizer = new Optimizer(parser, parser.getPhiInstructionNumbers(), parser.getProgramInstructions());;
		optimizer.optimize(true, true);
		RegisterAllocator regAlloc = new RegisterAllocator(Instruction.programInstructions, Instruction.phiInstructionNumbers, parser.blockMap, parser.blockStack);
		regAlloc.buildGraphAndAllocate(parser.root);
		//print(parser, "test014.txt.vcg");
		printInterferenceGraph("test014.txt.vcg", regAlloc.IGraph);
		//runXVCG("test014.txt.vcg"); 
	}

	@Test 
	public void test015() throws ParsingException, IOException{
		Parser parser = new Parser("src/test/resources/test015.txt"); 
		parser.parse();
		RegisterAllocator regAlloc = new RegisterAllocator(Instruction.programInstructions, Instruction.phiInstructionNumbers, parser.blockMap, parser.blockStack);
		regAlloc.buildGraphAndAllocate(parser.root);
		//print(parser, "test015.txt.vcg");
		printInterferenceGraph("test015.txt.vcg", regAlloc.IGraph);
		//runXVCG("test015.txt.vcg"); 
	}

	@Test
	public void test016() throws ParsingException, IOException{
		Parser parser = new Parser("src/test/resources/test016.txt"); 
		parser.parse();
		Optimizer optimizer = new Optimizer(parser, parser.getPhiInstructionNumbers(), parser.getProgramInstructions());;
		optimizer.optimize(true, true);
		RegisterAllocator regAlloc = new RegisterAllocator(Instruction.programInstructions, Instruction.phiInstructionNumbers, parser.blockMap, parser.blockStack);
		regAlloc.buildGraphAndAllocate(parser.root);
		//print(parser, "test016.txt.vcg");
		printInterferenceGraph("test016.txt.vcg", regAlloc.IGraph);
		//runXVCG("test016.txt.vcg"); 
	}

	@Test  
	public void test017() throws ParsingException, IOException{
		Parser parser = new Parser("src/test/resources/test017.txt"); 
		parser.parse();
		Optimizer optimizer = new Optimizer(parser, parser.getPhiInstructionNumbers(), parser.getProgramInstructions());;
		optimizer.optimize(true, true);
		RegisterAllocator regAlloc = new RegisterAllocator(Instruction.programInstructions, Instruction.phiInstructionNumbers, parser.blockMap, parser.blockStack);
		regAlloc.buildGraphAndAllocate(parser.root);
		print(parser, "test017.txt.vcg");
		printInterferenceGraph("test017.txt.vcg", regAlloc.IGraph);
		runXVCG("test017.txt.vcg"); 
	}

	@Test  
	public void test018() throws ParsingException, IOException{
		Parser parser = new Parser("src/test/resources/test018.txt"); 
		parser.parse();
		Optimizer optimizer = new Optimizer(parser, parser.getPhiInstructionNumbers(), parser.getProgramInstructions());;
		optimizer.optimize(true, true);
		RegisterAllocator regAlloc = new RegisterAllocator(Instruction.programInstructions, Instruction.phiInstructionNumbers, parser.blockMap, parser.blockStack);
		regAlloc.buildGraphAndAllocate(parser.root);
		//print(parser, "test018.txt.vcg");
		printInterferenceGraph("test018.txt.vcg", regAlloc.IGraph);
		//runXVCG("test018.txt.vcg"); 
	}

	@Test 
	public void test019() throws ParsingException, IOException{
		Parser parser = new Parser("src/test/resources/test019.txt"); 
		parser.parse();
		Optimizer optimizer = new Optimizer(parser, parser.getPhiInstructionNumbers(), parser.getProgramInstructions());;
		optimizer.optimize(true, true);
		RegisterAllocator regAlloc = new RegisterAllocator(Instruction.programInstructions, Instruction.phiInstructionNumbers, parser.blockMap, parser.blockStack);
		regAlloc.buildGraphAndAllocate(parser.root);
		//print(parser, "test019.txt.vcg");
		printInterferenceGraph("test019.txt.vcg", regAlloc.IGraph);
		//runXVCG("test019.txt.vcg"); 
	}

	@Test
	public void test020() throws ParsingException, IOException{
		Parser parser = new Parser("src/test/resources/test020.txt"); 
		parser.parse();
		Optimizer optimizer = new Optimizer(parser, parser.getPhiInstructionNumbers(), parser.getProgramInstructions());;
		optimizer.optimize(true, true);
		RegisterAllocator regAlloc = new RegisterAllocator(Instruction.programInstructions, Instruction.phiInstructionNumbers, parser.blockMap, parser.blockStack);
		regAlloc.buildGraphAndAllocate(parser.root);
		//print(parser, "test020.txt.vcg");
		printInterferenceGraph("test020.txt.vcg", regAlloc.IGraph);
		//runXVCG("test020.txt.vcg"); 
	}

	@Test  
	public void test021() throws ParsingException, IOException{
		Parser parser = new Parser("src/test/resources/test021.txt"); 
		parser.parse();
		Optimizer optimizer = new Optimizer(parser, parser.getPhiInstructionNumbers(), parser.getProgramInstructions());;
		optimizer.optimize(true, true);
		RegisterAllocator regAlloc = new RegisterAllocator(Instruction.programInstructions, Instruction.phiInstructionNumbers, parser.blockMap, parser.blockStack);
		regAlloc.buildGraphAndAllocate(parser.root);
		//print(parser, "test021.txt.vcg");
		printInterferenceGraph("test021.txt.vcg", regAlloc.IGraph);
		//runXVCG("test021.txt.vcg"); 
	}

	@Test  
	public void test022() throws ParsingException, IOException{
		Parser parser = new Parser("src/test/resources/test022.txt"); 
		parser.parse();
		Optimizer optimizer = new Optimizer(parser, parser.getPhiInstructionNumbers(), parser.getProgramInstructions());;
		optimizer.optimize(true, true);
		RegisterAllocator regAlloc = new RegisterAllocator(Instruction.programInstructions, Instruction.phiInstructionNumbers, parser.blockMap, parser.blockStack);
		regAlloc.buildGraphAndAllocate(parser.root);
		//print(parser, "test022.txt.vcg");
		printInterferenceGraph("test022.txt.vcg", regAlloc.IGraph);
		//runXVCG("test022.txt.vcg"); 
	}

	@Test  
	public void test023() throws ParsingException, IOException{
		Parser parser = new Parser("src/test/resources/test023.txt"); 
		parser.parse();
		Optimizer optimizer = new Optimizer(parser, parser.getPhiInstructionNumbers(), parser.getProgramInstructions());;
		optimizer.optimize(true, true);
		RegisterAllocator regAlloc = new RegisterAllocator(Instruction.programInstructions, Instruction.phiInstructionNumbers, parser.blockMap, parser.blockStack);
		regAlloc.buildGraphAndAllocate(parser.root);
		//print(parser, "test023.txt.vcg");
		printInterferenceGraph("test023.txt.vcg", regAlloc.IGraph);
		//runXVCG("test023.txt.vcg");  
	}
	
	@Test   
	public void test024() throws ParsingException, IOException{
		Parser parser = new Parser("src/test/resources/test024.txt"); 
		parser.parse();
		Optimizer optimizer = new Optimizer(parser, parser.getPhiInstructionNumbers(), parser.getProgramInstructions());;
		optimizer.optimize(true, true);
		RegisterAllocator regAlloc = new RegisterAllocator(Instruction.programInstructions, Instruction.phiInstructionNumbers, parser.blockMap, parser.blockStack);
		regAlloc.buildGraphAndAllocate(parser.root);
		//print(parser, "test024.txt.vcg");
		printInterferenceGraph("test024.txt.vcg", regAlloc.IGraph);
		//runXVCG("test024.txt.vcg"); 
	}
	
	@Test  
	public void test025() throws ParsingException, IOException{
		Parser parser = new Parser("src/test/resources/test025.txt"); 
		parser.parse();
		Optimizer optimizer = new Optimizer(parser, parser.getPhiInstructionNumbers(), parser.getProgramInstructions());;
		optimizer.optimize(true, true);
		RegisterAllocator regAlloc = new RegisterAllocator(Instruction.programInstructions, Instruction.phiInstructionNumbers, parser.blockMap, parser.blockStack);
		regAlloc.buildGraphAndAllocate(parser.root);
		print(parser, "test025.txt.vcg");
		printInterferenceGraph("test025.txt.vcg", regAlloc.IGraph);
		runXVCG("test025.txt.vcg"); 
	}
	
	@Test   
	public void test026() throws ParsingException, IOException{
		Parser parser = new Parser("src/test/resources/test026.txt"); 
		parser.parse();
		Optimizer optimizer = new Optimizer(parser, parser.getPhiInstructionNumbers(), parser.getProgramInstructions());;
		optimizer.optimize(true, true);
		RegisterAllocator regAlloc = new RegisterAllocator(Instruction.programInstructions, Instruction.phiInstructionNumbers, parser.blockMap, parser.blockStack);
		regAlloc.buildGraphAndAllocate(parser.root);
		//print(parser, "test026.txt.vcg");
		printInterferenceGraph("test026.txt.vcg", regAlloc.IGraph);
		//runXVCG("test026.txt.vcg"); 
	}
	
	@Test //TODO Null pointer exception being thrown in the if statement  
	public void test027() throws ParsingException, IOException{
		Parser parser = new Parser("src/test/resources/test027.txt"); 
		parser.parse();
		Optimizer optimizer = new Optimizer(parser, parser.getPhiInstructionNumbers(), parser.getProgramInstructions());;
		optimizer.optimize(true, true);
		RegisterAllocator regAlloc = new RegisterAllocator(Instruction.programInstructions, Instruction.phiInstructionNumbers, parser.blockMap, parser.blockStack);
		regAlloc.buildGraphAndAllocate(parser.root);
		//print(parser, "test027.txt.vcg");
		printInterferenceGraph("test027.txt.vcg", regAlloc.IGraph);
		//runXVCG("test027.txt.vcg"); 
	}
	
	@Test   
	public void test028() throws ParsingException, IOException{
		Parser parser = new Parser("src/test/resources/test028.txt"); 
		parser.parse();
		Optimizer optimizer = new Optimizer(parser, parser.getPhiInstructionNumbers(), parser.getProgramInstructions());;
		optimizer.optimize(true, true);
		RegisterAllocator regAlloc = new RegisterAllocator(Instruction.programInstructions, Instruction.phiInstructionNumbers, parser.blockMap, parser.blockStack);
		regAlloc.buildGraphAndAllocate(parser.root);
		print(parser, "test028.txt.vcg");
		printInterferenceGraph("test028.txt.vcg", regAlloc.IGraph);
		runXVCG("test028.txt.vcg"); 
	}
	
	@Test  
	public void test029() throws ParsingException, IOException{
		Parser parser = new Parser("src/test/resources/test029.txt"); 
		parser.parse();
		Optimizer optimizer = new Optimizer(parser, parser.getPhiInstructionNumbers(), parser.getProgramInstructions());;
		optimizer.optimize(true, true);
		RegisterAllocator regAlloc = new RegisterAllocator(Instruction.programInstructions, Instruction.phiInstructionNumbers, parser.blockMap, parser.blockStack);
		regAlloc.buildGraphAndAllocate(parser.root);
		//print(parser, "test029.txt.vcg");
		printInterferenceGraph("test029.txt.vcg", regAlloc.IGraph);
		//runXVCG("test029.txt.vcg"); 
	}
	
	@Test  
	public void test030() throws ParsingException, IOException{
		Parser parser = new Parser("src/test/resources/test030.txt"); 
		parser.parse();
		Optimizer optimizer = new Optimizer(parser, parser.getPhiInstructionNumbers(), parser.getProgramInstructions());;
		optimizer.optimize(true, true);
		RegisterAllocator regAlloc = new RegisterAllocator(Instruction.programInstructions, Instruction.phiInstructionNumbers, parser.blockMap, parser.blockStack);
		regAlloc.buildGraphAndAllocate(parser.root);
		//print(parser, "test030.txt.vcg");
		printInterferenceGraph("test030.txt.vcg", regAlloc.IGraph);
		//runXVCG("test030.txt.vcg"); 
	}
	
	@Test
	public void test031() throws ParsingException, IOException{
		Parser parser = new Parser("src/test/resources/test031.txt"); 
		parser.parse();
		Optimizer optimizer = new Optimizer(parser, parser.getPhiInstructionNumbers(), parser.getProgramInstructions());;
		optimizer.optimize(true, true);
		RegisterAllocator regAlloc = new RegisterAllocator(Instruction.programInstructions, Instruction.phiInstructionNumbers, parser.blockMap, parser.blockStack);
		regAlloc.buildGraphAndAllocate(parser.root);
		//print(parser, "test031.txt.vcg");
		printInterferenceGraph("test031.txt.vcg", regAlloc.IGraph);
		//runXVCG("test031.txt.vcg"); 
	}


	/*****Simple Unit Tests Begin
	 * @throws IOException ********************/

	@Test
	public void testIfElse() throws ParsingException, IOException {
		Parser parser = new Parser("src/test/resources/unit_tests/if_else.txt"); 
		parser.parse();
		Optimizer optimizer = new Optimizer(parser, parser.getPhiInstructionNumbers(), parser.getProgramInstructions());;
		optimizer.optimize(true, true);
		RegisterAllocator regAlloc = new RegisterAllocator(Instruction.programInstructions, Instruction.phiInstructionNumbers, parser.blockMap, parser.blockStack);
		regAlloc.buildGraphAndAllocate(parser.root); 
		print(parser, "if_else.txt.vcg");
		printInterferenceGraph("if_else.txt.vcg", regAlloc.IGraph);
		runXVCG("if_else.txt.vcg"); 
	}

	@Test
	public void testIfIf() throws ParsingException, IOException {
		Parser parser = new Parser("src/test/resources/unit_tests/if_if.txt"); 
		parser.parse();
		Optimizer optimizer = new Optimizer(parser, parser.getPhiInstructionNumbers(), parser.getProgramInstructions());;
		optimizer.optimize(true, true);
		RegisterAllocator regAlloc = new RegisterAllocator(Instruction.programInstructions, Instruction.phiInstructionNumbers, parser.blockMap, parser.blockStack);
		regAlloc.buildGraphAndAllocate(parser.root); 
		print(parser, "if_if.txt.vcg");
		runXVCG("if_if.txt.vcg");
	}

	@Test
	public void testIfElseIfElseIfElse() throws ParsingException, IOException {
		Parser parser = new Parser("src/test/resources/unit_tests/if_else_if_else_else.txt"); 
		parser.parse();
		Optimizer optimizer = new Optimizer(parser, parser.getPhiInstructionNumbers(), parser.getProgramInstructions());;
		optimizer.optimize(true, true);
		RegisterAllocator regAlloc = new RegisterAllocator(Instruction.programInstructions, Instruction.phiInstructionNumbers, parser.blockMap, parser.blockStack);
		regAlloc.buildGraphAndAllocate(parser.root); 
		print(parser, "if_else_if_else_else.txt.vcg");
		runXVCG("if_else_if_else_else.txt.vcg");
	}

	@Test  
	public void testNestedWhile() throws ParsingException, IOException{
		Parser parser = new Parser("src/test/resources/unit_tests/nested_while.txt"); 
		parser.parse();
		Optimizer optimizer = new Optimizer(parser, parser.getPhiInstructionNumbers(), parser.getProgramInstructions());;
		optimizer.optimize(true, true);
		RegisterAllocator regAlloc = new RegisterAllocator(Instruction.programInstructions, Instruction.phiInstructionNumbers, parser.blockMap, parser.blockStack);
		regAlloc.buildGraphAndAllocate(parser.root);
		print(parser, "nested_while.txt.vcg");
		runXVCG("nested_while.txt.vcg");;

	}

	@Test  
	public void arrayIfElse() throws ParsingException, IOException{
		Parser parser = new Parser("src/test/resources/unit_tests/array_if_else.txt"); 
		parser.parse();
		Optimizer optimizer = new Optimizer(parser, parser.getPhiInstructionNumbers(), parser.getProgramInstructions());;
		optimizer.optimize(true, true);
		RegisterAllocator regAlloc = new RegisterAllocator(Instruction.programInstructions, Instruction.phiInstructionNumbers, parser.blockMap, parser.blockStack);
		regAlloc.buildGraphAndAllocate(parser.root);
		print(parser, "array_if_else.txt.vcg");
		runXVCG("array_if_else.txt.vcg");
	}

	
}
