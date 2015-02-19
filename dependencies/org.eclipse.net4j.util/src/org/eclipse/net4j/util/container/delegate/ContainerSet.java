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

import java.util.Set;

/**
 * A default implementation of a {@link IContainerSet container set}.
 * 
 * @author Eike Stepper
 * @apiviz.exclude
 */
public class ContainerSet<E> extends ContainerCollection<E> implements IContainerSet<E>
{
  public ContainerSet(Set<E> delegate)
  {
    super(delegate);
  }

  @Override
  public Set<E> getDelegate()
  {
    return (Set<E>)super.getDelegate();
  }
}
