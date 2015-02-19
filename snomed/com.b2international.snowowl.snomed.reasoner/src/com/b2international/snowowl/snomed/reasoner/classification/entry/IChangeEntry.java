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

import com.b2international.snowowl.core.api.IComponentWithIconId;
import com.b2international.snowowl.snomed.reasoner.classification.entry.AbstractChangeEntry.Nature;

/**
 * Common interface for all change entries reported in a reasoner change set.
 * @see AbstractChangeEntry
 */
public interface IChangeEntry extends Serializable {

	/**
	 * @return the component for the source column (referenced concept for concept concrete domain members, the relationship source concept for
	 *         relationship concrete domain members and relationships)
	 */
	IComponentWithIconId<Long> getSource();

	/**
	 * @return the component for the type column ({@code null} for concept concrete domain members, the relationship type concept for relationship
	 *         concrete domain members and relationships)
	 */
	IComponentWithIconId<Long> getType();

	/**
	 * @return the component for the destination column ({@code null} for concept concrete domain members, the relationship destination concept for
	 *         relationship concrete domain members and relationships)
	 */
	IComponentWithIconId<Long> getDestination();

	/**
	 * @return the change nature {@link Nature#INFERRED} or {@link Nature#REDUNDANT}
	 */
	Nature getNature();
}