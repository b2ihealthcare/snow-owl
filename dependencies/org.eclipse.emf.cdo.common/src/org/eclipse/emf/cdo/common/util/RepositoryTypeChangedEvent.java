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
package org.eclipse.emf.cdo.common.util;

import org.eclipse.emf.cdo.common.CDOCommonRepository;
import org.eclipse.net4j.util.event.Event;
import org.eclipse.net4j.util.event.IEvent;
import org.eclipse.net4j.util.event.INotifier;

/**
 * An {@link IEvent event} fired when the {@link org.eclipse.emf.cdo.common.CDOCommonRepository.Type type} of a
 * repository has changed.
 * 
 * @author Eike Stepper
 * @since 3.0
 * @noextend This interface is not intended to be extended by clients.
 */
public class RepositoryTypeChangedEvent extends Event implements CDOCommonRepository.TypeChangedEvent
{
  private static final long serialVersionUID = 1L;

  private CDOCommonRepository.Type oldType;

  private CDOCommonRepository.Type newType;

  public RepositoryTypeChangedEvent(INotifier source, CDOCommonRepository.Type oldType, CDOCommonRepository.Type newType)
  {
    super(source);
    this.oldType = oldType;
    this.newType = newType;
  }

  public CDOCommonRepository.Type getOldType()
  {
    return oldType;
  }

  public CDOCommonRepository.Type getNewType()
  {
    return newType;
  }
}
