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
package org.eclipse.net4j.util.lifecycle;

/**
 * Enumerates the possible lifecycle states of an entity with a {@link ILifecycle lifecycle}.
 * 
 * @author Eike Stepper
 * @since 3.0
 */
public enum LifecycleState
{
  ACTIVATING, ACTIVE, DEACTIVATING, INACTIVE
}
