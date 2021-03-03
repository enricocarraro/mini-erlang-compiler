package minierlang;

import java.util.HashMap;
import java.util.Stack;

class FunctionSymbols {
  private Stack<HashMap<String, Long>> symbolTableStack;
  public long label;
  public Long resumePointerLabel, resumeIntegerLabel, returnLabel, parameterLabel;
  public String name;

  public FunctionSymbols() {
    label = 0;
    symbolTableStack = new Stack<>();
    symbolTableStack.push(new HashMap<String, Long>());
  }

  public Long get(String key) {
    return symbolTableStack.peek().get(key);
  }

  public void put(String key, Long value) {
    symbolTableStack.peek().put(key, value);
  }

  public long genLabel() {
    return label++;
  }

  public void openClause() {
    symbolTableStack.push(new HashMap<String, Long>());
  }
  
  public void closeClause() {
    symbolTableStack.pop();
  }
}