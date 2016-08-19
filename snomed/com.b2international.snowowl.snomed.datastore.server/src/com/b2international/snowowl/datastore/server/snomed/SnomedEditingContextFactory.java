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
package com.b2international.snowowl.datastore.server.snomed;

import com.b2international.snowowl.core.api.IBranchPath;
import com.b2international.snowowl.datastore.CDOEditingContext;
import com.b2international.snowowl.datastore.server.EditingContextFactory;
import com.b2international.snowowl.snomed.datastore.SnomedDatastoreActivator;
import com.b2international.snowowl.snomed.datastore.SnomedEditingContext;

/**
 * @since 4.5
 */
public class SnomedEditingContextFactory implements EditingContextFactory {

	@Override
	public CDOEditingContext createEditingContext(IBranchPath branchPath) {
		// TODO additional configuration???
		return new SnomedEditingContext(branchPath);
	}

	@Override
	public boolean belongsTo(String repositoryId) {
		return SnomedDatastoreActivator.REPOSITORY_UUID.equals(repositoryId);
	}

}
