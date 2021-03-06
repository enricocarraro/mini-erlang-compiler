package minierlang.exp;

import minierlang.Const;
import minierlang.Manager;
import minierlang.Node;

public abstract class UnaryExpression extends Expression {
  protected Expression rhs;

  public UnaryExpression(Expression rhs) {
    this.rhs = rhs;
    subgraphSize = 1 + rhs.subgraphSize + CLEANUP_LABEL_SIZE + RESUME_LABEL_SIZE;
  }

  public void genericGenerateCode(String function, Manager manager, Node parent) {
    super.generateCode(manager, parent);

    rhs.generateCode(manager, this);

    label = allocate(manager);
    manager.dumpFormatln(
            "\tinvoke void %s(%%%s* sret align 8 %%%d, %%%s* %%%d)",
            function, Const.LITERAL_STRUCT, label, Const.LITERAL_STRUCT, rhs.label);

    long unwindLabel = label + 1;
    long branchLabel = unwindLabel + Node.CLEANUP_LABEL_SIZE + Node.RESUME_LABEL_SIZE;
    manager.dumpFormatln("\t\tto label %%%d unwind label %%%d", branchLabel, unwindLabel);

    manager.cleanupError();
    destructDependencies(manager, this);
    manager.resumeError();
  }

  public long destructDependencies(Manager manager, Node caller) {
    long maxParentDep = super.destructDependencies(manager, caller);
    if (rhs != caller && rhs.label > maxParentDep) {
      rhs.destruct(manager, this);
    }
    return maxParentDep;
  }
}