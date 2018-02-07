package test;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.google.common.collect.Lists;

import boomerang.cfg.ExtendedICFG;
import boomerang.cfg.IExtendedICFG;
import heros.BiDiInterproceduralCFG;
import heros.incremental.UpdatableWrapper;
import ideal.Analysis;
import ideal.ResultReporter;
import ideal.debug.IDEDebugger;
import ideal.debug.IDebugger;
import ideal.incremental.accessgraph.UpdatableAccessGraph;
import soot.Body;
import soot.Local;
import soot.SceneTransformer;
import soot.SootMethod;
import soot.Unit;
import soot.Value;
import soot.jimple.InvokeExpr;
import soot.jimple.Stmt;
import soot.jimple.toolkits.ide.icfg.JimpleBasedInterproceduralCFG;
import test.ExpectedResults.InternalState;
import test.core.selfrunning.AbstractTestingFramework;
import test.core.selfrunning.ImprecisionException;
import typestate.TypestateAnalysisProblem;
import typestate.TypestateChangeFunction;
import typestate.TypestateDomainValue;

public abstract class IDEALTestingFramework extends AbstractTestingFramework{
	protected ExtendedICFG icfg;
	protected long analysisTime;
	private IDebugger<TypestateDomainValue<ConcreteState>> debugger;
	protected TestingResultReporter<ConcreteState> testingResultReporter;

	protected abstract TypestateChangeFunction<ConcreteState> createTypestateChangeFunction(IExtendedICFG<Unit, SootMethod> icfg);

	protected Analysis<TypestateDomainValue<ConcreteState>> createAnalysis() {
		return new Analysis<TypestateDomainValue<ConcreteState>>(new TypestateAnalysisProblem<ConcreteState>() {
			@Override
			public ResultReporter<TypestateDomainValue<ConcreteState>> resultReporter() {
				return IDEALTestingFramework.this.testingResultReporter;
			}

			@Override
			public BiDiInterproceduralCFG<Unit, SootMethod> icfg() {
				return icfg.getBaseECFG();
			}

			@Override
			public IDebugger<TypestateDomainValue<ConcreteState>> debugger() {
				return IDEALTestingFramework.this.getDebugger();
			}

			@Override
			public TypestateChangeFunction<ConcreteState> createTypestateChangeFunction() {
				return IDEALTestingFramework.this.createTypestateChangeFunction(icfg);
			}

			@Override
			public IExtendedICFG<Unit, SootMethod> eIcfg() {
				return icfg;
			}
		});
	}

	protected IDebugger<TypestateDomainValue<ConcreteState>> getDebugger() {
		if(debugger == null)
			debugger = new IDEDebugger<>();
		return debugger;
	}

	@Override
	protected SceneTransformer createAnalysisTransformer() throws ImprecisionException {
		return new SceneTransformer() {
			protected void internalTransform(String phaseName, @SuppressWarnings("rawtypes") Map options) {
				icfg = new ExtendedICFG(new JimpleBasedInterproceduralCFG(true));
				Set<Assertion> expectedResults = parseExpectedQueryResults(icfg.wrap(sootTestMethod));
				
				/*for (Assertion expectedAssertion : expectedResults) {
					System.out.println("expectedAssertion " + expectedAssertion);
				}*/
				
				testingResultReporter = new TestingResultReporter<ConcreteState>(expectedResults);
				
				executeAnalysis();
				
				List<Assertion> unsound = Lists.newLinkedList();
				List<Assertion> imprecise = Lists.newLinkedList();
				for (Assertion r : expectedResults) {
					if (!r.isSatisfied()) {
						unsound.add(r);
					}
				}
				for (Assertion r : expectedResults) {
					if (r.isImprecise()) {
						imprecise.add(r);
					}
				}
				if (!unsound.isEmpty())
					throw new RuntimeException("Unsound results: " + unsound);
				IDEALTestingFramework.this.removeVizFile();
				if (!imprecise.isEmpty()) {
					throw new ImprecisionException("Imprecise results: " + imprecise);
				}
			}
		};
	}

	protected void executeAnalysis() {
		IDEALTestingFramework.this.createAnalysis().run();
	}

	private Set<Assertion> parseExpectedQueryResults(UpdatableWrapper<SootMethod> sootTestMethod) {
		Set<Assertion> results = new HashSet<>();
		parseExpectedQueryResults(sootTestMethod, results, new HashSet<UpdatableWrapper<SootMethod>>());
		return results;
	}

	private void parseExpectedQueryResults(UpdatableWrapper<SootMethod> m, Set<Assertion> queries, Set<UpdatableWrapper<SootMethod>> visited) {
		if (!m.getContents().hasActiveBody() || visited.contains(m))
			return;
		visited.add(m);
		Body activeBody = m.getContents().getActiveBody();
		
		/*System.out.println("----------------------------------------------------");
		System.out.println(activeBody);
		System.out.println("----------------------------------------------------");*/
		
		for (UpdatableWrapper<Unit> callSite : icfg.getCallsFromWithin(m)) {
			for (UpdatableWrapper<SootMethod> callee : icfg.getCalleesOfCallAt(callSite))
				parseExpectedQueryResults(callee, queries, visited);
		}
		for (Unit u : activeBody.getUnits()) {
			if (!(u instanceof Stmt))
				continue;

			Stmt stmt = (Stmt) u;
			if (!(stmt.containsInvokeExpr()))
				continue;
			InvokeExpr invokeExpr = stmt.getInvokeExpr();
			String invocationName = invokeExpr.getMethod().getName();
			if (!invocationName.startsWith("mayBeIn") && !invocationName.startsWith("mustBeIn"))
				continue;
			Value param = invokeExpr.getArg(0);
			if (!(param instanceof Local))
				continue;
			Local queryVar = (Local) param;
			UpdatableAccessGraph val = new UpdatableAccessGraph(icfg.wrap(queryVar));
			if (invocationName.startsWith("mayBeIn")) {
				if (invocationName.contains("Error"))
					queries.add(new MayBe(icfg.wrap((Unit) stmt), val, InternalState.ERROR));
				else
					queries.add(new MayBe(icfg.wrap((Unit) stmt), val, InternalState.ACCEPTING));
			} else if (invocationName.startsWith("mustBeIn")) {
				if (invocationName.contains("Error"))
					queries.add(new MustBe(icfg.wrap((Unit) stmt), val, InternalState.ERROR));
				else
					queries.add(new MustBe(icfg.wrap((Unit) stmt), val, InternalState.ACCEPTING));
			}
		}
	}

	/**
	 * The methods parameter describes the variable that a query is issued for.
	 * Note: We misuse the @Deprecated annotation to highlight the method in the
	 * Code.
	 */

	@Deprecated
	protected static void mayBeInErrorState(Object variable) {

	}

	@Deprecated
	protected static void mustBeInErrorState(Object variable) {

	}

	@Deprecated
	protected static void mayBeInAcceptingState(Object variable) {

	}

	@Deprecated
	protected void mustBeInAcceptingState(Object variable) {

	}

	/**
	 * This method can be used in test cases to create branching. It is not
	 * optimized away.
	 * 
	 * @return
	 */
	protected boolean staticallyUnknown() {
		return true;
	}

}
