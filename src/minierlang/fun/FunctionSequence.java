package minierlang.fun;

import minierlang.Manager;
import minierlang.Node;

public class FunctionSequence extends Node {
	FunctionSequence seqHead;
	Function tail;

	public FunctionSequence(Function tail) {
		this.tail = tail;
		seqHead = null;
	}
	public FunctionSequence(FunctionSequence head, Function tail) {
		seqHead = head;
		this.tail = tail;
	}
	
	public void generateCode(Manager manager, Node parent) {
		super.generateCode(manager, parent);
		if(seqHead != null) {
			seqHead.generateCode(manager, this);
		}
		tail.generateCode(manager, this);
	}
	
	public long destruct(Manager manager, Node caller) {
		return 0;
		
	}
	public long destructDependencies(Manager manager, Node caller) {
		return 0;
	}

}
