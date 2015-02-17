package compiler.components.intermediate_rep;

import java.util.ArrayList;
import java.util.List;

public class Result {
	public ResultEnum type = null;
	public Integer constValue = null;
	public Integer fixUp = null;
	public String varValue = "";
	public String conditionValue = null;  //TODO what will this get set to?
	public Integer instrNum = null;
	public String funcName = null;

	public List<Result> arrayExprs = null;

	public static final Result EMPTY_RESULT = new Result(ResultEnum.EMPTY);
	
	public enum ResultEnum {
		FUNCTION(), CONSTANT(),  VARIABLE(), CONDITION(), INSTR(), EMPTY();
	} 

	public Result(ResultEnum type){
		this.type = type;
		arrayExprs = new ArrayList<Result>();
	}

	public Result() {
	   this(ResultEnum.EMPTY); 
	}

	public String getVarNameWithoutIndex() {
		return varValue.split("_")[0];
	}

	public int getVariableIndex() {
		return Integer.valueOf(varValue.split("_")[1]);
	}

	public String toString(){
		if(type.equals(ResultEnum.CONSTANT)) {
			return "#" + constValue.toString();
		}
		if(type.equals(ResultEnum.VARIABLE)) {
			return varValue;
		}
		if(type.equals(ResultEnum.INSTR)) {
			return "(" + instrNum.toString() + ")";
		}
		if(type.equals(ResultEnum.CONDITION)) {
			return conditionValue;
		}
		if(type.equals(ResultEnum.FUNCTION)) {
			return funcName;
		}
		if(type.equals(ResultEnum.EMPTY)) {
			return "";
		}
		else
			return super.toString();
	}

}
