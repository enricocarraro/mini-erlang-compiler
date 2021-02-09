
package minierlang.exp.unary;

import minierlang.Const;
import minierlang.exp.Expression;
import minierlang.Manager;
import minierlang.Node;
import minierlang.exp.UnaryExpression;

public class Negative extends UnaryExpression {
    public Negative(Expression rhs) {
        super(rhs);
    }
    public void generateCode(Manager manager, Node parent) {
        manager.dumpln("\t; start " + this.getClass().getName());
        super.genericGenerateCode(Const.LITERAL_NEGATIVE, manager, parent);
    }
}