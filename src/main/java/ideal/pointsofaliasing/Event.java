package ideal.pointsofaliasing;

import heros.incremental.UpdatableWrapper;
import soot.Unit;

public abstract class Event<V> implements PointOfAlias<V>{
	abstract UpdatableWrapper<Unit> getCallsite();
}
