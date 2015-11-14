package br.ufpe.integrativocbr.plugin;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JSplitPane;

import org.aksw.owl2sparql.OWLClassExpressionToSPARQLConverter;
import org.protege.editor.owl.ui.clsdescriptioneditor.ExpressionEditor;
import org.protege.editor.owl.ui.clsdescriptioneditor.OWLExpressionChecker;
import org.protege.editor.owl.ui.view.AbstractOWLViewComponent;
import org.semanticweb.owlapi.model.OWLClassExpression;
import org.semanticweb.owlapi.model.OWLException;

import br.ufpe.cin.aac3.gryphon.Gryphon;
import br.ufpe.cin.aac3.gryphon.GryphonConfig;

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
		testGryphonButton.setText("Teste Gryphon");
		testGryphonButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				testOntologiesButtonAction();
			}
		});

		JPanel buttonsPanel = new JPanel();
		buttonsPanel.setLayout(new FlowLayout());
		buttonsPanel.add(testGryphonButton);
		buttonsPanel.add(testSparqlConversionButton);
		buttonsPanel.add(testOntologiesButton);
		return buttonsPanel;
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
		OWLClassExpressionToSPARQLConverter converter = new OWLClassExpressionToSPARQLConverter();
		String text;
		try {
			text = converter.convert(expressionEditor.createObject(), "?x", false);
			JOptionPane.showMessageDialog(this, "SPARQL conversion:\n" + text);
		} catch (OWLException e) {
			e.printStackTrace();
		}
	}
	
	private void testOntologiesButtonAction() {
		JOptionPane.showMessageDialog(this, getOWLModelManager().getActiveOntology().toString());
		JOptionPane.showMessageDialog(this, getOWLModelManager().getActiveOntology().getDirectImports().toString());
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
