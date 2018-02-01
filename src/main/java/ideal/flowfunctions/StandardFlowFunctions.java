package ideal.flowfunctions;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import com.google.common.collect.Sets;

import boomerang.AliasFinder;
import boomerang.accessgraph.WrappedSootField;
import boomerang.forward.AbstractFlowFunctions;
import heros.FlowFunction;
import heros.FlowFunctions;
import heros.flowfunc.Identity;
import heros.incremental.UpdatableWrapper;
import ideal.Analysis;
import ideal.PerSeedAnalysisContext;
import ideal.incremental.accessgraph.UpdatableAccessGraph;
import ideal.incremental.accessgraph.UpdatableWrappedSootField;
import ideal.pointsofaliasing.CallSite;
import ideal.pointsofaliasing.InstanceFieldWrite;
import ideal.pointsofaliasing.NullnessCheck;
import ideal.pointsofaliasing.ReturnEvent;
import soot.Local;
import soot.PointsToAnalysis;
import soot.PrimType;
import soot.RefType;
import soot.Scene;
import soot.SootClass;
import soot.SootField;
import soot.SootMethod;
import soot.Type;
import soot.Unit;
import soot.Value;
import soot.jimple.ArrayRef;
import soot.jimple.AssignStmt;
import soot.jimple.CastExpr;
import soot.jimple.CaughtExceptionRef;
import soot.jimple.Constant;
import soot.jimple.EqExpr;
import soot.jimple.IdentityStmt;
import soot.jimple.IfStmt;
import soot.jimple.InstanceFieldRef;
import soot.jimple.InstanceInvokeExpr;
import soot.jimple.InvokeExpr;
import soot.jimple.NewExpr;
import soot.jimple.NullConstant;
import soot.jimple.ReturnStmt;
import soot.jimple.StaticFieldRef;
import soot.jimple.Stmt;

/**
 * This class defines the flow function for IDEAL. 
 *
 */
public class StandardFlowFunctions<V> extends AbstractFlowFunctions
		implements FlowFunctions<UpdatableWrapper<Unit>, UpdatableAccessGraph, UpdatableWrapper<SootMethod>> {
	private final PerSeedAnalysisContext<V> context;
	public StandardFlowFunctions(PerSeedAnalysisContext<V> context) {
		this.context = context;
	}

	
	@Override
	public FlowFunction<UpdatableAccessGraph> getNormalFlowFunction(final UpdatableAccessGraph sourceFact, final UpdatableWrapper<Unit> curr,
			final UpdatableWrapper<Unit> succ) {
		return new FlowFunction<UpdatableAccessGraph>() {

			@Override
			public Set<UpdatableAccessGraph> computeTargets(UpdatableAccessGraph source) {

				if(curr.getContents() instanceof IdentityStmt){
					IdentityStmt identityStmt = (IdentityStmt) curr.getContents();
					if (identityStmt.getRightOp() instanceof CaughtExceptionRef)
						return Collections.emptySet();
				}
				context.debugger().onNormalPropagation(sourceFact, curr.getContents(), succ.getContents(), source);
				if (AliasFinder.HANDLE_EXCEPTION_FLOW && !source.isStatic() && curr.getContents() instanceof IdentityStmt) {
					IdentityStmt identityStmt = (IdentityStmt) curr.getContents();
					if (identityStmt.getRightOp() instanceof CaughtExceptionRef
							&& identityStmt.getLeftOp() instanceof Local) {
						Local leftOp = (Local) identityStmt.getLeftOp();
						// e = d;
						if (!source.isStatic()){
							HashSet<UpdatableAccessGraph> out = new HashSet<UpdatableAccessGraph>();
							out.add(source);
							out.add(source.deriveWithNewLocal((Local) leftOp));
							return out;
						} else{
							return Collections.emptySet();
						}
					}
				}
					

				if (!(curr.getContents() instanceof AssignStmt)) {
					if (curr.getContents() instanceof IfStmt) {
						IfStmt ifStmt = (IfStmt) curr.getContents();
						Value condition = ifStmt.getCondition();

						if (condition instanceof EqExpr && source.getFieldCount() == 0) {
							EqExpr eqExpr = (EqExpr) condition;
							Value leftOp = eqExpr.getOp1();
							Value rightOp = eqExpr.getOp2();
							boolean nullnessCheck = false;
							if (rightOp instanceof NullConstant && leftOp.equals(source.getBase())) {
								nullnessCheck = true;
							} else if (leftOp instanceof NullConstant && rightOp.equals(source.getBase())) {
								nullnessCheck = true;
							}
							if (nullnessCheck && context.enableNullPointAlias()) {
								context.addPOA(new NullnessCheck<V>(sourceFact, curr, source, context.icfg().wrap((Unit) ifStmt.getTarget())));
							}
						}

					}
					return Collections.singleton(source);
				}

				AssignStmt as = (AssignStmt) curr.getContents();
				Value leftOp = as.getLeftOp();
				Value rightOp = as.getRightOp();

				HashSet<UpdatableAccessGraph> out = new HashSet<UpdatableAccessGraph>();
				out.add(source);

				if (rightOp instanceof Constant || rightOp instanceof NewExpr) {
					// a = new || a = 2
					if (leftOp instanceof Local && source.baseMatches(leftOp))
						// source == a.*
						return Collections.emptySet();
					// a.f = new || a.f = 2;
					if (leftOp instanceof InstanceFieldRef) {
						InstanceFieldRef fr = (InstanceFieldRef) leftOp;
						Value base = fr.getBase();
						SootField field = fr.getField();
						// source == a.f.*
						if (source.baseAndFirstFieldMatches(base, field))
							return Collections.emptySet();
					}

				}

				if (leftOp instanceof Local) {
					if (source.baseMatches(leftOp)) {
						if (rightOp instanceof InstanceFieldRef) {
							InstanceFieldRef fr = (InstanceFieldRef) rightOp;
							Value base = fr.getBase();
							SootField field = fr.getField();

							if (source.baseAndFirstFieldMatches(base, field)) {
								Set<UpdatableAccessGraph> popFirstField = source.popFirstField();
								out.addAll(popFirstField);
							} else {
								return Collections.emptySet();
							}
						} else {
							return Collections.emptySet();
						}
					}
				} else if (leftOp instanceof InstanceFieldRef) {
					InstanceFieldRef fr = (InstanceFieldRef) leftOp;
					Value base = fr.getBase();
					SootField field = fr.getField();
					if (source.baseAndFirstFieldMatches(base, field)) {
						return Collections.emptySet();
					}
				}
				if (rightOp instanceof CastExpr) {
					CastExpr castExpr = (CastExpr) rightOp;
					Value op = castExpr.getOp();
					if (op instanceof Local) {
						if (!source.isStatic() && source.baseMatches(op)) {
							out.add(source.deriveWithNewLocal((Local) leftOp));
						}
					}
				}

				if (rightOp instanceof Local && source.baseMatches(rightOp)) {

					if (leftOp instanceof Local) {
						// e = d;
						out.add(source.deriveWithNewLocal((Local) leftOp));
					} else if (leftOp instanceof InstanceFieldRef) {
						// d.f = e;
						InstanceFieldRef fr = (InstanceFieldRef) leftOp;
						Value base = fr.getBase();
						SootField field = fr.getField();

						if (base instanceof Local) {
							Local lBase = (Local) base;

							UpdatableAccessGraph withNewLocal = source.deriveWithNewLocal(lBase);
							UpdatableWrappedSootField newFirstField = new UpdatableWrappedSootField(field, curr);
							if (!pointsToSetEmpty(lBase)) {
								UpdatableAccessGraph newAp = withNewLocal.prependField(newFirstField);
								out.add(newAp);
								InstanceFieldWrite<V> instanceFieldWrite = new InstanceFieldWrite(sourceFact, context.icfg().wrap(as),
										lBase, newAp, succ);
								if (context.isInIDEPhase()) {
									out.addAll(context.getFlowAtPointOfAlias(instanceFieldWrite));
								} else {
									context.addPOA(instanceFieldWrite);
								}
							}
						}
					} else if (leftOp instanceof ArrayRef) {
						ArrayRef fr = (ArrayRef) leftOp;
						Value base = fr.getBase();

						if (base instanceof Local) {
							Local lBase = (Local) base;

							UpdatableAccessGraph withNewLocal = source.deriveWithNewLocal(lBase);
							UpdatableAccessGraph newAp = withNewLocal.prependField(
									new UpdatableWrappedSootField(AliasFinder.ARRAY_FIELD, curr));
							out.add(newAp);
							InstanceFieldWrite<V> instanceFieldWrite = new InstanceFieldWrite(sourceFact, context.icfg().wrap(as), lBase,
									newAp, succ);
							if (context.isInIDEPhase()) {
								out.addAll(context.getFlowAtPointOfAlias(instanceFieldWrite));
							} else {
								context.addPOA(instanceFieldWrite);
							}
						}
					} else if (leftOp instanceof StaticFieldRef && Analysis.ENABLE_STATIC_FIELDS) {
						// d.f = e;
						StaticFieldRef fr = (StaticFieldRef) leftOp;
						SootField field = fr.getField();
						UpdatableAccessGraph newAp = source
								.prependField(new UpdatableWrappedSootField(field, curr)).makeStatic();

						if(newAp.hasSetBasedFieldGraph()){
							newAp = source.dropTail()
									.prependField(new UpdatableWrappedSootField(field, curr)).makeStatic();
							out.add(newAp);
						}
						out.add(newAp);
						return out;
					}
				} else if (rightOp instanceof InstanceFieldRef) {
					InstanceFieldRef fr = (InstanceFieldRef) rightOp;
					Value base = fr.getBase();
					SootField field = fr.getField();

					if (source.baseMatches(base) && source.firstFirstFieldMayMatch(field)) {
						// e = a.f && source == a.f.*
						// replace in source
						if (leftOp instanceof Local && !source.baseMatches(leftOp)) {
							for(UpdatableWrappedSootField firstField : source.getFirstField()){
								UpdatableAccessGraph deriveWithNewLocal = source.deriveWithNewLocal((Local) leftOp);
								out.addAll(deriveWithNewLocal.popFirstField());
							}
						}
					}
				} else if (rightOp instanceof ArrayRef) {
					ArrayRef arrayRef = (ArrayRef) rightOp;
					if (source.baseAndFirstFieldMatches(arrayRef.getBase(), AliasFinder.ARRAY_FIELD)) {

						Set<UpdatableAccessGraph> withoutFirstField = source.popFirstField();
						for (UpdatableAccessGraph a : withoutFirstField) {
							for(UpdatableWrappedSootField firstField : source.getFirstField()){
								out.add(a.deriveWithNewLocal((Local) leftOp));
							}
						}
					}
				} else if (rightOp instanceof StaticFieldRef && Analysis.ENABLE_STATIC_FIELDS) {
					StaticFieldRef sfr = (StaticFieldRef) rightOp;
					if (source.isStatic() && source.firstFieldMustMatch(sfr.getField())) {
						if (leftOp instanceof Local) {
							Set<UpdatableAccessGraph> withoutFirstField = source.popFirstField();
							for (UpdatableAccessGraph a : withoutFirstField) {
								for(UpdatableWrappedSootField firstField : source.getFirstField()){
									out.add(a.deriveWithNewLocal((Local) leftOp));
								}
							}
						}
					}
				}

				return out;
			}
		};
	}


	protected boolean isFirstFieldUsedTransitivelyInMethod(UpdatableAccessGraph source, final UpdatableWrapper<SootMethod> callee) {
        for(UpdatableWrappedSootField wrappedField :  source.getFirstField()){
      	  if(context.icfg().isStaticFieldUsed(callee.getContents(), wrappedField.getField()))
      		  return true;
        }
		return false;
	}
	@Override
	public FlowFunction<UpdatableAccessGraph> getCallFlowFunction(final UpdatableAccessGraph d1, final UpdatableWrapper<Unit> callSite,
			final UpdatableWrapper<SootMethod> callee) {
		assert callee != null;
		final Local[] paramLocals = new Local[callee.getContents().getParameterCount()];
		for (int i = 0; i < callee.getContents().getParameterCount(); i++)
			paramLocals[i] = callee.getContents().getActiveBody().getParameterLocal(i);

		final Local thisLocal = callee.getContents().isStatic() ? null : callee.getContents().getActiveBody().getThisLocal();
		return new FlowFunction<UpdatableAccessGraph>() {
			@Override
			public Set<UpdatableAccessGraph> computeTargets(UpdatableAccessGraph source) {
				assert source != null;
				Set<UpdatableAccessGraph> out = new HashSet<>();
				Stmt is = (Stmt) callSite.getContents();
				source = source.deriveWithoutAllocationSite();
				if (Analysis.ENABLE_STATIC_FIELDS && source.isStatic()) {
					if (callee != null && isFirstFieldUsedTransitivelyInMethod(source,callee)) {
						return Collections.singleton(source);
					} else {
						return Collections.emptySet();
					}
				}

				if (is.containsInvokeExpr()) {
					final InvokeExpr ie = is.getInvokeExpr();
					for (int i = 0; i < paramLocals.length; i++) {
						Value arg = ie.getArg(i);
						if (arg instanceof Local && source.baseMatches(arg)) {
							if (!pointsToSetEmpty(paramLocals[i])) {
								out.add(source.deriveWithNewLocal(paramLocals[i]));
							}
						}
					}
					final Value[] callArgs = new Value[ie.getArgCount()];
					for (int i = 0; i < ie.getArgCount(); i++)
						callArgs[i] = ie.getArg(i);

					if (!callee.getContents().isStatic() && ie instanceof InstanceInvokeExpr) {
						InstanceInvokeExpr iIExpr = (InstanceInvokeExpr) is.getInvokeExpr();

						if (source.baseMatches(iIExpr.getBase())) {
							if (d1 != null && d1.hasAllocationSite() && source.getFieldCount() < 1) {
								Unit sourceStmt = d1.getSourceStmt().getContents();
								if (sourceStmt instanceof AssignStmt) {
									AssignStmt as = (AssignStmt) sourceStmt;
									Value rightOp = as.getRightOp();
									Type type = rightOp.getType();
									if (type instanceof RefType) {
										RefType refType = (RefType) type;
										SootClass typeClass = refType.getSootClass();
										SootClass methodClass = callee.getContents().getDeclaringClass();
										if (typeClass != null && methodClass != null && typeClass != methodClass
												&& !typeClass.isInterface()) {
											if (!Scene.v().getFastHierarchy().isSubclass(typeClass, methodClass)) {
												return Collections.emptySet();
											}
										}
									} else if (type instanceof PrimType) {
										return Collections.emptySet();
									}

								}
							}
							if(!pointsToSetEmpty(thisLocal)){
								UpdatableAccessGraph replacedThisValue = source.deriveWithNewLocal(thisLocal);
								out.add(replacedThisValue);
							}
						}
					}
				}
				return out;
			}
		};
	}

	protected boolean pointsToSetEmpty(Local local) {
		return Analysis.FLOWS_WITH_NON_EMPTY_PTS_SETS && Scene.v().getPointsToAnalysis().reachingObjects(local).isEmpty();
	}

	@Override
	public FlowFunction<UpdatableAccessGraph> getReturnFlowFunction(final UpdatableAccessGraph callerD1, final UpdatableAccessGraph calleeD1, final UpdatableWrapper<Unit> callSite,
			final UpdatableAccessGraph callerCallSiteFact, final UpdatableWrapper<SootMethod> callee, final UpdatableWrapper<Unit> exitStmt, final UpdatableWrapper<Unit> returnSite) {
		final Local[] paramLocals = new Local[callee.getContents().getParameterCount()];
		for (int i = 0; i < callee.getContents().getParameterCount(); i++)
			paramLocals[i] = callee.getContents().getActiveBody().getParameterLocal(i);
		final Local thisLocal = callee.getContents().isStatic() ? null : callee.getContents().getActiveBody().getThisLocal();
		return new FlowFunction<UpdatableAccessGraph>() {
			

			@Override
			public Set<UpdatableAccessGraph> computeTargets(UpdatableAccessGraph source) {
				// mapping of fields of AccessPath those will be killed in
				// callToReturn
				if (Analysis.ENABLE_STATIC_FIELDS && source.isStatic())
					return Collections.singleton(source);

				HashSet<UpdatableAccessGraph> out = new HashSet<UpdatableAccessGraph>();
				if (callSite.getContents() instanceof Stmt) {
					Stmt is = (Stmt) callSite.getContents();

					if (is.containsInvokeExpr()) {
						InvokeExpr ie = is.getInvokeExpr();
						for (int i = 0; i < paramLocals.length; i++) {

							if (paramLocals[i].equals(source.getBase())) {
								Value arg = ie.getArg(i);
								if (arg instanceof Local) {
									if(pointsToSetEmpty((Local) arg))
										return Collections.emptySet();
									UpdatableAccessGraph deriveWithNewLocal = source.deriveWithNewLocal((Local) arg);
									
									out.add(deriveWithNewLocal);
									CallSite<V> callSitePOA = new CallSite<>(callerD1, callSite, callerCallSiteFact,deriveWithNewLocal,
											returnSite);
									if (context.isInIDEPhase()) {
										out.addAll(context.getFlowAtPointOfAlias(callSitePOA));
									} else {
										context.addPOA(callSitePOA);
									}
								}

							}
						}
						if (!callee.getContents().isStatic() && ie instanceof InstanceInvokeExpr) {
							if (source.baseMatches(thisLocal)) {

								InstanceInvokeExpr iIExpr = (InstanceInvokeExpr) is.getInvokeExpr();
								Local newBase = (Local) iIExpr.getBase();
								if(pointsToSetEmpty((Local) newBase))
									return Collections.emptySet();
								if (pointsToSetCompatible(newBase, source.getBase())) {
									UpdatableAccessGraph possibleAccessPath = source.deriveWithNewLocal((Local) iIExpr.getBase());
									out.add(possibleAccessPath);
									
									CallSite<V> callSitePOA = new CallSite<>(callerD1, callSite, callerCallSiteFact,possibleAccessPath,
											returnSite);
									if (context.isInIDEPhase()) {
										out.addAll(context.getFlowAtPointOfAlias(callSitePOA));
									} else {
										context.addPOA(callSitePOA);
									}
								}
							}
						}
					}
				}

				if (callSite.getContents() instanceof AssignStmt && exitStmt.getContents() instanceof ReturnStmt) {
					AssignStmt as = (AssignStmt) callSite.getContents();
					Value leftOp = as.getLeftOp();
					// mapping of return value

					ReturnStmt returnStmt = (ReturnStmt) exitStmt.getContents();
					Value returns = returnStmt.getOp();
					// d = return out;
					if (leftOp instanceof Local) {
						if (returns instanceof Local && source.getBase().equals(returns) && !pointsToSetEmpty((Local)leftOp)) {
							out.add(source.deriveWithNewLocal((Local) leftOp));
						}
					}
				}
				if(context.isInIDEPhase()){
					Set<UpdatableAccessGraph> indirectFlows = new HashSet<>();
					for(UpdatableAccessGraph d3 : out){
						indirectFlows.addAll(context.getFlowAtPointOfAlias(new ReturnEvent<V>(exitStmt, source, callSite, d3, returnSite, callerD1, null)));
					}
					out.addAll(indirectFlows);
				}
				return out;
			}

		};
	}

	protected boolean pointsToSetCompatible(Local l1, Local l2) {
		PointsToAnalysis ptAnalysis = Scene.v().getPointsToAnalysis();
		return !Analysis.FLOWS_WITH_NON_EMPTY_PTS_SETS || ptAnalysis.reachingObjects(l1).hasNonEmptyIntersection(ptAnalysis.reachingObjects(l2));
	}

	@Override
	public FlowFunction<UpdatableAccessGraph> getCallToReturnFlowFunction(final UpdatableAccessGraph sourceFact, final UpdatableWrapper<Unit> callStmt,
			final UpdatableWrapper<Unit> returnSite, final boolean hasCallees) {
//		if (!hasCallees) {
//			return Identity.v();
//		}
		if (!(callStmt.getContents() instanceof Stmt)) {
			return Identity.v();
		}
		Stmt callSite = (Stmt) callStmt.getContents();
		if (!callSite.containsInvokeExpr()) {
			return Identity.v();
		}

		final InvokeExpr invokeExpr = callSite.getInvokeExpr();
		return new FlowFunction<UpdatableAccessGraph>() {
			@Override
			public Set<UpdatableAccessGraph> computeTargets(UpdatableAccessGraph source) {
				if(context.isStrongUpdate(callStmt, source)){
					return  Sets.newHashSet();
				}
				if(hasCallees){
					for (int i = 0; i < invokeExpr.getArgCount(); i++) {
						if (source.baseMatches(invokeExpr.getArg(i))) {
							return  Sets.newHashSet();
						}
					}
					if (invokeExpr instanceof InstanceInvokeExpr) {
						InstanceInvokeExpr iie = (InstanceInvokeExpr) invokeExpr;
						Value base = iie.getBase();
						if (source.baseMatches(base)) {
							return  Sets.newHashSet();
						}
					}
				}
				return Sets.newHashSet(source);
			}
		};
	}
}
