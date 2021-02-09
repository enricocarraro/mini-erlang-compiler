package minierlang;

public class FunctionClauseSequence extends Node {
  FunctionClause head;
  FunctionClauseSequence tail;

  public FunctionClauseSequence(FunctionClause head, FunctionClauseSequence tail) {
    this.head = head;
    this.tail = tail;
    subgraphSize = head.subgraphSize + (tail != null ? tail.subgraphSize : 0);
  }

  public void generateCode(Manager manager, Node parent) {
    super.generateCode(manager, parent);
    manager.openClause();
    manager.checkTailMatch(head, tail);

    head.generateCode(manager, this);
    if (tail != null) {
      manager.dumpln(manager.genLabel() + ":");
      tail.generateCode(manager, this);
    }
  }

  @Override
  public long destruct(Manager manager, Node caller) {
    return 0;
  }

  @Override
  public long destructDependencies(Manager manager, Node caller) {
    // TODO Auto-generated method stub
    return 0;
  }
}
