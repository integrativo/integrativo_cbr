package br.ufpe.integrativocbr.plugin;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.FilenameFilter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
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
import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.io.OWLXMLOntologyFormat;
import org.semanticweb.owlapi.io.SystemOutDocumentTarget;
import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLAnnotation;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLClassExpression;
import org.semanticweb.owlapi.model.OWLDataIntersectionOf;
import org.semanticweb.owlapi.model.OWLDataRange;
import org.semanticweb.owlapi.model.OWLEquivalentClassesAxiom;
import org.semanticweb.owlapi.model.OWLException;
import org.semanticweb.owlapi.model.OWLLiteral;
import org.semanticweb.owlapi.model.OWLObjectIntersectionOf;
import org.semanticweb.owlapi.model.OWLObjectProperty;
import org.semanticweb.owlapi.model.OWLObjectSomeValuesFrom;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;
import org.semanticweb.owlapi.model.OWLOntologyManager;
import org.semanticweb.owlapi.model.OWLOntologyStorageException;
import org.semanticweb.owlapi.util.OWLClassExpressionVisitorAdapter;

import br.ufpe.cin.aac3.gryphon.Gryphon;
import br.ufpe.cin.aac3.gryphon.Gryphon.ResultFormat;
import br.ufpe.cin.aac3.gryphon.GryphonConfig;
import br.ufpe.cin.aac3.gryphon.model.Database;
import br.ufpe.cin.aac3.gryphon.model.Ontology;
import br.ufpe.cin.integrativocbr.CycleResult;
import br.ufpe.cin.integrativocbr.GryphonResult;
import br.ufpe.cin.integrativocbr.IntegrativoCBRApplication;
import br.ufpe.cin.integrativocbr.event.CBREventListener;

public class OWLClassExpressionEditorViewComponent extends AbstractOWLViewComponent {

	private static final long serialVersionUID = 1L;
	private static final File GRYPHON_WORKING_DIR = new File(System.getProperty("user.home"), "/mst/GryphonFramework/integrationExample"); 
	private static final File MAPPING_FILE = new File(GRYPHON_WORKING_DIR, "mappings/db_localhost_3306_uniprot.ttl");

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
		testAxiomsButton.setText("Test Axioms");
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
				try {
					testClassesInMappingButtonAction();
				} catch (OWLOntologyCreationException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
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
	
	private Set<String> readClassesInMapping() {
		Set<String> result = new HashSet<String>();
		
		try {
			BufferedReader reader = new BufferedReader(new FileReader(MAPPING_FILE));
			
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
	
	private void testClassesInMappingButtonAction() throws OWLOntologyCreationException {
		
		final OWLOntologyManager myManager = OWLManager.createOWLOntologyManager();
		final OWLOntology myOntology = myManager.createOntology();
		
		new ProgressDialogWorker(null) {
			
			private Set<String> classesInMapping;
			
			private Set<OWLClassExpression> replaceClasses(
					Set<OWLClassExpression> classExprSet, Map<OWLClass, OWLClass> owlClassMap) {
				Set<OWLClassExpression> newClassExprSet = new HashSet<OWLClassExpression>();
				for (OWLClassExpression classExpr : classExprSet) {
					if (classExpr instanceof OWLObjectSomeValuesFrom) {
						OWLObjectSomeValuesFrom someValuesProp = ((OWLObjectSomeValuesFrom) classExpr);
						OWLClassExpression filler = someValuesProp.getFiller();
						OWLClassExpression newFiller = null;
						
						if (filler instanceof OWLClass) {
							newFiller = owlClassMap.get(someValuesProp.getFiller());
						} else if (filler instanceof OWLObjectIntersectionOf){
							newFiller = (OWLClassExpression) getOWLDataFactory().getOWLObjectIntersectionOf(
									replaceClasses(filler.asConjunctSet(), owlClassMap));
						}
						
						classExpr = getOWLDataFactory().getOWLObjectSomeValuesFrom(
								someValuesProp.getProperty(), newFiller);
					}
					
					OWLClass owlClass = owlClassMap.get(classExpr);
					if (owlClass == null) {
						newClassExprSet.add(classExpr);
					} else {
						newClassExprSet.add(owlClass);
					}
				}
				return newClassExprSet;
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

			private String includeLabelsAxiomInSparql(String newSparql, Set<OWLClass> owlClasses) {
				if (owlClasses.size() == 3) {
					return "PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#>\n" 
						+ newSparql.replace("}", "?x rdfs:label ?labelx .\n" +
												"?s2 rdfs:label ?labels2 .\n" +
												"?s3 rdfs:label ?labels3 .\n" +
												"} LIMIT 30")
								   .replace("SELECT  DISTINCT ?x", "SELECT DISTINCT ?labelx ?labels2 ?labels3");
				} else {
					return "PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#>\n" 
						+ newSparql.replace("}", "?x rdfs:label ?labelx .\n" +
											"?s1 rdfs:label ?labels1 .\n" +
											"} LIMIT 30")
							   .replace("SELECT  DISTINCT ?x", "SELECT DISTINCT ?labelx ?labels1");
				}
			}

			@Override
			protected Object doInBackground() throws Exception {
				long totalTimeMillis = System.currentTimeMillis();
				try {
					initGryphon();
	
					if (MAPPING_FILE.exists()) {
						publish("Mapping file found in: " + MAPPING_FILE.getAbsolutePath());
					} else {
						publish("Mapping file NOT found in: " + MAPPING_FILE.getAbsolutePath());
					}
					publish("");
					classesInMapping = readClassesInMapping();
					publish("Classes in Mapping: " + classesInMapping);
					
					Map<OWLClass, OWLClass> owlClassMap = new HashMap<OWLClass, OWLClass>();
					publish("");
					final OWLClassExpression classExpression = expressionEditor.createObject();
					
					for (OWLClass owlClass : classExpression.getClassesInSignature()) {
						publish("Searching mapping for class: " + owlClass.getIRI());
						OWLClass classInMapping = searchClassInMapping(owlClass);
						publish("Class in mapping: " + classInMapping);
						if (classInMapping == null) {
							publish("Class " + owlClass.toString() + " NOT found in mapping! Exiting.");
							return null;
						}
						owlClassMap.put(owlClass, classInMapping);
					}
					
					publish("\nFound these classes on mapping: " + owlClassMap);
					
					publish("\nMaking Gryphon query...");
					Set<OWLClassExpression> newClassExprSet = replaceClasses(classExpression.asConjunctSet(), owlClassMap);
					
					OWLClassExpression newExpression = null; 
					if (classExpression instanceof OWLObjectIntersectionOf) {
						newExpression = getOWLDataFactory().getOWLObjectIntersectionOf(newClassExprSet);
					} else {
						publish("I don't know how to handle: " + classExpression.getClass().getName());
					}
					
					if (newExpression != null) {
						publish("OLD SPARQL:");
						publish(convertToSparqlQuery(classExpression));

						publish("New SPARQL:");
						String newSparql = convertToSparqlQuery(newExpression);
						publish(newSparql);
						
						newSparql = includeLabelsAxiomInSparql(newSparql, classExpression.getClassesInSignature());
						publish("Including labels in SPARQL:");
						publish(newSparql);
						
						publish("Querying Gryphon... ") ;
						Gryphon.query(newSparql, ResultFormat.JSON);
					
						File resultFolder = Gryphon.getResultFolder();
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
								publish(text.toString());
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
					
					publish("\nInitializing CBR...");
					Set<OWLClass> classesInSignature = newExpression.getClassesInSignature();
					final String[] classesInSignatureStringIDArray = new String[classesInSignature.size()];
					
					// Q1 (2)
//					classesInSignatureStringIDArray[0] = "http://purl.bioontology.org/ontology/NCBITAXON/131567";
//					classesInSignatureStringIDArray[1] = "http://purl.obolibrary.org/obo/GO_0008150";
					
					// Q1 (6.2)
//					classesInSignatureStringIDArray[0] = "http://purl.obolibrary.org/obo/GO_0008150";
//					classesInSignatureStringIDArray[1] = "http://purl.obolibrary.org/obo/PR_000000001";
//					classesInSignatureStringIDArray[2] = "http://purl.bioontology.org/ontology/NCBITAXON/131567";

					// Q2 (6.3)
//					classesInSignatureStringIDArray[0] = "http://purl.obolibrary.org/obo/PR_000000001";
//					classesInSignatureStringIDArray[1] = "http://purl.bioontology.org/ontology/NCBITAXON/131567";
					
					// Q3 (6.9)
//					classesInSignatureStringIDArray[0] = "http://purl.obolibrary.org/obo/CHEBI_23367";
//					classesInSignatureStringIDArray[1] = "http://purl.bioontology.org/ontology/NCBITAXON/131567";
					
					// Q4 (6.12)
					classesInSignatureStringIDArray[0] = "http://purl.bioontology.org/ontology/NCBITAXON/131567";
					classesInSignatureStringIDArray[1] = "http://purl.obolibrary.org/obo/PR_000000001";
					classesInSignatureStringIDArray[2] = "http://purl.obolibrary.org/obo/GO_0008150";
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
						public void onResultCycle(GryphonResult gryphonResult, CycleResult[] cycleResults, double average) {
							if (average < 0.8) {
								return;
							}
							
							StringBuilder newOWLClassName = new StringBuilder();
							Map<OWLClass, OWLClass> owlClassMap = new HashMap<OWLClass, OWLClass>();
							List<OWLClass> newClasses = new ArrayList<OWLClass>();
							OWLClass owlClassA = null;
							
							for (int i = 0; i < cycleResults.length; i++) {
								OWLClass owlClassRef = getOWLModelManager().getOWLDataFactory().getOWLClass(IRI.create(cycleResults[i].getClassId()));
								OWLClass owlClassOrigin = getOWLModelManager().getOWLDataFactory().getOWLClass(IRI.create(classesInSignatureStringIDArray[i]));
								owlClassMap.put(owlClassOrigin, owlClassRef);
								if (newOWLClassName.length() > 0) {
									newOWLClassName.append("_");
								}
								newOWLClassName.append(owlClassRef.toStringID());
								if (i == 0) {
									owlClassA = owlClassRef;
								}
								newClasses.add(owlClassRef);
							}
							
							OWLClass newOWLClass = getOWLModelManager().getOWLDataFactory().getOWLClass(IRI.create(newOWLClassName.toString()));
							
							OWLObjectIntersectionOf intersection = getOWLDataFactory().getOWLObjectIntersectionOf(
								replaceClasses(classExpression.asConjunctSet(), owlClassMap));
							
							OWLEquivalentClassesAxiom equivalentClassesAxiom = getOWLModelManager().getOWLDataFactory()
								.getOWLEquivalentClassesAxiom(newOWLClass, intersection);
							
							publish("NEW AXIOM:");
							publish(equivalentClassesAxiom.toString());

							// Class A
							myManager.addAxiom(myOntology, getOWLDataFactory().getOWLDeclarationAxiom(owlClassA));
							
							// Other Classes
							for (int i = 1; i < cycleResults.length; i++) {
								OWLClass owlClass = getOWLModelManager().getOWLDataFactory().getOWLClass(IRI.create(cycleResults[i].getClassId()));
								myManager.addAxiom(myOntology, getOWLDataFactory().getOWLDeclarationAxiom(owlClass));
							}
							
							myManager.addAxiom(myOntology, equivalentClassesAxiom);
							myManager.addAxiom(myOntology, getOWLDataFactory().getOWLSubClassOfAxiom(newOWLClass, owlClassA));
							
							OWLAnnotation labelAnnotation = getOWLDataFactory().getOWLAnnotation(
									getOWLDataFactory().getRDFSLabel(), 
									getOWLDataFactory().getOWLLiteral(concatLabels(getOWLModelManager().getActiveOntologies(), newClasses)));
							
							myManager.addAxiom(myOntology,
									getOWLDataFactory().getOWLAnnotationAssertionAxiom(newOWLClass.getIRI(), labelAnnotation));
						}
						
					}, classesInSignatureStringIDArray);
					
					totalTimeMillis = System.currentTimeMillis() - totalTimeMillis;
					
					publish("Total time: " + (totalTimeMillis / 1000) + "s");
					
				} catch (Exception e) {
					e.printStackTrace();
				}
				return null;
			}
			
			private String concatLabels(Set<OWLOntology> ontos, Collection<OWLClass> owlClasses) {
				StringBuilder result = new StringBuilder();
				for (OWLClass owlClass : owlClasses) {
					String label = null; 
					for (OWLOntology onto : ontos) {
						label = getOWLClassLabel(onto, owlClass);
						if (label != null) {
							break;
						}
					}
					if (result.length() > 0) {
						result.append("_");
					}
					if (label != null) {
						result.append(label);
					} else {
						result.append(owlClass.getIRI());
					}
				}
				return result.toString();
			}
			
			private String getOWLClassLabel(OWLOntology onto, OWLClass owlClass) {
				for (OWLAnnotation annotation : owlClass.getAnnotations(onto, getOWLDataFactory().getRDFSLabel())) {
					if (annotation.getValue() instanceof OWLLiteral) {
						return ((OWLLiteral) annotation.getValue()).getLiteral();
					}
				}
				return null;
			}
			
			protected void done() {
				super.done();
				try {
					System.out.println("\n\nNEW AXIOMS:");
					myManager.saveOntology(myOntology, new OWLXMLOntologyFormat(), new SystemOutDocumentTarget());
				} catch (OWLOntologyStorageException e) {
					e.printStackTrace();
				}
			};

			
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
			// initGryphon();
			// writeNewAxioms(axiomsMissingInMappingFile(newAxioms));
			
		} catch (OWLException e) {
			e.printStackTrace();
		}
	}
	
	private void writeNewAxioms(List<NewAxiom> axioms) {
		if (axioms == null || axioms.isEmpty()) {
			return;
		}
		try {
			BufferedWriter writer = new BufferedWriter(new FileWriter(getMappingFile(), true));
			writer.append('\n');
			for (NewAxiom newAxiom : axioms) {
				writer.append(newAxiom.toString());
			}
			writer.close();
		} catch (IOException ex) {
			ex.printStackTrace();
		}
	}

	private List<NewAxiom> axiomsMissingInMappingFile(List<NewAxiom> newAxioms) {
		try {
			String line;
			BufferedReader reader = new BufferedReader(new FileReader(getMappingFile()));
			List<NewAxiom> result = new ArrayList<NewAxiom>(newAxioms);
			while ((line = reader.readLine()) != null && result.size() > 0) {
				Iterator<NewAxiom> iterator = result.iterator();
				while (iterator.hasNext()) {
					NewAxiom newAxiom = (NewAxiom) iterator.next();
					if (line.contains(newAxiom.getMappingName())) {
						iterator.remove();
					}
				}
			}
			return result;
		} catch (IOException ex) {
			ex.printStackTrace();
			return null;
		}
	}
	
	private File getMappingFile() throws FileNotFoundException {
		String[] list = Gryphon.getMapFolder().list(new FilenameFilter() {
			@Override
			public boolean accept(File dir, String name) {
				return name.endsWith(".ttl");
			}
		});
		if (list.length > 0) {
			return new File(Gryphon.getMapFolder(), list[0]);
		}
		throw new FileNotFoundException("Mapping file not found");
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
						public void onResultCycle(GryphonResult gryphonResult,
								CycleResult[] cycleResults, double average) {
							publish("Result cycle: " + gryphonResult + ", " + cycleResults + ", " + average);
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
		initGryphon();
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
	
	private String convertToSparqlQuery(OWLClassExpression owlClassExpr) {
		OWLClassExpressionToSPARQLConverter converter = new OWLClassExpressionToSPARQLConverter();
		
		// to fill internal variables in converter
		converter.asGroupGraphPattern(owlClassExpr, "?x");
		
		// real conversion
		return converter.convert(owlClassExpr, "?x", false);
	}
	
	private String convertToSparqlQuery() throws OWLException {
		return convertToSparqlQuery(expressionEditor.createObject());
	}
	
	private void initGryphon() {
		GryphonConfig.setWorkingDirectory(GRYPHON_WORKING_DIR);
		GryphonConfig.setLogEnabled(true);
		GryphonConfig.setShowLogo(true);
		Gryphon.init();
		
		OWLOntology globalOnto = getOWLModelManager().getActiveOntology();
		Gryphon.setGlobalOntology(createGryphonOntology(globalOnto));
		
		Set<OWLOntology> ontoImports = globalOnto.getDirectImports();
		for (OWLOntology ontoImport : ontoImports) {
			Gryphon.addLocalOntology(createGryphonOntology(ontoImport));
		}

		Database localDB = new Database("localhost", 3306, "root", "admin123", "uniprot", Gryphon.DBMS.MySQL);
		Gryphon.addLocalDatabase(localDB);
		
		// Gryphon.alignAndMap();
	}
	
	private void testGryphonQueryButtonAction() {
//		generateMappings();
		initGryphon();

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
