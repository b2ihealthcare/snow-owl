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
import org.eclipse.net4j.util.container.SetContainer;

import java.util.Set;

/**
 * A {@link IContainer container} that is a {@link Set}.
 *
 * @author Eike Stepper
 * @see SetContainer
 */
public interface IContainerSet<E> extends IContainerCollection<E>, Set<E>
{
  public Set<E> getDelegate();
}
