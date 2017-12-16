package test;

import java.util.Map.Entry;
import java.util.Set;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;

import heros.incremental.UpdatableWrapper;
import ideal.AnalysisSolver;
import ideal.IFactAtStatement;
import ideal.ResultReporter;
import soot.Unit;
import typestate.TypestateDomainValue;

public class TestingResultReporter<State> implements ResultReporter<TypestateDomainValue<State>>{
	private Multimap<UpdatableWrapper<Unit>, Assertion> stmtToResults = HashMultimap.create();
	public TestingResultReporter(Set<Assertion> expectedResults) {
		for(Assertion e : expectedResults){
//			System.out.println("e " + e);
			if(e instanceof ComparableResult)
				stmtToResults.put(((ComparableResult) e).getStmt(), e);
		}
	}

	@Override
	public void onSeedFinished(IFactAtStatement seed, AnalysisSolver<TypestateDomainValue<State>> solver) {
		for(Entry<UpdatableWrapper<Unit>, Assertion> e : stmtToResults.entries()){
			if(e.getValue() instanceof ComparableResult){
				ComparableResult expectedResults = (ComparableResult) e.getValue();
				TypestateDomainValue<State> resultAt = solver.resultAt(e.getKey(), expectedResults.getAccessGraph());
				/*System.out.println("--------------------------------TestingResultReporter-------------------------------------");
				System.out.println("e.getKey " + e.getKey());
				System.out.println("expectedResults.getAccessGraph " + expectedResults.getAccessGraph());
				System.out.println("e.getKey class " + e.getKey().getClass());
				System.out.println("expectedResults.getAccessGraph class " + expectedResults.getAccessGraph().getClass());
				System.out.println("resultAt " + resultAt);
				System.out.println("--------------------------------TestingResultReporter-------------------------------------");*/
				if(resultAt != null)
					expectedResults.computedResults(resultAt);
			}
		}
	}

	@Override
	public void onSeedTimeout(IFactAtStatement seed) {
		// TODO Auto-generated method stub
		
	}

}
