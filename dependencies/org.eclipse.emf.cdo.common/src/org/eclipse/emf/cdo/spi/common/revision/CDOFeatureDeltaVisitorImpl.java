/*
 * Copyright (c) 2004 - 2012 Eike Stepper (Berlin, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Simon McDuff - initial API and implementation
 *    Eike Stepper - maintenance
 */
package org.eclipse.emf.cdo.spi.common.revision;

import org.eclipse.emf.cdo.common.revision.delta.CDOAddFeatureDelta;
import org.eclipse.emf.cdo.common.revision.delta.CDOClearFeatureDelta;
import org.eclipse.emf.cdo.common.revision.delta.CDOContainerFeatureDelta;
import org.eclipse.emf.cdo.common.revision.delta.CDOFeatureDelta;
import org.eclipse.emf.cdo.common.revision.delta.CDOFeatureDeltaVisitor;
import org.eclipse.emf.cdo.common.revision.delta.CDOListFeatureDelta;
import org.eclipse.emf.cdo.common.revision.delta.CDOMoveFeatureDelta;
import org.eclipse.emf.cdo.common.revision.delta.CDORemoveFeatureDelta;
import org.eclipse.emf.cdo.common.revision.delta.CDOSetFeatureDelta;
import org.eclipse.emf.cdo.common.revision.delta.CDOUnsetFeatureDelta;

/**
 * @author Simon McDuff
 * @since 3.0
 */
public class CDOFeatureDeltaVisitorImpl implements CDOFeatureDeltaVisitor
{
  public CDOFeatureDeltaVisitorImpl()
  {
  }

  public void visit(CDOAddFeatureDelta delta)
  {
  }

  public void visit(CDOClearFeatureDelta delta)
  {
  }

  public void visit(CDOContainerFeatureDelta delta)
  {
  }

  public void visit(CDOListFeatureDelta deltas)
  {
    for (CDOFeatureDelta delta : deltas.getListChanges())
    {
      try
      {
        delta.accept(this);
      }
      catch (StopVisitException ex)
      {
        return;
      }
    }
  }

  public void visit(CDOMoveFeatureDelta delta)
  {
  }

  public void visit(CDORemoveFeatureDelta delta)
  {
  }

  public void visit(CDOSetFeatureDelta delta)
  {
  }

  public void visit(CDOUnsetFeatureDelta delta)
  {
  }

  /**
   * @since 4.0
   */
  protected void stopVisit()
  {
    throw new StopVisitException();
  }

  /**
   * @author Eike Stepper
   * @since 4.0
   */
  private static final class StopVisitException extends RuntimeException
  {
    private static final long serialVersionUID = 1L;

    public StopVisitException()
    {
    }
  }
}
