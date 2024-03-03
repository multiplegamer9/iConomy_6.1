package com.iCo6;

import com.iCo6.IO.Database;
import com.iCo6.IO.exceptions.MissingDriver;
import com.iCo6.command.Handler;
import com.iCo6.command.Parser;
import com.iCo6.command.exceptions.InvalidUsage;
import com.iCo6.handlers.Create;
import com.iCo6.handlers.Give;
import com.iCo6.handlers.Money;
import com.iCo6.handlers.Remove;
import com.iCo6.handlers.Set;
import com.iCo6.handlers.Take;
import com.iCo6.system.Account;
import com.iCo6.system.Accounts;
import com.iCo6.system.Holdings;
import com.iCo6.util.Common;
import com.iCo6.util.Messaging;
import com.iCo6.util.Template;
import com.iCo6.util.org.apache.commons.dbutils.DbUtils;
import com.iCo6.util.org.apache.commons.dbutils.QueryRunner;
import com.iCo6.util.wget;
import com.nijikokun.bukkit.Permissions.Permissions;
import java.io.File;
import java.sql.Connection;
import java.sql.SQLException;
import java.text.DecimalFormat;
import java.util.Locale;
import org.bukkit.Server;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.craftbukkit.CraftServer;
import org.bukkit.entity.Player;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.util.config.Configuration;

public class iConomy extends JavaPlugin {
  public PluginDescriptionFile info;
  
  public PluginManager manager;
  
  private static Accounts Accounts = new Accounts();
  
  private Parser Commands = new Parser();
  
  private Permissions Permissions;

  public static boolean TerminalSupport = false;
  
  public static File directory;
  
  public static Database Database;
  
  public static Server Server;
  
  public static Template Template;
  
  public void onEnable() {
    long l1, startTime = System.nanoTime();
    try {
      Locale.setDefault(Locale.US);
      Server = getServer();
      TerminalSupport = ((CraftServer)getServer()).getReader().getTerminal().isANSISupported();
      this.info = getDescription();
      directory = getDataFolder();
      if (!directory.exists())
        directory.mkdir(); 
      Common.extract(new String[] { "Config.yml", "Template.yml" });
      Constants.load(new Configuration(new File(directory, "Config.yml")));
      Template = new Template(directory.getPath(), "Template.yml");
      Database.Type type = Database.getType(Constants.Nodes.DatabaseType.toString());
      if (!type.equals(com.iCo6.IO.Database.Type.InventoryDB) && !type.equals(com.iCo6.IO.Database.Type.MiniDB)) {
        Constants.Drivers driver = null;
        switch (type) {
          case H2DB:
            driver = Constants.Drivers.H2;
            break;
          case MySQL:
            driver = Constants.Drivers.MySQL;
            break;
          case SQLite:
            driver = Constants.Drivers.SQLite;
            break;
          case Postgre:
            driver = Constants.Drivers.Postgre;
            break;
        } 
        if (driver != null && 
          !(new File("lib", driver.getFilename())).exists()) {
          System.out.println("[iConomy] Downloading " + driver.getFilename() + "...");
          wget.fetch(driver.getUrl(), driver.getFilename());
          System.out.println("[iConomy] Finished Downloading.");
        } 
      } 
      this.Commands.add("/money +name", (Handler)new Money(this));
      this.Commands.add("/money -create +name", (Handler)new Create(this));
      this.Commands.add("/money -remove +name", (Handler)new Remove(this));
      this.Commands.add("/money -give +name +amount:empty", (Handler)new Give(this));
      this.Commands.add("/money -take +name +amount:empty", (Handler)new Take(this));
      this.Commands.add("/money -set +name +amount:empty", (Handler)new Set(this));
      try {
        Database = new Database(Constants.Nodes.DatabaseType.toString(), Constants.Nodes.DatabaseUrl.toString(), Constants.Nodes.DatabaseUsername.toString(), Constants.Nodes.DatabasePassword.toString());
        if (Database.getDatabase() == null && 
          !Database.tableExists(Constants.Nodes.DatabaseTable.toString())) {
          System.out.println("should create table..");
          String SQL = Common.resourceToString("SQL/Core/Create-Table-" + Database.getType().toString().toLowerCase() + ".sql");
          SQL = String.format(SQL, new Object[] { Constants.Nodes.DatabaseTable.getValue() });
          try {
            QueryRunner run = new QueryRunner();
            Connection c = Database.getConnection();
            try {
              run.update(c, SQL);
            } catch (SQLException ex) {
              System.out.println("[iConomy] Error creating database: " + ex);
            } finally {
              DbUtils.close(c);
            } 
          } catch (SQLException ex) {
            System.out.println("[iConomy] Database Error: " + ex);
          } 
        } 
      } catch (MissingDriver ex) {
        System.out.println(ex.getMessage());
      } 
      String name = "Nijikokun";
      System.out.println(name + " exists? " + Accounts.exists(name));
      Account Nijikokun = Accounts.get(name);
      Holdings holdings = Nijikokun.getHoldings();
      System.out.println("Balance: " + holdings.getBalance());
    } finally {
      l1 = System.nanoTime();
    } 
    long duration = l1 - startTime;
    System.out.println("[" + this.info.getName() + "] Enabled (" + Common.readableProfile(duration) + ")");
  }
  
  public void onDisable() {
    long l1;
    String name = this.info.getName();
    System.out.println("[" + name + "] Closing general data...");
    long startTime = System.nanoTime();
    try {
      this.info = null;
      Server = null;
      this.manager = null;
      Accounts = null;
      this.Commands = null;
      Database = null;
      Template = null;
      TerminalSupport = false;
    } finally {
      l1 = System.nanoTime();
    } 
    long duration = l1 - startTime;
    System.out.println("[" + name + "] Disabled. (" + Common.readableProfile(duration) + ")");
  }
  
  public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
    Handler handler = this.Commands.getHandler(command.getName());
    String split = "/" + command.getName().toLowerCase();
    for (int i = 0; i < args.length; i++)
      split = split + " " + args[i]; 
    Messaging.save(sender);
    this.Commands.save(split);
    this.Commands.parse();
    if (this.Commands.getHandler() != null)
      handler = this.Commands.getHandler(); 
    if (handler == null)
      return false; 
    try {
      return handler.perform(sender, this.Commands.getArguments());
    } catch (InvalidUsage ex) {
      Messaging.send(sender, ex.getMessage());
      return false;
    } 
  }
  
  public boolean hasPermissions(CommandSender sender, String command) {
    if (sender instanceof Player) {
      Player player = (Player)sender;
      if (this.Commands.hasPermission(command)) {
        String node = this.Commands.getPermission(command);
        if (this.Permissions != null)
          return Permissions.Security.permission(player, node);
        return player.isOp();
      } 
    } 
    return true;
  }
  
  public static String format(String account) {
    return Accounts.get(account).getHoldings().toString();
  }
  
  public static String format(double amount) {
    DecimalFormat formatter = new DecimalFormat("#,##0.00");
    String formatted = formatter.format(amount);
    if (formatted.endsWith("."))
      formatted = formatted.substring(0, formatted.length() - 1); 
    return Common.formatted(formatted, Constants.Nodes.Major.getStringList(), Constants.Nodes.Minor.getStringList());
  }
}

