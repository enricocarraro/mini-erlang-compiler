package minierlang.exp.terms;

import minierlang.Node;
import minierlang.exp.Term;
import minierlang.Manager;
import minierlang.Const;

public class Atom extends Term {
  String value;

  public Atom(String atom) {
    value = atom;
    this.subgraphSize = 1 + Node.CLEANUP_LABEL_SIZE + Node.RESUME_LABEL_SIZE;
  }

  public void generateCode(Manager manager, Node parent) {
    super.generateCode(manager, parent);
    manager.dumpln("\t; start " + this.getClass().getName());
    long atomId = manager.getAtom(value);
    label = allocate(manager);
    manager.dump(
        String.format(
            "\tinvoke void %s(%%%s*", Const.LITERAL_CONSTRUCT_ATOM, Const.LITERAL_STRUCT));

    manager.dumpln(String.format(" %%%d, i64 %d)", label, atomId));

    long unwindLabel = label + 1;
    long branchLabel = unwindLabel + Node.CLEANUP_LABEL_SIZE + Node.RESUME_LABEL_SIZE;
    manager.dumpln(String.format("\t\tto label %%%d unwind label %%%d", branchLabel, unwindLabel));

    manager.cleanupError(manager);
    destructDependencies(manager, this);
    manager.resumeError(manager);
  }
}