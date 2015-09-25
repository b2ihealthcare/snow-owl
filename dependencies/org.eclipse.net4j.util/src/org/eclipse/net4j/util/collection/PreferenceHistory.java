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

import org.eclipse.net4j.util.CheckUtil;
import org.eclipse.net4j.util.om.pref.OMPreference;

/**
 * @author Eike Stepper
 */
public class PreferenceHistory extends History<String>
{
  private OMPreference<String[]> preference;

  public PreferenceHistory(OMPreference<String[]> preference)
  {
    CheckUtil.checkArg(preference, "preference");
    this.preference = preference;
  }

  public OMPreference<String[]> getPreference()
  {
    return preference;
  }

  @Override
  protected void load()
  {
    String[] value = preference.getValue();
    if (value != null)
    {
      for (String data : value)
      {
        IHistoryElement<String> element = createElement(data);
        elements.add(element);
      }
    }
  }

  @Override
  protected void save()
  {
    String[] array = getData(new String[size()]);
    preference.setValue(array);
  }
}
