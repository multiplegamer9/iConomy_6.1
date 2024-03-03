package com.iCo6.handlers;

import com.iCo6.command.Handler;
import com.iCo6.command.Parser;
import com.iCo6.command.exceptions.InvalidUsage;
import com.iCo6.iConomy;
import com.iCo6.system.Accounts;
import com.iCo6.util.Messaging;
import com.iCo6.util.Template;
import java.util.LinkedHashMap;
import org.bukkit.command.CommandSender;

public class Create extends Handler {
  private Accounts Accounts = new Accounts();
  
  public Create(iConomy plugin) {
    super(plugin, iConomy.Template);
  }
  
  public boolean perform(CommandSender sender, LinkedHashMap<String, Parser.Argument> arguments) throws InvalidUsage {
    String name = ((Parser.Argument)arguments.get("name")).getStringValue();
    String tag = this.template.color(Template.Node.TAG_MONEY);
    if (!sender.isOp())
      throw new InvalidUsage("You dont have permission to use this command.");
    if (name.equals("0"))
      throw new InvalidUsage("Missing name parameter: /money create <name>"); 
    if (this.Accounts.exists(name)) {
      this.template.set(Template.Node.ERROR_EXISTS);
      Messaging.send(sender, tag + this.template.parse());
      return false;
    } 
    if (!this.Accounts.create(name)) {
      this.template.set(Template.Node.ERROR_CREATE);
      this.template.add("name", name);
      Messaging.send(sender, tag + this.template.parse());
      return false;
    } 
    this.template.set(Template.Node.ACCOUNTS_CREATE);
    this.template.add("name", name);
    Messaging.send(sender, tag + this.template.parse());
    return false;
  }
}

