package minierlang;

public class FunctionClauseWhen extends FunctionClause {
	//GuardSequence guardSequence;
	public FunctionClauseWhen(String name, Expression argument, ExpressionSequence expressions) {
		super(name, argument, expressions);
		// TODO Auto-generated constructor stub
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
    public void generateCode(Manager manager) {
    }
}
