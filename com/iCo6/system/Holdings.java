package com.iCo6.system;

import com.iCo6.Constants;
import com.iCo6.iConomy;
import com.iCo6.system.events.HoldingsUpdate;
import com.iCo6.util.Common;
import com.iCo6.util.Messaging;
import com.iCo6.util.Template;
import java.text.DecimalFormat;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;

public class Holdings {
  private String name;
  
  private Queried database = new Queried();
  
  public Holdings(String name) {
    this.name = name;
  }
  
  public Double getBalance() {
    return Double.valueOf(Queried.getBalance(this.name));
  }
  
  public void setBalance(double balance) {
    Queried.setBalance(this.name, balance);
  }
  
  public void showBalance(CommandSender to) {
    if (to != null) {
      String str = iConomy.Template.raw(Template.Node.TAG_MONEY);
      Template template1 = iConomy.Template;
      template1.set(Template.Node.PLAYER_BALANCE);
      template1.add("name", this.name);
      template1.add("balance", getBalance());
      Messaging.send(to, str + template1.parse());
    } 
    Player player = iConomy.Server.getPlayer(this.name);
    if (iConomy.Server.getPlayer(this.name) == null)
      return; 
    String tag = iConomy.Template.color(Template.Node.TAG_MONEY);
    Template template = iConomy.Template;
    template.set(Template.Node.PERSONAL_BALANCE);
    template.add("balance", getBalance());
    Messaging.send(player, tag + template.parse());
  }
  
  public void add(double amount) {
    double balance = getBalance().doubleValue();
    double ending = balance + amount;
    math(amount, balance, ending);
  }
  
  public void subtract(double amount) {
    double balance = getBalance().doubleValue();
    double ending = balance - amount;
    math(amount, balance, ending);
  }
  
  public void divide(double amount) {
    double balance = getBalance().doubleValue();
    double ending = balance / amount;
    math(amount, balance, ending);
  }
  
  public void multiply(double amount) {
    double balance = getBalance().doubleValue();
    double ending = balance * amount;
    math(amount, balance, ending);
  }
  
  public boolean isNegative() {
    return (getBalance().doubleValue() < 0.0D);
  }
  
  public boolean hasEnough(double amount) {
    return (amount <= getBalance().doubleValue());
  }
  
  public boolean hasOver(double amount) {
    return (amount < getBalance().doubleValue());
  }
  
  public boolean hasUnder(double amount) {
    return (amount > getBalance().doubleValue());
  }
  
  private void math(double amount, double balance, double ending) {
    HoldingsUpdate Event = new HoldingsUpdate(this.name, balance, ending, amount);
    iConomy.Server.getPluginManager().callEvent((Event)Event);
    if (!Event.isCancelled())
      setBalance(ending); 
  }
  
  public String toString() {
    DecimalFormat formatter = new DecimalFormat("#,##0.00");
    Double balance = getBalance();
    String formatted = formatter.format(balance);
    if (formatted.endsWith("."))
      formatted = formatted.substring(0, formatted.length() - 1); 
    return Common.formatted(formatted, Constants.Nodes.Major.getStringList(), Constants.Nodes.Minor.getStringList());
  }
}


