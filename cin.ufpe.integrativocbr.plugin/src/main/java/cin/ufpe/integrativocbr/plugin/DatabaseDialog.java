package cin.ufpe.integrativocbr.plugin;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.Window;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;

public class DatabaseDialog extends JDialog {

	private static final long serialVersionUID = 1L;

	private JPanel mainPanel = new JPanel();
	private JPanel fieldsPanel = new JPanel();
	private JTextField hostField = new JTextField();
	private JTextField userNameField = new JTextField();
	private JPasswordField userPasswordField = new JPasswordField();
	private JPanel buttonsPanel = new JPanel();
	private JButton okButton = new JButton("OK");
	private JButton cancelButton = new JButton("Cancelar");
	
	public DatabaseDialog(JPanel parent) {
		setModal(true);
		setTitle("Adicionar Banco");
		setSize(300, 200);
		setLocationRelativeTo(parent);
		configureComponents();
	}

	private void configureComponents() {
		mainPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
		mainPanel.setLayout(new BorderLayout(10, 10));
		fieldsPanel.setLayout(new GridLayout(0, 2, 10, 10));
		fieldsPanel.add(new JLabel("Host:"));
		fieldsPanel.add(hostField);
		fieldsPanel.add(new JLabel("User:"));
		fieldsPanel.add(userNameField);
		fieldsPanel.add(new JLabel("Password:"));
		fieldsPanel.add(userPasswordField);
		
		mainPanel.add(fieldsPanel, BorderLayout.CENTER);

		buttonsPanel.setLayout(new FlowLayout(FlowLayout.RIGHT, 10, 10));
		getRootPane().setDefaultButton(okButton);
		buttonsPanel.add(okButton);
		buttonsPanel.add(cancelButton);
		mainPanel.add(buttonsPanel, BorderLayout.SOUTH);
		
		add(mainPanel, BorderLayout.CENTER);
	}	

}
