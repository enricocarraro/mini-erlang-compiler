package minierlang.exp.binary;

import minierlang.Const;
import minierlang.Manager;
import minierlang.Node;
import minierlang.exp.BinaryExpression;
import minierlang.exp.Expression;

public class Equals extends BinaryExpression {
  public Equals(Expression lhs, Expression rhs) {
    super(lhs, rhs);
  }

  public void generateCode(Manager manager, Node parent) {
    manager.dumpln("\t; start " + this.getClass().getName());
    super.genericGenerateCode(Const.LITERAL_EQ, manager, parent);
  }

  
}