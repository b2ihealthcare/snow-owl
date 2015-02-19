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
package com.b2international.snowowl.internal.rpc;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

import com.b2international.snowowl.rpc.RpcProtocol;

/**
 * A factory which creates {@link Proxy} implementations for a service interface that call through the RPC mechanism from the server.
 * 
 *         implementations</a> (08.05.2013.)
 */
public class RpcServerStubFactory {
	
	private static final Object[] NO_ARGS = new Object[0];
	
	private static final String TO_STRING_METHOD = "toString";
	private static final String HASH_CODE_METHOD = "hashCode";
	private static final String EQUALS_METHOD = "equals";
	
	private final class ProtocolInvocationHandler implements InvocationHandler {
	
		private final String serviceClassName;
		private final int correlationID;
		private final int paramIdx;

		public ProtocolInvocationHandler(final String serviceClassName, final int correlationID, final int paramIdx) {
			this.serviceClassName = serviceClassName;
			this.correlationID = correlationID;
			this.paramIdx = paramIdx;
		}

		public Object invoke(final Object proxy, final Method method, Object[] args) throws Throwable {
			
			if (null == args) {
				args = NO_ARGS;
			}
			
			if (Object.class == method.getDeclaringClass() && HASH_CODE_METHOD.equals(method.getName())) {
				return hashCodeInternal(proxy);
			}
			
			if (Object.class == method.getDeclaringClass() && EQUALS_METHOD.equals(method.getName())) {
				//guard against AIOOBE
				return equalsInternal(proxy, args.length == 0 ? null : args[0]);
			}
			
			if (Object.class == method.getDeclaringClass() && TO_STRING_METHOD.equals(method.getName())) {
				return toStringInternal(proxy);
			}

			return protocol.sendServerRequestWithMonitoring(serviceClassName, method, args, correlationID, paramIdx);
		}

		private String toStringInternal(final Object me) {
			return serviceClassName + '@' + Integer.toHexString(hashCodeInternal(me)) + " [proxy]";
		}

		private int hashCodeInternal(final Object me) {
			return System.identityHashCode(me);
		}
		
		private boolean equalsInternal(final Object me, final Object other) {
			
			if (null == other) {
				return false;
			}
			
			if (me == other) {
				return true;
			}
			
			if (other.getClass() != me.getClass()) {
				//not same proxy type; return false.
				//this may not be true for other scenarios.
				return false;
			}
			
			final InvocationHandler handler = Proxy.getInvocationHandler(other);
			if (handler instanceof ProtocolInvocationHandler) {
				return ((ProtocolInvocationHandler) handler).serviceClassName.equals(serviceClassName);
			}

			// the proxies behave differently.
			return false;
		}
	}

	private final RpcProtocol protocol;
	
	public RpcServerStubFactory(final RpcProtocol protocol) {
		this.protocol = protocol;
	}

	public <T> T createServerStub(final Class<T> proxyInterface, final int correlationID, final int paramIdx) {
		final Class<?>[] proxyInterfaces = new Class<?>[] { proxyInterface };
		return proxyInterface.cast(Proxy.newProxyInstance(proxyInterface.getClassLoader(), proxyInterfaces, new ProtocolInvocationHandler(proxyInterface.getName(), correlationID, paramIdx)));
	}
}