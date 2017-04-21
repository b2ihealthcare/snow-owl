/*
 * Copyright 2011-2016 B2i Healthcare Pte Ltd, http://b2i.sg
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
package com.b2international.snowowl.snomed.datastore;

import static com.google.common.base.Preconditions.checkNotNull;

import java.util.Iterator;

import com.b2international.commons.StringUtils;
import com.b2international.snowowl.snomed.datastore.snor.SnomedConstraintDocument;
import com.b2international.snowowl.snomed.datastore.snor.SnomedConstraintDocument.PredicateType;
import com.google.common.base.Predicate;
import com.google.common.collect.Iterables;

/**
 * Collection of utility methods related to data types.
 * 
 */
abstract public class DataTypeUtils {
	
	public static final String BIG_DECIMAL_EDIT_PATTERN = "#,###,###,###,###,###.#########";
	public static final String INTEGER_EDIT_PATTERN = "###,###,###.";
	
	/**
	 * Returns with the human readable name of the concrete domain type concept attribute predicate identified by the specified unique name. 
	 * @param dataTypeName the unique camel-case name of the concrete domain type predicate mini.
	 * @param predicates the predicates applicable for the concept
	 * @return the human readable name of the concrete domain type predicate.
	 */
	public static String getDataTypePredicateLabel(final String dataTypeName, final Iterable<SnomedConstraintDocument> predicates) {
		
		checkNotNull(dataTypeName, "Data type name argument cannot be null.");
		
		final Iterable<SnomedConstraintDocument> applicablePredicates = Iterables.filter(predicates, new Predicate<SnomedConstraintDocument>() {
			@Override public boolean apply(final SnomedConstraintDocument predicateMini) {
				return PredicateType.DATATYPE.equals(predicateMini.getPredicateType()) && dataTypeName.equals(predicateMini.getDataTypeName());
			}
		});
		
		final Iterator<SnomedConstraintDocument> applicablePredicatesIterator = applicablePredicates.iterator();
		
		if (!applicablePredicatesIterator.hasNext()) {
			return getDefaultDataTypeLabel(dataTypeName);
		} else {
			return applicablePredicatesIterator.next().getDataTypeLabel();
		}
	}

	/**
	 * Returns with the human readable name of the concrete domain type concept attribute predicate identified by the specified unique name. 
	 * @param dataTypeName the unique camel-case name of the concrete domain type predicate mini.
	 * @return the human readable name of the concrete domain type predicate.
	 */
	public static String getDefaultDataTypeLabel(final String dataTypeName) {
		
		return StringUtils.isEmpty(dataTypeName) 
				? StringUtils.EMPTY_STRING
				: StringUtils.capitalizeFirstLetter(StringUtils.splitCamelCase(dataTypeName.replaceFirst("canBeTaggedWith|isa|is|does|has", "")).toLowerCase());
	}
	
	private DataTypeUtils() {} 
}