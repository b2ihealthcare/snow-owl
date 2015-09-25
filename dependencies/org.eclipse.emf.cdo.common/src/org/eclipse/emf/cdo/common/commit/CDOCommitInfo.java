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
package org.eclipse.emf.cdo.common.commit;

import org.eclipse.emf.cdo.common.branch.CDOBranchPoint;

/**
 * {@link CDOCommitData Commit data} in the context of a {@link CDOCommitInfoManager commit info manager} with
 * additional commit informations.
 * 
 * @author Eike Stepper
 * @since 3.0
 * @noextend This interface is not intended to be extended by clients.
 * @noimplement This interface is not intended to be implemented by clients.
 * @apiviz.landmark
 */
public interface CDOCommitInfo extends CDOBranchPoint, CDOCommitData
{
  public CDOCommitInfoManager getCommitInfoManager();

  /**
   * @since 4.0
   */
  public long getPreviousTimeStamp();

  public String getUserID();

  public String getComment();
}
