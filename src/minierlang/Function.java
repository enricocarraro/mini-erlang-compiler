package minierlang;

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
      manager.dumpln(
          String.format(
              "define void @%s(%%%s* noalias sret align 8 %%%d, %%%s* %%%d) #0 personality i8*"
                  + " bitcast (i32 (...)* @__gxx_personality_v0 to i8*) {",
              manager.getFunctionName(),
              Const.LITERAL_STRUCT,
              returnLabel,
              Const.LITERAL_STRUCT,
              parameterLabel));
    } else {
      manager.dumpln(
          String.format(
              "define void @%s(%%%s* noalias sret align 8 %%%d) #0 personality i8* bitcast (i32"
                  + " (...)* @__gxx_personality_v0 to i8*) {",
              manager.getFunctionName(), Const.LITERAL_STRUCT, returnLabel));
    }
    manager.genLabel();
    long returnPointerLabel = manager.genLabel();
    manager.dumpln(String.format("\t%%%d = alloca i8*, align 8", returnPointerLabel));
    long bitcastReturnPointerLabel = manager.genLabel();

    manager.dumpln(
        String.format(
            "\t%%%d = bitcast %%%s* %%%d to i8*",
            bitcastReturnPointerLabel, Const.LITERAL_STRUCT, returnLabel));
    manager.dumpln(
        String.format(
            "\tstore i8* %%%d, i8** %%%d, align 8", bitcastReturnPointerLabel, returnPointerLabel));

    long resumePointerLabel = manager.genLabel();
    manager.setResumePointer(resumePointerLabel);
    long resumeIntegerLabel = manager.genLabel();
    manager.setResumeInteger(resumeIntegerLabel);
    manager.dumpln(String.format("\t%%%d = alloca i8*, align 8", resumePointerLabel));
    manager.dumpln(String.format("\t%%%d = alloca i32, align 4", resumeIntegerLabel));

    head.generateCode(manager, this);
    if (tail != null) {
      manager.dumpln(manager.genLabel() + ":");
      tail.generateCode(manager, this);
    }

    if (head.argument != null) {
      long badMatchLabel = manager.genLabel();
      long badMatchString = manager.genLabel();
      manager.dumpln(badMatchLabel + ":");
      manager.dumpln(
          String.format("\t%%%d = alloca %%\"%s\", align 8", badMatchString, Const.BASIC_STRING));
      manager.dumpln(
          String.format(
              "\tcall void %s(%%\"%s\"* %%%d, i8* getelementptr inbounds ([13 x i8], [13 x i8]* %s,"
                  + " i64 0, i64 0))",
              Const.BASIC_STRING_CONSTRUCT,
              Const.BASIC_STRING,
              badMatchString,
              Const.BAD_MATCHING_CONST));
      manager.dumpln(
          String.format(
              "\tinvoke void %s(%%\"%s\"* %%%d)",
              Const.THROW_ERROR, Const.BASIC_STRING, badMatchString));

      long trap = manager.genLabel();
      manager.dumpln(String.format("\t\tto label %%%d unwind label %%%d", trap, trap + 1));

      manager.dumpln(trap + ":");
      manager.dumpln(
          String.format(
              "\tcall void %s(%%\"%s\"* %%%d) #12",
              Const.BASIC_STRING_DESTRUCT, Const.BASIC_STRING, badMatchString));
      manager.dumpln("\tcall void @llvm.trap()");
      manager.dumpln("\tunreachable");

      manager.cleanupError(manager);
      manager.dumpln(
          String.format(
              "\tcall void %s(%%\"%s\"* %%%d) #12",
              Const.BASIC_STRING_DESTRUCT, Const.BASIC_STRING, badMatchString));

      long resumeLabel = manager.genLabel();
      manager.dumpln(resumeLabel + ":");
      manager.resumeError(manager);
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
