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
package com.b2international.snowowl.snomed.datastore.internal.rf2;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.collect.Sets.newHashSet;

import java.io.File;
import java.util.Collection;
import java.util.Date;
import java.util.Set;

import com.b2international.commons.StringUtils;
import com.b2international.snowowl.core.ApplicationContext;
import com.b2international.snowowl.core.branch.Branch;
import com.b2international.snowowl.core.date.DateFormats;
import com.b2international.snowowl.core.date.Dates;
import com.b2international.snowowl.eventbus.IEventBus;
import com.b2international.snowowl.snomed.SnomedConstants.Concepts;
import com.b2international.snowowl.snomed.common.ContentSubType;
import com.b2international.snowowl.snomed.core.domain.SnomedDescription;
import com.b2international.snowowl.snomed.datastore.SnomedDatastoreActivator;
import com.b2international.snowowl.snomed.datastore.SnomedMapSetSetting;
import com.b2international.snowowl.snomed.datastore.request.SnomedRequests;
import com.google.common.base.CharMatcher;
import com.google.common.base.Strings;
import com.google.common.collect.Iterables;

/**
 * Model used in the SNOMED CT to RF1/RF2 export process.
 */
public final class SnomedRf2ExportModel extends SnomedExportModel {

	public static final String RELEASE_TYPE_PROPERTY = "releaseType";
	public static final String EXPORT_PATH_PROPERTY = "exportPath";

	/**
	 * Flag to indicate whether the export wizard is for export one single SNOMED CT reference set or other SNOMED CT components.
	 * <br>If {@code true} only one reference set is selected for export otherwise {@code false}.
	 */
	private final boolean singleRefSetExport;
	private final Set<String> refSetIds;
	private boolean exportToRf1;
	private boolean extendedDescriptionTypesForRf1;
	private boolean includeUnpublised;

	private String startEffectiveTime;
	private String endEffectiveTime;
	private ContentSubType releaseType;
	private Set<SnomedMapSetSetting> settings;
	private Set<String> modulesToExport;

	private String namespace;
	private final Branch branch;
	private String userId;
	private String unsetEffectiveTimeLabel;
	private String codeSystemShortName;
	private boolean extensionOnly;

	public SnomedRf2ExportModel(final String userId, 
			final Branch branch, 
			final ContentSubType contentSubtype, 
			final String namespace, 
			final Collection<String> refSets, 
			final boolean isSingleRefsetExport) {
		super();
		releaseType = contentSubtype;
		this.branch = checkNotNull(branch, "branch");

		singleRefSetExport = isSingleRefsetExport;
		
		settings = newHashSet();
		modulesToExport = newHashSet();
		this.userId = userId;
		unsetEffectiveTimeLabel = "";
		this.namespace = namespace;
		this.refSetIds = newHashSet(refSets);
		setExportPath(initExportPath());
	}

	public Set<String> getRefSetIds() {
		return refSetIds;
	}

	public ContentSubType getReleaseType() {
		return releaseType;
	}

	public boolean isSingleRefSetExport() {
		return singleRefSetExport;
	}

	public boolean isCoreComponentsToExport() {
		return !singleRefSetExport;
	}

	public boolean isRefSetsToExport() {
		return !getRefSetIds().isEmpty();
	}

	public boolean isExportToRf1() {
		return exportToRf1;
	}

	public void setExportToRf1(boolean exportToRf1) {
		this.exportToRf1 = exportToRf1;
	}

	public Set<SnomedMapSetSetting> getSettings() {
		return settings;
	}

	public void setSettings(Set<SnomedMapSetSetting> settings) {
		this.settings = settings;
	}

	public boolean isExtendedDescriptionTypesForRf1() {
		return extendedDescriptionTypesForRf1;
	}

	public void setExtendedDescriptionTypesForRf1(boolean extendedDescriptionTypesForRf1) {
		this.extendedDescriptionTypesForRf1 = extendedDescriptionTypesForRf1;
	}

	public Set<String> getModulesToExport() {
		return modulesToExport;
	}

	public String getStartEffectiveTime() {
		return startEffectiveTime;
	}

	public void setStartEffectiveTime(String startEffectiveTime) {
		this.startEffectiveTime = startEffectiveTime;
	}

	public String getEndEffectiveTime() {
		return endEffectiveTime;
	}

	public void setEndEffectiveTime(String endEffectiveTime) {
		this.endEffectiveTime = endEffectiveTime;
	}

	public boolean includeUnpublised() {
		return includeUnpublised;
	}

	public void setIncludeUnpublised(boolean includeUnpublised) {
		this.includeUnpublised = includeUnpublised;
	}

	public void setNamespace(String namespace) {
		this.namespace = namespace;
	}

	public String getNamespace() {
		return namespace;
	}

	public Branch getClientBranch() {
		return branch;
	}

	public String getUserId() {
		return userId;
	}

	public String getUnsetEffectiveTimeLabel() {
		return unsetEffectiveTimeLabel;
	}
	
	public void setUnsetEffectiveTimeLabel(final String unsetEffectiveTimeLabel) {
		this.unsetEffectiveTimeLabel = unsetEffectiveTimeLabel;
	}

	/**
	 * @return the codeSystemShortName
	 */
	public String getCodeSystemShortName() {
		return codeSystemShortName;
	}
	
	/**
	 * @param codeSystemShortName the codeSystemShortName to set
	 */
	public void setCodeSystemShortName(String codeSystemShortName) {
		this.codeSystemShortName = codeSystemShortName;
	}
	
	/**
	 * @return the extensionOnly
	 */
	public boolean isExtensionOnly() {
		return extensionOnly;
	}
	
	/**
	 * @param extensionOnly the extensionOnly to set
	 */
	public void setExtensionOnly(boolean extensionOnly) {
		this.extensionOnly = extensionOnly;
	}
	
	private String initExportPath() {
		StringBuilder sb = new StringBuilder();
		sb.append(System.getProperty("user.home"));
		sb.append(File.separatorChar);
		
		if (isSingleRefSetExport()) {
			
			final String refsetId = Iterables.getOnlyElement(getRefSetIds());
			String refsetName = SnomedRequests.prepareSearchDescription()
				.one()
				.filterByActive(true)
				.filterByConcept(refsetId)
				.filterByType("<<" + Concepts.SYNONYM)
				.filterByPreferredIn("<"+Concepts.REFSET_LANGUAGE_TYPE)
				.build(SnomedDatastoreActivator.REPOSITORY_UUID, getClientBranch().path())
				.execute(ApplicationContext.getServiceForClass(IEventBus.class))
				.then(input -> {
					SnomedDescription pt = Iterables.getOnlyElement(input, null);
					if (pt == null || Strings.isNullOrEmpty(pt.getTerm())) { 
						return refsetId; 
					} else { 
						return pt.getTerm(); 
					}
				})
				.getSync();
			
			sb.append(StringUtils.capitalizeFirstLetter(CharMatcher.BREAKING_WHITESPACE.removeFrom(refsetName)));
			
		} else {
			sb.append("SnomedCT_Release");
			sb.append('_');
			sb.append(getNamespace());
			sb.append('_');
			sb.append(Dates.formatByHostTimeZone(new Date(), DateFormats.COMPACT_LONG));
		}
		
		sb.append(".zip");
		return sb.toString();
	}
	
}
