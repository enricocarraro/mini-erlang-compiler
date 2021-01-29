package minierlang.binexp;

import minierlang.BinaryExpression;
import minierlang.Const;
import minierlang.Expression;
import minierlang.Manager;

public class Less extends BinaryExpression {
    public Less(Expression lhs, Expression rhs) {
        super(lhs, rhs);
    }
    public void generateCode(Manager manager) {
        super.genericGenerateCode(Const.LITERAL_LESS, manager);
    }
}