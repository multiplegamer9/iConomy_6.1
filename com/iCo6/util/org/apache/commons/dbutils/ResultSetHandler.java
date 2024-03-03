package com.iCo6.util.org.apache.commons.dbutils;

import java.sql.ResultSet;
import java.sql.SQLException;

public interface ResultSetHandler<T> {
  T handle(ResultSet paramResultSet) throws SQLException;
}


