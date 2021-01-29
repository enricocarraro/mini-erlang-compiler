package minierlang;

public class FunctionClauseSequence extends Node {
    FunctionClause head;
    FunctionClauseSequence tail;

  public FunctionClauseSequence(
        FunctionClause head,
      FunctionClauseSequence tail) {
    this.head = head;
    this.tail = tail;
    subgraphSize = head.subgraphSize + (tail != null ? tail.subgraphSize : 0);
  }



  public void generateCode(Manager manager) {
    // TODO: Check for definition of different function clauses (different name) and launch semantic
    // error
	  checkTailMatch(manager);
 
      head.generateCode(manager);
      tail.generateCode(manager);
 
  }
  // TODO: Move this cleanupError and resumeError into Manager

  private void checkTailMatch(Manager manager) {
    if (tail != null) {
      if (tail.head.name != head.name) {
        manager.pSynError("Function clauses of the same function must have the same identifier.");
      } else if ((tail.head.argument == null || head.argument == null) && tail.head.argument != head.argument) {
        manager.pSynError(
            "Function clauses of the same function must have the same number of parameters.");
      }
    }
  }
}
