package compiler.components.parser;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import compiler.components.intermediate_rep.BasicBlock;
import compiler.components.intermediate_rep.Result;
import compiler.components.parser.Variable.VarType;

public class Function extends Result{

	public String funcName;
	public List<String> params;
	public Map<String, Variable> localSymbols;
	public BasicBlock beginBlockForFunction;
	public boolean hasBlocks = false;
	
	public Function(String funcName, List<String> params, Map<String,Variable> outerScopeSymbols, BasicBlock beginBlock){
		super(ResultEnum.FUNCTION);
		this.funcName = funcName;
		this.params = params; 
		this.localSymbols = new HashMap<String, Variable>(outerScopeSymbols);
		this.beginBlockForFunction = beginBlock;
		addParamsToLocalSymbols(params);
		this.hasBlocks = true;
	}

	public Function(String funcName, List<String> params, Map<String,Variable> outerScopeSymbols){
		super(ResultEnum.FUNCTION);
		this.funcName = funcName;
		this.params = params; 
		this.localSymbols = new HashMap<String, Variable>(outerScopeSymbols);
		addParamsToLocalSymbols(params);
	}

	public String toString() {
		return funcName;
	}

	public void addParamsToLocalSymbols(List<String> params) {
	    List<Integer> arrayDims = new ArrayList<Integer>();

	    for(String param: params) {
	        Variable var = new Variable(param, arrayDims,VarType.VAR);
	        localSymbols.put(param, var);
	    }
	}
}
