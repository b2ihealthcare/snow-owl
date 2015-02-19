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
package org.eclipse.net4j.util.container.delegate;

import org.eclipse.net4j.util.container.IContainer;

import java.util.Map;

/**
 * A {@link IContainer container} (of {@link java.util.Map.Entry map entries}) that is a {@link Map}.
 * 
 * @author Eike Stepper
 */
public interface IContainerMap<K, V> extends IContainer<Map.Entry<K, V>>, Map<K, V>
{
  public Map<K, V> getDelegate();
}
