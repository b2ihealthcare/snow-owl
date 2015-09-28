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
package org.eclipse.net4j.util.concurrent;

import org.eclipse.net4j.util.lifecycle.LifecycleUtil;
import org.eclipse.net4j.util.om.log.OMLogger;

/**
 * @author Eike Stepper
 */
public class QueueWorkerWorkSerializer extends QueueRunner implements IWorkSerializer
{
  public QueueWorkerWorkSerializer()
  {
    activate();
  }

  public void dispose()
  {
    LifecycleUtil.deactivate(this, OMLogger.Level.DEBUG);
  }
}
