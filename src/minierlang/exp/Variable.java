package minierlang.exp;

import minierlang.Const;
import minierlang.Manager;
import minierlang.Node;

public class Variable extends Expression {
  public String name;

  public Variable(String name) {
    this.name = name;
    this.subgraphSize = 1 + Node.CLEANUP_LABEL_SIZE + Node.RESUME_LABEL_SIZE;
  }

  public void generateCode(Manager manager, Node parent) {
    super.generateCode(manager, parent);
    manager.dumpln("\t; start " + this.getClass().getName() + " (" + name + ") ");

    Long llvmId = manager.getFromST(name);
    if (llvmId == null) {
      label = allocate(manager);
      manager.putIntoST(name, label);
      manager.dumpln(
          String.format(
              "\tinvoke void %s(%%%s* %%%d)",
              Const.LITERAL_CONSTRUCT_EMPTY, Const.LITERAL_STRUCT, label));

      long unwindLabel = label + 1;
      long branchLabel = unwindLabel + Node.CLEANUP_LABEL_SIZE + Node.RESUME_LABEL_SIZE;
      manager.dumpln(
          String.format("\t\tto label %%%d unwind label %%%d", branchLabel, unwindLabel));

      manager.cleanupError(manager);
      destructDependencies(manager, this);
      manager.resumeError(manager);
    } else {
      label = llvmId;
      // Nop instruction to make subgraphSize predictable, an optimizer can easily remove it.
      for (long i = 0; i < subgraphSize; i++) {
        manager.dumpln("\t%" + manager.genLabel() + " = add i1 0, 0\t; No-op");
      }
      manager.dumpln("\tbr label %" + manager.getCurrentLabel() + ":");
      label = llvmId;
    }
  }

  // Method to handle variables in functions' argument lists.
  public void generateCode(Manager manager, Node parent, Long argumentLabel) {
    super.generateCode(manager, parent);
    label = argumentLabel;

    Long llvmId = manager.getFromST(name);
    if (llvmId == null) {
      llvmId = label;
      manager.putIntoST(name, llvmId);
    }
  }
}
