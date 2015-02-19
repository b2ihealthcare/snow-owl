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

import java.io.IOException;
import java.lang.reflect.Method;
import java.text.MessageFormat;
import java.util.Arrays;
import java.util.concurrent.TimeUnit;

import org.eclipse.net4j.signal.RequestWithMonitoring;
import org.eclipse.net4j.util.io.ExtendedDataInputStream;
import org.eclipse.net4j.util.io.ExtendedDataOutputStream;
import org.eclipse.net4j.util.om.monitor.OMMonitor;

import com.b2international.snowowl.internal.rpc.bundle.OM;
import com.b2international.snowowl.rpc.RpcProtocol;
import com.b2international.snowowl.rpc.RpcSession;

/**
 * Represents a request for two-way RPC method invocations which may include a progress monitor.
 * 
 */
public abstract class InvokeRequestWithMonitoring extends RequestWithMonitoring<Object> {

	private long startTime;
	
	protected final String serviceClassName;
	protected final Method serviceMethod;
	protected final Object[] params;

	/**
	 * Creates a new {@link InvokeRequestWithMonitoring} instance associated with the specified client protocol and method call arguments.
	 * 
	 * @param protocol the protocol instance for this request
	 * @param signalID the signal ID for this request
	 * @param serviceClassName the name of the registered remote service interface
	 * @param serviceMethod metadata about the remote method
	 * @param params arguments of the remote method (may not be {@code null}; an empty array should be given for no-argument methods)
	 */
	protected InvokeRequestWithMonitoring(final RpcProtocol protocol, final short signalID, final String serviceClassName, final Method serviceMethod, final Object[] params) {
		super(protocol, signalID);
		this.serviceClassName = serviceClassName;
		this.serviceMethod = serviceMethod;
		this.params = params;
	}

	@Override
	public final RpcProtocol getProtocol() {
		return (RpcProtocol) super.getProtocol();
	}
	
	protected final RpcSession getServiceRegistry() {
		return getProtocol().getInfraStructure();
	}

	@Override
	protected void requesting(final ExtendedDataOutputStream out, final OMMonitor monitor) throws Exception {
		
		logRequestStart();
		monitor.begin();

		try {
			writeMethod(out);
		} finally {
			monitor.done();
		}
	}

	protected void writeMethod(final ExtendedDataOutputStream out) throws IOException {
		
		out.writeString(serviceClassName);
		out.writeString(serviceMethod.toGenericString());

		final int paramCount = params.length;
		out.writeInt(paramCount);

		for (int i = 0; i < paramCount; i++) {
			writeParameter(out, i);
		}
	}

	private void writeParameter(final ExtendedDataOutputStream out, final int paramIdx) throws IOException {
		
		final Object value = params[paramIdx];
		
		if (value == null) {
			out.writeBoolean(true);
			return;
		}

		out.writeBoolean(false);
		
		final ValueType type = ValueType.fromObject(value);
		out.writeEnum(type);
		
		if (type.isProxied()) {
			handleProxyParameter(out, paramIdx, type);
		} else {
			type.write(out, value);
		}
	}

	protected abstract void handleProxyParameter(final ExtendedDataOutputStream out, final int paramIdx, final ValueType type) throws IOException;

	@Override
	protected Object confirming(final ExtendedDataInputStream in, final OMMonitor monitor) throws Exception {
		
		monitor.begin();

		try {
			
			final boolean success = in.readBoolean();
			
			if (success) {
				return readResult(in);
			} else {
				return readAndThrowException(in);
			}

		} finally {
			monitor.done();
			logRequestFinish();
		}
	}

	private Object readAndThrowException(final ExtendedDataInputStream in) throws RemoteInvocationTargetException, IOException {
		final Exception exceptionFromCall = (Exception) in.readObject(getServiceRegistry().getClassResolver(serviceClassName));
		throw new RemoteInvocationTargetException(exceptionFromCall);
	}

	private Object readResult(final ExtendedDataInputStream in) throws IOException, ClassNotFoundException {
		
		final boolean nullValue = in.readBoolean();

		if (nullValue) {
			return null;
		}

		final ValueType type = in.readEnum(ValueType.class);
		Object result;
		switch (type) {
			case ENUM_VALUE:
				result = readEnum(in);
				break;
			case OBJECT:
				result = in.readObject(getServiceRegistry().getClassResolver(serviceClassName));
				break;
			default:
				result = type.read(in);
				break;
		}

		return result;
	}

	@SuppressWarnings("unchecked")
	private Object readEnum(final ExtendedDataInputStream in) throws IOException, ClassNotFoundException {

		final String enumClassName = in.readString();
		Class<Enum<?>> enumClass = (Class<Enum<?>>) getServiceRegistry().getClassByName(serviceClassName, enumClassName);

		// Is this value an enum class where methods have been overridden?
		if (enumClass.isAnonymousClass()) {
			enumClass = (Class<Enum<?>>) enumClass.getEnclosingClass();
		}

		return in.readEnum(enumClass);
	}

	/**
	 * 
	 * @param paramIdx
	 * @return
	 */
	public Object getParameter(final int paramIdx) {
		return params[paramIdx];
	}

	private void logRequestStart() {
		if (isLoggingEnabled()) {
			OM.LOG.info(MessageFormat.format(">>> {0}.{1}({2}): {3}", 
					getSimpleClassName(), 
					serviceMethod.getName(), 
					getParameterString(),
					getReturnTypeName()));
			
			startTime = System.nanoTime();
		}
	}

	private void logRequestFinish() {
		if (isLoggingEnabled()) {
			final long endTime = System.nanoTime();
			
			OM.LOG.info(MessageFormat.format("<<< {0}.{1}({2}): {3} [{4} ms]", 
					getSimpleClassName(), 
					serviceMethod.getName(), 
					getParameterString(),
					serviceMethod.getReturnType().getSimpleName(),
					TimeUnit.NANOSECONDS.toMillis(endTime - startTime)));
		}
	}

	private String getSimpleClassName() {
		return serviceClassName.substring(serviceClassName.lastIndexOf('.') + 1);
	}

	private String getReturnTypeName() {
		return serviceMethod.getReturnType().getSimpleName();
	}

	private String getParameterString() {
		final String parameterString = Arrays.deepToString(params);
		return parameterString.substring(1, parameterString.length() - 1);
	}
	
	private boolean isLoggingEnabled() {
		return getProtocol().getConfiguration().isLogging();
	}
}