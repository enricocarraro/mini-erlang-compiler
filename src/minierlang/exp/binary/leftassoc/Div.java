package minierlang.exp.binary.leftassoc;

import minierlang.Const;
import minierlang.Manager;
import minierlang.Node;
import minierlang.exp.Expression;
import minierlang.exp.LeftAssocBinaryExpression;

public class Div extends LeftAssocBinaryExpression {
  public Div(Expression lhs, Expression rhs) {
    super(lhs, rhs);
  }

  public void generateCode(Manager manager, Node parent) {
    manager.dumpln("\t; start " + this.getClass().getName() + " (" + subgraphSize + ")");
    super.genericGenerateCode(Const.LITERAL_DIV, manager, parent);
  }

  
}
