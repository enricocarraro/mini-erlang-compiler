package minierlang.exp.binary;

import minierlang.Const;
import minierlang.exp.Expression;
import minierlang.Manager;
import minierlang.Node;
import minierlang.exp.BinaryExpression;

public class IntegerDiv extends BinaryExpression {
    public IntegerDiv(Expression lhs, Expression rhs) {
        super(lhs, rhs);
    }
    public void generateCode(Manager manager, Node parent) {
        manager.dumpln("\t; start " + this.getClass().getName() + " (" + subgraphSize + ")");
        super.genericGenerateCode(Const.LITERAL_INT_DIV, manager, parent);
    }
}