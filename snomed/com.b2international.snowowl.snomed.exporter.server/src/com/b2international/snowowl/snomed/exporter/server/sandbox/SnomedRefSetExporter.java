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
package com.b2international.snowowl.snomed.exporter.server.sandbox;

import static com.b2international.snowowl.core.ApplicationContext.getServiceForClass;
import static com.b2international.snowowl.snomed.datastore.browser.SnomedIndexBrowserConstants.REFERENCE_SET_MEMBER_UUID;
import static com.google.common.base.Preconditions.checkNotNull;

import java.util.Set;

import org.apache.lucene.document.Document;
import org.apache.lucene.search.Query;
import org.eclipse.emf.cdo.transaction.CDOTransaction;

import com.b2international.snowowl.core.api.IBranchPath;
import com.b2international.snowowl.datastore.cdo.CDOTransactionFunction;
import com.b2international.snowowl.datastore.cdo.CDOUtils;
import com.b2international.snowowl.datastore.cdo.ICDOConnection;
import com.b2international.snowowl.datastore.cdo.ICDOConnectionManager;
import com.b2international.snowowl.snomed.common.SnomedRf2Headers;
import com.b2international.snowowl.snomed.datastore.SnomedDatastoreActivator;
import com.b2international.snowowl.snomed.datastore.SnomedRefSetLookupService;
import com.b2international.snowowl.snomed.datastore.index.mapping.SnomedMappings;
import com.b2international.snowowl.snomed.datastore.services.ISnomedConceptNameProvider;
import com.b2international.snowowl.snomed.exporter.server.ComponentExportType;
import com.b2international.snowowl.snomed.exporter.server.SnomedRf2Exporter;
import com.b2international.snowowl.snomed.exporter.server.SnomedRfFileNameBuilder;
import com.b2international.snowowl.snomed.snomedrefset.SnomedRefSet;
import com.b2international.snowowl.snomed.snomedrefset.SnomedRefSetType;

/**
 * Base for all SNOMED&nbsp;CT reference set RF2 exporters.
 *
 */
public class SnomedRefSetExporter extends SnomedCompositeExporter implements SnomedRf2Exporter {

	protected static final Set<String> COMMON_FIELDS_TO_LOAD = SnomedMappings.fieldsToLoad()
			.active()
			.effectiveTime()
			.module()
			.memberReferenceSetId()
			.memberReferencedComponentId()
			.field(REFERENCE_SET_MEMBER_UUID)
			.build();
	
	private final String refSetId;

	private SnomedRefSetType type;

	public SnomedRefSetExporter(final SnomedExportConfiguration configuration, final String refSetId, final SnomedRefSetType type) {
		super(configuration);
		this.refSetId = checkNotNull(refSetId, "refSetId");
		this.type = checkNotNull(type, "type");
	}

	@Override
	public Set<String> getFieldsToLoad() {
		return COMMON_FIELDS_TO_LOAD;
	}
	
	@Override
	public String transform(final Document doc) {
		final StringBuilder sb = new StringBuilder();
		sb.append(doc.get(REFERENCE_SET_MEMBER_UUID));
		sb.append(HT);
		sb.append(formatEffectiveTime(SnomedMappings.effectiveTime().getValue(doc)));
		sb.append(HT);
		sb.append(SnomedMappings.active().getValue(doc));
		sb.append(HT);
		sb.append(SnomedMappings.module().getValueAsString(doc));
		sb.append(HT);
		sb.append(SnomedMappings.memberRefSetId().getValueAsString(doc));
		sb.append(HT);
		sb.append(SnomedMappings.memberReferencedComponentId().getValue(doc));
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
		switch (type) {
			case SIMPLE: //$FALL-THROUGH$
			case ASSOCIATION: //$FALL-THROUGH$
			case CONCRETE_DATA_TYPE: //$FALL-THROUGH$
			case QUERY: //$FALL-THROUGH$
			case ATTRIBUTE_VALUE:  
				return RF2_CONTENT_REFERENCE_SET_RELATIVE_DIR;
			case EXTENDED_MAP: //$FALL-THROUGH$
			case SIMPLE_MAP: //$FALL-THROUGH$
			case COMPLEX_MAP: 
				return RF2_MAP_REFERENCE_SET_RELATIVE_DIR;
			case DESCRIPTION_TYPE: //$FALL-THROUGH$
			case MODULE_DEPENDENCY: 
				return RF2_METADATA_REFERENCE_SET_RELATIVE_DIR;
			case LANGUAGE: 
				return RF2_LANGUAGE_REFERENCE_SET_RELATIVE_DIR;
			default: throw new IllegalArgumentException("Unknown SNOMED CT reference set type: " + type);
		}
	}
	
	@Override
	public String getFileName() {
		final ICDOConnection connection = getServiceForClass(ICDOConnectionManager.class).getByUuid(SnomedDatastoreActivator.REPOSITORY_UUID);
		final IBranchPath branchPath = getConfiguration().getCurrentBranchPath();
		final String refSetName = getServiceForClass(ISnomedConceptNameProvider.class).getComponentLabel(branchPath, refSetId);
		return CDOUtils.apply(new CDOTransactionFunction<String>(connection, branchPath) {
			@Override
			protected String apply(final CDOTransaction transaction) {
				final SnomedRefSet refSet = new SnomedRefSetLookupService().getComponent(getRefSetId(), transaction);
				return buildRefSetFileName(refSetName, refSet);
			}
		});
	}
	
	protected String buildRefSetFileName(final String refSetName, final SnomedRefSet refSet) {
		return SnomedRfFileNameBuilder.buildRefSetFileName(getConfiguration(), refSetName, refSet);
	}

	@Override
	protected Query getSnapshotQuery() {
		return SnomedMappings.newQuery().memberRefSetId(getRefSetId()).matchAll();
	}
	
	/**Returns with the reference set identifier concept ID.*/
	protected String getRefSetId() {
		return refSetId;
	}
}
