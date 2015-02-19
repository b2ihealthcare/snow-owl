/*
 * Copyright 2011-2015 B2i Healthcare Pte Ltd, http://b2i.sg
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
/**
 * 
 */
package com.b2international.snowowl.datastore.cdo;

import org.eclipse.emf.common.notify.Adapter;
import org.eclipse.emf.common.notify.Notification;
import org.eclipse.emf.common.notify.impl.NotificationImpl;

import com.b2international.snowowl.core.api.IMutable;

/**
 * An extended receiver of notifications.
 * <p>Clients may implement this interface.</p>
 * @see Adapter
 */
public interface IMutableAdapter extends Adapter, IMutable {

	static final int EVENT_TYPE = -1;
	static final Object VALUE = new Object();
	
	/**
	 * A custom empty notification instance.
	 */
	public static final Notification EMPTY_IMPL = new NotificationImpl(EVENT_TYPE, VALUE, VALUE);
}