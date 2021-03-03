package minierlang.exp;

import minierlang.Manager;
import minierlang.Node;
import minierlang.exp.binary.Match;
import minierlang.exp.terms.AltList;
import minierlang.exp.terms.List;

public class ListMatching extends Expression {
  static long temporaryLabel = 0;
  AltList lhs;
  Expression rhs;
  Expression headMatch, tailMatch;

  public ListMatching(AltList lhs, Expression rhs) {
    // User code can't declare variables starting with a lowercase letter, so there can't be any
    // collision.
    long currentTemporaryLabel = temporaryLabel++;
    this.rhs = new Match(new Variable("tmp" + currentTemporaryLabel), rhs);
    headMatch =
        lhs.head instanceof AltList
            ? new ListMatching(
                (AltList) lhs.head,
                new FunctionCall(
                    "hd",
                    new ExpressionSequence(new Variable("tmp" + currentTemporaryLabel), null)))
            : new Match(
                lhs.head,
                new FunctionCall(
                    "hd",
                    new ExpressionSequence(new Variable("tmp" + currentTemporaryLabel), null)));
    if (lhs.tail != null && lhs.tail instanceof AltList) {
      tailMatch =
          new ListMatching(
              (AltList) lhs.tail,
              new FunctionCall(
                  "tl", new ExpressionSequence(new Variable("tmp" + currentTemporaryLabel), null)));

    } else {
      tailMatch =
          new Match(
              lhs.tail != null ? lhs.tail : new List(""),
              new FunctionCall(
                  "tl", new ExpressionSequence(new Variable("tmp" + currentTemporaryLabel), null)));
    }

    this.subgraphSize = 2 + this.rhs.subgraphSize + headMatch.subgraphSize + tailMatch.subgraphSize;
  }

  public void generateCode(Manager manager, Node parent) {
    manager.dumpln("\t; start " + this.getClass().getName() + "(" + subgraphSize + ")");
    super.generateCode(manager, parent);

    rhs.generateCode(manager, this);
    label = rhs.label;
    manager.dumpCodeLabel();

    headMatch.generateCode(manager, this);
    manager.dumpCodeLabel();

    tailMatch.generateCode(manager, this);
  }

  public long destruct(Manager manager, Node caller) {
    return destructDependencies(manager, caller);
  }

  public long destructDependencies(Manager manager, Node caller) {

    long maxParentDep = 0;
    if (caller != parent) {
      maxParentDep = parent.destruct(manager, this);
    }

    if (rhs != caller) {
      if (rhs.label >= maxParentDep) {
        maxParentDep = Math.max(rhs.destruct(manager, this), maxParentDep);
      }
      if (headMatch != caller) {
        if (headMatch.label > maxParentDep) {
          maxParentDep = Math.max(headMatch.destruct(manager, this), maxParentDep);
        }
        if (tailMatch != caller && tailMatch.label > maxParentDep) {
          maxParentDep = Math.max(tailMatch.destruct(manager, this), maxParentDep);
        } else {
        }
      }
    }
    return maxParentDep;
  }
}
