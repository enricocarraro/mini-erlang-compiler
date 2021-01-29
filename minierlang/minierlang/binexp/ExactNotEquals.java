
package minierlang.binexp;

import minierlang.BinaryExpression;
import minierlang.Const;
import minierlang.Expression;
import minierlang.Manager;

public class ExactNotEquals extends BinaryExpression {
    public ExactNotEquals(Expression lhs, Expression rhs) {
        super(lhs, rhs);
    }
    public void generateCode(Manager manager) {
        super.genericGenerateCode(Const.LITERAL_EXACT_NOT_EQ, manager);
    }
}
