/*
 * Copyright (c) 2004 - 2012 Eike Stepper (Berlin, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Kai Schlamp - initial API and implementation
 *    Eike Stepper - maintenance
 *    Kai Schlamp - Bug 284680 - [DB] Provide annotation to bypass ClassMapping
 *    Stefan Winkler - maintenance
 *    Stefan Winkler - Bug 285426: [DB] Implement user-defined typeMapping support
 */
package org.eclipse.emf.cdo.server.internal.db;

import org.eclipse.emf.ecore.EModelElement;
import org.eclipse.emf.ecore.util.EcoreUtil;

/**
 * @author Kai Schlamp
 */
public enum DBAnnotation
{
  TABLE_MAPPING("tableMapping"), //
  TABLE_NAME("tableName"), //
  COLUMN_NAME("columnName"), //
  COLUMN_TYPE("columnType"), //
  COLUMN_LENGTH("columnLength"), //
  TYPE_MAPPING("typeMapping");

  public final static String SOURCE_URI = "http://www.eclipse.org/CDO/DBStore";

  public final static String TABLE_MAPPING_NONE = "NONE";

  private String keyword;

  private DBAnnotation(String keyword)
  {
    this.keyword = keyword;
  }

  public String getKeyword()
  {
    return keyword == null ? super.toString() : keyword;
  }

  /**
   * @return A non-empty string or <code>null</code>.
   */
  public String getValue(EModelElement element)
  {
    String value = EcoreUtil.getAnnotation(element, SOURCE_URI, keyword);
    if (value != null && value.length() == 0)
    {
      return null;
    }

    return value;
  }

  @Override
  public String toString()
  {
    return getKeyword();
  }
}
