package com.iCo6.command;

import com.iCo6.command.exceptions.InvalidUsage;
import com.iCo6.iConomy;
import com.iCo6.util.Template;
import java.util.LinkedHashMap;
import java.util.List;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public abstract class Handler {
  protected final iConomy plugin;
  
  protected final Template template;
  
  public Handler(iConomy plugin, Template template) {
    this.plugin = plugin;
    this.template = template;
  }
  
  public abstract boolean perform(CommandSender paramCommandSender, LinkedHashMap<String, Parser.Argument> paramLinkedHashMap) throws InvalidUsage;
  
  protected static boolean isConsole(CommandSender sender) {
    if (!(sender instanceof Player))
      return true; 
    return false;
  }
  
  protected static Player getPlayer(CommandSender sender, String[] args, int index) {
    if (args.length > index) {
      List<Player> players = sender.getServer().matchPlayer(args[index]);
      if (players.isEmpty()) {
        sender.sendMessage("Could not find player with the name: " + args[index]);
        return null;
      } 
      return players.get(0);
    } 
    if (isConsole(sender))
      return null; 
    return (Player)sender;
  }
}


