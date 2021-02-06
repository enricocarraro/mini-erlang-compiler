package minierlang;

import minierlang.BinaryExpression;
import minierlang.Const;
import minierlang.Expression;
import minierlang.Manager;

public class Match extends BinaryExpression {
    public Match(Expression lhs, Expression rhs) {
        super(lhs, rhs);
    }
    public void generateCode(Manager manager, Node parent) {
    	super.generateCode(manager, parent);
    	
        manager.dumpln("\t; start " + this.getClass().getName());
        
        lhs.generateCode(manager, this);
        rhs.generateCode(manager, this);
        
        label = lhs.label;
        
        manager.dumpln(manager.genLabel() + ": ");
        manager.dumpln(String.format("\tinvoke void %s(%%%s* %%%d, %%%s* nonnull align 8 dereferenceable(16) %%%d)",
        Const.LITERAL_MATCH, Const.LITERAL_STRUCT, lhs.label,  Const.LITERAL_STRUCT, rhs.label));
        
        long unwindLabel = manager.getCurrentLabel();
        long branchLabel = unwindLabel + CLEANUP_LABEL_SIZE + RESUME_LABEL_SIZE;
        manager.dumpln(String.format("\t\tto label %%%d unwind label %%%d", branchLabel, unwindLabel));

        manager.cleanupError(manager);
        destructDependencies(manager, this);
        manager.resumeError(manager);
    }

    public void destruct(Manager manager, Node caller) {
        destructDependencies(manager, caller);
    }
}