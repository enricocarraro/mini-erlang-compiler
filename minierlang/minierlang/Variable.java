package minierlang;

class Variable extends Expression {
  public String name;

  public Variable(String name) {
    this.name = name;
    this.subgraphSize = 1 + Node.CLEANUP_LABEL_SIZE + Node.RESUME_LABEL_SIZE;
  }

  public void generateCode(Manager manager, Node parent) {
    super.generateCode(manager, parent);
    manager.dumpln("\t; start " + this.getClass().getName());

    Long llvmId = manager.getFromST(name);
    if (llvmId == null) {
      label = allocate(manager);
      System.err.println(name + ": " + label);
      manager.putIntoST(name, label);
      manager.dumpln(
          String.format(
              "\tinvoke void %s(%%%s* %%%d)",
              Const.LITERAL_CONSTRUCT_EMPTY, Const.LITERAL_STRUCT, label));

      long unwindLabel = label + 1;
      long branchLabel = unwindLabel + Node.CLEANUP_LABEL_SIZE + Node.RESUME_LABEL_SIZE;
      manager.dumpln(
          String.format("\t\tto label %%%d unwind label %%%d", branchLabel, unwindLabel));

      manager.cleanupError(manager);
      // destruct(manager);
      manager.resumeError(manager);
      manager.dumpln(manager.genLabel() + ": ");
    } else {
      label = llvmId;
      System.err.println(name + ": " + label);
    }
  }

  // Method to handle variables in functions' argument lists.
  public void generateCode(Manager manager, Node parent, Long argumentLabel) {
    super.generateCode(manager, parent);
    label = argumentLabel;

    Long llvmId = manager.getFromST(name);
    if (llvmId == null) {
      llvmId = label;
      manager.putIntoST(name, llvmId);
    }
  }
}
