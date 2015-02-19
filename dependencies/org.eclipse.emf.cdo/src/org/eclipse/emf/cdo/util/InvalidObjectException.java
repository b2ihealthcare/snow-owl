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
import org.eclipse.emf.cdo.transaction.CDOTransaction;

import org.eclipse.emf.internal.cdo.messages.Messages;

import java.text.MessageFormat;

/**
 * Exception occurs when an object isn't valid anymore. It was valid when we create it, but not anymore. The cause could
 * be that another {@link CDOTransaction} removed it.
 * 
 * @author Simon McDuff
 * @since 2.0
 * @noextend This interface is not intended to be extended by clients.
 * @noinstantiate This class is not intended to be instantiated by clients.
 */
public class InvalidObjectException extends CDOException
{
  private static final long serialVersionUID = 1L;

  /**
   * @since 3.0
   */
  public InvalidObjectException(CDOID id, CDOBranchPoint branchPoint)
  {
    super(MessageFormat.format(Messages.getString("InvalidObjectException.0"), //
        id, branchPoint.getBranch().getID(), CDOCommonUtil.formatTimeStamp(branchPoint.getTimeStamp())));
  }
}
