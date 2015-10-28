package br.ufpe.integrativocbr.plugin;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSplitPane;

import org.protege.editor.owl.ui.clsdescriptioneditor.ExpressionEditor;
import org.protege.editor.owl.ui.clsdescriptioneditor.OWLExpressionChecker;
import org.protege.editor.owl.ui.view.AbstractOWLViewComponent;
import org.semanticweb.owlapi.model.OWLClassExpression;

import br.ufpe.cin.aac3.gryphon.Gryphon;
import br.ufpe.cin.aac3.gryphon.GryphonConfig;

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
		editorPanel.add(createButtonsPanel(), BorderLayout.SOUTH);
		return editorPanel;
	}

	private JPanel createButtonsPanel() {
		JPanel buttonsPanel = new JPanel();
		JButton gryphonTestButton = new JButton("Gryphon test");
		gryphonTestButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				gryphonTestButtonAction();
			}
		});
		buttonsPanel.add(gryphonTestButton);
		return buttonsPanel;
	}
	
	private void gryphonTestButtonAction() {
		GryphonConfig.setWorkingDirectory(new File("integrationExample"));
		GryphonConfig.setLogEnabled(true);
		GryphonConfig.setShowLogo(true);
		Gryphon.init();
	}
	
	@Override
	protected void disposeOWLView() {
		// TODO Auto-generated method stub
		
	}
	
}
