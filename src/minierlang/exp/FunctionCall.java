package minierlang.exp;

import minierlang.Const;
import minierlang.Manager;
import minierlang.Node;

public class FunctionCall extends Expression {
  protected String name;
  protected Expression parameter;

  public FunctionCall(String name, Expression parameter) {
    this.name = name;
    this.parameter = parameter;
    subgraphSize =
        1
            + (parameter != null ? parameter.subgraphSize : 0)
            + CLEANUP_LABEL_SIZE
            + RESUME_LABEL_SIZE;
  }

  public void generateCode(Manager manager, Node parent) {
    super.generateCode(manager, parent);

    manager.recordFunctionCall(name, parameter);
    label = allocate(manager);

    if (parameter != null) {
      parameter.generateCode(manager, this);

      manager.dumpln(
          String.format(
              "\tinvoke void %s(%%%s* sret align 8 %%%d, %%%s* %%%d)",
              manager.getFunctionName(name, parameter),
              Const.LITERAL_STRUCT,
              label,
              Const.LITERAL_STRUCT,
              parameter.label));

    } else {
      manager.dumpln(
          String.format(
              "invoke void %s(%%%s* sret align 8 %%%d)",
              manager.getFunctionName(name, parameter), Const.LITERAL_STRUCT, label));
    }

    long unwindLabel = label + 1,
        branchLabel = unwindLabel + Node.CLEANUP_LABEL_SIZE + Node.RESUME_LABEL_SIZE;

    manager.dumpln(String.format("\t\tto label %%%d unwind label %%%d", branchLabel, unwindLabel));

    manager.cleanupError(manager);
    destructDependencies(manager, this);
    manager.resumeError(manager);
  }

  public long destructDependencies(Manager manager, Node caller) {
    long maxParentDep = super.destructDependencies(manager, caller);
    if (parameter != null && parameter != caller && parameter.label > maxParentDep) {
      parameter.destruct(manager, this);
    }
    return maxParentDep;
  }
}