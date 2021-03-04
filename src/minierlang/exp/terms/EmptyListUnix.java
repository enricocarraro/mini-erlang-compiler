package minierlang.exp.terms;

import minierlang.Const;
import minierlang.Manager;
import minierlang.Node;
import minierlang.exp.Term;

public class EmptyListUnix extends Term {

  public EmptyListUnix() {
    subgraphSize = 13 + CLEANUP_LABEL_SIZE*2 + RESUME_LABEL_SIZE;
  }

  public void generateCode(Manager manager, Node parent) {
    super.generateCode(manager, parent);
    manager.dumpln("\t; start " + this.getClass().getName());

    long listLabel = manager.genLabel();
    manager.dumpFormatln("\t%%%d = alloca %%%s, align 8", listLabel, Const.STD_LIST_UNIX);

    long initListLabel = manager.genLabel();
    manager.dumpFormatln("\t%%%d = alloca %%%s, align 8", initListLabel, Const.STD_INIT_LIST_UNIX);

    long allocatorLabel = manager.genLabel();
    manager.dumpFormatln("\t%%%d = alloca %%%s, align 1", allocatorLabel, Const.STD_ALLOCATOR);

    manager.dumpFormatln(
        "\tcall void %s(%%%s* %%%d) #3",
        Const.LITERAL_LIST_INIT_ALLOCATOR_UNIX, Const.STD_INIT_LIST_UNIX, initListLabel);
    manager.dumpFormatln(
        "\tcall void %s(%%%s* %%%d) #3",
        Const.LITERAL_EMPTY_LIST_INIT_ALLOCATOR_UNIX, Const.STD_ALLOCATOR, allocatorLabel);

    long initListBitcast = manager.genLabel();

    manager.dumpFormatln(
        "\t%%%d = bitcast %%%s* %%%d to { %%%s*, i64 }*",
        initListBitcast, Const.STD_INIT_LIST_UNIX, initListLabel, Const.LITERAL_STRUCT);

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

    manager.dumpFormatln(
        "\tinvoke void %s(%%%s* %%%d, %%%s* %%%d, i64 %%%d, %%%s* dereferenceable(1) %%%d)",
        Const.LITERAL_LIST_ALLOCATOR_UNIX,
        Const.STD_LIST_UNIX,
        listLabel,
        Const.LITERAL_STRUCT,
        loadLiteralPointer1,
        loadLiteralPointer2,
        Const.STD_ALLOCATOR,
        allocatorLabel);

    manager.dumpFormatln(
        "\t\tto label %%%d unwind label %%%d",
        manager.getCurrentLabel(), manager.getCurrentLabel() + 2);

    manager.dumpCodeLabel();
    label = allocate(manager);
    manager.dumpFormatln(
        "\tinvoke void %s(%%%s* %%%d, %%%s* %%%d) #3",
        Const.LITERAL_LIST_CONSTRUCT_UNIX,
        Const.LITERAL_STRUCT,
        label,
        Const.STD_LIST_UNIX,
        listLabel);

    manager.dumpFormatln(
        "\t\tto label %%%d unwind label %%%d",
        manager.getCurrentLabel(), manager.getCurrentLabel() + 5);

    manager.dumpCodeLabel();
    manager.dumpFormatln(
        "\tcall void %s(%%%s* %%%d) #3",
        Const.LITERAL_LIST_DESTRUCT_UNIX, Const.STD_LIST_UNIX, listLabel);
    manager.dumpFormatln(
        "\tcall void %s(%%%s* %%%d) #3",
        Const.LITERAL_LIST_ALLOCATOR_DESTRUCT_UNIX, Const.STD_ALLOCATOR, allocatorLabel);

    manager.dumpFormatln(
        "\tbr label %%%d",
        manager.getCurrentLabel() + CLEANUP_LABEL_SIZE * 2 + RESUME_LABEL_SIZE + 2);

    manager.cleanupError();
    manager.dumpFormatln("\tbr label %%%d", manager.getCurrentLabel() + CLEANUP_LABEL_SIZE);
    manager.cleanupError();
    manager.dumpFormatln(
        "\tcall void %s(%%%s* %%%d) #3",
        Const.LITERAL_LIST_DESTRUCT_UNIX, Const.STD_LIST_UNIX, listLabel);

    manager.dumpBrNextLabel();
    manager.dumpCodeLabel();
    manager.dumpFormatln(
        "\tcall void %s(%%%s* %%%d) #3",
        Const.LITERAL_LIST_ALLOCATOR_DESTRUCT_UNIX, Const.STD_ALLOCATOR, allocatorLabel);
    destructDependencies(manager, this);
    manager.dumpBrNextLabel();
    manager.dumpCodeLabel();
    manager.resumeError();
  }

  public long destructDependencies(Manager manager, Node caller) {
    return super.destructDependencies(manager, caller);
  }
}