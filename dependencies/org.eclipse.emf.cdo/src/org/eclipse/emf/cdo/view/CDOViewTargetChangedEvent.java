/*
 * Copyright (c) 2004 - 2012 Eike Stepper (Berlin, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Victor Roldan Betancort - initial API and implementation
 *    Eike Stepper - maintenance
 */
package org.eclipse.emf.cdo.view;

import org.eclipse.emf.cdo.common.branch.CDOBranchPoint;

/**
 * A {@link CDOViewEvent view event} fired when the branch point of a {@link CDOView view} has changed.
 * 
 * @author Victor Roldan Betancort
 * @since 3.0
 * @noextend This interface is not intended to be extended by clients.
 * @noimplement This interface is not intended to be implemented by clients.
 */
public interface CDOViewTargetChangedEvent extends CDOViewEvent
{
  public CDOBranchPoint getBranchPoint();
}
