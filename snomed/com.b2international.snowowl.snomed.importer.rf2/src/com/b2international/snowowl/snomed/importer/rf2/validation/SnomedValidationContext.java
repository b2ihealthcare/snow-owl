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
package com.b2international.snowowl.snomed.importer.rf2.validation;


import static com.google.common.collect.Maps.newHashMap;
import static com.google.common.collect.Sets.newHashSet;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.SubMonitor;
import org.slf4j.Logger;

import com.b2international.commons.StringUtils;
import com.b2international.index.Hits;
import com.b2international.index.query.Query;
import com.b2international.index.revision.RevisionSearcher;
import com.b2international.snowowl.core.LogUtils;
import com.b2international.snowowl.core.api.IBranchPath;
import com.b2international.snowowl.core.api.SnowowlRuntimeException;
import com.b2international.snowowl.core.exceptions.AlreadyExistsException;
import com.b2international.snowowl.core.terminology.ComponentCategory;
import com.b2international.snowowl.datastore.BranchPathUtils;
import com.b2international.snowowl.snomed.SnomedConstants.Concepts;
import com.b2international.snowowl.snomed.common.SnomedRf2Headers;
import com.b2international.snowowl.snomed.datastore.id.SnomedIdentifierValidator;
import com.b2international.snowowl.snomed.datastore.id.SnomedIdentifiers;
import com.b2international.snowowl.snomed.datastore.index.entry.SnomedDocument;
import com.b2international.snowowl.snomed.importer.ImportException;
import com.b2international.snowowl.snomed.importer.net4j.DefectType;
import com.b2international.snowowl.snomed.importer.net4j.ImportConfiguration;
import com.b2international.snowowl.snomed.importer.net4j.SnomedValidationDefect;
import com.b2international.snowowl.snomed.importer.rf2.RepositoryState;
import com.google.common.base.Charsets;
import com.google.common.base.Splitter;
import com.google.common.base.Strings;
import com.google.common.collect.Iterables;
import com.google.common.collect.LinkedListMultimap;
import com.google.common.collect.Lists;
import com.google.common.collect.Multimap;
import com.google.common.collect.Ordering;

/**
 * Provides utility methods for validating the release files.
 */
public final class SnomedValidationContext {
	
	private static final Splitter TAB_SPLITTER = Splitter.on("\t");
	
	private final Map<String, Multimap<DefectType, String>> defects = newHashMap();
	private final ImportConfiguration configuration;
	private final List<AbstractSnomedValidator> releaseFileValidators = Lists.newArrayList();
	
	private final Map<String, Boolean> componentStatus = newHashMap();
	
	private final String requestingUserId;
	private final IBranchPath branchPath;
	private final Logger logger;
	private final Set<String> effectiveTimes = newHashSet();
	private final RevisionSearcher searcher;
	private final RepositoryState repositoryState;
	
	public SnomedValidationContext(final RevisionSearcher searcher, 
			final String requestingUserId, 
			final ImportConfiguration configuration, 
			final Logger logger,
			final RepositoryState repositoryState) {
		this.searcher = searcher;
		this.logger = logger;
		this.requestingUserId = requestingUserId;
		this.configuration = configuration;
		this.repositoryState = repositoryState;
		this.branchPath = BranchPathUtils.createPath(configuration.getBranchPath());
		try {
			addReleaseFilesForValidating();
			addRefSetFilesForValidating();
		} catch (final IOException e) {
			throw new ImportException("Exception caught while collecting release files for validation.", e);
		}
	}
	
	private void preValidate(final SubMonitor monitor) {
		
		for (final AbstractSnomedValidator releaseFileValidator : releaseFileValidators) {
			effectiveTimes.addAll(releaseFileValidator.preValidate(monitor));
		}
	}

	private void doValidate(final SubMonitor monitor) {
		final SubMonitor subMonitor = SubMonitor.convert(monitor, effectiveTimes.size());
		for (String effectiveTime : Ordering.natural().immutableSortedCopy(effectiveTimes)) {
			logger.info("Validating components {}{}...", 
					Strings.isNullOrEmpty(effectiveTime) ? "without effectivetime" : "in effective time ",
					Strings.isNullOrEmpty(effectiveTime) ? "" : effectiveTime);
			if (!AbstractSnomedValidator.SPECIAL_EFFECTIVE_TIME_KEY.equals(effectiveTime)) {
				runValidators(effectiveTime, subMonitor);
			}
		}
		
		// Validate an unpublished effective time layer, or a combined snapshot
		if (effectiveTimes.contains(AbstractSnomedValidator.SPECIAL_EFFECTIVE_TIME_KEY)) {
			runValidators(AbstractSnomedValidator.SPECIAL_EFFECTIVE_TIME_KEY, subMonitor.newChild(1));
		}
	}
	
	private void runValidators(String effectiveTime, SubMonitor subMonitor) {
		for (final AbstractSnomedValidator releaseFileValidator : releaseFileValidators) {
			releaseFileValidator.doValidate(effectiveTime, subMonitor.newChild(1));
			releaseFileValidator.clearCaches();
		}		
	}

	private void postValidate(final SubMonitor monitor) {
		for (final AbstractSnomedValidator releaseFileValidator : releaseFileValidators) {
			releaseFileValidator.postValidate(monitor);
			releaseFileValidator.clearCaches();
		}
	}
	
	private void addReleaseFilesForValidating() throws IOException {
		if (isValidReleaseFile(configuration.getConceptFile())) {
			releaseFileValidators.add(new SnomedConceptValidator(configuration, this));
		}

		for (File descFile : configuration.getDescriptionFiles()) {
			if (isValidReleaseFile(descFile)) {
				releaseFileValidators.add(new SnomedDescriptionValidator(configuration, this, descFile));
			}
		}

		for (File textFile : configuration.getTextDefinitionFiles()) {
			if (isValidReleaseFile(textFile)) {
				releaseFileValidators.add(new SnomedDescriptionValidator(configuration, this, textFile));
			}
		}

		if (isValidReleaseFile(configuration.getRelationshipFile())) {
			releaseFileValidators.add(new SnomedRelationshipValidator(configuration, this, configuration.getRelationshipFile()));
		}
		
		if (isValidReleaseFile(configuration.getStatedRelationshipFile())) {
			releaseFileValidators.add(new SnomedRelationshipValidator(configuration, this, configuration.getStatedRelationshipFile()));
		}
	}
	
	private void addRefSetFilesForValidating() throws IOException {
		
		for (final URL url : configuration.getRefSetUrls()) {
			addRefSetFile(url);
		}
		
	}
	
	public boolean isValidReleaseFile(final File releaseFile) {
		return null != releaseFile && !releaseFile.getPath().isEmpty();
	}

	private void addRefSetFile(final URL url) throws IOException {
		
		try (BufferedReader reader = new BufferedReader(new InputStreamReader(url.openStream(), Charsets.UTF_8))) {
			final String header = reader.readLine();
			
			// guard against invalid files/folders in the SCT RF2 archive/root folder
			if (StringUtils.isEmpty(header)) {
				return;
			}
			
			List<String> headerElements = TAB_SPLITTER.splitToList(header);
			final String lastColumn = Iterables.getLast(headerElements);
			
			if (lastColumn.equalsIgnoreCase(SnomedRf2Headers.FIELD_REFERENCED_COMPONENT_ID)) {
				releaseFileValidators.add(new SnomedSimpleTypeRefSetValidator(configuration, url, this));
			} else if (lastColumn.equalsIgnoreCase(SnomedRf2Headers.FIELD_VALUE_ID)) {
				releaseFileValidators.add(new SnomedAttributeValueRefSetValidator(configuration, url, this));
			} else if (lastColumn.equalsIgnoreCase(SnomedRf2Headers.FIELD_CHARACTERISTIC_TYPE_ID))  {
				releaseFileValidators.add(new SnomedConcreteDataTypeRefSetValidator(configuration, url, this));
			} else if (lastColumn.equalsIgnoreCase(SnomedRf2Headers.FIELD_MAP_TARGET)) {
				releaseFileValidators.add(new SnomedSimpleMapTypeRefSetValidator(configuration, url, this));
			} else if (lastColumn.equalsIgnoreCase(SnomedRf2Headers.FIELD_CORRELATION_ID)) {
				releaseFileValidators.add(new SnomedComplexMapTypeRefSetValidator(configuration, url, this));
			} else if (lastColumn.equalsIgnoreCase(SnomedRf2Headers.FIELD_DESCRIPTION_LENGTH)) {
				releaseFileValidators.add(new SnomedDescriptionTypeRefSetValidator(configuration, url, this));
			} else if (lastColumn.equalsIgnoreCase(SnomedRf2Headers.FIELD_TARGET_COMPONENT) || lastColumn.equalsIgnoreCase(SnomedRf2Headers.FIELD_TARGET_COMPONENT_ID)) {
				releaseFileValidators.add(new SnomedAssociationRefSetValidator(configuration, url, this));
			} else if (lastColumn.equalsIgnoreCase(SnomedRf2Headers.FIELD_ACCEPTABILITY_ID)) {
				releaseFileValidators.add(new SnomedLanguageRefSetValidator(configuration, url, this));
			} else if (lastColumn.equalsIgnoreCase(SnomedRf2Headers.FIELD_TARGET_EFFECTIVE_TIME)) {
				releaseFileValidators.add(new SnomedModuleDependencyRefSetValidator(configuration, url, this));
			} else if (lastColumn.equals(SnomedRf2Headers.FIELD_MAP_CATEGORY_ID)) {
				releaseFileValidators.add(new SnomedExtendedMapTypeRefSetValidator(configuration, url, this));
			} else if (lastColumn.equalsIgnoreCase(SnomedRf2Headers.FIELD_MAP_TARGET_DESCRIPTION)) {
				releaseFileValidators.add(new SnomedSimpleMapWithDescriptionRefSetValidator(configuration, url, this));	
			} else if (lastColumn.equalsIgnoreCase(SnomedRf2Headers.FIELD_OWL_EXPRESSION)) {
				releaseFileValidators.add(new SnomedOWLExpressionRefSetValidator(configuration, url, this));
			} else if (lastColumn.equalsIgnoreCase(SnomedRf2Headers.FIELD_MRCM_EDITORIAL_GUIDE_REFERENCE)) {
				releaseFileValidators.add(new SnomedMRCMDomainRefSetValidator(configuration, url, this));
			} else if (lastColumn.equalsIgnoreCase(SnomedRf2Headers.FIELD_MRCM_RULE_REFSET_ID)) {
				releaseFileValidators.add(new SnomedMRCMModuleScopeRefSetValidator(configuration, url, this));
			} else if (lastColumn.equalsIgnoreCase(SnomedRf2Headers.FIELD_MRCM_CONTENT_TYPE_ID)) {
				if (headerElements.contains(SnomedRf2Headers.FIELD_MRCM_DOMAIN_ID)) {
					releaseFileValidators.add(new SnomedMRCMAttributeDomainRefSetValidator(configuration, url, this));
				} else if (headerElements.contains(SnomedRf2Headers.FIELD_MRCM_RANGE_CONSTRAINT)) {
					releaseFileValidators.add(new SnomedMRCMAttributeRangeRefSetValidator(configuration, url, this));
				}
			} else {
				logger.warn("Couldn't determine reference set type for file '" + configuration.getMappedName(url.getPath()) + "', not validating.");
			}
		}
	}

	public org.slf4j.Logger getLogger() {
		return logger;
	}

	public Collection<SnomedValidationDefect> validate(IProgressMonitor monitor) {
		final SubMonitor subMonitor = SubMonitor.convert(monitor, 3);
		logger.info("Validating release files...");
		LogUtils.logImportActivity(logger, requestingUserId, branchPath, "Validating RF2 release files.");

		preValidate(subMonitor.newChild(1, SubMonitor.SUPPRESS_NONE));
		doValidate(subMonitor.newChild(1, SubMonitor.SUPPRESS_NONE));
		postValidate(subMonitor.newChild(1, SubMonitor.SUPPRESS_NONE));

		final Collection<SnomedValidationDefect> validationResult = newHashSet();

		if (configuration.isValidReleaseFile(configuration.getStatedRelationshipFile())) {
			validationResult.addAll(new SnomedTaxonomyValidator(configuration, repositoryState, Concepts.STATED_RELATIONSHIP).validate());
		}
		
		if (configuration.isValidReleaseFile(configuration.getRelationshipFile())) {
			validationResult.addAll(new SnomedTaxonomyValidator(configuration, repositoryState, Concepts.INFERRED_RELATIONSHIP).validate());
		}
		
		this.defects.forEach((file, defects) -> {
			defects.asMap().forEach((type, messages) -> {
				validationResult.add(new SnomedValidationDefect(file, type, messages));
			});
		});

		return validationResult;
	}

	/*package*/ void registerComponent(final String sourceFile, ComponentCategory category, String componentId, boolean status) throws AlreadyExistsException {
		if (!getIdValidator(category).isValid(componentId)) {
			addDefect(sourceFile, DefectType.INVALID_ID, String.format("'%s' is not a valid '%s' identifier", componentId, category.name()));
		}
		// update status, component line registration should happen in effective time order
		componentStatus.put(componentId, status);
	}

	/*package*/ boolean isComponentExists(String componentId, ComponentCategory componentCategory) {
		if (componentStatus.containsKey(componentId)) {
			return true;
		} else {
			try {
				return existsInStore(componentId, componentCategory);
			} catch (IOException e) {
				throw new SnowowlRuntimeException(e);
			}
		}
	}

	/*package*/ boolean isComponentActive(String componentId, ComponentCategory category) {
		if (componentStatus.containsKey(componentId)) {
			return componentStatus.get(componentId);
		} else {
			try {
				return activeInStore(componentId, category);
			} catch (IOException e) {
				throw new SnowowlRuntimeException(e);
			}
		}
	}
	
	/*package*/ void addDefect(final String file, DefectType type, String...defects) {
		addDefect(file, type, Arrays.asList(defects));
	}
	
	/*package*/ void addDefect(final String file, DefectType type, Iterable<String> defects) {
		if (!this.defects.containsKey(file)) {
			this.defects.put(file, LinkedListMultimap.create());
		}
		this.defects.get(file).putAll(type, defects);
	}
	
	private SnomedIdentifierValidator getIdValidator(ComponentCategory category) {
		return SnomedIdentifiers.getIdentifierValidator(category);
	}

	private boolean existsInStore(String componentId, ComponentCategory componentCategory) throws IOException {
		final Class<? extends SnomedDocument> type = SnomedDocument.getType(componentCategory);
		final Query<? extends SnomedDocument> query = Query.select(type).where(SnomedDocument.Expressions.id(componentId)).limit(0).build();
		return searcher.search(query).getTotal() > 0;
	}
	
	private boolean activeInStore(String componentId, ComponentCategory componentCategory) throws IOException {
		final Class<? extends SnomedDocument> type = SnomedDocument.getType(componentCategory);
		final Query<? extends SnomedDocument> query = Query.select(type).where(SnomedDocument.Expressions.id(componentId)).limit(1).build();
		final Hits<? extends SnomedDocument> hits = searcher.search(query);
		return hits.getTotal() > 0 ? Iterables.getOnlyElement(hits).isActive() : false;
	}
}
