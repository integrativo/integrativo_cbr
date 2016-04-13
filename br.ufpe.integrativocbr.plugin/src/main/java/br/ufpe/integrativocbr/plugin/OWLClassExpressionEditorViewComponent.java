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
import java.util.Iterator;
import java.util.Set;

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
import org.semanticweb.owlapi.model.OWLOntology;

import br.ufpe.cin.aac3.gryphon.Gryphon;
import br.ufpe.cin.aac3.gryphon.Gryphon.ResultFormat;
import br.ufpe.cin.aac3.gryphon.GryphonConfig;
import br.ufpe.cin.aac3.gryphon.model.Database;
import br.ufpe.cin.aac3.gryphon.model.Ontology;
import br.ufpe.cin.integrativocbr.IntegrativoCBRApplication;
import br.ufpe.cin.integrativocbr.event.CBREventListener;

public class OWLClassExpressionEditorViewComponent extends AbstractOWLViewComponent {

	private static final long serialVersionUID = 1L;
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
		
		JPanel buttonsPanel = new JPanel();
		buttonsPanel.setLayout(new FlowLayout());
		buttonsPanel.add(testCBRButton);
		buttonsPanel.add(testGryphonButton);
		buttonsPanel.add(testSparqlConversionButton);
		buttonsPanel.add(testOntologiesButton);
		buttonsPanel.add(testGryphonQueryButton);
		return buttonsPanel;
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
						public void beforeResultCycle(String classIRI) {
							publish("Result: " + classIRI);
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
	
	private Ontology createGryphonOntology(OWLOntology onto) {
		return new Ontology(onto.getOntologyID().getOntologyIRI().toString(),
				onto.getOWLOntologyManager().getOntologyDocumentIRI(onto).toURI());
	}
	
	private String convertToSparqlQuery() throws OWLException {
		OWLClassExpressionToSPARQLConverter converter = new OWLClassExpressionToSPARQLConverter();
		
		// to fill internal variables in converter
		converter.asGroupGraphPattern(expressionEditor.createObject(), "?x");
		
		// real conversion
		return converter.convert(expressionEditor.createObject(), "?x", false);
	}
	
	private void testGryphonQueryButtonAction() {
		GryphonConfig.setWorkingDirectory(new File(System.getProperty("user.home"), "/mst/GryphonFramework/integrationSWAT4LSPaper"));
		GryphonConfig.setLogEnabled(true);
		GryphonConfig.setShowLogo(true);
		Gryphon.init();

		OWLOntology globalOnto = getOWLModelManager().getActiveOntology();
		Gryphon.setGlobalOntology(createGryphonOntology(globalOnto));
		
		Database localDB = new Database("localhost", 3306, "root", "admin123", "uniprot", Gryphon.DBMS.MySQL);
		Gryphon.addLocalDatabase(localDB);
		
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
