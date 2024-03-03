package com.iCo6.util.org.apache.commons.dbutils.handlers;

import com.iCo6.util.org.apache.commons.dbutils.RowProcessor;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Map;

public class MapListHandler extends AbstractListHandler<Map<String, Object>> {
  private final RowProcessor convert;
  
  public MapListHandler() {
    this(ArrayHandler.ROW_PROCESSOR);
  }
  
  public MapListHandler(RowProcessor convert) {
    this.convert = convert;
  }
  
  protected Map<String, Object> handleRow(ResultSet rs) throws SQLException {
    return this.convert.toMap(rs);
  }
}


