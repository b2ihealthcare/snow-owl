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

import static com.b2international.snowowl.snomed.common.ContentSubType.SNAPSHOT;
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

import com.b2international.collections.longs.LongCollection;
import com.b2international.snowowl.datastore.server.snomed.index.init.Rf2BasedSnomedTaxonomyBuilder;
import com.b2international.snowowl.snomed.SnomedConstants.Concepts;
import com.b2international.snowowl.snomed.datastore.index.entry.SnomedRelationshipIndexEntry;
import com.b2international.snowowl.snomed.datastore.taxonomy.IncompleteTaxonomyException;
import com.b2international.snowowl.snomed.datastore.taxonomy.InvalidRelationship;
import com.b2international.snowowl.snomed.datastore.taxonomy.SnomedTaxonomyBuilder;
import com.b2international.snowowl.snomed.datastore.taxonomy.SnomedTaxonomyBuilderResult;
import com.b2international.snowowl.snomed.importer.net4j.DefectType;
import com.b2international.snowowl.snomed.importer.net4j.ImportConfiguration;
import com.b2international.snowowl.snomed.importer.net4j.SnomedIncompleteTaxonomyValidationDefect;
import com.b2international.snowowl.snomed.importer.net4j.SnomedValidationDefect;
import com.b2international.snowowl.snomed.importer.rf2.RepositoryState;
import com.b2international.snowowl.snomed.importer.rf2.util.Rf2FileModifier;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSortedSet;
import com.google.common.collect.Lists;

/**
 * Class for validating the taxonomy of active concepts and active IS_A relationships.
 *
 */
public class SnomedTaxonomyValidator {

	private static final Logger LOGGER = getLogger(SnomedTaxonomyValidator.class);
	
	// new RF2 state
	private final File conceptsFile;
	private final File relationshipsFile;
	private final boolean snapshot;
	// current store state
	private final String characteristicType;
	private final LongCollection conceptIds;
	private final Collection<SnomedRelationshipIndexEntry.Views.StatementWithId> statements;

	public SnomedTaxonomyValidator(final ImportConfiguration configuration,
			final RepositoryState repositoryState,
			final String characteristicType) {
		this.characteristicType = characteristicType;
		this.snapshot = SNAPSHOT.equals(configuration.getVersion());
		this.conceptsFile = configuration.getConceptsFile();
		this.conceptIds = repositoryState.getConceptIds(); 
		this.statements = Concepts.INFERRED_RELATIONSHIP.equals(characteristicType) ? repositoryState.getInferredStatements() : repositoryState.getStatedStatements();
		
		if (Concepts.STATED_RELATIONSHIP.equals(characteristicType)) {
			relationshipsFile = configuration.getStatedRelationshipsFile();
		} else if (Concepts.INFERRED_RELATIONSHIP.equals(characteristicType)) {
			relationshipsFile = configuration.getRelationshipsFile();
		} else {
			throw new IllegalArgumentException("Collection mode " + characteristicType + " is not allowed.");
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
			final Rf2BasedSnomedTaxonomyBuilder builder = Rf2BasedSnomedTaxonomyBuilder.newInstance(new SnomedTaxonomyBuilder(conceptIds, statements), characteristicType);
			if (snapshot) {
				
				LOGGER.info("Validating SNOMED CT ontology based on the given RF2 release files...");
				
				
				if (hasConceptImport()) {
					final String conceptFilePath = removeConceptHeader();
					builder.applyNodeChanges(conceptFilePath);
				}
				
				if (hasRelationshipImport()) {
					final String relationshipFilePath = removeRelationshipHeader();
					builder.applyEdgeChanges(relationshipFilePath);
				}
				
				final SnomedTaxonomyBuilderResult result = builder.build();
				if (!result.getStatus().isOK()) {
					throw new IncompleteTaxonomyException(Lists.newArrayList(result.getInvalidRelationships()));
				}
			} else {
			
				LOGGER.info("Validating SNOMED CT ontology based on the given RF2 release files...");
				
				final Map<String, File> conceptFiles = hasConceptImport() ? Rf2FileModifier.split(conceptsFile) : ImmutableMap.<String, File>of();
				final Map<String, File> relationshipFiles = hasRelationshipImport() ? Rf2FileModifier.split(relationshipsFile) : ImmutableMap.<String, File>of();

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
			for (final InvalidRelationship invalidRelationship: e.getInvalidRelationships()) {
				final String sourceId = Long.toString(invalidRelationship.getSourceId());
				final String destinationId = Long.toString(invalidRelationship.getDestinationId());
				
				if (conceptIds.contains(invalidRelationship.getDestinationId())) {
					conceptIdsToInactivate.add(destinationId);
				}
				
				if (conceptIds.contains(invalidRelationship.getSourceId())) {
					conceptIdsToInactivate.add(sourceId);
				}
				
				final StringBuilder sb = new StringBuilder();
				sb.append("IS A relationship");
				sb.append(" '" + invalidRelationship.getRelationshipId() + "'");
				sb.append(" has a missing or inactive ");
				
				switch (invalidRelationship.getMissingConcept()) {
					case DESTINATION:
						sb.append("destination concept");
						sb.append(" '" + destinationId);
						break;
					case SOURCE:
						sb.append("source concept");
						sb.append(" '" + sourceId);
						break;
					default:
						throw new IllegalStateException("Unexpected missing concept type '" + invalidRelationship.getMissingConcept() + "'.");					
				}
				
				sb.append("'.");
				
				defects.add(sb.toString());
			}
			
			final SnomedValidationDefect defect = new SnomedIncompleteTaxonomyValidationDefect(defects, conceptIdsToInactivate);
			return Collections.<SnomedValidationDefect>singleton(defect);
		}
		
		LOGGER.info("SNOMED CT ontology validation successfully finished. No errors were found.");
		return emptySet();
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
