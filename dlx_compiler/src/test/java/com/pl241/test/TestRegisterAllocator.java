package com.pl241.test;

import java.io.IOException;
import java.util.HashMap;

import org.junit.Before;
import org.junit.Test;

import compiler.components.intermediate_rep.VCGWriter;
import compiler.components.optimization.CommonSubexpressionElimination;
import compiler.components.optimization.Optimizer;
import compiler.components.optimization.RegisterAllocator;
import compiler.components.parser.Instruction;
import compiler.components.parser.Parser;
import compiler.components.parser.ParsingException;

public class TestRegisterAllocator {
	private static String VCG_OUTPUT_DIR = "src/test/resources/reg/";
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
		vcg.emitControlFlowGraph(parser.root);
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
		Optimizer propagator = new Optimizer(parser, parser.getPhiInstructionNumbers(), parser.getProgramInstructions());
		CommonSubexpressionElimination cse = new CommonSubexpressionElimination(parser, parser.getProgramInstructions());
		RegisterAllocator regAlloc = new RegisterAllocator(Instruction.programInstructions);
		regAlloc.buildGraphAndAllocate(parser.root);
		printCFG(parser, "test001.txt.vcg");
		runXVCG("test001.txt.vcg"); 
	}

	@Test  
	public void test002() throws ParsingException, IOException{
		Parser parser = new Parser("src/test/resources/test002.txt"); 
		parser.parse();
		Optimizer propagator = new Optimizer(parser, parser.getPhiInstructionNumbers(), parser.getProgramInstructions());
		CommonSubexpressionElimination cse = new CommonSubexpressionElimination(parser, parser.getProgramInstructions());
		RegisterAllocator regAlloc = new RegisterAllocator(Instruction.programInstructions);
		regAlloc.buildGraphAndAllocate(parser.root);
		printCFG(parser, "test002.txt.vcg");
		runXVCG("test002.txt.vcg"); 
	}

	@Test   
	public void test003() throws ParsingException, IOException{
		Parser parser = new Parser("src/test/resources/test003.txt"); 
		parser.parse();
		Optimizer propagator = new Optimizer(parser, parser.getPhiInstructionNumbers(), parser.getProgramInstructions());
		CommonSubexpressionElimination cse = new CommonSubexpressionElimination(parser, parser.getProgramInstructions());
		RegisterAllocator regAlloc = new RegisterAllocator(Instruction.programInstructions);
		regAlloc.buildGraphAndAllocate(parser.root); 
		printCFG(parser, "test003.txt.vcg");
		runXVCG("test003.txt.vcg"); 
	}
	
	@Test  
	public void test004() throws ParsingException, IOException{
		Parser parser = new Parser("src/test/resources/test004.txt"); 
		parser.parse();
		Optimizer propagator = new Optimizer(parser, parser.getPhiInstructionNumbers(), parser.getProgramInstructions());
		CommonSubexpressionElimination cse = new CommonSubexpressionElimination(parser, parser.getProgramInstructions());
		RegisterAllocator regAlloc = new RegisterAllocator(Instruction.programInstructions);
		regAlloc.buildGraphAndAllocate(parser.root); 
		printCFG(parser, "test004.txt.vcg");
		runXVCG("test004.txt.vcg"); 
	}
	
	@Test //passing with cfg
	public void test005() throws ParsingException, IOException{
		Parser parser = new Parser("src/test/resources/test005.txt"); 
		parser.parse();
		Optimizer propagator = new Optimizer(parser, parser.getPhiInstructionNumbers(), parser.getProgramInstructions());
		CommonSubexpressionElimination cse = new CommonSubexpressionElimination(parser, parser.getProgramInstructions());
		RegisterAllocator regAlloc = new RegisterAllocator(Instruction.programInstructions);
		regAlloc.buildGraphAndAllocate(parser.root);
		printCFG(parser, "test005.txt.vcg");
		runXVCG("test005.txt.vcg"); 

	}
	
	@Test  //passing with cfg
	public void test006() throws ParsingException, IOException{
		Parser parser = new Parser("src/test/resources/test006.txt"); 
		parser.parse();
		Optimizer propagator = new Optimizer(parser, parser.getPhiInstructionNumbers(), parser.getProgramInstructions());
		CommonSubexpressionElimination cse = new CommonSubexpressionElimination(parser, parser.getProgramInstructions());
		RegisterAllocator regAlloc = new RegisterAllocator(Instruction.programInstructions);
		regAlloc.buildGraphAndAllocate(parser.root);
		printCFG(parser, "test006.txt.vcg");
		runXVCG("test006.txt.vcg"); 
	}

	@Test //passing with cfg 
	public void test007() throws ParsingException, IOException{
		Parser parser = new Parser("src/test/resources/test007.txt"); 
		parser.parse();
		Optimizer propagator = new Optimizer(parser, parser.getPhiInstructionNumbers(), parser.getProgramInstructions());
		CommonSubexpressionElimination cse = new CommonSubexpressionElimination(parser, parser.getProgramInstructions());
		RegisterAllocator regAlloc = new RegisterAllocator(Instruction.programInstructions);
		regAlloc.buildGraphAndAllocate(parser.root);
		printCFG(parser, "test007.txt.vcg");
		runXVCG("test007.txt.vcg"); 
	}

	@Test  //TODO simple while test not working
	public void test008() throws ParsingException, IOException{
		Parser parser = new Parser("src/test/resources/test008.txt"); 
		parser.parse();
		Optimizer propagator = new Optimizer(parser, parser.getPhiInstructionNumbers(), parser.getProgramInstructions());
		CommonSubexpressionElimination cse = new CommonSubexpressionElimination(parser, parser.getProgramInstructions());
		RegisterAllocator regAlloc = new RegisterAllocator(Instruction.programInstructions);
		regAlloc.buildGraphAndAllocate(parser.root);
		printCFG(parser, "test008.txt.vcg");
		runXVCG("test008.txt.vcg"); 
	}

	@Test //TODO ifs are generating empty blocks 
	public void test009() throws ParsingException, IOException{
		Parser parser = new Parser("src/test/resources/test009.txt"); 
		parser.parse();
		Optimizer propagator = new Optimizer(parser, parser.getPhiInstructionNumbers(), parser.getProgramInstructions());
		CommonSubexpressionElimination cse = new CommonSubexpressionElimination(parser, parser.getProgramInstructions());
		RegisterAllocator regAlloc = new RegisterAllocator(Instruction.programInstructions);
		regAlloc.buildGraphAndAllocate(parser.root);
		printCFG(parser, "test009.txt.vcg");
		runXVCG("test009.txt.vcg"); 
	}

	@Test  //passing with cfg
	public void test010() throws ParsingException, IOException{
		Parser parser = new Parser("src/test/resources/test010.txt"); 
		parser.parse();
		Optimizer propagator = new Optimizer(parser, parser.getPhiInstructionNumbers(), parser.getProgramInstructions());
		CommonSubexpressionElimination cse = new CommonSubexpressionElimination(parser, parser.getProgramInstructions());
		RegisterAllocator regAlloc = new RegisterAllocator(Instruction.programInstructions);
		regAlloc.buildGraphAndAllocate(parser.root);
		printCFG(parser, "test010.txt.vcg");
		runXVCG("test010.txt.vcg");;

	}

	@Test   
	public void test011() throws ParsingException, IOException{
		Parser parser = new Parser("src/test/resources/test011.txt"); 
		parser.parse();
		Optimizer propagator = new Optimizer(parser, parser.getPhiInstructionNumbers(), parser.getProgramInstructions());
		CommonSubexpressionElimination cse = new CommonSubexpressionElimination(parser, parser.getProgramInstructions());
		RegisterAllocator regAlloc = new RegisterAllocator(Instruction.programInstructions);
		regAlloc.buildGraphAndAllocate(parser.root);
		printCFG(parser, "test011.txt.vcg");
		runXVCG("test011.txt.vcg"); 
	}

	@Test //passing with cfg 
	public void test012() throws ParsingException, IOException{
		Parser parser = new Parser("src/test/resources/test012.txt"); 
		parser.parse();
		Optimizer propagator = new Optimizer(parser, parser.getPhiInstructionNumbers(), parser.getProgramInstructions());
		CommonSubexpressionElimination cse = new CommonSubexpressionElimination(parser, parser.getProgramInstructions());
		RegisterAllocator regAlloc = new RegisterAllocator(Instruction.programInstructions);
		regAlloc.buildGraphAndAllocate(parser.root);
		printCFG(parser, "test012.txt.vcg");
		runXVCG("test012.txt.vcg"); 
	}

	@Test 
	public void test013() throws ParsingException, IOException{
		Parser parser = new Parser("src/test/resources/test013.txt"); 
		parser.parse();
		Optimizer propagator = new Optimizer(parser, parser.getPhiInstructionNumbers(), parser.getProgramInstructions());
		CommonSubexpressionElimination cse = new CommonSubexpressionElimination(parser, parser.getProgramInstructions());
		RegisterAllocator regAlloc = new RegisterAllocator(Instruction.programInstructions);
		regAlloc.buildGraphAndAllocate(parser.root);
		printCFG(parser, "test013.txt.vcg");
		runXVCG("test013.txt.vcg");
	}

	@Test 
	public void test014() throws ParsingException, IOException{
		Parser parser = new Parser("src/test/resources/test014.txt"); 
		parser.parse();
		Optimizer propagator = new Optimizer(parser, parser.getPhiInstructionNumbers(), parser.getProgramInstructions());
		CommonSubexpressionElimination cse = new CommonSubexpressionElimination(parser, parser.getProgramInstructions());
		RegisterAllocator regAlloc = new RegisterAllocator(Instruction.programInstructions);
		regAlloc.buildGraphAndAllocate(parser.root);
		printCFG(parser, "test014.txt.vcg");
		runXVCG("test014.txt.vcg");
	}

	@Test 
	public void test015() throws ParsingException, IOException{
		Parser parser = new Parser("src/test/resources/test015.txt"); 
		parser.parse();
		RegisterAllocator regAlloc = new RegisterAllocator(Instruction.programInstructions);
		regAlloc.buildGraphAndAllocate(parser.root);
		printCFG(parser, "test015.txt.vcg");
		runXVCG("test015.txt.vcg");
	}

	@Test
	public void test016() throws ParsingException, IOException{
		Parser parser = new Parser("src/test/resources/test016.txt"); 
		parser.parse();
		Optimizer propagator = new Optimizer(parser, parser.getPhiInstructionNumbers(), parser.getProgramInstructions());
		CommonSubexpressionElimination cse = new CommonSubexpressionElimination(parser, parser.getProgramInstructions());
		RegisterAllocator regAlloc = new RegisterAllocator(Instruction.programInstructions);
		regAlloc.buildGraphAndAllocate(parser.root);
		printCFG(parser, "test016.txt.vcg");
		runXVCG("test016.txt.vcg");
	}

	@Test  
	public void test017() throws ParsingException, IOException{
		Parser parser = new Parser("src/test/resources/test017.txt"); 
		parser.parse();
		Optimizer propagator = new Optimizer(parser, parser.getPhiInstructionNumbers(), parser.getProgramInstructions());
		CommonSubexpressionElimination cse = new CommonSubexpressionElimination(parser, parser.getProgramInstructions());
		RegisterAllocator regAlloc = new RegisterAllocator(Instruction.programInstructions);
		regAlloc.buildGraphAndAllocate(parser.root);
		printCFG(parser, "test017.txt.vcg");
		runXVCG("test017.txt.vcg");
	}

	@Test  
	public void test018() throws ParsingException, IOException{
		Parser parser = new Parser("src/test/resources/test018.txt"); 
		parser.parse();
		Optimizer propagator = new Optimizer(parser, parser.getPhiInstructionNumbers(), parser.getProgramInstructions());
		CommonSubexpressionElimination cse = new CommonSubexpressionElimination(parser, parser.getProgramInstructions());
		RegisterAllocator regAlloc = new RegisterAllocator(Instruction.programInstructions);
		regAlloc.buildGraphAndAllocate(parser.root);
		printCFG(parser, "test018.txt.vcg");
		runXVCG("test018.txt.vcg");
	}

	@Test 
	public void test019() throws ParsingException, IOException{
		Parser parser = new Parser("src/test/resources/test019.txt"); 
		parser.parse();
		Optimizer propagator = new Optimizer(parser, parser.getPhiInstructionNumbers(), parser.getProgramInstructions());
		CommonSubexpressionElimination cse = new CommonSubexpressionElimination(parser, parser.getProgramInstructions());
		RegisterAllocator regAlloc = new RegisterAllocator(Instruction.programInstructions);
		regAlloc.buildGraphAndAllocate(parser.root);
		printCFG(parser, "test019.txt.vcg");
		runXVCG("test019.txt.vcg"); 
	}

	@Test
	public void test020() throws ParsingException, IOException{
		Parser parser = new Parser("src/test/resources/test020.txt"); 
		parser.parse();
		Optimizer propagator = new Optimizer(parser, parser.getPhiInstructionNumbers(), parser.getProgramInstructions());
		CommonSubexpressionElimination cse = new CommonSubexpressionElimination(parser, parser.getProgramInstructions());
		RegisterAllocator regAlloc = new RegisterAllocator(Instruction.programInstructions);
		regAlloc.buildGraphAndAllocate(parser.root);
		printCFG(parser, "test020.txt.vcg");
		runXVCG("test020.txt.vcg");
	}

	@Test  
	public void test021() throws ParsingException, IOException{
		Parser parser = new Parser("src/test/resources/test021.txt"); 
		parser.parse();
		Optimizer propagator = new Optimizer(parser, parser.getPhiInstructionNumbers(), parser.getProgramInstructions());
		CommonSubexpressionElimination cse = new CommonSubexpressionElimination(parser, parser.getProgramInstructions());
		RegisterAllocator regAlloc = new RegisterAllocator(Instruction.programInstructions);
		regAlloc.buildGraphAndAllocate(parser.root);
		printCFG(parser, "test021.txt.vcg");
		runXVCG("test021.txt.vcg"); 
	}

	@Test  
	public void test022() throws ParsingException, IOException{
		Parser parser = new Parser("src/test/resources/test022.txt"); 
		parser.parse();
		Optimizer propagator = new Optimizer(parser, parser.getPhiInstructionNumbers(), parser.getProgramInstructions());
		CommonSubexpressionElimination cse = new CommonSubexpressionElimination(parser, parser.getProgramInstructions());
		RegisterAllocator regAlloc = new RegisterAllocator(Instruction.programInstructions);
		regAlloc.buildGraphAndAllocate(parser.root);
		printCFG(parser, "test022.txt.vcg");
		runXVCG("test022.txt.vcg"); 
	}

	@Test  
	public void test023() throws ParsingException, IOException{
		Parser parser = new Parser("src/test/resources/test023.txt"); 
		parser.parse();
		Optimizer propagator = new Optimizer(parser, parser.getPhiInstructionNumbers(), parser.getProgramInstructions());
		CommonSubexpressionElimination cse = new CommonSubexpressionElimination(parser, parser.getProgramInstructions());
		RegisterAllocator regAlloc = new RegisterAllocator(Instruction.programInstructions);
		regAlloc.buildGraphAndAllocate(parser.root);
		printCFG(parser, "test023.txt.vcg");
		runXVCG("test023.txt.vcg"); 
	}
	
	@Test   
	public void test024() throws ParsingException, IOException{
		Parser parser = new Parser("src/test/resources/test024.txt"); 
		parser.parse();
		Optimizer propagator = new Optimizer(parser, parser.getPhiInstructionNumbers(), parser.getProgramInstructions());
		CommonSubexpressionElimination cse = new CommonSubexpressionElimination(parser, parser.getProgramInstructions());
		RegisterAllocator regAlloc = new RegisterAllocator(Instruction.programInstructions);
		regAlloc.buildGraphAndAllocate(parser.root);
		printCFG(parser, "test024.txt.vcg");
		runXVCG("test024.txt.vcg");
	}
	
	@Test  
	public void test025() throws ParsingException, IOException{
		Parser parser = new Parser("src/test/resources/test025.txt"); 
		parser.parse();
		Optimizer propagator = new Optimizer(parser, parser.getPhiInstructionNumbers(), parser.getProgramInstructions());
		CommonSubexpressionElimination cse = new CommonSubexpressionElimination(parser, parser.getProgramInstructions());
		RegisterAllocator regAlloc = new RegisterAllocator(Instruction.programInstructions);
		regAlloc.buildGraphAndAllocate(parser.root);
		printCFG(parser, "test025.txt.vcg");
		runXVCG("test025.txt.vcg"); 
	}
	
	@Test   
	public void test026() throws ParsingException, IOException{
		Parser parser = new Parser("src/test/resources/test026.txt"); 
		parser.parse();
		Optimizer propagator = new Optimizer(parser, parser.getPhiInstructionNumbers(), parser.getProgramInstructions());
		CommonSubexpressionElimination cse = new CommonSubexpressionElimination(parser, parser.getProgramInstructions());
		RegisterAllocator regAlloc = new RegisterAllocator(Instruction.programInstructions);
		regAlloc.buildGraphAndAllocate(parser.root);
		printCFG(parser, "test026.txt.vcg");
		runXVCG("test026.txt.vcg");
	}
	
	@Test //TODO Null pointer exception being thrown in the if statement  
	public void test027() throws ParsingException, IOException{
		Parser parser = new Parser("src/test/resources/test027.txt"); 
		parser.parse();
		Optimizer propagator = new Optimizer(parser, parser.getPhiInstructionNumbers(), parser.getProgramInstructions());
		CommonSubexpressionElimination cse = new CommonSubexpressionElimination(parser, parser.getProgramInstructions());
		RegisterAllocator regAlloc = new RegisterAllocator(Instruction.programInstructions);
		regAlloc.buildGraphAndAllocate(parser.root);
		printCFG(parser, "test027.txt.vcg");
		runXVCG("test027.txt.vcg"); 
	}
	
	@Test   
	public void test028() throws ParsingException, IOException{
		Parser parser = new Parser("src/test/resources/test028.txt"); 
		parser.parse();
		Optimizer propagator = new Optimizer(parser, parser.getPhiInstructionNumbers(), parser.getProgramInstructions());
		CommonSubexpressionElimination cse = new CommonSubexpressionElimination(parser, parser.getProgramInstructions());
		RegisterAllocator regAlloc = new RegisterAllocator(parser.getProgramInstructions());
		regAlloc.buildGraphAndAllocate(parser.root);
		printCFG(parser, "test028.txt.vcg");
		runXVCG("test028.txt.vcg");
	}
	
	@Test  
	public void test029() throws ParsingException, IOException{
		Parser parser = new Parser("src/test/resources/test029.txt"); 
		parser.parse();
		Optimizer propagator = new Optimizer(parser, parser.getPhiInstructionNumbers(), parser.getProgramInstructions());
		CommonSubexpressionElimination cse = new CommonSubexpressionElimination(parser, parser.getProgramInstructions());
		RegisterAllocator regAlloc = new RegisterAllocator(Instruction.programInstructions);
		regAlloc.buildGraphAndAllocate(parser.root);
		printCFG(parser, "test029.txt.vcg");
		runXVCG("test029.txt.vcg");
	}
	
	@Test  
	public void test030() throws ParsingException, IOException{
		Parser parser = new Parser("src/test/resources/test030.txt"); 
		parser.parse();
		Optimizer propagator = new Optimizer(parser, parser.getPhiInstructionNumbers(), parser.getProgramInstructions());
		CommonSubexpressionElimination cse = new CommonSubexpressionElimination(parser, parser.getProgramInstructions());
		RegisterAllocator regAlloc = new RegisterAllocator(Instruction.programInstructions);
		regAlloc.buildGraphAndAllocate(parser.root);
		printCFG(parser, "test030.txt.vcg");
		runXVCG("test030.txt.vcg");
	}
	
	@Test
	public void test031() throws ParsingException, IOException{
		Parser parser = new Parser("src/test/resources/test031.txt"); 
		parser.parse();
		Optimizer propagator = new Optimizer(parser, parser.getPhiInstructionNumbers(), parser.getProgramInstructions());
		CommonSubexpressionElimination cse = new CommonSubexpressionElimination(parser, parser.getProgramInstructions());
		RegisterAllocator regAlloc = new RegisterAllocator(Instruction.programInstructions);
		regAlloc.buildGraphAndAllocate(parser.root);
		printCFG(parser, "test031.txt.vcg");
		runXVCG("test031.txt.vcg");
	}


	/*****Simple Unit Tests Begin
	 * @throws IOException ********************/

	@Test
	public void testIfElse() throws ParsingException, IOException {
		Parser parser = new Parser("src/test/resources/unit_tests/if_else.txt"); 
		parser.parse();
		RegisterAllocator regAlloc = new RegisterAllocator(Instruction.programInstructions);
		regAlloc.buildGraphAndAllocate(parser.root); 
		printCFG(parser, "if_else.txt.vcg");
		runXVCG("if_else.txt.vcg");
	}

	@Test
	public void testIfIf() throws ParsingException, IOException {
		Parser parser = new Parser("src/test/resources/unit_tests/if_if.txt"); 
		parser.parse();
		RegisterAllocator regAlloc = new RegisterAllocator(Instruction.programInstructions);
		regAlloc.buildGraphAndAllocate(parser.root); 
		printCFG(parser, "if_if.txt.vcg");
		runXVCG("if_if.txt.vcg");
	}

	@Test
	public void testIfElseIfElseIfElse() throws ParsingException, IOException {
		Parser parser = new Parser("src/test/resources/unit_tests/if_else_if_else_else.txt"); 
		parser.parse();
		RegisterAllocator regAlloc = new RegisterAllocator(Instruction.programInstructions);
		regAlloc.buildGraphAndAllocate(parser.root); 
		printCFG(parser, "if_else_if_else_else.txt.vcg");
		runXVCG("if_else_if_else_else.txt.vcg");
	}

	@Test  
	public void testNestedWhile() throws ParsingException, IOException{
		Parser parser = new Parser("src/test/resources/unit_tests/nested_while.txt"); 
		parser.parse();
		RegisterAllocator regAlloc = new RegisterAllocator(Instruction.programInstructions);
		regAlloc.buildGraphAndAllocate(parser.root);
		printCFG(parser, "nested_while.txt.vcg");
		runXVCG("nested_while.txt.vcg");;

	}

	@Test  
	public void arrayIfElse() throws ParsingException, IOException{
		Parser parser = new Parser("src/test/resources/unit_tests/array_if_else.txt"); 
		parser.parse();
		RegisterAllocator regAlloc = new RegisterAllocator(Instruction.programInstructions);
		regAlloc.buildGraphAndAllocate(parser.root);
		printCFG(parser, "array_if_else.txt.vcg");
		runXVCG("array_if_else.txt.vcg");
	}

	
}