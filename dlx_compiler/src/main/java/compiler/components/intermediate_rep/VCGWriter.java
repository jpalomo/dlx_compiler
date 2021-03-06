package compiler.components.intermediate_rep;

import static compiler.components.intermediate_rep.BasicBlock.BlockType.FUNCTION;
import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashSet;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import compiler.components.parser.Instruction;

/**
 * Each method is responsible for creating a new line.
 * 
 * @author Palomo
 *
 */
public class VCGWriter {
	static Logger LOGGER = LoggerFactory.getLogger(VCGWriter.class);

	private PrintWriter writer;
	private HashSet<Integer> printedNodes;
	private Map<Integer, Instruction> programInstructions;

	public VCGWriter(String fileName, Map<Integer, Instruction> programInstructions) {
		try {
			writer = new PrintWriter(new File(fileName));
			printedNodes = new HashSet<Integer>();
			this.programInstructions = programInstructions;
		} catch (IOException e) {
			e.printStackTrace();
		}
		writer.println("graph: { title: \"Control Flow Graph\"");
		writer.println("layoutalgorithm: dfs");
		writer.println("manhattan_edges: yes");
		writer.print("smanhattan_edges: yes");
	}

	public void emitControlFlowGraph(BasicBlock bb) {
		
		//in control flow graphs multiple blocks may converge to a single block, when this happens, we dont want to duplicate the node
		if (!printedNodes.contains(bb.blockNumber)) {
			writer.println();
			writer.println("node: {");
			writer.println("title: \"" + bb.blockNumber + "\"");
			writer.print("label: \"" + bb.blockNumber + "[");
			writeBBInstructions(bb);
			emitExitBasicBlock();
			printedNodes.add(bb.blockNumber);
			LOGGER.trace("Printing block number:" + bb.toString());

		}
		else {
			return;
		}

		// print the information for the control flow
		for (BasicBlock controlFlow : bb.controlFlow) {
			writer.println();
			writer.println("edge: { sourcename: " + "\"" + bb.blockNumber
					+ "\"");
			writer.println("targetname: " + "\"" + controlFlow.blockNumber
					+ "\"");
			if((bb.blockType != FUNCTION) && (controlFlow.blockType.equals(FUNCTION))) {
				writer.println("color: blue");
			} 
			else {
				writer.println("color: red");
			}
			writer.println("}");
		}
		for (BasicBlock controlFlow : bb.controlFlow) {
			emitControlFlowGraph(controlFlow);
		}
	}

	public void emitDominatorGraph(BasicBlock bb) {
		
		//in control flow graphs multiple blocks may converge to a single block, when this happens, we dont want to duplicate the node
		if (!printedNodes.contains(bb.blockNumber)) {
			writer.println();
			writer.println("node: {");
			writer.println("title: \"" + bb.blockNumber + "\"");
			writer.print("label: \"" + bb.blockNumber + "[");
			writeBBInstructions(bb);
			emitExitBasicBlock();
			printedNodes.add(bb.blockNumber);
			LOGGER.trace("Printing block number:" + bb.toString());

		}
		else {
			return;
		}

		for (BasicBlock dominatee : bb.dominatees) {
			writer.println();
			writer.println("edge: { sourcename: " + "\"" + bb.blockNumber
					+ "\"");
			writer.println("targetname: " + "\"" + dominatee.blockNumber
					+ "\"");
			if(bb.blockType == FUNCTION){ 
				writer.println("color: blue");
			} 
			else{
				writer.println("color: red");
			}
			writer.println("}");
		}

		// print the information for the control flow
        for (BasicBlock dominatee : bb.dominatees) {
        	emitDominatorGraph(dominatee);
		}
	}
	
	private void writeBBInstructions(BasicBlock bb) {
		for (Integer instNum : bb.getInstructions()) {
			writer.println();
			writer.print(programInstructions.get(instNum).toString());
		}
		writer.write("\n]\"");
	}

	private void emitExitBasicBlock() {
		writer.println();
		writer.print("}");
	}

	public void close() {
		writer.println();
		writer.print("}");
		writer.flush();
		writer.close();
	}
}