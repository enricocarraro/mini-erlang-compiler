package minierlang;

import java.util.*;
import minierlang.exp.Expression;
import minierlang.exp.ExpressionSequence;
import minierlang.exp.FunctionCall;
import minierlang.fun.FunctionClause;
import minierlang.fun.FunctionClauseSequence;

public class Manager {
  public parser p;
  private HashSet<String> guardBIFs = new HashSet<>();

  public Manager(parser parser) {
    this.p = parser;

    guardBIFs.addAll(
        Arrays.asList(
            Const.BIF_IS_ATOM,
            Const.BIF_IS_BOOLEAN,
            Const.BIF_IS_FLOAT,
            Const.BIF_IS_INTEGER,
            Const.BIF_IS_LIST,
            Const.BIF_IS_NUMBER,
            Const.BIF_ABS,
            Const.BIF_FLOAT,
            Const.BIF_HD,
            Const.BIF_TL,
            Const.BIF_LENGTH,
            Const.BIF_ROUND,
            Const.BIF_TRUNC));
    p.functionSet.addAll(guardBIFs);
    p.functionSet.addAll(
        Arrays.asList(Const.IO_FORMAT_1, Const.IO_FORMAT_2, Const.LISTS_APPEND, Const.LISTS_NTH));
  }

  public int getLine() {
    return p.getLine();
  }

  public int getColumn() {
    return p.getColumn();
  }

  public void semanticError(String message) {
    p.errorBuffer.append(p.formatMessage(message, "SEMANTIC ERROR", getLine(), getColumn()));
    p.semErrors++;
  }

  public void semanticWarning(String message) {
    p.errorBuffer.append(p.formatMessage(message, "SEMANTIC WARNING", getLine(), getColumn()));
    p.semWarnings++;
  }

  public void syntaxError(String message) {
    System.err.println(p.formatMessage(message, "SYNTAX ERROR", getLine(), getColumn()));
    System.err.println("Could not continue parsing.");
    p.done_parsing();
  }

  public void syntaxWarning(String message) {
    p.errorBuffer.append(p.formatMessage(message, "SYNTAX WARNING", getLine(), getColumn()));
    p.synWarnings++;
    /* When there is a syntactic warning semantic is disable to avoid errors due to invalid data structures */
    p.enableSem = false;
  }

  /* Functions to dump program output */
  public void dump(String s) {
    p.outputBuffer.append(s);
  }

  public void dumpln(String s) {
    dump(s + "\n");
  }

  public void dumpFormatln(String s, Object... args) {
    dumpln(String.format(s, args));
  }

  public long getAtom(String atomValue) {
    Long atomID = p.atomTable.get(atomValue);
    if (atomID == null) {
      atomID = p.lastAtom++;
      p.atomTable.put(atomValue, atomID);
    }
    return atomID;
  }
  ;

  public Long getFromST(String key) {
    if (p.functionSymbolsStack.empty()) {
      semanticError("Symbol is not part of any function.");
    }

    return p.functionSymbolsStack.peek().get(key);
  }

  public void putIntoST(String key, Long value) {
    if (p.functionSymbolsStack.empty()) {
      p.functionSymbolsStack.push(new FunctionSymbols());
    }
    p.functionSymbolsStack.peek().put(key, value);
  }

  public void openClause() {
    if (p.functionSymbolsStack.empty()) {
      semanticError("Clauses must be inside functions.");
    }
    p.functionSymbolsStack.peek().openClause();
  }

  public void closeClause() {

    if (p.functionSymbolsStack.empty()) {
      semanticError("Clauses must be inside functions.");
    }
    p.functionSymbolsStack.peek().closeClause();
  }

  public long genLabel() {
    if (p.functionSymbolsStack.empty()) {
      p.functionSymbolsStack.push(new FunctionSymbols());
    }
    return p.functionSymbolsStack.peek().genLabel();
  }

  public long getCurrentLabel() {
    if (p.functionSymbolsStack.empty()) {
      p.functionSymbolsStack.push(new FunctionSymbols());
    }
    return p.functionSymbolsStack.peek().label;
  }

  public Long getResumePointer() {
    if (p.functionSymbolsStack.empty()
        || p.functionSymbolsStack.peek().resumePointerLabel == null) {
      syntaxError("Function didn't allocate registers for error handling.");
    }
    return p.functionSymbolsStack.peek().resumePointerLabel;
  }

  public Long getResumeInteger() {
    if (p.functionSymbolsStack.empty()
        || p.functionSymbolsStack.peek().resumeIntegerLabel == null) {
      syntaxError("Function didn't allocate registers for error handling.");
    }
    return p.functionSymbolsStack.peek().resumeIntegerLabel;
  }

  public void setResumePointer(long label) {
    if (p.functionSymbolsStack.empty()) {
      p.functionSymbolsStack.push(new FunctionSymbols());
    }
    p.functionSymbolsStack.peek().resumePointerLabel = label;
  }

  public void setResumeInteger(long label) {
    if (p.functionSymbolsStack.empty()) {
      p.functionSymbolsStack.push(new FunctionSymbols());
    }
    p.functionSymbolsStack.peek().resumeIntegerLabel = label;
  }

  public Long getReturnLabel() {
    if (p.functionSymbolsStack.empty() || p.functionSymbolsStack.peek().returnLabel == null) {
      syntaxError("Function didn't allocate registers for return value.");
    }
    return p.functionSymbolsStack.peek().returnLabel;
  }

  public void setReturnLabel(long label) {
    if (p.functionSymbolsStack.empty()) {
      p.functionSymbolsStack.push(new FunctionSymbols());
    }
    p.functionSymbolsStack.peek().returnLabel = label;
  }

  public Long getParameterLabel() {
    if (p.functionSymbolsStack.empty() || p.functionSymbolsStack.peek().parameterLabel == null) {
      syntaxError("Function didn't allocate registers for parameter.");
    }
    return p.functionSymbolsStack.peek().parameterLabel;
  }

  public void setParameterLabel(long label) {
    if (p.functionSymbolsStack.empty()) {
      p.functionSymbolsStack.push(new FunctionSymbols());
    }
    p.functionSymbolsStack.peek().parameterLabel = label;
  }

  public void popFunctionSymbols() {
    if (p.functionSymbolsStack.empty()) {
      syntaxError("Function didn't allocate registers for error handling.");
    }
    p.functionSymbolsStack.pop();
  }

  public void setFunctionName(String name) {
    if (p.functionSymbolsStack.empty()) {
      p.functionSymbolsStack.push(new FunctionSymbols());
    }

    if (p.functionSet.contains(name)) {
      syntaxError("Function " + name + " has already been defined.");
    }
    p.functionSet.add(name);
    p.functionSymbolsStack.peek().name = name;
  }

  public String getFunctionName() {
    if (p.functionSymbolsStack.empty()) {
      syntaxError("Function is not well formed.");
    }
    return p.functionSymbolsStack.peek().name;
  }

  public long dumpCodeLabel() {
    long label = genLabel();
    dumpln(label + ":");
    return label;
  }

  public void dumpBrNextLabel() {
    dumpln("\tbr label %" + getCurrentLabel());
  }

  public void cleanupError() {
    Long resumePointerLabel = getResumePointer();
    Long resumeIntegerLabel = getResumeInteger();
    dumpln(genLabel() + ":");

    long landingpad = genLabel();
    dumpFormatln("\t%%%d = landingpad { i8*, i32 }\n\t\tcleanup", landingpad);

    long extractedPointer = genLabel();
    dumpFormatln("\t%%%d = extractvalue { i8*, i32 } %%%d, 0", extractedPointer, landingpad);
    dumpFormatln("\tstore i8* %%%d, i8** %%%d, align 8", extractedPointer, resumePointerLabel);

    long extractedInteger = genLabel();
    dumpFormatln("\t%%%d = extractvalue { i8*, i32 } %%%d, 1", extractedInteger, landingpad);
    dumpFormatln("\tstore i32 %%%d, i32* %%%d, align 8", extractedInteger, resumeIntegerLabel);
  }

  public void resumeError() {
    Long resumePointerLabel = getResumePointer();
    Long resumeIntegerLabel = getResumeInteger();
    long pointer = genLabel();
    dumpFormatln("\t%%%d = load i8*, i8** %%%d, align 8", pointer, resumePointerLabel);

    long integer = genLabel();
    dumpFormatln("\t%%%d = load i32, i32* %%%d, align 4", integer, resumeIntegerLabel);

    long insertPointer = genLabel();
    dumpFormatln("\t%%%d = insertvalue { i8*, i32 } undef, i8* %%%d, 0", insertPointer, pointer);
    long insertInteger = genLabel();
    dumpFormatln(
        "\t%%%d = insertvalue { i8*, i32 } %%%d, i32 %%%d, 1",
        insertInteger, insertPointer, integer);

    dumpln("\tresume { i8*, i32 } %" + insertInteger);
  }

  public void recordFunctionDef(String name) {
    p.functionSet.add(name);
  }

  public void recordFunctionCall(String name, ExpressionSequence parameters) {
    name = getFunctionName(name, parameters);
    int line = getLine(), column = getColumn();

    ArrayList<Integer> callList = p.functionCalls.get(name);
    if (callList == null) {
      callList = new ArrayList<Integer>(Arrays.asList(line, column));
      p.functionCalls.put(name, callList);
    }
    callList.add(line);
    callList.add(column);
  }

  public String getFunctionName(String name, Expression argument) {
    return "@" + name + "." + (argument == null ? 0 : 1);
  }

  public String getFunctionName(String name, ExpressionSequence parameters) {

    int countPar = 0;
    while (parameters != null) {
      countPar++;
      parameters = parameters.tail;
    }
    return "@" + name + "." + countPar;
  }

  public void checkTailMatch(FunctionClause head, FunctionClauseSequence tail) {
    if (tail != null) {
      if (!tail.head.name.equals(head.name)) {
        syntaxError("Function clauses of the same function must have the same identifier.");
      } else if ((tail.head.argument == null || head.argument == null)
          && tail.head.argument != head.argument) {
        syntaxError(
            "Function clauses of the same function must have the same number of parameters.");
      }
    }
  }

  public boolean isFunctionAllowedInGuardExpression(FunctionCall functionCall) {
    return guardBIFs.contains(this.getFunctionName(functionCall.name, functionCall.parameters));
  }

  public void checkUndefinedFunctionsCalls() {
    if (!p.functionSet.contains("@start.0")) {
      semanticError("Cannot start program if start/0 is not defined.");
    }
    for (Map.Entry<String, ArrayList<Integer>> entry : p.functionCalls.entrySet()) {
      String key = entry.getKey();
      if (!p.functionSet.contains(key)) {
        String message =
            "Call to undefined function " + key.replace("@", "").replace(".", "/") + ".";
        p.errorBuffer.append(p.formatMessage(message, "SEMANTIC ERROR", null, null));
        p.semErrors++;
      }
    }
  }
}
