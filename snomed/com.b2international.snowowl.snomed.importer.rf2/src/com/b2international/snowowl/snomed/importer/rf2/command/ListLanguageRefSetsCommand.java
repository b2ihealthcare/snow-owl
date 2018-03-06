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
package com.b2international.snowowl.snomed.importer.rf2.command;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.stream.Collectors;

import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.osgi.framework.console.CommandInterpreter;

import com.b2international.snowowl.core.branch.Branch;
import com.b2international.snowowl.snomed.common.ContentSubType;
import com.b2international.snowowl.snomed.importer.net4j.ImportConfiguration;
import com.b2international.snowowl.snomed.importer.net4j.ImportConfiguration.ImportSourceKind;
import com.b2international.snowowl.snomed.importer.release.ReleaseFileSet;
import com.b2international.snowowl.snomed.importer.release.ReleaseFileSet.ReleaseComponentType;
import com.b2international.snowowl.snomed.importer.release.ReleaseFileSetSelectors;
import com.b2international.snowowl.snomed.importer.rf2.util.ImportUtil;
import com.b2international.snowowl.snomed.importer.rf2.util.SnomedRefSetNameCollector;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

public class ListLanguageRefSetsCommand extends AbstractRf2ImporterCommand {

	public ListLanguageRefSetsCommand() {
		super("rf2_languages", "<path>", "Lists all available language type reference set identifiers in a release archive",
				new String[] { "<path>\t\tSpecifies the release archive to scan." });
	}

	@Override
	public void execute(final CommandInterpreter interpreter) {

		final String archivePath = interpreter.nextArgument();

		if (archivePath == null) {
			printDetailedHelp(interpreter);
			return;
		}

		final File archiveFile = new File(archivePath);

		if (archiveFile.getPath().trim().length() < 1) {
			interpreter.println("Archive path is invalid.");
			return;
		}

		if (!archiveFile.canRead()) {
			interpreter.println("Archive file '" + archiveFile.getPath() + "' does not exist or is not readable.");
			return;
		}

		final List<String> zipFiles = ImportUtil.listZipFiles(archiveFile);
		
		ContentSubType contentSubType = null;
		ReleaseFileSet archiveFileSet = null;
		
		final List<ContentSubType> contentSubTypes = Lists.newArrayList(ContentSubType.values());
		Collections.sort(contentSubTypes); //natural order
		Collections.reverse(contentSubTypes); //pessimistic attempt
		
		for (final ContentSubType currentSubType : contentSubTypes) {
			
			archiveFileSet = ReleaseFileSetSelectors.SELECTORS.getFirstApplicable(zipFiles, currentSubType);
			
			if (null != archiveFileSet) {
				contentSubType = currentSubType;
				break;
			}
			
		}
		
		if (archiveFileSet == null) {
			interpreter.println("Archive file '" + archiveFile.getPath() + "' is an unrecognized release archive.");
			return;
		}

		final Map<String, String> languageRefsetToLabelMap = Maps.newHashMap();
		final ImportConfiguration config = new ImportConfiguration(Branch.MAIN_PATH);
		
		final Set<File> languageRefSetFiles = archiveFileSet
				.getAllFileName(zipFiles, ReleaseComponentType.LANGUAGE_REFERENCE_SET, contentSubType)
				.stream()
				.map(fileName -> new File(fileName))
				.collect(Collectors.toSet());
		
		final Set<File> descriptionFiles = archiveFileSet
				.getAllFileName(zipFiles, ReleaseComponentType.DESCRIPTION, contentSubType)
				.stream()
				.map(fileName -> new File(fileName))
				.collect(Collectors.toSet());

		descriptionFiles.forEach(file -> config.addDescriptionFile(file));
		
		Set<URL> refsetUrls = languageRefSetFiles.stream().map(file -> {
			try {
				return config.toURL(file);
			} catch (IOException e) {
				throw new RuntimeException(e);
			}
		}).collect(Collectors.toSet());
		
		final SnomedRefSetNameCollector provider = new SnomedRefSetNameCollector(refsetUrls, config, new NullProgressMonitor());
		provider.parse();
		
		// Setting up configuration only with the required fields
		config.setSourceKind(ImportSourceKind.ARCHIVE);
		config.setArchiveFile(archiveFile);
		
		for (final Entry<String, String> label : provider.getRefsetIdToLabelMap().entrySet()) {
			languageRefsetToLabelMap.put(label.getKey(), label.getValue());
		}
		
		if (languageRefsetToLabelMap.isEmpty()) {
			interpreter.println("No language reference sets could be found in release archive.");
			return;
		}

		interpreter.println("\n---------------------------------------------------------------------\n");

		for (final Entry<String, String> label : languageRefsetToLabelMap.entrySet()) {
			final StringBuilder sb = new StringBuilder();
			sb.append("\t");
			sb.append(label.getKey());
			sb.append(" | ");
			sb.append(label.getValue().trim());
			sb.append(" | ");
			interpreter.println(sb.toString());
		}
		
	}
}