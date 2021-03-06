package minierlang.exp.terms;

import minierlang.Const;
import minierlang.Manager;
import minierlang.Node;
import minierlang.exp.Term;

public class Number extends Term {
  Integer integerValue;
  Float floatValue;

  public Number(Float value) {
    floatValue = value;
    this.subgraphSize = 1 + Node.CLEANUP_LABEL_SIZE + Node.RESUME_LABEL_SIZE;
  }

  public Number(Integer value) {
    integerValue = value;
    this.subgraphSize = 1 + Node.CLEANUP_LABEL_SIZE + Node.RESUME_LABEL_SIZE;
  }

  public void generateCode(Manager manager, Node parent) {
    super.generateCode(manager, parent);
    manager.dumpln("\t; start " + this.getClass().getName() + " (" + subgraphSize + ")");
    
    label = allocate(manager);
    if (integerValue != null) {
      manager.dumpFormatln(
              "\tinvoke void %s(%%%s* %%%d, i32 %s)",
              Const.LITERAL_CONSTRUCT_INT, Const.LITERAL_STRUCT, label, integerValue);
    } else {
      manager.dumpFormatln(
              "\tinvoke void %s(%%%s* %%%d, double %s)",
              Const.LITERAL_CONSTRUCT_FLOAT, Const.LITERAL_STRUCT, label, floatValue);
    }

    long unwindLabel = label + 1;
    long branchLabel = unwindLabel + Node.CLEANUP_LABEL_SIZE + Node.RESUME_LABEL_SIZE;
    manager.dumpFormatln("\t\tto label %%%d unwind label %%%d", branchLabel, unwindLabel);

    manager.cleanupError();
    destructDependencies(manager, this);
    manager.resumeError();
  }
}
