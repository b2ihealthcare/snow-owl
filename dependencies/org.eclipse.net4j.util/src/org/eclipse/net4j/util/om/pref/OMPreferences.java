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

import org.eclipse.net4j.util.event.INotifier;
import org.eclipse.net4j.util.om.OMBundle;

/**
 * @author Eike Stepper
 * @noextend This interface is not intended to be extended by clients.
 * @noimplement This interface is not intended to be implemented by clients.
 */
public interface OMPreferences extends INotifier
{
  public static final boolean DEFAULT_BOOLEAN = false;

  public static final int DEFAULT_INTEGER = 0;

  public static final long DEFAULT_LONG = 0L;

  public static final float DEFAULT_FLOAT = 0.0f;

  public static final double DEFAULT_DOUBLE = 0.0d;

  public static final String DEFAULT_STRING = ""; //$NON-NLS-1$

  public static final String[] DEFAULT_ARRAY = {};

  public static final byte[] DEFAULT_BYTES = {};

  public OMBundle getBundle();

  public boolean isDirty();

  public void save();

  public OMPreference<Boolean> init(String name, boolean defaultValue);

  public OMPreference<Integer> init(String name, int defaultValue);

  public OMPreference<Long> init(String name, long defaultValue);

  public OMPreference<Float> init(String name, float defaultValue);

  public OMPreference<Double> init(String name, double defaultValue);

  public OMPreference<String> init(String name, String defaultValue);

  public OMPreference<String[]> init(String name, String[] defaultValue);

  public OMPreference<byte[]> init(String name, byte[] defaultValue);

  public OMPreference<Boolean> initBoolean(String name);

  public OMPreference<Integer> initInteger(String name);

  public OMPreference<Long> initLong(String name);

  public OMPreference<Float> initFloat(String name);

  public OMPreference<Double> initDouble(String name);

  public OMPreference<String> initString(String name);

  public OMPreference<String[]> initArray(String name);

  public OMPreference<byte[]> initBytes(String name);

  public boolean contains(String name);

  public OMPreference<?> get(String name);

  public OMPreference<Boolean> getBoolean(String name);

  public OMPreference<Integer> getInteger(String name);

  public OMPreference<Long> getLong(String name);

  public OMPreference<Float> getFloat(String name);

  public OMPreference<Double> getDouble(String name);

  public OMPreference<String> getString(String name);

  public OMPreference<String[]> getArray(String name);

  public OMPreference<byte[]> getBytes(String name);
}
