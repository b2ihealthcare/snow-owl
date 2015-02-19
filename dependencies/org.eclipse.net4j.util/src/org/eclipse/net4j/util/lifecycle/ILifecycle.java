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

import org.eclipse.net4j.util.event.INotifier;

/**
 * An entity that has a well-defined {@link #getLifecycleState() lifecycle} and can be {@link #activate() activated} or
 * {@link #deactivate() deactivated}.
 * <p>
 * A lifecycle can fire the following events:
 * <ul>
 * <li> {@link ILifecycleEvent} before and after the {@link #getLifecycleState() state} of this lifecycle changes.
 * </ul>
 *
 * @author Eike Stepper
 * @apiviz.landmark
 * @apiviz.has {@link LifecycleState}
 * @apiviz.uses {@link ILifecycleEvent} - - fires
 * @apiviz.excludeSubtypes
 */
public interface ILifecycle extends INotifier
{
  public void activate() throws LifecycleException;

  public Exception deactivate();

  /**
   * @since 3.0
   */
  public LifecycleState getLifecycleState();

  /**
   * @since 3.0
   */
  public boolean isActive();

  /**
   * A mix-in interface for {@link ILifecycle lifecycles} with deferrable activation.
   *
   * @author Eike Stepper
   * @since 3.2
   */
  public interface DeferrableActivation
  {
    public boolean isDeferredActivation();
  }
}
