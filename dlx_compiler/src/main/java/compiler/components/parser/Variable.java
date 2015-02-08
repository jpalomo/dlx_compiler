package compiler.components.parser;

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
    int ssaIndex;
    String varIdentifier;
    List<Integer> arrayDimSize;;

    public Variable(String varIdentifier, List<Integer> arrayDimSize, VarType type) {
        this.varIdentifier = varIdentifier;
        this.arrayDimSize = arrayDimSize;
        if(type.equals(VarType.VAR)){
        	isVar = true;
        }
        else if(type.equals(VarType.ARRAY)) {
        	isArray = true;
        }
    }

    public Variable(String varIdentifier, List<Integer> arrayDimSize, VarType type, int index) {
        this.varIdentifier = varIdentifier;
        this.arrayDimSize = arrayDimSize;
        if(type.equals(VarType.VAR)){
        	isVar = true;
        }
        else if(type.equals(VarType.ARRAY)) {
        	isArray = true;
        }
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

    public String toString() {
        return varIdentifier;
    }
}