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
package org.eclipse.emf.cdo.view;

import org.eclipse.emf.cdo.CDOObject;
import org.eclipse.emf.cdo.CDOState;

/**
 * Call-back handler used by {@link CDOView views} to tell implementors of this interface about {@link CDOState state}
 * changes of {@link CDOObject objects}.
 * 
 * @author Eike Stepper
 * @since 3.0
 * @see CDOView#addObjectHandler(CDOObjectHandler)
 * @see CDOView#removeObjectHandler(CDOObjectHandler)
 * @see CDOObject#cdoState()
 */
public interface CDOObjectHandler
{
  public void objectStateChanged(CDOView view, CDOObject object, CDOState oldState, CDOState newState);
}
