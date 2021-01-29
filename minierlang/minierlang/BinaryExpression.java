package minierlang;
public abstract class BinaryExpression extends Expression {
    protected Expression lhs, rhs;
    public BinaryExpression(Expression lhs, Expression rhs) {
        this.lhs = lhs;
        this.rhs = rhs;
        subgraphSize = 1 + rhs.subgraphSize + lhs.subgraphSize + CLEANUP_LABEL_SIZE + RESUME_LABEL_SIZE;
    }

    public void genericGenerateCode(String function, Manager manager) {
        lhs.generateCode(manager);
        rhs.generateCode(manager);
        
        label = allocate(manager);
       manager.dumpln(String.format("invoke void %s(%%s* sret align 8 %%d, %%s* %%d, %%s* nonnull align 8 dereferenceable(16) %%d)", 
        function,
        Const.LITERAL_STRUCT,
        label,
        Const.LITERAL_STRUCT,
        lhs.label,
        Const.LITERAL_STRUCT,
        rhs.label
        ));


        long unwindLabel = label + 1;
        long branchLabel = unwindLabel + Node.CLEANUP_LABEL_SIZE + Node.RESUME_LABEL_SIZE;
       manager.dumpln(String.format("\tto label %%d unwind label %%d", branchLabel, unwindLabel));
                    
        cleanupError(manager);
        destruct(manager);
        resumeError(manager);
    }

    public void destruct(Manager manager) {
        lhs.destruct(manager);
        rhs.destruct(manager);
        super.destruct(manager);
    }
    
}