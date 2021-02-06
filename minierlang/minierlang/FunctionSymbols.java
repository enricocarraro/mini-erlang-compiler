package minierlang;

import java.util.HashMap;

class FunctionSymbols {
  private HashMap<String, Long> symbolTable;
  public long label;
  public Long resumePointerLabel, resumeIntegerLabel, returnLabel, parameterLabel;
  public String name;

  public FunctionSymbols() {
    label = 0;
    symbolTable = new HashMap<String, Long>();
  }

  public Long get(String key) {
    return symbolTable.get(key);
  }

  public void put(String key, Long value) {
    symbolTable.put(key, value);
  }

  public long genLabel() {
    return label++;
  }
}