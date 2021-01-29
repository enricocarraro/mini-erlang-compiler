
package minierlang.unexp;

import minierlang.Const;
import minierlang.UnaryExpression;
import minierlang.Expression;
import minierlang.Manager;

public class Not extends UnaryExpression {
    public Not(Expression rhs) {
        super(rhs);
    }
    public void generateCode(Manager manager) {
        super.genericGenerateCode(Const.LITERAL_NOT, manager);
    }
}