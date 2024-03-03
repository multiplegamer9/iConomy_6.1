package com.iCo6.system.events;

import org.bukkit.event.Event;

public class AccountReset extends Event {
  private final String account;
  
  private boolean cancelled = false;
  
  public AccountReset(String account) {
    super("ACCOUNT_RESET");
    this.account = account;
  }
  
  public String getAccountName() {
    return this.account;
  }
  
  public boolean isCancelled() {
    return this.cancelled;
  }
  
  public void setCancelled(boolean cancelled) {
    this.cancelled = cancelled;
  }
}


