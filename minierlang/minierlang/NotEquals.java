package minierlang;

import minierlang.BinaryExpression;
import minierlang.Const;
import minierlang.Expression;
import minierlang.Manager;

public class NotEquals extends BinaryExpression {
    public NotEquals(Expression lhs, Expression rhs) {
        super(lhs, rhs);
    }
    public void generateCode(Manager manager, Node parent) {
        manager.dumpln("\t; start " + this.getClass().getName());
        super.genericGenerateCode(Const.LITERAL_NOT_EQ, manager, parent);
    }
}