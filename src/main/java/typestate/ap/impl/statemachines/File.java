package typestate.ap.impl.statemachines;
public class File {
  public void open() {

  }

  public void close() {
  };

  public int hashCode() {
    return 9;
  }
  
  public void wrappedClose(){
	  close();
  }
}
