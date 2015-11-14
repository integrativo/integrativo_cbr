package br.ufpe.integrativocbr.plugin;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.NumberFormat;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFormattedTextField;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;

import br.ufpe.integrativocbr.plugin.prefs.DatabasePreference;

public class DatabaseDialog extends JDialog {

	private static final long serialVersionUID = 1L;

	private JPanel mainPanel = new JPanel();
	private JPanel fieldsPanel = new JPanel();
	private JTextField hostField = new JTextField();
	private JFormattedTextField portField = new JFormattedTextField(NumberFormat.getIntegerInstance());
	private JTextField userNameField = new JTextField();
	private JPasswordField userPasswordField = new JPasswordField();
	private JTextField databaseNameField = new JTextField();
	private JPanel buttonsPanel = new JPanel();
	private JButton okButton = new JButton("OK");
	private JButton cancelButton = new JButton("Cancelar");

	private boolean ok = false;
	
	public DatabaseDialog(JPanel parent) {
		setModal(true);
		setTitle("Adicionar Banco");
		setSize(300, 300);
		setLocationRelativeTo(parent);
		configureComponents();
	}

	
	private void configureComponents() {
		mainPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
		mainPanel.setLayout(new BorderLayout(10, 10));
		fieldsPanel.setLayout(new GridLayout(0, 2, 10, 10));
		fieldsPanel.add(new JLabel("Host:"));
		fieldsPanel.add(hostField);
		fieldsPanel.add(new JLabel("Port:"));
		fieldsPanel.add(portField);
		fieldsPanel.add(new JLabel("User:"));
		fieldsPanel.add(userNameField);
		fieldsPanel.add(new JLabel("Password:"));
		fieldsPanel.add(userPasswordField);
		fieldsPanel.add(new JLabel("Database:"));
		fieldsPanel.add(databaseNameField);
		
		mainPanel.add(fieldsPanel, BorderLayout.CENTER);

		buttonsPanel.setLayout(new FlowLayout(FlowLayout.RIGHT, 10, 10));
		getRootPane().setDefaultButton(okButton);
		buttonsPanel.add(okButton);
		okButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				ok = true;
				dispose();
			}
		});
		buttonsPanel.add(cancelButton);
		cancelButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				dispose();
			}
		});
		mainPanel.add(buttonsPanel, BorderLayout.SOUTH);
		
		add(mainPanel, BorderLayout.CENTER);
	}	
	
	public DatabasePreference createDatabasePreference() {
		DatabasePreference pref = new DatabasePreference();
		pref.setHost(hostField.getText());
		pref.setPort(((Number) portField.getValue()).intValue());
		pref.setUserName(userNameField.getText());
		pref.setUserPassword(userPasswordField.getPassword());
		pref.setDatabaseName(databaseNameField.getText());
		return pref;
	}


	public boolean isOk() {
		return ok;
	}

}
