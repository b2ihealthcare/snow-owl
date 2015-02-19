/*
 * Copyright (c) 2004 - 2012 Eike Stepper (Berlin, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.net4j.util;

import org.eclipse.net4j.internal.util.bundle.OM;
import org.eclipse.net4j.util.collection.Pair;
import org.eclipse.net4j.util.io.IOUtil;
import org.eclipse.net4j.util.lifecycle.Lifecycle;

import java.io.PrintStream;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.WeakHashMap;

/**
 * Various static helper methods for dealing with Java reflection.
 * 
 * @author Eike Stepper
 */
public final class ReflectUtil
{
  private static final String NAMESPACE_SEPARATOR = "."; //$NON-NLS-1$

  public static final Class<Object> ROOT_CLASS = Object.class;

  public static final Class<?>[] NO_PARAMETERS = null;

  public static final Object[] NO_ARGUMENTS = null;

  private static final Method HASH_CODE_METHOD = lookupHashCodeMethod();

  private static final Map<Object, Long> ids = new WeakHashMap<Object, Long>();

  public static boolean DUMP_STATICS = false;

  private static long lastID;

  private ReflectUtil()
  {
  }

  public static Method getMethod(Class<?> c, String methodName, Class<?>... parameterTypes)
  {
    try
    {
      try
      {
        return c.getDeclaredMethod(methodName, parameterTypes);
      }
      catch (NoSuchMethodException ex)
      {
        Class<?> superclass = c.getSuperclass();
        if (superclass != null)
        {
          return getMethod(superclass, methodName, parameterTypes);
        }

        throw ex;
      }
    }
    catch (Exception ex)
    {
      throw WrappedException.wrap(ex);
    }
  }

  public static Object invokeMethod(Method method, Object target, Object... arguments)
  {
    boolean accessible = method.isAccessible();
    if (!accessible)
    {
      method.setAccessible(true);
    }

    try
    {
      return method.invoke(target, arguments);
    }
    catch (Exception ex)
    {
      throw WrappedException.wrap(ex);
    }
    finally
    {
      if (!accessible)
      {
        method.setAccessible(false);
      }
    }
  }

  public static Field getField(Class<?> c, String fieldName)
  {
    try
    {
      try
      {
        return c.getDeclaredField(fieldName);
      }
      catch (NoSuchFieldException ex)
      {
        Class<?> superclass = c.getSuperclass();
        if (superclass != null)
        {
          return getField(superclass, fieldName);
        }

        return null;
      }
    }
    catch (Exception ex)
    {
      throw WrappedException.wrap(ex);
    }
  }

  public static void collectFields(Class<?> c, List<Field> fields)
  {
    if (c == ROOT_CLASS)
    {
      return;
    }

    // Recurse
    collectFields(c.getSuperclass(), fields);

    try
    {
      Field[] declaredFields = c.getDeclaredFields();
      for (Field field : declaredFields)
      {
        if (field.isSynthetic())
        {
          continue;
        }

        if ((field.getModifiers() & Modifier.STATIC) != 0 && !DUMP_STATICS)
        {
          continue;
        }

        if (field.getAnnotation(ExcludeFromDump.class) != null)
        {
          continue;
        }

        fields.add(field);
      }
    }
    catch (Exception ex)
    {
      throw WrappedException.wrap(ex);
    }
  }

  public static Object getValue(Field field, Object target)
  {
    if (!field.isAccessible())
    {
      field.setAccessible(true);
    }

    try
    {
      return field.get(target);
    }
    catch (Exception ex)
    {
      throw WrappedException.wrap(ex);
    }
  }

  public static void setValue(Field field, Object target, Object value)
  {
    if (!field.isAccessible())
    {
      field.setAccessible(true);
    }

    try
    {
      field.set(target, value);
    }
    catch (Exception ex)
    {
      throw WrappedException.wrap(ex);
    }
  }

  public static void printStackTrace(PrintStream out, StackTraceElement[] stackTrace)
  {
    for (int i = 2; i < stackTrace.length; i++)
    {
      StackTraceElement stackTraceElement = stackTrace[i];
      out.println("\tat " + stackTraceElement); //$NON-NLS-1$
    }
  }

  public static void printStackTrace(StackTraceElement[] stackTrace)
  {
    printStackTrace(System.err, stackTrace);
  }

  public static Integer getHashCode(Object object)
  {
    try
    {
      return (Integer)HASH_CODE_METHOD.invoke(object, NO_ARGUMENTS);
    }
    catch (Exception ex)
    {
      IOUtil.print(ex);
    }

    return 0;
  }

  public static synchronized Long getID(Object object)
  {
    Long id = ids.get(object);
    if (id == null)
    {
      id = ++lastID;
      ids.put(object, id);
    }

    return id;
  }

  public static String getPackageName(Class<? extends Object> c)
  {
    if (c == null)
    {
      return null;
    }

    return getPackageName(c.getName());
  }

  public static String getPackageName(String className)
  {
    if (className == null)
    {
      return null;
    }

    int lastDot = className.lastIndexOf('.');
    if (lastDot != -1)
    {
      className = className.substring(0, lastDot);
    }

    return className;
  }

  public static String getSimpleName(Class<? extends Object> c)
  {
    if (c == null)
    {
      return null;
    }

    return c.getSimpleName();
  }

  public static String getSimpleClassName(String name)
  {
    if (name == null)
    {
      return null;
    }

    int lastDot = name.lastIndexOf('.');
    if (lastDot != -1)
    {
      name = name.substring(lastDot + 1);
    }

    return name.replace('$', '.');
  }

  public static String getSimpleClassName(Object object)
  {
    if (object == null)
    {
      return null;
    }

    return getSimpleName(object.getClass());
  }

  public static String getLabel(Object object)
  {
    if (object == null)
    {
      return null;
    }

    String name = object.getClass().getSimpleName();
    if (name.length() == 0)
    {
      name = "anonymous"; //$NON-NLS-1$
    }

    return name + "@" + getID(object); //$NON-NLS-1$
  }

  public static void dump(Object object)
  {
    dump(object, ""); //$NON-NLS-1$
  }

  public static void dump(Object object, String prefix)
  {
    dump(object, prefix, IOUtil.OUT());
  }

  public static void dump(Object object, String prefix, PrintStream out)
  {
    out.print(toString(object, prefix));
  }

  @SuppressWarnings("unchecked")
  public static Pair<Field, Object>[] dumpToArray(Object object)
  {
    List<Field> fields = new ArrayList<Field>();
    collectFields(object.getClass(), fields);
    Pair<Field, Object>[] result = new Pair[fields.size()];
    int i = 0;
    for (Field field : fields)
    {
      Object value = getValue(field, object);
      result[i++] = new Pair<Field, Object>(field, value);
    }

    return result;
  }

  public static Object instantiate(Map<Object, Object> properties, String namespace, String classKey,
      ClassLoader classLoader) throws ClassNotFoundException, InstantiationException, IllegalAccessException,
      IllegalArgumentException, InvocationTargetException
  {
    if (namespace != null)
    {
      if (namespace.length() == 0)
      {
        namespace = null;
      }
      else if (!namespace.endsWith(NAMESPACE_SEPARATOR))
      {
        namespace += NAMESPACE_SEPARATOR;
      }
    }

    String className = null;
    Map<String, Object> values = new HashMap<String, Object>();
    for (Entry<Object, Object> entry : properties.entrySet())
    {
      if (entry.getKey() instanceof String)
      {
        String key = (String)entry.getKey();
        if (namespace != null)
        {
          if (key.startsWith(namespace))
          {
            key = key.substring(namespace.length());
          }
          else
          {
            continue;
          }
        }

        if (classKey.equals(key))
        {
          Object classValue = entry.getValue();
          if (classValue instanceof String)
          {
            className = (String)classValue;
          }
          else
          {
            OM.LOG.warn("Value of classKey " + classKey + " is not a String"); //$NON-NLS-1$ //$NON-NLS-2$
          }
        }
        else
        {
          values.put(key, entry.getValue());
        }
      }
    }

    if (className == null)
    {
      throw new IllegalArgumentException("Properties do not contain a valid class name for key " + classKey); //$NON-NLS-1$
    }

    Class<?> c = classLoader.loadClass(className);
    Object instance = c.newInstance();
    Method[] methods = c.getMethods();
    for (Method method : methods)
    {
      if (isSetter(method))
      {
        String name = StringUtil.uncap(method.getName().substring(3));
        Object value = values.get(name);
        if (value != null)
        {
          Class<?> type = method.getParameterTypes()[0];
          if (!type.isAssignableFrom(value.getClass()))
          {
            if (value instanceof String)
            {
              String str = (String)value;
              value = null;
              if (type.isAssignableFrom(Boolean.class))
              {
                value = Boolean.parseBoolean(str);
              }
              else if (type.isAssignableFrom(Byte.class))
              {
                value = Byte.parseByte(str);
              }
              else if (type.isAssignableFrom(Short.class))
              {
                value = Short.parseShort(str);
              }
              else if (type.isAssignableFrom(Integer.class))
              {
                value = Integer.parseInt(str);
              }
              else if (type.isAssignableFrom(Long.class))
              {
                value = Long.parseLong(str);
              }
              else if (type.isAssignableFrom(Float.class))
              {
                value = Float.parseFloat(str);
              }
              else if (type.isAssignableFrom(Double.class))
              {
                value = Double.parseDouble(str);
              }
            }
            else
            {
              value = null;
            }
          }

          if (value == null)
          {
            throw new IllegalArgumentException("Value of property " + name + " can not be assigned to type " //$NON-NLS-1$ //$NON-NLS-2$
                + type.getName());
          }

          method.invoke(instance, value);
        }
      }
    }

    return instance;
  }

  public static boolean isSetter(Method method)
  {
    return method.getParameterTypes().length == 1 && isSetterName(method.getName());
  }

  public static boolean isSetterName(String name)
  {
    return name.startsWith("set") && name.length() > 3 && Character.isUpperCase(name.charAt(3)); //$NON-NLS-1$
  }

  public static String toString(Object object)
  {
    return toString(object, "  "); //$NON-NLS-1$
  }

  public static String toString(Object object, String prefix)
  {
    StringBuilder builder = new StringBuilder();
    builder.append(prefix);
    builder.append(getLabel(object));
    builder.append(StringUtil.NL);
    toString(object.getClass(), object, prefix, builder);
    return builder.toString();
  }

  private static void toString(Class<? extends Object> segment, Object object, String prefix, StringBuilder builder)
  {
    if (segment == ROOT_CLASS || segment == Lifecycle.class)
    {
      return;
    }

    // Recurse
    toString(segment.getSuperclass(), object, prefix, builder);

    String segmentPrefix = segment == object.getClass() ? "" : getSimpleName(segment) + NAMESPACE_SEPARATOR; //$NON-NLS-1$
    for (Field field : segment.getDeclaredFields())
    {
      if (field.isSynthetic())
      {
        continue;
      }

      if ((field.getModifiers() & Modifier.STATIC) != 0 && !DUMP_STATICS)
      {
        continue;
      }

      if (field.getAnnotation(ExcludeFromDump.class) != null)
      {
        continue;
      }

      builder.append(prefix);
      builder.append(segmentPrefix);
      builder.append(field.getName());
      builder.append(" = "); //$NON-NLS-1$

      Object value = getValue(field, object);
      if (value instanceof Map<?, ?>)
      {
        value = ((Map<?, ?>)value).entrySet();
      }

      if (value instanceof Collection<?>)
      {
        builder.append(StringUtil.NL);
        Collection<?> collection = (Collection<?>)value;
        Object[] array = collection.toArray(new Object[collection.size()]);
        for (Object element : array)
        {
          builder.append("    "); //$NON-NLS-1$
          builder.append(element);
          builder.append(StringUtil.NL);
        }
      }
      else
      {
        builder.append(value);
        builder.append(StringUtil.NL);
      }
    }
  }

  private static Method lookupHashCodeMethod()
  {
    Method method;

    try
    {
      method = ROOT_CLASS.getMethod("hashCode", NO_PARAMETERS); //$NON-NLS-1$
    }
    catch (Exception ex)
    {
      // This can really not happen
      throw new AssertionError();
    }

    if (!method.isAccessible())
    {
      method.setAccessible(true);
    }

    return method;
  }

  /**
   * Annotates fields that are to be skipped in {@link ReflectUtil#collectFields(Class, List)
   * ReflectUtil.collectFields()} and {@link ReflectUtil#toString(Object) ReflectUtil.toString()}.
   * 
   * @author Eike Stepper
   * @apiviz.exclude
   */
  @Retention(RetentionPolicy.RUNTIME)
  @Target(ElementType.FIELD)
  public @interface ExcludeFromDump
  {
  }
}
