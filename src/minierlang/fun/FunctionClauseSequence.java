package minierlang.fun;

import minierlang.Manager;
import minierlang.Node;

public class FunctionClauseSequence extends Node {
  public FunctionClause head;
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
      manager.dumpCodeLabel();
      tail.generateCode(manager, this);
    }
  }

  public long destruct(Manager manager, Node caller) {
    return 0;
  }

  public long destructDependencies(Manager manager, Node caller) {
    return 0;
  }
}
