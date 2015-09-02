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
import static com.b2international.snowowl.datastore.index.IndexUtils.getIntValue;
import static com.b2international.snowowl.snomed.common.SnomedTerminologyComponentConstants.CONCEPT_NUMBER;
import static com.b2international.snowowl.snomed.common.SnomedTerminologyComponentConstants.DESCRIPTION_NUMBER;
import static com.b2international.snowowl.snomed.common.SnomedTerminologyComponentConstants.REFSET_NUMBER;
import static com.b2international.snowowl.snomed.common.SnomedTerminologyComponentConstants.RELATIONSHIP_NUMBER;
import static com.b2international.snowowl.snomed.datastore.SnomedDatastoreActivator.REPOSITORY_UUID;
import static com.b2international.snowowl.snomed.datastore.SnomedRefSetUtil.getRefSetMemberClass;
import static com.b2international.snowowl.snomed.snomedrefset.SnomedRefSetType.get;

import java.util.Set;

import org.apache.lucene.document.Document;
import org.apache.lucene.index.IndexableField;
import org.eclipse.emf.ecore.EClass;

import com.b2international.snowowl.datastore.index.AbstractIndexService;
import com.b2international.snowowl.datastore.index.IndexUtils;
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

	private static final int PREDICATE_TYPE_ID = 999;

	private static final Set<String> FIELDS_TO_LOAD = SnomedMappings.fieldsToLoad().type().memberReferenceSetType().build();
	
	@Override
	protected EClass extractEClass(final Document doc) {
		
		IndexableField indexableField = Mappings.type().getField(doc);
		if (null == indexableField) {//component type is not stored for reference set members
			indexableField = SnomedMappings.memberRefSetType().getField(doc);
			if (null != indexableField) {
				return getRefSetMemberClass(get(getIntValue(indexableField)));
			}
			return null;
		}
		
		final int intValue = IndexUtils.getIntValue(indexableField);
		
		if (CONCEPT_NUMBER == intValue) {
			return SnomedPackage.eINSTANCE.getConcept();
		} else if (DESCRIPTION_NUMBER == intValue) {
			return SnomedPackage.eINSTANCE.getDescription();
		} else if (REFSET_NUMBER == intValue) {
			return SnomedRefSetPackage.eINSTANCE.getSnomedRefSet();
		} else if (RELATIONSHIP_NUMBER == intValue) {
			return SnomedPackage.eINSTANCE.getRelationship();
		} else if (PREDICATE_TYPE_ID == intValue) {
			return MrcmPackage.eINSTANCE.getAttributeConstraint();
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