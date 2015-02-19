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

import static org.eclipse.net4j.util.CheckUtil.checkNull;

import java.io.ObjectStreamClass;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.net4j.util.factory.ProductCreationException;
import org.eclipse.net4j.util.io.ExtendedIOUtil.ClassResolver;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleReference;

import com.b2international.snowowl.internal.rpc.RpcInput;
import com.b2international.snowowl.internal.rpc.RpcOutput;
import com.b2international.snowowl.internal.rpc.bundle.OM;

/**
 * An abstract implementation for {@link RpcSession} which delegates
 * {@link #resolveClass(ObjectStreamClass)} calls to {@link #getClassByName(String)}.
 * 
 */
public class RpcSessionImpl implements RpcSession {

	/**
	 * 
	 */
	private final class ServiceClassResolver implements ClassResolver {
		
		private final Class<?> serviceClass;
		
		private final ClassLoader classLoader;

		/**
		 * 
		 * @param serviceClass
		 */
		public ServiceClassResolver(final Class<?> serviceClass, final ClassLoader classLoader) {
			this.serviceClass = serviceClass;
			this.classLoader = classLoader;
		}

		public Class<?> getServiceClass() {
			return serviceClass;
		}

		/*
		 * (non-Javadoc)
		 * @see org.eclipse.net4j.util.io.ExtendedIOUtil.ClassResolver#resolveClass(java.io.ObjectStreamClass)
		 */
		public Class<?> resolveClass(final ObjectStreamClass v) throws ClassNotFoundException {
			return getClassByName(v.getName());
		}
		
		/**
		 * 
		 * @param className
		 * @return
		 * @throws ClassNotFoundException
		 */
		public Class<?> getClassByName(final String className) throws ClassNotFoundException {
			
			try {
				return Class.forName(className, true, classLoader);
			} catch (final ClassNotFoundException e) {
				
				final Bundle loadingBundle = getBundle();
				
				if (loadingBundle != null) {
					throw new ClassNotFoundException(e.getMessage() + " not found in bundle " + loadingBundle, e);
				} else {
					throw e;
				}
			}
		}
		
		private Bundle getBundle() {
			
			if (classLoader instanceof BundleReference) {
				return ((BundleReference) classLoader).getBundle();
			} else {
				return null;
			}
		}

	}

	private RpcProtocol protocol;
	
	private final RpcSessionImpl parent;
	private final Map<String, ServiceClassResolver> resolversByServiceClassName = Collections.synchronizedMap(new HashMap<String, ServiceClassResolver>());
	private final List<RpcServiceLookup> lookupList = Collections.synchronizedList(new ArrayList<RpcServiceLookup>());
	private final Map<String, Object> keyValueStore = Collections.synchronizedMap(new HashMap<String, Object>());
	
	/**
	 * Creates a new, empty {@link RpcSessionImpl} instance.
	 */
	public RpcSessionImpl() {
		parent = this;
		
		// Register proxied client classes on this bundle's class loader
		registerClassLoader(IProgressMonitor.class, OM.Activator.class.getClassLoader());
		registerClassLoader(RpcInput.class, OM.Activator.class.getClassLoader());
		registerClassLoader(RpcOutput.class, OM.Activator.class.getClassLoader());
	}
	
	/**
	 * Creates a new instance based on an existing {@link RpcSessionImpl} instance's service configuration data (but not the session
	 * values).
	 * @param source the source to copy service class lookup and invocation data from
	 */
	public RpcSessionImpl(final RpcSessionImpl source) {
		checkNull(source, "Source session parameter may not be null.");
		parent = source;
	}
	
	/*
	 * (non-Javadoc)
	 * @see com.b2international.snowowl.rpc.RpcSession#size()
	 */
	public int size() {
		return (this == parent) 
				? keyValueStore.size() 
				: keyValueStore.size() + parent.size();
	}

	/*
	 * (non-Javadoc)
	 * @see com.b2international.snowowl.rpc.RpcSession#isEmpty()
	 */
	public boolean isEmpty() {
		return (this == parent) 
				? keyValueStore.isEmpty() 
				: keyValueStore.isEmpty() && parent.isEmpty();
	}

	/*
	 * (non-Javadoc)
	 * @see com.b2international.snowowl.rpc.RpcSession#containsKey(java.lang.String)
	 */
	public boolean containsKey(final String key) {
		return (this == parent) 
				? keyValueStore.containsKey(key) 
				: keyValueStore.containsKey(key) || parent.containsKey(key);
	}

	/*
	 * (non-Javadoc)
	 * @see com.b2international.snowowl.rpc.RpcSession#get(java.lang.String)
	 */
	public Object get(final String key) {
		
		final Object value = keyValueStore.get(key);
		
		if (null != value) {
			return value;
		} else if (this != parent) {
			return parent.get(key);
		} else {
			return null;
		}
	}

	/*
	 * (non-Javadoc)
	 * @see com.b2international.snowowl.rpc.RpcSession#put(java.lang.String, java.lang.Object)
	 */
	public Object put(final String key, final Object value) {
		
		final Object oldValue = keyValueStore.put(key, value);
		
		if (null != oldValue) {
			return oldValue;
		} else if (this != parent) {
			return parent.get(key);
		} else {
			return null;
		}
	}

	/*
	 * (non-Javadoc)
	 * @see com.b2international.snowowl.rpc.RpcSession#remove(java.lang.String)
	 */
	public Object remove(String key) {
		
		/*
		 * XXX: Key assignments on the current map can only be removed through this method, so is it a problem if two threads attempt to
		 * remove the same key, both find that containsKey(...) returns true, and because of this, the request does not get forwarded to the
		 * parent session?
		 */
		if (keyValueStore.containsKey(key)) {
			return keyValueStore.remove(key);
		} else if (this != parent) {
			return parent.remove(key);
		} else {
			return null;
		}
	}
	
	/*
	 * (non-Javadoc)
	 * @see com.b2international.snowowl.rpc.RpcSession#keySet()
	 */
	public Set<String> keySet() {
		final Set<String> keys = new HashSet<String>();
		collectKeys(keys);
		return keys;
	}
	
	private void collectKeys(final Set<String> keys) {

		keys.addAll(keyValueStore.keySet());
		
		if (this != parent) {
			parent.collectKeys(keys);
		}
	}

	/*
	 * (non-Javadoc)
	 * @see com.b2international.snowowl.rpc.RpcSession#registerClassLoader(java.lang.Class, java.lang.ClassLoader)
	 */
	public void registerClassLoader(final Class<?> serviceClass, final ClassLoader classLoader) {
		synchronized (resolversByServiceClassName) {
			final String serviceClassName = serviceClass.getName();
			if (!isClassLoaderPresent(serviceClassName)) {
				resolversByServiceClassName.put(serviceClassName, new ServiceClassResolver(serviceClass, classLoader));
			}
		}
	}

	private boolean isClassLoaderPresent(final String serviceClassName) {
		return (this == parent)
				? resolversByServiceClassName.containsKey(serviceClassName)
				: resolversByServiceClassName.containsKey(serviceClassName) || parent.isClassLoaderPresent(serviceClassName);
	}

	/*
	 * (non-Javadoc)
	 * @see com.b2international.snowowl.rpc.RpcSession#registerServiceLookup(com.b2international.snowowl.rpc.RpcServiceLookup)
	 */
	public void registerServiceLookup(final RpcServiceLookup lookup) {
		synchronized (lookupList) {
			if (!isLookupPresent(lookup)) {
				lookupList.add(lookup);
			}
		}
	}

	private boolean isLookupPresent(final RpcServiceLookup lookup) {
		return (this == parent)
				? lookupList.contains(lookup)
				: lookupList.contains(lookup) || parent.isLookupPresent(lookup);
	}
	
	/*
	 * (non-Javadoc)
	 * @see com.b2international.snowowl.rpc.RpcSession#getClassResolver(java.lang.String)
	 */
	public ServiceClassResolver getClassResolver(final String serviceClassName) {
		
		final ServiceClassResolver value = resolversByServiceClassName.get(serviceClassName);
		
		if (null != value) {
			return value;
		} else if (this != parent) {
			return parent.getClassResolver(serviceClassName);
		} else {
			throw new IllegalArgumentException(MessageFormat.format("No class resolver has been registered for service class ''{0}''.", serviceClassName));
		}
	}
	
	/*
	 * (non-Javadoc)
	 * @see com.b2international.snowowl.rpc.RpcSession#getServiceClassByName(java.lang.String)
	 */
	public Class<?> getServiceClassByName(final String serviceClassName) throws ClassNotFoundException {
		return getClassResolver(serviceClassName).getServiceClass();
	}
	
	/*
	 * (non-Javadoc)
	 * @see com.b2international.snowowl.rpc.RpcSession#getClassByName(java.lang.String, java.lang.String)
	 */
	public Class<?> getClassByName(final String serviceClassName, final String className) throws ClassNotFoundException {
		return getClassResolver(serviceClassName).getClassByName(className);
	}

	/*
	 * (non-Javadoc)
	 * @see com.b2international.snowowl.rpc.RpcServiceLookup#getServiceImplementation(java.lang.Class)
	 */
	public <T> T getServiceImplementation(final Class<T> serviceClass) {

		synchronized (lookupList) {
			for (final RpcServiceLookup delegate : lookupList) {
				
				final T implementation = delegate.getServiceImplementation(serviceClass);
				
				if (implementation != null) {
					return implementation;
				}
			}
		}
		
		return (this == parent) ? null : parent.getServiceImplementation(serviceClass);
	}
	
	public void setProtocol(final RpcProtocol protocol) {
		
		checkNull(protocol, "RPC protocol parameter may not be null.");
		if (this.protocol != null) {
			throw new IllegalStateException("RPC protocol has already been set for this session.");
		}
		
		this.protocol = protocol;
	}
	
	/*
	 * (non-Javadoc)
	 * @see com.b2international.snowowl.rpc.RpcSession#getProtocol()
	 */
	public RpcProtocol getProtocol() {
		return protocol;
	}
	
	/**
	 * 
	 */
	public static final class Factory extends org.eclipse.net4j.util.factory.Factory {

		public static final String PRODUCT_GROUP = "com.b2international.snowowl.rpc";

		public static final String TYPE = "RpcSession";
		
		public Factory() {
			super(PRODUCT_GROUP, TYPE);
		}

		/*
		 * (non-Javadoc)
		 * @see org.eclipse.net4j.util.factory.IFactory#create(java.lang.String)
		 */
		public Object create(final String description) throws ProductCreationException {
			return new RpcSessionImpl();
		}
	}
}