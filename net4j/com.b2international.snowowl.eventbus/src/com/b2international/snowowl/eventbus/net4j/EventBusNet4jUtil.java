/*
 * Copyright 2011-2016 B2i Healthcare Pte Ltd, http://b2i.sg
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
package com.b2international.snowowl.eventbus.net4j;

import org.eclipse.net4j.signal.wrapping.GZIPStreamWrapperInjector;
import org.eclipse.net4j.util.container.IManagedContainer;
import org.eclipse.spi.net4j.ClientProtocolFactory;

import com.b2international.snowowl.eventbus.IEventBus;
import com.b2international.snowowl.internal.eventbus.EventBus;
import com.b2international.snowowl.internal.eventbus.net4j.EventBusProtocol;
import com.b2international.snowowl.internal.eventbus.net4j.EventBusProtocolInjector;

/**
 * @since 3.1
 */
public class EventBusNet4jUtil {

	/**
	 * Prepares the given {@link IManagedContainer} to deliver message through {@link IEventBus} instances over the
	 * network.
	 * 
	 * @param container
	 * @param gzip - to enable gzip compression on the protocol or not
	 */
	public static final void prepareContainer(IManagedContainer container, boolean gzip) {
		container.registerFactory(new EventBusProtocol.ClientFactory());
		container.registerFactory(new EventBusProtocol.ServerFactory());
		container.registerFactory(new EventBus.Factory());
		container.addPostProcessor(new EventBusProtocolInjector());
		if (gzip) {
			container.addPostProcessor(new GZIPStreamWrapperInjector(EventBusConstants.PROTOCOL_NAME));
		}
	}

	/**
	 * Returns the client protocol of {@link IEventBusProtocol}.
	 * 
	 * @param container
	 * @return
	 */
	public static IEventBusProtocol getClientProtocol(IManagedContainer container) {
		return (IEventBusProtocol) container.getElement(ClientProtocolFactory.PRODUCT_GROUP,
				EventBusConstants.PROTOCOL_NAME, "client", false);
	}

	/**
	 * Returns the event bus associated with the description of globalBus.
	 * 
	 * @param container
	 * @return
	 */
	public static IEventBus getBus(IManagedContainer container) {
		return getBus(container, Runtime.getRuntime().availableProcessors());
	}
	
	/**
	 * Returns the event bus associated with the description of globalBus.
	 * 
	 * @param container
	 * @param numberOfWorkers
	 * @return
	 */
	private static IEventBus getBus(IManagedContainer container, int numberOfWorkers) {
		return getBus(container, EventBusConstants.GLOBAL_BUS, numberOfWorkers, true);
	}

	/**
	 * Returns with a custom local event bus operating only on the local node.
	 * 
	 * @param container
	 * @param name
	 * @param numberOfWorkers
	 * @return
	 */
	private static IEventBus getBus(IManagedContainer container, String name, int numberOfWorkers, boolean worker) {
		return (IEventBus) container.getElement(EventBusConstants.EVENT_BUS_PRODUCT_GROUP,
				EventBusConstants.PROTOCOL_NAME, String.format("%s:%s:%s", name, numberOfWorkers, worker), true);
	}

}