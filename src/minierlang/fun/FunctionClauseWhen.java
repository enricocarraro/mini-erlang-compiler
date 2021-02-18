package minierlang.fun;

import minierlang.Manager;
import minierlang.Node;
import minierlang.exp.Expression;
import minierlang.exp.ExpressionSequence;

public class FunctionClauseWhen extends FunctionClause {
	//GuardSequence guardSequence;
	public FunctionClauseWhen(String name, Expression argument, ExpressionSequence expressions) {
		super(name, argument, expressions);
	}
    /*
    public FunctionClauseWhen(String name, FunctionArguments arguments, GuardSequence guardSequence, ExpressionSequence expressions) {
        super(name, arguments, expressions);
       // this.guardSequence = guardSequence
    }
    public FunctionClauseWhen(String name, ExpressionSequence expressions) {
        super(name, expressions);
     //   this.guardSequence = guardSequence
    }
    */
    public void generateCode(Manager manager, Node parent) {
        manager.dumpln("\t; start " + this.getClass().getName());
    }
}
