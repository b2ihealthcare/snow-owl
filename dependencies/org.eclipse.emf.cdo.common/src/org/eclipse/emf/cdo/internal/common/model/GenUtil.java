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
package org.eclipse.emf.cdo.internal.common.model;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.eclipse.emf.ecore.EClassifier;
import org.eclipse.net4j.util.StringUtil;

/**
 * @author Eike Stepper
 */
public final class GenUtil
{
  private GenUtil()
  {
  }

  /**
   * See GenGenBaseImpl.isPrimitiveType
   */
  public static boolean isPrimitiveType(EClassifier eType)
  {
    try
    {
      // J9 2.2 has problems assigning null to a Class variable.
      Object result = eType.getInstanceClass();
      if (result == null)
      {
        return false;
      }

      Class<?> instanceClass = (Class<?>)result;
      return instanceClass.isPrimitive();
    }
    catch (Exception e)
    {
      return false;
    }
  }

  /**
   * See GenFeatureImpl.getUpperName
   */
  public static String getFeatureUpperName(String featureName)
  {
    return format(featureName, '_', null, false, true).toUpperCase();
  }

  /**
   * See GenFeatureImpl.getGetAccessor
   */
  public static String getFeatureGetterName(String featureName, boolean isBooleanType)
  {
    String capName = StringUtil.cap(featureName);
    // if (isMapEntryFeature())
    // return "getTyped" + capName;
    String result = isBooleanType ? "is" + capName : "get" + ("Class".equals(capName) ? "Class_" : capName); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$

    // if (isListType() && !isFeatureMapType() && !isMapType() &&
    // getGenModel().isArrayAccessors())
    // {
    // result += "List";
    // }

    // GenClass rootImplementsInterface =
    // getGenModel().getRootImplementsInterfaceGenClass();
    // GenClass context = getContext();
    // if (rootImplementsInterface != null &&
    // !rootImplementsInterface.isEObject())
    // {
    // for (GenOperation genOperation :
    // rootImplementsInterface.getAllGenOperations())
    // {
    // if (genOperation.getName().equals(result) &&
    // genOperation.getGenParameters().isEmpty() &&
    // !genOperation.getType(context).equals(getType(context)))
    // {
    // result = result + "_";
    // break;
    // }
    // }
    // }

    return result;
  }

  /**
   * Formats a name by parsing it into words separated by underscores and/or mixed-casing and then recombining them
   * using the specified separator. A prefix can also be given to be recognized as a separate word or to be trimmed.
   * Leading underscores can be ignored or can cause a leading separator to be prepended.
   */
  public static String format(String name, char separator, String prefix, boolean includePrefix,
      boolean includeLeadingSeparator)
  {
    String leadingSeparators = includeLeadingSeparator ? getLeadingSeparators(name, '_') : null;
    if (leadingSeparators != null)
    {
      name = name.substring(leadingSeparators.length());
    }

    List<String> parsedName = new ArrayList<String>();
    if (prefix != null && name.startsWith(prefix) && name.length() > prefix.length()
        && Character.isUpperCase(name.charAt(prefix.length())))
    {
      name = name.substring(prefix.length());
      if (includePrefix)
      {
        parsedName = parseName(prefix, '_');
      }
    }

    if (name.length() != 0)
    {
      parsedName.addAll(parseName(name, '_'));
    }

    StringBuilder result = new StringBuilder();

    for (Iterator<String> nameIter = parsedName.iterator(); nameIter.hasNext();)
    {
      String nameComponent = nameIter.next();
      result.append(nameComponent);

      if (nameIter.hasNext() && nameComponent.length() > 1)
      {
        result.append(separator);
      }
    }

    if (result.length() == 0 && prefix != null)
    {
      result.append(prefix);
    }

    return leadingSeparators != null ? "_" + result.toString() : result.toString(); //$NON-NLS-1$
  }

  /**
   * This method breaks sourceName into words delimited by separator and/or mixed-case naming.
   */
  public static List<String> parseName(String sourceName, char separator)
  {
    List<String> result = new ArrayList<String>();
    if (sourceName != null)
    {
      StringBuilder currentWord = new StringBuilder();
      boolean lastIsLower = false;
      for (int index = 0, length = sourceName.length(); index < length; ++index)
      {
        char curChar = sourceName.charAt(index);
        if (Character.isUpperCase(curChar) || !lastIsLower && Character.isDigit(curChar) || curChar == separator)
        {
          if (lastIsLower && currentWord.length() > 1 || curChar == separator && currentWord.length() > 0)
          {
            result.add(currentWord.toString());
            currentWord = new StringBuilder();
          }

          lastIsLower = false;
        }
        else
        {
          if (!lastIsLower)
          {
            int currentWordLength = currentWord.length();
            if (currentWordLength > 1)
            {
              char lastChar = currentWord.charAt(--currentWordLength);
              currentWord.setLength(currentWordLength);
              result.add(currentWord.toString());
              currentWord = new StringBuilder();
              currentWord.append(lastChar);
            }
          }

          lastIsLower = true;
        }

        if (curChar != separator)
        {
          currentWord.append(curChar);
        }
      }

      result.add(currentWord.toString());
    }

    return result;
  }

  private static String getLeadingSeparators(String name, char separator)
  {
    int i = 0;
    for (int len = name.length(); i < len && name.charAt(i) == separator; i++)
    {
      // the for loop's condition finds the separator
    }

    return i != 0 ? name.substring(0, i) : null;
  }
}
