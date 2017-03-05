package typestate.impl.signature;

import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import boomerang.accessgraph.AccessGraph;
import heros.EdgeFunction;
import heros.solver.Pair;
import soot.SootClass;
import soot.SootMethod;
import soot.Unit;
import soot.jimple.infoflow.solver.cfg.InfoflowCFG;
import typestate.TypestateChangeFunction;
import typestate.TypestateDomainValue;
import typestate.finiteautomata.MatcherStateMachine;
import typestate.finiteautomata.MatcherTransition;
import typestate.finiteautomata.MatcherTransition.Parameter;
import typestate.finiteautomata.MatcherTransition.Type;
import typestate.finiteautomata.State;

public class SignatureStateMachine extends MatcherStateMachine implements TypestateChangeFunction {

  private MatcherTransition initialTrans;
  private InfoflowCFG icfg;

  public static enum States implements State {
    NONE, UNITIALIZED, SIGN_CHECK, VERIFY_CHECK, ERROR;

    @Override
    public boolean isErrorState() {
      return this == ERROR;
    }

    @Override
    public boolean isInitialState() {
      return this == UNITIALIZED;
    }
  }

  SignatureStateMachine(InfoflowCFG icfg) {
    this.icfg = icfg;
    initialTrans =
        new MatcherTransition(States.NONE, constructor(),Parameter.This, States.UNITIALIZED, Type.OnReturn);
    addTransition(initialTrans);
    addTransition(new MatcherTransition(States.UNITIALIZED, initSign(),Parameter.This, States.SIGN_CHECK, Type.OnReturn));
    addTransition(new MatcherTransition(States.UNITIALIZED, initVerify(),Parameter.This, States.VERIFY_CHECK, Type.OnReturn));
    addTransition(new MatcherTransition(States.UNITIALIZED, sign(),Parameter.This, States.ERROR, Type.OnReturn));
    addTransition(new MatcherTransition(States.UNITIALIZED, verify(),Parameter.This, States.ERROR, Type.OnReturn));
    addTransition(new MatcherTransition(States.UNITIALIZED, update(),Parameter.This, States.ERROR, Type.OnReturn));
    
    addTransition(new MatcherTransition(States.SIGN_CHECK, initSign(),Parameter.This, States.SIGN_CHECK, Type.OnReturn));
    addTransition(new MatcherTransition(States.SIGN_CHECK, initVerify(),Parameter.This, States.VERIFY_CHECK, Type.OnReturn));
    addTransition(new MatcherTransition(States.SIGN_CHECK, sign(),Parameter.This, States.SIGN_CHECK, Type.OnReturn));
    addTransition(new MatcherTransition(States.SIGN_CHECK, verify(),Parameter.This, States.ERROR, Type.OnReturn));
    addTransition(new MatcherTransition(States.SIGN_CHECK, update(),Parameter.This, States.SIGN_CHECK, Type.OnReturn));
    
    addTransition(new MatcherTransition(States.VERIFY_CHECK, initSign(),Parameter.This, States.SIGN_CHECK, Type.OnReturn));
    addTransition(new MatcherTransition(States.VERIFY_CHECK, initVerify(),Parameter.This, States.VERIFY_CHECK, Type.OnReturn));
    addTransition(new MatcherTransition(States.VERIFY_CHECK, sign(),Parameter.This, States.ERROR, Type.OnReturn));
    addTransition(new MatcherTransition(States.VERIFY_CHECK, verify(),Parameter.This, States.VERIFY_CHECK, Type.OnReturn));
    addTransition(new MatcherTransition(States.VERIFY_CHECK, update(),Parameter.This, States.VERIFY_CHECK, Type.OnReturn));
    
    addTransition(new MatcherTransition(States.ERROR, initSign(),Parameter.This, States.ERROR, Type.OnReturn));
    addTransition(new MatcherTransition(States.ERROR, initVerify(),Parameter.This, States.ERROR, Type.OnReturn));
    addTransition(new MatcherTransition(States.ERROR, sign(),Parameter.This, States.ERROR, Type.OnReturn));
    addTransition(new MatcherTransition(States.ERROR, verify(),Parameter.This, States.ERROR, Type.OnReturn));
    addTransition(new MatcherTransition(States.ERROR, update(),Parameter.This, States.ERROR, Type.OnReturn));
  }
  private Set<SootMethod> constructor() {
    List<SootClass> subclasses = getSubclassesOf("java.security.Signature");
    Set<SootMethod> out = new HashSet<>();
    for (SootClass c : subclasses) {
      for (SootMethod m : c.getMethods())
        if (m.isPublic() && m.getName().equals("getInstance"))
          out.add(m);
    }
    return out;
  }

  private Set<SootMethod> verify() {
    return selectMethodByName(getSubclassesOf("java.security.Signature"), "verify");
  }
  private Set<SootMethod> update() {
	    return selectMethodByName(getSubclassesOf("java.security.Signature"), "update");
	  }
  private Set<SootMethod> sign() {
    return selectMethodByName(getSubclassesOf("java.security.Signature"), "sign");
  }
  private Set<SootMethod> initSign() {
    return selectMethodByName(getSubclassesOf("java.security.Signature"), "initSign");
  }
  private Set<SootMethod> initVerify() {
	    return selectMethodByName(getSubclassesOf("java.security.Signature"), "initVerify");
	  }



  @Override
  public Collection<Pair<AccessGraph, EdgeFunction<TypestateDomainValue>>> generate(SootMethod m, Unit unit,
      Collection<SootMethod> calledMethod) {
	  if(!m.getDeclaringClass().isApplicationClass())
			return Collections.emptySet();
	  return generateReturnValueOf(unit, calledMethod, initialTrans);
  }


}

