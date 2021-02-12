package minierlang.exp.terms;

import minierlang.Node;
import minierlang.exp.Term;
import minierlang.Manager;
import minierlang.Const;

public class Atom extends Term {
  private String stringForm;
  private long atomId;
  public Atom(String atom) {
    stringForm = atom;
    this.subgraphSize = 1 + Node.CLEANUP_LABEL_SIZE + Node.RESUME_LABEL_SIZE;
  }

  protected void dumpConstructor(Manager manager, long label) {
    manager.dump(
        String.format(
            "\tinvoke void %s(%%%s* %%%d, i64 %d)", Const.LITERAL_CONSTRUCT_ATOM, Const.LITERAL_STRUCT, label, atomId));
  }

  public void generateCode(Manager manager, Node parent) {
    super.generateCode(manager, parent);
    manager.dumpln("\t; start " + this.getClass().getName());
    
    atomId = manager.getAtom(stringForm);
    label = allocate(manager);    
    dumpConstructor(manager, label);

    long unwindLabel = label + 1;
    long branchLabel = unwindLabel + Node.CLEANUP_LABEL_SIZE + Node.RESUME_LABEL_SIZE;
    manager.dumpln(String.format("\t\tto label %%%d unwind label %%%d", branchLabel, unwindLabel));

    manager.cleanupError(manager);
    destructDependencies(manager, this);
    manager.resumeError(manager);
  }
}