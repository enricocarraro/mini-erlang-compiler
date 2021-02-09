package minierlang;

import java.util.*;
import minierlang.exp.Expression;

public class Manager {
  private parser p;

  public Manager(parser parser) {
    this.p = parser;
    init();
  }

  private void init() {
    p.functionSet.addAll(
        Arrays.asList(
            Const.BIF_IS_ATOM,
            Const.BIF_IS_BOOLEAN,
            Const.BIF_IS_FLOAT,
            Const.BIF_IS_INTEGER,
            Const.BIF_IS_FUNCTION,
            Const.BIF_IS_LIST,
            Const.BIF_IS_NUMBER,
            Const.BIF_ABS,
            Const.BIF_FLOAT,
            Const.BIF_HD,
            Const.BIF_TL,
            Const.BIF_LENGTH,
            Const.BIF_ROUND,
            Const.BIF_TRUNC));
  }
  /* Disable semantic check */
  public void disableSem() {
    p.enableSem = false;
  }
  /* Return true if semantic is enabled, false otherwise */
  public boolean sem() {
    return p.enableSem;
  }

  public int getLine() {
    return p.getLine();
  }

  public int getColumn() {
    return p.getColumn();
  }

  /* Error management */

  private String formatMessage(String message, String type, int line, int column) {
    return String.format("[%s] at line %d, column %d: %s\n", type, line, column, message);
  }

  public void pSemError(String message) {
    p.errorBuffer.append(formatMessage(message, "SEM ERROR", p.getLine(), p.getColumn()));
    p.semErrors++;
  }

  public void pSemWarning(String message) {
    p.errorBuffer.append(formatMessage(message, "SEM WARNING", p.getLine(), p.getColumn()));
    p.semWarnings++;
  }

  public void pSynError(String message) {
    System.err.println(formatMessage(message, "SYN ERROR", p.getLine(), p.getColumn()));
    System.err.println("Could not continue parsing");
    p.done_parsing();
  }

  public void pSynWarning(String message) {
    p.errorBuffer.append(formatMessage(message, "SYN WARNING", p.getLine(), p.getColumn()));
    p.synWarnings++;
    /* When there is a syntactic warning semantic is disable to avoid errors due to invalid data structures */
    disableSem();
  }

  /* Functions to dump program output */
  public void dump(String s) {
    p.outputBuffer.append(s);
  }

  public void dumpln(String s) {
    p.outputBuffer.append(s + "\n");
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
      pSynWarning("Symbol is not part of any function.");
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
      pSemError("Clauses must be inside functions.");
    }
    p.functionSymbolsStack.peek().openClause();
  }

  public void closeClause() {
    if (p.functionSymbolsStack.empty()) {
      pSemError("Clauses must be inside functions.");
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
      pSynError("Function didn't allocate registers for error handling.");
    }
    return p.functionSymbolsStack.peek().resumePointerLabel;
  }

  public Long getResumeInteger() {
    if (p.functionSymbolsStack.empty()
        || p.functionSymbolsStack.peek().resumeIntegerLabel == null) {
      pSynError("Function didn't allocate registers for error handling.");
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
      pSynError("Function didn't allocate registers for return value.");
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
      pSynError("Function didn't allocate registers for parameter.");
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
      pSynError("Function didn't allocate registers for error handling.");
    }
    p.functionSymbolsStack.pop();
  }

  public void setFunctionName(String name) {
    if (p.functionSymbolsStack.empty()) {
      p.functionSymbolsStack.push(new FunctionSymbols());
    }

    if (p.functionSet.contains(name)) {
      pSynError("Function " + name + " has already been defined.");
    }
    p.functionSet.add(name);
    p.functionSymbolsStack.peek().name = name;
  }

  public String getFunctionName() {
    if (p.functionSymbolsStack.empty()) {
      pSynError("Function is not well formed.");
    }
    return p.functionSymbolsStack.peek().name;
  }

  public void cleanupError(Manager manager) {
    Long resumePointerLabel = manager.getResumePointer();
    Long resumeIntegerLabel = manager.getResumeInteger();
    manager.dumpln(manager.genLabel() + ":");

    long landingpad = manager.genLabel();
    manager.dumpln(String.format("\t%%%d = landingpad { i8*, i32 }\n\t\tcleanup", landingpad));

    long extractedPointer = manager.genLabel();
    manager.dumpln(
        String.format("\t%%%d = extractvalue { i8*, i32 } %%%d, 0", extractedPointer, landingpad));
    manager.dumpln(
        String.format(
            "\tstore i8* %%%d, i8** %%%d, align 8", extractedPointer, resumePointerLabel));

    long extractedInteger = manager.genLabel();
    manager.dumpln(
        String.format("\t%%%d = extractvalue { i8*, i32 } %%%d, 1", extractedInteger, landingpad));
    manager.dumpln(
        String.format(
            "\tstore i32 %%%d, i32* %%%d, align 8", extractedInteger, resumeIntegerLabel));
  }

  public void resumeError(Manager manager) {
    Long resumePointerLabel = manager.getResumePointer();
    Long resumeIntegerLabel = manager.getResumeInteger();
    long pointer = manager.genLabel();
    manager.dumpln(
        String.format("\t%%%d = load i8*, i8** %%%d, align 8", pointer, resumePointerLabel));

    long integer = manager.genLabel();
    manager.dumpln(
        String.format("\t%%%d = load i32, i32* %%%d, align 4", integer, resumeIntegerLabel));

    long insertPointer = manager.genLabel();
    manager.dumpln(
        String.format(
            "\t%%%d = insertvalue { i8*, i32 } undef, i8* %%%d, 0", insertPointer, pointer));
    long insertInteger = manager.genLabel();
    manager.dumpln(
        String.format(
            "\t%%%d = insertvalue { i8*, i32 } %%%d, i32 %%%d, 1",
            insertInteger, insertPointer, integer));

    manager.dumpln("\tresume { i8*, i32 } %" + insertInteger);
  }

  public void recordFunctionDef(String name) {
    p.functionSet.add(name);
  }

  public void recordFunctionCall(String name, Expression argument) {
    name = getFunctionName(name, argument);
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
    return name + "." + (argument != null ? 1 : 0);
  }

  public void checkTailMatch(FunctionClause head, FunctionClauseSequence tail) {
    if (tail != null) {
      if (!tail.head.name.equals(head.name)) {
        pSynError("Function clauses of the same function must have the same identifier.");
      } else if ((tail.head.argument == null || head.argument == null)
          && tail.head.argument != head.argument) {
        pSynError("Function clauses of the same function must have the same number of parameters.");
      }
    }
  }

  public void checkUndefinedFunctionsCalls() {
    for (Map.Entry<String, ArrayList<Integer>> entry : p.functionCalls.entrySet()) {
      String key = entry.getKey();
      if (!p.functionSet.contains(key)) {
        ArrayList<Integer> posList = entry.getValue();
        String message = "Call to undefined function " + key.replace(".", "/") + ".";

        for (int i = 0; i < posList.size() - 1; i++) {
          int line = posList.get(i), column = posList.get(++i);
          p.errorBuffer.append(formatMessage(message, "SEM ERROR", line, column));
          p.semErrors++;
        }
      }
    }
  }
}
