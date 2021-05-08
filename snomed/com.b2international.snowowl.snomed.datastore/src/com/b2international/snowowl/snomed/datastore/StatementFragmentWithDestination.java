/*
 * Copyright 2021 B2i Healthcare Pte Ltd, http://b2i.sg
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

import java.util.function.Function;

import com.google.common.base.MoreObjects.ToStringHelper;

/**
 * Represents the bare minimum of a SNOMED CT relationship with a destination ID.
 * 
 * @since 7.17
 */
public final class StatementFragmentWithDestination extends StatementFragment {

	private final long destinationId;
	private final boolean destinationNegated;

	public StatementFragmentWithDestination(final long typeId, final long destinationId) {
		this(typeId, 0, 0, false, -1L, -1L, false, false, destinationId, false);
	}

	public StatementFragmentWithDestination(
		final long typeId, 
		final int group, 
		final int unionGroup, 
		final boolean universal, 
		final long statementId,
		final long moduleId, 
		final boolean released, 
		final boolean hasStatedPair,
		final long destinationId, 
		final boolean destinationNegated) {

		super(typeId, group, unionGroup, universal, statementId, moduleId, released, hasStatedPair);
		this.destinationId = destinationId;
		this.destinationNegated = destinationNegated;
	}

	public long getDestinationId() {
		return destinationId;
	}
	
	public boolean isDestinationNegated() {
		return destinationNegated;
	}
	
	@Override
	public <T> T map(
		final Function<StatementFragmentWithDestination, T> destinationFn,
		final Function<StatementFragmentWithValue, T> valueFn) {
		return destinationFn.apply(this);
	}

	@Override
	protected ToStringHelper toStringHelper() {
		return super.toStringHelper()
			.add("destinationId", destinationId)
			.add("destinationNegated", destinationNegated);
	}
}
