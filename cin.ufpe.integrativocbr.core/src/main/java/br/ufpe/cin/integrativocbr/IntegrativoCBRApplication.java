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
import jcolibri.cbrcore.Connector;
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

import br.ufpe.cin.integrativocbr.CaseDescription;
import br.ufpe.cin.integrativocbr.GryphonConnector;
import br.ufpe.cin.integrativocbr.GryphonResult;
import br.ufpe.cin.integrativocbr.GryphonResultUtil;
import br.ufpe.cin.integrativocbr.SimilarityResult;
import es.ucm.fdi.gaia.ontobridge.OntoBridge;
import es.ucm.fdi.gaia.ontobridge.OntologyDocument;

public class IntegrativoCBRApplication implements StandardCBRApplication {

	private static Connector connector;
	
	private CBRCaseBase caseBase;
	private Map<String, SimilarityResult> similarityResult;
	
	private static Double evalResult;
	private static String classResult;
	
	@Override
	public void configure() throws ExecutionException {
		similarityResult = new HashMap<String, SimilarityResult>();
	}

	@Override
	public CBRCaseBase preCycle() throws ExecutionException {
		caseBase = new LinealCaseBase();
		caseBase.init(connector);
		
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
			evalResult = result.getEval();
			classResult = caseDescription.getClassId();
		}
	}

	@Override
	public void postCycle() throws ExecutionException {
	}

	private static OntoBridge configureOntoBridge() {
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
	
	
	public static void main(String[] args) throws OntologyAccessException, ExecutionException, JSONException, IOException {
		configureOntoBridge();
		
		IntegrativoCBRApplication app = new IntegrativoCBRApplication();
		app.configure();
		
		
//		Q1
//		===
//		FIXME old code
//		System.out.println(">>>> MONOMOLECULARENTITY TREE");
//		doCycle(app, "http://purl.org/biotop/btl2.owl#MonoMolecularEntity");
//		System.out.println(">>>> ORGANISM TREE");
//		doCycle(app, "http://purl.org/biotop/btl2.owl#Organism");
		
		// Q2
		// ===
		System.out.println(">>>> GO_0008150 TREE");
		GryphonConnector connectorA = new GryphonConnector("http://purl.obolibrary.org/obo/GO_0008150");
		System.out.println(">>>> ORGANISM TREE");
		GryphonConnector connectorB = new GryphonConnector("http://purl.org/biotop/btl2.owl#Organism");
		
		// Q3
		// ===
//		System.out.println(">>>> GO_0008150 TREE");
//		GryphonConnector connectorA = new GryphonConnector("http://purl.obolibrary.org/obo/GO_0008150");
//		System.out.println(">>>> GO_0005575 TREE");
//		GryphonConnector connectorB = new GryphonConnector("http://purl.obolibrary.org/obo/GO_0005575");
		
		// Q4
		// ===
//		System.out.println(">>>> GO_0008150 TREE");
//		GryphonConnector connectorA = new GryphonConnector("http://purl.obolibrary.org/obo/GO_0008150");
//		System.out.println(">>>> PR_000000001 TREE");
//		GryphonConnector connectorB = new GryphonConnector("http://purl.obolibrary.org/obo/PR_000000001");
		
		CBRQuery query;
		
		for (GryphonResult gryphonResult : GryphonResultUtil.readResults()) {
			// A
			// ===
			connector = connectorA;
			
			query = new CBRQuery();
			query.setDescription(new CaseDescription(null, gryphonResult.getP1()));
			
			app.preCycle();
			app.cycle(query);
			app.postCycle();
			
			double evalResultA = evalResult;
			String classResultA = classResult;
			
			// B
			// ===
			connector = connectorB;
			
			query = new CBRQuery();
			query.setDescription(new CaseDescription(null, gryphonResult.getP2()));
			
			app.preCycle();
			app.cycle(query);
			app.postCycle();
			
			double evalResultB = evalResult;
			String classResultB = classResult;

			// Average
			// =======
			
			System.out.println(">> " + gryphonResult);
			System.out.println("   >> classA = " + classResultA);
			System.out.println("   >> evalA = " + evalResultA);
			System.out.println("   >> classB = " + classResultB);
			System.out.println("   >> evalB = " + evalResultB);
			System.out.println("   >> average= " + (evalResultA + evalResultB) / 2);
			
		}
		
	}
}
