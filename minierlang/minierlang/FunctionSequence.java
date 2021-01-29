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
	@Override
	void generateCode(Manager manager) {
		if(seqHead != null) {
			seqHead.generateCode(manager);
		}
		tail.generateCode(manager);
	}

}
