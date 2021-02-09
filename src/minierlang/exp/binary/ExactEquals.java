package minierlang.exp.binary;

import minierlang.Const;
import minierlang.exp.Expression;
import minierlang.Manager;
import minierlang.Node;
import minierlang.exp.BinaryExpression;

public class ExactEquals extends BinaryExpression {
  public ExactEquals(Expression lhs, Expression rhs) {
    super(lhs, rhs);
  }

  public void generateCode(Manager manager, Node parent) {
    manager.dumpln("\t; start " + this.getClass().getName());
    super.genericGenerateCode(Const.LITERAL_EXACT_EQ, manager, parent);
  }
}