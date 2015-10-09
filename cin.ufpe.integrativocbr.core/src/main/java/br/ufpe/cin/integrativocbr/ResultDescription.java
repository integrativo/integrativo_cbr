package br.ufpe.cin.integrativocbr;

import jcolibri.cbrcore.Attribute;
import jcolibri.cbrcore.CaseComponent;
import jcolibri.datatypes.Instance;
import jcolibri.exception.OntologyAccessException;

public class ResultDescription implements CaseComponent {

	private String classId;
	private Instance labelForCosine;
	private Instance labelForDeep;
	private Instance labelForDeepBasic;
	private Instance labelForDetail;
	
	public ResultDescription(String classId) {
		this.classId = classId;
	}
	
	public ResultDescription(String classId, String label) throws OntologyAccessException {
		this.classId = classId;
		this.labelForCosine = new Instance(label);
		this.labelForDeep = new Instance(label);
		this.labelForDeepBasic = new Instance(label);
		this.labelForDetail = new Instance(label);
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

	public Instance getLabelForCosine() {
		return labelForCosine;
	}

	public void setLabelForCosine(Instance labelForCosine) {
		this.labelForCosine = labelForCosine;
	}

	public Instance getLabelForDeep() {
		return labelForDeep;
	}

	public void setLabelForDeep(Instance labelForDeep) {
		this.labelForDeep = labelForDeep;
	}

	public Instance getLabelForDeepBasic() {
		return labelForDeepBasic;
	}

	public void setLabelForDeepBasic(Instance labelForDeepBasic) {
		this.labelForDeepBasic = labelForDeepBasic;
	}

	public Instance getLabelForDetail() {
		return labelForDetail;
	}

	public void setLabelForDetail(Instance labelForDetail) {
		this.labelForDetail = labelForDetail;
	}

	@Override
	public String toString() {
		return "(" + classId + " - " + labelForCosine + ")"; 
	}
}