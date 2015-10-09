package br.ufpe.cin.integrativocbr;

public class SimilarityResult {

	private double eval1;
	private double eval2;
	private double average;
	private String label1;
	private String label2;

	public SimilarityResult(double eval1, double eval2, double average, String label1, String label2) {
		this.eval1 = eval1;
		this.eval2 = eval2;
		this.average = average;
		this.label1 = label1;
		this.label2 = label2;
	}

	
	public double getAverage() {
		return average;
	}

	public void setAverage(double average) {
		this.average = average;
	}

	public double getEval1() {
		return eval1;
	}

	public void setEval1(double eval1) {
		this.eval1 = eval1;
	}

	public double getEval2() {
		return eval2;
	}

	public void setEval2(double eval2) {
		this.eval2 = eval2;
	}

	public String getLabel1() {
		return label1;
	}

	public void setLabel1(String label1) {
		this.label1 = label1;
	}
	
	public String getLabel2() {
		return label2;
	}
	
	public void setLabel2(String label2) {
		this.label2 = label2;
	}
}
