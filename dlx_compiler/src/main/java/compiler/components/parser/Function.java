package compiler.components.parser;

import java.util.List;

import compiler.components.intermeditate_rep.Result;

public class Function extends Result{

	String funcName;
	List<String> params;
	
	public Function(String funcName, List<String> params){
		super(ResultEnum.FUNCTION);
		this.funcName = funcName;
		this.params = params; 
	}

	public String toString() {
		return funcName;
	}
}
