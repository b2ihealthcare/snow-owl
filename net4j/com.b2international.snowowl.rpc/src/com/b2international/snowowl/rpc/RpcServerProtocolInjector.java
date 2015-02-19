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
package com.b2international.snowowl.rpc;

import org.eclipse.net4j.util.container.IManagedContainer;

/**
 * Initializes the server-side session and sets the client multiplexer for future reference when an RPC server protocol instance is opened.
 * 
 */
public class RpcServerProtocolInjector extends RpcProtocolInjector {

	/*
	 * (non-Javadoc)
	 * @see com.b2international.snowowl.rpc.RpcProtocolInjector#processProtocol(org.eclipse.net4j.util.container.IManagedContainer, com.b2international.snowowl.rpc.RpcProtocol)
	 */
	protected Object processProtocol(final IManagedContainer container, final RpcProtocol protocol) {

		/*
		 * Client protocols will already have an infrastructure set; we can't use ILocationAware#isServer() or alternatives, because the
		 * protocol might be deactivated and not know the its location.
		 */
		if (protocol.getInfraStructure() != null) {
			return protocol;
		}
		
		final RpcSessionImpl session = (RpcSessionImpl) RpcUtil.getInitialServerSession(container);
		protocol.setInfraStructure(new RpcSessionImpl(session));
		return protocol;
	}
}