package com.iCo6.util.org.apache.commons.dbutils;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;

public interface RowProcessor {
  Object[] toArray(ResultSet paramResultSet) throws SQLException;
  
  <T> T toBean(ResultSet paramResultSet, Class<T> paramClass) throws SQLException;
  
  <T> List<T> toBeanList(ResultSet paramResultSet, Class<T> paramClass) throws SQLException;
  
  Map<String, Object> toMap(ResultSet paramResultSet) throws SQLException;
}


