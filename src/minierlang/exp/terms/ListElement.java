package minierlang.exp.terms;

import minierlang.Const;
import minierlang.Manager;
import minierlang.Node;
import minierlang.exp.Expression;
import minierlang.exp.Term;

public class ListElement extends Term {
  Expression head;
  ListElement tail;
  long length;

  public ListElement(String string) {
    subgraphSize = 2;
    if (string.length() > 0) {
      this.head = new Number((int) string.charAt(0));
      subgraphSize += head.subgraphSize;
      if (string.length() > 1) {
        this.tail = new ListElement(string.substring(1));
        subgraphSize += tail.subgraphSize + 1;
      }
    }
    this.length = computeSize();
  }

  public ListElement(Expression head, ListElement tail) {
    if (head instanceof List && tail == null) {
      // Alternative list declaration: handles [ expression | [ expression_sequence ] ].
      // (see alt_list_tail in parser.cup)
      List list = (List) head;
      this.head = list.head;
      this.tail = list.tail;
    } else {
      this.head = head;
      this.tail = tail;
    }
    this.length = computeSize();
    this.subgraphSize =
        this.head.subgraphSize + 2 + (this.tail != null ? this.tail.subgraphSize + 1 : 0);
  }

  public ListElement(Expression tail) {
    if (head instanceof List && tail == null) {
      // Alternative list declaration: handles [ expression | [ expression_sequence ] ].
      // (see alt_list_tail in parser.cup)
      
      List list = (List) head;
      this.head = list.head;
      this.tail = list.tail;
    } else {
      this.head = tail;
    }
    this.length = computeSize();
    this.subgraphSize =
        this.head.subgraphSize + 2 + (this.tail != null ? this.tail.subgraphSize + 1 : 0);
  }

  private long computeSize() {
    long len = 1;
    if (tail != null) {
      len += tail.length;
    }

    return len;
  }

  public void generateCode(Manager manager, Node parent, long oldArrayPointer, long structPointer) {
    super.generateCode(manager, parent);
    manager.dumpln("\t; start " + this.getClass().getName());

    head.generateCode(manager, this);
    label = head.label;

    manager.dumpCodeLabel();
    long arrayPointer = label = manager.genLabel();
    manager.dumpFormatln(
        "\t%%%d = getelementptr inbounds %%%s, %%%s* %%%d, i64 1",
        arrayPointer, Const.LITERAL_STRUCT, Const.LITERAL_STRUCT, oldArrayPointer);
    manager.dumpFormatln(
        "\tstore %%%s* %%%d, %%%s** %%%d, align 8",
        Const.LITERAL_STRUCT, arrayPointer, Const.LITERAL_STRUCT, structPointer);
    manager.dumpFormatln(
        "\tinvoke void %s(%%%s* %%%d, %%%s* nonnull align 8 dereferenceable(16) %%%d)",
        Const.LITERAL_CONSTRUCT_COPY,
        Const.LITERAL_STRUCT,
        arrayPointer,
        Const.LITERAL_STRUCT,
        head.label);

    if (tail != null) {
      long unwindLabel = manager.getCurrentLabel() + tail.subgraphSize + 1;

      manager.dumpFormatln(
          "\t\tto label %%%d unwind label %%%d", manager.getCurrentLabel(), unwindLabel);
      manager.dumpCodeLabel();
      tail.generateCode(manager, this, arrayPointer, structPointer);
    } else {
      manager.dumpFormatln(
          "\t\tto label %%%d unwind label %%%d",
          manager.getCurrentLabel() + CLEANUP_LABEL_SIZE + RESUME_LABEL_SIZE + List.PHI_SIZE + 3,
          manager.getCurrentLabel());
    }
  }

  public long destruct(Manager manager, Node caller) {
    return destructDependencies(manager, caller);
  }

  public long destructDependencies(Manager manager, Node caller) {
    long maxParentDep = super.destructDependencies(manager, caller);
    if (head != caller) {
      if (head.label > maxParentDep) {
        maxParentDep = Math.max(head.destruct(manager, this), maxParentDep);
      }
      if (tail != caller && tail != null) {
        maxParentDep = Math.max(tail.destructDependencies(manager, this), maxParentDep);
      }
    }

    return maxParentDep;
  }
}