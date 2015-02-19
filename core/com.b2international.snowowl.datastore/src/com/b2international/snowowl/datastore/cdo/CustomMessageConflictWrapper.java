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
package com.b2international.snowowl.datastore.cdo;

import static com.google.common.base.Strings.nullToEmpty;

import java.io.Serializable;

import org.eclipse.emf.cdo.common.id.CDOID;

/**
 * Customized conflict wrapper wrapping a customized message unlike {@link ConflictWrapper}.
 */
public class CustomMessageConflictWrapper extends ConflictWrapper implements Serializable, CustomConflictWrapper {
	
	private static final long serialVersionUID = 4894835432628435743L;

	private final String message;

	public CustomMessageConflictWrapper(final CDOID cdoId, final String message) {
		super(createChange(cdoId), createChange(cdoId));
		this.message = nullToEmpty(message);
	}
	
	@Override
	public String toString() {
		return message;
	}
	
}