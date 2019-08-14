/*
 * Copyright 2011-2019 B2i Healthcare Pte Ltd, http://b2i.sg
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
package com.b2international.snowowl.internal.eventbus.net4j;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import org.eclipse.net4j.channel.IChannel;
import org.eclipse.net4j.connector.IConnector;
import org.eclipse.net4j.signal.Request;
import org.eclipse.net4j.signal.RequestWithConfirmation;
import org.eclipse.net4j.signal.SignalProtocol;
import org.eclipse.net4j.signal.SignalReactor;
import org.eclipse.net4j.util.CheckUtil;
import org.eclipse.net4j.util.event.IEvent;
import org.eclipse.net4j.util.event.IListener;
import org.eclipse.net4j.util.event.INotifier;
import org.eclipse.net4j.util.factory.ProductCreationException;
import org.eclipse.net4j.util.lifecycle.LifecycleUtil;
import org.eclipse.spi.net4j.ClientProtocolFactory;
import org.eclipse.spi.net4j.ServerProtocolFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.b2international.snowowl.eventbus.IEventBus;
import com.b2international.snowowl.eventbus.IMessage;
import com.b2international.snowowl.eventbus.net4j.EventBusConstants;
import com.b2international.snowowl.eventbus.net4j.IEventBusProtocol;
import com.b2international.snowowl.eventbus.net4j.IRequestFactory;
import com.b2international.snowowl.internal.eventbus.HandlerChangedEvent;

/**
 * @since 3.1
 */
public class EventBusProtocol extends SignalProtocol<IEventBus> implements IEventBusProtocol, IListener {

	static final int ADDRESS_BOOK_REQ_TIMEOUT = 60 * 1000;
	private static final Logger LOG = LoggerFactory.getLogger(EventBusProtocol.class);
	private final IRequestFactory factory;
	private Set<String> remoteAddresses = Collections.synchronizedSet(new HashSet<String>());

	/*package*/ EventBusProtocol(String type, IRequestFactory factory) {
		super(type);
		this.factory = factory;
	}

	@Override
	protected SignalReactor createSignalReactor(short signalID) {
		switch (signalID) {
		case EventBusConstants.SEND_MESSAGE_SIGNAL:
			return new SendIndication(this, signalID);
		case EventBusConstants.HANDLER_INIT:
		case EventBusConstants.HANDLER_REGISTRATION:
		case EventBusConstants.HANDLER_UNREGISTRATION:
			return new HandlerChangeIndication(this, signalID);
		}
		return super.createSignalReactor(signalID);
	}

	@Override
	protected void doAfterActivate() throws Exception {
		super.doAfterActivate();
		if (getInfraStructure() instanceof INotifier) {
			((INotifier) getInfraStructure()).addListener(this);
		}
	}

	@Override
	protected void doBeforeDeactivate() throws Exception {
		if (getInfraStructure() instanceof INotifier) {
			((INotifier) getInfraStructure()).removeListener(this);
		}
		if (getInfraStructure() != null) {
			synchronized (remoteAddresses) {
				for (String address : remoteAddresses) {
					getInfraStructure().unregisterHandler(address, this);
				}
			}
		}
		super.doBeforeDeactivate();
	}

	@Override
	public void handle(IMessage message) {
		LOG.trace("Handling message: {}", message);
		send(EventBusConstants.SEND_MESSAGE_SIGNAL, message);
	}

	@Override
	public void notifyEvent(IEvent event) {
		LOG.trace("Got notification event: {}", event);
		if (event instanceof HandlerChangedEvent) {
			final HandlerChangedEvent changedEvent = (HandlerChangedEvent) event;
			final Object result = sendSync(changedEvent.isAdded() ? EventBusConstants.HANDLER_REGISTRATION
					: EventBusConstants.HANDLER_UNREGISTRATION, Collections.singleton(changedEvent.getAddress()));
			if (result instanceof Boolean && !(boolean) result) {
				LOG.error("Failed to {} address {} in remote end", changedEvent.isAdded() ? "register" : "unregister", changedEvent.getAddress());
			}
		}
	}

	@Override
	public IChannel open(IConnector connector) {
		final IChannel open = super.open(connector);
		if (getInfraStructure() != null) {
			final Object result = sendSync(EventBusConstants.HANDLER_INIT, getInfraStructure().getAddressBook());
			if (result instanceof Set) {
				registerAddressBook((Set<String>)result);
			}
		}
		return open;
	}
	
	private void send(short signalID, Object body) {
		if (body != null && isProtocolActive()) {
			LOG.trace("Sending async message, ID: {}, body: {}", signalID, body);
			try {
				final Request request = factory.createRequest(this, signalID, body);
				if (request != null) {
					request.sendAsync();
				}
			} catch (Exception e) {
				LOG.error("Exception happened while sending async request", e);
			}
		}
	}

	private Object sendSync(short signalID, Object body) {
		if (body != null && isProtocolActive()) {
			LOG.trace("Sending sync message, ID: {}, body: {}", signalID, body);
			try {
				final RequestWithConfirmation<Object> request = factory.createRequestWithConfirmation(this, signalID, body);
				if (request != null) {
					return request.send(ADDRESS_BOOK_REQ_TIMEOUT);
				}
			} catch (Exception e) {
				LOG.error("Exception happened while sending sync request", e);
			}
		}
		return null;
	}

	private boolean isProtocolActive() {
		return isActive() && LifecycleUtil.isActive(getChannel());
	}
	
	/*package*/ void registerAddressBook(Set<String> addresses) {
		CheckUtil.checkArg(addresses, "addresses");
		for (String address : addresses) {
			if (remoteAddresses.add(address)) {
				getInfraStructure().registerHandler(address, this);
			}
		}
	}
	
	/*package*/ void unregisterAddressBook(Set<String> addresses) {
		CheckUtil.checkArg(addresses, "addresses");
		for (String address : addresses) {
			if (remoteAddresses.remove(address)) {
				getInfraStructure().unregisterHandler(address, this);
			}
		}
	}
	
	public static class ServerFactory extends ServerProtocolFactory {

		public ServerFactory() {
			super(EventBusConstants.PROTOCOL_NAME);
		}

		@Override
		public Object create(String description) throws ProductCreationException {
			LOG.debug("Creating new EventBusProtocol in: {}", ServerFactory.class.getSimpleName());
			return new EventBusProtocol(getType(), new RequestFactory());
		}
		
	}

	public static class ClientFactory extends ClientProtocolFactory {

		public ClientFactory() {
			super(EventBusConstants.PROTOCOL_NAME);
		}

		@Override
		public Object create(String description) throws ProductCreationException {
			LOG.debug("Creating new EventBusProtocol in {}", ClientFactory.class.getSimpleName());
			return new EventBusProtocol(getType(), new RequestFactory());
		}

	}

}