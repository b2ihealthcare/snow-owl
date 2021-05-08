/*
 * Copyright 2011-2021 B2i Healthcare Pte Ltd, http://b2i.sg
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

import java.io.Serializable;
import java.util.function.Function;

import javax.annotation.OverridingMethodsMustInvokeSuper;

import com.google.common.base.MoreObjects;
import com.google.common.base.MoreObjects.ToStringHelper;

/**
 * Represents the bare minimum of a SNOMED CT relationship (without binding the source concept).
 * 
 * @since 1.0
 * @see StatementFragmentWithDestination
 * @see StatementFragmentWithValue
 */
public abstract class StatementFragment implements Serializable {

	private final long typeId;
	private final int group;
	private final int unionGroup;
	private final boolean universal;

	// Only stored if the original relationship is known
	private final long statementId;
	private final long moduleId;
	private final boolean released;
	private final boolean hasStatedPair;

	protected StatementFragment(
		final long typeId, 
		final int group, 
		final int unionGroup, 
		final boolean universal, 
		final long statementId, 
		final long moduleId,
		final boolean released, 
		final boolean hasStatedPair) {
		
		this.typeId = typeId;
		this.group = group;
		this.unionGroup = unionGroup;
		this.universal = universal;
		
		this.statementId = statementId;
		this.moduleId = moduleId;
		this.released = released;
		this.hasStatedPair = hasStatedPair;
	}

	public long getTypeId() {
		return typeId;
	}

	public int getGroup() {
		return group;
	}

	public int getUnionGroup() {
		return unionGroup;
	}

	public boolean isUniversal() {
		return universal;
	}

	public long getStatementId() {
		return statementId;
	}

	public long getModuleId() {
		return moduleId;
	}

	public boolean isReleased() {
		return released;
	}

	public boolean hasStatedPair() {
		return hasStatedPair;
	}
	
	public abstract <T> T map(
		final Function<StatementFragmentWithDestination, T> destinationFn, 
		final Function<StatementFragmentWithValue, T> valueFn);

	/*
	 * XXX: hashCode and equals is context-specific for statements; locking them to
	 * the default implementation here.
	 * 
	 * Define an Equivalence and use com.google.common.base.Equivalence#wrap to
	 * create instances that behave as expected in that particular setting, when eg.
	 * added to a Set.
	 */
	@Override
	public final int hashCode() {
		return super.hashCode();
	}
	
	@Override
	public final boolean equals(Object obj) {
		return super.equals(obj);
	}

	@OverridingMethodsMustInvokeSuper
	protected ToStringHelper toStringHelper() {
		return MoreObjects.toStringHelper(this)
			.add("typeId", typeId)
			.add("group", group)
			.add("unionGroup", unionGroup)
			.add("universal", universal)
			.add("statementId", statementId)
			.add("moduleId", moduleId)
			.add("released", released)
			.add("hasStatedPair", hasStatedPair);
	}

	@Override
	public final String toString() {
		return toStringHelper().toString();
	}
}
