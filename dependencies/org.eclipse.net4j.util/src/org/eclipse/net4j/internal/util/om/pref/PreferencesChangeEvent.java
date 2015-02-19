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
package org.eclipse.net4j.internal.util.om.pref;

import org.eclipse.net4j.util.event.Event;
import org.eclipse.net4j.util.om.pref.OMPreferencesChangeEvent;

/**
 * @author Eike Stepper
 */
public final class PreferencesChangeEvent<T> extends Event implements OMPreferencesChangeEvent<T>
{
  private static final long serialVersionUID = 1L;

  private Preference<T> preference;

  private T oldValue;

  private T newValue;

  public PreferencesChangeEvent(Preference<T> preference, T oldValue, T newValue)
  {
    super(preference.getPreferences());
    this.preference = preference;
    this.oldValue = oldValue;
    this.newValue = newValue;
  }

  @Override
  public Preferences getSource()
  {
    return (Preferences)super.getSource();
  }

  public Preference<T> getPreference()
  {
    return preference;
  }

  public T getOldValue()
  {
    return oldValue;
  }

  public T getNewValue()
  {
    return newValue;
  }
}
