/*
 * Copyright (c) 2004 - 2012 Eike Stepper (Berlin, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Simon McDuff - initial API and implementation
 */
package org.eclipse.emf.cdo.view;

import org.eclipse.emf.cdo.common.id.CDOWithID;

/**
 * A marker interface for stale objects as produced by {@link CDOStaleReferencePolicy#PROXY}.
 *
 * @author Simon McDuff
 * @since 3.0
 * @noextend This interface is not intended to be extended by clients.
 * @noimplement This interface is not intended to be implemented by clients.
 * @apiviz.exclude
 */
public interface CDOStaleObject extends CDOWithID
{
}
