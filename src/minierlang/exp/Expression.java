package minierlang.exp;

import minierlang.Const;
import minierlang.Manager;
import minierlang.Node;

public abstract class Expression extends Node {

  public Expression() {}

  protected long allocate(Manager manager) {
    long newLabel = manager.genLabel();
    manager.dumpln(String.format("\t%%%d = alloca %%%s, align 8", newLabel, Const.LITERAL_STRUCT));
    return newLabel;
  }

  public long destruct(Manager manager, Node caller) {
    long maxParentLabel = destructDependencies(manager, caller);
    
    if(maxParentLabel < label) {
    manager.dumpln(
        String.format(
            "\tcall void %s(%%%s* %%%d) ", Const.LITERAL_DESTRUCT, Const.LITERAL_STRUCT, label));
    return label;
    }
    return maxParentLabel;
  }

  public long destructDependencies(Manager manager, Node caller) {
    if (caller != parent) {
      return parent.destruct(manager, this);
    }
    
    return 0;
    
  }

  protected void generateReturn(Manager manager) {
    long returnLabel = manager.getReturnLabel();

    manager.dumpln(manager.genLabel() + ": ");
    manager.dumpln(
        String.format(
            "\tinvoke void %s(%%%s* %%%d, %%%s* nonnull align 8 dereferenceable(16) %%%d)",
            Const.LITERAL_CONSTRUCT_COPY,
            Const.LITERAL_STRUCT,
            returnLabel,
            Const.LITERAL_STRUCT,
            label));

    long unwindLabel = manager.getCurrentLabel();
    long branchLabel = unwindLabel + CLEANUP_LABEL_SIZE + RESUME_LABEL_SIZE;
    manager.dumpln(String.format("\t\tto label %%%d unwind label %%%d", branchLabel, unwindLabel));

    manager.cleanupError(manager);
    destruct(manager, this);
    manager.resumeError(manager);

    long rbl = manager.genLabel();
    if (branchLabel != rbl) {
      manager.pSynError(
          "impossible: labels generated are not counted right: " + branchLabel + " != " + rbl);
    }
    manager.dumpln(branchLabel + ":");
    destruct(manager, this);
    manager.dumpln("\tret void");
  }
} 