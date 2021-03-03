package minierlang.fun;

import minierlang.Const;
import minierlang.Manager;
import minierlang.Node;
import minierlang.exp.Expression;
import minierlang.exp.FunctionCall;
import minierlang.exp.ListMatching;
import minierlang.exp.binary.Match;
import minierlang.exp.terms.AltList;

public class Guard extends Expression {
	AltList guard_expression;
  public Guard(AltList guard_expression) {
    this.guard_expression = guard_expression;
    subgraphSize = guard_expression.subgraphSize + 2 + CLEANUP_LABEL_SIZE + RESUME_LABEL_SIZE;
  }
  
  private void checkGuardExpressionSemantic(Manager manager, Expression expression) {
	  if(expression instanceof Match || expression instanceof ListMatching) {
		  manager.semanticError("Pattern Matching is not allowed in guards.");
	  }
	  if(expression instanceof FunctionCall && manager.isFunctionAllowedInGuardExpression((FunctionCall) expression)) {
		manager.semanticError("Guards can contain call only to few selected functions, please read documentation.");
	  }

  }
  
  private void checkGuardSemantic(Manager manager) {
	  AltList current = guard_expression;
	  while(current != null) {
		  this.checkGuardExpressionSemantic(manager, current.head);
		  if(!(current.tail instanceof AltList)) break; 
		  current = (AltList)current.tail;	
	  } 
	
  }

  public void generateCode(Manager manager, Node parent) {
    super.generateCode(manager, parent);

    this.checkGuardSemantic(manager);
    guard_expression.generateCode(manager, this);
    manager.dumpCodeLabel();
    label = manager.genLabel();
    manager.dumpFormatln("%%%d = invoke zeroext i1 %s(%%%s* %%%d)",
    		label, Const.EVAL_GUARD, Const.LITERAL_STRUCT, guard_expression.label);
    long afterUnwind = manager.getCurrentLabel() + CLEANUP_LABEL_SIZE + RESUME_LABEL_SIZE;
    manager.dumpFormatln("\t\tto label %%%d unwind label %%%d", afterUnwind, manager.getCurrentLabel());
    manager.cleanupError();
    destructDependencies(manager, this);
    manager.resumeError();
  }

  public long destruct(Manager manager, Node caller) {
    return destructDependencies(manager, caller);
  }

  public long destructDependencies(Manager manager, Node caller) {
    long maxParentDep = super.destructDependencies(manager, caller);
    if(caller != guard_expression && guard_expression.label > maxParentDep) {
    	return this.guard_expression.destruct(manager, this);
    }
	return maxParentDep;
  }
}
