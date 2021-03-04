package minierlang.exp.terms;

import minierlang.Const;
import minierlang.Manager;
import minierlang.Node;
import minierlang.exp.Expression;
import minierlang.exp.Term;

public class AltList extends Term {
  public Expression head;
  public Expression tail;

  public AltList(Expression head, Expression tail) {
    this.head = head;
    this.tail = (tail == null ? new List("") : tail);
    this.subgraphSize =
        3
            + CLEANUP_LABEL_SIZE
            + RESUME_LABEL_SIZE
            + this.head.subgraphSize
            + this.tail.subgraphSize;
    ;
  }

  public AltList(String string) {
    if (string.length() > 0) {
      this.head = new Number((int) string.charAt(0));
      this.tail = string.length() > 1 ? new AltList(string.substring(1)) : new List("");
    }
    this.subgraphSize =
        3
            + CLEANUP_LABEL_SIZE
            + RESUME_LABEL_SIZE
            + this.head.subgraphSize
            + this.tail.subgraphSize;
    ;
  }

  public void generateCode(Manager manager, Node parent) {
    super.generateCode(manager, parent);
    manager.dumpln("\t; start " + this.getClass().getName());

    head.generateCode(manager, this);
    manager.dumpCodeLabel();
    tail.generateCode(manager, this);
    manager.dumpCodeLabel();

    label = allocate(manager);
    manager.dumpFormatln(
        "\tinvoke void %s(%%%s* %%%d, %%%s* %%%d, %%%s* %%%d)",
        Const.LITERAL_CONSTRUCT_LIST_ELEMENT,
        Const.LITERAL_STRUCT,
        label,
        Const.LITERAL_STRUCT,
        head.label,
        Const.LITERAL_STRUCT,
        tail.label);
    long afterUnwind = manager.getCurrentLabel() + CLEANUP_LABEL_SIZE + RESUME_LABEL_SIZE;
    manager.dumpFormatln(
        "\t\tto label %%%d unwind label %%%d", afterUnwind, manager.getCurrentLabel());
    manager.cleanupError();
    destructDependencies(manager, this);
    manager.resumeError();
  }

  public long destructDependencies(Manager manager, Node caller) {
    long maxParentDep = super.destructDependencies(manager, caller);
    if (head != caller) {
      if (head.label > maxParentDep) {
        maxParentDep = Math.max(head.destruct(manager, this), maxParentDep);
      }
      if (tail != caller) {
        maxParentDep = Math.max(tail.destructDependencies(manager, this), maxParentDep);
      }
    }

    return maxParentDep;
  }
}