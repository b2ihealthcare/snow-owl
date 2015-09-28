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

import java.util.List;
import java.util.Map;

import org.eclipse.emf.cdo.common.branch.CDOBranch;

/**
 * Provides consumers with all {@link CDORevision revisions} available in an instance of this interface.
 * 
 * @author Eike Stepper
 * @since 3.0
 * @apiviz.exclude
 */
public interface CDOAllRevisionsProvider
{
  public Map<CDOBranch, List<CDORevision>> getAllRevisions();
}
