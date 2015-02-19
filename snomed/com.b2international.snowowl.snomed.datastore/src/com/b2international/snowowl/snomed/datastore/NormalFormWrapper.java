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
package com.b2international.snowowl.snomed.datastore;

import java.util.Collection;
import java.util.Map;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;

/**
 * Simple POJO for representing a SNOMED&nbsp;CT concept as a bunch of parent concept IDs and relationships based on an SCG normal form expression. 
 */
public final class NormalFormWrapper {

	private final Collection<String> parentConceptIds;
	private final Collection<AttributeConceptGroupWrapper> attributeConceptGroups;

	/**
	 * Creates an instance of the SCG normal form representation.
	 * @param parentConceptIds the parent concept IDs.
	 * @param attributeConceptGroups the concept attribute groups.
	 */
	public NormalFormWrapper(final Collection<String> parentConceptIds, final Collection<AttributeConceptGroupWrapper> attributeConceptGroups) {
		this.parentConceptIds = parentConceptIds;
		this.attributeConceptGroups = attributeConceptGroups;
	}
	
	/**
	 * Collection of attribute concept representations.
	 * @return
	 */
	public Collection<AttributeConceptGroupWrapper> getAttributeConceptGroups() {
		return ImmutableSet.<AttributeConceptGroupWrapper>copyOf(attributeConceptGroups);
	}
	
	/**
	 * Returns with a copy of the parent concept IDs.
	 * @return the parent concept IDs.
	 */
	public Collection<String> getParentConceptIds() {
		return ImmutableSet.<String>copyOf(parentConceptIds);
	}
	
	/**
	 * POJO representation of the SCG attribute group concepts. 
	 */
	public static final class AttributeConceptGroupWrapper {
		
		private final Map<String, String> attributeConceptIds;
		private final int group;

		/**
		 * Creates an SCG attribute group representation. 
		 * @param attributeConceptIds the attribute concept IDs.
		 * @param group the relationship group.
		 */
		public AttributeConceptGroupWrapper(final Map<String, String> attributeConceptIds, final int group) {
			this.attributeConceptIds = attributeConceptIds;
			this.group = group;
		}

		/**
		 * Returns with a map of attribute concept IDs. Keys are the relationship type concept IDs. Values are the destination concept IDs.
		 * @return a map of attribute concept IDs.
		 */
		public Map<String, String> getAttributeConceptIds() {
			return ImmutableMap.<String, String>copyOf(attributeConceptIds);
		}
		
		/**
		 * Returns with the relationship group.
		 * @return relationship group.
		 */
		public int getGroup() {
			return group;
		}
		
	}
	
}