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
package com.b2international.snowowl.core.exceptions;

import java.util.Collection;
import java.util.Collections;
import java.util.Map;

import com.b2international.commons.exceptions.ConflictException;
import com.b2international.snowowl.core.merge.MergeConflict;
import com.google.common.base.Objects;

/**
 * @since 4.7
 */
public class MergeConflictException extends ConflictException {

	private static final long serialVersionUID = -2143620528072069325L;

	private final Map<String, Object> details;
	private final Collection<MergeConflict> conflicts;

	public MergeConflictException(final Collection<MergeConflict> conflicts, final String message, final Object... args) {
		this(conflicts, Collections.<String, Object>emptyMap(), message, args);
	}
	
	public MergeConflictException(final Collection<MergeConflict> conflicts, final Map<String, Object> details, final String message, final Object... args) {
		super(message, args);
		this.details = details;
		this.conflicts = conflicts;
	}

	@Override
	protected Map<String, Object> getAdditionalInfo() {
		return details;
	}

	public Collection<MergeConflict> getConflicts() {
		return conflicts;
	}
	
	@Override
	public String toString() {
		return Objects.toStringHelper(this).add("details", details).toString();
	}
}
