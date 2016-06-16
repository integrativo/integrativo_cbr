package br.ufpe.cin.integrativocbr.event;

import br.ufpe.cin.integrativocbr.CycleResult;


public interface CBREventListener {
	void beforeCreateGryphonConnector(String classIRI);
	void beforeCycle(String classIRI);
	void onResultCycle(CycleResult cycleResult);
}
