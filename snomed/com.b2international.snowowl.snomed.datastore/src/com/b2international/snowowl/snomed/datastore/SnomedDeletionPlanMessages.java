/*
 * Copyright 2011-2017 B2i Healthcare Pte Ltd, http://b2i.sg
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

/**
 * Messages to use in the deletion plan dialog
 * 
 * @since 4.6
 */
public final class SnomedDeletionPlanMessages {

	public static final String COMPONENT_IS_RELEASED_MESSAGE = "The %s '%s' has been released, and cannot be deleted.";
	
	public static final String UNABLE_TO_DELETE_CONCEPT_DUE_TO_RELEASED_INBOUND_RSHIP_MESSAGE = "Cannot delete concept: '%s' because it has a released inbound relationship: '%s'.";
	
	public static final String UNABLE_TO_DELETE_DESCRIPTION_TYPE_CONCEPT_MESSAGE = "Cannot delete concept: '%s' because it is used as a description type.";
	
	public static final String UNABLE_TO_DELETE_CONCEPT_DUE_TO_ISA_RSHIP_MESSAGE = "Concept '%s' would be deleted when the last active 'Is a' relationship is deleted.";
	
	public static final String UNABLE_TO_DELETE_RELATIONSHIP_MESSAGE = "Cannot relationship concept: '%s'.";
	
	public static final String UNABLE_TO_DELETE_REFERENCE_SET_MESSAGE = "Cannot delete reference set: '%s'.";
	
}
