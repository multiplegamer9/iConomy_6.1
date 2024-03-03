package com.iCo6.system;

import com.iCo6.Constants;
import com.iCo6.IO.InventoryDB;
import com.iCo6.IO.mini.Arguments;
import com.iCo6.IO.mini.Mini;
import com.iCo6.iConomy;
import com.iCo6.util.org.apache.commons.dbutils.DbUtils;
import com.iCo6.util.org.apache.commons.dbutils.QueryRunner;
import com.iCo6.util.org.apache.commons.dbutils.ResultSetHandler;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;

class Queried {
  static Mini database;
  
  static InventoryDB inventory;
  
  static ResultSetHandler<String> returnName = new ResultSetHandler<String>() {
      public String handle(ResultSet rs) throws SQLException {
        if (rs.next())
          return rs.getString("name"); 
        return null;
      }
    };
  
  static ResultSetHandler<List<String>> returnList = new ResultSetHandler<List<String>>() {
      private List<String> accounts;
      
      public List<String> handle(ResultSet rs) throws SQLException {
        this.accounts = new ArrayList<String>();
        while (rs.next())
          this.accounts.add(rs.getString("username")); 
        return this.accounts;
      }
    };
  
  static ResultSetHandler<Boolean> returnBoolean = new ResultSetHandler<Boolean>() {
      public Boolean handle(ResultSet rs) throws SQLException {
        return Boolean.valueOf(rs.next());
      }
    };
  
  static ResultSetHandler<Double> returnBalance = new ResultSetHandler<Double>() {
      public Double handle(ResultSet rs) throws SQLException {
        if (rs.next())
          return Double.valueOf(rs.getDouble("balance")); 
        return null;
      }
    };
  
  static boolean useMiniDB() {
    if (iConomy.Database.getType().toString().equalsIgnoreCase("minidb")) {
      if (database == null)
        database = iConomy.Database.getDatabase(); 
      return true;
    } 
    return false;
  }
  
  static boolean useInventoryDB() {
    if (iConomy.Database.getType().toString().equalsIgnoreCase("inventorydb")) {
      if (inventory == null)
        inventory = iConomy.Database.getInventoryDatabase(); 
      return true;
    } 
    return false;
  }
  
  static List<String> accountList() {
    if (useMiniDB())
      return new ArrayList<String>(database.getIndices().keySet()); 
    if (useInventoryDB())
      return new ArrayList<String>(inventory.getAllPlayers()); 
    List<String> accounts = new ArrayList<String>();
    try {
      QueryRunner run = new QueryRunner();
      Connection c = iConomy.Database.getConnection();
      try {
        String t = Constants.Nodes.DatabaseTable.toString();
        accounts = (List<String>)run.query(c, "SELECT username FROM " + t, returnList);
      } catch (SQLException ex) {
        System.out.println("[iConomy] Error issueing SQL query: " + ex);
      } finally {
        DbUtils.close(c);
      } 
    } catch (SQLException ex) {
      System.out.println("[iConomy] Database Error: " + ex);
    } 
    return accounts;
  }
  
  static boolean createAccount(String name, Double balance, Integer status) {
    Boolean created = Boolean.valueOf(false);
    if (useMiniDB()) {
      if (!hasAccount(name)) {
        Arguments Row = new Arguments(name);
        Row.setValue("balance", balance);
        Row.setValue("status", status);
        database.addIndex(Row.getKey(), Row);
        database.update();
        return true;
      } 
      database.setArgument(name, "balance", balance);
      database.update();
      return true;
    } 
    if (useInventoryDB()) {
      if (!hasAccount(name))
        return false; 
      inventory.setBalance(name, balance.doubleValue());
      return true;
    } 
    try {
      QueryRunner run = new QueryRunner();
      Connection c = iConomy.Database.getConnection();
      try {
        String t = Constants.Nodes.DatabaseTable.toString();
        Integer amount = Integer.valueOf(run.update(c, "INSERT INTO " + t + "(username, balance, status) values (?, ?, ?)", new Object[] { name, balance, status }));
        if (amount.intValue() > 0)
          created = Boolean.valueOf(true); 
      } catch (SQLException ex) {
        System.out.println("[iConomy] Error issueing SQL query: " + ex);
      } finally {
        DbUtils.close(c);
      } 
    } catch (SQLException ex) {
      System.out.println("[iConomy] Database Error: " + ex);
    } 
    return false;
  }
  
  static boolean removeAccount(String name) {
    Boolean removed = Boolean.valueOf(false);
    if (useMiniDB()) {
      database.removeIndex(name);
      database.update();
      return true;
    } 
    if (useInventoryDB())
      return false; 
    try {
      QueryRunner run = new QueryRunner();
      Connection c = iConomy.Database.getConnection();
      try {
        String t = Constants.Nodes.DatabaseTable.toString();
        Integer amount = Integer.valueOf(run.update(c, "DELETE FROM " + t + " WHERE username=?", name));
        if (amount.intValue() > 0)
          removed = Boolean.valueOf(true); 
      } catch (SQLException ex) {
        System.out.println("[iConomy] Error issueing SQL query: " + ex);
      } finally {
        DbUtils.close(c);
      } 
    } catch (SQLException ex) {
      System.out.println("[iConomy] Database Error: " + ex);
    } 
    return removed.booleanValue();
  }
  
  static boolean hasAccount(String name) {
    Boolean exists = Boolean.valueOf(false);
    if (useMiniDB())
      return database.hasIndex(name); 
    if (useInventoryDB())
      return inventory.dataExists(name); 
    try {
      QueryRunner run = new QueryRunner();
      Connection c = iConomy.Database.getConnection();
      try {
        String t = Constants.Nodes.DatabaseTable.toString();
        exists = (Boolean)run.query(c, "SELECT id FROM " + t + " WHERE username=?", returnBoolean, new Object[] { name });
      } catch (SQLException ex) {
        System.out.println("[iConomy] Error issueing SQL query: " + ex);
      } finally {
        DbUtils.close(c);
      } 
    } catch (SQLException ex) {
      System.out.println("[iConomy] Database Error: " + ex);
    } 
    return exists.booleanValue();
  }
  
  static double getBalance(String name) {
    Double balance = Constants.Nodes.Balance.getDouble();
    if (!hasAccount(name))
      return balance.doubleValue(); 
    if (useMiniDB())
      return database.getArguments(name).getDouble("balance").doubleValue(); 
    if (useInventoryDB())
      return inventory.getBalance(name); 
    try {
      QueryRunner run = new QueryRunner();
      Connection c = iConomy.Database.getConnection();
      try {
        String t = Constants.Nodes.DatabaseTable.toString();
        balance = (Double)run.query(c, "SELECT balance FROM " + t + " WHERE username=?", returnBalance, new Object[] { name });
      } catch (SQLException ex) {
        System.out.println("[iConomy] Error issueing SQL query: " + ex);
      } finally {
        DbUtils.close(c);
      } 
    } catch (SQLException ex) {
      System.out.println("[iConomy] Database Error: " + ex);
    } 
    return balance.doubleValue();
  }
  
  static void setBalance(String name, double balance) {
    if (!hasAccount(name)) {
      createAccount(name, Double.valueOf(balance), Integer.valueOf(0));
      return;
    } 
    if (useMiniDB()) {
      database.setArgument(name, "balance", Double.valueOf(balance));
      database.update();
      return;
    } 
    if (useInventoryDB()) {
      inventory.setBalance(name, balance);
      return;
    } 
    try {
      QueryRunner run = new QueryRunner();
      Connection c = iConomy.Database.getConnection();
      try {
        String t = Constants.Nodes.DatabaseTable.toString();
        int update = run.update(c, "UPDATE " + t + " SET balance=? WHERE username=?", new Object[] { Double.valueOf(balance), name });
      } catch (SQLException ex) {
        System.out.println("[iConomy] Error issueing SQL query: " + ex);
      } finally {
        DbUtils.close(c);
      } 
    } catch (SQLException ex) {
      System.out.println("[iConomy] Database Error: " + ex);
    } 
  }
  
  static void doInterest(String query, LinkedHashMap<String, HashMap<String, Object>> queries) {
    Object[][] parameters = new Object[queries.size()][2];
    int i = 0;
    for (String name : queries.keySet()) {
      parameters[i][0] = ((HashMap)queries.get(name)).get("balance");
      parameters[i][1] = name;
      i++;
    } 
    try {
      QueryRunner run = new QueryRunner();
      Connection c = iConomy.Database.getConnection();
      try {
        run.batch(c, query, parameters);
      } catch (SQLException ex) {
        System.out.println("[iConomy] Error with batching: " + ex);
      } finally {
        DbUtils.close(c);
      } 
    } catch (SQLException ex) {
      System.out.println("[iConomy] Database Error: " + ex);
    } 
  }
  
  static void purgeDatabase() {
    if (useMiniDB()) {
      for (String index : database.getIndices().keySet()) {
        if (database.getArguments(index).getDouble("balance") == Constants.Nodes.Balance.getDouble())
          database.removeIndex(index); 
      } 
      database.update();
    } 
    if (useInventoryDB())
      return; 
    try {
      QueryRunner run = new QueryRunner();
      Connection c = iConomy.Database.getConnection();
      try {
        String t = Constants.Nodes.DatabaseTable.toString();
        Integer amount = Integer.valueOf(run.update(c, "DELETE FROM " + t + " WHERE balance=?", Constants.Nodes.Balance.getDouble()));
      } catch (SQLException ex) {
        System.out.println("[iConomy] Error issueing SQL query: " + ex);
      } finally {
        DbUtils.close(c);
      } 
    } catch (SQLException ex) {
      System.out.println("[iConomy] Database Error: " + ex);
    } 
  }
  
  static void emptyDatabase() {
    if (useMiniDB()) {
      for (String index : database.getIndices().keySet())
        database.removeIndex(index); 
      database.update();
    } 
    if (useInventoryDB())
      return; 
    try {
      QueryRunner run = new QueryRunner();
      Connection c = iConomy.Database.getConnection();
      try {
        String t = Constants.Nodes.DatabaseTable.toString();
        Integer amount = Integer.valueOf(run.update(c, "TRUNCATE TABLE " + t));
      } catch (SQLException ex) {
        System.out.println("[iConomy] Error issueing SQL query: " + ex);
      } finally {
        DbUtils.close(c);
      } 
    } catch (SQLException ex) {
      System.out.println("[iConomy] Database Error: " + ex);
    } 
  }
}


