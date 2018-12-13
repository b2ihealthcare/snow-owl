/*
 * Copyright 2011-2018 B2i Healthcare Pte Ltd, http://b2i.sg
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
package com.b2international.snowowl.snomed.datastore.internal.rf2;

import java.util.Collection;
import java.util.List;

import com.b2international.commons.http.ExtendedLocale;
import com.b2international.snowowl.snomed.snomedrefset.SnomedRefSetType;
import com.google.common.collect.Lists;

/**
 * Model used in the reference set DSV export process.
 */
public class SnomedRefSetDSVExportModel extends SnomedExportModel {

	private String refSetId;
	private SnomedRefSetType refSetType;

	private boolean includeDescriptionId;
	private boolean includeRelationshipTargetId;
	private boolean includeInactiveMembers;
	private List<AbstractSnomedDsvExportItem> exportItems = Lists.newArrayList();
	private String delimiter;
	private long branchBase;
	// used for simple and complex map type refsets
	private String branchPath;
	
	private List<ExtendedLocale> locales;

	public SnomedRefSetDSVExportModel() {
		super();
	}
	
	public SnomedRefSetType getRefSetType() {
		return refSetType;
	}

	public String getRefSetId() {
		return refSetId;
	}
	
	public void setRefSetId(String refSetId) {
		this.refSetId = refSetId;
	}
	
	public void setLocales(List<ExtendedLocale> locales) {
		this.locales = locales;
	}
	
	public List<ExtendedLocale> getLocales() {
		return locales;
	}

	public String getDelimiter() {
		return delimiter;
	}

	public void setDelimiter(String delimiter) {
		this.delimiter = delimiter;
	}

	public long getBranchBase() {
		return branchBase;
	}

	public void setBranchBase(long branchBase) {
		this.branchBase = branchBase;
	}

	public List<AbstractSnomedDsvExportItem> getExportItems() {
		return exportItems;
	}

	public void clearExportItems() {
		exportItems.clear();
	}

	public void addExportItem(AbstractSnomedDsvExportItem exportItem) {
		exportItems.add(exportItem);
	}
	
	public void addExportItems(Collection<AbstractSnomedDsvExportItem> items) {
		exportItems.addAll(items);
	}

	public String getBranchPath() {
		return branchPath;
	}
	
	public void setIncludeDescriptionId(boolean includeDescriptionId) {
		this.includeDescriptionId = includeDescriptionId;
	}

	public boolean includeDescriptionId() {
		return includeDescriptionId;
	}
	
	public void setIncludeRelationshipTargetId(boolean includeRelationshipTargetId) {
		this.includeRelationshipTargetId = includeRelationshipTargetId;
	}
	
	public boolean includeRelationshipTargetId() {
		return includeRelationshipTargetId;
	}
	
	public void setIncludeInactiveMembers(boolean includeInactiveMembers) {
		this.includeInactiveMembers = includeInactiveMembers;
	}
	
	public boolean includeInactiveMembers() {
		return includeInactiveMembers;
	}
	
	public void setBranchPath(String branchPath) {
		this.branchPath = branchPath;
	}
	
}