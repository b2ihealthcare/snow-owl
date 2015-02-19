/*
 * Copyright (c) 2004 - 2012 Eike Stepper (Berlin, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Victor Roldan Betancort - initial API and implementation
 *    Eike Stepper - maintenance
 */
package org.eclipse.emf.cdo.view;

import org.eclipse.net4j.util.ReflectUtil.ExcludeFromDump;

import org.eclipse.emf.common.util.URI;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Base logic to handle CDOViewProvider priority and regular expression.
 * 
 * 
 * @author Victor Roldan Betancort
 * @since 2.0
 * @apiviz.exclude
 */
public abstract class AbstractCDOViewProvider implements CDOViewProvider
{
  private String regex;

  private int priority = DEFAULT_PRIORITY;

  @ExcludeFromDump
  private transient Pattern pattern;

  public AbstractCDOViewProvider()
  {
  }

  public AbstractCDOViewProvider(String regex, int priority)
  {
    this.regex = regex;
    this.priority = priority;
  }

  public AbstractCDOViewProvider(String regex)
  {
    this(regex, DEFAULT_PRIORITY);
  }

  public int getPriority()
  {
    return priority;
  }

  public void setPriority(int priority)
  {
    this.priority = priority;
  }

  public String getRegex()
  {
    return regex;
  }

  public void setRegex(String regex)
  {
    synchronized (regex)
    {
      this.regex = regex;
      pattern = null;
    }
  }

  public boolean matchesRegex(URI uri)
  {
    synchronized (regex)
    {
      if (pattern == null)
      {
        pattern = Pattern.compile(regex);
      }
    }

    Matcher matcher = pattern.matcher(uri.toString());
    return matcher.matches();
  }

  /**
   * Must be overwritten for non-canonical URI formats!
   * 
   * @since 4.0
   */
  public URI getResourceURI(CDOView view, String path)
  {
    return null;
  }
}
