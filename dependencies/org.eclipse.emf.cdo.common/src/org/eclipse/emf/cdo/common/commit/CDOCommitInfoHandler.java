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

/**
 * A call-back interface that indicates the ability to <i>handle</i> {@link CDOCommitInfo commit infos} that are passed
 * from other entities.
 * 
 * @author Eike Stepper
 * @since 3.0
 * @apiviz.uses {@link CDOCommitInfo} - - handles
 */
public interface CDOCommitInfoHandler
{
  public void handleCommitInfo(CDOCommitInfo commitInfo);
}
