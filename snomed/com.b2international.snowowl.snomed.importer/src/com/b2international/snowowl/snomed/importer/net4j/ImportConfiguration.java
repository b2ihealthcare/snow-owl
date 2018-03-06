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

import static com.google.common.collect.Sets.newHashSet;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.Collection;
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
	public static final String CONTENTSUBTYPE = "contentSubType";
	public static final String CREATE_VERSIONS_PROPERTY = "createVersions";
	
	public enum ImportSourceKind {
		ARCHIVE,
		FILES;
	}
	
	private final String branchPath;
	private String codeSystemShortName;
	private boolean createVersions;
	
	private ImportSourceKind sourceKind = ImportSourceKind.ARCHIVE;
	private ContentSubType contentSubType = ContentSubType.DELTA;
	
	private File archiveFile;
	private ReleaseFileSet releaseFileSet;
	
	private File conceptFile;
	private Set<File> descriptionFiles = newHashSet();
	private Set<File> textDefinitionFiles = newHashSet();
	private File relationshipFile;
	private File statedRelationshipFile;
	
	private final Set<URL> refSetURLs = Sets.newHashSet(); 
	private final Set<String> excludedRefSetIds = Sets.newHashSet();
	
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

	public File getConceptFile() {
		return conceptFile;
	}

	public void setConceptFile(final File conceptFile) {
		this.conceptFile = conceptFile;
	}

	public Collection<File> getDescriptionFiles() {
		return descriptionFiles;
	}
	
	public boolean addDescriptionFile(File descriptionFile) {
		return descriptionFiles.add(descriptionFile);
	}

	public Collection<File> getTextDefinitionFiles() {
		return textDefinitionFiles;
	}
	
	public boolean addTextDefinitionFile(File textDefinitionFile) {
		return textDefinitionFiles.add(textDefinitionFile);
	}

	public File getRelationshipFile() {
		return relationshipFile;
	}

	public void setRelationshipFile(final File relationshipFile) {
		this.relationshipFile = relationshipFile;
	}
	
	public ImportSourceKind getSourceKind() {
		return sourceKind;
	}

	public void setSourceKind(final ImportSourceKind sourceKind) {
		this.sourceKind = sourceKind;
	}

	public ContentSubType getContentSubType() {
		return contentSubType;
	}

	public void setContentSubType(final ContentSubType contentSubType) {
		this.contentSubType = contentSubType;
	}
	
	public File getStatedRelationshipFile() {
		return statedRelationshipFile;
	}

	public void setStatedRelationshipFile(final File statedRelationshipFile) {
		this.statedRelationshipFile = statedRelationshipFile;
	}
	
	public URL toURL(final File releaseFile) throws IOException {
		
		if (releaseFile == null || releaseFile.getPath().isEmpty()) {
			return null;
		}
		
		switch (getSourceKind()) {
		
			case ARCHIVE:
				return ZipURLHandler.createURL(getArchiveFile(), new Path(releaseFile.getPath()).toString());

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
	
	public void addRefSetURL(final URL refSetURL) {
		refSetURLs.add(refSetURL);
	}
	
	public void clear() {
		conceptFile = null;
		relationshipFile = null;
		statedRelationshipFile = null;
		
		descriptionFiles.clear();
		textDefinitionFiles.clear();
		
		refSetURLs.clear();
		excludedRefSetIds.clear();
	}

	public Set<URL> getRefSetUrls() {
		return refSetURLs;
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

	public boolean isValidReleaseFile(final File releaseFile) {
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
