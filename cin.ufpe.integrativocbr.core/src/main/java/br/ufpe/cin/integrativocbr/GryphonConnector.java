package br.ufpe.cin.integrativocbr;

import java.net.URL;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import jcolibri.cbrcore.CBRCase;
import jcolibri.cbrcore.CaseBaseFilter;
import jcolibri.cbrcore.Connector;
import jcolibri.exception.InitializingException;
import jcolibri.util.OntoBridgeSingleton;

import com.hp.hpl.jena.ontology.OntClass;

import es.ucm.fdi.gaia.ontobridge.OntoBridge;

public class GryphonConnector implements Connector {

	private Set<CBRCase> cbrCaseList = new HashSet<>();
	private Set<String> subClassesSet = new HashSet<>();
	private StringBuilder indentation = new StringBuilder();
	
	public GryphonConnector(String klass) {
		readSubClasses(klass);
	}
	
	private void readSubClasses(String klass) {
		if (subClassesSet.contains(klass)) {
			System.out.println(indentation + "Cycle detected! Skipping...");
			return;
		}
		
		OntoBridge ob = OntoBridgeSingleton.getOntoBridge();
		Iterator<String> subClassesIterator = ob.listSubClasses(klass, false);
		while (subClassesIterator.hasNext()) {
			
			String subClass = subClassesIterator.next();
			OntClass ontClass = ob.getModel().getOntClass(subClass);
			String label = ontClass.getLabel(null);
			System.out.println(indentation + ">> Subclass: " + subClass + " (" + label + ")");
			
			CBRCase cbrCase = new CBRCase();
			cbrCase.setDescription(new CaseDescription(subClass, label));
			cbrCaseList.add(cbrCase);
			
			System.out.println(indentation + ">> Entering subclasses");
			indentation.append("   ");
			readSubClasses(subClass);
			indentation.delete(0, 3);
			subClassesSet.add(subClass);
		}
	}


	@Override
	public void close() {
		cbrCaseList = null;
	}

	@Override
	public void deleteCases(Collection<CBRCase> cases) {
		cbrCaseList.removeAll(cases);
	}

	@Override
	public void initFromXMLfile(URL url) throws InitializingException {
		
	}

	@Override
	public Collection<CBRCase> retrieveAllCases() {
		return cbrCaseList;
	}

	@Override
	public Collection<CBRCase> retrieveSomeCases(CaseBaseFilter arg0) {
		return null;
	}

	@Override
	public void storeCases(Collection<CBRCase> arg0) {
	}

}
