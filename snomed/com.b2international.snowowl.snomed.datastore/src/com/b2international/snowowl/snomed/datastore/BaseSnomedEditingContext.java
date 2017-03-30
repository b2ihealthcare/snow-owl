/*
 * Copyright 2011-2017 B2i Healthcare Pte Ltd, http://b2i.sg
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

import static com.b2international.snowowl.datastore.cdo.CDOUtils.check;
import static com.google.common.base.Preconditions.checkNotNull;

import org.eclipse.emf.cdo.transaction.CDOTransaction;

import com.b2international.snowowl.core.api.IBranchPath;
import com.b2international.snowowl.datastore.CDOEditingContext;
import com.b2international.snowowl.snomed.SnomedPackage;

/**
 * Base implementation of all editing context instances acting on the SNOMED&nbsp;CT&nbsp;Store
 * for the SNOMED&nbsp;CT ontology. 
 *
 */
public abstract class BaseSnomedEditingContext extends CDOEditingContext {

	protected BaseSnomedEditingContext(final IBranchPath branchPath) {
		super(SnomedPackage.eINSTANCE, checkNotNull(branchPath, "branchPath"));
	}
	
	protected BaseSnomedEditingContext(final CDOTransaction transaction) {
		super(check(transaction));
	}
	
	@Override
	protected String getMetaRootResourceName() {
		return SnomedDatastoreActivator.META_ROOT_RESOURCE_NAME;
	}
	
}