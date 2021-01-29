package minierlang;

public class FunctionClause extends Node {
  String name;
  Expression argument;
  ExpressionSequence expressions;

  public FunctionClause(
      String name,
      Expression argument,
      ExpressionSequence expressions) {
    this.name = name;
    this.argument = argument;
    this.expressions = expressions;
    subgraphSize = expressions.subgraphSize;
  }

  public FunctionClause(String name, ExpressionSequence expressions, FunctionClause tail) {
    this.name = name;
    this.expressions = expressions;
    subgraphSize = expressions.subgraphSize;
    
  }

  public void generateCode(Manager manager) {

    if (argument == null) {
      expressions.generateCode(manager);
    } else {
      if (!(argument instanceof Variable)) {
        // Check that runtime argument is equal to function-def argument.
    	argument.generateCode(manager);
        long matchingLabel = manager.genLabel();
        manager.dumpln(
            String.format(
                "%%d = invoke zeroext i1 %s(%%s* %%d, %%s* nonnull align 8 dereferenceable(16)"
                    + " %%d)",
                matchingLabel,
                Const.LITERAL_MATCH,
                Const.LITERAL_STRUCT,
                manager.getParameterLabel(),
                Const.LITERAL_STRUCT,
                argument.label));

        long unwindLabel = matchingLabel + 1;
        long branchLabel = unwindLabel + CLEANUP_LABEL_SIZE + RESUME_LABEL_SIZE;
        manager.dumpln(String.format("to label %%d unwind label %%d", branchLabel, unwindLabel));

        cleanupError(manager);
        argument.destruct(manager);
        resumeError(manager);

        if (branchLabel != manager.genLabel()) {
          manager.pSynError("impossible: labels generated are not counted right.");
        }
        manager.dumpln(branchLabel + ":");
        argument.destruct(manager);
        long clauseExpressions = branchLabel + 1;
        long nextClause = branchLabel + 1 + expressions.subgraphSize;
        manager.dumpln(
            String.format(
                "br i1 %%d, label %%d, label %%d", matchingLabel, clauseExpressions, nextClause));
      } else {
    	 Variable variableArgument = (Variable) argument;  
		 variableArgument.generateCode(manager, manager.getParameterLabel());
      }
      expressions.generateCode(manager);

      expressions.generateReturn(manager);
    }
  }
  // TODO: Move this cleanupError and resumeError into Manager

}
