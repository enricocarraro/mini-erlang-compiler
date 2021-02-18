package minierlang.exp;

import minierlang.Const;
import minierlang.Manager;
import minierlang.Node;

public abstract class LeftAssocBinaryExpression extends Expression {
  protected Expression lhs, rhs;

  public LeftAssocBinaryExpression(Expression lhs, Expression rhs) {
	  this.lhs = lhs;
	    this.rhs = rhs;
	  subgraphSize = 3 + rhs.subgraphSize + lhs.subgraphSize + CLEANUP_LABEL_SIZE + RESUME_LABEL_SIZE;
	  
  }
  
  public void genericGenerateCode(String function, Manager manager, Node parent) {
	  super.generateCode(manager, parent);

	    rhs.generateCode(manager, this);
		  manager.dumpCodeLabel();
	    lhs.generateCode(manager, this);


    manager.dumpCodeLabel();
    label = allocate(manager);
    manager.dumpFormatln(
            "\tinvoke void %s(%%%s* sret align 8 %%%d, %%%s* %%%d, %%%s* nonnull align 8"
                + " dereferenceable(16) %%%d)",
            function,
            Const.LITERAL_STRUCT,
            label,
            Const.LITERAL_STRUCT,
            lhs.label,
            Const.LITERAL_STRUCT,
            rhs.label);

    long unwindLabel = label + 1;
    long branchLabel = unwindLabel + Node.CLEANUP_LABEL_SIZE + Node.RESUME_LABEL_SIZE;
    manager.dumpFormatln("\t\tto label %%%d unwind label %%%d", branchLabel, unwindLabel);

    manager.cleanupError();
    destructDependencies(manager, this);
    manager.resumeError();
  }


  public long destructDependencies(Manager manager, Node caller) {
    long maxParentDep = super.destructDependencies(manager, caller);
    if (rhs != caller) {
      if (rhs.label >= maxParentDep) {
        manager.dumpln("\t; l rhs (" + rhs.label + ") maxdep (" + maxParentDep + ")");
        maxParentDep = Math.max(rhs.destruct(manager, this), maxParentDep);
      }
      if (lhs != caller && lhs.label >= maxParentDep) {
        manager.dumpln("\t; l lhs (" + lhs.label + ") maxdep (" + maxParentDep + ")");
        maxParentDep = Math.max(lhs.destruct(manager, this), maxParentDep);
      }
    }

    return maxParentDep;
  }
}