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
package com.b2international.snowowl.snomed.importer.net4j;

import java.io.File;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;

import com.b2international.commons.StringUtils;
import com.b2international.snowowl.snomed.SnomedConstants.Concepts;
import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;

/**
 * Configuration to store and manage {@link SubsetEntry} and
 * {@link SnomedUnimportedRefSets} data for each subset file.
 * 
 * 
 */
public final class SnomedSubsetImportConfiguration {

	private static final String EFFECTIVE_TIME_FULL = "([1-9]){1}([0-9]){3}((0{1}[1-9]{1})|(1{1}[0-2]{1})){1}(0[1-9]|[12][0-9]|3[01]){1}";
	private static final String EFFECTIVE_TIME_MINUS_DAY = "([1-9]){1}([0-9]){3}((0{1}[1-9]{1})|(1{1}[0-2]{1})){1}";

	private Set<SubsetEntry> entries = Sets.newHashSet();
	private final List<SnomedUnimportedRefSets> unimportedRefSets = Lists.newArrayList();
	private final String branchPath;
	private final String namespace;
	private final String moduleId;
	private final String languageRefSetId;

	public SnomedSubsetImportConfiguration(String branchPath, String namespace, String moduleId, String languageRefSetId) {
		this.branchPath = branchPath;
		this.namespace = namespace;
		this.moduleId = moduleId;
		this.languageRefSetId = languageRefSetId;
	}
	
	public String getBranchPath() {
		return branchPath;
	}
	
	/**
	 * Gets the name of the subset from the {@code refSetURL} and creates a
	 * {@link SubsetEntry}.
	 * <p>
	 * Adds it to the {@link SnomedSubsetImportConfiguration} if it's not
	 * containing that subset already.
	 * 
	 * @param refSetURL
	 *            the {@link URL} of the subset file
	 * 
	 */
	public SubsetEntry addSubsetEntry(final URL refSetURL) {

		final SubsetEntry subsetEntry = new SubsetEntry(true);
		subsetEntry.setFileURL(refSetURL);
		setProperties(subsetEntry);
		
		File refSetFile;
		
		try {
			refSetFile = new File(refSetURL.toURI());
		} catch (final URISyntaxException e) {
			return null;
		}
		
		final IPath refSetPath = new Path(refSetFile.getAbsolutePath());

		subsetEntry.setExtension(Strings.nullToEmpty(refSetPath.getFileExtension()));
		setName(refSetPath.removeFileExtension().lastSegment(), subsetEntry);
		
		if (!alreadyContains(subsetEntry)) {
			getEntries().add(subsetEntry);
			return subsetEntry;
		}
		
		return null;
	}

	// Checks whether the user added the given subset before or not
	private boolean alreadyContains(final SubsetEntry subsetEntry) {
		boolean alreadyAdded = false;

		for (final SubsetEntry entry : getEntries()) {
			if (subsetEntry.getSubsetName().equals(entry.getSubsetName())) {
				alreadyAdded = true;
			}
		}
		
		return alreadyAdded;
	}

	// Sets the subset name
	private void setName(final String term, final SubsetEntry subsetEntry) {

		String subsetName = term;
		
		if (Concepts.CMT_REFSET_NAME_ID_MAP.containsKey(subsetName)) {
			subsetEntry.setSubsetName(subsetName);
			return;
		}
		
		subsetName = retainAfter(subsetName, "Concepts_");
		subsetName = retainAfter(subsetName, "Refset");
		subsetName = subsetName.replace('_', ' ');

		final String[] fileNameFields = subsetName.split(" ");
		subsetName = setPropertiesFromFileNameParts(subsetEntry, fileNameFields);
		
		subsetName = subsetName.trim();
		subsetName = StringUtils.splitCamelCaseAndCapitalize(subsetName);
		
		subsetEntry.setSubsetName(subsetName);
	}

	private String retainAfter(final String s, final String match) {
		final int firstOccurrence = s.indexOf(match);
		return (firstOccurrence > 0) ? s.substring(firstOccurrence + match.length()) : s;
	}

	// Sets the filename, namespace and the effective time for the subset
	private String setPropertiesFromFileNameParts(final SubsetEntry subsetEntry, final String[] fileNameParts) {
		
		final StringBuilder fileNameBuilder = new StringBuilder();
		
		for (String fileNamePart : fileNameParts) {
			
			if (fileNamePart.matches(EFFECTIVE_TIME_FULL) || fileNamePart.matches("\\("+ EFFECTIVE_TIME_FULL +"\\)")) {
				
				if (fileNamePart.startsWith("(") && fileNamePart.endsWith(")")) {
					fileNamePart = fileNamePart.substring(1, fileNamePart.length()-1);
				}
				
				subsetEntry.setEffectiveTime(fileNamePart);
				continue;
			}
			
			if (fileNamePart.contains("(") || fileNamePart.contains(")") || fileNamePart.matches(EFFECTIVE_TIME_MINUS_DAY)) {
				continue;
			}
			
			fileNameBuilder.append(fileNamePart);
		}
		
		return fileNameBuilder.toString();
	}

	// Sets the properties for the subset
	private void setProperties(final SubsetEntry subsetEntry) {
		subsetEntry.setHasHeader(true);
		subsetEntry.setSkipEmptyLines(true);
		subsetEntry.setNamespace(namespace);
		subsetEntry.setModuleId(moduleId);
		subsetEntry.setLanguageRefSetId(languageRefSetId);
	}

	/**
	 * Sets the {@code include} field of each {@link SubsetEntry} to the given
	 * value.
	 * 
	 * @param checked
	 *            a {@link Map} that contains the reference set name and the
	 *            value of the {@code include} field
	 */
	public void setChecked(final Map<String, Boolean> checked) {
		final Iterator<Entry<String, Boolean>> iterator = checked.entrySet().iterator();
		while (iterator.hasNext()) {
			final Entry<String, Boolean> next = iterator.next();
			for (final SubsetEntry entry : getEntries()) {
				if (next.getKey().equals(entry.getSubsetName())) {
					entry.setInclude(next.getValue());
				}
			}
		}
	}

	/**
	 * Sets all the {@link SubsetEntry} {@code include} field from the
	 * {@link SnomedSubsetImportConfiguration} to {@code true}.
	 */
	public void setAllChecked() {
		for (final SubsetEntry entry : getEntries()) {
			entry.setInclude(true);
		}
	}

	/**
	 * Checks whether all the SubsetEntries is not included or if there is at
	 * least one entry that is included.
	 * 
	 * @return {@code true} if there is one {@link SubsetEntry} which
	 *         {@code include} field is {@code true}, else {@code false}
	 */
	public boolean isAllUnchecked() {
		for (final SubsetEntry entry : getEntries()) {
			if (entry.isInclude()) {
				return false;
			}
		}
		return true;
	}

	/**
	 * Contains informations about a subset file.
	 * 
	 * 
	 */
	public class SubsetEntry {

		private String refSetParent;
		private int idColumnNumber;
		private int firstConceptRowNumber;
		private boolean isInclude;
		private boolean hasHeader;
		private boolean skipEmptyLines;
		private String quoteCharacter;
		private String fieldSeparator;
		private String subsetName;
		private String extension;
		private String lineFeedCharacter;
		private String namespace;
		private String effectiveTime;
		private URL fileURL;
		private List<String> headings;
		private Integer sheetNumber;
		private String moduleId;
		private String languageRefSetId;

		public SubsetEntry(final boolean isInclude) {
			this.isInclude = isInclude;
			headings = Lists.newArrayList();
		}

		public void setLanguageRefSetId(String languageRefSetId) {
			this.languageRefSetId = languageRefSetId;
		}
		
		public String getLanguageRefSetId() {
			return languageRefSetId;
		}

		public URL getFileURL() {
			return fileURL;
		}

		public void setFileURL(final URL fileURL) {
			this.fileURL = fileURL;
		}

		public String getSubsetName() {
			return subsetName;
		}

		public void setSubsetName(final String subsetName) {
			this.subsetName = subsetName;
		}

		public void setInclude(final boolean isInclude) {
			this.isInclude = isInclude;
		}
		
		public boolean isInclude() {
			return isInclude;
		}

		public boolean isHasHeader() {
			return hasHeader;
		}

		public void setHasHeader(final boolean hasHeader) {
			this.hasHeader = hasHeader;
		}

		public boolean isSkipEmptyLines() {
			return skipEmptyLines;
		}

		public void setSkipEmptyLines(final boolean skipEmptyLines) {
			this.skipEmptyLines = skipEmptyLines;
		}

		public String getExtension() {
			return extension;
		}

		public void setExtension(final String extension) {
			this.extension = extension;
		}

		public List<String> getHeadings() {
			return headings;
		}

		public void setHeadings(final List<String> headings) {
			this.headings = headings;
		}

		public String getFieldSeparator() {
			return fieldSeparator;
		}

		public void setFieldSeparator(final String fieldSeparator) {
			this.fieldSeparator = fieldSeparator;
		}

		public void setIdColumnNumber(final int selectionIndex) {
			this.idColumnNumber = selectionIndex;
		}
		
		public int getIdColumnNumber() {
			return idColumnNumber;
		}

		public void setQuoteCharacter(final String delimiterCharacter) {
			this.quoteCharacter = delimiterCharacter;
		}
		
		public String getQuoteCharacter() {
			return quoteCharacter;
		}

		public void setLineFeedCharacter(final String lineFeedCharacter) {
			this.lineFeedCharacter = lineFeedCharacter;
			
		}
		
		public String getLineFeedCharacter() {
			return lineFeedCharacter;
		}
		
		public void setNamespace(final String namespace) {
			this.namespace = namespace;
		}
		
		public String getNamespace() {
			return namespace;
		}

		public void setModuleId(String moduleId) {
			this.moduleId = moduleId;
		}
		
		public String getModuleId() {
			return moduleId;
		}
		
		public void setEffectiveTime(final String effectiveTime) {
			this.effectiveTime = effectiveTime;
		}
		
		public String getEffectiveTime() {
			return effectiveTime;
		}

		public void setSheetNumber(final Integer sheetNumber) {
			this.sheetNumber = sheetNumber;
		}
		
		public Integer getSheetNumber() {
			return sheetNumber;
		}

		public void setRefSetParent(String refSetParent) {
			this.refSetParent = refSetParent;
		}
		
		public String getRefSetParent() {
			return refSetParent;
		}
		
		public int getFirstConceptRowNumber() {
			return firstConceptRowNumber;
		}

		public void setFirstConceptRowNumber(final int firstConceptRowNumber) {
			this.firstConceptRowNumber = firstConceptRowNumber;
		}
	}

	public Set<SubsetEntry> getEntries() {
		return entries;
	}

	public void setEntries(final Set<SubsetEntry> entries) {
		this.entries = entries;
	}

	public List<SnomedUnimportedRefSets> getUnimportedRefSets() {
		return unimportedRefSets;
	}

}