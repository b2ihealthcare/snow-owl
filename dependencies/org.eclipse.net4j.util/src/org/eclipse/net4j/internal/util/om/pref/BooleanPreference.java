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

/**
 * @author Eike Stepper
 */
public final class BooleanPreference extends Preference<Boolean>
{
  public BooleanPreference(Preferences preferences, String name, Boolean defaultValue)
  {
    super(preferences, name, defaultValue);
  }

  @Override
  protected String getString()
  {
    return Boolean.toString(getValue());
  }

  @Override
  protected Boolean convert(String value)
  {
    return Boolean.parseBoolean(value);
  }

  public Type getType()
  {
    return Type.BOOLEAN;
  }
}
