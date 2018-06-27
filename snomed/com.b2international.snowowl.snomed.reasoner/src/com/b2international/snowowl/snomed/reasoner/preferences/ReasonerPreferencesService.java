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
package com.b2international.snowowl.snomed.reasoner.preferences;

import java.text.MessageFormat;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.atomic.AtomicInteger;

import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExtension;
import org.eclipse.core.runtime.IExtensionPoint;
import org.eclipse.core.runtime.IExtensionRegistry;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.RegistryFactory;
import org.eclipse.net4j.util.event.Notifier;
import org.osgi.framework.Bundle;
import org.protege.editor.owl.model.inference.ProtegeOWLReasonerInfo;

import com.b2international.commons.CompareUtils;
import com.b2international.commons.StringUtils;
import com.b2international.commons.status.SerializableStatus;
import com.b2international.snowowl.core.ApplicationContext;
import com.b2international.snowowl.snomed.reasoner.exceptions.ReasonerException;
import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;

/**
 * OSGi based preferences service for reasoner configurations.
 * <br>This class is registered to the {@link ApplicationContext application context} as {@link IReasonerPreferencesService}.
 */
public class ReasonerPreferencesService extends Notifier implements IReasonerPreferencesService {

	private static final String PLUGIN_ID = "com.b2international.snowowl.snomed.reasoner";
	
	private static final String N_A = "N/A";
	private static final String NAME_ATTRIBUTE = "name";
	private static final String VALUE_ATTRIBUTE = "value";
	private static final String CLASS_ATTRIBUTE = "class";
	private static final String EXTENSION_POINT_ID = "org.protege.editor.owl.inference_reasonerfactory";

	private static final AtomicInteger ID_PROVIDER = new AtomicInteger();

	private final Map<String, ReasonerMetadata> cache;

	public ReasonerPreferencesService() {

		cache = Maps.newHashMap();

		final IExtensionPoint extensionPoints = Platform.getExtensionRegistry().getExtensionPoint(EXTENSION_POINT_ID);

		for (final IExtension extension : extensionPoints.getExtensions()) {

			String name = N_A;

			for (final IConfigurationElement element : extension.getConfigurationElements()) {
				if (NAME_ATTRIBUTE.equals(element.getName())) {
					final String nameAttribute = element.getAttribute(VALUE_ATTRIBUTE);

					if (!StringUtils.isEmpty(nameAttribute)) {
						name = nameAttribute;
						break;
					}
				}
			}
			
			final Bundle contributorBundle = Platform.getBundle(extension.getContributor().getName());
			final String version = contributorBundle.getVersion().toString();
			final String extensionId = extension.getUniqueIdentifier();

			final ReasonerMetadata metadata = new ReasonerMetadata(ID_PROVIDER.getAndIncrement(), name, version, extensionId);
			cache.put(metadata.getExtensionId(), metadata);
		}
	}

	@Override
	public String toString() {

		final StringBuilder sb = new StringBuilder();
		final List<ReasonerMetadata> values = Lists.newArrayList(cache.values());

		Collections.sort(values);

		if (!CompareUtils.isEmpty(values)) {
			sb.append("\n");
		}

		for (final ReasonerMetadata metadata : values) {
			if (sb.length() > 0) {
				sb.append("\n");
			}
			sb.append("\t");
			sb.append(metadata);
		}

		if (!CompareUtils.isEmpty(values)) {
			sb.append("\n");
		}

		return sb.toString();
	}
	
	@Override
	public Map<String, ReasonerMetadata> getMetadata() {
		return Collections.unmodifiableMap(cache);
	}
	
	@Override
	public ProtegeOWLReasonerInfo createReasonerInfo(final String reasonerId) {
		
		Preconditions.checkNotNull(reasonerId, "Reasoner ID argument cannot be null.");
		final IExtensionRegistry registry = RegistryFactory.getRegistry();
	
		if (registry == null) {
			throw new ReasonerException("Extension registry is not available.");
		}
	
		final IExtensionPoint extensionPoint = registry.getExtensionPoint(EXTENSION_POINT_ID);
		final IExtension[] extensions = extensionPoint.getExtensions();
		for (final IExtension extension : extensions) {
			if (extension.getUniqueIdentifier().equals(reasonerId)) {
				for (final IConfigurationElement configElement: extension.getConfigurationElements()) {
					if (CLASS_ATTRIBUTE.equals(configElement.getName())) {
						try {
							final ProtegeOWLReasonerInfo reasonerInfo = (ProtegeOWLReasonerInfo) configElement.createExecutableExtension(VALUE_ATTRIBUTE);
							reasonerInfo.initialise();
							return reasonerInfo;
						} catch (final Throwable e) {
							throw new ReasonerException(MessageFormat.format("Couldn''t initialize reasoner factory for ID ''{0}''.", reasonerId), e);
						}
					}
				}
			}
		}
	
		throw new ReasonerException(MessageFormat.format("Couldn''t initialize reasoner factory for ID ''{0}''.", reasonerId));
		
	}

	/* (non-Javadoc)
	 * @see com.b2international.snowowl.snomed.reasoner.preferences.IReasonerPreferencesService#checkAllAvailableReasoners()
	 */
	@Override
	public Iterable<IStatus> checkAllAvailableReasoners() {

		final Collection<IStatus>  $ = Sets.newHashSet();
		
		for (final Entry<String, ReasonerMetadata> entry : getMetadata().entrySet()) {
			final String reasonerId = entry.getKey();
			try {
				createReasonerInfo(reasonerId);
			} catch (final Throwable t) {
				$.add(createErrorStatus(reasonerId, t));
			}
		}
		
		return CompareUtils.isEmpty($) ? OK_STATUS : $;
	}

	private static final Iterable<IStatus> OK_STATUS = Collections.<IStatus>singleton(
			new SerializableStatus(IStatus.OK, PLUGIN_ID, "All reasoner instances are available and ready for use."));
	
	private IStatus createErrorStatus(final String reasonerId, final Throwable t) {
		return new SerializableStatus(IStatus.ERROR, reasonerId, MessageFormat.format("Couldn''t initialize reasoner factory for ID ''{0}''.", reasonerId), t);
	}
}
