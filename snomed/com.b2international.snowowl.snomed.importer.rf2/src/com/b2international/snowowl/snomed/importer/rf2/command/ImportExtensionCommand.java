/*******************************************************************************
 * Copyright (c) 2016 B2i Healthcare. All rights reserved.
 *******************************************************************************/
package com.b2international.snowowl.snomed.importer.rf2.command;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Properties;
import java.util.Set;

import org.eclipse.osgi.framework.console.CommandInterpreter;

import com.b2international.commons.CompareUtils;
import com.b2international.commons.ConsoleProgressMonitor;
import com.b2international.snowowl.core.ApplicationContext;
import com.b2international.snowowl.core.api.IBranchPath;
import com.b2international.snowowl.datastore.BranchPathUtils;
import com.b2international.snowowl.eventbus.IEventBus;
import com.b2international.snowowl.importer.ImportException;
import com.b2international.snowowl.server.console.CommandLineAuthenticator;
import com.b2international.snowowl.snomed.common.ContentSubType;
import com.b2international.snowowl.snomed.datastore.SnomedInternationalCodeSystemFactory;
import com.b2international.snowowl.snomed.datastore.request.SnomedRequests;
import com.b2international.snowowl.snomed.importer.net4j.SnomedImportResult;
import com.b2international.snowowl.snomed.importer.net4j.SnomedValidationDefect;
import com.b2international.snowowl.snomed.importer.rf2.util.ImportUtil;
import com.b2international.snowowl.terminologymetadata.CodeSystem;
import com.google.common.base.Predicate;
import com.google.common.collect.FluentIterable;

/**
 * Import release command for the OSGi console
 */
public class ImportExtensionCommand extends AbstractRf2ImporterCommand {

	public ImportExtensionCommand() {
		super("rf2_extension",
				"-l <languageRefSetId> -t <type> -bv <base int. version tag> -cv <true|false> <archive_path> <extension_metadata_file_path>",
				"Imports extension terminology and reference sets from a release archive onto the top of a base international release.",
				new String[] { "-l <languageRefSetId>\tThe language reference set identifier to use for component labels.",
						"-t <type>\t\tThe import type (FULL, SNAPSHOT or DELTA).",
						"-bv <international release version tag>\t\tThe existing international version to import the extension onto. For example: '2016-01-01'.",
						"-cv <true|false>\t\tCreates versions for each effective time found in the extension release.",
						"<archive_path>\t\tSpecifies the release archive to import (must be a .zip file with a supported internal structure, such as the release archive of the International Release).",
						"<extension_metadata_file_path>\t\tPath to the metadata file describing the extension." });

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.b2international.snowowl.snomed.importer.rf2.command.
	 * AbstractRf2ImporterCommand#execute(org.eclipse.osgi.framework.console.
	 * CommandInterpreter)
	 */
	@Override
	public void execute(CommandInterpreter interpreter) {

		// ImportUtil importUtil = new ImportUtil();

		// language refset parameter
		if (!"-l".equals(interpreter.nextArgument())) {
			printDetailedHelp(interpreter);
			return;
		}

		final String languageRefSetId = interpreter.nextArgument();

		if (languageRefSetId == null || languageRefSetId.isEmpty()) {
			interpreter.println("Language reference set identifier expected.");
			printDetailedHelp(interpreter);
			return;
		}

		ContentSubType contentSubType = null;

		// Type parameter
		if ("-t".equals(interpreter.nextArgument())) {

			final String subType = interpreter.nextArgument();
			boolean subTypeSet = false;

			for (final ContentSubType candidate : ContentSubType.values()) {
				if (candidate.name().equalsIgnoreCase(subType)) {
					contentSubType = candidate;
					subTypeSet = true;
					break;
				}
			}

			if (!subTypeSet) {
				interpreter.println("Invalid import type '" + subType + ".");
				printDetailedHelp(interpreter);
				return;
			}
		}

		final String baseVersionTag;

		String branchSwitch = interpreter.nextArgument();
		if ("-bv".equals(branchSwitch)) {
			baseVersionTag = interpreter.nextArgument();

			if (!BranchPathUtils.exists(SNOMED_STORE, IBranchPath.MAIN_BRANCH + IBranchPath.SEPARATOR_CHAR + baseVersionTag)) {
				interpreter.println("Invalid international version tag: '" + baseVersionTag + "'.");
				printDetailedHelp(interpreter);
				return;
			}
		} else {
			interpreter.println("Expected paramater type '-bv', received instead: '" + branchSwitch + "'.");
			printDetailedHelp(interpreter);
			return;
		}

		String createVersionSwitch = interpreter.nextArgument();

		boolean toCreateVersion;
		if ("-cv".equals(createVersionSwitch)) {
			String toCreateVersionString = interpreter.nextArgument();

			if (toCreateVersionString.equalsIgnoreCase("true")) {
				toCreateVersion = true;
			} else if (toCreateVersionString.equalsIgnoreCase("false")) {
				toCreateVersion = false;

			} else {
				interpreter.println("Could not parse " + toCreateVersionString + " as a boolean.");
				printDetailedHelp(interpreter);
				return;
			}
		} else {
			interpreter.println("Expected paramater type '-cv' received instead: '" + createVersionSwitch + "'.");
			printDetailedHelp(interpreter);
			return;
		}

		final String archivePath = interpreter.nextArgument();
		if (archivePath == null) {
			interpreter.println("No archive path specified.");
			printDetailedHelp(interpreter);
			return;
		}

		final File archiveFile = new File(archivePath);
		
		//TODO: change this
		final CodeSystem snomedCodeSystem;

		final String metadataFilePath = interpreter.nextArgument();
		if (metadataFilePath == null) {
			interpreter.println("No metadata file path specified.");
			printDetailedHelp(interpreter);
			return;
		} else {
			Path configLocation = Paths.get(metadataFilePath);
			try (InputStream stream = Files.newInputStream(configLocation)) {
				Properties config = new Properties();
				config.load(stream);
				snomedCodeSystem = createCodeSystem(config);
			} catch (IOException e) {
				interpreter.printStackTrace(e);
				return;
			}
		}

		try {
			final CommandLineAuthenticator authenticator = new CommandLineAuthenticator();

			if (!authenticator.authenticate(interpreter)) {
				return;
			}

			//create the new branch for the extension
			IEventBus eventBus = ApplicationContext.getServiceForClass(IEventBus.class);
			
			IBranchPath parentBranchPath = BranchPathUtils.createPath(BranchPathUtils.createMainPath(), baseVersionTag);
			SnomedRequests.branching().prepareCreate().setParent(parentBranchPath.getPath()).setName(snomedCodeSystem.getShortName()).build().executeSync(eventBus);
			
			final String userId = authenticator.getUsername();
			
			ImportUtil importUtil = new ImportUtil();
			IBranchPath extensionBranchPath = BranchPathUtils.createPath(parentBranchPath, snomedCodeSystem.getShortName());
			final SnomedImportResult result = importUtil.doImport(userId, languageRefSetId, contentSubType, extensionBranchPath.getPath(), archiveFile, toCreateVersion, new ConsoleProgressMonitor());
			Set<SnomedValidationDefect> validationDefects = result.getValidationDefects();
			
			boolean criticalFound = FluentIterable.from(validationDefects).anyMatch(new Predicate<SnomedValidationDefect>() {

				@Override
				public boolean apply(SnomedValidationDefect defect) {
					return defect.getDefectType().isCritical();
				}
			});
			
			if (criticalFound) {
				interpreter.println("SNOMED CT import has been canceled due to critical errors found in the RF2 release.");
			} else {
				interpreter.println("SNOMED CT import has successfully finished.");
			}

		} catch (final ImportException e) {
			interpreter.printStackTrace(e);
		}
	}

	/**
	 * @param config
	 */
	private CodeSystem createCodeSystem(Properties properties) {
		CodeSystem snomedCodeSystem = new SnomedInternationalCodeSystemFactory().createNewCodeSystem();
		snomedCodeSystem.setShortName(properties.getProperty("shortname"));
		snomedCodeSystem.setCodeSystemOID(properties.getProperty("OID"));
		snomedCodeSystem.setLanguage(properties.getProperty("language"));
		snomedCodeSystem.setCitation(properties.getProperty("citation"));
		snomedCodeSystem.setIconPath(properties.getProperty("icon_path"));
		snomedCodeSystem.setMaintainingOrganizationLink(properties.getProperty("maintaining_org_link"));
		return snomedCodeSystem;
	}

}
