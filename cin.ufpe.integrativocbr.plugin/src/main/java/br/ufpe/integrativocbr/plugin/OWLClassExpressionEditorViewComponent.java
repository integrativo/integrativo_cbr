package br.ufpe.integrativocbr.plugin;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSplitPane;

import org.protege.editor.owl.ui.clsdescriptioneditor.ExpressionEditor;
import org.protege.editor.owl.ui.clsdescriptioneditor.OWLExpressionChecker;
import org.protege.editor.owl.ui.view.AbstractOWLViewComponent;
import org.semanticweb.owlapi.model.OWLClassExpression;

public class OWLClassExpressionEditorViewComponent extends AbstractOWLViewComponent {

	private static final long serialVersionUID = 1L;

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

	private JPanel createQueryPanel() {
		JPanel editorPanel = new JPanel(new BorderLayout());
		OWLExpressionChecker<OWLClassExpression> checker = getOWLModelManager().getOWLExpressionCheckerFactory().getOWLClassExpressionChecker();
		ExpressionEditor<OWLClassExpression> expressionEditor = new ExpressionEditor<OWLClassExpression>(getOWLEditorKit(), checker);

		editorPanel.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createTitledBorder(BorderFactory.createLineBorder(
                Color.LIGHT_GRAY), "DL Query (class expression)"), BorderFactory.createEmptyBorder(3, 3, 3, 3)));
		
		expressionEditor.setPreferredSize(new Dimension(100, 50));
		
		editorPanel.add(expressionEditor, BorderLayout.CENTER);
		return editorPanel;
	}

	@Override
	protected void disposeOWLView() {
		// TODO Auto-generated method stub
		
	}
	
}
