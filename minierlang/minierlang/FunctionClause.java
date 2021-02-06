package minierlang;

public class FunctionClause extends Node {
	String name;
	Expression argument;
	ExpressionSequence expressions;

	public FunctionClause(String name, Expression argument, ExpressionSequence expressions) {
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

	public void generateCode(Manager manager, Node parent) {
		super.generateCode(manager, parent);
		if (argument != null) {
			if (!(argument instanceof Variable)) {
				// Check that runtime argument is equal to function-def argument.
				argument.generateCode(manager, this);
				long matchingLabel = manager.genLabel();
				manager.dumpln(
						String.format(
								"\t%%%d = invoke zeroext i1 %s(%%%s* %%%d, %%%s* nonnull align 8 dereferenceable(16)"
										+ " %%%d)",
										matchingLabel,
										Const.LITERAL_MATCH,
										Const.LITERAL_STRUCT,
										manager.getParameterLabel(),
										Const.LITERAL_STRUCT,
										argument.label));

				long unwindLabel = matchingLabel + 1;
				long branchLabel = unwindLabel + CLEANUP_LABEL_SIZE + RESUME_LABEL_SIZE;
				manager.dumpln(String.format("\t\tto label %%%d unwind label %%%d", branchLabel, unwindLabel));

				manager.cleanupError(manager);
				argument.destruct(manager, this);
				manager.resumeError(manager);
				long rbl = manager.genLabel();
				if (branchLabel != rbl) {
					manager.pSynError("impossible: labels generated are not counted right: " + branchLabel + " != " + rbl);
				}
				manager.dumpln(branchLabel + ":");
				argument.destruct(manager, this);
				long clauseExpressions = branchLabel + 1;
				long nextClause = branchLabel + 1 + expressions.subgraphSize;
				manager.dumpln(
						String.format(
								"br i1 %%%d, label %%%d, label %%%d", matchingLabel, clauseExpressions, nextClause));
			} else {
				Variable variableArgument = (Variable) argument;
				variableArgument.generateCode(manager, this, manager.getParameterLabel());
			}
		}
		
		expressions.generateCode(manager, this);
		expressions.generateReturn(manager);
	}


	public void destruct(Manager manager, Node caller) {}

	@Override
	public void destructDependencies(Manager manager, Node caller) {
		// TODO Auto-generated method stub
		
	}

}
