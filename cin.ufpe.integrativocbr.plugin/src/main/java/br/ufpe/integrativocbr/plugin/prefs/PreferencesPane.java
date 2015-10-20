package br.ufpe.integrativocbr.plugin.prefs;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.AbstractTableModel;

import org.protege.editor.owl.ui.preferences.OWLPreferencesPanel;

import br.ufpe.integrativocbr.plugin.DatabaseDialog;

public class PreferencesPane extends OWLPreferencesPanel {

	private static final long serialVersionUID = 1L;

	private List<DatabasePreference> databasePreferences;

	private JPanel mainPanel = new JPanel();
	private DatabaseTableModel tableModel = new DatabaseTableModel();
	private JTable table = new JTable(tableModel);
	private JPanel buttonsPanel = new JPanel();
	
	@Override
	public void initialise() throws Exception {
		databasePreferences = IntegrativoPreferences.getInstance().readDatabasePrefsList();
		
		setLayout(new BorderLayout(10, 10));

		mainPanel.setLayout(new BorderLayout(10, 10));
		mainPanel.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createTitledBorder(BorderFactory.createLineBorder(
                Color.LIGHT_GRAY), "Lista de Bases de Dados"), BorderFactory.createEmptyBorder(3, 3, 3, 3)));
		add(mainPanel, BorderLayout.CENTER);
		
		JScrollPane scrollPane = new JScrollPane(table);
		table.setFillsViewportHeight(true);
		mainPanel.add(scrollPane, BorderLayout.CENTER);
		
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
		databasePreferences = null;
	}

	@Override
	public void applyChanges() {
		IntegrativoPreferences.getInstance().writeDatabasePrefsList(databasePreferences);
	}
	
	private void addDatabaseButtonActionPerformed() {
		DatabaseDialog dbDialog = new DatabaseDialog(this);
		dbDialog.setVisible(true);
		if (dbDialog.isOk()) {
			databasePreferences.add(dbDialog.createDatabasePreference());
		}
		tableModel.fireTableDataChanged();
	}

	private void removeDatabaseButtonActionPerformed() {
		if (table.getSelectedRow() > -1) {
			databasePreferences.remove(table.getSelectedRow());
			tableModel.fireTableDataChanged();
		}
	}
	
	private class DatabaseTableModel extends AbstractTableModel {

		private static final long serialVersionUID = 1L;

		@Override
		public int getColumnCount() {
			return 4;
		}
		
		@Override
		public String getColumnName(int column) {
			switch (column) {
			case 0: return "Host";
			case 1: return "Port";
			case 2: return "Database";
			case 3: return "User";
			}
			return "-";
		}
		
		@Override
		public int getRowCount() {
			return databasePreferences.size();
		}

		@Override
		public Object getValueAt(int rowIndex, int columnIndex) {
			switch (columnIndex) {
			case 0: return databasePreferences.get(rowIndex).getHost(); 
			case 1: return databasePreferences.get(rowIndex).getPort(); 
			case 2: return databasePreferences.get(rowIndex).getDatabaseName(); 
			case 3: return databasePreferences.get(rowIndex).getUserName();
			}
			return "-";
		}
		
	}
	
}
