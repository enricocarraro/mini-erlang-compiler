
package minierlang;

import minierlang.Const;
import minierlang.UnaryExpression;
import minierlang.Expression;
import minierlang.Manager;

public class Not extends UnaryExpression {
    public Not(Expression rhs) {
        super(rhs);
    }
    public void generateCode(Manager manager, Node parent) {
        manager.dumpln("\t; start " + this.getClass().getName());
        super.genericGenerateCode(Const.LITERAL_NOT, manager, parent);
    }
}