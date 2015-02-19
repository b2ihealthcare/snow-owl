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
package com.b2international.snowowl.snomed.datastore;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.collect.Sets.newHashSet;
import static java.util.Collections.unmodifiableCollection;

import java.util.Collection;

import com.b2international.snowowl.datastore.cdo.CDORootResourceNameProvider;

/**
 * Snow Owl specific CDO root resource name provider implementation for the SNOMED&nbsp;CT ontology.
 *
 */
public class SnomedCDORootResourceNameProvider implements CDORootResourceNameProvider {

	public static final String ROOT_RESOURCE_NAME = "SNOMED";
	public static final String REFSET_ROOT_RESOURCE_NAME = "SNOMED_REFSET";
	public static final String MRCM_ROOT_RESOURCE_NAME = "MRCM";
	public static final String META_ROOT_RESOURCE_NAME = "META_SNOMED";
	public static final String GENERATOR_RESOURCE_NAME = "ogfRoot";

	private static final Collection<String> ROOT_RESOURCES = unmodifiableCollection(newHashSet(
			ROOT_RESOURCE_NAME,
			REFSET_ROOT_RESOURCE_NAME,
			MRCM_ROOT_RESOURCE_NAME,
			META_ROOT_RESOURCE_NAME,
			GENERATOR_RESOURCE_NAME));
	
	@Override
	public Collection<String> getRootResourceNames() {
		return ROOT_RESOURCES;
	}

	@Override
	public String getRepositoryUuid() {
		return SnomedDatastoreActivator.REPOSITORY_UUID;
	}

	@Override
	public boolean isMetaRootResource(final String rootResourceName) {
		return META_ROOT_RESOURCE_NAME.equals(checkNotNull(rootResourceName, "rootResourceName"));
	}
	
}