package minierlang.terms;

import minierlang.Const;
import minierlang.Manager;
import minierlang.Node;
import minierlang.Term;

public class Atom extends Term {
    String value;
    public Atom(String atom){
        value = atom;
        this.subgraphSize = 1 + Node.CLEANUP_LABEL_SIZE + Node.RESUME_LABEL_SIZE;
    }
    public void generateCode(Manager manager) {
        long atomId = manager.getAtom(value);
        label = allocate(manager);
       manager.dumpln(String.format("invoke void @%s(%%s* %%d, i64 %d)", Const.LITERAL_CONSTRUCT_ATOM, Const.LITERAL_STRUCT, label, atomId));

        long unwindLabel = label + 1;
        long branchLabel = unwindLabel + Node.CLEANUP_LABEL_SIZE + Node.RESUME_LABEL_SIZE;
       manager.dumpln(String.format("\tto label %%d unwind label %%d", branchLabel, unwindLabel));
                    
        cleanupError(manager);
        destruct(manager);
        resumeError(manager);
    }

}