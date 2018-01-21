package typestate.ap.finiteautomata;

public interface State {
  public boolean isErrorState();

  public boolean isInitialState();
}
