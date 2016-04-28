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
package com.b2international.snowowl.snomed.importer.rf2.terminology;

import java.io.IOException;
import java.io.InputStream;
import java.util.Collection;
import java.util.Date;
import java.util.Map;

import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.TotalHitCountCollector;
import org.eclipse.core.runtime.SubMonitor;
import org.eclipse.emf.spi.cdo.InternalCDOTransaction;

import com.b2international.collections.PrimitiveMaps;
import com.b2international.collections.longs.LongValueMap;
import com.b2international.snowowl.core.ApplicationContext;
import com.b2international.snowowl.core.api.SnowowlServiceException;
import com.b2international.snowowl.importer.ImportAction;
import com.b2international.snowowl.snomed.Component;
import com.b2international.snowowl.snomed.datastore.id.ISnomedIdentifierService;
import com.b2international.snowowl.snomed.datastore.id.SnomedIdentifiers;
import com.b2international.snowowl.snomed.importer.rf2.csv.AbstractTerminologyComponentRow;
import com.b2international.snowowl.snomed.importer.rf2.model.AbstractSnomedImporter;
import com.b2international.snowowl.snomed.importer.rf2.model.SnomedImportConfiguration;
import com.b2international.snowowl.snomed.importer.rf2.model.SnomedImportContext;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;

public abstract class AbstractSnomedTerminologyImporter<T extends AbstractTerminologyComponentRow, C extends Component> extends AbstractSnomedImporter<T, C> {

	private final Collection<String> componentIdsToRegister = Sets.newHashSet();
	private final Map<String, C> componentById = Maps.newHashMap();
	private final SnomedIdentifiers snomedIdentifiers;

	protected AbstractSnomedTerminologyImporter(final SnomedImportConfiguration<T> importConfiguration, 
			final SnomedImportContext importContext, 
			final InputStream releaseFileStream, 
			final String releaseFileIdentifier) {
		
		super(importConfiguration, importContext, releaseFileStream, releaseFileIdentifier);
		this.snomedIdentifiers = new SnomedIdentifiers(ApplicationContext.getInstance().getServiceChecked(ISnomedIdentifierService.class));
	}

	@Override
	protected ImportAction commit(final SubMonitor subMonitor, final String formattedEffectiveTime) {
		componentById.clear();
		final ImportAction action = super.commit(subMonitor, formattedEffectiveTime);
		getComponentLookup().registerNewComponents();
		return action;
	}
	
	@Override
	protected final LongValueMap<String> getAvailableComponents(IndexSearcher index) throws IOException {
		final Query query = getAvailableComponentsQuery();
		
		final TotalHitCountCollector totalHitCollector = new TotalHitCountCollector();
		index.search(query, totalHitCollector);
		
		if (totalHitCollector.getTotalHits() <= 0) {
			return PrimitiveMaps.newObjectKeyLongOpenHashMap();
		} else {
			final ComponentIdAndEffectiveTimeCollector idTimeCollector = new ComponentIdAndEffectiveTimeCollector(totalHitCollector.getTotalHits());
			index.search(query, idTimeCollector);
			return idTimeCollector.getAvailableComponents();
		}
	}
	
	protected abstract Query getAvailableComponentsQuery();

	protected ComponentLookup<Component> getComponentLookup() {
		return getImportContext().getComponentLookup();
	}
	
	@Override
	protected Date getComponentEffectiveTime(C editedComponent) {
		return editedComponent.getEffectiveTime();
	}
	
	@Override
	protected void preCommit(final InternalCDOTransaction transaction) throws SnowowlServiceException {
		if (snomedIdentifiers.importSupported() && !componentIdsToRegister.isEmpty()) {
			snomedIdentifiers.register(componentIdsToRegister);
			componentIdsToRegister.clear();
		}

		super.preCommit(transaction);
	}

	@Override
	protected void handleCommitException() {
		snomedIdentifiers.rollback();
		super.handleCommitException();
	}
	
	protected abstract C createComponent(String containerId, String componentId);
	
	protected abstract C getComponent(String componentId);
	
	protected final C getOrCreateComponent(final String containerId, final String componentId) {
		C result = componentById.get(componentId);

		if (null != result) {
			return result;
		}

		result = getComponent(componentId);
		
		if (null == result) {
			result = createComponent(containerId, componentId);
			getComponentLookup().addNewComponent(result, componentId);
			if (snomedIdentifiers.importSupported()) {
				componentIdsToRegister.add(componentId);
			}
		}
		
		componentById.put(componentId, result);
		return result;
	}
	
}