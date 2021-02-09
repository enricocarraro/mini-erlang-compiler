package minierlang.exp;

import minierlang.Const;
import minierlang.Manager;
import minierlang.Node;

public abstract class BinaryExpression extends Expression {
  protected Expression lhs, rhs;

  public BinaryExpression(Expression lhs, Expression rhs) {
    this.lhs = lhs;
    this.rhs = rhs;
    subgraphSize = 3 + rhs.subgraphSize + lhs.subgraphSize + CLEANUP_LABEL_SIZE + RESUME_LABEL_SIZE;
  }

  public void genericGenerateCode(String function, Manager manager, Node parent) {
    super.generateCode(manager, parent);

    lhs.generateCode(manager, this);
	manager.dumpln(manager.genLabel() + ":");
    rhs.generateCode(manager, this);

    manager.dumpln(manager.genLabel() + ": ");
    label = allocate(manager);
    manager.dumpln(
        String.format(
            "\tinvoke void %s(%%%s* sret align 8 %%%d, %%%s* %%%d, %%%s* nonnull align 8"
                + " dereferenceable(16) %%%d)",
            function,
            Const.LITERAL_STRUCT,
            label,
            Const.LITERAL_STRUCT,
            lhs.label,
            Const.LITERAL_STRUCT,
            rhs.label));

    long unwindLabel = label + 1;
    long branchLabel = unwindLabel + Node.CLEANUP_LABEL_SIZE + Node.RESUME_LABEL_SIZE;
    manager.dumpln(String.format("\t\tto label %%%d unwind label %%%d", branchLabel, unwindLabel));

    manager.cleanupError(manager);
    destructDependencies(manager, this);
    manager.resumeError(manager);
  }

  public long destructDependencies(Manager manager, Node caller) {
    long maxParentDep = super.destructDependencies(manager, caller);
    if (lhs != caller) {
      if (lhs.label > maxParentDep) {
        maxParentDep = Math.max(lhs.destruct(manager, this), maxParentDep);
      }
      if (rhs != caller && rhs.label > maxParentDep) {
        maxParentDep = Math.max(rhs.destruct(manager, this), maxParentDep);
      }
    }

    return maxParentDep;
  }
}