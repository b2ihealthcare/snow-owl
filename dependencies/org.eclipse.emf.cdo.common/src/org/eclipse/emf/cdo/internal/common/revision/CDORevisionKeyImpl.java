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
package org.eclipse.emf.cdo.internal.common.revision;

import java.text.MessageFormat;

import org.eclipse.emf.cdo.common.branch.CDOBranch;
import org.eclipse.emf.cdo.common.id.CDOID;
import org.eclipse.emf.cdo.common.revision.CDORevisionKey;
import org.eclipse.emf.cdo.internal.common.branch.CDOBranchVersionImpl;

/**
 * @author Eike Stepper
 */
public class CDORevisionKeyImpl extends CDOBranchVersionImpl implements CDORevisionKey
{
  private CDOID id;

  public CDORevisionKeyImpl(CDOID id, CDOBranch branch, int version)
  {
    super(branch, version);
    this.id = id;
  }

  public CDOID getID()
  {
    return id;
  }

  @Override
  public int hashCode()
  {
    return id.hashCode() ^ super.hashCode();
  }

  @Override
  public boolean equals(Object obj)
  {
    if (obj == this)
    {
      return true;
    }

    if (obj instanceof CDORevisionKey)
    {
      CDORevisionKey that = (CDORevisionKey)obj;
      return id.equals(that.getID()) && getBranch().equals(that.getBranch()) && getVersion() == that.getVersion();
    }

    return false;
  }

  @Override
  public String toString()
  {
    return MessageFormat.format("{0}:{1}v{2}", id, getBranch().getID(), getVersion());
  }
}
