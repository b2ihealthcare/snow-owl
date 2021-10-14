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

import java.math.BigDecimal;
import java.util.*;

import org.mapdb.DB;
import org.mapdb.HTreeMap;
import org.mapdb.Serializer;

import com.b2international.collections.PrimitiveMaps;
import com.b2international.collections.PrimitiveSets;
import com.b2international.collections.longs.*;
import com.b2international.commons.collect.LongSets;
import com.b2international.commons.graph.LongTarjan;
import com.b2international.commons.options.Options;
import com.b2international.snowowl.core.ResourceURI;
import com.b2international.snowowl.core.domain.BranchContext;
import com.b2international.snowowl.core.domain.IComponent;
import com.b2international.snowowl.core.domain.TransactionContext;
import com.b2international.snowowl.core.internal.locks.DatastoreLockContextDescriptions;
import com.b2international.snowowl.core.request.ResourceRequests;
import com.b2international.snowowl.core.request.io.ImportDefectAcceptor.ImportDefectBuilder;
import com.b2international.snowowl.core.terminology.ComponentCategory;
import com.b2international.snowowl.core.uri.ComponentURI;
import com.b2international.snowowl.snomed.cis.SnomedIdentifiers;
import com.b2international.snowowl.snomed.common.SnomedConstants.Concepts;
import com.b2international.snowowl.snomed.common.SnomedRf2Headers;
import com.b2international.snowowl.snomed.core.domain.*;
import com.b2international.snowowl.snomed.core.domain.refset.SnomedReferenceSetMember;
import com.b2international.snowowl.snomed.datastore.index.entry.*;
import com.b2international.snowowl.snomed.datastore.request.SnomedRequests;
import com.google.common.base.Stopwatch;
import com.google.common.collect.HashMultimap;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Iterables;
import com.google.common.collect.Multimap;

/**
 * @since 6.0
 */
final class MapDBRf2EffectiveTimeSlice extends BaseRf2EffectiveTimeSlice {
	
	private final LongKeyMap<Set<String>> membersByReferencedComponent;
	private final LongKeyMap<LongSet> dependenciesByComponent;
	
	// tmp map to quickly collect batch of items before flushing it to disk
	private final Map<String, String[]> tmpComponentsById;
	private final HTreeMap<String, String[]> componentsById;
	private final boolean loadOnDemand;
	
	public MapDBRf2EffectiveTimeSlice(String effectiveTime, DB db, boolean loadOnDemand) {
		super(effectiveTime);
		this.componentsById = db.hashMap(effectiveTime, Serializer.STRING, Serializer.ELSA).create();
		this.tmpComponentsById = newHashMapWithExpectedSize(BATCH_SIZE);
		this.dependenciesByComponent = PrimitiveMaps.newLongKeyOpenHashMap();
		this.membersByReferencedComponent = PrimitiveMaps.newLongKeyOpenHashMap();
		this.loadOnDemand = loadOnDemand;
	}
	
	@Override
	public Map<String, String[]> getContent() {
		return componentsById;
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
	
	@Override
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
	
	@Override
	public void registerDependencies(long componentId, LongSet dependencies) {
		if (!dependenciesByComponent.containsKey(componentId)) {
			dependenciesByComponent.put(componentId, dependencies);
		} else {
			dependenciesByComponent.get(componentId).addAll(dependencies);
		}
	}
	
	@Override
	public void unregisterDependencies(String componentId) {
		dependenciesByComponent.remove(Long.valueOf(componentId));
	}
	
	@Override
	public LongKeyMap<LongSet> getDependenciesByComponent() {
		return dependenciesByComponent;
	}
	
	@Override
	public LongKeyMap<Set<String>> getMembersByReferencedComponent() {
		return membersByReferencedComponent;
	}

	@Override
	public void flush() {
		if (!tmpComponentsById.isEmpty()) {
			componentsById.putAll(tmpComponentsById);
		}
		tmpComponentsById.clear();
	}

	private List<LongSet> getImportPlan() {
		return new LongTarjan(60000, dependenciesByComponent::get).run(dependenciesByComponent.keySet());
	}
	
	@Override
	public void doImport(
			final BranchContext context, 
			final ResourceURI codeSystemUri, 
			final Rf2ImportConfiguration importConfig, 
			final ImmutableSet.Builder<ComponentURI> visitedComponents) throws Exception {
		
		final Stopwatch w = Stopwatch.createStarted();
		final String importingMessage = isUnpublishedSlice() ? "Importing unpublished components" : String.format("Importing components from %s", getEffectiveTime());
		final String commitMessage = isUnpublishedSlice() ? "Imported unpublished components" : String.format("Imported components from %s", getEffectiveTime());
		final boolean doCreateVersion = !isUnpublishedSlice() && !isSnapshotSlice() && importConfig.isCreateVersions();
		
		// Collect the type ID for all integer relationship values
		final LongKeyLongMap integerTypeIdsByValueId = PrimitiveMaps.newLongKeyLongOpenHashMap();
		
		context.log().info(importingMessage);
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
						
						// Record value types across the entire effective time slice
						if (component instanceof SnomedRelationship) {
							final SnomedRelationship relationship = (SnomedRelationship) component;
							final RelationshipValue relationshipValue = relationship.getValueAsObject();
							if (relationshipValue != null) {
								if (RelationshipValueType.INTEGER.equals(relationshipValue.type())) {
									integerTypeIdsByValueId.put(componentToImportL, Long.parseLong(relationship.getTypeId()));
								}
							}
						}
						
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
			
			// Check if any integer values should actually be decimals, indicated by the range constraint on MRCM members
			final LongSet decimalTypeIds = collectAttributesWithRangeConstraint(context, "dec(>#0..)");
			final LongSet decimalValueIds = collectValueTypeChanges(integerTypeIdsByValueId, decimalTypeIds);
			swapValueType(context, decimalValueIds);
			
			if (doCreateVersion) {
				ResourceRequests.prepareNewVersion()
					.setResource(codeSystemUri)
					.setVersion(getEffectiveTime())
					.setDescription("")
					.setEffectiveTime(getEffectiveDate())
//					.setForce(true) TODO force create version if reimporting latest release, patching it basically
					.buildAsync()
					.getRequest()
					.execute(context);
			}
		}
		
		context.log().info("{} in {}", commitMessage, w);
	}

	private LongSet collectAttributesWithRangeConstraint(final BranchContext context, final String rangeConstraint) {
		final LongSet typeIds = PrimitiveSets.newLongOpenHashSet();
		
		SnomedRequests.prepareSearchMember()
			.filterByActive(true)
			.filterByRefSet("<" + Concepts.REFSET_MRCM_ATTRIBUTE_RANGE_ROOT) // all MRCM range reference sets
			.filterByProps(Options.builder()
				.put(SnomedRf2Headers.FIELD_MRCM_RANGE_CONSTRAINT, rangeConstraint)
				.build())
			.setLimit(1000)
			.setFields(
				// ID and referenced component type is required by the reference set member converter 
				SnomedRefSetMemberIndexEntry.Fields.ID, 
				SnomedRefSetMemberIndexEntry.Fields.REFERENCED_COMPONENT_ID,
				SnomedRefSetMemberIndexEntry.Fields.REFERENCED_COMPONENT_TYPE)
			.build()
			.execute(context)
			.stream()
			.mapToLong(m -> Long.parseLong(m.getReferencedComponentId()))
			.forEachOrdered(typeIds::add);
		
		return typeIds;
	}

	private LongSet collectValueTypeChanges(final LongKeyLongMap typeIdsByValueId, final LongSet oppositeTypeIds) {
		if (typeIdsByValueId.isEmpty() || oppositeTypeIds.isEmpty()) {
			return LongCollections.emptySet();
		}
		
		final LongSet valueIds = typeIdsByValueId.keySet();
		final LongSet needsValueTypeChange = PrimitiveSets.newLongOpenHashSet();
		
		for (final LongIterator itr = valueIds.iterator(); itr.hasNext(); /* empty */) {
			final long valueId = itr.next();
			final long typeId = typeIdsByValueId.get(valueId);
			
			if (oppositeTypeIds.contains(typeId)) {
				// The relationship's value should have the "opposite" numeric type
				needsValueTypeChange.add(valueId);
			}
		}
		
		return needsValueTypeChange;
	}

	private void swapValueType(final BranchContext context, final LongSet idsToUpdate) throws Exception {
		if (idsToUpdate.isEmpty()) {
			return;
		}
		
		final Set<String> idsAsString = LongSets.toStringSet(idsToUpdate);
		idsToUpdate.clear();
		
		try (final TransactionContext tx = context.openTransaction(context, DatastoreLockContextDescriptions.IMPORT)) {
			for (final List<String> batch : Iterables.partition(idsAsString, BATCH_SIZE)) {
				final Map<String, SnomedRelationshipIndexEntry> entriesById = tx.lookup(batch, SnomedRelationshipIndexEntry.class);

				for (final SnomedRelationshipIndexEntry existingEntry : entriesById.values()) {
					final RelationshipValue oldValue = existingEntry.getValueAsObject();
					final BigDecimal numericValue = oldValue.map(
						i -> new BigDecimal(i),
						d -> null,
						s -> null);
							
					if (numericValue != null) {
						final RelationshipValue newValue = RelationshipValue.fromTypeAndObjects(RelationshipValueType.DECIMAL, numericValue, null);
						final SnomedRelationshipIndexEntry updatedEntry = SnomedRelationshipIndexEntry.builder(existingEntry)
							.value(newValue)
							.build();

						tx.update(existingEntry, updatedEntry);
					} else {
						context.log().warn("Non-integer value found on relationship {}, can not convert value type", existingEntry.getId());
					}
				}
			}
		
			tx.commit("Update value types using MRCM range constraints");
		}
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
					dependenciesByComponent.put(getIndexDocument(requiredDependency), requiredDependency);
				}
			}
		}
		return dependenciesByComponent;
	}

	private Class<? extends SnomedDocument> getIndexDocument(String componentId) {
		ComponentCategory type = SnomedIdentifiers.getComponentCategory(componentId);
		switch (type) {
		case CONCEPT: return SnomedConceptDocument.class;
		case DESCRIPTION: return SnomedDescriptionIndexEntry.class;
		case RELATIONSHIP: return SnomedRelationshipIndexEntry.class;
		default: throw new UnsupportedOperationException(String.format("Cannot determine document type from component ID and type: [%s,%s]", componentId, type));
		}
	}
}
