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
package com.b2international.snowowl.snomed.reasoner.classification.entry;

import java.io.Serializable;

import com.b2international.snowowl.snomed.reasoner.classification.entry.AbstractChangeEntry.Nature;

/**
 * Common interface for all change entries reported in a reasoner change set.
 */
public interface IChangeEntry extends Serializable {

	/**
	 * Returns the concept to be displayed in the source column, which is:
	 * <ul>
	 * <li>the relationship source concept for relationships;
	 * <li>the relationship source concept for relationship concrete domain members;
	 * <li>the referenced concept for concept concrete domain members.
	 * </ul>
	 * 
	 * @return the source concept
	 */
	ChangeConcept getSource();

	/**
	 * Returns the concept to be displayed in the type column, which is:
	 * <ul>
	 * <li>the relationship type concept for relationships;
	 * <li>the relationship type concept for relationship concrete domain members;
	 * <li>{@code null} for concept concrete domain members.
	 * </ul>
	 * 
	 * @return the type concept
	 */
	ChangeConcept getType();

	/**
	 * Returns the concept to be displayed in the destination column, which is:
	 * <ul>
	 * <li>the relationship destination concept for relationships;
	 * <li>the relationship destination concept for relationship concrete domain members;
	 * <li>{@code null} for concept concrete domain members.
	 * </ul>
	 * 
	 * @return the destination concept
	 */
	ChangeConcept getDestination();

	/**
	 * @return the change nature of this entry ({@link Nature#INFERRED} or {@link Nature#REDUNDANT})
	 */
	Nature getNature();
}
