/*
 * Copyright (c) 2004 - 2012 Eike Stepper (Berlin, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 *    Simon McDuff - bug 213402
 */
package org.eclipse.emf.cdo.eresource;

import org.eclipse.emf.cdo.eresource.impl.CDOResourceFactoryImpl;
import org.eclipse.emf.cdo.eresource.impl.CDOResourceImpl;
import org.eclipse.emf.cdo.util.CDOURIData;
import org.eclipse.emf.cdo.util.CDOURIUtil;

import org.eclipse.emf.ecore.resource.Resource;

/**
 * Creates {@link CDOResource} instances.
 * <p>
 * Note that the only important task of this factory is to instantiate a {@link CDOResourceImpl} and mark it
 * {@link CDOResource#isExisting() existing} or not. All further {@link CDOResource#getURI() URI} processing
 * is done later on in the registration process, especially in {@link CDOResourceImpl#basicSetResourceSet(org.eclipse.emf.ecore.resource.ResourceSet, org.eclipse.emf.common.notify.NotificationChain) CDOResourceImpl.basicSetResourceSet()}.
 * <p>
 * The recognized URI formats are explained in {@link CDOURIUtil} and {@link CDOURIData}.
 *
 * @author Eike Stepper
 * @noextend This interface is not intended to be extended by clients.
 * @noimplement This interface is not intended to be implemented by clients.
 * @apiviz.uses {@link CDOResource} - - creates
 */
public interface CDOResourceFactory extends Resource.Factory
{
  /**
   * @since 4.0
   */
  public static final CDOResourceFactory INSTANCE = CDOResourceFactoryImpl.INSTANCE;
}
