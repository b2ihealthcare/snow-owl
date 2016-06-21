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
package com.b2international.snowowl.snomed.exporter.model;

import java.io.File;
import java.io.IOException;
import java.util.Set;

import com.b2international.index.Hits;
import com.b2international.index.query.Query;
import com.b2international.index.query.Query.QueryBuilder;
import com.b2international.index.revision.RevisionIndex;
import com.b2international.index.revision.RevisionIndexRead;
import com.b2international.index.revision.RevisionSearcher;
import com.b2international.snowowl.core.ApplicationContext;
import com.b2international.snowowl.core.RepositoryManager;
import com.b2international.snowowl.datastore.BranchPathUtils;
import com.b2international.snowowl.snomed.SnomedConstants.Concepts;
import com.b2international.snowowl.snomed.datastore.MapSetType;
import com.b2international.snowowl.snomed.datastore.SnomedDatastoreActivator;
import com.b2international.snowowl.snomed.datastore.SnomedMapSetSetting;
import com.b2international.snowowl.snomed.datastore.SnomedRefSetUtil;
import com.b2international.snowowl.snomed.datastore.index.entry.SnomedConceptDocument;
import com.b2international.snowowl.snomed.snomedrefset.SnomedRefSetType;
import com.google.common.base.Optional;
import com.google.common.collect.FluentIterable;
import com.google.common.collect.Sets;

public final class SnomedExporterUtil {

	public static final String REFERENCE_SET_RELATIVE_ROOT_DIR = "Reference Sets";
	public static final String STRUCTURAL_REFERENCE_SET_RELATIVE_DIR = REFERENCE_SET_RELATIVE_ROOT_DIR + File.separatorChar + "Structural";
	public static final String PRODUCTION_REFERENCE_SET_RELATIVE_DIR = REFERENCE_SET_RELATIVE_ROOT_DIR + File.separatorChar + "Clinical" + File.separatorChar + "Production";
	
	/**
	 * Creates a set of map set setting for the SNOMED&nbsp;CT publication process for the RF1 release format.
	 * <p><b>NOTE:&nbsp;</b>this method should not be called on the server side since it uses the Snor to retrieve information.<br>
	 * This method should be invoked when clients are sure the RF1 release format is also selected for the SNOMED&nbsp;CT publication process.
	 * @param refSetIds the reference set identifier concept ID.
	 * @return a collection of map set setting. Can be empty if no map set should be created while publishing SNOMED&nbsp;CT into RF1.
	 */
	public static final Set<SnomedMapSetSetting> createSettings(final Set<String> refSetIds) {
		final Set<SnomedMapSetSetting> settings = Sets.newHashSet();
		for (final String refSetId : refSetIds) {
			if (shouldCreateMapSetSetting(refSetId))
				settings.add(createSetting(refSetId));
		}
		return settings;
	}

	/*returns true if a map set setting should be created for the RF1 publication process*/
	private static boolean shouldCreateMapSetSetting(final String refSetId) {
		
		RepositoryManager repositoryManager = ApplicationContext.getInstance().getService(RepositoryManager.class);
		RevisionIndex revisionIndex = repositoryManager.get(SnomedDatastoreActivator.REPOSITORY_UUID).service(RevisionIndex.class);
		
		QueryBuilder<SnomedConceptDocument> builder = Query.builder(SnomedConceptDocument.class);

		Query<SnomedConceptDocument> query = builder.selectAll().where(SnomedConceptDocument.Expressions.id(refSetId)).build();
		
		//TODO: is this always main?
		SnomedConceptDocument refsetConcept = revisionIndex.read(BranchPathUtils.createMainPath().getPath(), new RevisionIndexRead<SnomedConceptDocument>() {

			@Override
			public SnomedConceptDocument execute(RevisionSearcher searcher) throws IOException {
				
				Hits<SnomedConceptDocument> snomedConceptDocuments = searcher.search(query);
				Optional<SnomedConceptDocument> first = FluentIterable.<SnomedConceptDocument>from(snomedConceptDocuments).first();
				if (first.isPresent()) {
					return first.get();
				} else {
					throw new IllegalArgumentException("Could not find reference set with id: " + refSetId);
				}
			}
		});
		
		
		
		return isConceptType(refSetId) && !(!isMapping(refsetConcept.getRefSetType()) || isStructuralRefSet(refSetId));
	}

	/*returns true if the passed in reference set identifier concept ID is either CTV3 simple map ID or SNOMED RT simple map reference set ID*/
	private static boolean isStructuralRefSet(final String refSetId) {
		return Concepts.CTV3_SIMPLE_MAP_TYPE_REFERENCE_SET_ID.equals(refSetId) 
				|| Concepts.SNOMED_RT_SIMPLE_MAP_TYPE_REFERENCE_SET_ID.equals(refSetId);
	}
	
	/*creates and returns with a new map setting instance for the RF1 publication.*/
	private static SnomedMapSetSetting createSetting(final String refSetId) {
		if (Concepts.ICD_O_REFERENCE_SET_ID.equals(refSetId))
			return (SnomedMapSetSetting) SnomedMapSetSetting.ICD_O_SETTING;
		else if (Concepts.ICD_9_CM_REFERENCE_SET_ID.equals(refSetId))
			return (SnomedMapSetSetting) SnomedMapSetSetting.ICD_9_CM_SETTING;
		else if (Concepts.ICD_10_REFERENCE_SET_ID.equals(refSetId))
			return (SnomedMapSetSetting) SnomedMapSetSetting.ICD_10_SETTING;
		else 
			return new SnomedMapSetSetting(refSetId, "", "", "", "", MapSetType.UNSPECIFIED, isComplex(getType(refSetId)));
	}

	/*returns true if the reference set type is either simple map or complex map type*/
	private static boolean isMapping(final SnomedRefSetType type) {
		return SnomedRefSetUtil.isMapping(type);
	}

	/*returns true if the reference set member is a complex type*/
	private static boolean isComplex(final SnomedRefSetType type) {
		return SnomedRefSetUtil.isComplexMapping(type);
	}
	
	private SnomedExporterUtil() { }
	
}