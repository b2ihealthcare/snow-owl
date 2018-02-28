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
package com.b2international.snowowl.snomed.importer.rf2.command;

import java.io.File;
import java.io.IOException;

import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.eclipse.osgi.framework.console.CommandInterpreter;

import com.b2international.commons.CompareUtils;
import com.b2international.snowowl.core.api.IBranchPath;
import com.b2international.snowowl.core.api.SnowowlServiceException;
import com.b2international.snowowl.server.console.CommandLineAuthenticator;
import com.b2international.snowowl.snomed.importer.net4j.SnomedSubsetImportConfiguration;
import com.b2international.snowowl.snomed.importer.net4j.SnomedSubsetImportConfiguration.SubsetEntry;
import com.b2international.snowowl.snomed.importer.net4j.SnomedSubsetImportUtil;
import com.b2international.snowowl.snomed.importer.net4j.SnomedUnimportedRefSets;
import com.b2international.snowowl.snomed.importer.rf2.net4j.SnomedSubsetImporter;
import com.google.common.base.Strings;

/**
 * DSV import from OSGI console.
 * 
 */
public class ImportRefSetSubsetCommand extends AbstractRf2ImporterCommand {

	public ImportRefSetSubsetCommand() {
		super("dsv_refset", "<path> <hasHeader> <skipEmptyLines> <parentConcept>", "Imports reference sets in DSV format", new String[] {
				"<path>\t\tSpecifies the file to be used for importing.",
				"<hasHeader>\t\tSet to true if the source text file has a header row, false otherwise.",
				"<skipEmptyLines>\tSet to true if the source text file has empty lines which should be ignored, false otherwise.",
				"<parentConcept>\tUse this concept as parent of the newly imported reference set",
				"<namespace>\tUse this namespace to generate new RefSet component identifiers"
		});
	}

	@Override
	public void execute(CommandInterpreter interpreter) {
		
		String filePath = interpreter.nextArgument();
		String hasHeader = interpreter.nextArgument();
		String skipEmptyLines = interpreter.nextArgument();
		String parentRefSet = interpreter.nextArgument();
		String namespace = interpreter.nextArgument();
		String moduleId = interpreter.nextArgument();
		String languageRefSetId = interpreter.nextArgument();
		
		if (CompareUtils.isEmpty(filePath)) {
			printDetailedHelp(interpreter);
			return;
		}
		
		if (CompareUtils.isEmpty(hasHeader) || CompareUtils.isEmpty(skipEmptyLines) || CompareUtils.isEmpty(parentRefSet) || CompareUtils.isEmpty(namespace) || CompareUtils.isEmpty(moduleId) || CompareUtils.isEmpty(languageRefSetId)) {
			interpreter.println("All parameters should be specified!");
			return;
		}
		
		try {
			File refsetFile = new File(filePath);
			SnomedSubsetImportConfiguration importConfiguration = new SnomedSubsetImportConfiguration(IBranchPath.MAIN_BRANCH, namespace, moduleId, languageRefSetId);
			SubsetEntry entry = importConfiguration.addSubsetEntry(refsetFile.toURI().toURL());
			
			if (!refsetFile.canRead()) {
				interpreter.println("No file was given or the file cannot be read!");
			}
			
			SnomedSubsetImportUtil importUtil = new SnomedSubsetImportUtil();
			
			if (!importUtil.setProperties(entry)) {
				interpreter.println("No concept ID was found in the file!");
				return;
			}
			
			Boolean.parseBoolean(hasHeader);
			Boolean.parseBoolean(skipEmptyLines);
			
			if (!setParentRefSet(entry, parentRefSet)) {
				interpreter.println("The parentRefSet parameter should be between 0 and 4!");
				return;
			}
			
			final CommandLineAuthenticator authenticator = new CommandLineAuthenticator();
			if (!authenticator.authenticate(interpreter)) {
				return;
			}
			
			importUtil.updateNullProperties(entry);
			
			interpreter.println("Importing " + entry.getSubsetName() + "...");
			
			SnomedSubsetImporter importer = new SnomedSubsetImporter(IBranchPath.MAIN_BRANCH, authenticator.getUsername(), 
					entry.isHasHeader(), 
					entry.isSkipEmptyLines(), 
					entry.getIdColumnNumber(), 
					entry.getFirstConceptRowNumber(), 
					entry.getSheetNumber(), 
					entry.getRefSetParent(), 
					entry.getSubsetName(), 
					entry.getExtension(), 
					entry.getEffectiveTime(), 
					entry.getNamespace(), 
					entry.getModuleId(), 
					entry.getLanguageRefSetId(),
					entry.getFieldSeparator(), 
					entry.getQuoteCharacter(), 
					entry.getLineFeedCharacter(), 
					refsetFile);
			SnomedUnimportedRefSets unimportedRefSets = importer.doImport();
			
			if (0 == unimportedRefSets.getUnimportedRefSetMembers().size()) {
				interpreter.println("All concepts were imported.");
			} else {
				interpreter.println("\n" + unimportedRefSets.getUnimportedRefSetMembers().size() + " concepts were not imported.");
			}
			
		} catch (IOException e) {
			e.printStackTrace();
		} catch (InvalidFormatException e) {
			e.printStackTrace();
		} catch (SnowowlServiceException e) {
			e.printStackTrace();
		}
	}

	private boolean setParentRefSet(SubsetEntry entry, String parentRefSet) {
		if (Strings.isNullOrEmpty(parentRefSet)) {
			return false;
		}
		entry.setRefSetParent(parentRefSet);
		return true;
	}
}