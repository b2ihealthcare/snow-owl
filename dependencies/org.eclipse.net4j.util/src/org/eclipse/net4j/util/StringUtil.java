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

import org.eclipse.net4j.util.om.OMPlatform;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

/**
 * Various static helper methods for dealing with strings.
 * 
 * @author Eike Stepper
 */
public final class StringUtil
{
  public static final String EMPTY = ""; //$NON-NLS-1$

  public static final String NL = OMPlatform.INSTANCE.getProperty("line.separator"); //$NON-NLS-1$

  private StringUtil()
  {
  }

  /**
   * @since 2.0
   */
  public static String formatException(Throwable t)
  {
    ByteArrayOutputStream baos = new ByteArrayOutputStream();
    PrintStream s = new PrintStream(baos);
    t.printStackTrace(s);
    return baos.toString();
  }

  public static String replace(String text, String[] find, String[] replace)
  {
    for (int i = 0; i < find.length; i++)
    {
      int end = 0;
      for (;;)
      {
        int start = text.indexOf(find[i], end);
        if (start == -1)
        {
          break;
        }

        end = start + find[i].length();
        text = text.substring(0, start) + replace[i] + text.substring(end);
      }
    }

    return text;
  }

  public static String safe(String str)
  {
    if (str == null)
    {
      return EMPTY;
    }

    return str;
  }

  public static int compare(String s1, String s2)
  {
    if (s1 == null)
    {
      return s2 == null ? 0 : -1;
    }

    if (s2 == null)
    {
      return 1;
    }

    return s1.compareTo(s2);
  }

  /**
   * @since 3.1
   */
  public static boolean equalsUpperOrLowerCase(String s, String upperOrLowerCase)
  {
    if (s == null)
    {
      return upperOrLowerCase == null;
    }

    return s.equals(upperOrLowerCase.toLowerCase()) || s.equals(upperOrLowerCase.toUpperCase());
  }

  /**
   * @since 2.0
   */
  public static String capAll(String str)
  {
    if (str == null || str.length() == 0)
    {
      return str;
    }

    boolean inWhiteSpace = true;
    StringBuilder builder = new StringBuilder(str);
    for (int i = 0; i < builder.length(); i++)
    {
      char c = builder.charAt(i);
      boolean isWhiteSpace = Character.isWhitespace(c);
      if (!isWhiteSpace && inWhiteSpace)
      {
        builder.setCharAt(i, Character.toUpperCase(c));
      }

      inWhiteSpace = isWhiteSpace;
    }

    return builder.toString();
  }

  /**
   * @since 2.0
   */
  public static String uncapAll(String str)
  {
    if (str == null || str.length() == 0)
    {
      return str;
    }

    boolean inWhiteSpace = true;
    StringBuilder builder = new StringBuilder(str);
    for (int i = 0; i < builder.length(); i++)
    {
      char c = builder.charAt(i);
      boolean isWhiteSpace = Character.isWhitespace(c);
      if (!isWhiteSpace && inWhiteSpace)
      {
        builder.setCharAt(i, Character.toLowerCase(c));
      }

      inWhiteSpace = isWhiteSpace;
    }

    return builder.toString();
  }

  public static String cap(String str)
  {
    if (str == null || str.length() == 0)
    {
      return str;
    }

    char first = str.charAt(0);
    if (Character.isUpperCase(first))
    {
      return str;
    }

    if (str.length() == 1)
    {
      return str.toUpperCase();
    }

    StringBuilder builder = new StringBuilder(str);
    builder.setCharAt(0, Character.toUpperCase(first));
    return builder.toString();
  }

  public static String uncap(String str)
  {
    if (str == null || str.length() == 0)
    {
      return str;
    }

    char first = str.charAt(0);
    if (Character.isLowerCase(first))
    {
      return str;
    }

    if (str.length() == 1)
    {
      return str.toLowerCase();
    }

    StringBuilder builder = new StringBuilder(str);
    builder.setCharAt(0, Character.toLowerCase(first));
    return builder.toString();
  }

  public static int occurrences(String str, char c)
  {
    int count = 0;
    for (int i = 0; (i = str.indexOf(c, i)) != -1; ++i)
    {
      ++count;
    }

    return count;
  }

  public static int occurrences(String str, String c)
  {
    int count = 0;
    for (int i = 0; (i = str.indexOf(c, i)) != -1; i += c.length())
    {
      ++count;
    }

    return count;
  }

  public static boolean isEmpty(String str)
  {
    return ObjectUtil.isEmpty(str);
  }

  /**
   * Matches a string against a pattern.
   * <p>
   * Pattern description:
   * <ul>
   * <li><code>*</code> matches 0 or more characters
   * <li><code>?</code> matches a single character
   * <li><code>[...]</code> matches a set and/or range of characters
   * <li><code>\</code> escapes the following character
   * </ul>
   * 
   * @since 2.0
   */
  public static boolean glob(String pattern, String string)
  {
    return glob(pattern, string, null);
  }

  /**
   * Matches a string against a pattern and fills an array with the sub-matches.
   * <p>
   * Pattern description:
   * <ul>
   * <li><code>*</code> matches 0 or more characters
   * <li><code>?</code> matches a single character
   * <li><code>[...]</code> matches a set and/or range of characters
   * <li><code>\</code> escapes the following character
   * </ul>
   * 
   * @since 2.0
   */
  public static boolean glob(String pattern, String string, String[] subStrings)
  {
    return globRecurse(pattern, 0, string, 0, subStrings, 0);
  }

  private static boolean globRecurse(String pattern, int patternIndex, String string, int stringIndex,
      String[] subStrings, int subStringsIndex)
  {
    int patternLength = pattern.length();
    int stringLength = string.length();

    for (;;)
    {
      char patternChar = pattern.charAt(patternIndex);
      boolean endReached = stringIndex == stringLength;
      if (patternIndex == patternLength)
      {
        return endReached;
      }
      else if (endReached && patternChar != '*')
      {
        return false;
      }

      switch (patternChar)
      {
      case '*':
      {
        int startIndex = stringIndex;
        if (++patternIndex >= patternLength)
        {
          globRemember(string, startIndex, stringLength, subStrings, subStringsIndex);
          return true;
        }

        for (;;)
        {
          if (globRecurse(pattern, patternIndex, string, stringIndex, subStrings, subStringsIndex + 1))
          {
            globRemember(string, startIndex, stringIndex, subStrings, subStringsIndex);
            return true;
          }

          if (endReached)
          {
            return false;
          }

          ++stringIndex;
        }
      }

      case '?':
        ++patternIndex;
        globRemember(string, stringIndex, ++stringIndex, subStrings, subStringsIndex++);
        break;

      case '[':
        try
        {
          ++patternIndex;
          char stringChar = string.charAt(stringIndex);
          char rangeStartChar = patternChar;

          while (true)
          {
            if (rangeStartChar == ']')
            {
              return false;
            }

            if (rangeStartChar == stringChar)
            {
              break;
            }

            ++patternIndex;
            char nextPatternChar = patternChar;
            if (nextPatternChar == '-')
            {
              ++patternIndex;
              char rangeEndChar = patternChar;
              if (rangeStartChar <= stringChar && stringChar <= rangeEndChar)
              {
                break;
              }

              ++patternIndex;
              nextPatternChar = patternChar;
            }

            rangeStartChar = nextPatternChar;
          }

          patternIndex = pattern.indexOf(']', patternIndex) + 1;
          if (patternIndex <= 0)
          {
            return false;
          }

          globRemember(string, stringIndex, ++stringIndex, subStrings, subStringsIndex++);
        }
        catch (StringIndexOutOfBoundsException ex)
        {
          return false;
        }

        break;

      case '\\':
        if (++patternIndex >= patternLength)
        {
          return false;
        }

        //$FALL-THROUGH$
      default:
        if (patternChar++ != string.charAt(stringIndex++))
        {
          return false;
        }
      }
    }
  }

  private static void globRemember(String string, int start, int end, String[] subStrings, int subStringsIndex)
  {
    if (subStrings != null && subStringsIndex < subStrings.length)
    {
      subStrings[subStringsIndex] = string.substring(start, end);
    }
  }
}
