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
package com.b2international.snowowl.snomed.exporter.server.refset;

import java.io.File;
import java.util.List;

import com.b2international.commons.CompareUtils;
import com.b2international.snowowl.core.ApplicationContext;
import com.b2international.snowowl.core.api.IBranchPath;
import com.b2international.snowowl.snomed.SnomedConstants.LanguageCodeReferenceSetIdentifierMapping;
import com.b2international.snowowl.snomed.datastore.SnomedRefSetLookupService;
import com.b2international.snowowl.snomed.datastore.index.SnomedClientIndexService;
import com.b2international.snowowl.snomed.datastore.index.entry.SnomedRefSetIndexEntry;
import com.b2international.snowowl.snomed.datastore.index.refset.SnomedRefSetIndexQueryAdapter;
import com.b2international.snowowl.snomed.datastore.services.ISnomedConceptNameProvider;
import com.b2international.snowowl.snomed.exporter.server.SnomedRf1Exporter;
import com.b2international.snowowl.snomed.exporter.server.SnomedRfFileNameBuilder;
import com.b2international.snowowl.snomed.exporter.server.sandbox.SnomedExportConfiguration;
import com.b2international.snowowl.snomed.snomedrefset.SnomedRefSetType;

/**
 * Abstract subset RF1 exporter for SNOMED&nbsp;CT simple type and language type reference sets.
 * @see SnomedRf1Exporter
 * @see AbstractSnomedRefSetExporter
 */
public abstract class AbstractSnomedSubsetExporter implements SnomedRf1Exporter {

	/**
	 * Returns {@code true} if the exported subset is a language type SNOMED&nbsp;CT reference set. Otherwise it returns with {@code false}.
	 * @return {@code true} if the reference set is language type.
	 */
	//XXX language type reference set member cannot be created, modified or deleted on branch. so we can lookup reference sets in index
	protected static boolean isLanguageType(final String refSetId) {
		final List<SnomedRefSetIndexEntry> refSets = getRefSetById(refSetId);
		if (CompareUtils.isEmpty(refSets)) {
			return false;
		}
		final SnomedRefSetIndexEntry refSet = refSets.get(0);
		
		//if null it cannot be language type reference set
		return null == refSet ? false : SnomedRefSetType.LANGUAGE.equals(refSet.getType()); 
	}

	/*searched a reference set by its identifier concept ID in the index.*/
	private static List<SnomedRefSetIndexEntry> getRefSetById(final String refSetId) {
		final SnomedClientIndexService indexService = ApplicationContext.getInstance().getService(SnomedClientIndexService.class);
		final SnomedRefSetIndexQueryAdapter queryAdapter = new SnomedRefSetIndexQueryAdapter(SnomedRefSetIndexQueryAdapter.SEARCH_BY_ID, refSetId, null);
		return indexService.search(queryAdapter, 1);
	}

	/**
	 * Returns with the language code for the language type reference set identified by it identifier concept ID.
	 * @param refSetId the identifier concept ID.
	 * @return the language code.
	 */
	protected static String getLanguageCode(final String refSetId) {
		return LanguageCodeReferenceSetIdentifierMapping.getLanguageCode(refSetId).toUpperCase().replaceAll("EN", "en");
	}
	
	private final String folderName;
	private String label;
	private short referencedComponentType;
	protected SnomedExportConfiguration configuration;
	private String refSetId;

	protected AbstractSnomedSubsetExporter(final SnomedExportConfiguration configuration, final String refSetId) {
		this.configuration = configuration;
		this.refSetId = refSetId;
		referencedComponentType = getReferencedComponentType(refSetId);
		label = ApplicationContext.getServiceForClass(ISnomedConceptNameProvider.class).getComponentLabel(getBranchPath(), refSetId);
		if (isLanguageType(refSetId)) {
			folderName = "Language-" + getLanguageCode(refSetId);
		} else {
			folderName = String.valueOf(SnomedRfFileNameBuilder.toCamelCase(label));
		}
	}

	protected IBranchPath getBranchPath() {
		return this.configuration.getCurrentBranchPath();
	}

	protected String getRefSetId() {
		return refSetId;
	}
	
	@Override
	public SnomedExportConfiguration getConfiguration() {
		return configuration;
	}
	
	/*
	 * (non-Javadoc)
	 * @see com.b2international.snowowl.snomed.exporter.refset.AbstractSnomedRefSetExporter#getRelativeDirectory()
	 */
	@Override
	public String getRelativeDirectory() {
		return RF1_SUBSET_RELATIVE_DIRECTORY + File.separatorChar + folderName;
	}

	/**
	 * Returns with the subset type based on the referenced component type of the reference set.
	 * @return the subset type of the reference set.
	 */
	protected String getSubsetType() {
		switch (referencedComponentType) {
			case 100: return "1";
			case 101: return "2";
			case 102: return "3";
			default: throw new IllegalArgumentException("Unknown referenced component type: " + referencedComponentType);
		}
	}
	
	/**
	 * Returns with the label of the reference set. Same as the preferred term of the reference set identifier concept.
	 * @return the label of the reference set.
	 */
	protected String getLabel() {
		return label;
	}
	
	/**
	 * Returns with the folder name for the subset.
	 * @return the name of the folder.
	 */
	protected String getFolderName() {
		return folderName;
	}

	/*returns with the referenced component type for the reference set*/
	private short getReferencedComponentType(final String refSetId) {
		return new SnomedRefSetLookupService().getComponent(getBranchPath(), refSetId).getReferencedComponentType();
	}
	
}