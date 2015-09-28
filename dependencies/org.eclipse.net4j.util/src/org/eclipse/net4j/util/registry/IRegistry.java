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
package org.eclipse.net4j.util.registry;

import org.eclipse.net4j.util.container.IContainer;

import java.util.Map;

/**
 * @author Eike Stepper
 */
public interface IRegistry<K, V> extends Map<K, V>, IContainer<Map.Entry<K, V>>
{
  public boolean isAutoCommit();

  public void setAutoCommit(boolean on);

  public void commit(boolean notifications);

  public void commit();
}
