package minierlang.exp;

import minierlang.Manager;
import minierlang.Node;

public abstract class Term extends Expression {

    protected abstract void dumpConstructor(Manager manager, long label);
    public void generateConstructor(Manager manager, Node parent) {
        this.parent = parent;
        label = manager.getCurrentLabel();
        dumpConstructor(manager, manager.getCurrentLabel());
      }
}