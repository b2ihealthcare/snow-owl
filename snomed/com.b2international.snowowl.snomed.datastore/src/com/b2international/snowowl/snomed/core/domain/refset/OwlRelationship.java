/*
 * Copyright 2024 B2i Healthcare, https://b2ihealthcare.com
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
package com.b2international.snowowl.snomed.core.domain.refset;

import com.b2international.snowowl.snomed.core.domain.RelationshipValue;

/**
 * @since 9.2.2 
 */
public interface OwlRelationship {

	String getTypeId();

	String getDestinationId();

	Integer getRelationshipGroup();

	boolean hasValue();

	RelationshipValue getValueAsObject();
	
}
