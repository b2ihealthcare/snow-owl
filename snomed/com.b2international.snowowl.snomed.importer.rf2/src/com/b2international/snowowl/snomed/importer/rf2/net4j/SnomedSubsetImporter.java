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
package com.b2international.snowowl.snomed.importer.rf2.net4j;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.text.ParseException;
import java.util.Date;
import java.util.List;
import java.util.Set;

import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.emf.cdo.util.CommitException;

import com.b2international.commons.CompareUtils;
import com.b2international.commons.csv.CsvLexer.EOL;
import com.b2international.commons.csv.CsvParser;
import com.b2international.commons.csv.CsvSettings;
import com.b2international.commons.csv.RecordParserCallback;
import com.b2international.snowowl.core.ApplicationContext;
import com.b2international.snowowl.core.ComponentIdentifierPair;
import com.b2international.snowowl.core.api.IBranchPath;
import com.b2international.snowowl.core.api.SnowowlServiceException;
import com.b2international.snowowl.core.api.browser.ITerminologyBrowser;
import com.b2international.snowowl.core.date.DateFormats;
import com.b2international.snowowl.core.date.EffectiveTimes;
import com.b2international.snowowl.datastore.BranchPathUtils;
import com.b2international.snowowl.datastore.cdo.CDOUtils;
import com.b2international.snowowl.datastore.cdo.ICDOConnection;
import com.b2international.snowowl.datastore.cdo.ICDOConnectionManager;
import com.b2international.snowowl.datastore.server.CDOServerUtils;
import com.b2international.snowowl.datastore.utils.ComponentUtils2;
import com.b2international.snowowl.snomed.Component;
import com.b2international.snowowl.snomed.Concept;
import com.b2international.snowowl.snomed.SnomedConstants.Concepts;
import com.b2international.snowowl.snomed.SnomedPackage;
import com.b2international.snowowl.snomed.common.SnomedTerminologyComponentConstants;
import com.b2international.snowowl.snomed.datastore.SnomedConceptLookupService;
import com.b2international.snowowl.snomed.datastore.SnomedEditingContext;
import com.b2international.snowowl.snomed.datastore.SnomedRefSetEditingContext;
import com.b2international.snowowl.snomed.datastore.SnomedTerminologyBrowser;
import com.b2international.snowowl.snomed.datastore.index.entry.SnomedConceptIndexEntry;
import com.b2international.snowowl.snomed.importer.net4j.SnomedUnimportedRefSets;
import com.b2international.snowowl.snomed.refset.core.automap.CsvVariableFieldCountParser;
import com.b2international.snowowl.snomed.refset.core.automap.XlsParser;
import com.b2international.snowowl.snomed.snomedrefset.SnomedRefSetMember;
import com.b2international.snowowl.snomed.snomedrefset.SnomedRegularRefSet;
import com.google.common.base.Preconditions;
import com.google.common.collect.Sets;
import com.google.common.io.Closeables;

/**
 * Import simple type reference sets into Snow Owl from a SNOMED&nbsp;CT RF1
 * subset file.
 * 
 * 
 */
public class SnomedSubsetImporter {

	private static final org.slf4j.Logger LOGGER = org.slf4j.LoggerFactory.getLogger(SnomedSubsetImporter.class);
	
	private final boolean isUiImport;
	private final boolean hasHeader;
	private final boolean skipEmptyLines;
	private final int idColumnNumber;
	private final int firstConceptRowNumber;
	private final int sheetNumber;
	private final int refSetType;
	private final EOL lineFeedCharacter;
	private final File importFile;
	private char fieldSeparatorCharacter;
	private final String fileExtension;
	private final IBranchPath branchPath;
	private final String quoteCharacter;
	private final String effectiveTime;
	private final String subsetName;
	private final String namespace;
	private SnomedUnimportedRefSets unimportedRefSets;
	private final SnomedEditingContext context;
	private final String userId;
	private final Set<String> importedConceptIds;

	public SnomedSubsetImporter(final String branchPath, String userId, boolean hasHeader, boolean skipEmptyLines, int idColumnNumber, int firstConceptRowNumber, int sheetNumber, int refSetType, String subsetName, String fileExtension,
			String effectiveTime, String namespace, String fieldSeparator, String quoteCharacter, String lineFeedCharacter, File importFile) throws SnowowlServiceException {
		this.userId = userId;
		this.branchPath = BranchPathUtils.createPath(branchPath);
		this.hasHeader = hasHeader;
		this.skipEmptyLines = skipEmptyLines;
		this.idColumnNumber = idColumnNumber;
		this.firstConceptRowNumber = firstConceptRowNumber;
		this.sheetNumber = sheetNumber;
		this.refSetType = refSetType;
		this.fileExtension = fileExtension;
		this.effectiveTime = effectiveTime;
		this.namespace = namespace;
		this.quoteCharacter = quoteCharacter;
		this.importFile = importFile;
		this.isUiImport = true;
		this.importedConceptIds = Sets.newHashSet();
		
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

		this.context = new SnomedEditingContext(this.branchPath);
	}

	/**
	 * Does the import of a reference set from the obtained file.
	 * 
	 * @return {@link SnomedUnimportedRefSets}
	 * @throws SnowowlServiceException
	 *             if there was an error during the import
	 */
	public SnomedUnimportedRefSets doImport() throws SnowowlServiceException {
		BufferedReader reader = null;

		try {
			SubsetInformation information = createSubsetInformation();
			if (null != information.getEffectiveTime()) {
				unimportedRefSets = new SnomedUnimportedRefSets(
						importFile.getName(), 
						information.getSubsetName(), 
						information.getNameSpace(), 
						EffectiveTimes.format(information.getEffectiveTime(), DateFormats.SHORT));
			} else {
				unimportedRefSets = new SnomedUnimportedRefSets(importFile.getName(), information.getSubsetName(), information.getNameSpace(), null);
			}
			
			createHierarchy();
			SubsetImporterCallback callBack = createCallBack(information);
			CsvSettings csvSettings = createCVSSettings();
			
			if (fileExtension.equals("csv")) {
				CsvVariableFieldCountParser parser = new CsvVariableFieldCountParser(new File(importFile.getAbsolutePath()), csvSettings, hasHeader, skipEmptyLines);
				parser.parse();
				
				callBack.handleNonTxtFileRecord(parser.getContent());
			} else if (fileExtension.equals("xls") || fileExtension.equals("xlsx")) {
				XlsParser parser = new XlsParser(new File(importFile.getAbsolutePath()), hasHeader, skipEmptyLines);
				parser.parse(sheetNumber);
				
				callBack.handleNonTxtFileRecord(parser.getContent());
			} else if (fileExtension.equals("txt")) {
				int refSetColumnCount = getRefSetColumnCount();
				reader = new BufferedReader(new FileReader(importFile));
				CsvParser csvParser = new CsvParser(reader, csvSettings, callBack, refSetColumnCount);
				csvParser.parse();
			}

			if (null != information.getEffectiveTime()) {
				updateEffectiveTimes(information.getEffectiveTime());
			}

			try {
				String comment = new StringBuilder("Imported ").append(information.getSubsetName()).append(".").toString();
				CDOServerUtils.commit(context.getTransaction(), userId, comment, new NullProgressMonitor());
			} catch (CommitException e) {
				LOGGER.error("Error while committing imported reference set members.");
				throw new SnowowlServiceException("Error while committing imported reference set members.", e);
			}

		} catch (IOException e) {
			LOGGER.error("Error while reading input file.");
			throw new SnowowlServiceException("Error while reading input file.", e);
		} catch (ParseException e) {
			LOGGER.error("Error while parsing input file.");
			throw new SnowowlServiceException("Error while parsing input file.", e);
		} catch (final CommitException e) {
			LOGGER.error("Error while persisting changes.");
			throw new SnowowlServiceException("Error while persisting changes.", e);
		} finally {
			context.close();
			Closeables.closeQuietly(reader);
		}

		return unimportedRefSets;
	}
	
	// Creates the SubsetImporterCallBack for the given RefSet type
	private SubsetImporterCallback createCallBack(SubsetInformation information) {
		switch (refSetType) {
			case 0:	// place under Simple type reference set
				return new SubsetImporterCallback(information, unimportedRefSets, context, idColumnNumber, hasHeader, Concepts.REFSET_SIMPLE_TYPE);
			case 1:	 // place under B2i examples
				return new SubsetImporterCallback(information, unimportedRefSets, context, idColumnNumber, hasHeader, Concepts.REFSET_B2I_EXAMPLE);
			case 2:	// place under KP CONVERGENT MEDICAL TERMINOLOGY
				return new SubsetImporterCallback(information, unimportedRefSets, context, idColumnNumber, hasHeader, Concepts.REFSET_KP_CONVERGENT_MEDICAL_TERMINOLOGY);
			case 3:	// place under CORE PROBLEM LIST REFERENCE SETS
				return new SubsetImporterCallback(information, unimportedRefSets, context, idColumnNumber, hasHeader, Concepts.REFSET_CORE_PROBLEM_LIST_REFERENCE_SETS);
			case 4:	// place under INFOWAY PRIMARY HEALTH CARE REFERENCE SETS
				return new SubsetImporterCallback(information, unimportedRefSets, context, idColumnNumber, hasHeader, Concepts.REFSET_INFOWAY_PRIMARY_HEALTH_CARE_REFERENCE_SETS);
			default:
				return null;
		}
	}

	// Creates the hierarchy where the RefSet will be placed
	private void createHierarchy() throws CommitException {
		if (1 == refSetType) { // place under B2i examples
			createB2iExampleConcept();
		} else if (2 == refSetType) {  // place under KP CONVERGENT MEDICAL TERMINOLOGY
			createB2iExampleConcept();
			createKpConvergentMedicalTerminologyConcept();
		} else if (3 == refSetType) {  // place under CORE PROBLEM LIST REFERENCE SETS
			createB2iExampleConcept();
			createCOREProblemListConcept();
		} else if (4 == refSetType) {  // place under INFOWAY PRIMARY HEALTH CARE REFERENCE SETS
			createB2iExampleConcept();
			createInfowayPrimaryHealthCareConcept();
		}
	}

	// Creates the B2i examples concept
	private void createB2iExampleConcept() throws CommitException {
		if (null == getTerminologyBrowser().getConcept(branchPath, Concepts.REFSET_B2I_EXAMPLE)) {
			createConcept(Concepts.REFSET_SIMPLE_TYPE, Concepts.REFSET_B2I_EXAMPLE, Concepts.MODULE_B2I_EXTENSION, "B2i examples");
		}
	}

	// Creates the KP Convergent Medical Terminology concept
	private void createKpConvergentMedicalTerminologyConcept() throws CommitException {
		if (null == getTerminologyBrowser().getConcept(branchPath, Concepts.REFSET_KP_CONVERGENT_MEDICAL_TERMINOLOGY)) {
			createConcept(Concepts.REFSET_B2I_EXAMPLE, Concepts.REFSET_KP_CONVERGENT_MEDICAL_TERMINOLOGY, Concepts.MODULE_B2I_EXTENSION,
					"KP Convergent Medical Terminology");
		}
	}
	
	// Creates the CORE Problem List concept
	private void createCOREProblemListConcept() throws CommitException {
		if (null == getTerminologyBrowser().getConcept(branchPath, Concepts.REFSET_CORE_PROBLEM_LIST_REFERENCE_SETS)) {
			createConcept(Concepts.REFSET_B2I_EXAMPLE, Concepts.REFSET_CORE_PROBLEM_LIST_REFERENCE_SETS, Concepts.MODULE_B2I_EXTENSION,
					"CORE Problem List Reference Sets");
		}
	}

	// Creates the Infoway Primary Health Care concept
	private void createInfowayPrimaryHealthCareConcept() throws CommitException {
		if (null == getTerminologyBrowser().getConcept(branchPath, Concepts.REFSET_INFOWAY_PRIMARY_HEALTH_CARE_REFERENCE_SETS)) {
			createConcept(Concepts.REFSET_B2I_EXAMPLE, Concepts.REFSET_INFOWAY_PRIMARY_HEALTH_CARE_REFERENCE_SETS, Concepts.MODULE_B2I_EXTENSION,
					"Infoway Primary Health Care Reference Sets");
		}
	}

	// Sets the CVS settings
	private CsvSettings createCVSSettings() {
		return new CsvSettings("".equals(quoteCharacter) ? '\0' : quoteCharacter.charAt(0), fieldSeparatorCharacter, lineFeedCharacter, true);
	}

	// Creates the SubsetInformtaion
	private SubsetInformation createSubsetInformation() throws ParseException {
		SubsetInformation subsetInformation = new SubsetInformation(importFile.getName());
		if (isUiImport) {
			subsetInformation.setSubsetName(subsetName);
			if (!CompareUtils.isEmpty(namespace)) {
				subsetInformation.setNameSpace(namespace);
			} else {
				// default namespace is 1000154
				subsetInformation.setNameSpace("1000154");
			}
			if (!CompareUtils.isEmpty(effectiveTime)) {
				subsetInformation.setEffectiveTime(EffectiveTimes.parse(effectiveTime, DateFormats.SHORT));
			}
		} else {
			subsetInformation.parse();
		}
		return subsetInformation;
	}

	// Updates the effective time
	private void updateEffectiveTimes(Date effectiveTime) {
		for (final Component item : ComponentUtils2.getNewObjects(context.getTransaction(), Component.class)) {
			item.setEffectiveTime(effectiveTime);
			item.setReleased(true);
		}
		for (final SnomedRefSetMember member : ComponentUtils2.getNewObjects(context.getTransaction(), SnomedRefSetMember.class)) {
			member.setEffectiveTime(effectiveTime);
			member.setReleased(true);
		}
	}

	// FSN ID: 250928901000154112
	// PT ID: 94920731000154113
	// IS_A ID: 864454871000154127

	// Creates a concept
	private void createConcept(final String parentConceptId, final String conceptId, final String moduleId, final String label)
			throws CommitException {
		final Concept parentConcept = findConceptById(parentConceptId);
		if (parentConcept != null) {
			Concept module = findConceptById(moduleId);
			if (null == module) {
				module = context.getDefaultModuleConcept();
			}
			Concept concept = context.buildDefaultConcept(label, "1000154", module, parentConcept);
			concept.setId(conceptId);
			String comment = "Created '" + label + "' concept";
			CDOServerUtils.commit(context.getTransaction(), userId, comment, new NullProgressMonitor());
		} else {
			throw new RuntimeException("Couldn't find parent concept 'Module'!");
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
		int columnCount = 0;
		BufferedReader reader = null;
		try {
			reader = new BufferedReader(new FileReader(importFile));
			
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
		} finally {
			Closeables.closeQuietly(reader);
		}
	}
	
	protected ICDOConnection getCDOConnection() {
		return ApplicationContext.getInstance().getService(ICDOConnectionManager.class).get(SnomedPackage.eINSTANCE);
	}
	
	private ITerminologyBrowser<SnomedConceptIndexEntry, String> getTerminologyBrowser() {
		return ApplicationContext.getInstance().getService(SnomedTerminologyBrowser.class);
	}
	
	/*returns with the SNOMED CT concept identified by its unique ID. may return with null, if concept cannot be found with the specifeid ID.*/
	private Concept findConceptById(final String conceptId) {
		Preconditions.checkNotNull(context, "Editing context argument cannot be null.");
		CDOUtils.check(context.getTransaction());
		return new SnomedConceptLookupService().getComponent(conceptId, context.getTransaction());
	}

	/*
	 * Class which will creates the reference set and extracted the members from
	 * the import file rows and adds them to the reference set.
	 */
	private final class SubsetImporterCallback implements RecordParserCallback<String> {

		private SnomedRefSetEditingContext refSetEditingContext;
		private SnomedRegularRefSet refSet;
		private final SnomedUnimportedRefSets unImportedRefSets;
		private final SubsetInformation information;
		private String moduleId;
		private final int idColumnNumber;
		private final boolean hasHeader;

		private SubsetImporterCallback(SubsetInformation information, SnomedUnimportedRefSets unimportedRefSet, SnomedEditingContext editingContext,
				int idColumnNumber, boolean hasHeader, String refSetType) {
			this.information = information;
			this.unImportedRefSets = unimportedRefSet;
			this.idColumnNumber = idColumnNumber;
			this.hasHeader = hasHeader; 
			refSetEditingContext = editingContext.getRefSetEditingContext();
			Concept parentConcept = findConceptById(refSetType);
			final Concept module = findConceptById(parentConcept.getModule().getId());
			refSet = refSetEditingContext.createSnomedSimpleTypeRefSet(this.information.getSubsetName(), SnomedTerminologyComponentConstants.CONCEPT_NUMBER,
					"1000154", module, parentConcept);
			moduleId = module.getId();
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
				SnomedConceptIndexEntry concept = getConcept(rowsList.get(idColumnNumber));
				if (concept != null && concept.isActive() && importedConceptIds.add(concept.getId())) {
					SnomedRefSetMember member = createMember(concept);
					refSet.getMembers().add(member);
				} else {
					createUnimportedRefsetMember(rowsList, concept);
				}
			}
		}
		
		private void createMember(List<String> record) {
			SnomedConceptIndexEntry concept = getConcept(record.get(idColumnNumber));
			if (concept != null && concept.isActive() && importedConceptIds.add(concept.getId())) {
				SnomedRefSetMember member = createMember(concept);
				refSet.getMembers().add(member);
			} else {
				createUnimportedRefsetMember(record, concept);
			}
		}
		
		private void createUnimportedRefsetMember(List<String> rowsList, SnomedConceptIndexEntry concept) {
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
				unImportedRefSets.addRefSetMember("Inactive", rowsList.get(idColumnNumber), identifier);
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

		private SnomedRefSetMember createMember(SnomedConceptIndexEntry concept) {
			return refSetEditingContext.createSimpleTypeRefSetMember(createIdentifierPair(concept), moduleId, refSet);
		}

		private ComponentIdentifierPair<String> createIdentifierPair(SnomedConceptIndexEntry concept) {
			return SnomedRefSetEditingContext.createConceptTypePair(concept.getId());
		}

		private SnomedConceptIndexEntry getConcept(final String conceptId) {
			if (conceptId.matches("\\d+")) {
				return getTerminologyBrowser().getConcept(branchPath, conceptId);
			} else {
				return null;
			}
		}
	}
}