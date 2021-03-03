package minierlang.exp.terms;

import minierlang.Const;
import minierlang.Manager;
import minierlang.Node;
import minierlang.exp.Expression;
import minierlang.exp.Term;

public class List extends Term {
  public static final int PHI_SIZE = 4;
  Expression head;
  ListElement tail;
  private long length;
  private Long listLabel, arrayLabel;

  public List(String string) {
    subgraphSize = 7;
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

  public List(Expression head, ListElement tail) {
    this.head = head;
    this.tail = tail;
    this.length = computeSize();
    this.subgraphSize =
        7 + (head != null ? head.subgraphSize : 0) + (tail != null ? tail.subgraphSize + 1 : 0);
    ;
  }
  
  

  private long computeSize() {
    long len = 0;
    if (head != null) {
      len = 1;
      if (tail != null) {
        len += tail.length;
      }
    }

    return len;
  }

  public void generateCode(Manager manager, Node parent) {
    super.generateCode(manager, parent);
    manager.dumpln("\t; start " + this.getClass().getName());

    listLabel = manager.genLabel();
    manager.dumpFormatln("\t%%%d = alloca %%%s, align 8", listLabel, Const.STD_LIST);

    long initListLabel = manager.genLabel();
    manager.dumpFormatln("\t%%%d = alloca %%%s, align 8", initListLabel, Const.STD_INIT_LIST);

    long arrayPointer;
    if (head != null) {
      arrayLabel = manager.genLabel();
      manager.dumpFormatln(
          "\t%%%d = alloca [%d x %%%s], align 8", arrayLabel, length, Const.LITERAL_STRUCT);

      long structPointer = manager.genLabel();
      manager.dumpFormatln("\t%%%d = alloca %%%s*, align 8", structPointer, Const.LITERAL_STRUCT);

      head.generateCode(manager, this);
      manager.dumpCodeLabel();
      arrayPointer = manager.genLabel();
      manager.dumpFormatln(
          "\t%%%d = getelementptr inbounds [%d x %%%s], [%d x %%%s]* %%%d, i64 0, i64 0",
          arrayPointer, length, Const.LITERAL_STRUCT, length, Const.LITERAL_STRUCT, arrayLabel);
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
      listInitializationUnwind(manager, arrayPointer, structPointer);

      manager.dumpCodeLabel();
      long initListPointer = manager.genLabel();
      manager.dumpFormatln(
          "\t%%%d = getelementptr inbounds %%%s, %%%s* %%%d, i32 0, i32 0",
          initListPointer, Const.STD_INIT_LIST, Const.STD_INIT_LIST, initListLabel);

      long arrayPointerAfterInit = manager.genLabel();
      manager.dumpFormatln(
          "\t%%%d = getelementptr inbounds [%d x %%%s], [%d x %%%s]* %%%d, i64 0, i64 0",
          arrayPointerAfterInit,
          length,
          Const.LITERAL_STRUCT,
          length,
          Const.LITERAL_STRUCT,
          arrayLabel);
      manager.dumpFormatln(
          "\tstore %%%s* %%%d, %%%s** %%%d, align 8",
          Const.LITERAL_STRUCT, arrayPointerAfterInit, Const.LITERAL_STRUCT, initListPointer);

      long initListPointer2 = manager.genLabel();
      manager.dumpFormatln(
          "\t%%%d = getelementptr inbounds %%%s, %%%s* %%%d, i32 0, i32 1",
          initListPointer2, Const.STD_INIT_LIST, Const.STD_INIT_LIST, initListLabel);
      manager.dumpFormatln("\tstore i64 %d, i64* %%%d, align 8", length, initListPointer2);

    } else {
      manager.dumpFormatln(
          "\tcall void %s(%%%s* %%%d) #13",
          Const.LITERAL_EMPTY_LIST_INIT_ALLOCATOR, Const.STD_INIT_LIST, initListLabel);
      arrayPointer = 0;
    }

    long initListBitcast = manager.genLabel();

    manager.dumpFormatln(
        "\t%%%d = bitcast %%%s* %%%d to { %%%s*, i64 }*",
        initListBitcast, Const.STD_INIT_LIST, initListLabel, Const.LITERAL_STRUCT);

    long initListBitcastPointer1 = manager.genLabel();
    manager.dumpFormatln(
        "\t%%%d = getelementptr inbounds { %%%s*, i64 }, { %%%s*, i64 }* %%%d, i32 0, i32 0",
        initListBitcastPointer1, Const.LITERAL_STRUCT, Const.LITERAL_STRUCT, initListBitcast);
    long loadLiteralPointer1 = manager.genLabel();
    manager.dumpFormatln(
        "\t%%%d = load %%%s*, %%%s** %%%d, align 8",
        loadLiteralPointer1, Const.LITERAL_STRUCT, Const.LITERAL_STRUCT, initListBitcastPointer1);

    long initListBitcastPointer2 = manager.genLabel();
    manager.dumpFormatln(
        "\t%%%d = getelementptr inbounds { %%%s*, i64 }, { %%%s*, i64 }* %%%d, i32 0, i32 1",
        initListBitcastPointer2, Const.LITERAL_STRUCT, Const.LITERAL_STRUCT, initListBitcast);
    long loadLiteralPointer2 = manager.genLabel();
    manager.dumpFormatln(
        "\t%%%d = load i64, i64* %%%d, align 8", loadLiteralPointer2, initListBitcastPointer2);
    label = allocate(manager);
    if (head != null) {
      manager.dumpFormatln(
          "\tinvoke void %s(%%%s* %%%d, %%%s* %%%d, i64 %%%d)",
          Const.LITERAL_LIST_INIT_ALLOCATOR,
          Const.STD_LIST,
          listLabel,
          Const.LITERAL_STRUCT,
          loadLiteralPointer1,
          loadLiteralPointer2);

      long afterUnwind =
          manager.getCurrentLabel() + CLEANUP_LABEL_SIZE + RESUME_LABEL_SIZE + PHI_SIZE + 4;
      manager.dumpFormatln(
          "\t\tto label %%%d unwind label %%%d", afterUnwind, manager.getCurrentLabel());

      manager.cleanupError();
      manager.dumpFormatln("\tbr label %%%d", manager.getCurrentLabel());
      long destructListLabel1 = manager.getCurrentLabel();
      manager.dumpCodeLabel();
      destructListElements(manager, destructListLabel1);
      destructDependencies(manager, this);
      manager.resumeError();

      manager.dumpCodeLabel();
    } else {
      manager.dumpFormatln(
          "\tcall void %s(%%%s* %%%d, %%%s* %%%d, i64 %%%d)",
          Const.LITERAL_LIST_INIT_ALLOCATOR,
          Const.STD_LIST,
          listLabel,
          Const.LITERAL_STRUCT,
          loadLiteralPointer1,
          loadLiteralPointer2);
    }

    manager.dumpFormatln(
        "\tinvoke void %s(%%%s* %%%d, %%%s* %%%d)",
        Const.LITERAL_LIST_ALLOCATOR, Const.LITERAL_STRUCT, label, Const.STD_LIST, listLabel);

    long afterUnwind2 =
        manager.getCurrentLabel()
            + CLEANUP_LABEL_SIZE
            + RESUME_LABEL_SIZE
            + (head != null ? PHI_SIZE + 4 : 0);
    manager.dumpFormatln(
        "\t\tto label %%%d unwind label %%%d", afterUnwind2, manager.getCurrentLabel());
    manager.cleanupError();
    manager.dumpFormatln(
        "\tcall void %s(%%%s* %%%d) #13",
        Const.LITERAL_LIST_ALLOCATOR_DESTRUCT, Const.STD_LIST, listLabel);

    if (head != null) {
      manager.dumpBrNextLabel();
      long destructListLabel2 = manager.dumpCodeLabel();
      destructListElements(manager, destructListLabel2);
    }

    destructDependencies(manager, this);
    manager.resumeError();
    long destructLabel = manager.dumpCodeLabel();
    manager.dumpFormatln(
        "\tcall void %s(%%%s* %%%d) #13",
        Const.LITERAL_LIST_ALLOCATOR_DESTRUCT, Const.STD_LIST, listLabel);
    destructListElements(manager, destructLabel);
    destructDependencies(manager, parent);
    manager.dumpBrNextLabel();
  }

  public long destructDependencies(Manager manager, Node caller) {
    long maxParentDep = super.destructDependencies(manager, caller);
    if (head != caller) {
      if (head != null && head.label > maxParentDep) {
        maxParentDep = Math.max(head.destruct(manager, this), maxParentDep);
      }
      if (tail != caller && tail != null) {
        maxParentDep = Math.max(tail.destructDependencies(manager, this), maxParentDep);
      }
    }

    return maxParentDep;
  }

  private void listInitializationUnwind(Manager manager, long arrayPointer, long literalPointer) {
    long cleanup = manager.getCurrentLabel();
    manager.cleanupError();

    long literalPointerLoad = manager.genLabel();
    manager.dumpFormatln(
        "\t%%%d = load %%%s*, %%%s** %%%d, align 8",
        literalPointerLoad, Const.LITERAL_STRUCT, Const.LITERAL_STRUCT, literalPointer);

    long compareLabel = manager.genLabel();
    manager.dumpFormatln(
        "\t%%%d = icmp eq %%%s* %%%d, %%%d",
        compareLabel, Const.LITERAL_STRUCT, arrayPointer, literalPointerLoad);

    long destructCodeLabel = manager.getCurrentLabel() + PHI_SIZE;
    manager.dumpFormatln(
        "\tbr i1 %%%d, label %%%d, label %%%d",
        compareLabel, destructCodeLabel, manager.getCurrentLabel());

    phiDestruct(manager, arrayPointer, literalPointerLoad, cleanup);

    manager.dumpCodeLabel();
    destructDependencies(manager, this); // should destruct only dependencies, not list
    manager.resumeError();
  }

  private void phiDestruct(
      Manager manager, long arrayPointer, long literalPointer, long previousCodeLabel) {
    long phiCodeLabel = manager.dumpCodeLabel();

    long phiLabel = manager.genLabel();
    long prevElementPointer = manager.genLabel();

    manager.dumpFormatln(
        "\t%%%d = phi %%%s* [ %%%d, %%%d ], [ %%%d, %%%d ]",
        phiLabel,
        Const.LITERAL_STRUCT,
        literalPointer,
        previousCodeLabel,
        prevElementPointer,
        phiCodeLabel);

    manager.dumpFormatln(
        "\t%%%d = getelementptr inbounds %%%s, %%%s* %%%d, i64 -1",
        prevElementPointer, Const.LITERAL_STRUCT, Const.LITERAL_STRUCT, phiLabel);

    manager.dumpFormatln(
        "\tcall void %s(%%%s* %%%d) #13",
        Const.LITERAL_DESTRUCT, Const.LITERAL_STRUCT, prevElementPointer);

    long compareLabel = manager.genLabel();
    manager.dumpFormatln(
        "\t%%%d = icmp eq %%%s* %%%d, %%%d",
        compareLabel, Const.LITERAL_STRUCT, prevElementPointer, arrayPointer);

    manager.dumpFormatln(
        "\tbr i1 %%%d, label %%%d, label %%%d",
        compareLabel, manager.getCurrentLabel(), phiCodeLabel);
  }

  private void destructListElements(Manager manager, long destructLabel) {
    if (head != null) {
      long arrayPointer = manager.genLabel();
      manager.dumpFormatln(
          "\t%%%d = getelementptr inbounds [%d x %%%s], [%d x %%%s]* %%%d, i32 0, i32 0",
          arrayPointer, length, Const.LITERAL_STRUCT, length, Const.LITERAL_STRUCT, arrayLabel);

      long literalPointer = manager.genLabel();
      manager.dumpFormatln(
          "\t%%%d = getelementptr inbounds %%%s, %%%s* %%%d, i64 %d",
          literalPointer, Const.LITERAL_STRUCT, Const.LITERAL_STRUCT, arrayPointer, length);
      manager.dumpFormatln("\tbr label %%%d", manager.getCurrentLabel());
      phiDestruct(manager, arrayPointer, literalPointer, destructLabel);

      manager.dumpCodeLabel();
    }
  }
}