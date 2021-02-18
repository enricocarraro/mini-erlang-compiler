package minierlang.exp;

import minierlang.Const;
import minierlang.Manager;
import minierlang.Node;

public class FunctionCall extends Expression {
  protected String name;
  protected ExpressionSequence parameters;

  public FunctionCall(String name, ExpressionSequence parameters) {
    this.name = name;
    this.parameters = parameters;
    subgraphSize =
        1
            + (parameters != null ? parameters.subgraphSize : 0)
            + CLEANUP_LABEL_SIZE
            + RESUME_LABEL_SIZE;
  }

  public void generateCode(Manager manager, Node parent) {
    super.generateCode(manager, parent);
    manager.dumpln("\t; start " + this.getClass().getName() + " (" + subgraphSize + ")");
    
    manager.recordFunctionCall(name, parameters);
    label = allocate(manager);

    if (parameters != null) {
      parameters.generateCode(manager, this);
      manager.dumpCodeLabel();
      
      manager.dump(
          String.format(
              "\tinvoke void %s(%%%s* sret align 8 %%%d",
              manager.getFunctionName(name, parameters), Const.LITERAL_STRUCT, label));

      ExpressionSequence dfs_node = parameters;
      while (dfs_node != null) {
        manager.dump(String.format(", %%%s* %%%d", Const.LITERAL_STRUCT, dfs_node.head.label));
        dfs_node = dfs_node.tail;
      }
      manager.dumpln(")");

    } else {
      manager.dumpFormatln(
              "\tinvoke void %s(%%%s* sret align 8 %%%d)",
              manager.getFunctionName(name, parameters), Const.LITERAL_STRUCT, label);
    }

    long unwindLabel = manager.getCurrentLabel(),
        branchLabel = unwindLabel + Node.CLEANUP_LABEL_SIZE + Node.RESUME_LABEL_SIZE;

    manager.dumpFormatln("\t\tto label %%%d unwind label %%%d", branchLabel, unwindLabel);

    manager.cleanupError();
    destructDependencies(manager, this);
    manager.resumeError();
  }

  public long destructDependencies(Manager manager, Node caller) {
    long maxParentDep = super.destructDependencies(manager, caller);
    if (parameters != null && parameters != caller && parameters.label > maxParentDep) {
      parameters.destruct(manager, this);
    }
    return maxParentDep;
  }
}