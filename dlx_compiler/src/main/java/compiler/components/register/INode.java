package compiler.components.register;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class INode {
	public List<Integer> neighbors;
	public int numOfUses;
	public int nodeNumber; //instruction number
	public int registerNumber;
	public static int clusterId = -1;
	public List<INode> internalNodes;

	public INode(int nodeNumber) {
		this.nodeNumber= nodeNumber;
		neighbors = new LinkedList<Integer>();
		this.registerNumber = -1;
		internalNodes = new LinkedList<INode>();
	}
} 

	class SuperNode extends INode {
		public List<INode> internalNodes;
		
		public SuperNode() {
			super(clusterId--);
			this.internalNodes = new LinkedList<INode>();
			numOfUses = 0;
		}
	
}