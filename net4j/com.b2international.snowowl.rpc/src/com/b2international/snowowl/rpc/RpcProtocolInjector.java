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

import org.eclipse.net4j.util.container.IElementProcessor;
import org.eclipse.net4j.util.container.IManagedContainer;

/**
 * 
 */
public abstract class RpcProtocolInjector implements IElementProcessor {

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.net4j.util.container.IElementProcessor#process(org.eclipse.net4j.util.container.IManagedContainer, java.lang.String,
	 * java.lang.String, java.lang.String, java.lang.Object)
	 */
	public final Object process(final IManagedContainer container, final String productGroup, final String factoryType, final String description, final Object element) {
	
		if (!(element instanceof RpcProtocol)) {
			return element;
		}
			
		final RpcProtocol protocol = (RpcProtocol) element;
		return processProtocol(container, protocol);
	}

	protected abstract Object processProtocol(final IManagedContainer container, final RpcProtocol protocol);
}