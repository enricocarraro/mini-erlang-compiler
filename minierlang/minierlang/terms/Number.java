package minierlang.terms;

import minierlang.Const;
import minierlang.Manager;
import minierlang.Node;
import minierlang.Term;

public class Number extends Term {
    Integer integerValue;
    Float floatValue;  
    
    public Number(Float value) {
        floatValue = value;
        this.subgraphSize = 1 + Node.CLEANUP_LABEL_SIZE + Node.RESUME_LABEL_SIZE;
    }
    public Number(Integer value) {
        integerValue = value;
        this.subgraphSize = 1 + Node.CLEANUP_LABEL_SIZE + Node.RESUME_LABEL_SIZE;
    }

    public void generateCode(Manager manager) {
        label = allocate(manager);
        if(integerValue != null) {
           manager.dumpln(String.format("invoke void @%s(%%s* %%d, i32 %s)", Const.LITERAL_CONSTRUCT_INT, Const.LITERAL_STRUCT, label, integerValue));
        } else {
           manager.dumpln(String.format("invoke void @%s(%%s* %%d, double %s)", Const.LITERAL_CONSTRUCT_FLOAT, Const.LITERAL_STRUCT, label, floatValue));
        }
        long unwindLabel = label + 1;
        long branchLabel = unwindLabel + Node.CLEANUP_LABEL_SIZE + Node.RESUME_LABEL_SIZE;
       manager.dumpln(String.format("\tto label %%d unwind label %%d", branchLabel, unwindLabel));
                    
        cleanupError(manager);
        destruct(manager);
        resumeError(manager);
    }
}
