package br.ufpe.cin.integrativocbr;

import jcolibri.cbrcore.Attribute;
import jcolibri.cbrcore.CaseComponent;

public class CaseDescription implements CaseComponent {

	private String classId;
	private String label;
	
	public CaseDescription(String classId) {
		this.classId = classId;
	}
	
	public CaseDescription(String classId, String label) {
		this.classId = classId;
		this.label = label;
	}

	@Override
	public Attribute getIdAttribute() {
		return new Attribute("classId", getClass());
	}

	public String getCaseId() {
		return classId;
	}

	public void setCaseId(String classId) {
		this.classId = classId;
	}

	public String getClassId() {
		return classId;
	}

	public void setClassId(String classId) {
		this.classId = classId;
	}
	
	public String getLabel() {
		return label;
	}

	public void setLabel(String label) {
		this.label = label;
	}

	@Override
	public String toString() {
		return "(" + classId + " - " + label + ")"; 
	}
}