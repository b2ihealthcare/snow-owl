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
package org.eclipse.emf.cdo.common.revision.delta;

import org.eclipse.emf.cdo.common.id.CDOID;

/**
 * Provides consumers with the {@link CDORevisionDelta revision deltas} of {@link CDOID identifiable} CDO objects.
 * 
 * @author Eike Stepper
 * @since 4.0
 * @apiviz.uses {@link CDORevisionDelta} - - provides
 */
public interface CDORevisionDeltaProvider
{
  public CDORevisionDelta getRevisionDelta(CDOID id);
}
