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
package com.b2international.snowowl.snomed.mrcm.core.validator.util;

import com.b2international.snowowl.snomed.mrcm.core.widget.bean.ConceptWidgetBean;
import com.b2international.snowowl.snomed.mrcm.core.widget.bean.DataTypeWidgetBean;
import com.b2international.snowowl.snomed.mrcm.core.widget.bean.DescriptionWidgetBean;
import com.b2international.snowowl.snomed.mrcm.core.widget.bean.RelationshipWidgetBean;
import com.google.common.base.Predicate;
import com.google.common.collect.Iterables;

/**
 */
public class ConceptWidgetBeanUtil {

	/**
	 * Returns all {@link RelationshipWidgetBean} within the given {@link ConceptWidgetBean} instance for the given relationship type ID.
	 * 
	 * @param concept
	 * @return
	 */
	public static Iterable<RelationshipWidgetBean> getRelationships(final ConceptWidgetBean concept, final String typeId) {
		return Iterables.filter(concept.getRelationships(), new Predicate<RelationshipWidgetBean>() {
			@Override
			public boolean apply(RelationshipWidgetBean input) {
				return input.isTypeMatches(typeId);
			}
		});
	}

	/**
	 * Returns the fully specified name bean of the given {@link ConceptWidgetBean}. Or <code>null</code> if the concept does not have fully specified name.
	 * 
	 * @param concept
	 * @return
	 */
	public static DescriptionWidgetBean getFullySpecifiedNameBean(ConceptWidgetBean concept) {
		for (DescriptionWidgetBean description : concept.getDescriptionBeans()) {
			if (description.isFsn()) {
				return description;
			}
		}
		return null;
	}

	public static DescriptionWidgetBean getPreferredTerm(ConceptWidgetBean concept) {
		for (DescriptionWidgetBean description : Iterables.filter(concept.getDescriptions().getElements(), DescriptionWidgetBean.class)) {
			if(description.isPreferred()){
				return description;
			}
		}
		return null;
	}

	/**
	 * Returns the fully specified name of the given {@link ConceptWidgetBean}. Or <code>null</code> if the concept does not have fully specified name.
	 * 
	 * @param concept
	 * @return
	 */
	public static String getFullySpecifiedName(ConceptWidgetBean concept) {
		DescriptionWidgetBean description = getFullySpecifiedNameBean(concept);
		return description != null ? description.getTerm() : null;
	}

	/**
	 * Returns the {@link DescriptionWidgetBean} from the given {@link ConceptWidgetBean} with the given typeId.
	 * 
	 * @param concept
	 * @param typeId
	 */
	public static DescriptionWidgetBean getDescription(ConceptWidgetBean concept, String typeId) {
		for (DescriptionWidgetBean description : Iterables.filter(concept.getDescriptions().getElements(),
				DescriptionWidgetBean.class)) {
			if (typeId.equals(description.getSelectedType().getId())) {
				return description;
			}
		}
		return null;
	}

	/**
	 * Returns the {@link DataTypeWidgetBean} from the given {@link ConceptWidgetBean} with the given propertyName.
	 * 
	 * @param concept
	 * @param propertyName
	 * @return
	 */
	public static DataTypeWidgetBean getDataType(ConceptWidgetBean concept, String propertyName) {
		for (DataTypeWidgetBean dataType : concept.getDataTypes()) {
			if (propertyName.equals(dataType.getSelectedLabel())) {
				return dataType;
			}
		}
		return null;
	}

	/**
	 * Returns the {@link RelationshipWidgetBean} with the given typeId from the given {@link ConceptWidgetBean}.
	 * 
	 * @param concept
	 * @param relationshipId
	 * @return
	 */
	public static RelationshipWidgetBean getRelationship(ConceptWidgetBean concept, String relationshipId) {
		for (RelationshipWidgetBean relationship : concept.getRelationships()) {
			if (relationshipId.equals(relationship.getSelectedType().getId())) {
				return relationship;
			}
		}
		return null;
	}
}