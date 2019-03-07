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

import static com.google.common.collect.Sets.newHashSet;

import java.net.URL;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import org.eclipse.core.runtime.IProgressMonitor;

import com.b2international.snowowl.core.date.EffectiveTimes;
import com.b2international.snowowl.snomed.common.SnomedTerminologyComponentConstants;
import com.b2international.snowowl.snomed.importer.net4j.DefectType;
import com.b2international.snowowl.snomed.importer.net4j.ImportConfiguration;
import com.b2international.snowowl.snomed.importer.release.ReleaseFileSet.ReleaseComponentType;
import com.b2international.snowowl.snomed.importer.rf2.model.ComponentImportType;
import com.google.common.base.Strings;

/**
 * Represents a release file validator that validates the reference set.
 * 
 */
public abstract class SnomedRefSetValidator extends AbstractSnomedValidator {

	private static final int COLUMN_UUID = 0;
	private static final int COLUMN_EFFECTIVE_TIME = 1;
	private static final int COLUMN_REFERENCED_COMPONENT_ID = 5;
	
	private Set<String> uuidNotUnique = newHashSet();
	private Set<String> uuidInvalid = newHashSet();
	private Set<String> referencedComponentNotExist = newHashSet();
	private Set<String> uuids = newHashSet();
	
	private static final String EMPTY_FIELD_VALIDATION_MESSAGE = "%s reference set member with uuid '%s' has an empty field '%s' in effective time '%s'";
	
	public SnomedRefSetValidator(final ImportConfiguration configuration, final URL releaseUrl, final ComponentImportType importType,
			final SnomedValidationContext validationUtil, final String[] expectedHeader) {
		super(configuration, releaseUrl, importType, validationUtil, expectedHeader);
	}
	
	/**
	 * Gets the release type specific name.
	 * 
	 * @return
	 */
	protected abstract String getName();
	
	@Override
	protected void doValidate(final List<String> row) {
		validateIdUniqueness(row);
		validateReferencedComponent(row);
	}

	protected void validateIdUniqueness(final List<String> row) {
		
		final String uuid = row.get(COLUMN_UUID);
		
		try {
			UUID.fromString(uuid);
		} catch (final IllegalArgumentException e) {
			uuidInvalid.add(String.format("Invalid UUID '%s' in effective time '%s' in file '%s'", uuid, row.get(1), releaseFileName));
			return;
		}
		
		if (!uuids.add(String.format("%s|%s", uuid, row.get(COLUMN_EFFECTIVE_TIME)))) {
			uuidNotUnique.add(String.format("UUID '%s' and effective time '%s' is not unique in file '%s'", uuid, row.get(1), releaseFileName));
		}
	}

	/**
	 * Checks if the reference component is exist or not.
	 * 
	 * @param row the current row
	 * @param lineNumber the current line number
	 */
	private void validateReferencedComponent(final List<String> row) {
		validateReferencedComponent(row, COLUMN_REFERENCED_COMPONENT_ID);
	}

	protected void validateReferencedComponent(final List<String> row, int indexInRow) {
		final String componentId = row.get(indexInRow);
		if (!isComponentExists(componentId, getComponentType(componentId))) {
			// skip missing lang refset referenced components (FIXME support members with missing refcomps)
			if (this instanceof SnomedLanguageRefSetValidator) {
				return;
			}
			
			final String uuid = row.get(0);
			final String effectiveTime = getSafeEffectiveTime(row.get(1));
			
			referencedComponentNotExist.add(getMissingComponentMessage(uuid, effectiveTime, componentId));
		}
	}

	protected String getMissingComponentMessage(final String uuid, final String effectiveTime, final String componentId) {
		return getMissingComponentMessage(uuid, effectiveTime, "component", componentId); 
	}
	
	protected String getMissingComponentMessage(final String uuid, final String effectiveTime, final String type, final String componentId) {
		return String.format("Reference set member '%s' references non-existent %s '%s' in effective time '%s'", uuid, type, componentId, effectiveTime);
	}

	@Override
	protected void doValidate(String effectiveTime, IProgressMonitor monitor) {
		super.doValidate(effectiveTime, monitor);
		addDefect(DefectType.NOT_UNIQUE_REFSET_MEMBER_ID, uuidNotUnique);
		addDefect(DefectType.INCORRECT_REFSET_MEMBER_ID, uuidInvalid);
		addDefect(DefectType.REFSET_MEMBER_COMPONENT_NOT_EXIST, referencedComponentNotExist);
	}

	protected String getSafeEffectiveTime(String effectiveTime) {
		return Strings.isNullOrEmpty(effectiveTime) ? EffectiveTimes.UNSET_EFFECTIVE_TIME_LABEL : effectiveTime;
	}
	
	protected void validateNotEmptyFieldValue(String fieldValue, String fieldName, List<String> row, List<String> results) {
		if (Strings.isNullOrEmpty(fieldValue)) {
			final String uuid = row.get(0);
			final String safeEffectiveTime = getSafeEffectiveTime(row.get(1));
			results.add(String.format(EMPTY_FIELD_VALIDATION_MESSAGE, getName(), uuid, fieldName, safeEffectiveTime));
		}
	}
	
	/**returns with the proper import component type based on the component ID argument.*/
	private ReleaseComponentType getComponentType(final String componentId) {
		final short value = SnomedTerminologyComponentConstants.getTerminologyComponentIdValueSafe(componentId);
		switch (value) {
			case SnomedTerminologyComponentConstants.DESCRIPTION_NUMBER: return ReleaseComponentType.DESCRIPTION;
			case SnomedTerminologyComponentConstants.RELATIONSHIP_NUMBER: return ReleaseComponentType.RELATIONSHIP;
			default : return ReleaseComponentType.CONCEPT;
		}
	}
	
	@Override
	protected void clearCaches() {
		uuids = newHashSet();
		uuidNotUnique = newHashSet();
		uuidInvalid = newHashSet();
		referencedComponentNotExist = newHashSet();
	}
	
}