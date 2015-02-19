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
package com.b2international.snowowl.datastore.server.snomed.index.init;

import org.eclipse.core.runtime.IProgressMonitor;

import com.b2international.snowowl.core.api.IBranchPath;
import com.b2international.snowowl.core.api.SnowowlServiceException;
import com.b2international.snowowl.datastore.server.snomed.index.init.ImportIndexServerService.DescriptionTypePredicateAdapter;
import com.b2international.snowowl.datastore.server.snomed.index.init.ImportIndexServerService.IDescriptionTypePredicate;

/**
 * Interface for populating the content of the {@link ImportIndexServerService}.
 *
 */
public interface IImportIndexServiceFeeder {

	/**
	 * Initialize the content of the import index service argument.
	 * @param service import index service.
	 * @param branchPath branch path.
	 * @param monitor monitor for the process.
	 * @throws SnowowlServiceException in case of failed content initialization.
	 */
	void initContent(final ImportIndexServerService service, final IBranchPath branchPath, final IProgressMonitor monitor) throws SnowowlServiceException;
	
	/**
	 * Represents a fully specified name description type predicate.
	 */
	IDescriptionTypePredicate FSN_PREDICATE = new DescriptionTypePredicateAdapter() {
		public boolean isFsn() { return true;};
	};
	
	/**
	 * Predicate for representing a synonym description type concept or any if its descendant.
	 */
	IDescriptionTypePredicate SYNONYM_OR_DESCENDANT_PREDICATE = new DescriptionTypePredicateAdapter() {
		public boolean isSynonymOrDescendant() { return true; };
	};
	
	/**
	 * Represents a description type which does not fit neither {@link #FSN_PREDICATE} nor {@link #SYNONYM_OR_DESCENDANT_PREDICATE}. 
	 */
	IDescriptionTypePredicate OTHER_DESCRIPTION_PREDICATE = new DescriptionTypePredicateAdapter() {
	};
	
}