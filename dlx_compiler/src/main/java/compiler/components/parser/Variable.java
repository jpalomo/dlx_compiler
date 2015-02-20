package compiler.components.parser;

import java.util.ArrayList;
import java.util.List;

/**
 * Class used to represent variables that are defined in the program and stored 
 * in the variable tables. 
 * 
 * @author jpalomo 
 */
public class Variable {

    public enum VarType {
        VAR, ARRAY, SSA
    }
    
    boolean isVar = false;
    boolean isArray = false;
    Integer ssaIndex;
    String varIdentifier;
    List<Integer> arrayDimSize;

    public int previousSSAIndex;

    public Variable(String varIdentifier, List<Integer> arrayDimSize, VarType type) {
        this.varIdentifier = varIdentifier;
        this.arrayDimSize = arrayDimSize;
        if(type.equals(VarType.VAR)){
        	isVar = true;
        	ssaIndex = 0;
        }
        else if(type.equals(VarType.ARRAY)) {
        	isArray = true;
        }
    }

    private Variable() {
    	
    }

    public String getVarIdentifier() {
        return varIdentifier;
    }
    
    public List<Integer> getArrayDimSize() {
        return arrayDimSize;
    }

    public int getssaIndex() {
        return ssaIndex;
    }
    
    public boolean isVar() {
        return isVar;
    }

    public boolean isArray() {
        return isArray;
    }

    public String getAsSSAVar(){
    	if(isArray) {
    		return varIdentifier;
    	}
		return varIdentifier + "_" + ssaIndex;
    }

    public String getPreviousSSAVar(){
    	if(isArray) {
    		return varIdentifier;
    	}
		return varIdentifier + "_" + previousSSAIndex;
    }

    public String toString() {
    	if(isVar) {
    		return getAsSSAVar();
    	}
        return varIdentifier;
    }

    public int getArraySize() {
        int totalDimSize = 0;
        if(arrayDimSize.size() > 0) {
            totalDimSize = 1;
            for(Integer dim: arrayDimSize) { 
                totalDimSize *= dim;
            }
        }
        return totalDimSize;
    }

    public static Variable clone(Variable v) {
    	Variable var = new Variable();
    	var.arrayDimSize = new ArrayList<Integer>(v.arrayDimSize);
    	var.isArray = new Boolean(v.isArray);
    	var.isVar = new Boolean(v.isVar);
    	var.previousSSAIndex = v.previousSSAIndex;
    	if(v.isVar) {
    		var.ssaIndex = new Integer(v.ssaIndex);
    	}
    	var.varIdentifier = new String(v.varIdentifier);
    	return var;
    }
}