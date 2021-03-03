package minierlang.exp.binary;

import minierlang.Const;
import minierlang.Manager;
import minierlang.Node;
import minierlang.exp.BinaryExpression;
import minierlang.exp.Expression;

public class Match extends BinaryExpression {

  public Match(Expression lhs, Expression rhs) {
    super(lhs, rhs);
    subgraphSize = 2 + rhs.subgraphSize + lhs.subgraphSize + CLEANUP_LABEL_SIZE + RESUME_LABEL_SIZE;
  }

  public void generateCode(Manager manager, Node parent) {
    super.generateCode(manager, parent);

    manager.dumpln("\t; start " + this.getClass().getName() + "(" + subgraphSize + ")");

    lhs.generateCode(manager, this);
    label = lhs.label;

    manager.dumpCodeLabel();
    rhs.generateCode(manager, this);

    manager.dumpCodeLabel();
    manager.dumpFormatln(
        "\tinvoke void %s(%%%s* %%%d, %%%s* nonnull align 8 dereferenceable(16) %%%d)",
        Const.LITERAL_MATCH, Const.LITERAL_STRUCT, label, Const.LITERAL_STRUCT, rhs.label);
    
    long unwindLabel = manager.getCurrentLabel();
    long branchLabel = unwindLabel + CLEANUP_LABEL_SIZE + RESUME_LABEL_SIZE;
    manager.dumpFormatln("\t\tto label %%%d unwind label %%%d", branchLabel, unwindLabel);

    manager.cleanupError();
    destructDependencies(manager, this);
    manager.resumeError();
  }

  public long destruct(Manager manager, Node caller) {
    return destructDependencies(manager, caller);
  }
}