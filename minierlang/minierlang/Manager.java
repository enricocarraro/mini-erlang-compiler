package minierlang;

import java.util.*;

public class Manager {
  private parser p;

  public Manager(parser parser) {
    this.p = parser;
  }
  /* Disable semantic check */
  public void disableSem() {
    p.enableSem = false;
  }
  /* Return true if semantic is enabled, false otherwise */
  public boolean sem() {
    return p.enableSem;
  }

  /* Error management */
  public void pSemError(String message) {
    p.errorBuffer.append(
        "SEM ERROR: line: " + p.getLine() + " col: " + p.getColumn() + ": " + message + "\n");
    p.semErrors++;
  }

  public void pSemWarning(String message) {
    p.errorBuffer.append(
        "SEM WARNING: line: " + p.getLine() + " col: " + p.getColumn() + ": " + message + "\n");
    p.semWarnings++;
  }

  public void pSynError(String message) {
    System.err.println(
        "SYN ERROR: line: " + p.getLine() + " col: " + p.getColumn() + ": " + message);
    System.err.println("Could not continue parsing");
    p.done_parsing();
  }

  public void pSynWarning(String message) {
    p.errorBuffer.append(
        "SYN WARNING: line: " + p.getLine() + " col: " + p.getColumn() + ": " + message + "\n");
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

  public long genLabel() {
    if (p.functionSymbolsStack.empty()) {
      p.functionSymbolsStack.push(new FunctionSymbols());
    }
    return p.functionSymbolsStack.peek().genLabel();
  }
  ;

  public long getCurrentLabel() {
    if (p.functionSymbolsStack.empty()) {
      p.functionSymbolsStack.push(new FunctionSymbols());
    }
    return p.functionSymbolsStack.peek().label;
  }
  ;

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
}
