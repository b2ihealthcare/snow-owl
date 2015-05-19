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
package com.b2international.snowowl.snomed.mrcm.core.widget.model;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

import java.text.MessageFormat;
import java.util.List;

import com.b2international.commons.StringUtils;
import com.b2international.snowowl.snomed.mrcm.DataType;
import com.google.common.collect.Iterables;

/**
 * A widget model representing relationship groups. Different sets of relationship widget models can be served based on
 * grouping.
 * 
 */
public class RelationshipGroupWidgetModel extends ContainerWidgetModel {

	private static final long serialVersionUID = -1053607091740717775L;

	public enum GroupFlag {
		GROUPED, UNGROUPED;
	}
	
	/**
	 * Default constructor for serialization.
	 */
	protected RelationshipGroupWidgetModel() {
	}
	
	/**
	 * Creates a relationship group model for ungrouped relationships.
	 * 
	 * @param children the child relationship widget models (may not be {@code null}; elements may not be {@code null})
	 * @return the created group model instance
	 */
	public static RelationshipGroupWidgetModel createUngroupedModel(final List<RelationshipWidgetModel> children) {
		return new RelationshipGroupWidgetModel("Ungrouped Properties", children, GroupFlag.UNGROUPED);
	}
	
	/**
	 * Creates a relationship group model for regular relationship groups.
	 * 
	 * @param children the child relationship widget models (may not be {@code null}; elements may not be {@code null})
	 * @return the created group model instance
	 */
	public static RelationshipGroupWidgetModel createGroupedModel(final List<? extends WidgetModel> children) {
		return new RelationshipGroupWidgetModel("Property group %d", children, GroupFlag.GROUPED);
	}
	
	private GroupFlag groupFlag;
	private boolean concreteDomainSupported;
	
	private RelationshipGroupWidgetModel(final String label, final List<? extends WidgetModel> models, final GroupFlag groupFlag) {
		
		super(label, models);
		this.groupFlag = checkNotNull(groupFlag, "groupFlag");
	}
	
	public boolean isConcreteDomainSupported() {
		return isUngrouped() && concreteDomainSupported;
	}
	
	public void setConcreteDomainSupported(boolean concreteDomainSupported) {
		this.concreteDomainSupported = concreteDomainSupported;
	}
	
	public boolean isUngrouped() {
		return GroupFlag.UNGROUPED.equals(groupFlag);
	}

	/**
	 * Checks if the specified group flag matches this group model's corresponding flag.
	 * 
	 * @param otherGroupFlag the group flag to check
	 * @return {@code true} on a positive match, {@code false} otherwise
	 */
	public boolean matches(final GroupFlag otherGroupFlag) {
		return groupFlag.equals(otherGroupFlag);
	}
	
	/**
	 * Returns the first matching relationship group widget model for the specified identifiers.
	 * 
	 * @param typeId the type identifier to match (may not be {@code null} or empty)
	 * @param valueId the value identifier to match (may not be {@code null} or empty)
	 * @param characteristicTypeId the characteristic type identifier to match (may not be {@code null} or empty)
	 * @return the first matching relationship widget model which accepts all three arguments
	 * @throws IllegalStateException if no matching relationship model can be found
	 */
	public RelationshipWidgetModel getFirstMatching(final String typeId, final String valueId, final String characteristicTypeId) {
		
		checkArgument(!StringUtils.isEmpty(typeId), "typeId is null or empty.");
		checkArgument(!StringUtils.isEmpty(valueId), "valueId is null or empty.");
		checkArgument(!StringUtils.isEmpty(characteristicTypeId), "characteristicTypeId is null or empty.");
		
		for (final RelationshipWidgetModel candidate : Iterables.filter(getChildren(), RelationshipWidgetModel.class)) {
			if (candidate.matches(typeId, valueId, characteristicTypeId)) {
				return candidate;
			}
		}
		
		throw new IllegalStateException(MessageFormat.format(
				"Couldn''t find matching relationship model for type ''{0}'', value ''{1}'' and characteristic type ''{2}''.",
				typeId, valueId, characteristicTypeId));
	}
	
	public DataTypeWidgetModel getFirstMatching(final String label, final DataType dataType) {
		
		checkArgument(!StringUtils.isEmpty(label), "label is null or empty.");
		checkNotNull(dataType, "dataType is null.");
		
		for (final DataTypeWidgetModel candidate : Iterables.filter(getChildren(), DataTypeWidgetModel.class)) {
			if (candidate.matches(label, dataType)) {
				return candidate;
			}
		}
		
		throw new IllegalStateException(MessageFormat.format("Couldn''t find matching concrete domain model for type ''{0}''.", dataType));
	}

}