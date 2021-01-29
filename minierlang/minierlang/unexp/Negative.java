
package minierlang.unexp;

import minierlang.Const;
import minierlang.UnaryExpression;
import minierlang.Expression;
import minierlang.Manager;

public class Negative extends UnaryExpression {
    public Negative(Expression rhs) {
        super(rhs);
    }
    public void generateCode(Manager manager) {
        super.genericGenerateCode(Const.LITERAL_NEGATIVE, manager);
    }
}