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

import org.eclipse.emf.cdo.common.id.CDOID;
import org.eclipse.emf.cdo.session.CDOSessionInvalidationEvent;
import org.eclipse.emf.cdo.view.CDOAdapterPolicy;
import org.eclipse.emf.cdo.view.CDOView;
import org.eclipse.emf.cdo.view.CDOView.Options;

import org.eclipse.emf.common.notify.Adapter;
import org.eclipse.emf.common.notify.Notification;

/**
 * A custom EMF {@link Notification notification} that is emitted to {@link Adapter adapters} of the objects in a
 * {@link CDOView view} if {@link Options#setInvalidationNotificationEnabled(boolean) invalidation notification} is
 * enabled for the view. Since the notifications are constructed out of the information in a
 * {@link CDOSessionInvalidationEvent} (i.e. {@link CDOID CDOIDs}) they don't carry detailed change deltas. All the
 * methods related to change deltas throw {@link UnsupportedOperationException UnsupportedOperationExceptions}.
 * 
 * @author Simon McDuff
 * @see CDOSessionInvalidationEvent
 * @see CDOAdapterPolicy
 * @noextend This interface is not intended to be extended by clients.
 * @noimplement This interface is not intended to be implemented by clients.
 */
public interface CDOInvalidationNotification extends CDONotification
{
}
