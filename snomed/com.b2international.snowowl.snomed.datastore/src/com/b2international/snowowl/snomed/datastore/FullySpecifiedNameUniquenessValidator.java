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

import static com.b2international.snowowl.datastore.BranchPathUtils.createActivePath;
import static com.google.common.base.Preconditions.checkNotNull;

import java.io.Serializable;
import java.util.Collection;

import org.eclipse.core.databinding.validation.IValidator;
import org.eclipse.core.runtime.IStatus;

import com.b2international.snowowl.core.ApplicationContext;
import com.b2international.snowowl.core.api.IBranchPath;
import com.b2international.snowowl.core.api.index.IIndexQueryAdapter;
import com.b2international.snowowl.core.validation.ComponentValidationStatus;
import com.b2international.snowowl.snomed.SnomedConstants;
import com.b2international.snowowl.snomed.datastore.index.SnomedDescriptionSortKeyQueryAdapter;
import com.b2international.snowowl.snomed.datastore.index.SnomedIndexService;
import com.b2international.snowowl.snomed.datastore.index.entry.SnomedDescriptionIndexEntry;

/**
 * Data binding {@link IValidator validator} for checking the uniqueness of a fully specified name description.
 * 
 */
public class FullySpecifiedNameUniquenessValidator implements IValidator, Serializable {

	private static final long serialVersionUID = -4234940980892877333L;
	private IBranchPath branchPath;

	public FullySpecifiedNameUniquenessValidator() {
		this(createActivePath(SnomedDatastoreActivator.REPOSITORY_UUID));
	}
	
	public FullySpecifiedNameUniquenessValidator(final IBranchPath branchPath) {
		this.branchPath = checkNotNull(branchPath, "branchPath");
	}
	
	@Override
	public IStatus validate(Object value) {

		if (!(value instanceof String)) {
			return new ComponentValidationStatus(IStatus.ERROR, SnomedDatastoreActivator.PLUGIN_ID, "String value expected."); 
		}
		
		final String fsn = ((String) value).trim();
		
		if (fsn.isEmpty()) {
			return new ComponentValidationStatus(IStatus.ERROR, SnomedDatastoreActivator.PLUGIN_ID, "Please determine preferred term.");
		}
			
		final IIndexQueryAdapter<SnomedDescriptionIndexEntry> queryAdapter = new SnomedDescriptionSortKeyQueryAdapter(fsn, 
				SnomedDescriptionSortKeyQueryAdapter.SEARCH_DESCRIPTION_ACTIVE_ONLY,
				SnomedConstants.Concepts.FULLY_SPECIFIED_NAME);
		
		if (getIndexService().getHitCount(branchPath, queryAdapter) < 1) {
			return ComponentValidationStatus.OK_STATUS;
		}
		
		final Collection<SnomedDescriptionIndexEntry> results = getIndexService().searchUnsorted(branchPath, queryAdapter);
		
		for (final SnomedDescriptionIndexEntry indexEntry : results) {
			if (indexEntry.getLabel().equals(fsn)) {
				return new ComponentValidationStatus(IStatus.ERROR, SnomedDatastoreActivator.PLUGIN_ID, "Fully specified name is not unique.");
			}
		}

		return ComponentValidationStatus.OK_STATUS;
	}

	private SnomedIndexService getIndexService() {
		return ApplicationContext.getInstance().getService(SnomedIndexService.class);
	}
}