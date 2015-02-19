/*
 * Copyright (c) 2004 - 2012 Eike Stepper (Berlin, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Victor Roldan Betancort - initial API and implementation
 *    Eike Stepper - maintenance
 */
package org.eclipse.net4j.util.options;

import org.eclipse.net4j.util.event.INotifier;

/**
 * Encapsulates a set of notifying configuration options.
 *
 * @since 2.0
 * @author Victor Roldan Betancort
 * @see IOptionsContainer
 */
public interface IOptions extends INotifier
{
  /**
   * Returns the container of this options object.
   */
  public IOptionsContainer getContainer();
}
