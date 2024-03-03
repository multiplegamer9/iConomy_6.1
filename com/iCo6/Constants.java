package com.iCo6;

import java.util.ArrayList;
import java.util.List;
import org.bukkit.util.config.Configuration;

public class Constants {
  public enum Drivers {
    H2("http://mirror.nexua.org/Dependencies/h2.jar", "h2.jar"),
    MySQL("http://mirror.nexua.org/Dependencies/mysql-connector-java-bin.jar", "mysql.jar"),
    SQLite("http://mirror.nexua.org/Dependencies/sqlite-jdbc.jar", "sqlite.jar"),
    Postgre("http://mirror.nexua.org/Dependencies/postgresql.jdbc4.jar", "postgresql.jar");
    
    String url;
    
    String filename;
    
    Drivers(String url, String filename) {
      this.url = url;
      this.filename = filename;
    }
    
    public String getFilename() {
      return this.filename;
    }
    
    public String getUrl() {
      return this.url;
    }
    
    public String toString() {
      return this.url;
    }
  }
  
  public enum Nodes {
    Minor("System.Default.Currency.Minor", new ArrayList()),
    Major("System.Default.Currency.Major", new ArrayList()),
    MultiWorld("System.Default.Account.MultiWorld", Boolean.valueOf(false)),
    Balance("System.Default.Account.Holdings", Double.valueOf(30.0D)),
    AllowMinor("System.Formatting.Minor", Boolean.valueOf(false)),
    isSplit("System.Formatting.Seperate", Boolean.valueOf(false)),
    isSingle("System.Formatting.Single", Boolean.valueOf(false)),
    Logging("System.Logging.Enabled", Boolean.valueOf(false)),
    Interest("System.Interest.Enabled", Boolean.valueOf(false)),
    InterestOnline("System.Interest.Online", Boolean.valueOf(false)),
    InterestTimer("System.Interest.Interval", Integer.valueOf(60)),
    InterestPercentage("System.Interest.Amount.Percentage", Double.valueOf(0.0D)),
    InterestCutoff("System.Interest.Amount.Cutoff", Double.valueOf(0.0D)),
    InterestMin("System.Interest.Amount.Maximum", Double.valueOf(1.0D)),
    InterestMax("System.Interest.Amount.Minimum", Double.valueOf(2.0D)),
    DatabaseType("System.Database.Type", "MiniDB"),
    DatabaseTable("System.Database.Table", "iConomy"),
    DatabaseUrl("System.Database.URL", "mysql:\\\\localhost:3306\\iConomy"),
    DatabaseUsername("System.Database.Username", "root"),
    DatabasePassword("System.Database.Password", ""),
    DatabaseMajorItem("System.Database.MajorItem", Integer.valueOf(266)),
    DatabaseMinorItem("System.Database.MinorItem", Integer.valueOf(265));
    
    String node;
    
    Object value;
    
    Nodes(String node, Object value) {
      this.node = node;
      this.value = value;
    }
    
    public String getNode() {
      return this.node;
    }
    
    public Object getValue() {
      return this.value;
    }
    
    public Boolean getBoolean() {
      return (Boolean)this.value;
    }
    
    public Integer getInteger() {
      return (Integer)this.value;
    }
    
    public Double getDouble() {
      return (Double)this.value;
    }
    
    public Long getLong() {
      return (Long)this.value;
    }
    
    public List<String> getStringList() {
      return (List<String>)this.value;
    }
    
    public void setValue(Object value) {
      this.value = value;
    }
    
    public String toString() {
      return String.valueOf(this.value);
    }
  }
  
  public static void load(Configuration config) {
    config.load();
    for (Nodes n : Nodes.values()) {
      if (config.getProperty(n.getNode()) != null)
        n.setValue(config.getProperty(n.getNode())); 
    } 
  }
}
