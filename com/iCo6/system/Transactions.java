package com.iCo6.system;

import com.iCo6.Constants;
import com.iCo6.IO.mini.Mini;
import com.iCo6.iConomy;
import com.iCo6.util.Common;

public class Transactions {
  public void insert(Transaction data) {
    if (!Constants.Nodes.Logging.getBoolean().booleanValue())
      return; 
    if (Common.matches(iConomy.Database.getType().toString(), new String[] { "inventorydb", "minidb" })) {
      Mini database = iConomy.Database.getTransactionDatabase();
      if (database == null)
        return; 
      return;
    } 
  }
  
  public class Transaction {
    private long time = 0L;
    
    private String where;
    
    private String from;
    
    private String to;
    
    private Double fromBalance = Double.valueOf(0.0D), toBalance = Double.valueOf(0.0D);
    
    private Double set = Double.valueOf(0.0D);
    
    private Double gain = Double.valueOf(0.0D);
    
    private Double loss = Double.valueOf(0.0D);
    
    public Transaction(String where) {
      this.where = where;
      this.time = System.currentTimeMillis() / 1000L;
    }
    
    public Transaction(String where, String from, String to) {
      this.where = where;
      this.from = from;
      this.to = to;
      this.time = System.currentTimeMillis() / 1000L;
    }
    
    public Transaction(long time, String where, String from, String to, Double fromBalance, Double toBalance, Double set, Double gain, Double loss) {
      this.time = time;
      this.where = where;
      this.from = from;
      this.to = to;
      this.fromBalance = fromBalance;
      this.toBalance = toBalance;
      this.set = set;
      this.gain = gain;
      this.loss = loss;
    }
    
    public String getFrom() {
      return this.from;
    }
    
    public Double getGain() {
      return this.gain;
    }
    
    public Double getLoss() {
      return this.loss;
    }
    
    public Double getSet() {
      return this.set;
    }
    
    public Double getFromBalance() {
      return this.fromBalance;
    }
    
    public long getTime() {
      return this.time;
    }
    
    public String getTo() {
      return this.to;
    }
    
    public Double getToBalance() {
      return this.toBalance;
    }
    
    public Transaction setTime(long time) {
      this.time = time;
      return this;
    }
    
    public String getWhere() {
      return this.where;
    }
    
    public Transaction setFrom(String from) {
      this.from = from;
      return this;
    }
    
    public Transaction setFromBalance(Double fromBalance) {
      this.fromBalance = fromBalance;
      return this;
    }
    
    public Transaction setGain(Double gain) {
      this.gain = gain;
      return this;
    }
    
    public Transaction setLoss(Double loss) {
      this.loss = loss;
      return this;
    }
    
    public Transaction setSet(Double set) {
      this.set = set;
      return this;
    }
    
    public Transaction setTo(String to) {
      this.to = to;
      return this;
    }
    
    public Transaction setWhere(String where) {
      this.where = where;
      return this;
    }
    
    public Transaction setToBalance(Double toBalance) {
      this.toBalance = toBalance;
      return this;
    }
  }
}


