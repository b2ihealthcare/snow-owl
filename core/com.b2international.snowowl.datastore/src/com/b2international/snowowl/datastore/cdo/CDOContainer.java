/*
 * Copyright 2011-2017 B2i Healthcare Pte Ltd, http://b2i.sg
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
package com.b2international.snowowl.datastore.cdo;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Strings.nullToEmpty;
import static com.google.common.base.Suppliers.memoize;
import static com.google.common.cache.CacheBuilder.newBuilder;
import static com.google.common.collect.Maps.newHashMap;
import static java.util.Collections.unmodifiableMap;

import java.util.Collections;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import javax.annotation.Nullable;

import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExtensionRegistry;
import org.eclipse.core.runtime.Platform;
import org.eclipse.emf.cdo.common.id.CDOID;
import org.eclipse.emf.cdo.common.model.CDOPackageRegistry;
import org.eclipse.emf.cdo.common.model.CDOPackageUnit;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EClassifier;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.EPackage.Registry;
import org.eclipse.net4j.util.lifecycle.Lifecycle;
import org.eclipse.net4j.util.lifecycle.LifecycleUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.b2international.commons.CompareUtils;
import com.b2international.commons.StringUtils;
import com.b2international.snowowl.core.api.NsUri;
import com.google.common.base.Preconditions;
import com.google.common.base.Strings;
import com.google.common.base.Supplier;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.google.common.collect.Iterables;
import com.google.common.collect.Iterators;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;


/**
 * Basic CDO container implementation.
 *
 */
public abstract class CDOContainer<T extends ICDOManagedItem<T>> extends Lifecycle implements ICDOContainer<T> {
	
	/**Shared logger.*/
	protected static final Logger LOGGER = LoggerFactory.getLogger(CDOContainer.class);
	
	private static final LoadingCache<String, NsUri> CACHE;
	
	static {
		
		CACHE = CacheBuilder.newBuilder().build(new CacheLoader<String, NsUri>() {
			@Override public NsUri load(final String nsUri) throws Exception {
				return StringUtils.isEmpty(nsUri) ? NsUri.NULL_IMPL : new NsUri(nsUri);
			}
		});
		
	}
	
	private static final String REPOSITORY_EXT_ID = "com.b2international.snowowl.datastore.repository";
	private static final String NS_URI_ELEMENT = "nsUri";
	private static final String URI_ATTRIBUTE = "uri";
	private static final String UUID_ATTRIBUTE = "uuid";
	private static final String NAME_ATTRIBUTE = "name";
	private static final String TOOLING_ID_ATTRIBUTE = "toolingId";
	private static final String NAMESPACE_ID_ATTRIBUTE = "namespaceId";
	private static final String META_REPOSITORY_ATTRIBUTE = "meta";
	private static final String DEPENDS_ON_ATTRIBUTE = "dependsOn";

	private final Supplier<Map<String, String>> masterToSlaveMappings = memoize(new Supplier<Map<String, String>>() {
		public Map<String, String> get() {
			final Map<String, String> masterToSlaveMappings = newHashMap();
			for (final String uuid : uuidKeySet()) {
				final T managedItem = getByUuid(uuid);
				final String masterUuid = managedItem.getMasterUuid();
				if (null != masterUuid) {
					masterToSlaveMappings.put(masterUuid, uuid);
				}
			}
			return unmodifiableMap(masterToSlaveMappings);
		}
	});
	
	private final LoadingCache<Class<?>, T> classToItemCache = newBuilder().build(new CacheLoader<Class<?>, T>() {
		public T load(final Class<?> clazz) throws Exception {
			checkNotNull(clazz, "clazz");
			for (final Iterator<T> itr = iterator(); itr.hasNext(); /**/) {
				final T item = itr.next();
				final CDOPackageRegistry packageRegistry = item.getPackageRegistry();
				final CDOPackageUnit[] packageUnits = packageRegistry.getPackageUnits();
				for (final CDOPackageUnit packageUnit : packageUnits) {
					final EPackage[] ePackages = packageUnit.getEPackages(true);
					for (final EPackage ePackage : ePackages) {
						for (final EClassifier classifier : ePackage.getEClassifiers()) {
							if (clazz == classifier.getInstanceClass()) {
								return CDOContainer.this.get(ePackage);
							}
						}
					}
				}
			}
			throw new IllegalArgumentException("Cannot find managed item for class: " + clazz.getName());
		}
	});
	
	/**Mapping between the EClasses and the unique model namespace URIs. Supports lazy loading.*/
	private LoadingCache<EClass, NsUri> eclassToNsUriMap;
	
	/**Mapping between the Ecore model namespace URIs and managed item UUIDs.*/
	private Map<NsUri, String> nsUriToUuidMap;
	
	/**Mapping between the UUID to the actual managed items.*/
	private Map<String, T> uuidToItems;
	
	/**Mapping between the unique repository namespace IDs and the managed item UUIDs.*/
	private Map<Byte, String> namespaceToUuidMap;

	/**Mapping between the Ecore model namespace URIs and the managed items.*/
	private Map<NsUri, T> nsUriToItems;

	@Override
	public T get(final Class<?> clazz) {
		return classToItemCache.getUnchecked(checkNotNull(clazz, "clazz"));
	}
	
	@Override
	public T get(final EPackage ePackage) {
		checkActive();
		Preconditions.checkNotNull(ePackage, "EPackage argument cannot be null.");
		return get(ePackage.getNsURI());
	}
	
	@Override
	public T get(final String nsUri) {
		checkActive();
		return get(getOrCreateNsUri(Preconditions.checkNotNull(nsUri, "Namespace URI argument cannot be null.")));
	}
	
	@Override
	public T get(final NsUri nsUri) {
		checkActive();
		Preconditions.checkNotNull(nsUri, "Namespace URI argument cannot be null.");
		
		final String uuid = nsUriToUuidMap.get(nsUri);
		
		if (StringUtils.isEmpty(uuid)) {
			
			LOGGER.warn("Cannot find matching UUID for namespace URI: " + nsUri);
			return null;
			
		}
		
		final T item = getByUuid(uuid);
		
		if (null == item) {
			
			LOGGER.warn("Cannot find managed item for namespace URI: " + nsUri);
			return null;
		}
		
		LifecycleUtil.checkActive(item);
		
		return item;
	}
	
	@Override
	public T get(final EClass eClass) {
		checkActive();
		Preconditions.checkNotNull(eClass, "EClass argument cannot be null.");
		return get(eClass.getEPackage());
	}

	@Override
	public T get(final long cdoId) {
		
		checkActive();
		
		final byte terminologyNamaspaceId = (byte) (cdoId >> 56L);
		
		final String uuid = namespaceToUuidMap.get(terminologyNamaspaceId);
		
		if (StringUtils.isEmpty(uuid)) {
			
			LOGGER.warn("Cannot find matching UUID for CDO ID: " + cdoId);
			return null;
			
		}
		
		final T item = getByUuid(uuid);
		
		if (null == item) {
			
			LOGGER.warn("Cannot find managed item for CDO ID: " + cdoId);
			return null;
		}
		
		LifecycleUtil.checkActive(item);
		
		return item;
	}
	
	@Override
	public T get(final CDOID cdoId) {
		
		checkActive();
		
		Preconditions.checkNotNull(cdoId, "CDO ID argument cannot be null.");
		Preconditions.checkState(!cdoId.isTemporary(), "CDO ID was temporary.");
		
		return get(CDOIDUtils.asLong(cdoId));
	}
	
	@Override
	public T getByUuid(final String uuid) {
		
		checkActive();
		Preconditions.checkNotNull(uuid, "UUID argument cannot be null.");
		
		return null == uuid ? null : uuidToItems.get(uuid);
	}
	
	@Override
	public Iterator<T> iterator() {
		checkActive();
		return Iterators.unmodifiableIterator(uuidToItems.values().iterator());
	}
	
	@Override
	public Set<String> uuidKeySet() {
		return uuidToItems.keySet();
	}

	@Override
	public boolean isMeta(final String uuid) {
		return getByUuid(checkNotNull(uuid, "uuid")).isMeta();
	}
	
	@Override
	@Nullable public String getMasterUuid(final String uuid) {
		return getByUuid(checkNotNull(uuid, "uuid")).getMasterUuid();
	}
	
	@Override
	@Nullable public String getSlaveUuid(final T managedItem) {
		return getSlaveUuid(checkNotNull(managedItem, "managedItem").getUuid());
	}
	
	@Override
	@Nullable public String getSlaveUuid(final String uuid) {
		return masterToSlaveMappings.get().get(uuid);
	}
	
	/**
	 * (non-API)
	 * <br>
	 * Returns with a collection of {@link NsUri namespace URI}s with the associated managed item.
	 * @param managedItem the managed item.
	 * @return a collection of namespace URIs.
	 */
	public synchronized Iterable<NsUri> getNsUris(final T managedItem) {
		
		Preconditions.checkNotNull(managedItem, "Managed item argument cannot be null.");
		
		final Set<NsUri> $ = Sets.newHashSet();
		
		for (final Entry<NsUri, T> entry : nsUriToItems.entrySet()) {
			
			if (managedItem.equals(entry.getValue())) {
				
				$.add(entry.getKey());
				
			}
			
		}
		
		return Collections.unmodifiableCollection($);
		
	}

	@Override
	protected void doDeactivate() throws Exception {

		LOGGER.info("Deactivating " + this.getClass().getSimpleName() + "...");
		
		if (null != uuidToItems) {
			for (final Iterator<T> itr = Iterators.unmodifiableIterator(uuidToItems.values().iterator()); itr.hasNext(); /* nothing */) {
				itr.next().deactivate();
			}
		}
		
		if (null != nsUriToUuidMap) {
			nsUriToUuidMap.clear();
			nsUriToUuidMap = null;
		}
		
		if (null != eclassToNsUriMap) {
			eclassToNsUriMap.cleanUp();
			eclassToNsUriMap = null;
		}
		
		if (null != namespaceToUuidMap) {
			namespaceToUuidMap.clear();
			namespaceToUuidMap = null;
		}
		
		if (null != nsUriToItems) {
			nsUriToItems = null;
		}
	
		super.doDeactivate();
		LOGGER.info(this.getClass().getSimpleName() + " has been successfully deactivated.");
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.net4j.util.lifecycle.Lifecycle#doBeforeActivate()
	 */
	@Override
	protected void doBeforeActivate() throws Exception {

		final Map<NsUri, T> _nsUriToItems = Maps.newHashMap();
		
		final IExtensionRegistry registry = Platform.getExtensionRegistry();

		final Map<String, String> namespaceIdsByRepositoryId = newHashMap();
		
		//load all CDO repository extensions, instantiate repositories and apply inverse mapping to the namespace URI
		for (final IConfigurationElement repositoryElement : registry.getConfigurationElementsFor(REPOSITORY_EXT_ID)) {
			
			final Set<NsUri> nsUris = Sets.newHashSet();
			
			for (final IConfigurationElement nsUriElement : repositoryElement.getChildren()) {
				
				if (NS_URI_ELEMENT.equals(nsUriElement.getName())) {
					
					nsUris.add(check(new NsUri(nsUriElement.getAttribute(URI_ATTRIBUTE))));
					
				}
				
			}
			
			final String repositoryUuid = Preconditions.checkNotNull(
					repositoryElement.getAttribute(UUID_ATTRIBUTE), "Repository name attribute was null.");
			
			final String _namespaceId = Preconditions.checkNotNull(
					repositoryElement.getAttribute(NAMESPACE_ID_ATTRIBUTE), "Repository namespace argument was null.");

			@Nullable final String toolingId = repositoryElement.getAttribute(TOOLING_ID_ATTRIBUTE);
			@Nullable final String repositoryName = repositoryElement.getAttribute(NAME_ATTRIBUTE); 
			@Nullable final String dependsOnRepositoryUuid = repositoryElement.getAttribute(DEPENDS_ON_ATTRIBUTE);
			final boolean metaRepository = Boolean.parseBoolean(nullToEmpty(repositoryElement.getAttribute(META_REPOSITORY_ATTRIBUTE)));
			
			String existingRepository = namespaceIdsByRepositoryId.put(_namespaceId, repositoryName);
			Preconditions.checkState(Strings.isNullOrEmpty(existingRepository), "Another repository '%s' is already using namespaceId '%s' of '%s' repository.", existingRepository, _namespaceId, repositoryName);
			
			Byte namespaceId = null;

			try {
				
				namespaceId = Byte.decode(_namespaceId);
				Preconditions.checkState(-1 < namespaceId.byteValue(), "Repository namespace ID should be an unsigned byte. Repository: " + repositoryUuid);

			} catch (final NumberFormatException e) {
				
				throw new IllegalStateException("Cannot specify repository namespace ID for repository [" + repositoryUuid +  "]");
				
			}
			
			
			final T managedItem = createItem(repositoryUuid, repositoryName, namespaceId, toolingId, dependsOnRepositoryUuid, metaRepository);
			managedItem.setContainer(this);
			
			for (final NsUri nsUri : nsUris) {
				
				_nsUriToItems.put(nsUri, managedItem);
				
			}
			
		}
		
		nsUriToItems = Collections.unmodifiableMap(_nsUriToItems); 
		
		super.doBeforeActivate();
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.net4j.util.lifecycle.Lifecycle#doActivate()
	 */
	@Override
	protected void doActivate() throws Exception {
		
		LOGGER.info("Activating " + this.getClass().getSimpleName() + "...");

		final Map<String, T> _uuidToItems = Maps.newHashMap();
		final Map<NsUri, String> _nsUriToUuidMap = Maps.newHashMap();
		final Map<Byte, String> _namespaceToUuidMap = Maps.newHashMap();
		
		//initialize mapping after activating managed items one by one
		for (final Iterator<Entry<NsUri, T>> itr = nsUriToItems.entrySet().iterator(); itr.hasNext(); /*  */) {
			
			final Entry<NsUri, T> entry = itr.next();
			
			//activate managed item
			LifecycleUtil.activate(entry.getValue());
			
			final String uuid = entry.getValue().getUuid();
			
			_nsUriToUuidMap.put(entry.getKey(), uuid);
			_uuidToItems.put(uuid, entry.getValue());
			_namespaceToUuidMap.put(entry.getValue().getNamespaceId(), uuid);
			
		}
		
		nsUriToUuidMap = Collections.unmodifiableMap(_nsUriToUuidMap);
		uuidToItems = Collections.unmodifiableMap(_uuidToItems);
		namespaceToUuidMap = Collections.unmodifiableMap(_namespaceToUuidMap);
		
		super.doActivate();
		
		LOGGER.info(this.getClass().getSimpleName() + " has been successfully activated.");
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.net4j.util.lifecycle.Lifecycle#doAfterActivate()
	 */
	@Override
	protected void doAfterActivate() throws Exception {
		
		Preconditions.checkNotNull(uuidToItems, "UUID to repository cache is not initialized yet.");
		Preconditions.checkNotNull(nsUriToUuidMap, "Namespace URI to repository UUID cache is not initialized yet.");
		
		eclassToNsUriMap = CacheBuilder.newBuilder().build(new CacheLoader<EClass, NsUri>() {

			@Override public NsUri load(final EClass eClass) throws Exception {
				
				Preconditions.checkNotNull(eClass, "EClass argument cannot be null.");
				
				final EPackage ePackage = eClass.getEPackage();
				Preconditions.checkNotNull(ePackage, "Cannot specify package for EClass. EClass: '" + eClass + "'.");
				
				final String nsURI = ePackage.getNsURI();
				Preconditions.checkState(!StringUtils.isEmpty(nsURI), "Namespace URI is not specified for EPackage. EPackage: '" + ePackage + "'.");
				
				final NsUri candidateNsUri = getOrCreateNsUri(nsURI);
				for (final Iterator<NsUri> itr = Iterators.unmodifiableIterator(nsUriToUuidMap.keySet().iterator()); itr.hasNext(); /* nothing */) {
					
					final NsUri $ = itr.next();
					
					if ($.equals(candidateNsUri)) {
						
						return $; 
						
					}
					
				}
				
				LOGGER.warn("Unsupported EClass type: '" + eClass + "'.");
				
				return NsUri.NULL_IMPL; 
			}
		});
		
		super.doAfterActivate();
	}

	/**Returns with the first managed item in an indeterministic fashion.*/
	protected final T getFirst() {
		
		checkActive();
		
		if (CompareUtils.isEmpty(iterator())) {
			
			throw new IllegalStateException("Managed items is empty.");
			
		}
		
		final T managedItem = Iterables.get(this, 0);
		LifecycleUtil.checkActive(managedItem);
		
		return managedItem;
	}
	
	/**
	 * (non-API)
	 * 
	 * Returns with an iterator of the managed item. Requires inactive state. 
	 * 
	 * @return
	 */
	public final Iterator<T> _iterator() {
		checkInactive();
		return Iterators.unmodifiableIterator(nsUriToItems.values().iterator());
	}
	
	/**Returns with the unique ID of the managed item*/
	protected abstract String getUuid(final T managedItem);
	
	/**Creates a managed item instance based on the repository UUID, human readable name and the unique namespace ID.*/
	protected abstract T createItem(final String repositoryUuid, @Nullable final String repositoryName, final byte namespaceId, 
			@Nullable final String toolingId, @Nullable final String dependsOnRepositoryUuid, final boolean metaRepository);

	/*checks the existence of the package against the namespace URI*/
	private NsUri check(final NsUri nsUri) {
		Preconditions.checkNotNull(nsUri, "Namespace URI argument cannot be null.");
		Preconditions.checkNotNull(Registry.INSTANCE.getEPackage(nsUri.getNsUri()), "EPackage does not exist for namespace URI: " + nsUri);
		return nsUri;
	}
	
	/*safely returns with the namespace URI instance .*/
	private static NsUri getOrCreateNsUri(final String nsUri) {
		try {
			return CACHE.get(nsUri);
		} catch (final Throwable t) {
			LOGGER.warn("Cannot get the namespace URI from cache. Creating a new instance instead.", t);
			return new NsUri(Strings.nullToEmpty(nsUri));
		}
	}
	
}