/*
 * Copyright (c) 2004 - 2012 Eike Stepper (Berlin, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 *    Stefan Winkler - bug 259402
 *    Stefan Winkler - redesign (prepared statements)
 *    Stefan Winkler - bug 276926
 */
package org.eclipse.emf.cdo.server.internal.db.mapping.horizontal;

import org.eclipse.emf.cdo.server.db.IMetaDataManager;
import org.eclipse.emf.cdo.server.db.mapping.IMappingStrategy;
import org.eclipse.emf.cdo.server.internal.db.IObjectTypeMapper;

import org.eclipse.net4j.util.lifecycle.Lifecycle;

/**
 * @author Eike Stepper
 * @since 4.0
 */
public abstract class AbstractObjectTypeMapper extends Lifecycle implements IObjectTypeMapper
{
  private IMappingStrategy mappingStrategy;

  private IMetaDataManager metaDataManager;

  public AbstractObjectTypeMapper()
  {
  }

  public IMappingStrategy getMappingStrategy()
  {
    return mappingStrategy;
  }

  public void setMappingStrategy(IMappingStrategy mappingStrategy)
  {
    this.mappingStrategy = mappingStrategy;
  }

  public IMetaDataManager getMetaDataManager()
  {
    return metaDataManager;
  }

  @Override
  protected void doBeforeActivate() throws Exception
  {
    super.doBeforeActivate();
    checkState(mappingStrategy, "mappingStrategy"); //$NON-NLS-1$
  }

  @Override
  protected void doActivate() throws Exception
  {
    metaDataManager = getMappingStrategy().getStore().getMetaDataManager();
  }

  @Override
  protected void doDeactivate() throws Exception
  {
    metaDataManager = null;
  }
}
