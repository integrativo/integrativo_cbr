package br.ufpe.cin.integrativocbr.event;

import br.ufpe.cin.integrativocbr.CycleResult;
import br.ufpe.cin.integrativocbr.GryphonResult;


public interface CBREventListener {
	void beforeCreateGryphonConnector(String classIRI);
	void beforeCycle(String classIRI);
	void onResultCycle(GryphonResult gryphonResult, CycleResult[] cycleResults, double average);
}
