package minierlang;

public abstract class Node {
    protected static final int CLEANUP_LABEL_SIZE = 4;
    protected static final int RESUME_LABEL_SIZE = 4;
    public long label;
    public long subgraphSize;
    public Node(){
     
    }
    public Node(long subgraphSize){
        this.subgraphSize = subgraphSize;
    }
    abstract void generateCode(Manager manager);

    
    public void cleanupError(Manager manager) {
        long resumePointerLabel = manager.getResumePointer();
        long resumeIntegerLabel = manager.getResumeInteger();
        
       manager.dumpln(manager.genLabel() + ":");
    
        long landingpad = manager.genLabel();
       manager.dumpln(String.format("%%d = landingpad { i8*, i32 } cleanup", landingpad));
    
        long extractedPointer = manager.genLabel();
       manager.dumpln(String.format("%%d = extractvalue { i8*, i32 } %%d, 0", extractedPointer, landingpad));
       manager.dumpln(String.format("store i8* %%d, i8** %%d, align 8", extractedPointer, resumePointerLabel));
    
        long extractedInteger = manager.genLabel();
       manager.dumpln(String.format("%%d = extractvalue { i8*, i32 } %%d, 1", extractedInteger, landingpad));
       manager.dumpln(String.format("store i8* %%d, i8** %%d, align 8", extractedInteger, resumeIntegerLabel));
        
      }
    
       public void resumeError(Manager manager) {
        long resumePointerLabel = manager.getResumePointer();
        long resumeIntegerLabel = manager.getResumeInteger();
        
        long pointer = manager.genLabel();
       manager.dumpln(String.format("%%d = load i8*, i8** %%d, align 8", pointer, resumePointerLabel));
        
        long integer = manager.genLabel();
       manager.dumpln(String.format("%%d = load i32, i32* %%d, align 4", integer, resumeIntegerLabel));
        
        long insertPointer = manager.genLabel();
       manager.dumpln(String.format("%%d = insertvalue { i8*, i32 } undef, i8* %%d, 0", insertPointer, pointer));
        long insertInteger = manager.genLabel();
       manager.dumpln(String.format("%%d = insertvalue { i8*, i32 } undef, i8* %%d, 0", insertInteger, integer));
        
       manager.dumpln("resume { i8*, i32 } %" + insertInteger);
      }
}