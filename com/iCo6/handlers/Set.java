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
import org.bukkit.command.CommandSender;

public class Set extends Handler {
  private Accounts Accounts = new Accounts();
  
  public Set(iConomy plugin) {
    super(plugin, iConomy.Template);
  }
  
  public boolean perform(CommandSender sender, LinkedHashMap<String, Parser.Argument> arguments) throws InvalidUsage {
    Double amount;
    String name = ((Parser.Argument)arguments.get("name")).getStringValue();
    String tag = this.template.color(Template.Node.TAG_MONEY);
    if (!sender.isOp())
      throw new InvalidUsage("You dont have permission to use this command.");
    if (name.equals("0"))
      throw new InvalidUsage("Missing name parameter: /money set <name> <amount>"); 
    if (((Parser.Argument)arguments.get("amount")).getStringValue().equals("empty"))
      throw new InvalidUsage("Missing amount parameter: /money set <name> <amount>"); 
    try {
      amount = ((Parser.Argument)arguments.get("amount")).getDoubleValue();
    } catch (NumberFormatException e) {
      throw new InvalidUsage("Invalid amount parameter, must be double.");
    } 
    if (Double.isInfinite(amount.doubleValue()) || Double.isNaN(amount.doubleValue()))
      throw new InvalidUsage("Invalid amount parameter, must be double."); 
    if (!this.Accounts.exists(name)) {
      this.template.set(Template.Node.ERROR_ACCOUNT);
      this.template.add("name", name);
      Messaging.send(sender, tag + this.template.parse());
      return false;
    } 
    Account account = new Account(name);
    account.getHoldings().setBalance(amount.doubleValue());
    this.template.set(Template.Node.PLAYER_SET);
    this.template.add("name", name);
    this.template.add("balance", account.getHoldings().toString());
    Messaging.send(sender, tag + this.template.parse());
    return false;
  }
}

