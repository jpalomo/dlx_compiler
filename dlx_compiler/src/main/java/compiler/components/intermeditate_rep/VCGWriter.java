package compiler.components.intermeditate_rep;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashSet;
import java.util.Map;

/**
 * Each method is responsible for creating a new line.
 * 
 * @author Palomo
 *
 */
public class VCGWriter {
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

	public void emitBeginBasicBlock(BasicBlock bb, boolean dominators,
			boolean controlFlows) {
		
		//in control flow graphs multiple blocks may converge to a single block, when this happens, we dont want to duplicate the node
		if (!printedNodes.contains(bb.blockNumber)) {
			writer.println();
			writer.println("node: {");
			writer.println("title: \"" + bb.blockNumber + "\"");
			writer.print("label: \"" + bb.blockNumber + "[");
			writeBBInstructions(bb);
			emitExitBasicBlock();
			printedNodes.add(bb.blockNumber);
		}

		// Create all the dominator links
		if (dominators) {
			for (BasicBlock dominatee : bb.getDominatees()) {
				writer.println();
				writer.println("edge: { sourcename: " + "\"" + bb.blockNumber
						+ "\"");
				writer.println("targetname: " + "\"" + dominatee.blockNumber
						+ "\"");
				writer.println("color: blue");
				writer.println("}");
			}
			for (BasicBlock dominatee : bb.getDominatees()) {
				emitBeginBasicBlock(dominatee, dominators, controlFlows);
			}
		}

		// print the information for the control flow
		if (controlFlows) {
			for (BasicBlock controlFlow : bb.getControlFlow()) {
				writer.println();
				writer.println("edge: { sourcename: " + "\"" + bb.blockNumber
						+ "\"");
				writer.println("targetname: " + "\"" + controlFlow.blockNumber
						+ "\"");
				writer.println("color: red");
				writer.println("}");
			}
			for (BasicBlock controlFlow : bb.getControlFlow()) {
				emitBeginBasicBlock(controlFlow, dominators, controlFlows);
			}
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