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
package org.eclipse.net4j.util.collection;

import org.eclipse.net4j.util.om.pref.OMPreference;

/**
 * @author Eike Stepper
 */
public final class HistoryUtil
{
  private HistoryUtil()
  {
  }

  public static IHistory<String> createHistory()
  {
    return new History<String>();
  }

  public static IHistory<String> createPreferenceHistory(OMPreference<String[]> preference)
  {
    return new PreferenceHistory(preference);
  }
}
