/*
 * Copyright 2017 B2i Healthcare Pte Ltd, http://b2i.sg
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
package com.b2international.snowowl.snomed.datastore.request.rf2;

import static com.google.common.collect.Lists.newArrayListWithExpectedSize;
import static com.google.common.collect.Maps.newHashMapWithExpectedSize;
import static com.google.common.collect.Sets.newHashSet;

import java.util.Collection;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.eclipse.emf.cdo.CDOObject;
import org.mapdb.DB;
import org.mapdb.HTreeMap;
import org.mapdb.Serializer;

import com.b2international.collections.PrimitiveMaps;
import com.b2international.collections.PrimitiveSets;
import com.b2international.collections.longs.LongIterator;
import com.b2international.collections.longs.LongKeyMap;
import com.b2international.collections.longs.LongSet;
import com.b2international.commons.collect.LongSets;
import com.b2international.commons.graph.LongTarjan;
import com.b2international.index.revision.Purge;
import com.b2international.snowowl.core.date.DateFormats;
import com.b2international.snowowl.core.date.EffectiveTimes;
import com.b2international.snowowl.core.domain.BranchContext;
import com.b2international.snowowl.core.domain.IComponent;
import com.b2international.snowowl.core.domain.TransactionContextProvider;
import com.b2international.snowowl.datastore.oplock.impl.DatastoreLockContextDescriptions;
import com.b2international.snowowl.datastore.request.RepositoryRequests;
import com.b2international.snowowl.datastore.request.repository.PurgeRequest;
import com.b2international.snowowl.snomed.Concept;
import com.b2international.snowowl.snomed.Description;
import com.b2international.snowowl.snomed.Relationship;
import com.b2international.snowowl.snomed.common.SnomedTerminologyComponentConstants;
import com.b2international.snowowl.snomed.core.domain.SnomedComponent;
import com.b2international.snowowl.snomed.core.domain.SnomedCoreComponent;
import com.b2international.snowowl.snomed.core.domain.refset.SnomedReferenceSetMember;
import com.b2international.snowowl.snomed.datastore.id.SnomedIdentifiers;
import com.b2international.snowowl.snomed.snomedrefset.SnomedRefSet;
import com.b2international.snowowl.terminologymetadata.CodeSystem;
import com.b2international.snowowl.terminologymetadata.CodeSystemVersion;
import com.b2international.snowowl.terminologyregistry.core.builder.CodeSystemVersionBuilder;
import com.google.common.base.Stopwatch;
import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;

/**
 * @since 6.0
 */
final class Rf2EffectiveTimeSlice {

	private static final int BATCH_SIZE = 5000;

	private final Date effectiveDate;
	private final String effectiveTime;
	
	private final LongKeyMap<Set<String>> membersByContainer;
	private final LongKeyMap<LongSet> dependenciesByComponent;
	
	// tmp map to quickly collect batch of items before flushing it to disk
	private final Map<String, String[]> tmpComponentsById;
	private final HTreeMap<String, String[]> componentsById;
	
	public Rf2EffectiveTimeSlice(DB db, String effectiveTime) {
		this.effectiveDate = EffectiveTimes.parse(effectiveTime, DateFormats.SHORT);
		this.effectiveTime = EffectiveTimes.format(effectiveDate, DateFormats.DEFAULT);
		this.componentsById = db.hashMap(effectiveTime, Serializer.STRING, Serializer.ELSA).create();
		this.tmpComponentsById = newHashMapWithExpectedSize(BATCH_SIZE);
		this.dependenciesByComponent = PrimitiveMaps.newLongKeyOpenHashMap();
		this.membersByContainer = PrimitiveMaps.newLongKeyOpenHashMap();
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
		
		throw new IllegalArgumentException("Unrecognized RF2 component: " + componentId + " - " + valuesWithType);
	}

	public void register(String containerId, String type, String[] values) {
		String[] valuesWithType = new String[values.length + 1];
		valuesWithType[0] = type;
		System.arraycopy(values, 0, valuesWithType, 1, values.length);

		final String componentId = values[0];
		final long containerIdL = Long.parseLong(containerId);
		// track refset members via membersByContainer map
		if (type.endsWith("member")) {
			if (!membersByContainer.containsKey(containerIdL)) {
				membersByContainer.put(containerIdL, newHashSet());
			}
			membersByContainer.get(containerIdL).add(componentId);
		} else {
			// register other non-concept components in the dependency graph to force strongly connected subgraphs
			if (!IComponent.ROOT_ID.equals(containerId)) {
				registerDependencies(containerIdL, PrimitiveSets.newLongOpenHashSet(Long.parseLong(componentId)));
			}
		}
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

	public void flush() {
		if (!tmpComponentsById.isEmpty()) {
			componentsById.putAll(tmpComponentsById);
		}
		tmpComponentsById.clear();
	}

	private List<LongSet> getImportPlan() {
		return new LongTarjan(60000, dependenciesByComponent::get).run(dependenciesByComponent.keySet());
	}

	public void doImport(BranchContext context, boolean createVersions) throws Exception {
		Stopwatch w = Stopwatch.createStarted();
		System.err.println("Importing components from " + effectiveTime);
		try (Rf2TransactionContext tx = new Rf2TransactionContext(context.service(TransactionContextProvider.class).get(context))) {
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
					}
					// add all members of this component to this batch as well
					final Set<String> containerComponents = membersByContainer.remove(componentToImportL);
					if (containerComponents != null) {
						for (String containedComponentId : containerComponents) {
							SnomedReferenceSetMember containedComponent = getComponent(containedComponentId);
							if (containedComponent != null) {
								componentsToImport.add(containedComponent);
							}
						}
					}
				}
				
				tx.add(componentsToImport, getDependencies(componentsToImport));
				
				if (createVersions && !importPlan.hasNext()) {
					final CodeSystemVersion version = new CodeSystemVersionBuilder()
							.withDescription("")
							.withEffectiveDate(effectiveDate)
							.withImportDate(new Date())
							.withParentBranchPath(context.branch().path())
							.withVersionId(effectiveTime)
							.build();
					tx.lookup(SnomedTerminologyComponentConstants.SNOMED_SHORT_NAME, CodeSystem.class).getCodeSystemVersions().add(version);
				}
				
				// TODO consider moving preCommit into commit method
				tx.preCommit();
				tx.commit("info@b2international.com", "Imported components from " + effectiveTime, DatastoreLockContextDescriptions.ROOT);
			}
			
			if (createVersions) {
				// purge index
				PurgeRequest.builder()
					.setBranchPath(context.branch().path())
					.setPurge(Purge.LATEST)
					.build()
					.execute(context);
				
				// do actually create a branch with the effective time name
				RepositoryRequests
					.branching()
					.prepareCreate()
					.setParent(context.branch().path())
					.setName(effectiveTime)
					.build()
					.execute(context);
			}
		}
		System.err.println("Imported components from " + effectiveTime + " in " + w);
	}

	private Multimap<Class<? extends CDOObject>, String> getDependencies(Collection<SnomedComponent> componentsToImport) {
		final Multimap<Class<? extends CDOObject>, String> dependenciesByComponent = HashMultimap.create();
		for (SnomedComponent component : componentsToImport) {
			if (component instanceof SnomedCoreComponent) {
				LongSet dependencies = this.dependenciesByComponent.get(Long.parseLong(component.getId()));
				if (dependencies != null) {
					Set<String> requiredDependencies = LongSets.toStringSet(dependencies);
					for (String requiredDependency : requiredDependencies) {
						dependenciesByComponent.put(getCdoType(requiredDependency), requiredDependency);
					}
				}
			} else if (component instanceof SnomedReferenceSetMember) {
				dependenciesByComponent.put(SnomedRefSet.class, ((SnomedReferenceSetMember) component).getReferenceSetId());
			}
		}
		return dependenciesByComponent;
	}

	private Class<? extends CDOObject> getCdoType(String componentId) {
		switch (SnomedIdentifiers.getComponentCategory(componentId)) {
		case CONCEPT: return Concept.class;
		case DESCRIPTION: return Description.class;
		case RELATIONSHIP: return Relationship.class;
		default: throw new UnsupportedOperationException("Cannot determine cdo type from component ID: " + componentId);
		}
	}

}