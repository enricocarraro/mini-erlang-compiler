package minierlang;


public abstract class Expression extends Node {
    public Expression() {}
    protected long allocate(Manager manager) {
        long newLabel = manager.genLabel();
       manager.dumpln(String.format("%%d = alloca %%s, align 8", newLabel, Const.LITERAL_STRUCT));
        return newLabel;
    }
    protected void destruct(Manager manager) {
        manager.dumpln(String.format("call void %s(%%s* %%d) ", Const.LITERAL_DESTRUCT, Const.LITERAL_STRUCT, label));
    }
    
    protected void generateReturn(Manager manager) {
        long returnLabel = manager.getReturnLabel();
        manager.dumpln(String.format("invoke void %s(%%s* %%d, %%s* nonnull align 8 dereferenceable(16) %%d)",
        Const.LITERAL_CONSTRUCT_COPY, Const.LITERAL_STRUCT, returnLabel,  Const.LITERAL_STRUCT, label));
        
        long unwindLabel = manager.getCurrentLabel() + 1;
        long branchLabel = unwindLabel + CLEANUP_LABEL_SIZE + RESUME_LABEL_SIZE;
        manager.dumpln(String.format("to label %%d unwind label %%d", branchLabel, unwindLabel));

        cleanupError(manager);
        destruct(manager);
        resumeError(manager);

        if (branchLabel != manager.genLabel()) {
          manager.pSynError("impossible: labels generated are not counted right.");
        }
        manager.dumpln(branchLabel + ":");
        destruct(manager);
        manager.dumpln("ret void");
    }

	public abstract void generateCode(Manager manager);
} 