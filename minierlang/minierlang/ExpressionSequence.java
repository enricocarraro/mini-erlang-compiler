package minierlang;


class ExpressionSequence extends Node {
	Expression head;
	ExpressionSequence tail;
	public ExpressionSequence(Expression head, ExpressionSequence tail) {
		super(head.subgraphSize + (tail == null ? 0 : tail.subgraphSize));
		this.head = head;
		this.tail = tail;
		if(tail != null) {
			tail.parent = this;
		}
	}

	public void generateReturn(Manager manager) {
		if(tail == null) {
			head.generateReturn(manager);
		} else {
			tail.generateReturn(manager);
		}
	}

	public void generateCode(Manager manager, Node parent) {
		super.generateCode(manager, parent);
		manager.dumpln("\t; start " + this.getClass().getName());
		head.generateCode(manager, this);
		label = head.label;
		if(tail != null) {
			manager.dumpln(manager.genLabel() + ":");
			tail.generateCode(manager, this);
		}
	}

	public void destructDependencies(Manager manager, Node caller) {
		if(parent != caller) {
			parent.destruct(manager, this);
		}
		if(head != caller) {
			head.destruct(manager, this);
		}
	}
	public void destruct(Manager manager, Node caller) {
		destructDependencies(manager, caller);
	}

	
} 