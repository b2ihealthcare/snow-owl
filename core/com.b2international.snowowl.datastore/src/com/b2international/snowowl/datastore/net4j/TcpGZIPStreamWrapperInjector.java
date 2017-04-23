/*
 * Copyright 2017 B2i Healthcare Pte Ltd, http://b2i.sg
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
package com.b2international.snowowl.datastore.net4j;

import org.eclipse.net4j.signal.SignalProtocol;
import org.eclipse.net4j.signal.wrapping.GZIPStreamWrapperInjector;
import org.eclipse.net4j.tcp.ITCPConnector;
import org.eclipse.net4j.util.container.IManagedContainer;

/**
 * @since 5.10
 */
public class TcpGZIPStreamWrapperInjector extends GZIPStreamWrapperInjector {

	public TcpGZIPStreamWrapperInjector(String protocolID) {
		super(protocolID);
	}
	
	@Override
	protected boolean shouldInject(IManagedContainer container, String productGroup, String factoryType,
			String description, SignalProtocol<?> protocol) {
		if (!(protocol.getChannel().getMultiplexer() instanceof ITCPConnector)) {
			return false;
		} else {
			return super.shouldInject(container, productGroup, factoryType, description, protocol);
		}
	}

}
