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
package org.eclipse.emf.cdo.server;

/**
 * Provides the consumer with CDO {@link IRepository repositories} specified by their name.
 * 
 * @author Eike Stepper
 * @apiviz.uses {@link IRepository} - - provides
 */
public interface IRepositoryProvider
{
  public IRepository getRepository(String name);
}
