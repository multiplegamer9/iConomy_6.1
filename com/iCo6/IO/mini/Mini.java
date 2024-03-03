package com.iCo6.IO.mini;

import com.iCo6.IO.mini.file.Manager;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.LinkedList;

public class Mini {
  private String folder;
  
  private String database;
  
  private String source;
  
  private boolean changed = false;
  
  private Manager Database;
  
  private LinkedHashMap<String, Arguments> Indexes;
  
  private LinkedHashMap<String, Arguments> pushedIndexes;
  
  public Mini(String folder, String database) {
    this.database = database;
    this.folder = folder;
    this.Database = new Manager(this.folder, this.database, true);
    read();
  }
  
  public static void main(String[] args) {
    Mini mini = new Mini(".", "mini");
    Double amount = mini.getArguments("Nijikokun").getDouble("money");
    mini.setArgument("Nijikokun", "money", Double.valueOf(amount.doubleValue() + 2.0D), true);
    System.out.println(mini.getArguments("Nijikokun").getDouble("money"));
    System.out.println(mini.getIndices().toString());
  }
  
  private String[] trim(String[] values) {
    for (int i = 0, length = values.length; i < length; i++) {
      if (values[i] != null)
        values[i] = values[i].trim(); 
    } 
    return values;
  }
  
  private void read() {
    read(true);
  }
  
  private void read(boolean pushed) {
    this.Database = new Manager(this.folder, this.database, true);
    this.Database.removeDuplicates();
    this.Database.read();
    this.Indexes = new LinkedHashMap<String, Arguments>();
    if (pushed)
      this.pushedIndexes = new LinkedHashMap<String, Arguments>(); 
    for (String line : this.Database.getLines()) {
      if (line.trim().isEmpty())
        continue; 
      String[] parsed = trim(line.trim().split(" "));
      if (parsed[0].contains(":") || parsed[0].isEmpty())
        continue; 
      Arguments entry = new Arguments(parsed[0].toLowerCase());
      for (String item : parsed) {
        if (item.contains(":")) {
          String[] map = trim(item.split(":", 2));
          String key = map[0], value = map[1];
          if (key != null)
            entry.setValue(key, value); 
        } 
      } 
      this.Indexes.put(parsed[0].toLowerCase(), entry);
    } 
  }
  
  public boolean hasIndex(String key) {
    return this.Indexes.containsKey(key.toLowerCase());
  }
  
  public LinkedHashMap<String, Arguments> getIndices() {
    return this.Indexes;
  }
  
  public void addIndex(String index, Arguments entry) {
    this.pushedIndexes.put(index.toLowerCase(), entry);
    this.changed = true;
  }
  
  public boolean alterIndex(String original, String updated) {
    return alterIndex(original, updated, true);
  }
  
  public boolean alterIndex(String original, String updated, boolean update) {
    if (!hasIndex(original) || hasIndex(updated))
      return false; 
    Arguments data = this.Indexes.get(original.toLowerCase());
    removeIndex(original);
    addIndex(updated, data);
    if (update)
      update(); 
    return true;
  }
  
  public void removeIndex(String key) {
    this.Database.remove(((Arguments)this.Indexes.get(key.toLowerCase())).toString());
    read(false);
  }
  
  public Arguments getArguments(String key) {
    return this.Indexes.get(key.toLowerCase());
  }
  
  public void setArgument(String index, String key, Object value) {
    setArgument(index, key, String.valueOf(value), false);
  }
  
  public void setArgument(String index, String key, String value, boolean save) {
    if (!hasIndex(index))
      return; 
    this.changed = true;
    Arguments original = ((Arguments)this.Indexes.get(index.toLowerCase())).copy();
    original.setValue(key, value);
    this.pushedIndexes.put(index.toLowerCase(), original);
    if (save)
      update(); 
  }
  
  public void setArgument(String index, String key, Object value, boolean save) {
    String formatted = "";
    if (value instanceof int[]) {
      for (int v : (int[])value)
        formatted = v + ","; 
    } else if (value instanceof String[]) {
      for (String v : (String[])value)
        formatted = v + ","; 
    } else if (value instanceof Double[]) {
      for (Double v : (Double[])value)
        formatted = v + ","; 
    } else if (value instanceof Boolean[]) {
      for (Boolean v : (Boolean[])value)
        formatted = v + ","; 
    } else if (value instanceof Long[]) {
      for (Long v : (Long[])value)
        formatted = v + ","; 
    } else if (value instanceof Float[]) {
      for (Float v : (Float[])value)
        formatted = v + ","; 
    } else if (value instanceof Byte[]) {
      for (Byte v : (Byte[])value)
        formatted = v + ","; 
    } else if (value instanceof char[]) {
      for (char v : (char[])value)
        formatted = v + ","; 
    } else if (value instanceof ArrayList) {
      ArrayList data = (ArrayList)value;
      for (Object v : data)
        formatted = v + ","; 
    } 
    if (formatted.length() > 1) {
      formatted.substring(0, formatted.length() - 2);
    } else {
      formatted = String.valueOf(value);
    } 
    setArgument(index, key, formatted, save);
  }
  
  public void update() {
    if (!this.changed)
      return; 
    LinkedList<String> lines = new LinkedList<String>();
    for (String key : this.pushedIndexes.keySet()) {
      if (this.Indexes.containsKey(key) && 
        !((Arguments)this.Indexes.get(key)).toString().equals(((Arguments)this.pushedIndexes.get(key)).toString()))
        this.Database.remove(((Arguments)this.Indexes.get(key)).toString()); 
    } 
    read(false);
    for (String key : this.pushedIndexes.keySet()) {
      if (this.Indexes.containsKey(key)) {
        if (!((Arguments)this.Indexes.get(key)).toString().equals(((Arguments)this.pushedIndexes.get(key)).toString())) {
          this.Indexes.put(key, this.pushedIndexes.get(key));
          this.Database.append(((Arguments)this.Indexes.get(key)).toString());
        } 
        continue;
      } 
      this.Database.append(((Arguments)this.pushedIndexes.get(key)).toString());
    } 
    this.pushedIndexes.clear();
    read();
  }
}


