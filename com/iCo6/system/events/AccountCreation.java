package com.iCo6.system.events;

import org.bukkit.event.Event;

public class AccountCreation extends Event {
  private final String account;
  
  private boolean cancelled = false;
  
  public AccountCreation(String account) {
    super("ACCOUNT_REMOVE");
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


