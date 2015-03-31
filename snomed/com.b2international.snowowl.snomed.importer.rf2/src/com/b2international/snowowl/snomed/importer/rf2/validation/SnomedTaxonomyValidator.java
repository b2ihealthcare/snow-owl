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
package com.b2international.snowowl.snomed.importer.rf2.validation;

import static com.b2international.commons.StringUtils.isEmpty;
import static com.b2international.snowowl.core.ApplicationContext.getServiceForClass;
import static com.b2international.snowowl.datastore.server.snomed.index.init.Rf2BasedSnomedTaxonomyBuilder.newInstance;
import static com.b2international.snowowl.datastore.server.snomed.index.init.Rf2BasedSnomedTaxonomyBuilder.newValidationInstance;
import static com.b2international.snowowl.snomed.common.ContentSubType.SNAPSHOT;
import static com.b2international.snowowl.snomed.datastore.SnomedDatastoreActivator.REPOSITORY_UUID;
import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.collect.Iterables.concat;
import static com.google.common.collect.Lists.newArrayList;
import static com.google.common.collect.Sets.newHashSet;
import static java.util.Collections.emptySet;
import static java.util.Collections.sort;
import static org.slf4j.LoggerFactory.getLogger;

import java.io.File;
import java.io.IOException;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import javax.annotation.Nullable;

import org.slf4j.Logger;

import com.b2international.commons.Pair;
import com.b2international.snowowl.core.api.IBranchPath;
import com.b2international.snowowl.datastore.ContentAvailabilityInfoManager;
import com.b2international.snowowl.datastore.server.snomed.index.AbstractSnomedTaxonomyBuilder;
import com.b2international.snowowl.datastore.server.snomed.index.SnomedTaxonomyBuilder;
import com.b2international.snowowl.datastore.server.snomed.index.init.Rf2BasedSnomedTaxonomyBuilder;
import com.b2international.snowowl.snomed.datastore.SnomedTerminologyBrowser;
import com.b2international.snowowl.snomed.datastore.index.IncompleteTaxonomyException;
import com.b2international.snowowl.snomed.datastore.services.SnomedConceptNameProvider;
import com.b2international.snowowl.snomed.importer.net4j.ImportConfiguration;
import com.b2international.snowowl.snomed.importer.net4j.SnomedIncompleteTaxonomyValidationDefect;
import com.b2international.snowowl.snomed.importer.net4j.SnomedValidationDefect;
import com.b2international.snowowl.snomed.importer.net4j.SnomedValidationDefect.DefectType;
import com.b2international.snowowl.snomed.importer.rf2.util.Rf2FileModifier;

/**
 * Class for validating the taxonomy of active concepts and active IS_A relationships.
 *
 */
public class SnomedTaxonomyValidator {

	private static final Logger LOGGER = getLogger(SnomedTaxonomyValidator.class);
	
	private final IBranchPath branchPath;
	private final ImportConfiguration configuration;

	public SnomedTaxonomyValidator(final IBranchPath branchPath, final ImportConfiguration configuration) {
		this.configuration = checkNotNull(configuration, "configuration");
		this.branchPath = checkNotNull(branchPath, "branchPath");
	}
	
	/**
	 * Schematically validates the RF2 file by building a taxonomy between the concepts
	 * and the active IS_A relationships. Returns with an empty collection if there were no
	 * validation errors otherwise returns with a collection of {@link SnomedValidationDefect defects}
	 * representing the problems.
	 * @return
	 */
	public Collection<SnomedValidationDefect> validate() {
		final Collection<SnomedValidationDefect> defects = newHashSet();
		if (isCoreImport()) {
			defects.addAll(doValidate());
		}
		return defects;
		
	}

	/*
	 * Two major use cases exist:
	 *  |
	 *  + - snapshot import
	 *  | |
	 *  | +-> build taxonomy based on the content and apply the changes from the release files.
	 *  |
	 *  + - full or delta import
	 *    |
	 *    +-> build the taxonomy from the file. in case of full import split the files based effective times.
	 *  
	 */
	private Collection<SnomedValidationDefect> doValidate() {
		try {
			
			if (isSnapshot()) {
				
				LOGGER.info("Validating SNOMED CT ontology based on the given RF2 release files...");
				
				final String conceptFilePath = removeConceptHeader();
				final String relationshipFilePath = removeRelationshipHeader();
				final Rf2BasedSnomedTaxonomyBuilder builder = createBuilder(conceptFilePath, relationshipFilePath);
				builder.applyNodeChanges(conceptFilePath);
				builder.applyEdgeChanges(relationshipFilePath);
				builder.build();
					
			} else {
			
				LOGGER.info("Validating SNOMED CT ontology based on the given RF2 release files...");
				
				final Map<String, File> conceptFiles = Rf2FileModifier.split(configuration.getConceptsFile());
				final Map<String, File> relationshipFiles = Rf2FileModifier.split(configuration.getRelationshipsFile());
				
				Rf2BasedSnomedTaxonomyBuilder builder = null;
				final List<String> effectiveTimes = newArrayList(newHashSet(concat(conceptFiles.keySet(), relationshipFiles.keySet())));
				sort(effectiveTimes);
				
				for (final String effectiveTime : effectiveTimes) {
					
					LOGGER.info("Validating concepts and relationships from '" + effectiveTime + "'...");
					
					final File conceptFile = conceptFiles.get(effectiveTime);
					final File relationshipFile = relationshipFiles.get(effectiveTime);
					
					builder = null == builder ? createBuilder(getFilePath(conceptFile), getFilePath(relationshipFile)) : newInstance(builder);
					builder.applyNodeChanges(getFilePath(conceptFile));
					builder.applyEdgeChanges(getFilePath(relationshipFile));
					builder.build();
				}
				
			}
			
		} catch (final IOException e) {
			LOGGER.error("Validation failed with an IOException.", e);
			return Collections.<SnomedValidationDefect>singleton(new SnomedValidationDefect(DefectType.IO_PROBLEM, Collections.<String>emptySet()));
		} catch (final IncompleteTaxonomyException e) {
			LOGGER.error("Validation failed.");
			final Collection<String> defects = newHashSet();
			final Collection<String> conceptIdsToInactivate = newHashSet();
			for (final Pair<String, String> conflictingNodes: e.getIncompleteNodePairs()) {
				final String sourceId = conflictingNodes.getA();
				final String destinationId = conflictingNodes.getB();
				
				if (canInactivate(destinationId)) {
					conceptIdsToInactivate.add(destinationId);
				}
				
				if (canInactivate(sourceId)) {
					conceptIdsToInactivate.add(sourceId);
				}
				
				String sourceLabel = SnomedConceptNameProvider.INSTANCE.getComponentLabel(branchPath, sourceId);
				String destinationLabel = SnomedConceptNameProvider.INSTANCE.getComponentLabel(branchPath, destinationId);
				
				final StringBuilder sb = new StringBuilder();
				sb.append("Source concept '");
				sb.append(sourceId);
				if (!isEmpty(sourceLabel)) {
					sb.append("|");
					sb.append(sourceLabel);
					sb.append("|");
				}
				sb.append("' is referencing to a concept that would be inactivated with the current import. Destination concept: '");
				sb.append(destinationId);
				if (!isEmpty(destinationLabel)) {
					sb.append("|");
					sb.append(destinationLabel);
					sb.append("|");
				}
				sb.append("'.");
				defects.add(sb.toString());
			}
			
			final SnomedValidationDefect defect = new SnomedIncompleteTaxonomyValidationDefect(defects, conceptIdsToInactivate);
			return Collections.<SnomedValidationDefect>singleton(defect);
		}
		
		LOGGER.info("SNOMED CT ontology validation successfully finished. No errors where found.");
		return emptySet();
	}

	private boolean canInactivate(final String sourceId) {
		return getServiceForClass(SnomedTerminologyBrowser.class).exists(branchPath, sourceId);
	}

	private String getFilePath(@Nullable final File file) {
		return null == file ?  null : file.getPath();
	}
	
	private String removeConceptHeader() throws IOException {
		return Rf2FileModifier.removeHeader(configuration.getConceptsFile()).getPath();
	}
	
	private String removeRelationshipHeader() throws IOException {
		return Rf2FileModifier.removeHeader(configuration.getRelationshipsFile()).getPath();
	}

	private boolean isSnapshot() {
		return SNAPSHOT.equals(configuration.getVersion());
	}

	private Rf2BasedSnomedTaxonomyBuilder createBuilder(final String conceptFilePath, final String relationshipFilePath) {
		final AbstractSnomedTaxonomyBuilder original = new SnomedTaxonomyBuilder(branchPath).build();
		return newValidationInstance(original, conceptFilePath, relationshipFilePath);
	}

	private boolean isSnomedContentAvailable() {
		return ContentAvailabilityInfoManager.INSTANCE.isAvailable(REPOSITORY_UUID);
	}

	private boolean isCoreImport() {
		return hasConceptImport() && hasRelationshipImport();
	}

	private boolean hasConceptImport() {
		return null != configuration.getConceptsFile();
	}

	private boolean hasRelationshipImport() {
		return null != configuration.getRelationshipsFile();
	}
	
}
