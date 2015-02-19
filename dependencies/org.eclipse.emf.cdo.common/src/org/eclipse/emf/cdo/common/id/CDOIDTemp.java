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
package org.eclipse.emf.cdo.common.id;

import org.eclipse.emf.cdo.common.CDOCommonTransaction;

/**
 * The {@link CDOID ID} of a CDO object that is new in a dirty (uncommitted) CDO {@link CDOCommonTransaction
 * transaction}.
 * 
 * @author Eike Stepper
 * @noextend This interface is not intended to be extended by clients.
 * @noimplement This interface is not intended to be implemented by clients.
 */
public interface CDOIDTemp extends CDOID
{
}
