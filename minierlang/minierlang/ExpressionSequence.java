package minierlang;


class ExpressionSequence extends Node {
    Expression head;
    ExpressionSequence tail;
    public ExpressionSequence(Expression head, ExpressionSequence tail) {
        super(head.subgraphSize + tail.subgraphSize);
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

	public void generateCode(Manager manager) {
		head.generateCode(manager);
		if(tail != null) {
			tail.generateCode(manager);
		}
	}
} 