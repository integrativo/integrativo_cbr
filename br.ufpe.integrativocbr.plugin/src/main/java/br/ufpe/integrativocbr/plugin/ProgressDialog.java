package br.ufpe.integrativocbr.plugin;

import java.awt.BorderLayout;
import java.awt.Font;
import java.awt.Frame;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

public class ProgressDialog extends JDialog {

	private static final long serialVersionUID = 1L;

	private JTextArea textArea;
	private JPanel bottomPanel;
	
	public ProgressDialog(Frame owner) {
		super(owner, false);
		setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
		setTitle("Progresso");
		setSize(700, 400);
		setLocationRelativeTo(owner);
		initComponents();
	}

	private void initComponents() {
		JPanel centerPanel = new JPanel();
		centerPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
		BorderLayout borderLayout = new BorderLayout();
		borderLayout.setVgap(10);
		centerPanel.setLayout(borderLayout);
		centerPanel.add(new JLabel("Atividade:"), BorderLayout.NORTH);
		textArea = new JTextArea();
		textArea.setFont(new Font("Courier New", Font.PLAIN, 12));
		textArea.setLineWrap(true);
		textArea.setEditable(false);
		centerPanel.add(new JScrollPane(textArea), BorderLayout.CENTER);
		
		add(centerPanel, BorderLayout.CENTER);
		
		bottomPanel = new JPanel(new BorderLayout());
		JButton closeButton = new JButton("Fechar");
		bottomPanel.add(closeButton);
		bottomPanel.setVisible(false);
		closeButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				ProgressDialog.this.dispose();
			}
		});
		add(bottomPanel, BorderLayout.SOUTH);
	}

	public void showCloseButton() {
		bottomPanel.setVisible(true);
		setDefaultCloseOperation(DISPOSE_ON_CLOSE);
	}
	
	public void appendText(String text) {
		textArea.append(text);
		textArea.append("\n");
		textArea.setCaretPosition(textArea.getDocument().getLength());
	}
	
}
