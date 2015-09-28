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
public final class LongPreference extends Preference<Long>
{
  public LongPreference(Preferences preferences, String name, Long defaultValue)
  {
    super(preferences, name, defaultValue);
  }

  @Override
  protected String getString()
  {
    return Long.toString(getValue());
  }

  @Override
  protected Long convert(String value)
  {
    return Long.parseLong(value);
  }

  public Type getType()
  {
    return Type.LONG;
  }
}
