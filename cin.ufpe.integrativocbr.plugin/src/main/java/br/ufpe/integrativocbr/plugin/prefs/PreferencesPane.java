package br.ufpe.integrativocbr.plugin.prefs;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTable;

import org.protege.editor.owl.ui.preferences.OWLPreferencesPanel;

import br.ufpe.integrativocbr.plugin.DatabaseDialog;

public class PreferencesPane extends OWLPreferencesPanel {

	private static final long serialVersionUID = 1L;

	private JPanel mainPanel = new JPanel();
	private JTable table = new JTable();
	private JPanel buttonsPanel = new JPanel();
	
	private List<DatabasePreference> databasePreferences =
			IntegrativoPreferences.getInstance().readDatabasePrefsList();

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
		// TODO Auto-generated method stub/
		
	}
	
	private void addDatabaseButtonActionPerformed() {
		DatabaseDialog dbDialog = new DatabaseDialog(this);
		dbDialog.setVisible(true);
		if (dbDialog.isOk()) {
			dbDialog.setVisible(true);
			databasePreferences.add(dbDialog.createDatabasePreference());
			IntegrativoPreferences.getInstance().writeDatabasePrefsList(databasePreferences);
		}
	}

	private void removeDatabaseButtonActionPerformed() {
		JOptionPane.showMessageDialog(this, "Remove Database Here!");
	}
	
}
