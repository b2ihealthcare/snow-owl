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
package com.b2international.snowowl.snomed.exporter.server.rf1;

import static com.b2international.commons.StringUtils.valueOfOrEmptyString;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.b2international.index.Hits;
import com.b2international.index.query.Expression;
import com.b2international.index.query.Expressions;
import com.b2international.index.query.Query;
import com.b2international.index.revision.RevisionSearcher;
import com.b2international.snowowl.core.ApplicationContext;
import com.b2international.snowowl.snomed.SnomedConstants.Concepts;
import com.b2international.snowowl.snomed.common.SnomedRf2Headers;
import com.b2international.snowowl.snomed.core.domain.Acceptability;
import com.b2international.snowowl.snomed.datastore.ILanguageConfigurationProvider;
import com.b2international.snowowl.snomed.datastore.index.entry.SnomedDescriptionIndexEntry;
import com.b2international.snowowl.snomed.datastore.index.entry.SnomedRefSetMemberIndexEntry;
import com.b2international.snowowl.snomed.exporter.server.ComponentExportType;
import com.b2international.snowowl.snomed.exporter.server.SnomedExportContext;
import com.google.common.base.Charsets;
import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Sets;

/**
 * RF2 export implementation for SNOMED&nbsp;CT description.
 *
 */
public class SnomedRf1DescriptionExporter extends AbstractSnomedRf1CoreExporter<SnomedDescriptionIndexEntry> {

	/**
	 * Line in the Description RF1 file
	 */
	class Rf1Description {
		
		public String id;
		public String status;
		public String conceptId;
		public String term;
		public String capitalStatus;
		public String typeId;
		private static final String LANGUAGE_CODE = "en";
		
		@Override
		public String toString() {
			
			return new StringBuilder(id)
					.append(HT)
					.append(getDescriptionStatus(valueOfOrEmptyString(status)))
					.append(HT)
					.append(valueOfOrEmptyString(conceptId))
					.append(HT)
					.append(valueOfOrEmptyString(term))
					.append(HT)
					.append(mapper.getInitialCapitalStatus(valueOfOrEmptyString(capitalStatus))) //initial capital status
					.append(HT)
					.append(typeId) //type
					.append(HT)
					.append(LANGUAGE_CODE) //language code
					.append(HT)
					.toString();
		}
	}
	
	private static final Logger LOGGER = LoggerFactory.getLogger(SnomedRf1DescriptionExporter.class);
	
	private static final String EXTENDED_DESCRIPTION_TYPE_EXPLANATION_FILE_NAME = "Extended_Description_Type_Explanation.txt";
	
	private static final List<String> EXTENDED_DESCRIPTION_TYPE_EXPLANATION = ImmutableList.<String> builder()
			.add("TypeID\tDescription type")
			.add(" 1\tPreferred term")
			.add(" 2\tSynonym")
			.add(" 3\tFully specified name")
			.add(" 4\tFull name")
			.add(" 5\tAbbreviation")
			.add(" 6\tProduct term")
			.add(" 7\tShort name")
			.add(" 8\tPreferred plural")
			.add(" 9\tNote")
			.add("10\tSearch term")
			.add("11\tAbbreviation plural")
			.add("12\tProduct term plural")
			.build();
	
	private Id2Rf1PropertyMapper mapper;
	private final Set<String> undefinedDescriptionTypeIds = Sets.newHashSet();
	private String preferredLanguageId;
	private boolean includeExtendedDescriptionTypes;
	
	public SnomedRf1DescriptionExporter(final SnomedExportContext exportContext, final RevisionSearcher revisionSearcher,
			final boolean includeExtendedDescriptionTypes) {
		super(exportContext, SnomedDescriptionIndexEntry.class, revisionSearcher);
		mapper = exportContext.getId2Rf1PropertyMapper();
		this.includeExtendedDescriptionTypes = includeExtendedDescriptionTypes;
		this.preferredLanguageId = ApplicationContext.getInstance().getService(ILanguageConfigurationProvider.class).getLanguageConfiguration().getLanguageRefSetId();
	}
	
	@Override
	public String convertToString(SnomedDescriptionIndexEntry doc) {
		Rf1Description description = new Rf1Description();
		
		description.id = doc.getId();
		description.status = doc.isActive() ? "1" : "0";
		description.conceptId = doc.getConceptId();
		description.term = doc.getTerm();
		description.capitalStatus = doc.getCaseSignificanceId();
		if (includeExtendedDescriptionTypes) {
			description.typeId = getExtendedDescriptionType(doc.getTypeId());
		} else {
			description.typeId = getDescriptionType(doc.getTypeId());
		}
		
		//inactivation status
		if (!doc.isActive()) {
			
			Expression condition = Expressions.builder()
					.filter(SnomedRefSetMemberIndexEntry.Expressions.referencedComponentIds(Sets.newHashSet(doc.getId())))
					.filter(SnomedRefSetMemberIndexEntry.Expressions.referenceSetId(Sets.newHashSet(Concepts.REFSET_CONCEPT_INACTIVITY_INDICATOR)))
					.filter(SnomedRefSetMemberIndexEntry.Expressions.active()).build();
			
			Query<SnomedRefSetMemberIndexEntry> query = Query.select(SnomedRefSetMemberIndexEntry.class).where(condition).build();
						
			Hits<SnomedRefSetMemberIndexEntry> snomedRefSetMemberIndexEntrys;
			try {
				snomedRefSetMemberIndexEntrys = getSearcher().search(query);
			} catch (IOException e) {
				throw new RuntimeException(e);
			}
			
			//there should be only one max
			for (SnomedRefSetMemberIndexEntry snomedRefSetMemberIndexEntry : snomedRefSetMemberIndexEntrys) {
				description.status = snomedRefSetMemberIndexEntry.getValueId();
			}
		}
		
		//handle preferred terms separately, overriding the initial value
		Map<String, Acceptability> acceptabilityMap = doc.getAcceptabilityMap();
		Acceptability acceptability = acceptabilityMap.get(preferredLanguageId);
		if (Acceptability.PREFERRED == acceptability) {
			if (!doc.getTypeId().equals(Concepts.FULLY_SPECIFIED_NAME) && (!doc.getTypeId().equals(Concepts.TEXT_DEFINITION))) {
				
				//preferred term constant
				description.typeId = "1";
			} 
		}
		return description.toString();
	}

	@Override
	public ComponentExportType getType() {
		return ComponentExportType.DESCRIPTION;
	}

	@Override
	public String[] getColumnHeaders() {
		return SnomedRf2Headers.DESCRIPTION_HEADER;
	}
	
	@Override
	public void execute() throws IOException {
		super.execute();
		
		if (includeExtendedDescriptionTypes) {
			
			Path workingDirPath = Paths.get(getExportContext().getReleaseRootPath().toString(), getRelativeDirectory());
			if (Files.notExists(workingDirPath)) {
				Files.createDirectories(workingDirPath);
			}
			
			Path extendedFilePath = Paths.get(workingDirPath.toString(), EXTENDED_DESCRIPTION_TYPE_EXPLANATION_FILE_NAME);
			
			if (Files.notExists(extendedFilePath)) {
				Files.createFile(extendedFilePath);
			}
			
			Files.write(extendedFilePath, EXTENDED_DESCRIPTION_TYPE_EXPLANATION, Charsets.UTF_8);
		}
	}
	
	/*returns with a number indicating the status of a description for RF1 publication.*/
	private String getDescriptionStatus(final String stringValue) {
		if ("1".equals(stringValue)) { //magic mapping between rf1 and rf2 status
			return "0";
		} else if ("0".equals(stringValue)) {
			return "1";
		} else {
			return Preconditions.checkNotNull(mapper.getDescriptionStatusProperty(stringValue));
		}
	}
	
	private String getDescriptionType(final String descriptionType) {
		
		if (descriptionType.equals(Concepts.FULLY_SPECIFIED_NAME)) {
			return "3";
		} else if (descriptionType.equals(Concepts.SYNONYM)) {
			return "2";
		} 
		// Report undefined type IDs only once
		if (undefinedDescriptionTypeIds.add(descriptionType)) {
			LOGGER.warn("Description type ID '" + descriptionType + "' not mapped to RF1, exporting as synonym.");
		}
		return "2";
	}

	private String getExtendedDescriptionType(final String descriptionType) {
		
		if (descriptionType.equals(Concepts.FULLY_SPECIFIED_NAME)) {
			return "3";
		} else if (descriptionType.equals(Concepts.FULL_NAME)) {
			return "4";
		} else if (descriptionType.equals(Concepts.ABBREVIATION)) {
			return "5";
		} else if (descriptionType.equals(Concepts.PRODUCT_TERM)) {
			return "6";
		} else if (descriptionType.equals(Concepts.SHORT_NAME)) {
			return "7";
		} else if (descriptionType.equals(Concepts.PREFERRED_PLURAL)) {
			return "8";
		} else if (descriptionType.equals(Concepts.NOTE)) {
			return "9";
		} else if (descriptionType.equals(Concepts.SEARCH_TERM)) {
			return "10";
		} else if (descriptionType.equals(Concepts.ABBREVIATION_PLURAL)) {
			return "11";
		} else if (descriptionType.equals(Concepts.PRODUCT_TERM_PLURAL)) {
			return "12";
		} else if (descriptionType.equals(Concepts.SYNONYM)) {
			return "2";
		}
		
		// Report undefined type IDs only once
		if (undefinedDescriptionTypeIds.add(descriptionType)) {
			LOGGER.warn("Description type ID '" + descriptionType + "' not mapped to RF1, exporting as synonym.");
		}
		return "2"; //everything else is like synonym
	}
	
}
