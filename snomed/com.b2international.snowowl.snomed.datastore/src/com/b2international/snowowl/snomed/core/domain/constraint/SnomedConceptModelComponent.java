/*
 * Copyright 2018 B2i Healthcare Pte Ltd, http://b2i.sg
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
package com.b2international.snowowl.snomed.core.domain.constraint;

import java.util.Collection;
import java.util.Date;
import java.util.Locale;
import java.util.Objects;

import com.b2international.commons.StringUtils;
import com.b2international.snowowl.core.domain.BaseComponent;
import com.b2international.snowowl.snomed.common.SnomedTerminologyComponentConstants;
import com.google.common.base.Strings;

/**
 * Captures properties required for change tracking on individual components of the MRCM concept model.
 * 
 * @since 6.5
 */
public abstract class SnomedConceptModelComponent extends BaseComponent {

	public static final String PROP_ID = "id";
	public static final String PROP_AUTHOR = "author";
	public static final String PROP_ACTIVE = "active";
	public static final String PROP_EFFECTIVE_TIME = "effectiveTime";

	private boolean active;
	private long effectiveTime;
	private String author;

	public boolean isActive() {
		return active;
	}

	public void setActive(final boolean active) {
		this.active = active;
	}

	public long getEffectiveTime() {
		return effectiveTime;
	}

	public void setEffectiveTime(final long effectiveTime) {
		this.effectiveTime = effectiveTime;
	}

	public String getAuthor() {
		return author;
	}

	public void setAuthor(final String author) {
		this.author = author;
	}

	@Override
	public short getTerminologyComponentId() {
		// XXX: Returning the same component type ID for all parts
		return SnomedTerminologyComponentConstants.CONSTRAINT_NUMBER;
	}

	/**
	 * Creates a deep copy of the given model component object hierarchy, updating
	 * the effective time and the author on each copy.
	 * 
	 * @param date
	 * @param userName
	 * @return
	 */
	public abstract SnomedConceptModelComponent deepCopy(Date date, String userName);

	/**
	 * Adds all referenced SNOMED CT concept identifiers to the specified set.
	 * 
	 * @param conceptIds
	 */
	public abstract void collectConceptIds(Collection<String> conceptIds);

	/**
	 * Checks whether the concept model component is fully populated with valid input.
	 * 
	 * @return an error message, or <code>null</code> if no issues could be found
	 */
	public String validate() {
		if (Strings.isNullOrEmpty(getAuthor())) {
			return String.format("Author should not be empty on %s with UUID %s.", displayName(), getId());
		}

		return null;
	}

	protected final String displayName() {
		final String className = getClass().getSimpleName(); // eg. "SnomedCardinalityPredicate"
		final String withoutPrefix = className.substring("Snomed".length()); // eg. "CardinalityPredicate"
		return StringUtils.splitCamelCase(withoutPrefix).toLowerCase(Locale.ENGLISH); // eg. "cardinality predicate"
	}

	public int structuralHashCode() {
		return structuralHashCode(active, author, effectiveTime);
	}

	public boolean structurallyEquals(final SnomedConceptModelComponent obj) {
		if (this == obj) { return true; }
		if (obj == null) { return false; }
		if (getClass() != obj.getClass()) { return false; }

		final SnomedConceptModelComponent other = (SnomedConceptModelComponent) obj;

		if (active != other.active) { return false; }
		if (effectiveTime != other.effectiveTime) { return false; }
		if (!Objects.equals(author, other.author)) { return false; }
		return true;
	}
	
	public static boolean structurallyEquals(final SnomedConceptModelComponent left, final SnomedConceptModelComponent right) {
		if (left == right) { return true; }
		if (left == null) { return false; }
		return left.structurallyEquals(right);
	}
	
	public static int structuralHashCode(Object... items) {
		int result = 1;
		
		for (Object item : items) {
			result = 31 * result;
			if (item instanceof SnomedConceptModelComponent) {
				result += ((SnomedConceptModelComponent) item).structuralHashCode();
			} else if (item != null) {
				result += item.hashCode();
			}
		}
		
		return result;
	}
}
