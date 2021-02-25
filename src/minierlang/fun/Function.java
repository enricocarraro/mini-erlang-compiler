package minierlang.fun;

import minierlang.Const;
import minierlang.Manager;
import minierlang.Node;

public class Function extends Node {
  FunctionClause head;
  FunctionClauseSequence tail;

  public Function(FunctionClause head, FunctionClauseSequence tail) {
    this.head = head;
    this.tail = tail;
    subgraphSize = head.subgraphSize + (tail != null ? tail.subgraphSize : 0);
  }

  public void generateCode(Manager manager, Node parent) {
    super.generateCode(manager, parent);

    manager.checkTailMatch(head, tail);
    manager.setFunctionName(manager.getFunctionName(head.name, head.argument));
    manager.openClause();

    long returnLabel = manager.genLabel();
    manager.setReturnLabel(returnLabel);

    manager.dumpln("; Function Attrs: noinline optnone ssp uwtable");

    if (head.argument != null) {
      long parameterLabel = manager.genLabel();
      manager.setParameterLabel(parameterLabel);
      manager.dumpFormatln(
              "define void %s(%%%s* noalias sret align 8 %%%d, %%%s* %%%d) #0 personality i8*"
                  + " bitcast (i32 (...)* @__gxx_personality_v0 to i8*) {",
              manager.getFunctionName(),
              Const.LITERAL_STRUCT,
              returnLabel,
              Const.LITERAL_STRUCT,
              parameterLabel);
    } else {
      manager.dumpFormatln(
              "define void %s(%%%s* noalias sret align 8 %%%d) #0 personality i8* bitcast (i32"
                  + " (...)* @__gxx_personality_v0 to i8*) {",
              manager.getFunctionName(), Const.LITERAL_STRUCT, returnLabel);
    }
    manager.genLabel();
    long returnPointerLabel = manager.genLabel();
    manager.dumpFormatln("\t%%%d = alloca i8*, align 8", returnPointerLabel);
    long bitcastReturnPointerLabel = manager.genLabel();

    manager.dumpFormatln(
            "\t%%%d = bitcast %%%s* %%%d to i8*",
            bitcastReturnPointerLabel, Const.LITERAL_STRUCT, returnLabel);
    manager.dumpFormatln(
            "\tstore i8* %%%d, i8** %%%d, align 8", bitcastReturnPointerLabel, returnPointerLabel);

    long resumePointerLabel = manager.genLabel();
    manager.setResumePointer(resumePointerLabel);
    long resumeIntegerLabel = manager.genLabel();
    manager.setResumeInteger(resumeIntegerLabel);
    manager.dumpFormatln("\t%%%d = alloca i8*, align 8", resumePointerLabel);
    manager.dumpFormatln("\t%%%d = alloca i32, align 4", resumeIntegerLabel);

    head.generateCode(manager, this);
    if (tail != null) {
      manager.dumpCodeLabel();
      tail.generateCode(manager, this);
    }

    if (head.argument != null) {
      manager.dumpCodeLabel();
      manager.dumpFormatln("\tcall void %s()", Const.BAD_MATCHING_ERROR);
      manager.dumpln("\tret void");
    }
    manager.dumpln("}\n");
    manager.popFunctionSymbols();
  }

  @Override
  public long destruct(Manager manager, Node caller) {
    return 0;
  }

  @Override
  public long destructDependencies(Manager manager, Node caller) {
    // TODO Auto-generated method stub
    return 0;
  }
}
