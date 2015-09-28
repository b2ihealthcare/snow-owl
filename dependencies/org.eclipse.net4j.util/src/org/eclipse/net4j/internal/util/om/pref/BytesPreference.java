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

import org.eclipse.net4j.util.HexUtil;

/**
 * @author Eike Stepper
 */
public final class BytesPreference extends Preference<byte[]>
{
  public BytesPreference(Preferences preferences, String name, byte[] defaultValue)
  {
    super(preferences, name, defaultValue);
  }

  @Override
  protected String getString()
  {
    return HexUtil.bytesToHex(getValue());
  }

  @Override
  protected byte[] convert(String value)
  {
    return HexUtil.hexToBytes(value);
  }

  public Type getType()
  {
    return Type.BYTES;
  }
}
