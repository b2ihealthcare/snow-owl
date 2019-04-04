/*
 * Copyright 2011-2019 B2i Healthcare Pte Ltd, http://b2i.sg
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
import static java.util.Collections.emptySet;
import static java.util.Collections.singleton;
import static java.util.stream.Collectors.toList;
import static org.slf4j.LoggerFactory.getLogger;

import java.io.File;
import java.io.IOException;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.SortedMap;

import org.slf4j.Logger;

import com.b2international.collections.longs.LongCollection;
import com.b2international.snowowl.snomed.SnomedConstants.Concepts;
import com.b2international.snowowl.snomed.datastore.taxonomy.InvalidRelationship;
import com.b2international.snowowl.snomed.datastore.taxonomy.InvalidRelationship.MissingConcept;
import com.b2international.snowowl.snomed.datastore.taxonomy.TaxonomyGraphStatus;
import com.b2international.snowowl.snomed.importer.net4j.DefectType;
import com.b2international.snowowl.snomed.importer.net4j.ImportConfiguration;
import com.b2international.snowowl.snomed.importer.net4j.SnomedIncompleteTaxonomyValidationDefect;
import com.b2international.snowowl.snomed.importer.net4j.SnomedValidationDefect;
import com.b2international.snowowl.snomed.importer.rf2.RF2TaxonomyGraph;
import com.b2international.snowowl.snomed.importer.rf2.model.SnomedImportContext;
import com.b2international.snowowl.snomed.importer.rf2.util.Rf2FileModifier;
import com.google.common.base.Strings;
import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.ImmutableSortedMap;
import com.google.common.collect.ImmutableSortedSet;
import com.google.common.collect.Multimap;

/**
 * Class for validating the taxonomy of active concepts and active IS_A relationships.
 *
 */
public class SnomedTaxonomyValidator {

	private static final Logger LOGGER = getLogger(SnomedTaxonomyValidator.class);

	private static final Comparator<String> EFFECTIVE_TIME_COMPARATOR = new Comparator<String>() {
		@Override
		public int compare(String o1, String o2) {
			// consider empty greater than non-empty
			if (o1.isEmpty() && !o2.isEmpty()) {
				return 1;
			} else if (!o1.isEmpty() && o2.isEmpty()) {
				return -1;
			}
			return o1.compareTo(o2);
		}
	};
	
	// new RF2 state
	private final File conceptsFile;
	private final File relationshipsFile;
	private final File owlExpressionFile;
	private final boolean snapshot;
	// current store state
	private final SnomedImportContext context;
	private final String characteristicType;

	private final LongCollection conceptIds;
	private final Collection<Object[]> isaStatements;


	public SnomedTaxonomyValidator(
			final SnomedImportContext context,
			final LongCollection conceptIds,
			final Collection<Object[]> isaStatements, 
			final ImportConfiguration configuration,
			final File owlExpressionFile,
			final String characteristicType) {
		this.context = context;
		this.conceptIds = conceptIds;
		this.isaStatements = isaStatements;
		this.characteristicType = characteristicType;
		this.snapshot = SNAPSHOT.equals(configuration.getContentSubType());
		this.conceptsFile = configuration.getConceptFile();
		this.owlExpressionFile = owlExpressionFile;
		
		if (Concepts.STATED_RELATIONSHIP.equals(characteristicType)) {
			relationshipsFile = configuration.getStatedRelationshipFile();
		} else if (Concepts.INFERRED_RELATIONSHIP.equals(characteristicType)) {
				relationshipsFile = configuration.getRelationshipFile();
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
			
			final Multimap<String, InvalidRelationship> invalidRelationships = processTaxonomy();
			
			if (!invalidRelationships.isEmpty()) {
				
				String messageWithEffectiveTime = "ISA Relationship [%s -> %s] refers to an inactive concept '%s' (%s) in effective time '%s'";
				String messageWithOutEffectiveTime = "ISA Relationship [%s -> %s] refers to an inactive concept '%s' (%s)";
				
				List<String> validationMessages = invalidRelationships.asMap().entrySet().stream().flatMap(entry -> {
					
					String effectiveTime = entry.getKey();
					Collection<InvalidRelationship> relationships = entry.getValue();
					
					return relationships.stream().map(relationship -> {
						
						String missingReference = MissingConcept.DESTINATION == relationship.getMissingConcept()
								? String.valueOf(relationship.getDestinationId()) : String.valueOf(relationship.getSourceId());
								
						String missingReferenceLabel = relationship.getMissingConcept().getLabel();
						
						if (!Strings.isNullOrEmpty(effectiveTime)) {
							return String.format(messageWithEffectiveTime, relationship.getSourceId(), relationship.getDestinationId(), missingReference, missingReferenceLabel, effectiveTime);
						} else {
							return String.format(messageWithOutEffectiveTime, relationship.getSourceId(), relationship.getDestinationId(), missingReference, missingReferenceLabel);
						}
								
					});
					
				}).collect(toList());
				
				LOGGER.info("{} SNOMED CT ontology validation successfully finished. {} taxonomy {} identified.",
						Concepts.STATED_RELATIONSHIP.equals(characteristicType) ? "Stated" : "Inferred", 
						validationMessages.size(),
						validationMessages.size() > 1 ? "issues were" : "issue was");
				
				return singleton(new SnomedIncompleteTaxonomyValidationDefect(relationshipsFile.getName(), validationMessages));
				
			}
			
		} catch (final IOException e) {
			LOGGER.error("Validation failed.", e);
			return singleton(new SnomedValidationDefect(relationshipsFile.getName(), DefectType.IO_PROBLEM, Collections.<String>emptySet()));
		}
		
		LOGGER.info("{} SNOMED CT ontology validation successfully finished. No errors were found.",
				Concepts.STATED_RELATIONSHIP.equals(characteristicType) ? "Stated" : "Inferred");

		return emptySet();
	}

	private Multimap<String, InvalidRelationship> processTaxonomy() throws IOException {
		final RF2TaxonomyGraph graph = new RF2TaxonomyGraph(context, characteristicType);
		graph.init(conceptIds, isaStatements);
		
		final Multimap<String, InvalidRelationship> invalidRelationships = ArrayListMultimap.create();
		if (snapshot) {
			
			LOGGER.info("Validating SNOMED CT ontology based on the given RF2 release files...");
			
			
			graph.applyNodeChanges(conceptsFile);
			graph.applyEdgeChanges(relationshipsFile);
			graph.applyAxioms(owlExpressionFile);
			
			final TaxonomyGraphStatus result = graph.update();
			if (!result.getStatus().isOK()) {
				invalidRelationships.putAll("", result.getInvalidRelationships());
			}
			
		} else {
		
			LOGGER.info("Validating SNOMED CT ontology based on the given RF2 release files...");
			
			final SortedMap<String, File> conceptFiles = hasConceptImport() ? Rf2FileModifier.split(conceptsFile) : ImmutableSortedMap.of();
			final SortedMap<String, File> relationshipFiles = hasRelationshipImport() ? Rf2FileModifier.split(relationshipsFile) : ImmutableSortedMap.of();
			final SortedMap<String, File> owlFiles = hasOwlExpressionFile() ? Rf2FileModifier.split(owlExpressionFile) : ImmutableSortedMap.of();

			final List<String> effectiveTimes = ImmutableSortedSet.orderedBy(EFFECTIVE_TIME_COMPARATOR)
					.addAll(conceptFiles.keySet())
					.addAll(relationshipFiles.keySet())
					.build()
					.asList();
						
			for (final String effectiveTime : effectiveTimes) {
				LOGGER.info("Validating taxonomy in '{}'...", effectiveTime);
				
				final File conceptFile = conceptFiles.get(effectiveTime);
				final File relationshipFile = relationshipFiles.get(effectiveTime);
				final File owlFile = owlFiles.get(effectiveTime);
				
				graph.applyNodeChanges(conceptFile);
				graph.applyEdgeChanges(relationshipFile);
				graph.applyAxioms(owlFile);
				
				final TaxonomyGraphStatus result = graph.update();
				if (!result.getStatus().isOK()) {
					invalidRelationships.putAll(effectiveTime, result.getInvalidRelationships());
				}
			}
			
		}
		
		return invalidRelationships;
	}

	private boolean canValidate() {
		return hasConceptImport() || hasRelationshipImport() || hasOwlExpressionFile();
	}

	private boolean hasConceptImport() {
		return null != conceptsFile && !conceptsFile.getPath().isEmpty();
	}

	private boolean hasRelationshipImport() {
		return null != relationshipsFile && !relationshipsFile.getPath().isEmpty();
	}
	
	private boolean hasOwlExpressionFile() {
		return owlExpressionFile != null;
	}
	
}
