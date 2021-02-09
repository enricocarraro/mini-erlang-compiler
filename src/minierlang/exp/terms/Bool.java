package minierlang.exp.terms;

import minierlang.Const;
import minierlang.Manager;
import minierlang.Node;
import minierlang.exp.Term;

public class Bool extends Term {
  public String value;

  public Bool(String bool) {
    value = bool;
    this.subgraphSize = 1 + Node.CLEANUP_LABEL_SIZE + Node.RESUME_LABEL_SIZE;
  }

  public void generateCode(Manager manager, Node parent) {
    super.generateCode(manager, parent);
    manager.dumpln("\t; start " + this.getClass().getName());
    label = allocate(manager);
    manager.dumpln(
        String.format(
            "\tinvoke void %s(%%%s* %%%d, i1 zeroext %s)",
            Const.LITERAL_CONSTRUCT_BOOLEAN, Const.LITERAL_STRUCT, label, value));

    long unwindLabel = label + 1;
    long branchLabel = unwindLabel + Node.CLEANUP_LABEL_SIZE + Node.RESUME_LABEL_SIZE;
    manager.dumpln(String.format("\t\tto label %%%d unwind label %%%d", branchLabel, unwindLabel));

    manager.cleanupError(manager);
    destructDependencies(manager, this);
    manager.resumeError(manager);
  }
}
