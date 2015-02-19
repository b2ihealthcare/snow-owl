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
package com.b2international.snowowl.datastore.server.net4j.push;

import java.io.Serializable;
import java.util.Collection;

import org.eclipse.net4j.util.event.IEvent;
import org.eclipse.net4j.util.event.IListener;
import org.eclipse.net4j.util.lifecycle.LifecycleEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.b2international.snowowl.datastore.editor.notification.NotificationMessage;
import com.b2international.snowowl.datastore.net4j.push.PushConstants;
import com.b2international.snowowl.datastore.net4j.push.PushServiceException;
import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;
import com.google.common.collect.Multimaps;

/**
 * Server-side service for the push notification mechanism.
 * 
 * @since 2.8
 */
public enum PushServerService implements IListener {
	INSTANCE;

	private static Logger LOGGER = LoggerFactory.getLogger(PushServerService.class);
	
	private Multimap<Serializable, PushServerProtocol> subscribers = Multimaps.synchronizedListMultimap(
			ArrayListMultimap.<Serializable, PushServerProtocol>create());

	/**
	 * Pushes a notification message with the specified topic to the connected clients.
	 * 
	 * @param topic the topic of the notification
	 * @param message the notification message
	 * @throws PushServiceException 
	 */
	public <T extends Serializable, M extends NotificationMessage<? extends Serializable>> void push(T topic, M message) throws PushServiceException {
		if (PushConstants.BROADCAST_NOTIFICATION_TOPIC.equals(topic)) {
			synchronized (subscribers) {
				Collection<PushServerProtocol> protocols = subscribers.values();
				pushToProtocols(topic, message, protocols);
			}
		} else {
			synchronized (subscribers) {
				Collection<PushServerProtocol> protocols = subscribers.get(topic);
				pushToProtocols(topic, message, protocols);
			}
		}
	}

	private <T extends Serializable, M extends NotificationMessage<? extends Serializable>> void pushToProtocols(T topic, M message, Collection<PushServerProtocol> protocols) throws PushServiceException {
		for (PushServerProtocol protocol : protocols) {
			if (protocol.isActive()) {
				protocol.sendPushRequest(topic, message);
			} else {
				LOGGER.warn("Tried to push to inactive protocol: " + protocol + ". Removing from subscribers.");
				subscribers.values().remove(protocol);	// already in synchronized block
			}
		}
	}
	
	/**
	 * Adds a new subscriber to be notified when a notification is sent with the specified topic.
	 * 
	 * @param topic the topic to subscribe to
	 * @param protocol the client-specific protocol instance 
	 */
	public <T extends Serializable> void subscribe(T topic, PushServerProtocol protocol) {
		protocol.addListener(this);
		subscribers.put(topic, protocol);
	}
	
	/**
	 * Removes a subscriber from the specified topic. 
	 * Does nothing if the subscriber was not registered.
	 * 
	 * @param topic the topic remove the subscriber from
	 * @param protocol the client-specific protocol instance 
	 */
	public <T extends Serializable> void unsubscribe(T topic, PushServerProtocol protocol) {
		protocol.removeListener(this);
		subscribers.remove(topic, protocol);
	}

	@Override
	public void notifyEvent(IEvent event) {
		if (event instanceof LifecycleEvent) {
			LifecycleEvent lifecycleEvent = (LifecycleEvent) event;
			if (lifecycleEvent.getKind() == org.eclipse.net4j.util.lifecycle.ILifecycleEvent.Kind.DEACTIVATED) {
				PushServerProtocol deactivatedProtocol = (PushServerProtocol) lifecycleEvent.getSource();
				LOGGER.info("Client connection lost, removing corresponding protocol from subscribers.");
				synchronized (subscribers) {
					subscribers.values().remove(deactivatedProtocol);
				}
			}
		}
	}
}