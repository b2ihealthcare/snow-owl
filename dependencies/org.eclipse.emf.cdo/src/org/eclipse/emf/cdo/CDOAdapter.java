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

import org.eclipse.emf.cdo.view.CDOAdapterPolicy;
import org.eclipse.emf.cdo.view.CDOView;
import org.eclipse.emf.cdo.view.CDOView.Options;

import org.eclipse.emf.common.notify.Adapter;

/**
 * A marker interface for {@link Adapter adpters} to indicate that change subscriptions should be registered with the
 * repository if they are attached to {@link CDOObject objects}.
 * <p>
 * This special marker interface is intended to be used with {@link CDOAdapterPolicy#CDO}. Note that you can also define
 * your own {@link CDOAdapterPolicy adapter policy} and {@link Options#addChangeSubscriptionPolicy(CDOAdapterPolicy)
 * register} it with the {@link CDOView view} to make your own adapters trigger change subscription.
 * 
 * @author Simon McDuff
 * @since 2.0
 * @apiviz.uses {@link CDONotification} - - receives
 */
public interface CDOAdapter extends Adapter
{
}
