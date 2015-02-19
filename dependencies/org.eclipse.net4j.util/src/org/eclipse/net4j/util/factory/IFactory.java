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
package org.eclipse.net4j.util.factory;

/**
 * {@link #create(String) Creates} objects from a string {@link #getDescriptionFor(Object) description}.
 * 
 * @author Eike Stepper
 * @apiviz.landmark
 * @apiviz.has {@link IFactoryKey}
 */
public interface IFactory
{
  public IFactoryKey getKey();

  public Object create(String description) throws ProductCreationException;

  public String getDescriptionFor(Object product);
}
