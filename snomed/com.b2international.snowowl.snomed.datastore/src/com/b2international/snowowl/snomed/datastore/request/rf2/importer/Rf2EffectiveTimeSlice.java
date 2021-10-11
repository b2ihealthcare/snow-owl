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

import com.b2international.collections.longs.LongKeyMap;
import com.b2international.collections.longs.LongSet;
import com.b2international.snowowl.core.ResourceURI;
import com.b2international.snowowl.core.domain.BranchContext;
import com.b2international.snowowl.core.request.io.ImportDefectAcceptor.ImportDefectBuilder;
import com.b2international.snowowl.core.uri.ComponentURI;
import com.google.common.collect.ImmutableSet.Builder;

/**
 * @since 8.0
 */
public interface Rf2EffectiveTimeSlice {

	String SNAPSHOT_SLICE = "snapshot";
	
	int BATCH_SIZE = 5000;
	
	void register(String containerId, Rf2ContentType<?> type, String[] values, ImportDefectBuilder defectBuilder);

	void registerDependencies(long componentId, LongSet dependencies);

	String getEffectiveTime();

	void flush();

	Map<String, String[]> getContent();

	void unregisterDependencies(String componentId);

	LongKeyMap<LongSet> getDependenciesByComponent();

	LongKeyMap<Set<String>> getMembersByReferencedComponent();

	void doImport(BranchContext context, ResourceURI codeSystemUri, Rf2ImportConfiguration importConfig, Builder<ComponentURI> visitedComponents) throws Exception;
	
}
