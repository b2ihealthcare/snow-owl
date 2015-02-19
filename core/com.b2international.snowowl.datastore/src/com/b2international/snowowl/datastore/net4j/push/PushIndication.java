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
package com.b2international.snowowl.datastore.net4j.push;

import java.util.Set;

import org.eclipse.net4j.signal.Indication;
import org.eclipse.net4j.signal.SignalProtocol;
import org.eclipse.net4j.util.io.ExtendedDataInputStream;

import com.b2international.snowowl.core.api.Net4jProtocolConstants;
import com.b2international.snowowl.datastore.editor.notification.INotificationListener;
import com.b2international.snowowl.datastore.editor.notification.NotificationMessage;
import com.google.common.collect.MapMaker;
import com.google.common.collect.Sets;

/**
 * An {@link Indication} for handling a server-side notification.
 * 
 * @since 2.8
 */
public class PushIndication extends Indication {

	public PushIndication(SignalProtocol<?> protocol) {
		super(protocol, Net4jProtocolConstants.PUSH_SIGNAL);
	}

	@Override
	protected void indicating(ExtendedDataInputStream in) throws Exception {
		Object topic = in.readObject();
		Object message = in.readObject(NotificationMessage.class.getClassLoader());
		NotificationManager.INSTANCE.notifyListeners(topic, message);
	}
	
	public enum NotificationManager {
		INSTANCE;

		// TODO: changed to concurrent set implementation to avoid concurrent modification exceptions, revisit this decision 
		private Set<INotificationListener> listeners = Sets.<INotificationListener>newSetFromMap(new MapMaker().<INotificationListener, Boolean>makeMap());

		public void notifyListeners(Object topic, Object notification) {
			for (INotificationListener listener : listeners) {
				listener.handleNotification(topic, notification);
			}
		}

		public void addNotificationListener(INotificationListener listener) {
			listeners.add(listener);
		}

		public void removePushNotificationListener(INotificationListener listener) {
			listeners.remove(listener);
		}

	}

}