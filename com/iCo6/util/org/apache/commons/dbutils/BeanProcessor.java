package com.iCo6.util.org.apache.commons.dbutils;

import java.beans.BeanInfo;
import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.sql.Date;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Time;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class BeanProcessor {
  protected static final int PROPERTY_NOT_FOUND = -1;

  private static final Map<Class<?>, Object> primitiveDefaults = new HashMap<Class<?>, Object>();

  static {
    primitiveDefaults.put(int.class, Integer.valueOf(0));
    primitiveDefaults.put(short.class, Short.valueOf((short)0));
    primitiveDefaults.put(byte.class, Byte.valueOf((byte)0));
    primitiveDefaults.put(float.class, Float.valueOf(0.0F));
    primitiveDefaults.put(double.class, Double.valueOf(0.0D));
    primitiveDefaults.put(long.class, Long.valueOf(0L));
    primitiveDefaults.put(boolean.class, Boolean.FALSE);
  }

  public <T> T toBean(ResultSet rs, Class<T> type) throws SQLException {
    PropertyDescriptor[] props = propertyDescriptors(type);
    ResultSetMetaData rsmd = rs.getMetaData();
    int[] columnToProperty = mapColumnsToProperties(rsmd, props);
    return createBean(rs, type, props, columnToProperty);
  }

  public <T> List<T> toBeanList(ResultSet rs, Class<T> type) throws SQLException {
    List<T> results = new ArrayList<T>();
    if (!rs.next())
      return results;
    PropertyDescriptor[] props = propertyDescriptors(type);
    ResultSetMetaData rsmd = rs.getMetaData();
    int[] columnToProperty = mapColumnsToProperties(rsmd, props);
    do {
      results.add(createBean(rs, type, props, columnToProperty));
    } while (rs.next());
    return results;
  }

  private <T> T createBean(ResultSet rs, Class<T> type, PropertyDescriptor[] props, int[] columnToProperty) throws SQLException {
    T bean = newInstance(type);
    for (int i = 1; i < columnToProperty.length; i++) {
      if (columnToProperty[i] != -1) {
        PropertyDescriptor prop = props[columnToProperty[i]];
        Class<?> propType = prop.getPropertyType();
        Object value = processColumn(rs, i, propType);
        if (propType != null && value == null && propType.isPrimitive())
          value = primitiveDefaults.get(propType);
        callSetter(bean, prop, value);
      }
    }
    return bean;
  }

  private void callSetter(Object target, PropertyDescriptor prop, Object value) throws SQLException {
    Method setter = prop.getWriteMethod();
    if (setter == null)
      return;
    Class<?>[] params = setter.getParameterTypes();
    try {
      if (value != null &&
              value instanceof Date)
        if (params[0].getName().equals("java.sql.Date")) {
          value = new Date(((Date)value).getTime());
        } else if (params[0].getName().equals("java.sql.Time")) {
          value = new Time(((Date)value).getTime());
        } else if (params[0].getName().equals("java.sql.Timestamp")) {
          value = new Timestamp(((Date)value).getTime());
        }
      if (isCompatibleType(value, params[0])) {
        setter.invoke(target, new Object[] { value });
      } else {
        throw new SQLException("Cannot set " + prop.getName() + ": incompatible types.");
      }
    } catch (IllegalArgumentException e) {
      throw new SQLException("Cannot set " + prop.getName() + ": " + e.getMessage());
    } catch (IllegalAccessException e) {
      throw new SQLException("Cannot set " + prop.getName() + ": " + e.getMessage());
    } catch (InvocationTargetException e) {
      throw new SQLException("Cannot set " + prop.getName() + ": " + e.getMessage());
    }
  }

  private boolean isCompatibleType(Object value, Class<?> type) {
    if (value == null || type.isInstance(value))
      return true;
    if (type.equals(int.class) && Integer.class.isInstance(value))
      return true;
    if (type.equals(long.class) && Long.class.isInstance(value))
      return true;
    if (type.equals(double.class) && Double.class.isInstance(value))
      return true;
    if (type.equals(float.class) && Float.class.isInstance(value))
      return true;
    if (type.equals(short.class) && Short.class.isInstance(value))
      return true;
    if (type.equals(byte.class) && Byte.class.isInstance(value))
      return true;
    if (type.equals(char.class) && Character.class.isInstance(value))
      return true;
    if (type.equals(boolean.class) && Boolean.class.isInstance(value))
      return true;
    return false;
  }

  protected <T> T newInstance(Class<T> c) throws SQLException {
    try {
      return c.newInstance();
    } catch (InstantiationException e) {
      throw new SQLException("Cannot create " + c.getName() + ": " + e.getMessage());
    } catch (IllegalAccessException e) {
      throw new SQLException("Cannot create " + c.getName() + ": " + e.getMessage());
    }
  }

  private PropertyDescriptor[] propertyDescriptors(Class<?> c) throws SQLException {
    BeanInfo beanInfo = null;
    try {
      beanInfo = Introspector.getBeanInfo(c);
    } catch (IntrospectionException e) {
      throw new SQLException("Bean introspection failed: " + e.getMessage());
    }
    return beanInfo.getPropertyDescriptors();
  }

  protected int[] mapColumnsToProperties(ResultSetMetaData rsmd, PropertyDescriptor[] props) throws SQLException {
    int cols = rsmd.getColumnCount();
    int[] columnToProperty = new int[cols + 1];
    Arrays.fill(columnToProperty, -1);
    for (int col = 1; col <= cols; col++) {
      String columnName = rsmd.getColumnLabel(col);
      if (null == columnName || 0 == columnName.length())
        columnName = rsmd.getColumnName(col);
      for (int i = 0; i < props.length; i++) {
        if (columnName.equalsIgnoreCase(props[i].getName())) {
          columnToProperty[col] = i;
          break;
        }
      }
    }
    return columnToProperty;
  }

  protected Object processColumn(ResultSet rs, int index, Class<?> propType) throws SQLException {
    if (!propType.isPrimitive() && rs.getObject(index) == null)
      return null;
    if (!propType.isPrimitive() && rs.getObject(index) == null)
      return null;
    if (propType.equals(String.class))
      return rs.getString(index);
    if (propType.equals(int.class) || propType.equals(Integer.class))
      return Integer.valueOf(rs.getInt(index));
    if (propType.equals(boolean.class) || propType.equals(Boolean.class))
      return Boolean.valueOf(rs.getBoolean(index));
    if (propType.equals(long.class) || propType.equals(Long.class))
      return Long.valueOf(rs.getLong(index));
    if (propType.equals(double.class) || propType.equals(Double.class))
      return Double.valueOf(rs.getDouble(index));
    if (propType.equals(float.class) || propType.equals(Float.class))
      return Float.valueOf(rs.getFloat(index));
    if (propType.equals(short.class) || propType.equals(Short.class))
      return Short.valueOf(rs.getShort(index));
    if (propType.equals(byte.class) || propType.equals(Byte.class))
      return Byte.valueOf(rs.getByte(index));
    if (propType.equals(Timestamp.class))
      return rs.getTimestamp(index);
    return rs.getObject(index);
  }
}
