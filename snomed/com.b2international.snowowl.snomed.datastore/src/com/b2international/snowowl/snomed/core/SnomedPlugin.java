/*
 * Copyright 2018 B2i Healthcare Pte Ltd, http://b2i.sg
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
package com.b2international.snowowl.snomed.core;

import com.b2international.snowowl.core.repository.TerminologyRepositoryPlugin;
import com.b2international.snowowl.datastore.TerminologyRepositoryInitializer;
import com.b2international.snowowl.snomed.common.SnomedTerminologyComponentConstants;
import com.b2international.snowowl.snomed.datastore.SnomedDatastoreActivator;
import com.b2international.snowowl.snomed.datastore.internal.SnomedRepositoryInitializer;

/**
 * @since 7.0
 */
public final class SnomedPlugin extends TerminologyRepositoryPlugin {

	@Override
	protected String getRepositoryId() {
		return SnomedDatastoreActivator.REPOSITORY_UUID;
	}
	
	@Override
	protected String getToolingId() {
		return SnomedTerminologyComponentConstants.TERMINOLOGY_ID;
	}
	
	@Override
	protected TerminologyRepositoryInitializer getTerminologyRepositoryInitializer() {
		return new SnomedRepositoryInitializer();
	}
	
}
