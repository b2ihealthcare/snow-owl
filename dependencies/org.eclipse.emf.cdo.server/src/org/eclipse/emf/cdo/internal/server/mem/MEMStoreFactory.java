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
package org.eclipse.emf.cdo.internal.server.mem;

import org.eclipse.emf.cdo.server.IStore;
import org.eclipse.emf.cdo.server.IStoreFactory;

import org.w3c.dom.Element;

import java.util.Map;

/**
 * @author Simon McDuff
 */
public class MEMStoreFactory implements IStoreFactory
{
  public MEMStoreFactory()
  {
  }

  public String getStoreType()
  {
    return MEMStore.TYPE;
  }

  public IStore createStore(String repositoryName, Map<String, String> repositoryProperties, Element storeConfig)
  {
    return new MEMStore();
  }
}
