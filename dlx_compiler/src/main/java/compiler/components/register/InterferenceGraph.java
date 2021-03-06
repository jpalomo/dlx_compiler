package compiler.components.register;

import java.util.ArrayList;
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

	public Map<Integer, INode> graph;
	public Map<Integer, SuperNode> nodesToClusters;
	private static final int NUM_REGISTERS = 8;

	public InterferenceGraph(Map<Integer, Integer> frequencies) {
		this.graph = new HashMap<Integer, INode>();
		this.nodesToClusters = new HashMap<Integer, SuperNode>();
	}
	
	public Map<Integer, INode> getGraph() {
		return graph;
	}
	
	public boolean isGraphEmpty() {
		if(graph.keySet().size() > 0) {
			return false;
		}
		return true;
	}

	public INode getNode(int nodeNum) {
		return graph.get(Integer.valueOf(nodeNum)); 
	}
	
	public int getRegisterNumber(int nodeNum) {
	
		if(nodesToClusters.containsKey(nodeNum)) {
			while(nodesToClusters.containsKey(nodeNum)) {
				nodeNum = nodesToClusters.get(nodeNum).nodeNumber;
			}
		}
		
		INode node = getNode(nodeNum);
		if(node == null) {
			return -1;
		}
		return node.registerNumber;
		
	}

	public void addNodeToGraph(int newNodeNumber) {
		if(!graph.containsKey(newNodeNumber)) {
			INode newNode = new INode(newNodeNumber);
			graph.put(newNodeNumber, newNode);
			LOGGER.debug("Added node {} to graph", newNodeNumber);
		}
	}
	
	// override above function for adding a node that is not new (i.e. adding a cluster)
	public void addNodeToGraph(INode node) {
		if(!graph.containsKey(node.nodeNumber)) {
			graph.put(node.nodeNumber, node);
			LOGGER.debug("Added node {} to graph", node.nodeNumber);
		}
	}
	
	public void addNodeBackToGraph(INode node, List<Integer> neighbors) {
		if(!graph.containsKey(node.nodeNumber)) {
			graph.put(node.nodeNumber, node);
		}
		for(int neighbor : neighbors) {
			addEdge(node.nodeNumber, neighbor);
			addEdge(neighbor, node.nodeNumber);
		}
		LOGGER.debug("Added node {} and its edges back to graph", node.nodeNumber);
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
		if(fromNode == null) {
			fromNode = new INode(from);
			graph.put(from, fromNode);
		}
		toNode = graph.get(to);
		if(toNode == null) {
			toNode = new INode(to);
			graph.put(to, toNode);
		}
		
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
	
		for(int nodeNum = 0; nodeNum < node.neighbors.size(); nodeNum++) {
			INode neighbor = graph.get(Integer.valueOf(node.neighbors.get(nodeNum)));
			if(neighbor != null && neighbor.neighbors.contains(node.nodeNumber)) {
				neighbor.neighbors.remove(Integer.valueOf(node.nodeNumber));
				LOGGER.debug("Removed {} from {}.", node.nodeNumber, neighbor.nodeNumber);
				node.neighbors.remove(Integer.valueOf(neighbor.nodeNumber));
				LOGGER.debug("Removed {} from {}.", neighbor.nodeNumber, node.nodeNumber);
				nodeNum--;
			}
		}
        LOGGER.debug("Removing {} from graph.", node.nodeNumber);
		return graph.remove(Integer.valueOf(node.nodeNumber));
	}
	
	public INode removeFromGraphTemp(INode node) {
		
        LOGGER.debug("Removing {} from graph.", node.nodeNumber);
		return graph.remove(Integer.valueOf(node.nodeNumber));
	}
	
	public void updateNeighbors(INode node, SuperNode cluster) {
		
		List<Integer> tempList = new ArrayList<Integer>(node.neighbors);
		
		for (int i : tempList) {
			INode temp = getNode(i);
			if (temp != null) {
				for (int j = 0; j < temp.neighbors.size(); j++) {
					if (temp.neighbors.get(j) == node.nodeNumber) {
						int removed = temp.neighbors.remove(j);
						temp.neighbors.add(cluster.nodeNumber);
						LOGGER.debug("Updating neighbor {} from {} to {}.", temp.neighbors.get(j), removed, cluster.nodeNumber);
						node.neighbors.remove(Integer.valueOf(node.nodeNumber));
						node.neighbors.add(cluster.nodeNumber);
						LOGGER.debug("Updating neighbor {} from {} to {}.", node.nodeNumber, node.nodeNumber, cluster.nodeNumber);
						break;
					}
				}
			}
		}
		
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
		int maxEdges = -1;
		INode returnNode = null;
		for(INode node : graph.values()) {
			if(node.neighbors.size() <= numOfEdges) {
				if (node.neighbors.size() > maxEdges) {
					maxEdges = node.neighbors.size();
					returnNode = node;
				}
			}
		}
			
		if (returnNode == null) {
			LOGGER.debug("Could not find a node with less than {} number of edges. Finding node by cost.", numOfEdges);
			return findNodeBasedOnCost();
		}
		else {
			LOGGER.debug("Node {} has less than {} edges with {} number of edges.", returnNode.nodeNumber, numOfEdges, returnNode.neighbors.size()); 
			return returnNode;
		}
	}
	
	public void intepretCosts(Map<Integer, Integer> frequencies) {
		
		for(int i : frequencies.keySet()) {
			if(graph.containsKey(i)) {
				INode node = graph.get(i);
				node.numOfUses = frequencies.get(i);
			}
		}
	}

}