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

import org.eclipse.emf.cdo.common.commit.CDOCommitData;
import org.eclipse.emf.cdo.common.util.CDOException;
import org.eclipse.emf.cdo.transaction.CDOTransaction;

import org.eclipse.emf.internal.cdo.messages.Messages;

import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.resource.Resource;

import java.text.MessageFormat;

/**
 * An unchecked exception being thrown from {@link CDOTransaction#commit()} if the commit {@link CDOCommitData change
 * set} is referencing {@link EObject objects} that are not contained by any {@link Resource resource} before the server
 * is contacted.
 * 
 * @author Simon McDuff
 * @since 2.0
 * @noextend This interface is not intended to be extended by clients.
 * @noinstantiate This class is not intended to be instantiated by clients.
 */
public class DanglingReferenceException extends CDOException
{
  private static final long serialVersionUID = 1L;

  private EObject target;

  public DanglingReferenceException(EObject object)
  {
    super(MessageFormat.format(Messages.getString("DanglingReferenceException.0"), object, object.getClass().getName())); //$NON-NLS-1$
    target = object;
  }

  public EObject getTarget()
  {
    return target;
  }
}
