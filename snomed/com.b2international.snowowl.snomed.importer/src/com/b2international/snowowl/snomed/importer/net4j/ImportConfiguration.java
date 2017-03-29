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
package com.b2international.snowowl.snomed.importer.net4j;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.Map;
import java.util.Set;

import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;

import com.b2international.commons.ZipURLHandler;
import com.b2international.snowowl.snomed.common.ContentSubType;
import com.b2international.snowowl.snomed.importer.release.ReleaseFileSet;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;

/**
 * Model class for SNOMED CT full release import configuration.
 */
public final class ImportConfiguration {

	public static final String ARCHIVE_FILE_PROPERTY = "archiveFile";
	public static final String ROOT_FILE_PROPERTY = "rootFile";
	public static final String CONCEPTS_FILE_PROPERTY = "conceptsFile";
	public static final String DESCRIPTIONS_FILE_PROPERTY = "descriptionsFile";
	public static final String RELATIONSHIPS_FILE_PROPERTY = "relationshipsFile";
	public static final String STATED_RELATIONSHIPS_FILE_PROPERTY = "statedRelationshipsFile";
	public static final String LANGUAGE_REF_SET_FILE_PROPERTY = "languageRefSetFile";
	public static final String LANGUAGE_REF_SET_ID_PROPERTY = "languageRefSetId";
	public static final String IMPORT_SOURCE_KIND_PROPERTY = "sourceKind";
	public static final String DESCRIPTION_TYPE_REFSET_FILE_PROPERTY = "descriptionType";
	public static final String TEXT_DEFINITION_FILE_PROPERTY = "textDefinitionFile";
	public static final String VERSION_PROPERTY = "version";
	public static final String CREATE_VERSIONS_PROPERTY = "createVersions";
	
	public enum ImportSourceKind {
		ARCHIVE,
		ROOT_DIRECTORY,
		FILES;
	}
	
	private File archiveFile;
	private File rootFile;
	private File conceptsFile;
	private File descriptionsFile;
	private File relationshipsFile;
	private File statedRelationshipsFile;
	private File languageRefSetFile;
	private File descriptionType;
	private File textDefinitionFile;
	
	private ImportSourceKind sourceKind = ImportSourceKind.ARCHIVE;
	private ContentSubType version = ContentSubType.DELTA;
	private final Set<URL> additionalRefSetURLs = Sets.newHashSet(); 
	private final Set<String> excludedRefSetIds = Sets.newHashSet();
	
	private String codeSystemShortName;
	private boolean createVersions;
	
	/* Not bound */
	private ReleaseFileSet releaseFileSet;
	
	private final String branchPath;
	private final Map<String, String> releaseFileNameMappings = Maps.newHashMap();

	public ImportConfiguration(final String branchPath) {
		this.branchPath = branchPath;
	}
	
	public boolean isCreateVersions() {
		return createVersions;
	}
	
	public void setCreateVersions(boolean createVersions) {
		this.createVersions = createVersions;
	}
	
	public File getArchiveFile() {
		return archiveFile;
	}

	public void setArchiveFile(final File archiveFile) {
		this.archiveFile = archiveFile;
	}

	public File getRootFile() {
		return rootFile;
	}

	public void setRootFile(final File rootFile) {
		this.rootFile = rootFile;
	}

	public File getConceptsFile() {
		return conceptsFile;
	}

	public void setConceptsFile(final File conceptsFile) {
		this.conceptsFile = conceptsFile;
	}

	public File getDescriptionsFile() {
		return descriptionsFile;
	}

	public void setDescriptionsFile(final File descriptionsFile) {
		this.descriptionsFile = descriptionsFile;
	}

	public File getRelationshipsFile() {
		return relationshipsFile;
	}

	public void setRelationshipsFile(final File relationshipsFile) {
		this.relationshipsFile = relationshipsFile;
	}

	public File getLanguageRefSetFile() {
		return languageRefSetFile;
	}

	public void setLanguageRefSetFile(final File languageRefSetFile) {
		this.languageRefSetFile = languageRefSetFile;
	}

	public ImportSourceKind getSourceKind() {
		return sourceKind;
	}

	public void setSourceKind(final ImportSourceKind sourceKind) {
		this.sourceKind = sourceKind;
	}

	public ContentSubType getVersion() {
		return version;
	}

	public void setVersion(final ContentSubType version) {
		this.version = version;
	}
	
	public File getStatedRelationshipsFile() {
		return statedRelationshipsFile;
	}

	public void setStatedRelationshipsFile(final File statedRelationshipsFile) {
		this.statedRelationshipsFile = statedRelationshipsFile;
	}
	
	public File getDescriptionType() {
		return descriptionType;
	}

	public void setDescriptionType(final File descriptionType) {
		this.descriptionType = descriptionType;
	}
	
	public File getTextDefinitionFile() {
		return textDefinitionFile;
	}

	public void setTextDefinitionFile(final File textDefinitionFile) {
		this.textDefinitionFile = textDefinitionFile;
	}

	public URL toURL(final File releaseFile) throws IOException {
		
		if (releaseFile == null || releaseFile.getPath().isEmpty()) {
			return null;
		}
		
		switch (getSourceKind()) {
		
			case ARCHIVE:
				return ZipURLHandler.createURL(getArchiveFile(), new Path(releaseFile.getPath()).toString());

			case ROOT_DIRECTORY:
				return new File(getRootFile(), releaseFile.getPath()).toURI().toURL();

			case FILES:
				return releaseFile.toURI().toURL();
				
			default:
				throw new IllegalStateException("Unhandled source kind '" + getSourceKind() + "'.");
		}
	}

	public ReleaseFileSet getReleaseFileSet() {
		return releaseFileSet;
	}

	public void setReleaseFileSet(final ReleaseFileSet releaseFileSet) {
		this.releaseFileSet = releaseFileSet;
	}	
	
	public void addRefSetSource(final URL refSetURL) {
		additionalRefSetURLs.add(refSetURL);
	}
	
	public void clearRefSetSettings() {
		additionalRefSetURLs.clear();
		excludedRefSetIds.clear();
	}

	public Set<URL> getRefSetUrls() {
		return additionalRefSetURLs;
	}

	public Set<String> getExcludedRefSetIds() {
		return excludedRefSetIds;
	}

	public boolean isExcluded(final String refSetId) {
		return excludedRefSetIds.contains(refSetId);
	}
	
	public void excludeRefSet(final String refSetId) {
		excludedRefSetIds.add(refSetId);
	}
	
	public void includeRefSet(final String refSetId) {
		excludedRefSetIds.remove(refSetId);
	}

	public static boolean isValidReleaseFile(final File releaseFile) {
		return null != releaseFile && !releaseFile.getPath().isEmpty();
	}

	public String getMappedName(final String releaseRelativePath) {

		if (releaseFileNameMappings.containsKey(releaseRelativePath)) {
			return releaseFileNameMappings.get(releaseRelativePath);
		}
		
		final IPath releasePath = new Path(releaseRelativePath);
		return releasePath.removeTrailingSeparator().lastSegment();
	}
	
	public void addReleaseFileNameMapping(final String releaseRelativePath, final String mappedName) {
		releaseFileNameMappings.put(releaseRelativePath, mappedName);
	}
	
	public String getBranchPath() {
		return branchPath;
	}
	
	public String getCodeSystemShortName() {
		return codeSystemShortName;
	}

	public void setCodeSystemShortName(String codeSystemShortName) {
		this.codeSystemShortName = codeSystemShortName;
	}
}
