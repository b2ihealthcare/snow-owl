/*
 * Copyright 2011-2018 B2i Healthcare Pte Ltd, http://b2i.sg
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

import org.eclipse.net4j.util.container.IElementProcessor;
import org.eclipse.net4j.util.container.IManagedContainer;

import com.b2international.snowowl.eventbus.net4j.EventBusNet4jUtil;
import com.b2international.snowowl.eventbus.net4j.IEventBusProtocol;

/**
 * @since 3.1
 */
public class EventBusProtocolInjector implements IElementProcessor {

	private final int numberOfWorkers;

	public EventBusProtocolInjector(int numberOfWorkers) {
		this.numberOfWorkers = numberOfWorkers;
	}

	@Override
	public Object process(IManagedContainer container, String productGroup, String factoryType, String description,
			Object element) {
		if (element instanceof IEventBusProtocol) {
			final IEventBusProtocol protocol = (EventBusProtocol) element;
			if (protocol.getInfraStructure() == null) {
				protocol.setInfraStructure(EventBusNet4jUtil.getBus(container, numberOfWorkers));
			}
		}
		return element;
	}

}