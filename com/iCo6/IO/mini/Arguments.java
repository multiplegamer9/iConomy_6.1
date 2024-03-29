package com.iCo6.IO.mini;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

public class Arguments {
  private String key;
  
  private LinkedHashMap<String, String> values;
  
  public Arguments(String key) {
    this.key = key.toLowerCase();
    this.values = new LinkedHashMap<String, String>();
  }
  
  public String getKey() {
    return this.key;
  }
  
  private String encode(String data) {
    return data.trim().replace(" ", "}+{");
  }
  
  private String decode(String data) {
    return data.replace("}+{", " ").trim();
  }
  
  public boolean hasKey(String key) {
    return this.values.containsKey(key);
  }
  
  public void setValue(String key, Object value) {
    this.values.put(encode(key.toLowerCase()), encode(String.valueOf(value)));
  }
  
  public String getValue(String key) {
    return decode(this.values.get(key.toLowerCase()));
  }
  
  public Integer getInteger(String key) throws NumberFormatException {
    return Integer.valueOf(getValue(key));
  }
  
  public Double getDouble(String key) throws NumberFormatException {
    return Double.valueOf(getValue(key));
  }
  
  public Long getLong(String key) throws NumberFormatException {
    return Long.valueOf(getValue(key));
  }
  
  public Float getFloat(String key) throws NumberFormatException {
    return Float.valueOf(getValue(key));
  }
  
  public Short getShort(String key) throws NumberFormatException {
    return Short.valueOf(getValue(key));
  }
  
  public Boolean getBoolean(String key) {
    return Boolean.valueOf(getValue(key));
  }
  
  public String[] getArray(String key) {
    String value = getValue(key);
    if (value == null || !value.contains(","))
      return null; 
    if (value.split(",") == null)
      return null; 
    return trim(value.split(","));
  }
  
  private String[] trim(String[] values) {
    for (int i = 0, length = values.length; i < length; i++) {
      if (values[i] != null)
        values[i] = values[i].trim(); 
    } 
    return values;
  }
  
  private List trim(List<String> values) {
    List<String> trimmed = new ArrayList();
    for (int i = 0, length = values.size(); i < length; i++) {
      String v = values.get(i);
      if (v != null)
        v = v.trim(); 
      trimmed.add(v);
    } 
    return trimmed;
  }
  
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append(this.key).append(" ");
    for (String k : this.values.keySet())
      sb.append(k).append(":").append(this.values.get(k)).append(" "); 
    return sb.toString().trim();
  }
  
  public Arguments copy() {
    Arguments copy = new Arguments(this.key);
    for (String k : this.values.keySet())
      copy.values.put(k, this.values.get(k)); 
    return copy;
  }
}


