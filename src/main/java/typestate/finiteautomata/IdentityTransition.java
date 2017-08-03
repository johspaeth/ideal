package typestate.finiteautomata;

public class IdentityTransition<State> implements ITransition<State> {
	private static IdentityTransition instance;
	private IdentityTransition() {
	}

	public static IdentityTransition v(){
		if(instance == null)
			instance = new IdentityTransition<>();
		return instance;
	}
	
	@Override
	public State from() {
		throw new RuntimeException("Unreachable");
	}

	@Override
	public State to() {
		throw new RuntimeException("Unreachable");
	}

}
