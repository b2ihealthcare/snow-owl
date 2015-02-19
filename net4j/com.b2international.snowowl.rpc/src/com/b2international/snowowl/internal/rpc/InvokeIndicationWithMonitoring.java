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
import java.io.NotSerializableException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.text.MessageFormat;
import java.util.Arrays;
import java.util.concurrent.TimeUnit;

import org.eclipse.net4j.channel.IChannelMultiplexer;
import org.eclipse.net4j.signal.IndicationWithMonitoring;
import org.eclipse.net4j.util.io.ExtendedDataInputStream;
import org.eclipse.net4j.util.io.ExtendedDataOutputStream;
import org.eclipse.net4j.util.lifecycle.LifecycleException;
import org.eclipse.net4j.util.om.monitor.OMMonitor;
import org.eclipse.net4j.util.om.monitor.OMMonitor.Async;

import com.b2international.snowowl.internal.rpc.bundle.OM;
import com.b2international.snowowl.rpc.RpcProtocol;
import com.b2international.snowowl.rpc.RpcSession;
import com.b2international.snowowl.rpc.RpcThreadLocal;

/**
 * An indication for two-way RPC method invocations which may include a progress monitor.
 * 
 */
public abstract class InvokeIndicationWithMonitoring extends IndicationWithMonitoring {

	private long startTime;

	private Object result;
	private Exception exceptionFromCall;

	private String serviceClassName;
	private Method serviceMethod;
	private Object[] params;
	
	/**
	 * Creates a new {@link InvokeIndicationWithMonitoring} instance associated with the specified server protocol.
	 * 
	 * @param protocol the protocol instance for this request
	 * @param signalID the signal ID for this request
	 */
	protected InvokeIndicationWithMonitoring(final RpcProtocol protocol, final short signalID) {
		super(protocol, signalID);
	}

	@Override
	public final RpcProtocol getProtocol() {
		return (RpcProtocol) super.getProtocol();
	}
	
	protected final RpcSession getSession() {
		return getProtocol().getInfraStructure();
	}

	@Override
	protected void indicating(final ExtendedDataInputStream in, final OMMonitor monitor) throws Exception {
		
		if (isLoggingEnabled()) {
			startTime = System.nanoTime();
		}
		
		monitor.begin();
		Async async = null;
		
		try {
			async = monitor.forkAsync();
			readMethod(in);
		} finally  {
			stopAsync(async);
			monitor.done();
		}
	}

	protected void readMethod(final ExtendedDataInputStream in) throws IOException, ClassNotFoundException, NoSuchMethodException, Exception {
		serviceClassName = in.readString();
		final String serviceMethodName = in.readString();
		final int paramCount = in.readInt();
		params = new Object[paramCount];

		for (int i = 0; i < paramCount; i++) {
			params[i] = readParam(serviceClassName, in, i);
		}

		final Class<?> serviceClass = getServiceClass(serviceClassName);
		serviceMethod = getServiceMethod(serviceClass, serviceMethodName);

		logIndicationStart();
		
		final Object serviceImplementation = getServiceImplementation(serviceClass);
		
		try {
			
			RpcThreadLocal.setSession(getSession());
			result = serviceMethod.invoke(serviceImplementation, params);
			
		} catch (final InvocationTargetException e) {
			
			final Exception exceptionToLog;
			
			if (e.getCause() instanceof Exception) {
				exceptionToLog =  (Exception) e.getCause();
			} else {
				exceptionToLog = e;
			}
					
			registerException(serviceClassName, serviceMethodName, params, exceptionToLog);
			return;
			
		} catch (final Exception e) {
			throw e;
		} finally {
			RpcThreadLocal.releaseSession();
		}
	}

	protected Class<?> getServiceClass(final String serviceClassName) throws ClassNotFoundException {
		return getSession().getServiceClassByName(serviceClassName);
	}
	
	private void registerException(final String serviceClassName, final String serviceMethodName, final Object[] params, final Exception e) throws Exception {
		exceptionFromCall = e;
	}

	private Object readParam(final String serviceClassName, final ExtendedDataInputStream in, final int paramIdx) throws IOException, ClassNotFoundException {

		final boolean nullValue = in.readBoolean();

		if (nullValue) {
			return null;
		}

		final ValueType type = in.readEnum(ValueType.class);

		if (type.isProxied()) {
			return handleProxyParameter(in, type, paramIdx);
		}
		
		switch (type) {
			case ENUM_VALUE:
				return readEnum(serviceClassName, in);
			case OBJECT:
				return in.readObject(getSession().getClassResolver(serviceClassName));
			default:
				return type.read(in);
		}
	}
	
	protected abstract Object handleProxyParameter(ExtendedDataInputStream in, ValueType type, int paramIdx);

	@SuppressWarnings("unchecked")
	private Object readEnum(final String serviceClassName, final ExtendedDataInputStream in) throws IOException, ClassNotFoundException {

		final String enumClassName = in.readString();
		Class<Enum<?>> enumClass = (Class<Enum<?>>) getSession().getClassByName(serviceClassName, enumClassName);

		// Is this value an enum class where methods have been overridden?
		if (enumClass.isAnonymousClass()) {
			enumClass = (Class<Enum<?>>) enumClass.getEnclosingClass();
		}

		return in.readEnum(enumClass);
	}
	
	protected abstract Object getServiceImplementation(final Class<?> serviceClass);

	// For the server, it's something like:
	//		return getServiceRegistry().getServiceImplementation(serviceClass);
	
	private Method getServiceMethod(final Class<?> serviceClass, final String serviceMethodName) throws NoSuchMethodException {
		Method serviceMethod = null;

		for (final Method m : serviceClass.getMethods()) {
			if (serviceMethodName.equals(m.toGenericString())) {
				serviceMethod = m;
				break;
			}
		}

		if (serviceMethod == null) {
			throw new NoSuchMethodException(serviceMethodName);
		}

		return serviceMethod;
	}

	@Override
	protected void responding(final ExtendedDataOutputStream out, final OMMonitor monitor) throws Exception {
		
		monitor.begin();
		Async async = null;

		try {
			
			async = monitor.forkAsync();

			if (exceptionFromCall != null) {
				
				// indicate exception during method invocation
				out.writeBoolean(false);
				
				try {
					out.writeObject(exceptionFromCall);
				} catch (final NotSerializableException e) {
					// Net4j will take care of sending a String representation which includes the server-side stack trace
					e.addSuppressed(exceptionFromCall);
					throw e;
				}
				
			} else {
				
				// indicate successful method invocation
				out.writeBoolean(true);
				writeResult(out);
			}
			
		} finally {
			stopAsync(async);
			monitor.done();
			logIndicationFinish();
		}
	}

	private void writeResult(final ExtendedDataOutputStream out) throws IOException {

		if (result == null) {
			out.writeBoolean(true);
			return;
		}

		out.writeBoolean(false);
		
		final ValueType type = ValueType.fromObject(result);
		out.writeEnum(type);
		type.write(out, result);
	}

	protected void stopAsync(final Async async) {
		if (null != async) {
			async.stop();
		}
	}
	
	private void logIndicationStart() {
		if (isLoggingEnabled()) {
			OM.LOG.info(MessageFormat.format("{0} >>> {1}.{2}({3}): {4}", 
					getClientName(),
					getSimpleClassName(), 
					serviceMethod.getName(), 
					getParameterString(),
					getReturnTypeName()));
			
			startTime = System.nanoTime();
		}
	}

	private void logIndicationFinish() {
		if (isLoggingEnabled()) {
			final long endTime = System.nanoTime();
			
			OM.LOG.info(MessageFormat.format("{0} <<< {1}.{2}({3}): {4} [{5} ms]", 
					getClientName(),
					getSimpleClassName(), 
					serviceMethod.getName(), 
					getParameterString(),
					serviceMethod.getReturnType().getSimpleName(),
					TimeUnit.NANOSECONDS.toMillis(endTime - startTime)));
		}
	}

	private String getClientName() {
		
		try {
			final IChannelMultiplexer multiplexer = getProtocol().getChannel().getMultiplexer();
			return multiplexer.toString();
		} catch (final NullPointerException | LifecycleException ignored) {
			return "[Unknown client]";
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