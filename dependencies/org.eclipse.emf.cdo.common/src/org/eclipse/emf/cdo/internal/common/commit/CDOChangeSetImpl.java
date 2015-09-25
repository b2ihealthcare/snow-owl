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
package org.eclipse.emf.cdo.internal.common.commit;

import org.eclipse.emf.cdo.common.branch.CDOBranchPoint;
import org.eclipse.emf.cdo.common.commit.CDOChangeSet;
import org.eclipse.emf.cdo.common.commit.CDOChangeSetData;
import org.eclipse.emf.cdo.spi.common.branch.CDOBranchUtil;

/**
 * @author Eike Stepper
 */
public class CDOChangeSetImpl extends CDOChangeSetDataImpl implements CDOChangeSet
{
  private CDOBranchPoint startPoint;

  private CDOBranchPoint endPoint;

  public CDOChangeSetImpl(CDOBranchPoint startPoint, CDOBranchPoint endPoint, CDOChangeSetData data)
  {
    super(data.getNewObjects(), data.getChangedObjects(), data.getDetachedObjects());
    this.startPoint = startPoint;
    this.endPoint = endPoint;
  }

  public CDOBranchPoint getStartPoint()
  {
    return startPoint;
  }

  public CDOBranchPoint getEndPoint()
  {
    return endPoint;
  }

  public CDOBranchPoint getAncestorPoint()
  {
    return CDOBranchUtil.getAncestor(startPoint, endPoint);
  }
}
