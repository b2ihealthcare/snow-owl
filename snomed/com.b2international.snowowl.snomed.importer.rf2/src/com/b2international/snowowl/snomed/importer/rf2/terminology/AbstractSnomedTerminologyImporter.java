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
package com.b2international.snowowl.snomed.importer.rf2.terminology;

import java.io.InputStream;
import java.text.MessageFormat;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.Set;
import java.util.function.Function;

import org.eclipse.core.runtime.SubMonitor;
import org.eclipse.emf.spi.cdo.InternalCDOTransaction;

import com.b2international.commons.CompareUtils;
import com.b2international.snowowl.core.ApplicationContext;
import com.b2international.snowowl.core.api.SnowowlServiceException;
import com.b2international.snowowl.eventbus.IEventBus;
import com.b2international.snowowl.snomed.Component;
import com.b2international.snowowl.snomed.Concept;
import com.b2international.snowowl.snomed.datastore.SnomedDatastoreActivator;
import com.b2international.snowowl.snomed.datastore.id.ISnomedIdentifierService;
import com.b2international.snowowl.snomed.datastore.request.SnomedRequests;
import com.b2international.snowowl.snomed.importer.ImportAction;
import com.b2international.snowowl.snomed.importer.rf2.csv.AbstractTerminologyComponentRow;
import com.b2international.snowowl.snomed.importer.rf2.model.AbstractSnomedImporter;
import com.b2international.snowowl.snomed.importer.rf2.model.SnomedImportConfiguration;
import com.b2international.snowowl.snomed.importer.rf2.model.SnomedImportContext;
import com.google.common.collect.Iterables;
import com.google.common.collect.Sets;

public abstract class AbstractSnomedTerminologyImporter<T extends AbstractTerminologyComponentRow, C extends Component> extends AbstractSnomedImporter<T, C> {

	private Set<String> componentIdsToRegister;
	private boolean importSupported;

	protected AbstractSnomedTerminologyImporter(final SnomedImportConfiguration<T> importConfiguration, 
			final SnomedImportContext importContext, 
			final InputStream releaseFileStream, 
			final String releaseFileIdentifier) {
		
		super(importConfiguration, importContext, releaseFileStream, releaseFileIdentifier);
		this.importSupported = ApplicationContext.getInstance().getServiceChecked(ISnomedIdentifierService.class).importSupported();
	}

	@Override
	protected ImportAction commit(final SubMonitor subMonitor, final String formattedEffectiveTime) {
		final ImportAction action = super.commit(subMonitor, formattedEffectiveTime);
		getComponentLookup().registerNewComponentStorageKeys();
		return action;
	}
	
	protected ComponentLookup<Component> getComponentLookup() {
		return getImportContext().getComponentLookup();
	}
	
	@Override
	protected Function<T, String> getRowIdMapper() {
		return AbstractTerminologyComponentRow::getId;
	}
	
	@Override
	protected final Collection<C> loadComponents(Set<String> componentIds) {
		return (Collection<C>) getComponents(componentIds);
	}
	
	@Override
	protected final C getNewComponent(String componentId) {
		return (C) getComponentLookup().getNewComponent(componentId);
	}
	
	@Override
	protected void registerNewComponent(C component) {
		getComponentLookup().addNewComponent(component, component.getId());
		if (importSupported) {
			if (componentIdsToRegister == null) {
				componentIdsToRegister = Sets.newHashSet();
			}
			componentIdsToRegister.add(component.getId());
		}
	}
	
	@Override
	protected Date getComponentEffectiveTime(C editedComponent) {
		return editedComponent.getEffectiveTime();
	}
	
	@Override
	protected void preCommit(final InternalCDOTransaction transaction) throws SnowowlServiceException {
		if (importSupported && !CompareUtils.isEmpty(componentIdsToRegister)) {
			SnomedRequests.identifiers().prepareRegister()
					.setComponentIds(componentIdsToRegister)
					.build(SnomedDatastoreActivator.REPOSITORY_UUID)
					.execute(ApplicationContext.getServiceForClass(IEventBus.class))
					.getSync();

			componentIdsToRegister = null;
		}

		super.preCommit(transaction);
	}
	
	@Override
	protected final Function<C, String> getComponentIdMapper() {
		return Component::getId;
	}

	@Override
	protected C createComponent(String componentId) {
		final C component = createCoreComponent();
		component.setId(componentId);
		return component;
	}
	
	protected abstract C createCoreComponent();

	protected final Concept getConceptSafe(final String conceptId, final String conceptField, final String componentId) {
		final Concept result = (Concept) Iterables.getOnlyElement(getComponents(Collections.singleton(conceptId)), null);
		
		if (null == result) {
			throw new NullPointerException(MessageFormat.format("Concept ''{0}'' for field {1}, {2} ''{3}'' not found.", 
					conceptId, conceptField, getImportConfiguration().getType().getDisplayName(), componentId));
		}
		
		return result;
	}
	
}