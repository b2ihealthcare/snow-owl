/*
 * Copyright 2011-2016 B2i Healthcare Pte Ltd, http://b2i.sg
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
package com.b2international.snowowl.snomed.exporter.server.rf2;

import static com.google.common.base.Preconditions.checkNotNull;

import com.b2international.index.query.Expressions.ExpressionBuilder;
import com.b2international.index.revision.RevisionSearcher;
import com.b2international.snowowl.core.ApplicationContext;
import com.b2international.snowowl.eventbus.IEventBus;
import com.b2international.snowowl.snomed.common.SnomedRf2Headers;
import com.b2international.snowowl.snomed.core.domain.SnomedConcept;
import com.b2international.snowowl.snomed.core.domain.SnomedDescription;
import com.b2international.snowowl.snomed.core.domain.refset.SnomedReferenceSet;
import com.b2international.snowowl.snomed.core.lang.LanguageSetting;
import com.b2international.snowowl.snomed.datastore.SnomedDatastoreActivator;
import com.b2international.snowowl.snomed.datastore.index.entry.SnomedRefSetMemberIndexEntry;
import com.b2international.snowowl.snomed.datastore.request.SnomedRequests;
import com.b2international.snowowl.snomed.exporter.server.ComponentExportType;
import com.b2international.snowowl.snomed.exporter.server.SnomedExportContext;
import com.b2international.snowowl.snomed.exporter.server.SnomedRfFileNameBuilder;
import com.google.common.base.Function;
import com.google.common.base.Strings;

/**
 * Base for all SNOMED&nbsp;CT reference set RF2 exporters.
 *
 */
public class SnomedRefSetExporter extends AbstractSnomedRf2CoreExporter<SnomedRefSetMemberIndexEntry> {

	private SnomedReferenceSet refset;

	public SnomedRefSetExporter(final SnomedExportContext exportContext, final SnomedReferenceSet refset, final RevisionSearcher revisionSearcher) {
		super(exportContext, SnomedRefSetMemberIndexEntry.class, revisionSearcher);
		this.refset = checkNotNull(refset, "refset");
	}
	
	@Override
	protected void appendExpressionConstraint(ExpressionBuilder builder) {
		builder.filter(SnomedRefSetMemberIndexEntry.Expressions.referenceSetId(refset.getId()));
	}

	@Override
	public String convertToString(final SnomedRefSetMemberIndexEntry doc) {
		final StringBuilder sb = new StringBuilder();
		sb.append(doc.getId());
		sb.append(HT);
		sb.append(formatEffectiveTime(doc.getEffectiveTime()));
		sb.append(HT);
		sb.append(formatStatus(doc.isActive()));
		sb.append(HT);
		sb.append(doc.getModuleId());
		sb.append(HT);
		sb.append(doc.getReferenceSetId());
		sb.append(HT);
		sb.append(doc.getReferencedComponentId());
		return sb.toString();
	}
	
	@Override
	public ComponentExportType getType() {
		return ComponentExportType.REF_SET;
	}
	
	@Override
	public String[] getColumnHeaders() {
		return SnomedRf2Headers.SIMPLE_TYPE_HEADER;
	}
	
	@Override
	public String getRelativeDirectory() {
		switch (refset.getType()) {
			case SIMPLE: //$FALL-THROUGH$
			case ASSOCIATION: //$FALL-THROUGH$
			case CONCRETE_DATA_TYPE: //$FALL-THROUGH$
			case QUERY: //$FALL-THROUGH$
			case ATTRIBUTE_VALUE:
			case OWL_AXIOM:
				return RF2_CONTENT_REFERENCE_SET_RELATIVE_DIR;
			case EXTENDED_MAP: //$FALL-THROUGH$
			case SIMPLE_MAP: //$FALL-THROUGH$
			case COMPLEX_MAP: 
				return RF2_MAP_REFERENCE_SET_RELATIVE_DIR;
			case DESCRIPTION_TYPE: //$FALL-THROUGH$
			case MODULE_DEPENDENCY:
			case MRCM_DOMAIN:
			case MRCM_ATTRIBUTE_DOMAIN:
			case MRCM_ATTRIBUTE_RANGE:
			case MRCM_MODULE_SCOPE:
				return RF2_METADATA_REFERENCE_SET_RELATIVE_DIR;
			case LANGUAGE: 
				return RF2_LANGUAGE_REFERENCE_SET_RELATIVE_DIR;
			default: throw new IllegalArgumentException("Unknown SNOMED CT reference set type: " + refset.getType());
		}
	}
	
	@Override
	public String getFileName() {
		return SnomedRfFileNameBuilder.buildRefSetFileName(getExportContext(), getRefsetName(), refset);
	}

	protected String getRefsetName() {
		
		return SnomedRequests.prepareGetConcept(refset.getId())
			.setExpand("pt()")
			.setLocales(ApplicationContext.getServiceForClass(LanguageSetting.class).getLanguagePreference())
			.build(SnomedDatastoreActivator.REPOSITORY_UUID, getExportContext().getCurrentBranchPath().getPath())
			.execute(ApplicationContext.getServiceForClass(IEventBus.class))
			.then(new Function<SnomedConcept, String>() {
				@Override public String apply(SnomedConcept input) {
					SnomedDescription pt = input.getPt();
					if (pt == null || Strings.isNullOrEmpty(pt.getTerm())) { 
						return refset.getId(); 
					} else { 
						return pt.getTerm(); 
					}
				};
			})
			.getSync();
	}
	
	protected SnomedReferenceSet getRefset() {
		return refset;
	}

}
