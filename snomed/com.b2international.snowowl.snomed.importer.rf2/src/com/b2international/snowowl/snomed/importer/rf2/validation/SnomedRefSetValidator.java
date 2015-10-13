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

import static com.google.common.collect.Sets.newHashSet;

import java.net.URL;
import java.text.MessageFormat;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import org.eclipse.core.runtime.IProgressMonitor;

import com.b2international.snowowl.snomed.common.SnomedTerminologyComponentConstants;
import com.b2international.snowowl.snomed.importer.net4j.DefectType;
import com.b2international.snowowl.snomed.importer.net4j.ImportConfiguration;
import com.b2international.snowowl.snomed.importer.release.ReleaseFileSet.ReleaseComponentType;
import com.b2international.snowowl.snomed.importer.rf2.model.ComponentImportType;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;

/**
 * Represents a release file validator that validates the reference set.
 * 
 */
public abstract class SnomedRefSetValidator extends AbstractSnomedValidator {
	
	private static class ReferencedComponentIdAndStatus {
		
		private final String referencedComponentId;
		private boolean active;

		public ReferencedComponentIdAndStatus(final String referencedComponentId, final boolean active) {
			this.referencedComponentId = referencedComponentId;
			this.active = active;
		}

		public String getReferencedComponentId() {
			return referencedComponentId;
		}
		
		public boolean isActive() {
			return active;
		}
		
		public void setActive(final boolean active) {
			this.active = active;
		}
	}

	private static final int COLUMN_UUID = 0;
	private static final int COLUMN_STATUS = 1;
	private static final int COLUMN_REFERENCED_COMPONENT_ID = 5;
	
	private final Set<String> uuidNotUnique = newHashSet();
	private final Set<String> uuidInvalid = newHashSet();
	private final Map<UUID, ReferencedComponentIdAndStatus> memberDataByUuid;
	private final Set<String> referencedComponentNotExist = Sets.newHashSet();

	public SnomedRefSetValidator(final ImportConfiguration configuration, final URL releaseUrl, final ComponentImportType importType, final SnomedValidationContext validationUtil, final String[] expectedHeader) {
		super(configuration, releaseUrl, importType, validationUtil, expectedHeader);
		memberDataByUuid = Maps.newHashMap();
	}
	
	/**
	 * Gets the release type specific name.
	 * 
	 * @return
	 */
	protected abstract String getName();
	
	@Override
	protected void doValidate(final List<String> row, final int lineNumber) {
		validateIdUniqueness(row, lineNumber);
		validateReferencedComponent(row, lineNumber);
	}

	/**
	 * Checks if the UUID is unique or not.
	 * 
	 * @param row the current row
	 * @param lineNumber the current line number
	 */
	protected void validateIdUniqueness(final List<String> row, final int lineNumber) {
		
		final String uuidString = row.get(COLUMN_UUID);
		final UUID rowUuid;
		
		try {
			rowUuid = UUID.fromString(uuidString);
		} catch (final IllegalArgumentException e) {
			addDefectDescription(uuidInvalid, lineNumber);
			return;
		}
		
		final String rowReferencedComponentId = row.get(COLUMN_REFERENCED_COMPONENT_ID);
		final boolean rowActive = "1".equals(row.get(COLUMN_STATUS));
		
		final ReferencedComponentIdAndStatus existingData = memberDataByUuid.get(rowUuid);
		
		if (null != existingData) {

			if (existingData.getReferencedComponentId().equals(rowReferencedComponentId)) {
				// if the id is for the same component as before, update the active flag
				existingData.setActive(rowActive);
			} else if (existingData.isActive()) { 
				// if it's for different component and the member referring to the previous component is still active, report it as an issue
				addDefectDescription(uuidNotUnique, lineNumber);
			}
			
		} else {
			memberDataByUuid.put(rowUuid, new ReferencedComponentIdAndStatus(rowReferencedComponentId, rowActive));
		}
	}

	/**
	 * Checks if the reference component is exist or not.
	 * 
	 * @param row the current row
	 * @param lineNumber the current line number
	 */
	private void validateReferencedComponent(final List<String> row, final int lineNumber) {
		final String componentId = row.get(COLUMN_REFERENCED_COMPONENT_ID);
		if (!isComponentExists(componentId, getComponentType(componentId))) {
			addDefectDescription(referencedComponentNotExist, lineNumber, row.get(5));
		}
	}

	@Override
	protected void doValidate(IProgressMonitor monitor) {
		super.doValidate(monitor);
		addDefect(DefectType.NOT_UNIQUE_REFSET_MEMBER_ID, uuidNotUnique);
		addDefect(DefectType.INCORRECT_REFSET_MEMBER_ID, uuidInvalid);
		addDefect(DefectType.REFSET_MEMBER_COMPONENT_NOT_EXIST, referencedComponentNotExist);
	}
	
	/**
	 * Add a reference set defect to the given set.
	 * 
	 * @param refsetDefects the set of the defects of a given defect type
	 * @param lineNumber the number of the line where the defect can be found
	 */
	protected void addDefectDescription(final Set<String> refsetDefects, final int lineNumber) {
		refsetDefects.add(MessageFormat.format("Line number {0} in the ''{1}'' {2} reference set file.", 
				lineNumber, releaseFileName, getName()));
	}
	
	/**
	 * Add a reference set defect to the given set.
	 * 
	 * @param refsetDefects the set of the defects of a given defect type
	 * @param lineNumber the number of the line where the defect can be found
	 * @param componentId the ID of the component
	 */
	protected void addDefectDescription(final Set<String> refsetDefects, final int lineNumber, final String componentId) {
		refsetDefects.add(MessageFormat.format("Line number {0} in the ''{1}'' {2} reference set file with component ID {3}.", 
				lineNumber, releaseFileName, getName(), componentId));
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
	
}