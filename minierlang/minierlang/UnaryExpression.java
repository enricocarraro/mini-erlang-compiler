package minierlang;

public abstract class UnaryExpression extends Expression {
	protected Expression rhs;
    public UnaryExpression(Expression rhs) {
        this.rhs = rhs;
        subgraphSize = 1 + rhs.subgraphSize + CLEANUP_LABEL_SIZE + RESUME_LABEL_SIZE;
    }
    public void genericGenerateCode(String function, Manager manager) {
        rhs.generateCode(manager);
        
        label = allocate(manager);
       manager.dumpln(String.format("invoke void %s(%%s* sret align 8 %%d, %%s* %%d)", 
        function,
        Const.LITERAL_STRUCT,
        label,
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
        rhs.destruct(manager);
        super.destruct(manager);
    }
}