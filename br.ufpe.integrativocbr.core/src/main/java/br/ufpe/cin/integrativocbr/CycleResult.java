package br.ufpe.cin.integrativocbr;

public class CycleResult {
	private double evalResult;
	private String classId;

	public CycleResult(double evalResult, String classId) {
		this.evalResult = evalResult;
		this.classId = classId;
	}

	public double getEvalResult() {
		return evalResult;
	}

	public String getClassId() {
		return classId;
	}

	@Override
	public String toString() {
		return "CycleResult [evalResult=" + evalResult + ", classId=" + classId
				+ "]";
	}
}
