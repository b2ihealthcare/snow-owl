/*
 * Copyright (c) 2004 - 2012 Eike Stepper (Berlin, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *    Simon McDuff - initial API and implementation
 *    Eike Stepper - maintenance
 */
package org.eclipse.emf.cdo;

import org.eclipse.emf.common.notify.Notification;

/**
 * A base interface for all CDO specific notifications.
 * 
 * @since 2.0
 * @author Simon McDuff
 * @noextend This interface is not intended to be extended by clients.
 * @noimplement This interface is not intended to be implemented by clients.
 */
public interface CDONotification extends Notification
{
  public static final int EVENT_TYPE_CDO_START = Notification.EVENT_TYPE_COUNT + 100;

  public static final int DETACH_OBJECT = EVENT_TYPE_CDO_START + 1;

  public static final int INVALIDATE = EVENT_TYPE_CDO_START + 2;
}
