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
package com.b2international.snowowl.snomed.importer.rf2.util;


import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.List;
import java.util.Set;

import org.eclipse.core.runtime.SubMonitor;
import org.slf4j.Logger;

import com.b2international.commons.StringUtils;
import com.b2international.snowowl.core.LogUtils;
import com.b2international.snowowl.core.api.IBranchPath;
import com.b2international.snowowl.datastore.BranchPathUtils;
import com.b2international.snowowl.importer.ImportException;
import com.b2international.snowowl.snomed.common.SnomedRf2Headers;
import com.b2international.snowowl.snomed.importer.net4j.ImportConfiguration;
import com.b2international.snowowl.snomed.importer.net4j.SnomedValidationDefect;
import com.b2international.snowowl.snomed.importer.rf2.validation.AbstractSnomedValidator;
import com.b2international.snowowl.snomed.importer.rf2.validation.SnomedAssociationRefSetValidator;
import com.b2international.snowowl.snomed.importer.rf2.validation.SnomedAttributeValueRefSetValidator;
import com.b2international.snowowl.snomed.importer.rf2.validation.SnomedComplexMapTypeRefSetValidator;
import com.b2international.snowowl.snomed.importer.rf2.validation.SnomedConceptValidator;
import com.b2international.snowowl.snomed.importer.rf2.validation.SnomedConcreteDataTypeRefSetValidator;
import com.b2international.snowowl.snomed.importer.rf2.validation.SnomedDescriptionTypeRefSetValidator;
import com.b2international.snowowl.snomed.importer.rf2.validation.SnomedDescriptionValidator;
import com.b2international.snowowl.snomed.importer.rf2.validation.SnomedLanguageRefSetValidator;
import com.b2international.snowowl.snomed.importer.rf2.validation.SnomedModuleDependencyRefSetValidator;
import com.b2international.snowowl.snomed.importer.rf2.validation.SnomedRelationshipValidator;
import com.b2international.snowowl.snomed.importer.rf2.validation.SnomedSimpleMapTypeRefSetValidator;
import com.b2international.snowowl.snomed.importer.rf2.validation.SnomedSimpleTypeRefSetValidator;
import com.google.common.base.Predicates;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.google.common.io.Closeables;

/**
 * Provides utility methods for validating the release files. 
 * 
 */
public class ValidationUtil {
	
	private final Set<SnomedValidationDefect> defects;
	private final ImportConfiguration configuration;
	private final List<AbstractSnomedValidator> releaseFileValidators;
	private final Set<String> activeConceptIds;
	private final Set<String> inactiveConceptIds;
	private final Set<String> descriptionIds;
	private final Set<String> relationshipIds;
	private final String requestingUserId;
	private final IBranchPath branchPath;
	private final Logger logger;
	
	public ValidationUtil(String requestingUserId, final ImportConfiguration configuration, Logger logger) {
		this.logger = logger;
		this.requestingUserId = requestingUserId;
		this.configuration = configuration;
		branchPath = BranchPathUtils.createPath(configuration.getBranchPath());
		defects = Sets.newHashSet();
		releaseFileValidators = Lists.newArrayList();
		
		try {
			addReleaseFilesForValidating();
			addRefSetFilesForValidating();
		} catch (final IOException e) {
			throw new ImportException("Exception caught while collecting release files for validation.", e);
		}
		
		activeConceptIds = Sets.newHashSet();
		inactiveConceptIds = Sets.newHashSet();
		descriptionIds = Sets.newHashSet();
		relationshipIds = Sets.newHashSet();
	}
	
	public void preValidate(final SubMonitor monitor) {
		for (final AbstractSnomedValidator releaseFileValidator : releaseFileValidators) {
			releaseFileValidator.preValidate(monitor);
		}
	}

	public void doValidate(final SubMonitor monitor) {
		for (final AbstractSnomedValidator releaseFileValidator : releaseFileValidators) {
			LogUtils.logImportActivity(logger, requestingUserId, branchPath, "Validating '" + releaseFileValidator.getReleaseFileName() + "' release file.");
			releaseFileValidator.doValidate(monitor);
		}
	}
	
	public void postValidate(final SubMonitor monitor) {
		for (final AbstractSnomedValidator releaseFileValidator : releaseFileValidators) {
			releaseFileValidator.postValidate(monitor);
		}
	}
	
	public Set<SnomedValidationDefect> getDefects() {
		return defects;
	}
	
	private void addReleaseFilesForValidating() throws IOException {
		if (isValidReleaseFile(configuration.getConceptsFile())) {
			releaseFileValidators.add(new SnomedConceptValidator(configuration, configuration.getConceptsFile(), defects, this));
		}

		if (isValidReleaseFile(configuration.getDescriptionsFile())) {
			releaseFileValidators.add(new SnomedDescriptionValidator(configuration, configuration.getDescriptionsFile(), defects, this));
		}

		if (isValidReleaseFile(configuration.getTextDefinitionFile())) {
			releaseFileValidators.add(new SnomedDescriptionValidator(configuration, configuration.getTextDefinitionFile(), defects, this));
		}

		if (isValidReleaseFile(configuration.getRelationshipsFile())) {
			releaseFileValidators.add(new SnomedRelationshipValidator(configuration, configuration.getRelationshipsFile(), defects, this));
		}
		
		if (isValidReleaseFile(configuration.getStatedRelationshipsFile())) {
			releaseFileValidators.add(new SnomedRelationshipValidator(configuration, configuration.getStatedRelationshipsFile(), defects, this));
		}
	}
	
	private void addRefSetFilesForValidating() throws IOException {
		
		for (final URL url : configuration.getRefSetUrls()) {
			addRefSetFile(url);
		}

		//if the reference file URL set does not contain a language validator yet, specify one 
		if (!Iterables.any(releaseFileValidators, Predicates.instanceOf(SnomedLanguageRefSetValidator.class))) {
			
			if (isValidReleaseFile(configuration.getLanguageRefSetFile())) {
				releaseFileValidators.add(new SnomedLanguageRefSetValidator(configuration, configuration.toURL(configuration.getLanguageRefSetFile()), defects, this));
			}
			
		}
	}
	
	public boolean isValidReleaseFile(final File releaseFile) {
		return null != releaseFile && !releaseFile.getPath().isEmpty();
	}

	private void addRefSetFile(final URL url) throws IOException {
		BufferedReader reader = null;
		String lastColumn;
		
		try {
			reader = new BufferedReader(new InputStreamReader(url.openStream()));
			final String header = reader.readLine();
			
			//guard against invalid files/folders in the SCT RF2 archive/root folder
			if (StringUtils.isEmpty(header)) {
				return;
			}
			
			lastColumn = header.substring(header.lastIndexOf("\t") + 1);
			
			if (lastColumn.equalsIgnoreCase(SnomedRf2Headers.FIELD_REFERENCED_COMPONENT_ID)) {
				releaseFileValidators.add(new SnomedSimpleTypeRefSetValidator(configuration, url, defects, this));
			} else if (lastColumn.equalsIgnoreCase(SnomedRf2Headers.FIELD_VALUE_ID)) {
				releaseFileValidators.add(new SnomedAttributeValueRefSetValidator(configuration, url, defects, this));
			} else if (lastColumn.equalsIgnoreCase(SnomedRf2Headers.FIELD_CHARACTERISTIC_TYPE_ID))  {
				releaseFileValidators.add(new SnomedConcreteDataTypeRefSetValidator(configuration, url, defects, this, true));
			} else if (lastColumn.equalsIgnoreCase(SnomedRf2Headers.FIELD_VALUE)) { // AU CDT refset
				releaseFileValidators.add(new SnomedConcreteDataTypeRefSetValidator(configuration, url, defects, this, false));
			} else if (lastColumn.equalsIgnoreCase(SnomedRf2Headers.FIELD_MAP_TARGET)) {
				releaseFileValidators.add(new SnomedSimpleMapTypeRefSetValidator(configuration, url, defects, this, false));
			} else if (lastColumn.equalsIgnoreCase(SnomedRf2Headers.FIELD_CORRELATION_ID)) {
				releaseFileValidators.add(new SnomedComplexMapTypeRefSetValidator(configuration, url, defects, this));
			} else if (lastColumn.equalsIgnoreCase(SnomedRf2Headers.FIELD_DESCRIPTION_LENGTH)) {
				releaseFileValidators.add(new SnomedDescriptionTypeRefSetValidator(configuration, url, defects, this));
			} else if (lastColumn.equalsIgnoreCase(SnomedRf2Headers.FIELD_TARGET_COMPONENT) || lastColumn.equalsIgnoreCase(SnomedRf2Headers.FIELD_TARGET_COMPONENT_ID)) {
				releaseFileValidators.add(new SnomedAssociationRefSetValidator(configuration, url, defects, this));
			} else if (lastColumn.equalsIgnoreCase(SnomedRf2Headers.FIELD_ACCEPTABILITY_ID)) {
				releaseFileValidators.add(new SnomedLanguageRefSetValidator(configuration, url, defects, this));
			} else if (lastColumn.equalsIgnoreCase(SnomedRf2Headers.FIELD_TARGET_EFFECTIVE_TIME)) {
				releaseFileValidators.add(new SnomedModuleDependencyRefSetValidator(configuration, url, defects, this));
			} else if (lastColumn.equals(SnomedRf2Headers.FIELD_MAP_CATEGORY_ID)) {
				releaseFileValidators.add(new SnomedExtendedMapTypeRefSetValidator(configuration, url, defects, this));
			} else if (lastColumn.equalsIgnoreCase(SnomedRf2Headers.FIELD_MAP_TARGET_DESCRIPTION)) {
				releaseFileValidators.add(new SnomedSimpleMapTypeRefSetValidator(configuration, url, defects, this, true));	
			} else {
				logger.warn("Couldn't determine reference set type for file '" + configuration.getMappedName(url.getPath()) + "', not validating.");
			}
				
		} finally {
			Closeables.closeQuietly(reader);
		}
		
	}

	public Set<String> getActiveConceptIds() {
		return activeConceptIds;
	}

	public Set<String> getInactiveConceptIds() {
		return inactiveConceptIds;
	}

	public Set<String> getDescriptionIds() {
		return descriptionIds;
	}

	public Set<String> getRelationshipIds() {
		return relationshipIds;
	}

	public org.slf4j.Logger getLogger() {
		return logger;
	}

}