package compiler.components.intermeditate_rep;

import com.google.common.base.Preconditions;

public class Instruction {
	public int instructionNum;
	public String operand1;
	public String operator;
	public String operand2;
	public String orignalPhiVar;

	private Instruction(int instructionNum, String operator, String operand1, String operand2, boolean isPhi, String orignalPhiVar) {
		this.instructionNum = instructionNum;
		this.operator = operator;
		this.operand1 = operand1;
		this.operand2 = operand2;
		this.orignalPhiVar = orignalPhiVar;
	}

	public static InstructionBuilder builder(int instructionNum) {
		return new InstructionBuilder(instructionNum);
	}

	/**
	 * Returns a formatted string of the instruction: format = 1d:%6s %4s %4s
	 */
	public String toString() {
		String s;

		s = String.format("%-1d: %-6s %4s %4s", instructionNum, operator, operand1, operand2);
		

		return s;
	}

	public static class InstructionBuilder {
		private int instructionNum;
		private String operand1;
		private String operator;
		private String operand2;
		private String orignalPhiVar;

		private InstructionBuilder(int instructionNum){
			this.instructionNum = instructionNum;
		}

		public InstructionBuilder operator(String operator){
			this.operator = operator;
			return this;
		}

		public InstructionBuilder op1(String operand1){
			this.operand1 = operand1;
			return this;
		}

		public InstructionBuilder op2(String operand2){
			this.operand2 = operand2;
			return this; 
		}

		public InstructionBuilder orignalPhiVar(String orignalPhiVar){
			this.orignalPhiVar = orignalPhiVar;
			return this; 
		}
		/** builds a general 'op x y' instruction */
		public Instruction build() {
			Preconditions.checkNotNull(operator, "operator must not be null");
			return new Instruction(instructionNum, operator, operand1, operand2, false, null);
		}

		/** builds a Phi 'PHI(x, y)' instruction */
		public Instruction buildPhi(){
			Preconditions.checkNotNull(orignalPhiVar, "the original vairable name must not be null");
			this.operator = "PHI";
			if(operand2 == null){
				operand2 = orignalPhiVar;
			}
			return new Instruction(instructionNum, operator, operand1, operand2, true, orignalPhiVar); 
		}
	}
}