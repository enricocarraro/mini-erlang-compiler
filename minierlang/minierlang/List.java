package minierlang;

import minierlang.Expression;
import minierlang.Manager;
import minierlang.Term;

class List extends Term {
    Term current;
    List next;
    /*public List(String bool){
        value = bool;
        this.subgraphSize = 0;
    }
    public List(Expression head, List tail){
        value = bool;
        this.subgraphSize = 0;
    }
    public void generateCode(Manager manager, Node parent) {
        manager.dumpln("\t; start " + this.getClass().getName());
        dump("i1 " + value);
    }*/
	@Override
    public void generateCode(Manager manager, Node parent) {
        manager.dumpln("\t; start " + this.getClass().getName());
		// TODO Auto-generated method stub
		
	}
}