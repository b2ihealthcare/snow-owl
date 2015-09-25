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
package org.eclipse.emf.cdo.common.revision;

import org.eclipse.emf.cdo.common.branch.CDOBranchPoint;
import org.eclipse.emf.cdo.common.id.CDOID;

/**
 * Provides consumers with the {@link CDORevision revisions} of {@link CDOID identifiable} CDO objects by selecting a
 * particular one from several possible {@link CDOBranchPoint branch points}.
 * 
 * @author Eike Stepper
 * @since 3.0
 * @apiviz.uses {@link CDORevision} - - provides
 */
public interface CDORevisionProvider
{
  public CDORevision getRevision(CDOID id);
}
