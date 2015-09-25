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

/**
 * @author Eike Stepper
 * @noextend This interface is not intended to be extended by clients.
 * @noimplement This interface is not intended to be implemented by clients.
 */
public interface OMPreference<T>
{
  public OMPreferences getPreferences();

  public String getName();

  public Type getType();

  public T getDefaultValue();

  public T getValue();

  public T setValue(T value);

  public T unSet();

  public boolean isSet();

  /**
   * @author Eike Stepper
   */
  public enum Type
  {
    BOOLEAN, INTEGER, LONG, FLOAT, DOUBLE, STRING, ARRAY, BYTES
  }
}
