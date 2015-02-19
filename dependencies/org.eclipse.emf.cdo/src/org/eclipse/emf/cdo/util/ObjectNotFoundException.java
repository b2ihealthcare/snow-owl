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
package org.eclipse.emf.cdo.util;

import org.eclipse.emf.cdo.common.branch.CDOBranchPoint;
import org.eclipse.emf.cdo.common.id.CDOID;
import org.eclipse.emf.cdo.common.util.CDOCommonUtil;
import org.eclipse.emf.cdo.common.util.CDOException;

import org.eclipse.emf.internal.cdo.messages.Messages;

import java.text.MessageFormat;

/**
 * Exception occurs when an id doesn't exist on the server.
 * 
 * @author Simon McDuff
 * @since 2.0
 * @noextend This interface is not intended to be extended by clients.
 * @noinstantiate This class is not intended to be instantiated by clients.
 */
public class ObjectNotFoundException extends CDOException
{
  private static final long serialVersionUID = 1L;

  private CDOID id;

  private CDOBranchPoint branchPoint;

  public ObjectNotFoundException(CDOID id)
  {
    super(MessageFormat.format(Messages.getString("ObjectNotFoundException.0"), id)); //$NON-NLS-1$
    this.id = id;
  }

  /**
   * @since 3.0
   */
  public ObjectNotFoundException(CDOID id, CDOBranchPoint branchPoint)
  {
    super(MessageFormat.format(Messages.getString("ObjectNotFoundException.1"), //$NON-NLS-1$
        id, branchPoint.getBranch().getID(), CDOCommonUtil.formatTimeStamp(branchPoint.getTimeStamp())));
    this.id = id;
    this.branchPoint = branchPoint;
  }

  /**
   * @since 3.0
   */
  public CDOID getID()
  {
    return id;
  }

  /**
   * @since 3.0
   */
  public CDOBranchPoint getBranchPoint()
  {
    return branchPoint;
  }
}
