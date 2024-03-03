package com.iCo6.util.org.apache.commons.dbutils.handlers;

import com.iCo6.util.org.apache.commons.dbutils.ResultSetHandler;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public abstract class AbstractListHandler<T> implements ResultSetHandler<List<T>> {
  protected abstract T handleRow(ResultSet paramResultSet) throws SQLException;
  
  public List<T> handle(ResultSet rs) throws SQLException {
    List<T> rows = new ArrayList<T>();
    while (rs.next())
      rows.add(handleRow(rs)); 
    return rows;
  }
}


