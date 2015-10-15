package cin.ufpe.integrativocbr.plugin;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTable;

import org.protege.editor.owl.ui.preferences.OWLPreferencesPanel;

public class PreferencesPane extends OWLPreferencesPanel {

	private static final long serialVersionUID = 1L;

	private JPanel mainPanel = new JPanel();
	private JTable table = new JTable();
	private JPanel buttonsPanel = new JPanel();
	
	@Override
	public void initialise() throws Exception {
		setLayout(new BorderLayout(10, 10));

		mainPanel.setLayout(new BorderLayout(10, 10));
		mainPanel.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createTitledBorder(BorderFactory.createLineBorder(
                Color.LIGHT_GRAY), "Lista de Bases de Dados"), BorderFactory.createEmptyBorder(3, 3, 3, 3)));
		add(mainPanel, BorderLayout.CENTER);
		
		mainPanel.add(table, BorderLayout.CENTER);
		
		buttonsPanel.setLayout(new FlowLayout(FlowLayout.LEFT, 10, 10));
		
		JButton addDatabaseButton = new JButton("Adicionar Banco");
		addDatabaseButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				addDatabaseButtonActionPerformed();
			}
		});
		buttonsPanel.add(addDatabaseButton);
		
		JButton removeDatabaseButton = new JButton("Remover Banco");
		removeDatabaseButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				removeDatabaseButtonActionPerformed();
			}
		});
		buttonsPanel.add(removeDatabaseButton);

		mainPanel.add(buttonsPanel, BorderLayout.SOUTH);
		
}

	@Override
	public void dispose() throws Exception {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void applyChanges() {
		// TODO Auto-generated method stub
		
	}
	
	private void addDatabaseButtonActionPerformed() {
		new DatabaseDialog(this).setVisible(true);
	}

	private void removeDatabaseButtonActionPerformed() {
		JOptionPane.showMessageDialog(this, "Remove Database Here!");
	}
	
}
