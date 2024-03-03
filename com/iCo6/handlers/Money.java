package com.iCo6.handlers;

import com.iCo6.command.Handler;
import com.iCo6.command.Parser;
import com.iCo6.command.exceptions.InvalidUsage;
import com.iCo6.iConomy;
import com.iCo6.system.Account;
import com.iCo6.system.Accounts;
import com.iCo6.util.Messaging;
import java.util.LinkedHashMap;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class Money extends Handler {
  private Accounts Accounts = new Accounts();
  
  public Money(iConomy plugin) {
    super(plugin, iConomy.Template);
  }
  
  public boolean perform(CommandSender sender, LinkedHashMap<String, Parser.Argument> arguments) throws InvalidUsage {
    String name = ((Parser.Argument)arguments.get("name")).getStringValue();
    if (name.equals("0")) {
      if (isConsole(sender)) {
        Messaging.send(sender, "`rCannot check money on non-living organism.");
        return false;
      } 
      Player player = (Player)sender;
      if (player == null)
        return false; 
      Account account1 = new Account(player.getName());
      account1.getHoldings().showBalance(null);
      return false;
    } 
    if (!this.Accounts.exists(name)) {
      Messaging.send(sender, "`rAccount for " + name + " does not exist!");
      return false;
    } 
    Account account = new Account(name);
    account.getHoldings().showBalance(sender);
    return false;
  }
}


