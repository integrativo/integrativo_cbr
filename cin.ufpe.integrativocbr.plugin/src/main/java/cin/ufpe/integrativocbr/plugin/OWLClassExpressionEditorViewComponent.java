package cin.ufpe.integrativocbr.plugin;

import java.awt.BorderLayout;

import javax.swing.JLabel;
import javax.swing.JPanel;

import org.protege.editor.owl.ui.view.AbstractOWLViewComponent;

public class OWLClassExpressionEditorViewComponent extends AbstractOWLViewComponent {

	private static final long serialVersionUID = 1L;

	@Override
	protected void initialiseOWLView() throws Exception {
		setLayout(new BorderLayout());
		JPanel mainPanel = new JPanel();
		mainPanel.add(new JLabel("Teste"));
		add(mainPanel, BorderLayout.CENTER);
	}

	@Override
	protected void disposeOWLView() {
		// TODO Auto-generated method stub
		
	}
	
}
