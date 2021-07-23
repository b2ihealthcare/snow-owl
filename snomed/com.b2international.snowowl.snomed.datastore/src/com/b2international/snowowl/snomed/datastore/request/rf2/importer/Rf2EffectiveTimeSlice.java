/*
 * Copyright 2017-2021 B2i Healthcare Pte Ltd, http://b2i.sg
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
package com.b2international.snowowl.snomed.datastore.request.rf2.importer;

import static com.google.common.collect.Lists.newArrayListWithExpectedSize;
import static com.google.common.collect.Maps.newHashMapWithExpectedSize;
import static com.google.common.collect.Sets.newHashSet;

import java.time.LocalDate;
import java.util.*;

import org.mapdb.DB;
import org.mapdb.HTreeMap;
import org.mapdb.Serializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.b2international.collections.PrimitiveMaps;
import com.b2international.collections.PrimitiveSets;
import com.b2international.collections.longs.LongIterator;
import com.b2international.collections.longs.LongKeyMap;
import com.b2international.collections.longs.LongSet;
import com.b2international.commons.collect.LongSets;
import com.b2international.commons.graph.LongTarjan;
import com.b2international.snowowl.core.ResourceURI;
import com.b2international.snowowl.core.date.DateFormats;
import com.b2international.snowowl.core.date.EffectiveTimes;
import com.b2international.snowowl.core.domain.BranchContext;
import com.b2international.snowowl.core.domain.IComponent;
import com.b2international.snowowl.core.internal.locks.DatastoreLockContextDescriptions;
import com.b2international.snowowl.core.request.ResourceRequests;
import com.b2international.snowowl.core.request.io.ImportDefectAcceptor.ImportDefectBuilder;
import com.b2international.snowowl.core.terminology.ComponentCategory;
import com.b2international.snowowl.core.uri.ComponentURI;
import com.b2international.snowowl.snomed.cis.SnomedIdentifiers;
import com.b2international.snowowl.snomed.common.SnomedTerminologyComponentConstants;
import com.b2international.snowowl.snomed.core.domain.*;
import com.b2international.snowowl.snomed.core.domain.refset.SnomedReferenceSetMember;
import com.b2international.snowowl.snomed.datastore.index.entry.SnomedConceptDocument;
import com.b2international.snowowl.snomed.datastore.index.entry.SnomedDescriptionIndexEntry;
import com.b2international.snowowl.snomed.datastore.index.entry.SnomedDocument;
import com.b2international.snowowl.snomed.datastore.index.entry.SnomedRelationshipIndexEntry;
import com.google.common.base.Stopwatch;
import com.google.common.collect.HashMultimap;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Multimap;

/**
 * @since 6.0
 */
public final class Rf2EffectiveTimeSlice {
	
	private static final Logger LOG = LoggerFactory.getLogger("import");
	private static final int BATCH_SIZE = 5000;

	public static final String SNAPSHOT_SLICE = "snapshot";

	private final LocalDate effectiveDate;
	private final String effectiveTime;
	
	private final LongKeyMap<Set<String>> membersByReferencedComponent;
	private final LongKeyMap<LongSet> dependenciesByComponent;
	
	// tmp map to quickly collect batch of items before flushing it to disk
	private final Map<String, String[]> tmpComponentsById;
	private final HTreeMap<String, String[]> componentsById;
	private final boolean loadOnDemand;
	
	public Rf2EffectiveTimeSlice(DB db, String effectiveTime, boolean loadOnDemand) {
		if (EffectiveTimes.UNSET_EFFECTIVE_TIME_LABEL.equals(effectiveTime) || SNAPSHOT_SLICE.equals(effectiveTime)) {
			this.effectiveDate = null;
			this.effectiveTime = effectiveTime;
		} else {
			this.effectiveDate = EffectiveTimes.parse(effectiveTime, DateFormats.SHORT);
			this.effectiveTime = EffectiveTimes.format(effectiveDate, DateFormats.DEFAULT);
		}

		this.componentsById = db.hashMap(effectiveTime, Serializer.STRING, Serializer.ELSA).create();
		this.tmpComponentsById = newHashMapWithExpectedSize(BATCH_SIZE);
		this.dependenciesByComponent = PrimitiveMaps.newLongKeyOpenHashMap();
		this.membersByReferencedComponent = PrimitiveMaps.newLongKeyOpenHashMap();
		this.loadOnDemand = loadOnDemand;
	}
	
	public Map<String, String[]> getContent() {
		return componentsById;
	}
	
	public String getEffectiveTime() {
		return effectiveTime;
	}
	
	private <T extends SnomedComponent> T getComponent(String componentId) {
		final String[] valuesWithType = componentsById.get(componentId);

		// skip non-RF2 componentIds
		if (valuesWithType == null) {
			return null;
		}
		
		for (Rf2ContentType<?> resolver : Rf2Format.getContentTypes()) {
			if (valuesWithType[0].equals(resolver.getType())) {
				String[] values = new String[valuesWithType.length - 1];
				System.arraycopy(valuesWithType, 1, values, 0, valuesWithType.length - 1);
				return (T) resolver.resolve(values);
			}
		}
		
		throw new IllegalArgumentException("Unrecognized RF2 component: " + componentId + " - " + Arrays.toString(valuesWithType));
	}
	
	public void register(String containerId, Rf2ContentType<?> type, String[] values, ImportDefectBuilder defectBuilder) {
		
		String[] valuesWithType = new String[values.length + 1];
		valuesWithType[0] = type.getType();
		System.arraycopy(values, 0, valuesWithType, 1, values.length);

		final String componentId = values[0];
		final long containerIdL = Long.parseLong(containerId);

		// track refset members via membersByReferencedComponent map
		if (Rf2RefSetContentType.class.isAssignableFrom(type.getClass())) {
			if (!membersByReferencedComponent.containsKey(containerIdL)) {
				membersByReferencedComponent.put(containerIdL, newHashSet());
			}
			membersByReferencedComponent.get(containerIdL).add(componentId);
		} else {
			// register other non-concept components in the dependency graph to force strongly connected subgraphs
			if (!IComponent.ROOT_ID.equals(containerId)) {
				registerDependencies(containerIdL, PrimitiveSets.newLongOpenHashSet(Long.parseLong(componentId)));
			}
		}
		
		type.validate(defectBuilder, values);
		
		tmpComponentsById.put(componentId, valuesWithType);
		if (tmpComponentsById.size() >= BATCH_SIZE) {
			flush();
		}
	}
	
	public void registerDependencies(long componentId, LongSet dependencies) {
		if (!dependenciesByComponent.containsKey(componentId)) {
			dependenciesByComponent.put(componentId, dependencies);
		} else {
			dependenciesByComponent.get(componentId).addAll(dependencies);
		}
	}
	
	public void unregisterDependencies(String componentId) {
		dependenciesByComponent.remove(Long.valueOf(componentId));
	}
	
	public LongKeyMap<LongSet> getDependenciesByComponent() {
		return dependenciesByComponent;
	}
	
	public LongKeyMap<Set<String>> getMembersByReferencedComponent() {
		return membersByReferencedComponent;
	}

	public void flush() {
		if (!tmpComponentsById.isEmpty()) {
			componentsById.putAll(tmpComponentsById);
		}
		tmpComponentsById.clear();
	}

	private List<LongSet> getImportPlan() {
		return new LongTarjan(60000, dependenciesByComponent::get).run(dependenciesByComponent.keySet());
	}
	
	public void doImport(
			final BranchContext context, 
			final ResourceURI codeSystemUri, 
			final Rf2ImportConfiguration importConfig, 
			final ImmutableSet.Builder<ComponentURI> visitedComponents) throws Exception {
		
		final Stopwatch w = Stopwatch.createStarted();
		final String importingMessage = isUnpublishedSlice() ? "Importing unpublished components" : String.format("Importing components from %s", effectiveTime);
		final String commitMessage = isUnpublishedSlice() ? "Imported unpublished components" : String.format("Imported components from %s", effectiveTime);
		final boolean doCreateVersion = !isUnpublishedSlice() && !isSnapshotSlice() && importConfig.isCreateVersions();
		
		LOG.info(importingMessage);
		try (Rf2TransactionContext tx = new Rf2TransactionContext(context.openTransaction(context, DatastoreLockContextDescriptions.IMPORT), loadOnDemand, importConfig)) {
			final Iterator<LongSet> importPlan = getImportPlan().iterator();
			while (importPlan.hasNext()) {
				LongSet componentsToImportInBatch = importPlan.next();
				LongIterator it = componentsToImportInBatch.iterator();
				final Collection<SnomedComponent> componentsToImport = newArrayListWithExpectedSize(componentsToImportInBatch.size());
				while (it.hasNext()) {
					long componentToImportL = it.next();
					String componentToImport = Long.toString(componentToImportL);
					final SnomedComponent component = getComponent(componentToImport);
					if (component != null) {
						componentsToImport.add(component);
						
						// Register container concept as visited component 
						final String conceptId = getConceptId(component); 
						visitedComponents.add(ComponentURI.of(codeSystemUri, SnomedConcept.TYPE, conceptId));
					}
					// add all members of this component to this batch as well
					final Set<String> containerComponents = membersByReferencedComponent.remove(componentToImportL);
					if (containerComponents != null) {
						for (String containedComponentId : containerComponents) {
							SnomedReferenceSetMember containedComponent = getComponent(containedComponentId);
							if (containedComponent != null) {
								componentsToImport.add(containedComponent);
								
								// Register reference set as visited component
								final String refSetId = containedComponent.getRefsetId();
								visitedComponents.add(ComponentURI.of(codeSystemUri, SnomedConcept.REFSET_TYPE, refSetId));
							}
						}
					}
				}
				
				tx.add(componentsToImport, getDependencies(componentsToImport));
				tx.commit(commitMessage);
			}
			
			if (doCreateVersion) {
				ResourceRequests.prepareNewVersion()
					.setResource(codeSystemUri)
					.setVersion(effectiveTime)
					.setDescription("")
					.setEffectiveTime(effectiveDate)
					.buildAsync()
					.getRequest()
					.execute(context);
			}
		}
		LOG.info("{} in {}", commitMessage, w);
	}

	private String getConceptId(SnomedComponent component) {
		if (component instanceof SnomedConcept) {
			return component.getId();
		} else if (component instanceof SnomedDescription) {
			return ((SnomedDescription) component).getConceptId();
		} else if (component instanceof SnomedRelationship) {
			return ((SnomedRelationship) component).getSourceId();
		}
		
		return null;
	}
	
	private boolean isUnpublishedSlice() {
		return EffectiveTimes.UNSET_EFFECTIVE_TIME_LABEL.equals(effectiveTime);
	}
	
	private boolean isSnapshotSlice() {
		return SNAPSHOT_SLICE.equals(effectiveTime);
	}
	
	private Multimap<Class<? extends SnomedDocument>, String> getDependencies(Collection<SnomedComponent> componentsToImport) {
		final Multimap<Class<? extends SnomedDocument>, String> dependenciesByComponent = HashMultimap.create();
		for (SnomedComponent component : componentsToImport) {
			final long sourceId;
			if (component instanceof SnomedCoreComponent) {
				sourceId = Long.parseLong(component.getId());
			} else if (component instanceof SnomedReferenceSetMember) {
				sourceId = Long.parseLong(((SnomedReferenceSetMember) component).getReferencedComponent().getId());
			} else {
				throw new UnsupportedOperationException("Unsupported component type " + component);
			}
			LongSet dependencies = this.dependenciesByComponent.get(sourceId);
			if (dependencies != null) {
				Set<String> requiredDependencies = LongSets.toStringSet(dependencies);
				for (String requiredDependency : requiredDependencies) {
					dependenciesByComponent.put(getCdoType(requiredDependency), requiredDependency);
				}
			}
		}
		return dependenciesByComponent;
	}

	private Class<? extends SnomedDocument> getCdoType(String componentId) {
		ComponentCategory type = SnomedIdentifiers.getComponentCategory(componentId);
		switch (type) {
		case CONCEPT: return SnomedConceptDocument.class;
		case DESCRIPTION: return SnomedDescriptionIndexEntry.class;
		case RELATIONSHIP: return SnomedRelationshipIndexEntry.class;
		default: throw new UnsupportedOperationException(String.format("Cannot determine document type from component ID and type: [%s,%s]", componentId, type));
		}
	}

}