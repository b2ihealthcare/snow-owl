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
package org.eclipse.emf.cdo.spi.server;

import org.eclipse.emf.cdo.common.id.CDOID;
import org.eclipse.emf.cdo.server.IRepository;
import org.eclipse.emf.cdo.server.IStore;

import org.eclipse.net4j.util.lifecycle.ILifecycle;

/**
 * @author Eike Stepper
 * @since 3.0
 */
public interface InternalStore extends IStore, ILifecycle
{
  public InternalRepository getRepository();

  public void setRepository(IRepository repository);

  public void setRevisionTemporality(RevisionTemporality revisionTemporality);

  public void setRevisionParallelism(RevisionParallelism revisionParallelism);

  public int getNextBranchID();

  public int getNextLocalBranchID();

  public void setLastBranchID(int lastBranchID);

  public void setLastLocalBranchID(int lastLocalBranchID);

  public void setLastCommitTime(long lastCommitTime);

  public void setLastNonLocalCommitTime(long lastNonLocalCommitTime);

  /**
   * @since 4.0
   */
  public boolean isLocal(CDOID id);

  /**
   * @since 4.0
   */
  public boolean isDropAllDataOnActivate();

  /**
   * @since 4.0
   */
  public void setDropAllDataOnActivate(boolean dropAllDataOnActivate);

  /**
   * @since 4.0
   */
  public void setCreationTime(long creationTime);

  /**
   * @author Eike Stepper
   * @since 4.0
   */
  public interface NoExternalReferences
  {
  }

  /**
   * @author Eike Stepper
   * @since 4.0
   */
  public interface NoQueryXRefs
  {
  }

  /**
   * @author Eike Stepper
   * @since 4.0
   */
  public interface NoLargeObjects
  {
  }

  /**
   * @author Eike Stepper
   * @since 4.0
   */
  public interface NoFeatureMaps
  {
  }

  /**
   * @author Eike Stepper
   * @since 4.0
   */
  public interface NoHandleRevisions
  {
  }

  /**
   * @author Eike Stepper
   * @since 4.0
   */
  public interface NoRawAccess
  {
  }
}
