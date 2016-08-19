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
import static com.b2international.snowowl.datastore.server.snomed.index.init.Rf2BasedSnomedTaxonomyBuilder.newValidationInstance;
import static com.b2international.snowowl.snomed.common.ContentSubType.SNAPSHOT;
import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.collect.Sets.newHashSet;
import static java.util.Collections.emptySet;
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
import com.b2international.snowowl.datastore.server.snomed.index.init.Rf2BasedSnomedTaxonomyBuilder;
import com.b2international.snowowl.snomed.datastore.SnomedTerminologyBrowser;
import com.b2international.snowowl.snomed.datastore.StatementCollectionMode;
import com.b2international.snowowl.snomed.datastore.services.ISnomedConceptNameProvider;
import com.b2international.snowowl.snomed.datastore.taxonomy.AbstractSnomedTaxonomyBuilder;
import com.b2international.snowowl.snomed.datastore.taxonomy.IncompleteTaxonomyException;
import com.b2international.snowowl.snomed.datastore.taxonomy.SnomedTaxonomyBuilder;
import com.b2international.snowowl.snomed.importer.net4j.DefectType;
import com.b2international.snowowl.snomed.importer.net4j.ImportConfiguration;
import com.b2international.snowowl.snomed.importer.net4j.SnomedIncompleteTaxonomyValidationDefect;
import com.b2international.snowowl.snomed.importer.net4j.SnomedValidationDefect;
import com.b2international.snowowl.snomed.importer.rf2.util.Rf2FileModifier;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSortedSet;

/**
 * Class for validating the taxonomy of active concepts and active IS_A relationships.
 *
 */
public class SnomedTaxonomyValidator {

	private static final Logger LOGGER = getLogger(SnomedTaxonomyValidator.class);
	
	private final IBranchPath branchPath;
	private final StatementCollectionMode mode;
	private final File conceptsFile;
	private final File relationshipsFile;
	private final boolean snapshot;

	public SnomedTaxonomyValidator(final IBranchPath branchPath, final ImportConfiguration configuration, final StatementCollectionMode mode) {
		this.branchPath = checkNotNull(branchPath, "branchPath");
		this.mode = checkNotNull(mode, "mode");
		this.snapshot = SNAPSHOT.equals(configuration.getVersion());
		this.conceptsFile = configuration.getConceptsFile();
		
		if (mode == StatementCollectionMode.STATED_ISA_ONLY) {
			relationshipsFile = configuration.getStatedRelationshipsFile();
		} else if (mode == StatementCollectionMode.INFERRED_ISA_ONLY) {
			relationshipsFile = configuration.getRelationshipsFile();
		} else {
			throw new IllegalArgumentException("Collection mode " + mode + " is not allowed.");
		}
	}
	
	/**
	 * Schematically validates the RF2 file by building a taxonomy between the concepts
	 * and the active IS_A relationships. Returns with an empty collection if there were no
	 * validation errors otherwise returns with a collection of {@link SnomedValidationDefect defects}
	 * representing the problems.
	 * @return
	 */
	public Collection<SnomedValidationDefect> validate() {
		if (canValidate()) {
			return doValidate();
		}
		return Collections.emptySet();
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
			
			if (snapshot) {
				
				LOGGER.info("Validating SNOMED CT ontology based on the given RF2 release files...");
				
				final Rf2BasedSnomedTaxonomyBuilder builder = createBuilder();
				
				if (hasConceptImport()) {
					final String conceptFilePath = removeConceptHeader();
					builder.applyNodeChanges(conceptFilePath);
				}
				
				if (hasRelationshipImport()) {
					final String relationshipFilePath = removeRelationshipHeader();
					builder.applyEdgeChanges(relationshipFilePath);
				}
				
				builder.build();
					
			} else {
			
				LOGGER.info("Validating SNOMED CT ontology based on the given RF2 release files...");
				
				final Map<String, File> conceptFiles = hasConceptImport() ? Rf2FileModifier.split(conceptsFile) : ImmutableMap.<String, File>of();
				final Map<String, File> relationshipFiles = hasRelationshipImport() ? Rf2FileModifier.split(relationshipsFile) : ImmutableMap.<String, File>of();

				final Rf2BasedSnomedTaxonomyBuilder builder = createBuilder();
				final List<String> effectiveTimes = ImmutableSortedSet.<String>naturalOrder()
						.addAll(conceptFiles.keySet())
						.addAll(relationshipFiles.keySet())
						.build()
						.asList();
				
				for (final String effectiveTime : effectiveTimes) {
					LOGGER.info("Validating concepts and relationships from '" + effectiveTime + "'...");
					
					final File conceptFile = conceptFiles.get(effectiveTime);
					final File relationshipFile = relationshipFiles.get(effectiveTime);
					
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
				
				String sourceLabel = getNameProvider().getComponentLabel(branchPath, sourceId);
				String destinationLabel = getNameProvider().getComponentLabel(branchPath, destinationId);
				
				final StringBuilder sb = new StringBuilder();
				sb.append("IS A relationship with source concept '");
				sb.append(sourceId);
				if (!isEmpty(sourceLabel)) {
					sb.append("|");
					sb.append(sourceLabel);
					sb.append("|");
				}
				sb.append("' and destination concept '");
				sb.append(destinationId);
				if (!isEmpty(destinationLabel)) {
					sb.append("|");
					sb.append(destinationLabel);
					sb.append("|");
				}
				sb.append("' has a missing or inactive source or destination concept.");
				defects.add(sb.toString());
			}
			
			final SnomedValidationDefect defect = new SnomedIncompleteTaxonomyValidationDefect(defects, conceptIdsToInactivate);
			return Collections.<SnomedValidationDefect>singleton(defect);
		}
		
		LOGGER.info("SNOMED CT ontology validation successfully finished. No errors were found.");
		return emptySet();
	}

	private ISnomedConceptNameProvider getNameProvider() {
		return getServiceForClass(ISnomedConceptNameProvider.class);
	}

	private boolean canInactivate(final String sourceId) {
		return getServiceForClass(SnomedTerminologyBrowser.class).exists(branchPath, sourceId);
	}

	private String getFilePath(@Nullable final File file) {
		return null == file ?  null : file.getPath();
	}
	
	private String removeConceptHeader() throws IOException {
		return Rf2FileModifier.removeHeader(conceptsFile).getPath();
	}
	
	private String removeRelationshipHeader() throws IOException {
		return Rf2FileModifier.removeHeader(relationshipsFile).getPath();
	}

	private Rf2BasedSnomedTaxonomyBuilder createBuilder() {
		final AbstractSnomedTaxonomyBuilder original = new SnomedTaxonomyBuilder(branchPath, mode);
		return newValidationInstance(original, mode.getCharacteristicType());
	}

	private boolean canValidate() {
		return hasConceptImport() || hasRelationshipImport();
	}

	private boolean hasConceptImport() {
		return null != conceptsFile && !conceptsFile.getPath().isEmpty();
	}

	private boolean hasRelationshipImport() {
		return null != relationshipsFile && !relationshipsFile.getPath().isEmpty();
	}
}
