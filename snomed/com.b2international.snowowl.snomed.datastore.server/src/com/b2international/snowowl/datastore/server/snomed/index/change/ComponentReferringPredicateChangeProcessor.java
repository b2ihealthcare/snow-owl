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
package com.b2international.snowowl.datastore.server.snomed.index.change;

import static com.google.common.collect.Sets.newHashSet;

import java.util.Collection;
import java.util.Collections;
import java.util.Set;

import org.eclipse.emf.cdo.common.id.CDOID;
import org.eclipse.emf.cdo.common.id.CDOIDUtil;

import com.b2international.collections.longs.LongSet;
import com.b2international.index.revision.RevisionSearcher;
import com.b2international.snowowl.datastore.ICDOCommitChangeSet;
import com.b2international.snowowl.snomed.datastore.PredicateUtils;
import com.b2international.snowowl.snomed.datastore.PredicateUtils.ConstraintDomain;
import com.b2international.snowowl.snomed.mrcm.AttributeConstraint;
import com.b2international.snowowl.snomed.mrcm.ConceptModelPredicate;
import com.b2international.snowowl.snomed.mrcm.ConceptSetDefinition;
import com.b2international.snowowl.snomed.mrcm.MrcmPackage;
import com.b2international.snowowl.snomed.mrcm.core.ConceptModelUtils;
import com.google.common.base.Function;
import com.google.common.collect.FluentIterable;
import com.google.common.collect.HashMultimap;
import com.google.common.collect.ImmutableListMultimap;
import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.Iterables;
import com.google.common.collect.Multimap;
import com.google.common.collect.Multimaps;
import com.google.common.collect.Sets;

/**
 * @since 4.7
 */
public class ComponentReferringPredicateChangeProcessor {
	
	private final LongSet allConceptIds;
	private final Collection<ConstraintDomain> allConstraintDomains;
	
	public ComponentReferringPredicateChangeProcessor(final LongSet allConceptIds, final Collection<ConstraintDomain> allConstraintDomains) {
		this.allConceptIds = allConceptIds;
		this.allConstraintDomains = allConstraintDomains;
	}
	
	public Multimap<String, String> process(ICDOCommitChangeSet commitChangeSet, RevisionSearcher searcher) {
		final Collection<AttributeConstraint> newAndDirtyConstraints = newHashSet();

		for (ConceptModelPredicate predicate : Iterables.concat(commitChangeSet.getNewComponents(ConceptModelPredicate.class),
				commitChangeSet.getDirtyComponents(ConceptModelPredicate.class))) {
			newAndDirtyConstraints.add(ConceptModelUtils.getContainerConstraint(predicate));
		}

		for (ConceptSetDefinition definition : Iterables.concat(commitChangeSet.getNewComponents(ConceptSetDefinition.class),
				commitChangeSet.getDirtyComponents(ConceptSetDefinition.class))) {
			newAndDirtyConstraints.add(ConceptModelUtils.getContainerConstraint(definition));
		}
		
		return doProcess(newAndDirtyConstraints, commitChangeSet.getDetachedComponents(MrcmPackage.Literals.ATTRIBUTE_CONSTRAINT));
	}
	
	/* marks the proper concepts and reference sets if the transaction contains attribute constraint changes */
	private Multimap<String, String> doProcess(final Iterable<AttributeConstraint> newAndDirtyConstraints, final Iterable<CDOID> deletedConstraints) {
		if (Iterables.isEmpty(newAndDirtyConstraints) && Iterables.isEmpty(deletedConstraints)) {
			return ImmutableMultimap.of();
		}
		
		final Multimap<Long, ConstraintDomain> constraintToDomainKeys = HashMultimap.create();
		
		
		for (final ConstraintDomain constraintDomain : allConstraintDomains) {
			constraintToDomainKeys.put(constraintDomain.getStorageKey(), constraintDomain);
		}
		
		final Set<Long> conceptIds = Sets.newHashSet();
		
		for (final AttributeConstraint constraint : newAndDirtyConstraints) {
			final long constraintStoragekey = CDOIDUtil.getLong(constraint.cdoID());
			
			final Set<ConstraintDomain> newDomains = PredicateUtils.processConstraintDomain(constraintStoragekey, constraint.getDomain());
			
			for (final ConstraintDomain domain : newDomains) {
				conceptIds.add(domain.getComponentId());
			}
			
			constraintToDomainKeys.replaceValues(constraintStoragekey, newDomains);
		}
		
		for (final CDOID cdoId : deletedConstraints) {
			final long constraintStoragekey = CDOIDUtil.getLong(cdoId);
			final Collection<ConstraintDomain> domains = constraintToDomainKeys.get(constraintStoragekey);
			for (final ConstraintDomain domain : domains) {
				conceptIds.add(domain.getComponentId());
			}
			
			constraintToDomainKeys.removeAll(constraintStoragekey);
		}
		
		final ImmutableListMultimap<Long, ConstraintDomain> conceptIdToDomainKeys = Multimaps.index(constraintToDomainKeys.values(), new Function<ConstraintDomain, Long>() {
			@Override public Long apply(final ConstraintDomain input) {
				return input.getComponentId();
			}
		});
		
		final Multimap<String, String> conceptReferringPredicates = HashMultimap.create();
		
		for (final Long conceptId : conceptIds) {
			if (allConceptIds.contains(conceptId)) {
				final Collection<String> predicateKeys;
				
				if (!conceptIdToDomainKeys.containsKey(conceptId)) {
					predicateKeys = Collections.emptyList();
				} else {
					predicateKeys = FluentIterable.from(conceptIdToDomainKeys.get(conceptId)).transform(new Function<ConstraintDomain, String>() {
						@Override public String apply(final ConstraintDomain input) {
							return input.getPredicateKey();
						}
					}).toSet();
				}
				
				conceptReferringPredicates.putAll(String.valueOf(conceptId), predicateKeys);
			}
		}
		
		return conceptReferringPredicates;
	}
	
}
