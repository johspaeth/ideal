package typestate.tests;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

import org.junit.Test;

import boomerang.cfg.IExtendedICFG;
import ideal.debug.IDebugger;
import ideal.debug.NullDebugger;
import soot.SootMethod;
import soot.Unit;
import test.ConcreteState;
import test.slowmethod.SlowMethodDetector;
import typestate.TypestateChangeFunction;
import typestate.TypestateDomainValue;
import typestate.impl.statemachines.InputStreamStateMachine;

public class InputStreamTest extends SlowMethodDetector {

	@Test
	public void test1() throws IOException {
		InputStream inputStream = new FileInputStream("");
		inputStream.close();
		inputStream.read();
		mustBeInErrorState(inputStream);
	}

	@Test
	public void test2() throws IOException {
	    InputStream inputStream = new FileInputStream("");
	    inputStream.close();
	    inputStream.close();
	    inputStream.read();
	    mustBeInErrorState(inputStream);
	}

	@Test
	public void test3() throws IOException {
	    InputStream inputStream = new FileInputStream("");
	    inputStream.read();
	    inputStream.close();
	    mustBeInAcceptingState(inputStream);
	}

	@Override
	protected TypestateChangeFunction<ConcreteState> createTypestateChangeFunction(IExtendedICFG<Unit, SootMethod> icfg) {
		return new InputStreamStateMachine(icfg);
	}
	
	@Override
	protected IDebugger<TypestateDomainValue<ConcreteState>> getDebugger() {
		return new NullDebugger<TypestateDomainValue<ConcreteState>>();
	}
}