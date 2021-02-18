package minierlang.exp;

import minierlang.Const;
import minierlang.Manager;
import minierlang.Node;

public abstract class Expression extends Node {

  public Expression() {}

  public long allocate(Manager manager) {
    long newLabel = manager.genLabel();
    manager.dumpFormatln("\t%%%d = alloca %%%s, align 8", newLabel, Const.LITERAL_STRUCT);
    return newLabel;
  }

  public long destruct(Manager manager, Node caller) {
    long maxParentLabel = destructDependencies(manager, caller);
    if (maxParentLabel <= label) {
      manager.dumpFormatln(
          "\tcall void %s(%%%s* %%%d) #13", Const.LITERAL_DESTRUCT, Const.LITERAL_STRUCT, label);
    }
    return Math.max(maxParentLabel, label);
  }

  public long destructDependencies(Manager manager, Node caller) {
    if (caller != parent) {
      return parent.destruct(manager, this);
    }

    return 0;
  }

  protected void generateReturn(Manager manager) {
    long returnLabel = manager.getReturnLabel();

    manager.dumpCodeLabel();
    manager.dumpFormatln(
        "\tinvoke void %s(%%%s* %%%d, %%%s* nonnull align 8 dereferenceable(16) %%%d)",
        Const.LITERAL_CONSTRUCT_COPY,
        Const.LITERAL_STRUCT,
        returnLabel,
        Const.LITERAL_STRUCT,
        label);

    long unwindLabel = manager.getCurrentLabel();
    long branchLabel = unwindLabel + CLEANUP_LABEL_SIZE + RESUME_LABEL_SIZE;
    manager.dumpFormatln("\t\tto label %%%d unwind label %%%d", branchLabel, unwindLabel);

    manager.cleanupError();
    destruct(manager, this);
    manager.resumeError();

    manager.dumpCodeLabel();
    destruct(manager, this);
    manager.dumpln("\tret void");
  }
} 