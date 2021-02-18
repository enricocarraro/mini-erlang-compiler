package minierlang;

public abstract class Node {
  public Node parent;
  protected static final int CLEANUP_LABEL_SIZE = 4;
  protected static final int RESUME_LABEL_SIZE = 4;
  public long label;
  public long subgraphSize;

  public Node() {}

  public Node(long subgraphSize) {
    this.subgraphSize = subgraphSize;
  }

  public void generateCode(Manager manager, Node parent) {
	  this.parent = parent;
  }
  
  // Call destructors for any dependency the Node has, but itself.
  public abstract long destructDependencies(Manager manager, Node caller);
  
  // Call destructors for any dependency that the Node has, including itself.
  public abstract long destruct(Manager manager, Node caller);
  
}