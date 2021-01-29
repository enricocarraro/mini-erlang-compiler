package minierlang;

class Variable extends Expression {
	public String name;

	public Variable(String name) {
		this.name = name;
		this.subgraphSize = 1 + Node.CLEANUP_LABEL_SIZE + Node.RESUME_LABEL_SIZE;
	}

	public void generateCode(Manager manager) {
		label = allocate(manager);

		Long llvmId = manager.getFromST(name);
		if (llvmId == null) {
			llvmId = label;
			manager.putIntoST(name, llvmId);
		}

		manager.dumpln(
				String.format("invoke void @%s(%%s* %%d)", Const.LITERAL_CONSTRUCT_EMPTY, Const.LITERAL_STRUCT, label));

		long unwindLabel = label + 1;
		long branchLabel = unwindLabel + Node.CLEANUP_LABEL_SIZE + Node.RESUME_LABEL_SIZE;
		manager.dumpln(String.format("\tto label %%d unwind label %%d", branchLabel, unwindLabel));

		cleanupError(manager);
		destruct(manager);
		resumeError(manager);
	}

	// Method to handle variables in functions' argument lists.
	public void generateCode(Manager manager, Long argumentLabel) {
		label = argumentLabel;

		Long llvmId = manager.getFromST(name);
		if (llvmId == null) {
			llvmId = label;
			manager.putIntoST(name, llvmId);
		}

	}
}
