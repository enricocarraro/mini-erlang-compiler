package minierlang.exp.terms;

import minierlang.exp.Expression;
import minierlang.Const;
import minierlang.Manager;
import minierlang.Node;
import minierlang.exp.Term;

class List extends Term {
	private Expression head;
    private List tail;
    private long length;
    public List(String string){
    	if(string.length() > 0) {
    		this.head = new Number((int) string.charAt(0));
    		if(string.length() > 1) {	
            	this.tail = new List(string.substring(1));
            }
    	}
    	this.length = computeSize();
    }
    public List(Expression head, List tail){
        this.head = head;
        this.tail = tail;
        this.length = computeSize();
        this.subgraphSize = 0;
    }
    
    private long computeSize() {
    	long len = 0;
    	if(head != null) {
        	len = 1;
        	if(tail != null) {
            	len += tail.length;
            }
        }
        
        
        return len;
    }
    
    
   
    public void generateCode(Manager manager, Node parent) {
        manager.dumpln("\t; start " + this.getClass().getName());
		
        label = allocate(manager);
    	long listLabel = manager.genLabel();
    	manager.dumpln(String.format("\t%%%d = alloca %%%s, align 8", listLabel, Const.STD_LIST));
    	
    	long initListLabel = manager.genLabel();
    	manager.dumpln(String.format("\t%%%d = alloca %%%s, align 8", initListLabel, Const.STD_INIT_LIST));
    	
    	long arrayLabel = manager.genLabel();
    	manager.dumpln(String.format("\t%%%d = alloca [%d x %%%s], align 8", arrayLabel, length, Const.LITERAL_STRUCT));
    	
    	long pointerLabel = manager.genLabel();
    	manager.dumpln(String.format("\t%%%d = alloca %%%s*, align 8", pointerLabel, Const.LITERAL_STRUCT));
        
    	long arrayPointer = manager.genLabel();
    	if(head != null) {
            manager.dumpln(String.format("\t%d = getelementptr inbounds [%d x %%%s], [6 x %struct.Literal]* %%%d, i64 0, i64 0", arrayPointer, length, Const.LITERAL_STRUCT, length, Const.LITERAL_STRUCT, arrayLabel));
            manager.dumpln(String.format("store %%%s* %%%d, %%%s** %%%d, align 8", Const.LITERAL_STRUCT, arrayPointer, Const.LITERAL_STRUCT, pointerLabel));
            head.con
        } 
		
        manager.dumpln(String.format("invoke void @_ZN7LiteralC1Ei(%struct.Literal* %46, i32 104)", Const.LITERAL_CONSTRUCT_INT, Const.LITERAL_STRUCT, arrayPointer, head
        /*
  %46 = getelementptr inbounds [6 x %struct.Literal], [6 x %struct.Literal]* %8, i64 0, i64 0
  
  invoke void @_ZN7LiteralC1Ei(%struct.Literal* %46, i32 104)
          to label %47 unwind label %173
         * 
         */
	}
    
    public void generateElementCode(Manager manager, Node parent) {
    	
    }
}