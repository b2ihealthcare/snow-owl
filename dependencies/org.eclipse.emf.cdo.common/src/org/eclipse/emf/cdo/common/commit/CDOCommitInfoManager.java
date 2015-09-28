/*
 * Copyright (c) 2004 - 2012 Eike Stepper (Berlin, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Andre Dietisheim - initial API and implementation
 */
package org.eclipse.emf.cdo.common.commit;

import org.eclipse.emf.cdo.common.branch.CDOBranch;

/**
 * Loads, provides and possible manages {@link CDOCommitInfo commit info} objects.
 * 
 * @author Andre Dietisheim
 * @since 3.0
 * @noextend This interface is not intended to be extended by clients.
 * @noimplement This interface is not intended to be implemented by clients.
 * @apiviz.landmark
 * @apiviz.uses {@link CDOCommitInfo} - - manages
 */
public interface CDOCommitInfoManager
{
  /**
   * @since 4.0
   */
  public CDOCommitInfo getCommitInfo(long timeStamp);

  public void getCommitInfos(CDOBranch branch, long startTime, long endTime, CDOCommitInfoHandler handler);

  /**
   * @since 4.0
   */
  public void getCommitInfos(CDOBranch branch, long startTime, String userID, String comment, int count,
      CDOCommitInfoHandler handler);
}
