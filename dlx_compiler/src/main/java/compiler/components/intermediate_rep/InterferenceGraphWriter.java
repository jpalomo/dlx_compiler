package compiler.components.intermediate_rep;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import compiler.components.parser.Instruction;
import compiler.components.register.InterferenceGraph;
import compiler.components.register.InterferenceGraph.INode;

/**
 * Each method is responsible for creating a new line.
 * 
 * @author Palomo
 *
 */
public class InterferenceGraphWriter {
	static Logger LOGGER = LoggerFactory.getLogger(InterferenceGraphWriter.class);

	private PrintWriter writer;

	public InterferenceGraphWriter(String fileName, Map<Integer, Instruction> programInstructions) {
		try {
			writer = new PrintWriter(new File(fileName));
		} catch (IOException e) {
			e.printStackTrace();
		}
		writer.println("graph: { title: \"Control Flow Graph\"");
		writer.println("layoutalgorithm: dfs");
		writer.println("manhattan_edges: no");
		writer.print("smanhattan_edges: no");
	}

	public void emitInterferenceGraph(InterferenceGraph ig) {
		
		Set<Integer> addedSources = new HashSet<Integer>();
		Set<Integer> addedDest = new HashSet<Integer>();
		for(int nodeNum : ig.getGraph().keySet()) {
			
			INode node = ig.getGraph().get(nodeNum);
			writer.println();
			writer.println("node: {");
			writer.println("title: \"" + node.nodeNumber + "\"");
			writer.print("label: \"" + node.nodeNumber + "\"");

			emitExitNode();
			// print the information for the control flow
			for (int neighbor : node.neighbors) {
				INode neighborNode = ig.getGraph().get(neighbor);
				/*if(added(addedSources, addedDest, neighbor)) {
					continue;
				}*/

				//addedEdges.add(nodeNum + neighbor);
				writer.println();
				writer.println("edge: { sourcename: " + "\"" + nodeNum
					+ "\"");
				writer.println("targetname: " + "\"" + neighbor
					+ "\"");
				writer.println("color: blue");
				writer.println("}");
			}
		}
	}


	private boolean added(Set<Integer> addedSources, Set<Integer> addedDest, int source, int dest) {
		return false;
	}

	private void emitExitNode() {
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