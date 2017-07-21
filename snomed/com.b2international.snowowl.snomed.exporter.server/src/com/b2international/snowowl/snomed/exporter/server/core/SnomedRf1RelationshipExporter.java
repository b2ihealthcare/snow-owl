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
package com.b2international.snowowl.snomed.exporter.server.core;

import static com.b2international.commons.StringUtils.valueOfOrEmptyString;
import static com.b2international.snowowl.core.ApplicationContext.getServiceForClass;
import static com.b2international.snowowl.snomed.common.SnomedTerminologyComponentConstants.RELATIONSHIP_NUMBER;
import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Suppliers.memoize;

import java.util.Iterator;
import java.util.Map;

import com.b2international.snowowl.core.ApplicationContext;
import com.b2international.snowowl.core.domain.IComponent;
import com.b2international.snowowl.eventbus.IEventBus;
import com.b2international.snowowl.snomed.SnomedConstants.Concepts;
import com.b2international.snowowl.snomed.core.domain.ISnomedRelationship;
import com.b2international.snowowl.snomed.core.domain.SnomedRelationships;
import com.b2international.snowowl.snomed.datastore.request.SnomedRequests;
import com.b2international.snowowl.snomed.datastore.services.ISnomedComponentService;
import com.b2international.snowowl.snomed.datastore.services.ISnomedComponentService.IdStorageKeyPair;
import com.b2international.snowowl.snomed.exporter.server.ComponentExportType;
import com.b2international.snowowl.snomed.exporter.server.Id2Rf1PropertyMapper;
import com.b2international.snowowl.snomed.exporter.server.SnomedReleaseFileHeaders;
import com.b2international.snowowl.snomed.exporter.server.SnomedRf1Exporter;
import com.b2international.snowowl.snomed.exporter.server.SnomedRfFileNameBuilder;
import com.b2international.snowowl.snomed.exporter.server.sandbox.SnomedExportConfiguration;
import com.google.common.base.Function;
import com.google.common.base.Supplier;
import com.google.common.collect.AbstractIterator;
import com.google.common.collect.Maps;

/**
 * RF1 exporter for relationships.
 *
 */
public class SnomedRf1RelationshipExporter implements SnomedRf1Exporter {

	private final Id2Rf1PropertyMapper mapper;
	private final SnomedExportConfiguration configuration;
	private final Supplier<Iterator<String>> itrSupplier;

	private Map<String, ISnomedRelationship> relationships;

	public SnomedRf1RelationshipExporter(final SnomedExportConfiguration configuration, final Id2Rf1PropertyMapper mapper) {
		this.configuration = checkNotNull(configuration, "configuration");
		this.mapper = checkNotNull(mapper, "mapper");
		itrSupplier = createSupplier();
		initMaps(configuration.getCurrentBranchPath().getPath());
	}
	
	private void initMaps(String path) {
		
		relationships = SnomedRequests.prepareSearchRelationship()
			.all()
			.build(path)
			.execute(ApplicationContext.getServiceForClass(IEventBus.class))
			.then(new Function<SnomedRelationships, Map<String, ISnomedRelationship>>() {
				@Override
				public Map<String, ISnomedRelationship> apply(SnomedRelationships input) {
					return Maps.uniqueIndex(input, IComponent.ID_FUNCTION);
				}
			}).getSync();
		
	}

	private Supplier<Iterator<String>> createSupplier() {
		return memoize(new Supplier<Iterator<String>>() {
			@Override
			public Iterator<String> get() {
				return new AbstractIterator<String>() {
					
					private final Iterator<IdStorageKeyPair> idIterator = getServiceForClass(ISnomedComponentService.class)
							.getAllComponentIdStorageKeys(configuration.getCurrentBranchPath(), RELATIONSHIP_NUMBER).iterator();
					
					private Object[] _values;
					
					@Override
					protected String computeNext() {
						
						while (idIterator.hasNext()) {
							
							_values = new Object[7];
							
							final String relationshipId = idIterator.next().getId();
							
							ISnomedRelationship relationship = relationships.get(relationshipId);
							
							_values[0] = relationship.getId();
							_values[1] = relationship.getSourceId();
							_values[2] = relationship.getTypeId();
							_values[3] = relationship.getDestinationId();
							_values[4] = relationship.getCharacteristicType().getConceptId();
							_values[5] = Concepts.OPTIONAL_REFINABLE;
							_values[6] = relationship.getGroup();
							
							return new StringBuilder(valueOfOrEmptyString(_values[0])) // ID
								.append(HT)
								.append(valueOfOrEmptyString(_values[1])) // source
								.append(HT)
								.append(valueOfOrEmptyString(_values[2])) // type
								.append(HT)
								.append(valueOfOrEmptyString(_values[3])) // destination
								.append(HT)
								.append(checkNotNull(mapper.getRelationshipType(valueOfOrEmptyString(_values[4])))) // characteristic type
								.append(HT)
								.append(checkNotNull(mapper.getRefinabilityType(valueOfOrEmptyString(_values[5])))) // refinability
								.append(HT)
								.append(valueOfOrEmptyString(_values[6])) //group
								.toString();
						}
						
						return endOfData();
					}
				};
			}
		});
	}
	
	@Override
	public String getRelativeDirectory() {
		return RF1_CORE_RELATIVE_DIRECTORY;
	}

	@Override
	public String getFileName() {
		return SnomedRfFileNameBuilder.buildCoreRf1FileName(getType(), configuration);
	}

	@Override
	public ComponentExportType getType() {
		return ComponentExportType.RELATIONSHIP;
	}

	@Override
	public String[] getColumnHeaders() {
		return SnomedReleaseFileHeaders.RF1_RELATIONSHIP_HEADER;
	}

	@Override
	public boolean hasNext() {
		return itrSupplier.get().hasNext();
	}

	@Override
	public String next() {
		return itrSupplier.get().next();
	}

	@Override
	public void remove() {
		throw new UnsupportedOperationException();
	}

	@Override
	public Iterator<String> iterator() {
		return itrSupplier.get();
	}

	@Override
	public void close() throws Exception {
		//intentionally ignored
	}
	
	@Override
	public SnomedExportConfiguration getConfiguration() {
		return configuration;
	}
	
}