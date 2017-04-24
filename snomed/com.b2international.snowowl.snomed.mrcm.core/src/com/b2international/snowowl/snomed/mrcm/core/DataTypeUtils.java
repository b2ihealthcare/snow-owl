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
package com.b2international.snowowl.snomed.mrcm.core;

import static com.google.common.base.Preconditions.checkNotNull;

import java.util.Collection;
import java.util.List;

import com.b2international.commons.CompareUtils;
import com.b2international.commons.StringUtils;
import com.b2international.snowowl.snomed.datastore.snor.SnomedConstraintDocument;
import com.b2international.snowowl.snomed.datastore.snor.SnomedConstraintDocument.PredicateType;
import com.google.common.base.Predicate;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;

/**
 * Collection of utility methods related to data types.
 * 
 */
abstract public class DataTypeUtils {
	
	/**
	 * Returns with the human readable name of the concrete domain type concept attribute predicate identified by the specified unique name. 
	 * @param dataTypeName the unique camel-case name of the concrete domain type predicate mini.
	 * @param conceptId the unique ID of the concept. 
	 * @param predicates the predicates applicable for the concept
	 * @return the human readable name of the concrete domain type predicate.
	 */
	public static String getDataTypePredicateLabel(final String dataTypeName, String conceptId, Collection<SnomedConstraintDocument> predicates) {
		return getDataTypePredicateLabel(dataTypeName, predicates);
	}
	
	/**
	 * Returns with the human readable name of the concrete domain type concept attribute predicate identified by the specified unique name. 
	 * @param dataTypeName the unique camel-case name of the concrete domain type predicate mini.
	 * @return the human readable name of the concrete domain type predicate.
	 */
	public static String getDataTypePredicateLabel(final String dataTypeName) {
		return getDefaultDataTypeLabel(dataTypeName);
	}
	
	private static String getDataTypePredicateLabel(final String dataTypeName, Iterable<SnomedConstraintDocument> predicates2) {
		final List<SnomedConstraintDocument> predicates = Lists.newArrayList(Iterables.filter(predicates2, new Predicate<SnomedConstraintDocument>() {
			@Override public boolean apply(final SnomedConstraintDocument predicateMini) {
				return PredicateType.DATATYPE.equals(predicateMini.getPredicateType()) && checkNotNull(dataTypeName, "Data type name argument cannot be null.").equals(predicateMini.getDataTypeName());
			}
		}));
		if (CompareUtils.isEmpty(predicates)) {
			return getDefaultDataTypeLabel(dataTypeName);
		} else {
			return predicates.get(0).getDataTypeLabel();
		}
	}

	private static String getDefaultDataTypeLabel(final String dataTypeName) {
		return StringUtils.isEmpty(dataTypeName) ? "" : StringUtils.capitalizeFirstLetter(StringUtils.splitCamelCase(dataTypeName.replaceFirst("canBeTaggedWith|isa|is|does|has", "")).toLowerCase());
	}
	
	private DataTypeUtils() {} 
}