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
package com.b2international.snowowl.snomed.datastore.index;

import org.apache.lucene.document.Document;

import com.b2international.snowowl.datastore.cdo.CDOIDUtils;
import com.b2international.snowowl.datastore.index.AbstractIndexMappingStrategy;
import com.b2international.snowowl.snomed.datastore.index.mapping.SnomedMappings;
import com.b2international.snowowl.snomed.datastore.index.update.ConstraintUpdater;
import com.b2international.snowowl.snomed.mrcm.AttributeConstraint;

/**
 * Strategy for creating index documents for MRCM based concept {@link AttributeConstraint attribute constraint}s.
 */
public final class PredicateIndexMappingStrategy extends AbstractIndexMappingStrategy {

	private AttributeConstraint constraint;

	public PredicateIndexMappingStrategy(AttributeConstraint constraint) {
		this.constraint = constraint;
	}
	
	@Override
	public Document createDocument() {
		return SnomedMappings.doc().with(new ConstraintUpdater(constraint)).build();
	}
	
	@Override
	protected long getStorageKey() {
		return CDOIDUtils.asLong(constraint.cdoID());
	}
	
}