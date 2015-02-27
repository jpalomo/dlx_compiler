package compiler.components.register;

import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class InterferenceGraph {
	static Logger LOGGER = LoggerFactory.getLogger(InterferenceGraph.class);

	private Map<Integer, INode> graph;
	private static final int NUM_REGISTERS = 8;
	public static int clusterId = -1;

	public Map<Integer, INode> getGraph() {
		return graph;
	}

	public INode getNode(int nodeNum) {
		return graph.get(nodeNum); 
	}

	public InterferenceGraph(Map<Integer, Integer> frequencies) {
		this.graph = new HashMap<Integer, INode>();
	}

	public void addNodeToGraph(int newNodeNumber) {
		if(!graph.containsKey(newNodeNumber)) {
			INode newNode = new INode(newNodeNumber);
			graph.put(newNodeNumber, newNode);
			LOGGER.debug("Added node {} to graph", newNodeNumber);
		}
	}
	
	public void addEdges(Set<Integer> liveSet) {
		
		Iterator<Integer> iter = liveSet.iterator();
		
		while(iter.hasNext()) {
			addNodeToGraph(iter.next());
		}
		
		iter = liveSet.iterator();
		while (iter.hasNext()) {
			Iterator<Integer> iter2 = liveSet.iterator();
			int temp = iter.next();
			while(iter2.hasNext()) {
				addEdge(temp,iter2.next()); 
			}
		}
	}
	
	public void addEdge(int from, int to) {
		INode fromNode;
		INode toNode;

		if(from == to) {
			return;
		}
		
		fromNode = graph.get(from);
		toNode = graph.get(to);
		
		if(!fromNode.neighbors.contains(to)) {
			fromNode.neighbors.add(to);
		LOGGER.debug("Added edge from {} to {}", from, to);
		}
		if(!toNode.neighbors.contains(from)) {
			toNode.neighbors.add(from);
		LOGGER.debug("Added edge from {} to {}", to, from);
		}
	}

	public INode removeFromGraph(INode node) {
		for(int nodeNum : node.neighbors) {
			INode neighbor = graph.get(nodeNum);
			if(neighbor.neighbors.contains(node.nodeNumber)) {
				neighbor.neighbors.remove(node.nodeNumber);
				LOGGER.debug("Removed {} from {}.", node.nodeNumber, neighbor.nodeNumber);
				node.neighbors.remove(neighbor.nodeNumber);
				LOGGER.debug("Removed {} from {}.", neighbor.nodeNumber, node.nodeNumber);
			}
		}
        LOGGER.debug("Removing {} from graph.", node.nodeNumber);
		return graph.remove(node.nodeNumber);
	}

	public INode findNodeBasedOnCost() {
		INode lowestCostNode = null;
		for(INode node : graph.values()) {
			if(lowestCostNode == null) {
				lowestCostNode = node;
				LOGGER.debug("Setting lowest cost node to {} with {} number of uses.", lowestCostNode.nodeNumber, lowestCostNode.numOfUses);
				continue;
			}

			if(node.numOfUses > lowestCostNode.numOfUses) {
				lowestCostNode = node;
				LOGGER.debug("Found new lowest cost node {} with {} number of uses.", lowestCostNode.nodeNumber, lowestCostNode.numOfUses);
			}
		}
		return lowestCostNode;
	}

	public INode findNodeWithEdgesLess(int numOfEdges) {
		for(INode node : graph.values()) {
			if(node.neighbors.size() <= numOfEdges) {
				LOGGER.debug("Node {} has less than {} edges with {} number of edges.", node.nodeNumber, numOfEdges, node.neighbors.size()); 
				return node;
			}
		}

		return findNodeBasedOnCost();
	}
	
	public void intepretCosts(Map<Integer, Integer> frequencies) {
		
		for(int i : frequencies.keySet()) {
			if(graph.containsKey(i)) {
				INode node = graph.get(i);
				node.numOfUses = frequencies.get(i);
			}
		}
	}

	public void colorGraph() {
		
	}

	public class INode{
		public List<Integer> neighbors;
		public int numOfUses;
		public int nodeNumber; //instruction number

		public INode(int nodeNumber) {
			this.nodeNumber= nodeNumber;
			neighbors = new LinkedList<Integer>();
		}
	} 

	public class SuperNode extends INode {
		public List<INode> internalNodes;
		
		public SuperNode() {
			super(clusterId--);
			numOfUses = 0;
		}
		
	}
}