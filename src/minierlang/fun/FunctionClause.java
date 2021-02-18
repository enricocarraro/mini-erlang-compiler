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

  public FunctionClause(String name, Expression argument, ExpressionSequence expressions) {
    this.name = name;
    this.argument = argument;
    this.expressions = expressions;
    subgraphSize = expressions.subgraphSize;
  }

  public FunctionClause(String name, ExpressionSequence expressions) {
    this.name = name;
    this.expressions = expressions;
    subgraphSize = expressions.subgraphSize;
  }

  public void generateCode(Manager manager, Node parent) {
    super.generateCode(manager, parent);

    if (argument != null) {
      if (!(argument instanceof Variable)) {
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
        if (branchLabel != rbl) {
          manager.pSynError(
              "impossible: labels generated are not counted right: " + branchLabel + " != " + rbl);
        }
        manager.dumpln(branchLabel + ":");
        argument.destruct(manager, this);
        long clauseExpressions = branchLabel + 1;
        long nextClause = branchLabel + 2 + expressions.subgraphSize;
        manager.dumpFormatln(
            "\tbr i1 %%%d, label %%%d, label %%%d", matchingLabel, clauseExpressions, nextClause);
        manager.dumpCodeLabel();
      } else {
        Variable variableArgument = (Variable) argument;
        variableArgument.generateCode(manager, this, manager.getParameterLabel());
      }
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
    if (argument != null && argument instanceof Variable){
      maxParentDep = 1;      
    }

    return maxParentDep;
  }
}
