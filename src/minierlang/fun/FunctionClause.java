package minierlang.fun;

import minierlang.Const;
import minierlang.Manager;
import minierlang.Node;
import minierlang.exp.Expression;
import minierlang.exp.ExpressionSequence;
import minierlang.exp.Variable;

public class FunctionClause extends Node {
  public String name;
  public Expression argument;
  ExpressionSequence expressions;
  Guard guard;
  private boolean lastClause = false;

  public FunctionClause(String name, Expression argument, ExpressionSequence expressions) {
    this.name = name;
    this.argument = argument;
    this.expressions = expressions;
    subgraphSize = expressions.subgraphSize;
  }

  public FunctionClause(
      String name, Expression argument, Guard guard, ExpressionSequence expressions) {
    this.name = name;
    this.argument = argument;
    this.expressions = expressions;
    this.guard = guard;
    subgraphSize = expressions.subgraphSize;
  }

  public FunctionClause(
      String name,
      Expression argument,
      Guard guard,
      ExpressionSequence expressions,
      boolean lastClause) {
    this.name = name;
    this.argument = argument;
    this.guard = guard;
    this.expressions = expressions;
    subgraphSize = expressions.subgraphSize;
  }

  public void generateCode(Manager manager, Node parent) {
    this.parent = parent;

    if (argument != null) {
      if (argument instanceof Variable) {
         Variable variableArgument = (Variable) argument;
        variableArgument.generateCode(manager, this, manager.getParameterLabel());
      } else {
        // Check that runtime argument is equal to function-def argument.
        argument.generateCode(manager, this);

        manager.dumpCodeLabel();
        long matchingLabel = manager.genLabel();
        manager.dumpFormatln(
            "\t%%%d = invoke zeroext i1 %s(%%%s* %%%d, %%%s* nonnull align 8"
                + " dereferenceable(16) %%%d)",
            matchingLabel,
            Const.LITERAL_CLAUSE_MATCH,
            Const.LITERAL_STRUCT,
            manager.getParameterLabel(),
            Const.LITERAL_STRUCT,
            argument.label);

        long unwindLabel = matchingLabel + 1;
        long branchLabel = unwindLabel + CLEANUP_LABEL_SIZE + RESUME_LABEL_SIZE;
        manager.dumpFormatln("\t\tto label %%%d unwind label %%%d", branchLabel, unwindLabel);

        manager.cleanupError();
        argument.destruct(manager, this);
        manager.resumeError();
        long rbl = manager.genLabel();
        manager.dumpln(branchLabel + ":");
        argument.destruct(manager, this);
        long clauseExpressions = branchLabel + 1;
        long nextClause = branchLabel + 2 + expressions.subgraphSize;
        manager.dumpFormatln(
            "\tbr i1 %%%d, label %%%d, label %%%d", matchingLabel, clauseExpressions, nextClause);
        manager.dumpCodeLabel();
      }
    }
    if (guard != null) {
      guard.generateCode(manager, this);
      manager.dumpCodeLabel();

      long clauseExpressions = manager.getCurrentLabel();
      long nextClause = manager.getCurrentLabel() + 1 + expressions.subgraphSize;
      manager.dumpFormatln(
          "\tbr i1 %%%d, label %%%d, label %%%d", guard.label, clauseExpressions, nextClause);
      manager.dumpCodeLabel();
    }
    expressions.generateCode(manager, this);
    expressions.generateReturn(manager);
    manager.closeClause();
  }

  public long destruct(Manager manager, Node caller) {
    return destructDependencies(manager, caller);
  }

  public long destructDependencies(Manager manager, Node caller) {
    long maxParentDep = 0;
    if (argument != null && argument instanceof Variable) {
      maxParentDep = 1;
    }
    if (guard != null && guard.label > maxParentDep) {
      maxParentDep = guard.destruct(manager, this);
    }

    return maxParentDep;
  }

  void fun() {
    
  }
}
