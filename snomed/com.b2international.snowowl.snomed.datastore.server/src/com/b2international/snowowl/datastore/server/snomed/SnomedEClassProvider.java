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

import static com.b2international.snowowl.core.ApplicationContext.getServiceForClass;
import static com.b2international.snowowl.snomed.common.SnomedTerminologyComponentConstants.CONCEPT_NUMBER;
import static com.b2international.snowowl.snomed.common.SnomedTerminologyComponentConstants.DESCRIPTION_NUMBER;
import static com.b2international.snowowl.snomed.common.SnomedTerminologyComponentConstants.PREDICATE_TYPE_ID;
import static com.b2international.snowowl.snomed.common.SnomedTerminologyComponentConstants.RELATIONSHIP_NUMBER;
import static com.b2international.snowowl.snomed.datastore.SnomedDatastoreActivator.REPOSITORY_UUID;
import static com.b2international.snowowl.snomed.datastore.SnomedRefSetUtil.getRefSetMemberClass;
import static com.b2international.snowowl.snomed.snomedrefset.SnomedRefSetType.get;

import java.util.Set;

import org.apache.lucene.document.Document;
import org.apache.lucene.index.IndexableField;
import org.eclipse.emf.ecore.EClass;

import com.b2international.snowowl.datastore.index.AbstractIndexService;
import com.b2international.snowowl.datastore.index.mapping.Mappings;
import com.b2international.snowowl.datastore.server.EClassProvider;
import com.b2international.snowowl.datastore.server.IEClassProvider;
import com.b2international.snowowl.snomed.SnomedPackage;
import com.b2international.snowowl.snomed.datastore.index.SnomedIndexService;
import com.b2international.snowowl.snomed.datastore.index.mapping.SnomedMappings;
import com.b2international.snowowl.snomed.mrcm.MrcmPackage;
import com.b2international.snowowl.snomed.snomedrefset.SnomedRefSetPackage;

/**
 * {@link IEClassProvider EClass provider} for SNOMED&nbsp;CT ontology.
 */
public class SnomedEClassProvider extends EClassProvider {

	private static final Set<String> FIELDS_TO_LOAD = SnomedMappings.fieldsToLoad().type().memberReferenceSetType().storageKey().build();
	
	@Override
	protected EClass extractEClass(Document doc) {
		throw new UnsupportedOperationException("Storage Key is required to determine correct EClass");
	}
	
	@Override
	protected EClass extractEClass(final Document doc, long storageKey) {
		final IndexableField[] indexableFields = Mappings.type().getFields(doc);
		if (indexableFields.length == 0) {//component type is not stored for reference set members
			return getRefSetMemberClass(get(SnomedMappings.memberRefSetType().getValue(doc)));
		} else if (indexableFields.length == 1) {
			final int intValue = indexableFields[0].numericValue().intValue();
			switch (intValue) {
			case CONCEPT_NUMBER: return SnomedPackage.Literals.CONCEPT;
			case DESCRIPTION_NUMBER: return SnomedPackage.Literals.DESCRIPTION;
			case RELATIONSHIP_NUMBER: return SnomedPackage.Literals.RELATIONSHIP;
			case PREDICATE_TYPE_ID: return MrcmPackage.Literals.ATTRIBUTE_CONSTRAINT;
			}
		} else {
			// TODO implement proper type and storagekey match
			final long conceptStorageKey = Mappings.storageKey().getValue(doc);
			if (storageKey == conceptStorageKey) {
				return SnomedPackage.Literals.CONCEPT;
			} else {
				return SnomedRefSetPackage.Literals.SNOMED_REF_SET;
			}
		}
		return null;
	}

	@Override
	protected AbstractIndexService<?> getServerService() {
		return (AbstractIndexService<?>) getServiceForClass(SnomedIndexService.class);
	}

	@Override
	protected Set<String> getFieldsToLoad() {
		return FIELDS_TO_LOAD;
	}
	
	@Override
	public int getPriority() {
		return 1;
	}
	
	@Override
	public String getRepositoryUuid() {
		return REPOSITORY_UUID;
	}
	
}