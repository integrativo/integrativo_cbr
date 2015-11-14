package br.ufpe.cin.integrativocbr;

public class GryphonResult {

	private String p1;
	private String p2;
	
	public GryphonResult() {
	}
	
	public GryphonResult(String p1, String p2) {
		super();
		this.p1 = p1;
		this.p2 = p2;
	}

	public String getP1() {
		return p1;
	}

	public void setP1(String p1) {
		this.p1 = p1;
	}

	public String getP2() {
		return p2;
	}

	public void setP2(String p2) {
		this.p2 = p2;
	}

	@Override
	public String toString() {
		return "GryphonResult [p1=" + p1 + ", p2=" + p2 + "]";
	}
	
}
