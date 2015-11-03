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
package com.b2international.snowowl.datastore.server.snomed.index.change;

import static com.google.common.collect.Sets.newHashSet;

import java.util.Collection;
import java.util.Set;

import org.eclipse.emf.cdo.common.id.CDOID;
import org.eclipse.emf.cdo.common.id.CDOIDUtil;

import com.b2international.snowowl.core.ApplicationContext;
import com.b2international.snowowl.core.api.IBranchPath;
import com.b2international.snowowl.datastore.ICDOCommitChangeSet;
import com.b2international.snowowl.datastore.index.ChangeSetProcessorBase;
import com.b2international.snowowl.snomed.datastore.PredicateUtils;
import com.b2international.snowowl.snomed.datastore.PredicateUtils.ConstraintDomain;
import com.b2international.snowowl.snomed.datastore.SnomedPredicateBrowser;
import com.b2international.snowowl.snomed.datastore.index.mapping.SnomedDocumentBuilder;
import com.b2international.snowowl.snomed.datastore.index.update.ComponentConstraintUpdater;
import com.b2international.snowowl.snomed.datastore.index.update.ConstraintUpdater;
import com.b2international.snowowl.snomed.mrcm.AttributeConstraint;
import com.b2international.snowowl.snomed.mrcm.ConceptModelPredicate;
import com.b2international.snowowl.snomed.mrcm.ConceptSetDefinition;
import com.b2international.snowowl.snomed.mrcm.MrcmPackage;
import com.b2international.snowowl.snomed.mrcm.core.ConceptModelUtils;
import com.google.common.base.Function;
import com.google.common.collect.FluentIterable;
import com.google.common.collect.HashMultimap;
import com.google.common.collect.Iterables;
import com.google.common.collect.Multimap;

import bak.pcj.set.LongSet;

/**
 * @since 4.3
 */
public class ConstraintChangeProcessor extends ChangeSetProcessorBase<SnomedDocumentBuilder> {

	private IBranchPath branchPath;
	private SnomedPredicateBrowser constraintService;
	private LongSet allConceptIds;

	public ConstraintChangeProcessor(IBranchPath branchPath, LongSet allConceptIds) {
		super("predicate changes");
		this.branchPath = branchPath;
		this.allConceptIds = allConceptIds;
		this.constraintService = ApplicationContext.getInstance().getServiceChecked(SnomedPredicateBrowser.class);
	}

	@Override
	public void process(ICDOCommitChangeSet commitChangeSet) {
		final Collection<AttributeConstraint> newAndDirtyConstraints = newHashSet();

		for (ConceptModelPredicate predicate : Iterables.concat(getNewComponents(commitChangeSet, ConceptModelPredicate.class),
				getDirtyComponents(commitChangeSet, ConceptModelPredicate.class))) {
			newAndDirtyConstraints.add(ConceptModelUtils.getContainerConstraint(predicate));
		}

		for (ConceptSetDefinition definition : Iterables.concat(getNewComponents(commitChangeSet, ConceptSetDefinition.class),
				getDirtyComponents(commitChangeSet, ConceptSetDefinition.class))) {
			newAndDirtyConstraints.add(ConceptModelUtils.getContainerConstraint(definition));
		}

		// (re)index new/changed constraints
		for (AttributeConstraint constraint : newAndDirtyConstraints) {
			registerUpdate(constraint.getUuid(), new ConstraintUpdater(constraint));
		}

		final Collection<CDOID> deletedConstraints = getDetachedComponents(commitChangeSet, MrcmPackage.Literals.ATTRIBUTE_CONSTRAINT);
		registerDeletions(deletedConstraints);

		processAttributeConstraints(newAndDirtyConstraints, deletedConstraints);
	}

	/* marks the proper concepts and reference sets if the transaction contains attribute constraint changes */
	private void processAttributeConstraints(Iterable<AttributeConstraint> newAndDirtyConstraints, Iterable<CDOID> deletedConstraints) {
		final Multimap<String, ConstraintDomain> conceptToPredicateKeys = HashMultimap.create();
		
		for (AttributeConstraint constraint : newAndDirtyConstraints) {
			final Set<ConstraintDomain> newDomains = PredicateUtils.processConstraintDomain(CDOIDUtil.getLong(constraint.cdoID()), constraint.getDomain());
			for (ConstraintDomain domain : newDomains) {
				conceptToPredicateKeys.put(Long.toString(domain.getComponentId()), domain);
			}
		}
		
		for (AttributeConstraint constraint : newAndDirtyConstraints) {
			final Set<ConstraintDomain> previousDomains = constraintService.getConstraintDomains(branchPath, CDOIDUtil.getLong(constraint.cdoID()));
			for (ConstraintDomain domain : previousDomains) {
				conceptToPredicateKeys.put(Long.toString(domain.getComponentId()), domain);
			}
		}
		
		for (CDOID cdoId : deletedConstraints) {
			final Set<ConstraintDomain> previousDomains = constraintService.getConstraintDomains(branchPath, CDOIDUtil.getLong(cdoId));
			for (ConstraintDomain domain : previousDomains) {
				conceptToPredicateKeys.remove(Long.toString(domain.getComponentId()), domain);
			}
		}
		
		for (String conceptId : conceptToPredicateKeys.keySet()) {
			if (allConceptIds.contains(Long.parseLong(conceptId))) {
				final Collection<String> predicateKeys = FluentIterable.from(conceptToPredicateKeys.get(conceptId)).transform(new Function<ConstraintDomain, String>() {
					@Override
					public String apply(ConstraintDomain input) {
						return input.getPredicateKey();
					}
				}).toSet();
				registerUpdate(conceptId, new ComponentConstraintUpdater(conceptId, predicateKeys));
			}
		}
	}

}
