package test;

import boomerang.accessgraph.AccessGraph;
import heros.incremental.UpdatableWrapper;
import soot.Unit;
import typestate.TypestateDomainValue;

public class MayBe extends ExpectedResults<ConcreteState> {

	MayBe(UpdatableWrapper<Unit> unit, AccessGraph accessGraph, InternalState state) {
		super(unit, accessGraph, state);
	}
	public String toString(){
		return "Maybe " + super.toString();
	}
	@Override
	public void computedResults(TypestateDomainValue<ConcreteState> results) {
		for(ConcreteState s : results.getStates()){
			if(state == InternalState.ACCEPTING){
				satisfied |= !s.isErrorState();
			} else if(state == InternalState.ERROR){
				satisfied |= s.isErrorState();
			}
		}
	}
}	
