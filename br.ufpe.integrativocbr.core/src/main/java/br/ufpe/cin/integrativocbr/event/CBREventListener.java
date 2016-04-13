package br.ufpe.cin.integrativocbr.event;

public interface CBREventListener {
	void beforeCreateGryphonConnector(String classIRI);
	void beforeCycle(String classIRI);
	void beforeResultCycle(String classIRI);
}
