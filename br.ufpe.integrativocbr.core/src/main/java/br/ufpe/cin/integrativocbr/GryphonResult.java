package br.ufpe.cin.integrativocbr;

import java.util.Arrays;


public class GryphonResult {

	private String[] tuples;
	
	public GryphonResult() {
	}
	
	public GryphonResult(String... tuples) {
		super();
		this.tuples = tuples;
	}

	public String[] getTuples() {
		return tuples;
	}

	public void setTuples(String[] tuples) {
		this.tuples = tuples;
	}

	@Override
	public String toString() {
		return "GryphonResult " + Arrays.asList(tuples);
	}
	
}
