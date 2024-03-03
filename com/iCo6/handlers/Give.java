package com.iCo6.handlers;

import com.iCo6.command.Handler;
import com.iCo6.command.Parser;
import com.iCo6.command.exceptions.InvalidUsage;
import com.iCo6.iConomy;
import com.iCo6.system.Account;
import com.iCo6.system.Accounts;
import com.iCo6.util.Messaging;
import com.iCo6.util.Template;
import java.util.LinkedHashMap;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class Give extends Handler {
  private Accounts Accounts = new Accounts();
  
  public Give(iConomy plugin) {
    super(plugin, iConomy.Template);
  }
  
  public boolean perform(CommandSender sender, LinkedHashMap<String, Parser.Argument> arguments) throws InvalidUsage {
    Double amount;
    Account senderacc = new Account(sender.getName());
    String name = ((Parser.Argument)arguments.get("name")).getStringValue();
    String tag = this.template.color(Template.Node.TAG_MONEY);
    if (name.equals("0"))
      throw new InvalidUsage("Missing name parameter: /money give <name> <amount>"); 
    if (sender.getName().equals(name))
      throw new InvalidUsage("You cant send money to yourself, silly!");
    if (((Parser.Argument)arguments.get("amount")).getStringValue().equals("empty"))
      throw new InvalidUsage("Missing amount parameter: /money give <name> <amount>");
    try {
      amount = ((Parser.Argument)arguments.get("amount")).getDoubleValue();
    } catch (NumberFormatException e) {
      throw new InvalidUsage("Invalid amount parameter, must be double.");
    } 
    if (Double.isInfinite(amount.doubleValue()) || Double.isNaN(amount.doubleValue()))
      throw new InvalidUsage("Invalid amount parameter, must be double.");
    if (senderacc.getHoldings().getBalance() < 1)
      throw new InvalidUsage("You got no money!");
    if (amount < 0)
      throw new InvalidUsage("Invalid payment amount.");
    if (amount > senderacc.getHoldings().getBalance())
      throw new InvalidUsage("You're trying to send more money than you own!");
    if (!this.Accounts.exists(name)) {
      this.template.set(Template.Node.ERROR_ACCOUNT);
      this.template.add("name", name);
      Messaging.send(sender, tag + this.template.parse());
      return false;
    }

    Account account = new Account(name);
    account.getHoldings().add(amount.doubleValue());
    this.template.set(Template.Node.PLAYER_CREDIT);
    this.template.add("name", name);
    this.template.add("balance", account.getHoldings().toString());

    senderacc.getHoldings().subtract(amount.doubleValue());
    this.template.set(Template.Node.PLAYER_CREDIT);
    this.template.add("name", name);
    this.template.add("balance", senderacc.getHoldings().toString());
    Messaging.send(sender, tag + "Gave " + name +" "+ ChatColor.WHITE + amount +ChatColor.DARK_GREEN + " Dollars.");
    Player reciever = Bukkit.getPlayer(name.toString());
    if (reciever != null)
     Messaging.send(reciever, tag + sender.getName() + " Gave you " + ChatColor.WHITE + amount +ChatColor.DARK_GREEN + " Dollars.");
    else
      System.out.println("Player offline, Not sending message");
    return false;
  }
}
