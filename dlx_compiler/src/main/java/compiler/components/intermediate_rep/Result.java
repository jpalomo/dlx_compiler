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
	public Integer registerNum = 1;
	public Integer dlxInsNum = null;

	public List<Result> arrayExprs = null;

	public static final Result EMPTY_RESULT = new Result(ResultEnum.EMPTY);
	
	public enum ResultEnum {
		FUNCTION(), CONSTANT(),  VARIABLE(), CONDITION(), INSTR(), EMPTY(), REGISTER();
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
		if(type.equals(ResultEnum.REGISTER)) {
			return "R" + registerNum.toString();
		}
		else
			return super.toString();
	}

	public static Result clone(Result thingToClone) {
		Result r = new Result();
		r.type = thingToClone.type;
		if (thingToClone.constValue != null) {
			r.constValue = new Integer(thingToClone.constValue);
		}
		if (thingToClone.fixUp != null ) {
			r.fixUp = new Integer(thingToClone.fixUp);
		}
		
		r.varValue = new String(thingToClone.varValue);

		r.conditionValue = thingToClone.conditionValue;  //TODO what will thingToClone get set to?
		if (thingToClone.instrNum != null) {
			r.instrNum = new Integer(thingToClone.instrNum);
		}
		r.funcName = thingToClone.funcName;
		
		return r;
	}

	@Override
	public boolean equals(Object obj) {
		if(obj instanceof Result) {
			Result r = (Result) obj;
			if(r.type.equals(this.type)) {
				if(this.type.equals(ResultEnum.EMPTY)) {
					return true;
				}
				if(type.equals(ResultEnum.INSTR)) {
					return this.instrNum.intValue() == r.instrNum.intValue();
				}
				else if(type.equals(ResultEnum.VARIABLE)) {
					return this.varValue.equals(r.varValue);
				}
				else if(type.equals(ResultEnum.CONSTANT)) {
					return this.constValue.intValue() == r.constValue.intValue();
				}
			}
		}
		return false;
	}
}
