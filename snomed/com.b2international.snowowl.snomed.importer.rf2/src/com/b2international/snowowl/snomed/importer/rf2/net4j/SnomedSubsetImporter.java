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
package com.b2international.snowowl.snomed.importer.rf2.net4j;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.text.ParseException;
import java.util.Date;
import java.util.List;
import java.util.Set;

import com.b2international.commons.CompareUtils;
import com.b2international.commons.StringUtils;
import com.b2international.commons.csv.CsvLexer.EOL;
import com.b2international.commons.csv.CsvParser;
import com.b2international.commons.csv.CsvSettings;
import com.b2international.commons.csv.RecordParserCallback;
import com.b2international.index.revision.RevisionIndex;
import com.b2international.snowowl.core.ApplicationContext;
import com.b2international.snowowl.core.RepositoryManager;
import com.b2international.snowowl.core.api.IBranchPath;
import com.b2international.snowowl.core.api.SnowowlRuntimeException;
import com.b2international.snowowl.core.api.SnowowlServiceException;
import com.b2international.snowowl.core.date.DateFormats;
import com.b2international.snowowl.core.date.EffectiveTimes;
import com.b2international.snowowl.core.domain.TransactionContext;
import com.b2international.snowowl.core.exceptions.BadRequestException;
import com.b2international.snowowl.core.terminology.ComponentCategory;
import com.b2international.snowowl.datastore.BranchPathUtils;
import com.b2international.snowowl.datastore.oplock.impl.DatastoreLockContextDescriptions;
import com.b2international.snowowl.datastore.server.snomed.ImportOnlySnomedTransactionContext;
import com.b2international.snowowl.snomed.Component;
import com.b2international.snowowl.snomed.SnomedConstants.Concepts;
import com.b2international.snowowl.snomed.common.SnomedTerminologyComponentConstants;
import com.b2international.snowowl.snomed.core.domain.CharacteristicType;
import com.b2international.snowowl.snomed.core.store.SnomedComponents;
import com.b2international.snowowl.snomed.datastore.SnomedConceptLookupService;
import com.b2international.snowowl.snomed.datastore.SnomedDatastoreActivator;
import com.b2international.snowowl.snomed.datastore.SnomedEditingContext;
import com.b2international.snowowl.snomed.datastore.index.entry.SnomedConceptDocument;
import com.b2international.snowowl.snomed.datastore.request.SnomedConceptCreateRequestBuilder;
import com.b2international.snowowl.snomed.datastore.request.SnomedRefSetCreateRequestBuilder;
import com.b2international.snowowl.snomed.datastore.request.SnomedRequests;
import com.b2international.snowowl.snomed.importer.net4j.SnomedUnimportedRefSets;
import com.b2international.snowowl.snomed.refset.core.automap.CsvVariableFieldCountParser;
import com.b2international.snowowl.snomed.refset.core.automap.XlsParser;
import com.b2international.snowowl.snomed.snomedrefset.SnomedRefSetMember;
import com.b2international.snowowl.snomed.snomedrefset.SnomedRefSetType;
import com.google.common.collect.Sets;

/**
 * Import simple type reference sets into Snow Owl from a SNOMED&nbsp;CT RF1
 * subset file.
 */
public class SnomedSubsetImporter {

	private static final org.slf4j.Logger LOGGER = org.slf4j.LoggerFactory.getLogger(SnomedSubsetImporter.class);
	
	private final boolean hasHeader;
	private final boolean skipEmptyLines;
	private final int idColumnNumber;
	private final int firstConceptRowNumber;
	private final int sheetNumber;
	private final EOL lineFeedCharacter;
	private final File importFile;
	private char fieldSeparatorCharacter;
	private final String fileExtension;
	private final IBranchPath branchPath;
	private final String quoteCharacter;
	private final String effectiveTime;
	private final String subsetName;
	private final String namespace;
	private final String userId;
	private final Set<String> importedConceptIds;
	private final String refSetParent;
	private final String moduleId;
	private final String languageRefSetId;

	public SnomedSubsetImporter(final String branchPath, 
			final String userId, 
			final boolean hasHeader, 
			final boolean skipEmptyLines, 
			final int idColumnNumber, 
			final int firstConceptRowNumber, 
			final int sheetNumber, 
			final String refSetParent, 
			final String subsetName, 
			final String fileExtension,
			final String effectiveTime, 
			final String namespace,
			final String moduleId,
			final String languageRefSetId,
			final String fieldSeparator, 
			final String quoteCharacter, 
			final String lineFeedCharacter, 
			final File importFile) throws SnowowlServiceException {
		this.userId = userId;
		this.branchPath = BranchPathUtils.createPath(branchPath);
		this.hasHeader = hasHeader;
		this.skipEmptyLines = skipEmptyLines;
		this.idColumnNumber = idColumnNumber;
		this.firstConceptRowNumber = firstConceptRowNumber;
		this.sheetNumber = sheetNumber;
		this.fileExtension = fileExtension;
		this.effectiveTime = effectiveTime;
		this.namespace = namespace;
		this.moduleId = moduleId;
		this.languageRefSetId = languageRefSetId;
		this.quoteCharacter = quoteCharacter;
		this.importFile = importFile;
		this.importedConceptIds = Sets.newHashSet();
		this.refSetParent = refSetParent;
		
		if (lineFeedCharacter.equals("nl")) {
			this.lineFeedCharacter = EOL.LF;
		} else {
			this.lineFeedCharacter = EOL.CRLF;
		}
		
		if (subsetName.endsWith("reference set")) {
			this.subsetName = subsetName;
		} else {
			this.subsetName = subsetName + " reference set";
		}
		
		if (fieldSeparator.equals("\\|")) {
			fieldSeparatorCharacter = '|';
		} else if (fieldSeparator.equals("")) {
			fieldSeparatorCharacter = '\0';
		} else {
			fieldSeparatorCharacter = fieldSeparator.charAt(0);
		}

	}

	/**
	 * Does the import of a reference set from the obtained file.
	 * 
	 * @return {@link SnomedUnimportedRefSets}
	 * @throws SnowowlServiceException
	 *             if there was an error during the import
	 */
	public SnomedUnimportedRefSets doImport() throws SnowowlRuntimeException {
		return ApplicationContext.getServiceForClass(RepositoryManager.class).get(SnomedDatastoreActivator.REPOSITORY_UUID).service(RevisionIndex.class).read(branchPath.getPath(), searcher -> {
			try (TransactionContext context = new ImportOnlySnomedTransactionContext(userId, searcher, new SnomedEditingContext(this.branchPath))) {
				
				final SubsetInformation information = createSubsetInformation();
				final SnomedUnimportedRefSets unimportedRefSets;
				
				if (null != information.getEffectiveTime()) {
					unimportedRefSets = new SnomedUnimportedRefSets(
							importFile.getName(), 
							information.getSubsetName(), 
							information.getNameSpace(), 
							EffectiveTimes.format(information.getEffectiveTime(), DateFormats.SHORT));
				} else {
					unimportedRefSets = new SnomedUnimportedRefSets(importFile.getName(), information.getSubsetName(), information.getNameSpace(), null);
				}
				
				final String label = information.getSubsetName();
				final SubsetImporterCallback callBack = new SubsetImporterCallback(label, unimportedRefSets, context, idColumnNumber, hasHeader, refSetParent, moduleId, languageRefSetId);
				final CsvSettings csvSettings = createCSVSettings();
				
				if (fileExtension.equals("csv")) {
					CsvVariableFieldCountParser parser = new CsvVariableFieldCountParser(new File(importFile.getAbsolutePath()), csvSettings, hasHeader, skipEmptyLines);
					parser.parse();
					
					callBack.handleNonTxtFileRecord(parser.getContent());
				} else if (fileExtension.equals("xls") || fileExtension.equals("xlsx")) {
					XlsParser parser = new XlsParser(new File(importFile.getAbsolutePath()), hasHeader, skipEmptyLines);
					parser.parse(sheetNumber);
					
					callBack.handleNonTxtFileRecord(parser.getContent());
				} else if (fileExtension.equals("txt")) {
					try (BufferedReader reader = new BufferedReader(new FileReader(importFile))) {
						new CsvParser(reader, csvSettings, callBack, getRefSetColumnCount()).parse();
					}
				}
				
				if (null != information.getEffectiveTime()) {
					updateEffectiveTimes(context, information.getEffectiveTime());
				}
				
				final String comment = new StringBuilder("Imported ").append(information.getSubsetName()).append(".").toString();
				context.commit(userId, comment, DatastoreLockContextDescriptions.ROOT);
				return unimportedRefSets;
			} catch (IOException e) {
				LOGGER.error("Error while reading input file.");
				throw new SnowowlRuntimeException("Error while reading input file.", e);
			} catch (ParseException e) {
				LOGGER.error("Error while parsing input file.");
				throw new SnowowlRuntimeException("Error while parsing input file.", e);
			} catch (Exception e) {
				throw new SnowowlRuntimeException("Error while importing subsets.", e);
			}
		});
	}
	
	// Sets the CVS settings
	private CsvSettings createCSVSettings() {
		return new CsvSettings("".equals(quoteCharacter) ? '\0' : quoteCharacter.charAt(0), fieldSeparatorCharacter, lineFeedCharacter, true);
	}

	// Creates the SubsetInformtaion
	private SubsetInformation createSubsetInformation() throws ParseException {
		SubsetInformation subsetInformation = new SubsetInformation(importFile.getName());
		subsetInformation.setSubsetName(subsetName);
		subsetInformation.setNameSpace(StringUtils.isEmpty(namespace) ? Concepts.B2I_NAMESPACE : namespace);
		if (!CompareUtils.isEmpty(effectiveTime)) {
			subsetInformation.setEffectiveTime(EffectiveTimes.parse(effectiveTime, DateFormats.SHORT));
		}
		return subsetInformation;
	}

	// Updates the effective time
	private void updateEffectiveTimes(TransactionContext context, Date effectiveTime) {
		final SnomedEditingContext editingContext = context.service(SnomedEditingContext.class);
		for (final Component item : editingContext.getNewObjects(Component.class)) {
			item.setEffectiveTime(effectiveTime);
			item.setReleased(true);
		}
		for (final SnomedRefSetMember member : editingContext.getNewObjects(SnomedRefSetMember.class)) {
			member.setEffectiveTime(effectiveTime);
			member.setReleased(true);
		}
	}

	/*
	 * Count the columns of the import file.
	 * 
	 * @return the number of the columns
	 * 
	 * @throws SnowowlServiceException
	 */
	private int getRefSetColumnCount() throws SnowowlServiceException {
		try (BufferedReader reader = new BufferedReader(new FileReader(importFile))) {

			int columnCount = 0;
			char[] charArray = new char[1024];
			reader.read(charArray, 0, 1024);
			
			boolean isNewLine = false;
			for (char c : charArray) {
				if (!isNewLine) {
					if (c == fieldSeparatorCharacter && !isNewLine) {
						columnCount++;
					} else if (c == '\r' || c == '\n') {
						columnCount++;
						isNewLine = true;
					}
				}
			}
			
			return columnCount;
		} catch (IOException e) {
			LOGGER.error("Error while counting columns.");
			throw new SnowowlServiceException("Error while reading input file.", e);
		}
	}
	
	/*
	 * Class which creates the reference set and extracts and adds the members from the import file rows to the reference set.
	 */
	private final class SubsetImporterCallback implements RecordParserCallback<String> {

		private final SnomedUnimportedRefSets unImportedRefSets;
		private String moduleId;
		private final int idColumnNumber;
		private final boolean hasHeader;
		private final String refSetId;
		private final TransactionContext context;
		private final String languageRefSetId;

		private SubsetImporterCallback(String label, SnomedUnimportedRefSets unimportedRefSet, TransactionContext context,
				int idColumnNumber, boolean hasHeader, String refSetParent, String moduleId, String languageRefSetId) {
			
			this.unImportedRefSets = unimportedRefSet;
			this.context = context;
			this.idColumnNumber = idColumnNumber;
			this.hasHeader = hasHeader;
			this.moduleId = moduleId;
			this.languageRefSetId = languageRefSetId;
			
			SnomedEditingContext editingContext = context.service(SnomedEditingContext.class);
			
			SnomedRefSetCreateRequestBuilder refSetCreateReq = SnomedRequests
					.prepareNewRefSet()
					.setType(SnomedRefSetType.SIMPLE)
					.setReferencedComponentType(SnomedTerminologyComponentConstants.CONCEPT);
			
			SnomedConceptCreateRequestBuilder identifierConceptReq = SnomedRequests
					.prepareNewConcept()
					.setModuleId(moduleId)
					.addRelationship(SnomedRequests.prepareNewRelationship()
							.setId(generateComponentId(context, ComponentCategory.RELATIONSHIP, unimportedRefSet.getNameSpace()))
							.setDestinationId(refSetParent)
							.setTypeId(Concepts.IS_A))
					.addRelationship(SnomedRequests.prepareNewRelationship()
							.setId(generateComponentId(context, ComponentCategory.RELATIONSHIP, unimportedRefSet.getNameSpace()))
							.setDestinationId(refSetParent)
							.setTypeId(Concepts.IS_A)
							.setCharacteristicType(CharacteristicType.INFERRED_RELATIONSHIP))
					.addDescription(SnomedRequests
							.prepareNewDescription()
							.setId(generateComponentId(context, ComponentCategory.DESCRIPTION, unimportedRefSet.getNameSpace()))
							.setModuleId(moduleId)
							.setTerm(String.format("%s (%s)", label, "foundation metadata concept"))
							.setTypeId(Concepts.FULLY_SPECIFIED_NAME)
							.preferredIn(languageRefSetId))
					.addDescription(SnomedRequests
							.prepareNewDescription()
							.setId(generateComponentId(context, ComponentCategory.DESCRIPTION, unimportedRefSet.getNameSpace()))
							.setModuleId(moduleId)
							.setTerm(label)
							.setTypeId(Concepts.SYNONYM)
							.preferredIn(languageRefSetId))
					.setRefSet(refSetCreateReq);
			
			final String cmtRefSetId = getIdIfCMTConcept(label);
			if (cmtRefSetId == null) {
				identifierConceptReq.setId(generateComponentId(context, ComponentCategory.CONCEPT, unimportedRefSet.getNameSpace()));
			} else {
				identifierConceptReq.setId(cmtRefSetId);
			}
			
			this.refSetId = identifierConceptReq.build().execute(context);
		}

		private String generateComponentId(TransactionContext context, ComponentCategory category, String namespace) {
			return SnomedRequests.identifiers()
											.prepareGenerate()
											.setQuantity(1)
											.setNamespace(namespace)
											.setCategory(category)
											.build()
											.execute(context)
											.first()
											.orElseThrow(() -> new BadRequestException("Couldn't generate ID for args (%s, %s).", category, namespace));
		}
		
		private String getIdIfCMTConcept(String label) {
			return Concepts.CMT_REFSET_NAME_ID_MAP.get(label.replaceAll(" reference set", ""));
		}

		@Override
		public void handleRecord(int recordCount, List<String> record) {
			if (hasHeader) {
				if (firstConceptRowNumber < recordCount) {
					createMember(record);
				}
			} else {
				createMember(record);
			}
		}
		
		public void handleNonTxtFileRecord(List<List<String>> content) {
			for (List<String> rowsList : content) {
				SnomedConceptDocument concept = getConcept(rowsList.get(idColumnNumber));
				if (concept != null && importedConceptIds.add(concept.getId())) {
					createMember(concept);
				} else {
					createUnimportedRefsetMember(rowsList, concept);
				}
			}
		}
		
		private void createMember(List<String> record) {
			SnomedConceptDocument concept = getConcept(record.get(idColumnNumber));
			if (concept != null && importedConceptIds.add(concept.getId())) {
				createMember(concept);
			} else {
				createUnimportedRefsetMember(record, concept);
			}
		}
		
		private void createUnimportedRefsetMember(List<String> rowsList, SnomedConceptDocument concept) {
			String identifier;
			if (null != getFullySpecifiedName(rowsList)) {
				identifier = getFullySpecifiedName(rowsList);
			} else {
				identifier = rowsList.get(idColumnNumber) ;
			}
			
			if (concept == null) {
				unImportedRefSets.addRefSetMember("Not found", rowsList.get(idColumnNumber), identifier);
			} else if (!importedConceptIds.add(concept.getId())) {
				unImportedRefSets.addRefSetMember("Duplicated", rowsList.get(idColumnNumber), identifier);
			} else {
				unImportedRefSets.addRefSetMember("Unknown", rowsList.get(idColumnNumber), identifier);
			}
		}
		
		private String getFullySpecifiedName(List<String> rowsList) {
			for (String value : rowsList) {
				if (value.contains("(") && value.contains(")")) {
					return value;
				}
			}
			return null;
		}

		private void createMember(SnomedConceptDocument concept) {
			SnomedComponents.newSimpleMember()
				.withActive(concept.isActive())
				.withReferencedComponent(concept.getId())
				.withModule(moduleId)
				.withRefSet(refSetId)
				.addTo(context);
		}

		private SnomedConceptDocument getConcept(final String conceptId) {
			if (conceptId.matches("\\d+")) {
				return getConcept(branchPath, conceptId);
			} else {
				return null;
			}
		}
		
		private SnomedConceptDocument getConcept(IBranchPath branchPath, String conceptId) {
			return new SnomedConceptLookupService().getComponent(branchPath, conceptId);
		}

	}
}