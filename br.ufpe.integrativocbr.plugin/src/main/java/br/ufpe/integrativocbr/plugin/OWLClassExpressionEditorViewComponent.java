package br.ufpe.integrativocbr.plugin;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JSplitPane;
import javax.swing.SwingWorker;

import org.aksw.owl2sparql.OWLClassExpressionToSPARQLConverter;
import org.protege.editor.owl.ui.clsdescriptioneditor.ExpressionEditor;
import org.protege.editor.owl.ui.clsdescriptioneditor.OWLExpressionChecker;
import org.protege.editor.owl.ui.view.AbstractOWLViewComponent;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLClassExpression;
import org.semanticweb.owlapi.model.OWLException;
import org.semanticweb.owlapi.model.OWLObjectIntersectionOf;
import org.semanticweb.owlapi.model.OWLObjectProperty;
import org.semanticweb.owlapi.model.OWLObjectSomeValuesFrom;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.util.OWLClassExpressionVisitorAdapter;

import br.ufpe.cin.aac3.gryphon.Gryphon;
import br.ufpe.cin.aac3.gryphon.Gryphon.ResultFormat;
import br.ufpe.cin.aac3.gryphon.GryphonConfig;
import br.ufpe.cin.aac3.gryphon.model.Database;
import br.ufpe.cin.aac3.gryphon.model.Ontology;
import br.ufpe.cin.integrativocbr.CycleResult;
import br.ufpe.cin.integrativocbr.IntegrativoCBRApplication;
import br.ufpe.cin.integrativocbr.event.CBREventListener;

public class OWLClassExpressionEditorViewComponent extends AbstractOWLViewComponent {

	private static final long serialVersionUID = 1L;
	private static final String MAPPING_FILE = "db_localhost_3306_uniprot.ttl";

	private ExpressionEditor<OWLClassExpression> expressionEditor;

	@Override
	protected void initialiseOWLView() throws Exception {
		setLayout(new BorderLayout(10, 10));
		
		JSplitPane splitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT, 
				createQueryPanel(), createResultsPanel());
		splitPane.setDividerLocation(0.3);
		add(splitPane, BorderLayout.CENTER);
	}

	private JPanel createResultsPanel() {
		JPanel resultsPanel = new JPanel();
		resultsPanel.add(new JLabel("Results"));
		return resultsPanel;
	}
	
	private JPanel createButtonsPanel() {
		JButton testCBRButton = new JButton();
		testCBRButton.setText("Teste CBR");
		testCBRButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				testCBRButtonAction();
			}
		});
		
		JButton testSparqlConversionButton = new JButton();
		testSparqlConversionButton.setText("Test Sparql Conversion");
		testSparqlConversionButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				testSparqlConversionButtonAction();
			}
		});
		
		JButton testGryphonButton = new JButton();
		testGryphonButton.setText("Teste Gryphon");
		testGryphonButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				testGryphonButtonAction();
			}
		});

		JButton testOntologiesButton = new JButton();
		testOntologiesButton.setText("Teste Ontologies");
		testOntologiesButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				testOntologiesButtonAction();
			}
		});

		JButton testGryphonQueryButton = new JButton();
		testGryphonQueryButton.setText("Teste Consulta Gryphon");
		testGryphonQueryButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				testGryphonQueryButtonAction();
			}
		});
		
		JButton testAxiomsButton = new JButton();
		testAxiomsButton.setText("Teste Axiomas");
		testAxiomsButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				testAxiomsButtonAction();			
			}
		});
	
		JButton testClassesInMappingButton = new JButton();
		testClassesInMappingButton.setText("Classes no Mapeamento");
		testClassesInMappingButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				testClassesInMappingButtonAction();
			}
		});
		
		JPanel buttonsPanel = new JPanel();
		buttonsPanel.setLayout(new FlowLayout());
		buttonsPanel.add(testClassesInMappingButton);
		buttonsPanel.add(testAxiomsButton);
		buttonsPanel.add(testCBRButton);
		buttonsPanel.add(testGryphonButton);
		buttonsPanel.add(testSparqlConversionButton);
		buttonsPanel.add(testOntologiesButton);
		buttonsPanel.add(testGryphonQueryButton);
		return buttonsPanel;
	}
	
	private Set<String> readClassesInMapping(File mappingFile) {
		Set<String> result = new HashSet<String>();
		
		try {
			BufferedReader reader = new BufferedReader(new FileReader(mappingFile));
			
			// d2rq:class <http://purl.obolibrary.org/obo/GO_0008150>;
			Pattern pattern = Pattern.compile("\\s+d2rq\\:class\\s+<(.+?)>;");
			String line;
			
			while ((line = reader.readLine()) != null) {
				Matcher matcher = pattern.matcher(line);
				if (matcher.find()) {
					result.add(matcher.group(1));
				}
			}
			
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		return result;
	}
	
	private void testClassesInMappingButtonAction() {
		new ProgressDialogWorker(null) {
			
			private Set<String> classesInMapping;
			
			@Override
			protected Object doInBackground() throws Exception {
				initGryphon();
				File mappingFile = new File(Gryphon.getMapFolder() + "/" + MAPPING_FILE); 
				
				if (mappingFile.exists()) {
					publish("Mapping file found in: " + mappingFile.getAbsolutePath());
				} else {
					publish("Mapping file NOT found in: " + mappingFile.getAbsolutePath());
				}
				publish("");
				classesInMapping = readClassesInMapping(mappingFile);
				publish("Classes in Mapping: " + classesInMapping);
				
				List<OWLClass> owlClassList = new ArrayList<OWLClass>();
				publish("");
				OWLClassExpression classExpression = expressionEditor.createObject();
				for (OWLClass owlClass : classExpression.getClassesInSignature()) {
					publish("Searching mapping for class: " + owlClass.getIRI());
					OWLClass classInMapping = searchClassInMapping(owlClass);
					publish("Class in mapping: " + classInMapping);
					if (classInMapping == null) {
						publish("Class " + owlClass.toString() + " NOT found in mapping! Exiting.");
						return null;
					}
					owlClassList.add(classInMapping);
				}
				
				publish("\nFound these classes on mapping: " + owlClassList);
				publish("\nInitializing CBR...");
				IntegrativoCBRApplication.executeCBR(new CBREventListener() {

					@Override
					public void beforeCreateGryphonConnector(String classIRI) {
						publish("Creating connector: " + classIRI);
					}

					@Override
					public void beforeCycle(String classIRI) {
						publish("Cycle: " + classIRI);
					}

					@Override
					public void onResultCycle(CycleResult cycleResult) {
						publish("Result: " + cycleResult);
					}
					
				}, classesInMapping.toArray(new String[classesInMapping.size()]));

				return null;
			}
			
			private OWLClass searchClassInMapping(OWLClass owlClass) {
				if (classesInMapping.contains(owlClass.toStringID())) {
					publish("Mapping found for: " + owlClass.toStringID());
					return owlClass;
				} else {
					publish("Mapping NOT found for: " + owlClass.toStringID() + ". Searching superclass...");
					for (OWLClassExpression owlClassExpression : owlClass.getSuperClasses(getOWLModelManager().getActiveOntology())) {
						OWLClass owlClassAux = searchClassInMapping(owlClassExpression.asOWLClass());
						if (owlClassAux != null) {
							return owlClassAux;
						}
					}
					return null;
				}
			}
			
		}.execute();
	}
	
	private void testAxiomsButtonAction() {
		try {
			OWLClassExpression classExpression = expressionEditor.createObject();
			final List<OWLClass> owlClasses = new ArrayList<OWLClass>();
			final List<NewAxiom> newAxioms = new ArrayList<NewAxiom>();
			
			classExpression.getNNF().accept(new OWLClassExpressionVisitorAdapter() {
				@Override
				public void visit(OWLObjectIntersectionOf intersection) {
					for (OWLClassExpression expr : intersection.getOperands()) {
						if (expr instanceof OWLClass) {
							owlClasses.add((OWLClass) expr);
						}
					}
					for (OWLClassExpression expr : intersection.getOperands()) {
						if (expr instanceof OWLObjectSomeValuesFrom) {
							OWLObjectSomeValuesFrom some = (OWLObjectSomeValuesFrom) expr;
							for (OWLObjectProperty objectProperty : some.getObjectPropertiesInSignature()) {
								for (OWLClass owlClass2 : some.getClassesInSignature()) {
									for (OWLClass owlClass1 : owlClasses) {
										newAxioms.add(new NewAxiom(owlClass1, objectProperty, owlClass2, getOWLModelManager().getActiveOntology()));
									}
								}
							}
						}
					}
				}
			});
			
			System.out.println("-----------");
			for (NewAxiom newAxiom : newAxioms) {
				System.out.println(newAxiom);
			}
			System.out.println("-----------");
		} catch (OWLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	private void testCBRButtonAction() {
		try {
			OWLClassExpression classExpression = expressionEditor.createObject();
			Set<OWLClass> classes = classExpression.getClassesInSignature();
			final String[] classesIRI = new String[classes.size()];
			int index = 0;
			Iterator<OWLClass> iterator = classes.iterator();
			
			while (iterator.hasNext()) {
				classesIRI[index] = iterator.next().getIRI().toString();
				index++;
			}
			final ProgressDialog progressDialog = new ProgressDialog(null);
			progressDialog.setVisible(true);
			
			new SwingWorker<Object, String>() {

				@Override
				protected Object doInBackground() throws Exception {
					IntegrativoCBRApplication.executeCBR(new CBREventListener() {

						@Override
						public void beforeCreateGryphonConnector(String classIRI) {
							publish("Creating connector: " + classIRI);
						}

						@Override
						public void beforeCycle(String classIRI) {
							publish("Cycle: " + classIRI);
						}

						@Override
						public void onResultCycle(CycleResult cycleResult) {
							publish("Result: " + cycleResult);
						}
						
					}, classesIRI);
					return null;
				}
				
				protected void process(java.util.List<String> chunks) {
					for (String chunk : chunks) {
						progressDialog.setVisible(true);
						progressDialog.appendText(chunk);
					}
				}
				
				@Override
				protected void done() {
					progressDialog.dispose();
				}
			}.execute();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	private void testGryphonButtonAction() {
		System.out.println("*** INIT Test Gryphon");
		GryphonConfig.setWorkingDirectory(new File("integrationExample"));
		GryphonConfig.setLogEnabled(true);
		GryphonConfig.setShowLogo(true);
		Gryphon.init();
		System.out.println("*** END Test Gryphon");
	}

	private void testSparqlConversionButtonAction() {
		try {
			String sparqlQuery = convertToSparqlQuery();
			System.out.println(sparqlQuery);
			JOptionPane.showMessageDialog(this, sparqlQuery);
		} catch (Exception e) {
			e.printStackTrace();
			JOptionPane.showMessageDialog(this, "Erro na conversão: " + e.getMessage());
		}
	}
	
	private void testOntologiesButtonAction() {
		OWLOntology onto = getOWLModelManager().getActiveOntology();
		JOptionPane.showMessageDialog(this, onto.getOWLOntologyManager().getOntologyDocumentIRI(onto));

		Set<OWLOntology> ontoImports = onto.getDirectImports();
		StringBuilder text = new StringBuilder();
		for (OWLOntology ontoImport : ontoImports) {
			text.append(ontoImport.getOWLOntologyManager().getOntologyDocumentIRI(ontoImport));
			text.append("\n");
		}
		JOptionPane.showMessageDialog(this, text.toString());
	}
	
	private String getOntologyNameFromIRI(String iri) {
		String[] patterns = new String[] { "~(.+)?#", "/(\\w+)$", "/(\\w+)\\.owl$" };
		
		for (String patternString : patterns) {
			Pattern pattern = Pattern.compile(patternString);
			Matcher matcher = pattern.matcher(iri);
			if (matcher.find()) {
				return matcher.group(1);
			}
		}
		return null;
	}
	
	private Ontology createGryphonOntology(OWLOntology onto) {
		return new Ontology(getOntologyNameFromIRI(onto.getOntologyID().getOntologyIRI().toString()),
				onto.getOWLOntologyManager().getOntologyDocumentIRI(onto).toURI());
	}
	
	private String convertToSparqlQuery() throws OWLException {
		OWLClassExpressionToSPARQLConverter converter = new OWLClassExpressionToSPARQLConverter();
		
		// to fill internal variables in converter
		converter.asGroupGraphPattern(expressionEditor.createObject(), "?x");
		
		// real conversion
		return converter.convert(expressionEditor.createObject(), "?x", false);
	}
	
	private void initGryphon() {
		GryphonConfig.setWorkingDirectory(new File(System.getProperty("user.home"), "/mst/GryphonFramework/integrationExample"));
		GryphonConfig.setLogEnabled(true);
		GryphonConfig.setShowLogo(true);
		Gryphon.init();
	}
	
	private void testGryphonQueryButtonAction() {
		initGryphon();
		
		OWLOntology globalOnto = getOWLModelManager().getActiveOntology();
		Gryphon.setGlobalOntology(createGryphonOntology(globalOnto));
		
		Set<OWLOntology> ontoImports = globalOnto.getDirectImports();
		for (OWLOntology ontoImport : ontoImports) {
			Gryphon.addLocalOntology(createGryphonOntology(ontoImport));
		}
		
		
		Database localDB = new Database("localhost", 3306, "root", "admin123", "uniprot", Gryphon.DBMS.MySQL);
		Gryphon.addLocalDatabase(localDB);
		
		// Gryphon.alignAndMap();
		
		String sparqlQuery = null;
		try {
			sparqlQuery = convertToSparqlQuery();
		} catch (OWLException e) {
			e.printStackTrace();
			JOptionPane.showMessageDialog(this, 
					"Erro na conversão para SPARQL.\n" + e.getMessage());
		}
		
		if (sparqlQuery != null) {
			Gryphon.query(sparqlQuery, ResultFormat.JSON);
			
			File resultFolder = Gryphon.getResultFolder();
//			for (File file : resultFolder.listFiles()) {
//				System.out.println(file.getAbsolutePath());
//				JOptionPane.showMessageDialog(this, file.getAbsolutePath());
//			}
			File resultFile = new File(resultFolder, "db_localhost_3306_uniprot.json");
			BufferedReader reader;
			try {
				reader = new BufferedReader(new FileReader(resultFile));
				try {
					String line;
					StringBuilder text = new StringBuilder();
					while ((line = reader.readLine()) != null) {
						text.append(line);
						text.append("\n");
						if (text.length() > 400) {
							text.append("\n(...)");
							break;
						}
					}
					JOptionPane.showMessageDialog(this, text.toString());
				} catch (IOException e) {
					e.printStackTrace();
				} finally {
					try {
						reader.close();
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			} catch (FileNotFoundException e1) {
				e1.printStackTrace();
			}
		}
	}
	
	private JPanel createQueryPanel() {
		JPanel editorPanel = new JPanel(new BorderLayout());
		OWLExpressionChecker<OWLClassExpression> checker = getOWLModelManager().getOWLExpressionCheckerFactory().getOWLClassExpressionChecker();
		expressionEditor = new ExpressionEditor<OWLClassExpression>(getOWLEditorKit(), checker);

		editorPanel.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createTitledBorder(BorderFactory.createLineBorder(
                Color.LIGHT_GRAY), "DL Query (class expression)"), BorderFactory.createEmptyBorder(3, 3, 3, 3)));
		
		expressionEditor.setPreferredSize(new Dimension(100, 50));
		
		editorPanel.add(expressionEditor, BorderLayout.CENTER);
		editorPanel.add(createButtonsPanel(), BorderLayout.SOUTH);
		return editorPanel;
	}
	
	@Override
	protected void disposeOWLView() {
		// TODO Auto-generated method stub
		
	}
	
}
