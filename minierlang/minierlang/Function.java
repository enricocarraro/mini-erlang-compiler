package minierlang;

public class Function extends Node {
  FunctionClause head;
  FunctionClauseSequence tail;

  public Function(FunctionClause head, FunctionClauseSequence tail) {
    this.head = head;
    this.tail = tail;
    subgraphSize = head.subgraphSize + (tail != null ? tail.subgraphSize : 0);
  }

  // TODO: Move this cleanupError and resumeError into Manager
  public void generateCode(Manager manager) {
    // TODO: Check for definition of different function clauses (different name) and
    // launch semantic
    // error
    manager.setFunctionName(head.name + "_" + (head.argument != null ? 1 : 0));

    long returnLabel = manager.genLabel();
    manager.setReturnLabel(returnLabel);
    if (head.argument != null) {
      long parameterLabel = manager.genLabel();
      manager.setParameterLabel(parameterLabel);
      manager.dumpln(
          String.format(
              "define void @%s(%%s* noalias sret align 8 %%d, %%s* %%d) #0 personality i8* bitcast"
                  + " (i32 (...)* @__gxx_personality_v0 to i8*) {",
              manager.getFunctionName(), Const.LITERAL_STRUCT, returnLabel, Const.LITERAL_STRUCT, parameterLabel));
    } else {
      manager.dumpln(
          String.format(
              "define void @%s(%%s* noalias sret align 8 %%d) #0 personality i8* bitcast (i32"
                  + " (...)* @__gxx_personality_v0 to i8*) {",
              head.name, Const.LITERAL_STRUCT, returnLabel));
    }

    long returnPointerLabel = manager.genLabel();
    manager.dumpln(String.format("%%d = alloca i8*, align 8", returnPointerLabel));
    long bitcastReturnPointerLabel = manager.genLabel();

    manager.dumpln(
        String.format(
            "%%d = bitcast %%s* %%d to i8*",
            bitcastReturnPointerLabel, Const.LITERAL_STRUCT, returnLabel));
    manager.dumpln(
        String.format(
            "store i8* %%d, i8** %%d, align 8", bitcastReturnPointerLabel, returnPointerLabel));

    long resumePointerLabel = manager.genLabel();
    manager.setResumePointer(resumePointerLabel);
    long resumeIntegerLabel = manager.genLabel();
    manager.setResumeInteger(resumeIntegerLabel);
    manager.dumpln(String.format("%%d = alloca i8*, align 8", resumePointerLabel));
    manager.dumpln(String.format("%%d = alloca i32, align 4", resumeIntegerLabel));

    long resumeLabel = 7 + resumeIntegerLabel;
    manager.setResumeLabel(resumeLabel);

    head.generateCode(manager);
    if(tail != null) {
    	tail.generateCode(manager);
    }

    long badMatchLabel = manager.genLabel();
    long badMatchString = manager.genLabel();
    manager.dumpln(badMatchLabel + ":");
    manager.dumpln(
        String.format("%%d = alloca %\"%s\", align 8", badMatchString, Const.BASIC_STRING));
    manager.dumpln(
        String.format(
            "call void %s(%\"%s\"* %%d, i8* getelementptr inbounds ([13 x i8], [13 x i8]* %s, i64"
                + " 0, i64 0))",
            Const.BASIC_STRING_CONSTRUCT,
            Const.BASIC_STRING,
            badMatchString,
            Const.BAD_MATCHING_CONST));
    manager.dumpln(
        String.format(
            "invoke void %s(%\"%s\"* %12)", Const.THROW_ERROR, Const.BASIC_STRING, badMatchString));

    long trap = manager.genLabel();
    manager.dumpln(String.format("\tto label %%d unwind label %%d", trap, trap + 1));

    manager.dumpln(trap + ":");
    manager.dumpln(
        String.format(
            "call void %s(%\"%s\"* %%d) #12",
            Const.BASIC_STRING_DESTRUCT, Const.BASIC_STRING, badMatchString));
    manager.dumpln("call void @llvm.trap()");
    manager.dumpln("unreachable");

    cleanupError(resumePointerLabel, resumeIntegerLabel, manager);

    manager.dumpln(resumeLabel + ":");
    if (manager.genLabel() != resumeLabel) {
      manager.pSynError("more registers than expected");
    }

    resumeError(resumePointerLabel, resumeIntegerLabel, manager);
    manager.popFunctionSymbols();
  }

  public void cleanupError(long resumePointerLabel, long resumeIntegerLabel, Manager manager) {
    manager.dumpln(manager.genLabel() + ":");

    long landingpad = manager.genLabel();
    manager.dumpln(String.format("%%d = landingpad { i8*, i32 } cleanup", landingpad));

    long extractedPointer = manager.genLabel();
    manager.dumpln(
        String.format("%%d = extractvalue { i8*, i32 } %%d, 0", extractedPointer, landingpad));
    manager.dumpln(
        String.format("store i8* %%d, i8** %%d, align 8", extractedPointer, resumePointerLabel));

    long extractedInteger = manager.genLabel();
    manager.dumpln(
        String.format("%%d = extractvalue { i8*, i32 } %%d, 1", extractedInteger, landingpad));
    manager.dumpln(
        String.format("store i8* %%d, i8** %%d, align 8", extractedInteger, resumeIntegerLabel));

    manager.dumpln("br " + (extractedInteger + CLEANUP_LABEL_SIZE + RESUME_LABEL_SIZE));
  }

  public void resumeError(long resumePointerLabel, long resumeIntegerLabel, Manager manager) {
    long pointer = manager.genLabel();
    manager.dumpln(String.format("%%d = load i8*, i8** %%d, align 8", pointer, resumePointerLabel));

    long integer = manager.genLabel();
    manager.dumpln(String.format("%%d = load i32, i32* %%d, align 4", integer, resumeIntegerLabel));

    long insertPointer = manager.genLabel();
    manager.dumpln(
        String.format("%%d = insertvalue { i8*, i32 } undef, i8* %%d, 0", insertPointer, pointer));
    long insertInteger = manager.genLabel();
    manager.dumpln(
        String.format("%%d = insertvalue { i8*, i32 } undef, i8* %%d, 0", insertInteger, integer));

    manager.dumpln("resume { i8*, i32 } %" + insertInteger);
  }

  public void destructError(Manager manager) {}
}
