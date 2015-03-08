package com.pl241.test;

import java.io.IOException;

import org.junit.Test;

import compiler.components.codegen.DLXCodeGen;
import compiler.components.codegen.DLXSimulator;
import compiler.components.optimization.Optimizer;
import compiler.components.parser.Instruction;
import compiler.components.parser.Parser;
import compiler.components.parser.ParsingException;
import compiler.components.register.RegisterAllocator;

public class TestDLXCodeGen {

	@Test //passing with cfg
	public void test001() throws ParsingException, IOException{
		Parser parser = new Parser("src/test/resources/test001.txt"); 
		parser.parse();
		Optimizer optimizer = new Optimizer(parser, parser.getPhiInstructionNumbers(), parser.getProgramInstructions());;
		optimizer.optimize(true, true);
		RegisterAllocator regAlloc = new RegisterAllocator(Instruction.programInstructions, Instruction.phiInstructionNumbers, parser.blockMap, parser.blockStack);
		regAlloc.buildGraphAndAllocate(parser.root);

		DLXCodeGen codeGenerator = new DLXCodeGen(Instruction.programInstructions);
		codeGenerator.generateAssembly();

		DLXSimulator.load(codeGenerator.getInstructionWords());
		DLXSimulator.execute();
	}

}
