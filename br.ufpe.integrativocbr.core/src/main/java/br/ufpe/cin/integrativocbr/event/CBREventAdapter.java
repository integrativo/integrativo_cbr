package br.ufpe.cin.integrativocbr.event;

import br.ufpe.cin.integrativocbr.CycleResult;
import br.ufpe.cin.integrativocbr.GryphonResult;

public class CBREventAdapter implements CBREventListener {

	@Override
	public void beforeCreateGryphonConnector(String classIRI) {
	}

	@Override
	public void beforeCycle(String classIRI) {
	}

	@Override
	public void onResultCycle(GryphonResult gryphonResult,
			CycleResult[] cycleResults, double average) {
	}
}
