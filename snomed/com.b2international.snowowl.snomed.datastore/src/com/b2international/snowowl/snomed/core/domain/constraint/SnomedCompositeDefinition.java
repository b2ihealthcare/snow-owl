/*
 * Copyright 2018 B2i Healthcare Pte Ltd, http://b2i.sg
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
package com.b2international.snowowl.snomed.core.domain.constraint;

import static com.google.common.collect.Sets.newHashSet;

import java.util.Collection;
import java.util.Date;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

import com.b2international.snowowl.snomed.datastore.index.constraint.CompositeDefinitionFragment;
import com.google.common.base.Joiner;

/**
 * @since 6.5
 */
public final class SnomedCompositeDefinition extends SnomedConceptSetDefinition {

	private Set<SnomedConceptSetDefinition> children = newHashSet();

	public Set<SnomedConceptSetDefinition> getChildren() {
		return children;
	}

	public void setChildren(final Set<SnomedConceptSetDefinition> children) {
		this.children = children;
	}

	@Override
	public String toEcl() {
		final Set<String> subExpressions = children.stream()
				.map(SnomedConceptSetDefinition::toEcl)
				.collect(Collectors.toSet());

		// Wrap each sub-expression into parentheses
		return "(" + Joiner.on(") OR (").join(subExpressions) + ")";
	}

	@Override
	public CompositeDefinitionFragment createModel() {
		return new CompositeDefinitionFragment(getId(), isActive(), getEffectiveTime(), getAuthor(), children.stream().map(SnomedConceptSetDefinition::createModel).collect(Collectors.toSet()));
	}

	@Override
	public SnomedCompositeDefinition deepCopy(final Date date, final String userName) {
		final SnomedCompositeDefinition copy = new SnomedCompositeDefinition();

		copy.setActive(isActive());
		copy.setAuthor(userName);
		copy.setChildren(getChildren().stream()
				.map(d -> d.deepCopy(date, userName))
				.collect(Collectors.toSet()));
		copy.setEffectiveTime(date.getTime());
		copy.setId(UUID.randomUUID().toString());

		return copy;
	}

	@Override
	public void collectConceptIds(final Collection<String> conceptIds) {
		children.forEach(d -> d.collectConceptIds(conceptIds));
	}

	@Override
	public String validate() {
		final String parentMessage = super.validate();

		if (parentMessage != null) {
			return parentMessage;
		}

		if (getChildren().isEmpty()) { return String.format("%s with UUID %s should include at least one child definition.", displayName(), getId()); }

		for (final SnomedConceptSetDefinition child : getChildren()) {
			final String childMessage = child.validate();
			if (childMessage != null) { return childMessage; }
		}

		return null;
	}

	@Override
	public int structuralHashCode() {
		return 31 * super.structuralHashCode() + structuralHashCode(children);
	}

	@Override
	public boolean structurallyEquals(final SnomedConceptModelComponent obj) {
		if (this == obj) { return true; }
		if (!super.structurallyEquals(obj)) { return false; }
		if (getClass() != obj.getClass()) { return false; }

		final SnomedCompositeDefinition other = (SnomedCompositeDefinition) obj;

		if (children.size() != other.children.size()) { return false; }
		// Compare child definitions pairwise, as regular equals cannot be used here
		for (final SnomedConceptSetDefinition child : children) {
			boolean matchFound = false;
			
			for (final SnomedConceptSetDefinition otherChild : other.children) {
				if (structurallyEquals(child, otherChild)) {
					matchFound = true;
					break;
				}
			}
			
			if (!matchFound) {
				return false;
			}
		}
		
		return true;
	}
}
