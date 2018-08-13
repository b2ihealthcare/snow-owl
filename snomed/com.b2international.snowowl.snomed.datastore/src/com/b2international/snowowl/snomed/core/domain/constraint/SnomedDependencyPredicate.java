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

import com.b2international.snowowl.snomed.datastore.index.constraint.DependencyPredicateFragment;

/**
 * @since 6.5
 */
public final class SnomedDependencyPredicate extends SnomedPredicate {

	private GroupRule groupRule;
	private DependencyOperator dependencyOperator;
	private Set<SnomedPredicate> children = newHashSet();

	public GroupRule getGroupRule() {
		return groupRule;
	}

	public void setGroupRule(final GroupRule groupRule) {
		this.groupRule = groupRule;
	}

	public DependencyOperator getDependencyOperator() {
		return dependencyOperator;
	}

	public void setDependencyOperator(final DependencyOperator dependencyOperator) {
		this.dependencyOperator = dependencyOperator;
	}

	public Set<SnomedPredicate> getChildren() {
		return children;
	}

	public void setChildren(final Set<SnomedPredicate> children) {
		this.children = children;
	}

	@Override
	public DependencyPredicateFragment createModel() {
		return new DependencyPredicateFragment(getId(), isActive(), getEffectiveTime(), getAuthor(), getGroupRule(), getDependencyOperator(), children.stream().map(SnomedPredicate::createModel).collect(Collectors.toSet()));
	}

	@Override
	public SnomedDependencyPredicate deepCopy(final Date date, final String userName) {
		final SnomedDependencyPredicate copy = new SnomedDependencyPredicate();

		copy.setActive(isActive());
		copy.setAuthor(userName);
		copy.setChildren(getChildren().stream()
				.map(p -> p.deepCopy(date, userName))
				.collect(Collectors.toSet()));
		copy.setDependencyOperator(getDependencyOperator());
		copy.setEffectiveTime(date.getTime());
		copy.setGroupRule(getGroupRule());
		copy.setId(UUID.randomUUID().toString());

		return copy;
	}

	@Override
	public void collectConceptIds(final Collection<String> conceptIds) {
		children.forEach(p -> p.collectConceptIds(conceptIds));
	}

	@Override
	public String validate() {
		final String parentMessage = super.validate();

		if (parentMessage != null) {
			return parentMessage;
		}

		if (getGroupRule() == null) { return String.format("Group rule should be specified for %s with UUID %s.", displayName(), getId()); }
		if (getDependencyOperator() == null) { return String.format("Dependency operator should be specified for %s with UUID %s.", displayName(), getId()); }
		if (getChildren().isEmpty()) { return String.format("%s with UUID %s should include at least one child predicate.", displayName(), getId()); }

		for (final SnomedPredicate child : getChildren()) {
			final String childMessage = child.validate();
			if (childMessage != null) { return childMessage; }
		}

		return null;
	}

	@Override
	public int structuralHashCode() {
		return 31 * super.structuralHashCode() + structuralHashCode(children, dependencyOperator, groupRule);
	}

	@Override
	public boolean structurallyEquals(final SnomedConceptModelComponent obj) {
		if (this == obj) { return true; }
		if (!super.structurallyEquals(obj)) { return false; }
		if (getClass() != obj.getClass()) { return false; }

		final SnomedDependencyPredicate other = (SnomedDependencyPredicate) obj;

		if (dependencyOperator != other.dependencyOperator) { return false; }
		if (groupRule != other.groupRule) { return false; }
		
		if (children.size() != other.children.size()) { return false; }
		// Compare child definitions pairwise, as regular equals cannot be used here
		for (final SnomedPredicate child : children) {
			boolean matchFound = false;
			
			for (final SnomedPredicate otherChild : other.children) {
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
