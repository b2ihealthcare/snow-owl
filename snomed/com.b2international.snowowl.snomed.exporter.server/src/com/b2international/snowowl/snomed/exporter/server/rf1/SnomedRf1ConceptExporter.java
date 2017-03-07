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
package com.b2international.snowowl.snomed.exporter.server.rf1;

import static com.b2international.commons.StringUtils.valueOfOrEmptyString;

import java.io.IOException;

import com.b2international.commons.BooleanUtils;
import com.b2international.index.Hits;
import com.b2international.index.query.Expression;
import com.b2international.index.query.Expressions;
import com.b2international.index.query.Query;
import com.b2international.index.revision.RevisionSearcher;
import com.b2international.snowowl.core.ApplicationContext;
import com.b2international.snowowl.eventbus.IEventBus;
import com.b2international.snowowl.snomed.SnomedConstants.Concepts;
import com.b2international.snowowl.snomed.core.domain.SnomedConcept;
import com.b2international.snowowl.snomed.core.lang.LanguageSetting;
import com.b2international.snowowl.snomed.datastore.SnomedDatastoreActivator;
import com.b2international.snowowl.snomed.datastore.index.entry.SnomedConceptDocument;
import com.b2international.snowowl.snomed.datastore.index.entry.SnomedRefSetMemberIndexEntry;
import com.b2international.snowowl.snomed.datastore.request.SnomedRequests;
import com.b2international.snowowl.snomed.exporter.server.ComponentExportType;
import com.b2international.snowowl.snomed.exporter.server.SnomedExportContext;
import com.google.common.base.Preconditions;
import com.google.common.collect.Sets;

/**
 * RF2 exporter for SNOMED&nbsp;CT concepts.
 *
 */
public class SnomedRf1ConceptExporter extends AbstractSnomedRf1CoreExporter<SnomedConceptDocument> {

	/**
	 * Line in the Concept RF1 file
	 */
	class Rf1Concept {
		
		public String id;
		public String status;
		public String fsn;
		public String ctv3;
		public String snomedRt;
		public String definitionStatus;
		
		@Override
		public String toString() {
			
			return new StringBuilder(valueOfOrEmptyString(id))
					.append(HT)
					.append(getConceptStatus(valueOfOrEmptyString(status)))
					.append(HT)
					.append(valueOfOrEmptyString(fsn))
					.append(HT)
					.append(valueOfOrEmptyString(ctv3))
					.append(HT)
					.append(valueOfOrEmptyString(snomedRt))
					.append(HT)
					.append(valueOfOrEmptyString(definitionStatus))
					.toString();
		}
	}
	
	public SnomedRf1ConceptExporter(final SnomedExportContext exportContext, final RevisionSearcher revisionSearcher) {
		super(exportContext, SnomedConceptDocument.class, revisionSearcher);
	}

	
	@Override
	public String convertToString(SnomedConceptDocument doc) {
		Rf1Concept concept = new Rf1Concept();
		
		concept.id = doc.getId();
		concept.status = BooleanUtils.toString(doc.isActive());
		concept.definitionStatus = BooleanUtils.toString(doc.isPrimitive());
		
		final LanguageSetting languageSetting = ApplicationContext.getInstance().getService(LanguageSetting.class);
		IEventBus eventBus = ApplicationContext.getInstance().getService(IEventBus.class);
		
		try {
			//fsn
			SnomedConcept snomedConcept = SnomedRequests.prepareGetConcept(doc.getId())
				.setLocales(languageSetting.getLanguagePreference())
				.setExpand("fsn()")
				.build(SnomedDatastoreActivator.REPOSITORY_UUID, getExportContext().getCurrentBranchPath().getPath())
				.execute(eventBus)
				.getSync();
			
			concept.fsn = snomedConcept.getFsn().getTerm();
		
			//inactivation status
			if (!doc.isActive()) {
				
				Expression condition = Expressions.builder()
						.must(SnomedRefSetMemberIndexEntry.Expressions.referencedComponentIds(Sets.newHashSet(doc.getId())))
						.must(SnomedRefSetMemberIndexEntry.Expressions.referenceSetId(Sets.newHashSet(Concepts.REFSET_CONCEPT_INACTIVITY_INDICATOR)))
						.must(SnomedRefSetMemberIndexEntry.Expressions.active()).build();
				
				Query<SnomedRefSetMemberIndexEntry> query = Query.select(SnomedRefSetMemberIndexEntry.class).where(condition).build();
							
				Hits<SnomedRefSetMemberIndexEntry> snomedRefSetMemberIndexEntrys;
					snomedRefSetMemberIndexEntrys = getRevisionSearcher().search(query);
				
				//there should be only one max
				for (SnomedRefSetMemberIndexEntry snomedRefSetMemberIndexEntry : snomedRefSetMemberIndexEntrys) {
					concept.status = snomedRefSetMemberIndexEntry.getValueId();
				}
			}
			
			Expression condition = Expressions.builder()
					.must(SnomedRefSetMemberIndexEntry.Expressions.referencedComponentIds(Sets.newHashSet(doc.getId())))
					.must(SnomedRefSetMemberIndexEntry.Expressions.referenceSetId(Sets.newHashSet(Concepts.CTV3_SIMPLE_MAP_TYPE_REFERENCE_SET_ID)))
					.must(SnomedRefSetMemberIndexEntry.Expressions.active()).build();
			
			Query<SnomedRefSetMemberIndexEntry> query = Query.select(SnomedRefSetMemberIndexEntry.class).where(condition).build();
			Hits<SnomedRefSetMemberIndexEntry> snomedRefSetMemberIndexEntrys = getRevisionSearcher().search(query);
			
			//there should be only one max
			for (SnomedRefSetMemberIndexEntry snomedRefSetMemberIndexEntry : snomedRefSetMemberIndexEntrys) {
				concept.ctv3 = snomedRefSetMemberIndexEntry.getTargetComponent();
			}
			
			condition = Expressions.builder()
					.must(SnomedRefSetMemberIndexEntry.Expressions.referencedComponentIds(Sets.newHashSet(doc.getId())))
					.must(SnomedRefSetMemberIndexEntry.Expressions.referenceSetId(Sets.newHashSet(Concepts.SNOMED_RT_SIMPLE_MAP_TYPE_REFERENCE_SET_ID)))
					.must(SnomedRefSetMemberIndexEntry.Expressions.active()).build();
			
			query = Query.select(SnomedRefSetMemberIndexEntry.class).where(condition).build();
			snomedRefSetMemberIndexEntrys = getRevisionSearcher().search(query);
			
			//there should be only one max
			for (SnomedRefSetMemberIndexEntry snomedRefSetMemberIndexEntry : snomedRefSetMemberIndexEntrys) {
				concept.snomedRt = snomedRefSetMemberIndexEntry.getTargetComponent();
			}
			return concept.toString();
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}
	
	@Override
	public ComponentExportType getType() {
		return ComponentExportType.CONCEPT;
	}

	@Override
	public String[] getColumnHeaders() {
		return SnomedRf1ReleaseFileHeaders.RF1_CONCEPT_HEADER;
	}
	
	/*returns with a number indicating the status of a concept for RF1 publication.*/
	private String getConceptStatus(final String stringValue) {
		//magic mapping between RF1 and RF2 statuses
		if ("1".equals(stringValue)) {
			return "0";
		} else if ("0".equals(stringValue)) {
			return "1";
		} else {
			Id2Rf1PropertyMapper id2Rf1PropertyMapper = getExportContext().getId2Rf1PropertyMapper();
			return Preconditions.checkNotNull(id2Rf1PropertyMapper.getConceptStatusProperty(stringValue));
		}
	}

}
