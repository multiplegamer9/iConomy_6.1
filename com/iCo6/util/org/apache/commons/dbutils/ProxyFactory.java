package com.iCo6.util.org.apache.commons.dbutils;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Proxy;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.Driver;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.Statement;

public class ProxyFactory {
  private static final ProxyFactory instance = new ProxyFactory();
  
  public static ProxyFactory instance() {
    return instance;
  }
  
  public <T> T newProxyInstance(Class<T> type, InvocationHandler handler) {
    return type.cast(Proxy.newProxyInstance(handler.getClass().getClassLoader(), new Class[] { type }, handler));
  }
  
  public CallableStatement createCallableStatement(InvocationHandler handler) {
    return newProxyInstance(CallableStatement.class, handler);
  }
  
  public Connection createConnection(InvocationHandler handler) {
    return newProxyInstance(Connection.class, handler);
  }
  
  public Driver createDriver(InvocationHandler handler) {
    return newProxyInstance(Driver.class, handler);
  }
  
  public PreparedStatement createPreparedStatement(InvocationHandler handler) {
    return newProxyInstance(PreparedStatement.class, handler);
  }
  
  public ResultSet createResultSet(InvocationHandler handler) {
    return newProxyInstance(ResultSet.class, handler);
  }
  
  public ResultSetMetaData createResultSetMetaData(InvocationHandler handler) {
    return newProxyInstance(ResultSetMetaData.class, handler);
  }
  
  public Statement createStatement(InvocationHandler handler) {
    return newProxyInstance(Statement.class, handler);
  }
}


