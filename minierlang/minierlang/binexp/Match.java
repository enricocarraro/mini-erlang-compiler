package minierlang.binexp;

import minierlang.BinaryExpression;
import minierlang.Const;
import minierlang.Expression;
import minierlang.Manager;

public class Match extends BinaryExpression {
    public Match(Expression lhs, Expression rhs) {
        super(lhs, rhs);
    }
    public void generateCode(Manager manager) {

        // to do variable assignment 
        super.genericGenerateCode(Const.LITERAL_MATCH, manager);
    }
}