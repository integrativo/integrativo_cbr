package br.ufpe.cin.integrativocbr;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import jcolibri.casebase.LinealCaseBase;
import jcolibri.cbraplications.StandardCBRApplication;
import jcolibri.cbrcore.Attribute;
import jcolibri.cbrcore.CBRCaseBase;
import jcolibri.cbrcore.CBRQuery;
import jcolibri.exception.ExecutionException;
import jcolibri.exception.OntologyAccessException;
import jcolibri.method.retrieve.RetrievalResult;
import jcolibri.method.retrieve.NNretrieval.NNConfig;
import jcolibri.method.retrieve.NNretrieval.NNScoringMethod;
import jcolibri.method.retrieve.NNretrieval.similarity.global.Average;
import jcolibri.method.retrieve.NNretrieval.similarity.local.MaxString;
import jcolibri.method.retrieve.selection.SelectCases;
import jcolibri.util.OntoBridgeSingleton;

import org.json.JSONException;

import es.ucm.fdi.gaia.ontobridge.OntoBridge;
import es.ucm.fdi.gaia.ontobridge.OntologyDocument;

public class IntegrativoCBRApplication implements StandardCBRApplication {

	private CBRCaseBase caseBase;
	private Map<String, SimilarityResult> similarityResult;
	private GryphonConnector currentConnector;
	private CycleResult cycleResult;
	
	@Override
	public void configure() throws ExecutionException {
		similarityResult = new HashMap<String, SimilarityResult>();
	}

	@Override
	public CBRCaseBase preCycle() throws ExecutionException {
		caseBase = new LinealCaseBase();
		caseBase.init(currentConnector);
		
//		for (CBRCase c : caseBase.getCases()) {
//			System.out.println(c);
//		}
		return caseBase;
	}

	@Override
	public void cycle(CBRQuery query) throws ExecutionException {
		NNConfig simConfig = new NNConfig();
		simConfig.setDescriptionSimFunction(new Average());
		simConfig.addMapping(new Attribute("label", CaseDescription.class), new MaxString());
		
		Collection<RetrievalResult> results = NNScoringMethod.evaluateSimilarity(caseBase.getCases(), query, simConfig);
		if (results.isEmpty()) {
			System.out.println("No results for: " + query);
		} else {
			RetrievalResult result = SelectCases.selectTopKRR(results, 1).iterator().next();
			CaseDescription caseDescription = (CaseDescription) result.get_case().getDescription();
			
			cycleResult = new CycleResult(result.getEval(), caseDescription.getClassId());
		}
	}

	@Override
	public void postCycle() throws ExecutionException {
	}

	protected OntoBridge configureOntoBridge() {
		String basePath = IntegrativoCBRApplication.class.getResource("/mscExperiment").getFile() + File.separator;
		String baseSourcesPath = basePath + "sources/";
		
		OntoBridge ob = OntoBridgeSingleton.getOntoBridge();
		ob.initWithOutReasoner();
		
		OntologyDocument mainOnto = new OntologyDocument(basePath + "TesteGryphon.owl");
		List<OntologyDocument> subOntologies = new ArrayList<OntologyDocument>();
		subOntologies.add(new OntologyDocument(baseSourcesPath + "btl2.owl"));
		subOntologies.add(new OntologyDocument(baseSourcesPath + "chebi_homocysteine.owl"));
		subOntologies.add(new OntologyDocument(baseSourcesPath + "DiseaseList.owl"));
		subOntologies.add(new OntologyDocument(baseSourcesPath + "go_module.owl"));
		subOntologies.add(new OntologyDocument(baseSourcesPath + "ncbitaxon.owl"));
		subOntologies.add(new OntologyDocument(baseSourcesPath + "PR_protein.owl"));
		
		ob.loadOntology(mainOnto, subOntologies, true);
		return ob;
	}
	
	public Map<String, SimilarityResult> getSimilarityResult() {
		return similarityResult;
	}
	
	public static void executeCBR(String... classes) throws Exception {
		IntegrativoCBRApplication app = new IntegrativoCBRApplication();
		app.configureOntoBridge();
		app.configure();
		
		GryphonConnector[] connectors = new GryphonConnector[classes.length];
		for (int i = 0; i < classes.length; i++) {
			System.out.println(">>>> Initializing connector for class: " + classes[i]);
			connectors[i] = new GryphonConnector(classes[i]);
		}
		
		CBRQuery query;
		for (GryphonResult gryphonResult : GryphonResultUtil.readResults()) {

			CycleResult[] cycleResults = new CycleResult[connectors.length];
			for (int i = 0; i < connectors.length; i++) {
				query = new CBRQuery();
				query.setDescription(new CaseDescription(null, gryphonResult.getTuples()[i]));
				
				app.setCurrentConnector(connectors[i]);
				app.preCycle();
				app.cycle(query);
				app.postCycle();
				
				cycleResults[i] = app.getCycleResult();
			}

			// Average
			// =======
			
			char classLetter = 'A';
			double evalResultSum = 0;
			
			System.out.println(">> " + gryphonResult);
			for (CycleResult cycleResult : cycleResults) {
				System.out.printf("   >> class%c = %s\n", classLetter, cycleResult.getClassId());
				System.out.printf("   >> evalResult%c = %s\n", classLetter, cycleResult.getEvalResult());
				classLetter++;
				evalResultSum += cycleResult.getEvalResult();
			}
			System.out.println("   >> average = " + evalResultSum / cycleResults.length);
		}
	}

	public static void main(String[] args) throws Exception {
		// Q1 classes
//		IntegrativoCBRApplication.executeCBR(
//				"http://purl.org/biotop/btl2.owl#MonoMolecularEntity",
//				"http://purl.org/biotop/btl2.owl#Organism");
		
		// Q2 classes
		IntegrativoCBRApplication.executeCBR(
				"http://purl.obolibrary.org/obo/GO_0008150",
				"http://purl.org/biotop/btl2.owl#Organism");
		
		// Q3 classes
//		IntegrativoCBRApplication.executeCBR(
//				"http://purl.obolibrary.org/obo/GO_0008150",
//				"http://purl.obolibrary.org/obo/GO_0005575");
		
		// Q4 classes
//		IntegrativoCBRApplication.executeCBR(
//				"http://purl.obolibrary.org/obo/GO_0008150",
//				"http://purl.obolibrary.org/obo/PR_000000001");

		// Q5 classes
//		IntegrativoCBRApplication.executeCBR(
//				"http://purl.obolibrary.org/obo/GO_0008150",
//				"http://purl.obolibrary.org/obo/PR_000000001");
	}
	
	private void setCurrentConnector(GryphonConnector connector) {
		this.currentConnector = connector;
	}
	
	private class CycleResult {
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
	}
	
	public CycleResult getCycleResult() {
		return cycleResult;
	}
}
