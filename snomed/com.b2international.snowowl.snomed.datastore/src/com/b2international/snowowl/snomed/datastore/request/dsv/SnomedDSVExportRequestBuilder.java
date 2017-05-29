/*
 * Copyright 2017 B2i Healthcare Pte Ltd, http://b2i.sg
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
package com.b2international.snowowl.snomed.datastore.request.dsv;

import java.util.List;
import java.util.UUID;

import com.b2international.commons.http.ExtendedLocale;
import com.b2international.snowowl.core.domain.BranchContext;
import com.b2international.snowowl.core.events.BaseRequestBuilder;
import com.b2international.snowowl.core.events.Request;
import com.b2international.snowowl.datastore.request.RevisionIndexRequestBuilder;
import com.b2international.snowowl.snomed.datastore.internal.rf2.AbstractSnomedDsvExportItem;
import com.b2international.snowowl.snomed.snomedrefset.SnomedRefSetType;


/**
 * @since 5.11
 */
public final class SnomedDSVExportRequestBuilder extends BaseRequestBuilder<SnomedDSVExportRequestBuilder, BranchContext, UUID> implements RevisionIndexRequestBuilder<UUID> {

	private String refSetId;
	private SnomedRefSetType refSetType;
	private int conceptSize;
	private boolean descriptionIdExpected;
	private boolean relationshipTargetExpected;
	private List<ExtendedLocale> locales;
	private String delimiter;
	private List<AbstractSnomedDsvExportItem> exportItems;
	
	SnomedDSVExportRequestBuilder() {}
	
	public SnomedDSVExportRequestBuilder setRefSetId(String refSetId) {
		this.refSetId = refSetId;
		return getSelf();
	}
	
	
	public SnomedDSVExportRequestBuilder setRefSetType(SnomedRefSetType refSetType) {
		this.refSetType = refSetType;
		return getSelf();
	}
	
	public SnomedDSVExportRequestBuilder setConceptSize(int conceptSize) {
		this.conceptSize = conceptSize;
		return getSelf();
	}
	
	public SnomedDSVExportRequestBuilder setDescriptionIdExpected(boolean descriptionIdExpected) {
		this.descriptionIdExpected = descriptionIdExpected;
		return getSelf();
	}
	
	public SnomedDSVExportRequestBuilder setRelationshipTargetExpected(boolean relationshipTargetExpected) {
		this.relationshipTargetExpected = relationshipTargetExpected;
		return getSelf();
	}

	public SnomedDSVExportRequestBuilder setLocales(List<ExtendedLocale> locales) {
		this.locales = locales;
		return getSelf();
	}
	
	public SnomedDSVExportRequestBuilder setDelimiter(String delimiter) {
		this.delimiter = delimiter;
		return getSelf();
	}
	
	public SnomedDSVExportRequestBuilder setExportItems(List<AbstractSnomedDsvExportItem> exportItems) {
		this.exportItems = exportItems;
		return getSelf();
	}
	
	@Override
	protected Request<BranchContext, UUID> doBuild() {
		SnomedDSVExportRequest req = new SnomedDSVExportRequest();
		req.setRefsetId(refSetId);
		req.setRefsetType(refSetType);
		req.setConceptSize(conceptSize);
		req.setDescriptionIdExpected(descriptionIdExpected);
		req.setRelationshipTargetExpected(relationshipTargetExpected);
		req.setLocales(locales);
		req.setDelimiter(delimiter);
		req.setExportItems(exportItems);
		return req;
	}
}
