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
package com.b2international.snowowl.snomed.api.domain;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.collect.Iterables.find;
import static com.google.common.collect.Lists.newArrayList;

import javax.annotation.Nullable;

import com.b2international.snowowl.snomed.SnomedConstants.Concepts;
import com.google.common.base.Predicate;

/**
 * Enumeration to specify the reason of the inactivation of a SNOMED&nbsp;CT component.
 * @see ISnomedDescription#getDescriptionInactivationIndicator()
 *
 */
public enum DescriptionInactivationIndicator {

	/**
	 * The description has been marked as inactive because it is a duplication of another description.
	 */
	DUPLICATE(Concepts.DUPLICATE),
	
	/**
	 * The description is still active but it refers to an inactive concept.
	 */
	CONCEPT_NON_CURRENT(Concepts.CONCEPT_NON_CURRENT),
	
	/**
	 * Description has been marked as inactive because the associated term does not describe the 
	 * referred concept. 
	 */
	INAPPROPRIATE(Concepts.INAPPROPRIATE),
	
	/**
	 * The description has been inactivated because it has been moved to another namespace. 
	 */
	MOVED_ELSEWHERE(Concepts.MOVED_ELSEWHERE),
	
	/**
	 * Although the description is still active but it is in the process of being moved into
	 * another namespace and when the move is completed it will be marked as inactive.
	 */
	PENDING_MOVE(Concepts.PENDING_MOVE),
	
	/**
	 * Description referring onto a limited concept.
	 * <br>Description must have inactive status as of {@code 2010-01-31}. 
	 */
	LIMITED(Concepts.LIMITED),
	
	/**
	 * Inactive description containing any kind of error.
	 */
	ERRONEOUS(Concepts.ERRONEOUS),
	
	/**
	 * The description has been made inactive because it is an outdated name or spelling that is no longer used
	 * and/or valid.
	 */
	OUTDATED(Concepts.OUTDATED);
	
	private String valueId;

	private DescriptionInactivationIndicator(final String valueId) {
		this.valueId = checkNotNull(valueId, "valueId");
	}
	
	/**
	 * Returns with unique identifier of the concept that represents the inactivation reason of
	 * the SNOMED&nbsp;CT description. 
	 * @return the valueId the value ID of the inactivation reason.
	 */
	public String getValueId() {
		return valueId;
	}
	
	/**
	 * Returns with the description inactivation indication for the given value ID. May return with {@code null}
	 * if the description inactivation reason cannot be found for the value argument.
	 * @param valueId the value argument.
	 * @return the description inactivation indication or {@code null} if cannot be found.
	 */
	@Nullable public static DescriptionInactivationIndicator getInactivationIndicatorByValueId(@Nullable final String valueId) {
		return null == valueId ? null : find(newArrayList(DescriptionInactivationIndicator.values()), 
				new Predicate<DescriptionInactivationIndicator>() {
					public boolean apply(final DescriptionInactivationIndicator indicator) {
						return checkNotNull(indicator, "indicator").valueId.equals(valueId);
					}
		}, null);
	}
	
}