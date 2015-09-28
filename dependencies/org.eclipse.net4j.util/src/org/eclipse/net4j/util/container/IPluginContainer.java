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
package org.eclipse.net4j.util.container;

import org.eclipse.net4j.internal.util.container.PluginContainer;

import org.eclipse.core.runtime.IExtensionRegistry;

/**
 * A {@link IManagedContainer managed container} that is configured by the {@link IExtensionRegistry extension registry}
 * .
 * 
 * @author Eike Stepper
 * @noextend This interface is not intended to be extended by clients.
 * @noimplement This interface is not intended to be implemented by clients.
 */
public interface IPluginContainer extends IManagedContainer
{
  public static final IPluginContainer INSTANCE = PluginContainer.getInstance();
}
