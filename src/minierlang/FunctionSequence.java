package minierlang;

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

	
	@Override
	public long destruct(Manager manager, Node caller) {
		return 0;
		
	}

	@Override
	public long destructDependencies(Manager manager, Node caller) {
		// TODO Auto-generated method stub
		return 0;
	}

}
