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

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.ExecutorService;

import org.eclipse.net4j.connector.IConnector;
import org.eclipse.net4j.signal.Request;
import org.eclipse.net4j.signal.RequestWithConfirmation;
import org.eclipse.net4j.util.lifecycle.LifecycleUtil;
import org.eclipse.spi.net4j.InternalChannel;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import com.b2international.snowowl.eventbus.IMessage;
import com.b2international.snowowl.eventbus.net4j.EventBusConstants;
import com.b2international.snowowl.eventbus.net4j.IRequestFactory;
import com.b2international.snowowl.internal.eventbus.EventBus;
import com.b2international.snowowl.internal.eventbus.HandlerChangedEvent;

/**
 * @since 3.1
 */
@RunWith(MockitoJUnitRunner.class)
public class EventBusProtocolTest {

	private static final String ADDRESS = "address";

	@Mock
	private InternalChannel channel;
	
	@Mock
	private ExecutorService executorService;
	
	@Mock
	private IConnector connector;
	
	@Mock
	private HandlerChangedEvent event;

	@Mock
	private IMessage message;
	
	@Mock
	private EventBus eb;
	
	@Mock
	private IRequestFactory factory;
	
	@Mock
	private Request request;
	
	@Mock
	private RequestWithConfirmation<Object> requestWithConfirmation;
	
	private Object resultObject = new Object();
	private Set<String> addresses = new HashSet<String>();
	private EventBusProtocol protocol;

	@Before
	public void before() {
		addresses.add(ADDRESS);
		when(eb.getAddressBook()).thenReturn(addresses);
		protocol = new EventBusProtocol(EventBusConstants.PROTOCOL_NAME, factory);
		protocol.setChannel(channel);
		when(channel.isActive()).thenReturn(true);
		protocol.setExecutorService(executorService);
		LifecycleUtil.activate(protocol);
	}

	@Test
	public void testOpen_WithInfrastructure_ShouldSendRegistration() throws Exception {
		prepareRequestSync(protocol, EventBusConstants.HANDLER_INIT, addresses, resultObject);
		protocol.setInfraStructure(eb);
		protocol.open(connector);
		// verify that the protocol got opened
		verify(connector).openChannel(protocol);
		// verify message has been sent
		verify(requestWithConfirmation).send(EventBusProtocol.ADDRESS_BOOK_REQ_TIMEOUT);
	}
	
	@Test
	public void testHandle_Null_Message() throws Exception {
		protocol.handle(null);
	}
	
	@Test
	public void testHandle_Nonnull_Message() throws Exception {
		prepareRequest(protocol, EventBusConstants.SEND_MESSAGE_SIGNAL, message);
		protocol.handle(message);
		verify(request).sendAsync();
	}
	
	@Test
	public void testNotifyEvent_Null() throws Exception {
		protocol.notifyEvent(null);
	}
	
	@Test
	public void testNotifyEvent_HandlerAdded() throws Exception {
		prepareEvent(true);
		prepareRequestSync(protocol, EventBusConstants.HANDLER_REGISTRATION, addresses, true);
		protocol.notifyEvent(event);
		verify(requestWithConfirmation).send(EventBusProtocol.ADDRESS_BOOK_REQ_TIMEOUT);
	}
	
	@Test
	public void testNotifyEvent_HandlerRemoved() throws Exception {
		prepareEvent(false);
		prepareRequestSync(protocol, EventBusConstants.HANDLER_UNREGISTRATION, addresses, true);
		protocol.notifyEvent(event);
		verify(requestWithConfirmation).send(EventBusProtocol.ADDRESS_BOOK_REQ_TIMEOUT);
	}
	
	@Test
	public void testNotifyEvent_NullResult_ShouldBeHandledProperly() throws Exception {
		prepareEvent(true);
		prepareRequestSync(protocol, EventBusConstants.HANDLER_REGISTRATION, addresses, null);
		protocol.notifyEvent(event);
		verify(requestWithConfirmation).send(EventBusProtocol.ADDRESS_BOOK_REQ_TIMEOUT);
	}
	
	private void prepareEvent(boolean isAdded) {
		when(event.getAddress()).thenReturn(ADDRESS);
		when(event.isAdded()).thenReturn(isAdded);
	}

	private void prepareRequest(EventBusProtocol protocol, short signalID, Object message) {
		when(factory.createRequest(protocol, signalID, message)).thenReturn(request);		
	}
	
	private void prepareRequestSync(EventBusProtocol protocol, short signalID, Object message, Object result) throws Exception {
		when(factory.createRequestWithConfirmation(protocol, signalID, message)).thenReturn(requestWithConfirmation);
	}
	
}
