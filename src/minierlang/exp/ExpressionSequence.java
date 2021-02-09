package minierlang.exp;

import minierlang.Manager;
import minierlang.Node;

public class ExpressionSequence extends Node {
	Expression head;
	ExpressionSequence tail;
	public ExpressionSequence(Expression head, ExpressionSequence tail) {
		super(head.subgraphSize + (tail == null ? CLEANUP_LABEL_SIZE + RESUME_LABEL_SIZE + 2 : tail.subgraphSize + 1));
		this.head = head;
		this.tail = tail;
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
		manager.dumpln("\t; start " + this.getClass().getName() + " (" + subgraphSize + ")");
		head.generateCode(manager, this);
		label = head.label;
		if(tail != null) {
			manager.dumpln(manager.genLabel() + ":");
			tail.generateCode(manager, this);
		}
	}

	public long destructDependencies(Manager manager, Node caller) {
		long maxParentDep = 0;
		if(parent != caller) {
			maxParentDep = parent.destruct(manager, this);
		}
		if(head != caller && head.label > maxParentDep) {
			maxParentDep = Math.max(head.destruct(manager, this), maxParentDep);
		}
		return maxParentDep;
	}
	public long destruct(Manager manager, Node caller) {
		return destructDependencies(manager, caller);
	}

	
} 