/*
 * Copyright 2021 B2i Healthcare Pte Ltd, http://b2i.sg
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

import java.util.Map;
import java.util.Set;

import com.b2international.collections.PrimitiveMaps;
import com.b2international.collections.longs.LongKeyMap;
import com.b2international.collections.longs.LongSet;
import com.b2international.snowowl.core.ResourceURI;
import com.b2international.snowowl.core.domain.BranchContext;
import com.b2international.snowowl.core.request.io.ImportDefectAcceptor.ImportDefectBuilder;
import com.b2international.snowowl.core.uri.ComponentURI;
import com.google.common.collect.ImmutableSet.Builder;

/**
 * An {@link Rf2EffectiveTimeSlice} implementation that ignores every method call and it does not register the given read RF2 rows at all inside it, essentially skips all content.
 * 
 * @since 8.0
 */
final class IgnoredRf2EffectiveTimeSlice extends BaseRf2EffectiveTimeSlice {

	private final LongKeyMap<LongSet> emptyDependencies = PrimitiveMaps.newLongKeyOpenHashMap();
	private final LongKeyMap<Set<String>> emptyMembers = PrimitiveMaps.newLongKeyOpenHashMap();

	public IgnoredRf2EffectiveTimeSlice(String effectiveTimeToIgnore) {
		super(effectiveTimeToIgnore);
	}
	
	@Override
	public void register(String containerId, Rf2ContentType<?> type, String[] values, ImportDefectBuilder defectBuilder) {
	}

	@Override
	public void registerDependencies(long componentId, LongSet dependencies) {
	}
	
	@Override
	public void flush() {
	}

	@Override
	public Map<String, String[]> getContent() {
		return Map.of();
	}

	@Override
	public void unregisterDependencies(String componentId) {
	}

	@Override
	public LongKeyMap<LongSet> getDependenciesByComponent() {
		return emptyDependencies;
	}

	@Override
	public LongKeyMap<Set<String>> getMembersByReferencedComponent() {
		return emptyMembers;
	}

	@Override
	public void doImport(BranchContext context, ResourceURI codeSystemUri, Rf2ImportConfiguration importConfig, Builder<ComponentURI> visitedComponents) throws Exception {
		context.log().info("EffectiveTime '{}' is already present in the system, skipping.", getEffectiveTime());
	}

}
