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
package com.b2international.snowowl.snomed.datastore.server.events;

import com.b2international.snowowl.core.domain.TransactionContext;
import com.b2international.snowowl.snomed.Component;
import com.b2international.snowowl.snomed.Concept;
import com.b2international.snowowl.snomed.Description;
import com.b2international.snowowl.snomed.Relationship;
import com.b2international.snowowl.snomed.snomedrefset.SnomedRefSet;

/**
 * @since 4.5
 */
final class SnomedComponentDeleteRequest extends SnomedRequest<TransactionContext, Void> {

	private String componentId;
	private Class<? extends Component> type;

	public SnomedComponentDeleteRequest(String componentId, Class<? extends Component> type) {
		this.componentId = componentId;
		this.type = type;
	}
	
	@Override
	public Void execute(TransactionContext context) {
		context.delete(context.lookup(componentId, type));
		return null;
	}

	@Override
	protected Class<Void> getReturnType() {
		return Void.class;
	}

	@Override
	protected String getPath() {
		if (type == Concept.class) {
			return "/concepts";
		} else if (type == Relationship.class) {
			return "/relationships";
		} else if (type == Description.class) {
			return "/descriptions";
		} else if (SnomedRefSet.class.isAssignableFrom(type)) {
			return "/refsets";
		}
		throw new UnsupportedOperationException("Unsupported component type: " + type.getName());
	}

}
