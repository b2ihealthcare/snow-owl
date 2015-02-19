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

import org.eclipse.net4j.util.event.Event;

/**
 * A default implementation of a lifecycle {@link ILifecycleEvent event}.
 * 
 * @author Eike Stepper
 * @noextend This class is not intended to be subclassed by clients.
 * @apiviz.exclude
 */
public class LifecycleEvent extends Event implements ILifecycleEvent
{
  private static final long serialVersionUID = 1L;

  private Kind kind;

  public LifecycleEvent(Lifecycle lifecycle, Kind kind)
  {
    super(lifecycle);
    this.kind = kind;
  }

  /**
   * @since 3.0
   */
  @Override
  public ILifecycle getSource()
  {
    return (ILifecycle)super.getSource();
  }

  public Kind getKind()
  {
    return kind;
  }
}
