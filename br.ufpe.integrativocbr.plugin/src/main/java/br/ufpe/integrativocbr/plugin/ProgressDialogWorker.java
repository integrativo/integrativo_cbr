package br.ufpe.integrativocbr.plugin;

import java.awt.Frame;

import javax.swing.SwingWorker;

public abstract class ProgressDialogWorker extends SwingWorker<Object, String> {

	private ProgressDialog progressDialog;
	
	public ProgressDialogWorker(Frame owner) {
		progressDialog = new ProgressDialog(owner);
	}
	
	protected void process(java.util.List<String> chunks) {
		for (String chunk : chunks) {
			if (!progressDialog.isVisible()) {
				progressDialog.setVisible(true);
			}
			progressDialog.appendText(chunk);
		}
	}

	@Override
	protected void done() {
		progressDialog.appendText("-- END OF PROCESSING --");
		progressDialog.showCloseButton();
	}
	
}
