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
import com.b2international.snowowl.core.ApplicationContext;
import com.b2international.snowowl.core.LogUtils;
import com.b2international.snowowl.core.api.IBranchPath;
import com.b2international.snowowl.core.exceptions.AlreadyExistsException;
import com.b2international.snowowl.core.terminology.ComponentCategory;
import com.b2international.snowowl.datastore.BranchPathUtils;
import com.b2international.snowowl.importer.ImportException;
import com.b2international.snowowl.snomed.common.SnomedRf2Headers;
import com.b2international.snowowl.snomed.datastore.SnomedConceptLookupService;
import com.b2international.snowowl.snomed.datastore.SnomedDescriptionLookupService;
import com.b2international.snowowl.snomed.datastore.SnomedRelationshipLookupService;
import com.b2international.snowowl.snomed.datastore.StatementCollectionMode;
import com.b2international.snowowl.snomed.datastore.id.SnomedIdentifierValidator;
import com.b2international.snowowl.snomed.datastore.id.SnomedIdentifiers;
import com.b2international.snowowl.snomed.datastore.services.IClientSnomedComponentService;
import com.b2international.snowowl.snomed.importer.net4j.DefectType;
import com.b2international.snowowl.snomed.importer.net4j.ImportConfiguration;
import com.b2international.snowowl.snomed.importer.net4j.SnomedValidationDefect;
import com.google.common.base.Charsets;
import com.google.common.base.Predicates;
import com.google.common.base.Supplier;
import com.google.common.base.Suppliers;
import com.google.common.collect.Iterables;
import com.google.common.collect.LinkedHashMultimap;
import com.google.common.collect.Lists;
import com.google.common.collect.Multimap;
import com.google.common.collect.Ordering;

import bak.pcj.set.LongSet;

/**
 * Provides utility methods for validating the release files.
 */
public final class SnomedValidationContext {
	
	private final Supplier<LongSet> descriptionIdSupplier = Suppliers.memoize(new Supplier<LongSet>() {
		@Override public LongSet get() {
			return ApplicationContext.getInstance().getService(IClientSnomedComponentService.class).getAllDescriptionIds();
		}
	});
	
	
	private final Multimap<DefectType, String> defects = LinkedHashMultimap.create();
	private final ImportConfiguration configuration;
	private final List<AbstractSnomedValidator> releaseFileValidators = Lists.newArrayList();
	
	private final Map<String, Boolean> componentStatus = newHashMap();
	
	private final String requestingUserId;
	private final IBranchPath branchPath;
	private final Logger logger;
	private final SnomedConceptLookupService conceptLookupService = new SnomedConceptLookupService();
	private final SnomedDescriptionLookupService descriptionLookupService = new SnomedDescriptionLookupService();
	private final SnomedRelationshipLookupService relationshipLookupService = new SnomedRelationshipLookupService();
	private final Set<String> effectiveTimes = newHashSet();
	
	public SnomedValidationContext(String requestingUserId, final ImportConfiguration configuration, Logger logger) {
		this.logger = logger;
		this.requestingUserId = requestingUserId;
		this.configuration = configuration;
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
			if (!"".equals(effectiveTime)) {
				runValidators(effectiveTime, subMonitor);
			}
		}
		
		// validate Unpublished effective time last
		if (effectiveTimes.contains("")) {
			runValidators("", subMonitor.newChild(1));
		}
	}
	
	private void runValidators(String effectiveTime, SubMonitor subMonitor) {
		for (final AbstractSnomedValidator releaseFileValidator : releaseFileValidators) {
			releaseFileValidator.doValidate(effectiveTime, subMonitor.newChild(1));
		}		
	}

	private void postValidate(final SubMonitor monitor) {
		for (final AbstractSnomedValidator releaseFileValidator : releaseFileValidators) {
			releaseFileValidator.postValidate(monitor);
		}
	}
	
	private void addReleaseFilesForValidating() throws IOException {
		if (isValidReleaseFile(configuration.getConceptsFile())) {
			releaseFileValidators.add(new SnomedConceptValidator(configuration, this));
		}

		if (isValidReleaseFile(configuration.getDescriptionsFile())) {
			releaseFileValidators.add(new SnomedDescriptionValidator(configuration, this));
		}

		if (isValidReleaseFile(configuration.getTextDefinitionFile())) {
			releaseFileValidators.add(new SnomedDescriptionValidator(configuration, this));
		}

		if (isValidReleaseFile(configuration.getRelationshipsFile())) {
			releaseFileValidators.add(new SnomedRelationshipValidator(configuration, this));
		}
		
		if (isValidReleaseFile(configuration.getStatedRelationshipsFile())) {
			releaseFileValidators.add(new SnomedRelationshipValidator(configuration, this));
		}
	}
	
	private void addRefSetFilesForValidating() throws IOException {
		
		for (final URL url : configuration.getRefSetUrls()) {
			addRefSetFile(url);
		}

		//if the reference file URL set does not contain a language validator yet, specify one 
		if (!Iterables.any(releaseFileValidators, Predicates.instanceOf(SnomedLanguageRefSetValidator.class))) {
			
			if (isValidReleaseFile(configuration.getLanguageRefSetFile())) {
				releaseFileValidators.add(new SnomedLanguageRefSetValidator(configuration, configuration.toURL(configuration.getLanguageRefSetFile()), this));
			}
			
		}
	}
	
	public boolean isValidReleaseFile(final File releaseFile) {
		return null != releaseFile && !releaseFile.getPath().isEmpty();
	}

	private void addRefSetFile(final URL url) throws IOException {
		
		try (BufferedReader reader = new BufferedReader(new InputStreamReader(url.openStream(), Charsets.UTF_8))) {
			final String header = reader.readLine();
			
			//guard against invalid files/folders in the SCT RF2 archive/root folder
			if (StringUtils.isEmpty(header)) {
				return;
			}
			
			final String lastColumn = header.substring(header.lastIndexOf("\t") + 1);
			
			if (lastColumn.equalsIgnoreCase(SnomedRf2Headers.FIELD_REFERENCED_COMPONENT_ID)) {
				releaseFileValidators.add(new SnomedSimpleTypeRefSetValidator(configuration, url, this));
			} else if (lastColumn.equalsIgnoreCase(SnomedRf2Headers.FIELD_VALUE_ID)) {
				releaseFileValidators.add(new SnomedAttributeValueRefSetValidator(configuration, url, this));
			} else if (lastColumn.equalsIgnoreCase(SnomedRf2Headers.FIELD_CHARACTERISTIC_TYPE_ID))  {
				releaseFileValidators.add(new SnomedConcreteDataTypeRefSetValidator(configuration, url, this, true));
			} else if (lastColumn.equalsIgnoreCase(SnomedRf2Headers.FIELD_VALUE)) { // AU CDT refset
				releaseFileValidators.add(new SnomedConcreteDataTypeRefSetValidator(configuration, url, this, false));
			} else if (lastColumn.equalsIgnoreCase(SnomedRf2Headers.FIELD_MAP_TARGET)) {
				releaseFileValidators.add(new SnomedSimpleMapTypeRefSetValidator(configuration, url, this, false));
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
				releaseFileValidators.add(new SnomedSimpleMapTypeRefSetValidator(configuration, url, this, true));	
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
		
		validationResult.addAll(new SnomedTaxonomyValidator(branchPath, configuration, StatementCollectionMode.INFERRED_ISA_ONLY).validate());
		validationResult.addAll(new SnomedTaxonomyValidator(branchPath, configuration, StatementCollectionMode.STATED_ISA_ONLY).validate());
		
		for (DefectType type : this.defects.keySet()) {
			final Collection<String> messages = this.defects.get(type);
			validationResult.add(new SnomedValidationDefect(type, messages));
		}

		return validationResult;
	}

	/*package*/ void registerComponent(ComponentCategory category, String componentId, boolean status) throws AlreadyExistsException {
		if (!getIdValidator(category).isValid(componentId)) {
			addDefect(DefectType.INVALID_ID, String.format("'%s' is not a valid '%s' identifier", componentId, category.name()));
		}
		// update status, component line registration should happen in effective time order
		componentStatus.put(componentId, status);
	}

	/*package*/ boolean isComponentExists(String componentId, ComponentCategory componentCategory) {
		boolean exists = componentStatus.containsKey(componentId);
		
		if (!exists) {
			exists = existsInStore(componentId, componentCategory);
		}
		
		return exists;
	}

	/*package*/ boolean isComponentActive(String componentId, ComponentCategory category) {
		if (componentStatus.containsKey(componentId)) {
			return componentStatus.get(componentId);
		} else {
			return activeInStore(componentId, category);
		}
	}
	
	/*package*/ void addDefect(DefectType type, String...defects) {
		addDefect(type, Arrays.asList(defects));
	}
	
	/*package*/ void addDefect(DefectType type, Iterable<String> defects) {
		this.defects.putAll(type, defects);
	}
	
	private SnomedIdentifierValidator getIdValidator(ComponentCategory category) {
		return SnomedIdentifiers.getIdentifierValidator(category);
	}

	private boolean existsInStore(String componentId, ComponentCategory componentCategory) {
		switch (componentCategory) {
		case CONCEPT: return conceptLookupService.exists(branchPath, componentId);
		case DESCRIPTION: return descriptionIdSupplier.get().contains(Long.parseLong(componentId));
		case RELATIONSHIP: return relationshipLookupService.exists(branchPath, componentId);
		default: throw new UnsupportedOperationException("Cannot get lookup service for " + componentCategory);
		}
	}
	
	private boolean activeInStore(String componentId, ComponentCategory componentCategory) {
		switch (componentCategory) {
		case CONCEPT: return conceptLookupService.getComponent(branchPath, componentId).isActive();
		case DESCRIPTION: return descriptionLookupService.getComponent(branchPath, componentId).isActive();
		case RELATIONSHIP: return relationshipLookupService.getComponent(branchPath, componentId).isActive();
		default: throw new UnsupportedOperationException("Cannot get lookup service for " + componentCategory);
		}
	}
	
}