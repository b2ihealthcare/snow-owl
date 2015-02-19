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
package org.eclipse.net4j.util.om.pref;

import org.eclipse.net4j.util.event.IEvent;

/**
 * An {@link IEvent event} fired from a {@link OMPreferences preferences} object when a {@link OMPreference preference}
 * value has changed.
 * 
 * @author Eike Stepper
 * @noextend This interface is not intended to be extended by clients.
 * @noimplement This interface is not intended to be implemented by clients.
 */
public interface OMPreferencesChangeEvent<T> extends IEvent
{
  /**
   * @since 3.0
   */
  public OMPreferences getSource();

  public OMPreference<T> getPreference();

  public T getOldValue();

  public T getNewValue();
}
