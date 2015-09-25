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
import org.eclipse.emf.cdo.common.revision.CDORevisable;
import org.eclipse.emf.cdo.common.util.CDOCommonUtil;
import org.eclipse.emf.cdo.internal.common.branch.CDOBranchVersionImpl;
import org.eclipse.net4j.util.ObjectUtil;

/**
 * @author Eike Stepper
 */
public class CDORevisableImpl extends CDOBranchVersionImpl implements CDORevisable
{
  private long timeStamp;

  private long revised;

  public CDORevisableImpl(CDOBranch branch, int version, long timeStamp, long revised)
  {
    super(branch, version);
    this.timeStamp = timeStamp;
    this.revised = revised;
  }

  public CDORevisableImpl(CDORevisable source)
  {
    super(source.getBranch(), source.getVersion());
    timeStamp = source.getTimeStamp();
    revised = source.getRevised();
  }

  public CDORevisableImpl(CDOBranch branch, int version)
  {
    super(branch, version);
  }

  public long getTimeStamp()
  {
    return timeStamp;
  }

  public long getRevised()
  {
    return revised;
  }

  @Override
  public int hashCode()
  {
    return ObjectUtil.hashCode(timeStamp) ^ ObjectUtil.hashCode(revised) ^ super.hashCode();
  }

  @Override
  public boolean equals(Object obj)
  {
    if (obj == this)
    {
      return true;
    }

    if (obj instanceof CDORevisable)
    {
      CDORevisable that = (CDORevisable)obj;
      return timeStamp == that.getTimeStamp() && revised == that.getRevised() && getBranch().equals(that.getBranch())
          && getVersion() == that.getVersion();
    }

    return false;
  }

  @Override
  public String toString()
  {
    return MessageFormat.format("{0}v{1}[{2}-{3}]", getBranch().getID(), getVersion(),
        CDOCommonUtil.formatTimeStamp(timeStamp), CDOCommonUtil.formatTimeStamp(revised));
  }
}
