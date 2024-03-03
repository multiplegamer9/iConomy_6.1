package com.iCo6.system;

import com.iCo6.iConomy;
import com.iCo6.util.Messaging;
import com.iCo6.util.Template;
import org.bukkit.entity.Player;

public class Account {
  String name;
  
  public Account(String name) {
    this.name = name;
  }
  
  public Account(String name, Boolean create) {
    this.name = name;
  }
  
  public void showHoldings(boolean console) {
    if (console)
      return; 
    Player player = iConomy.Server.getPlayer(this.name);
    if (iConomy.Server.getPlayer(this.name) == null)
      return; 
    String tag = iConomy.Template.color(Template.Node.TAG_MONEY);
    Template template = iConomy.Template;
    template.set(Template.Node.PERSONAL_BALANCE);
    template.add("balance", getHoldings().getBalance());
    Messaging.send(player, tag + template.parse());
  }
  
  public Holdings getHoldings() {
    return new Holdings(this.name);
  }
  
  public boolean remove() {
    return Queried.removeAccount(this.name);
  }
  
  public String toString() {
    String tag = iConomy.Template.raw(Template.Node.TAG_MONEY);
    Template template = iConomy.Template;
    template.set(Template.Node.PLAYER_BALANCE);
    template.add("name", this.name);
    template.add("balance", getHoldings().getBalance());
    return tag + template.parseRaw();
  }
}


