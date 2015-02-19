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
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.eclipse.net4j.util.ReflectUtil;

import com.b2international.snowowl.rpc.Async;
import com.b2international.snowowl.rpc.RpcException;
import com.b2international.snowowl.rpc.RpcProtocol;

/**
 * A factory which creates {@link Proxy} implementations for a service interface that call through the RPC mechanism.
 * 
 *         implementations</a> (08.05.2013.)
 */
public class RpcClientStubFactory {
	
	private static final Object[] NO_ARGS = new Object[0];
	
	private static final String TO_STRING_METHOD = "toString";
	private static final String HASH_CODE_METHOD = "hashCode";
	private static final String EQUALS_METHOD = "equals";
	
	private final class ProtocolInvocationHandler implements InvocationHandler {
	
		private final String serviceClassName;

		public ProtocolInvocationHandler(final String serviceClassName) {
			this.serviceClassName = serviceClassName;
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
				return ReflectUtil.getSimpleClassName(serviceClassName) + " [proxy]"; 
			}

			checkArguments(method);
			
			try {
				return doInvoke(method, args);
			} catch (final Exception e) {
				
				// RpcExceptions don't make it to the other end, rethrow without changing them
				if (e instanceof RpcException) {
					throw e;
				}
				
				// Otherwise, update the stack trace to make it look like it was thrown from the proxy, then rethrow
				final List<StackTraceElement> rewrittenStackTrace = new ArrayList<StackTraceElement>(); 
				rewrittenStackTrace.addAll(Arrays.asList(Thread.currentThread().getStackTrace()));
				
				// The first two elements belong to Thread.getStackTrace() and the current catch clause, remove them
				if (rewrittenStackTrace.size() > 2) {
					rewrittenStackTrace.remove(1);
					rewrittenStackTrace.remove(0);
				}
				
				e.setStackTrace((StackTraceElement[]) rewrittenStackTrace.toArray(new StackTraceElement[rewrittenStackTrace.size()]));
				
				throw e;
			}
		}

		private Object doInvoke(final Method method, Object[] args) throws Exception {
			final Async asyncAnnotation = method.getAnnotation(Async.class);
			
			if (null != asyncAnnotation) {
				
				final Class<?> returnType = method.getReturnType();
				
				if (Void.TYPE.equals(returnType)) {
					protocol.sendAsync(serviceClassName, method, args);
					return null;
				} else {
					throw new UnsupportedOperationException("Async annotation can't be put on non-void methods.");
				}
				
			} else {
				return protocol.sendClientRequestWithMonitoring(serviceClassName, method, args);
			}
		}
		
		private void checkArguments(final Method method) {

			final Class<?>[] parameterTypes = method.getParameterTypes();
			
			for (int i = 0; i < parameterTypes.length; i++) {
				
				final ValueType valueType = ValueType.fromClass(parameterTypes[i]);
				
				if (valueType.isProxied() && !valueType.getValueClass().equals(parameterTypes[i])) {
					throw new UnsupportedOperationException("Can't send subtypes of proxied values.");
				}
			}
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

		private int hashCodeInternal(final Object proxy) {
			return 31 + serviceClassName.hashCode();
		}
	}

	private final RpcProtocol protocol;

	public RpcClientStubFactory(final RpcProtocol protocol) {
		this.protocol = protocol;
	}

	public <T> T createClientStub(final Class<T> serviceInterface) {
		final Class<?>[] proxyInterfaces = new Class<?>[] { serviceInterface };
		final String serviceClassName = serviceInterface.getName();
		return serviceInterface.cast(Proxy.newProxyInstance(serviceInterface.getClassLoader(), proxyInterfaces, new ProtocolInvocationHandler(serviceClassName)));
	}
}