/*
 * Copyright 2011-2018 B2i Healthcare Pte Ltd, http://b2i.sg
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
package com.b2international.snowowl.snomed.datastore.index.change;

import static com.google.common.collect.Sets.newHashSet;

import java.util.Set;

import org.eclipse.emf.cdo.common.id.CDOIDUtil;

import com.b2international.index.revision.RevisionSearcher;
import com.b2international.snowowl.datastore.ICDOCommitChangeSet;
import com.b2international.snowowl.datastore.index.ChangeSetProcessorBase;
import com.b2international.snowowl.snomed.core.mrcm.ConceptModelUtils;
import com.b2international.snowowl.snomed.datastore.index.constraint.SnomedConstraintDocument;
import com.b2international.snowowl.snomed.mrcm.AttributeConstraint;
import com.b2international.snowowl.snomed.mrcm.ConceptModelComponent;
import com.b2international.snowowl.snomed.mrcm.ConceptModelPredicate;
import com.b2international.snowowl.snomed.mrcm.ConceptSetDefinition;
import com.b2international.snowowl.snomed.mrcm.MrcmPackage;
import com.google.common.collect.Iterables;

/**
 * @since 4.3
 */
public class ConstraintChangeProcessor extends ChangeSetProcessorBase {

	public ConstraintChangeProcessor() {
		super("predicate changes");
	}

	@Override
	public void process(ICDOCommitChangeSet commitChangeSet, RevisionSearcher searcher) {

		Set<AttributeConstraint> newConstraints = newHashSet();
		Set<AttributeConstraint> changedConstraints = newHashSet();
		
		for (ConceptModelComponent component : Iterables.concat(
				commitChangeSet.getNewComponents(ConceptModelPredicate.class),
				commitChangeSet.getNewComponents(ConceptSetDefinition.class))) {

			final AttributeConstraint constraint = ConceptModelUtils.getContainerConstraint(component);
			if (commitChangeSet.getNewComponents().contains(constraint)) {
				newConstraints.add(constraint);
			} else {
				changedConstraints.add(constraint);	
			}
		}

		for (ConceptModelComponent component : Iterables.concat(
				commitChangeSet.getDirtyComponents(ConceptModelPredicate.class),
				commitChangeSet.getDirtyComponents(ConceptSetDefinition.class))) {
			
			final AttributeConstraint constraint = ConceptModelUtils.getContainerConstraint(component);
			changedConstraints.add(constraint);
		}
		
		for (AttributeConstraint newConstraint : newConstraints) {
			indexNewRevision(SnomedConstraintDocument.builder(newConstraint)
					.storageKey(CDOIDUtil.getLong(newConstraint.cdoID()))
					.build());
		}
		
		for (AttributeConstraint changedConstraint : changedConstraints) {
			indexChangedRevision(SnomedConstraintDocument.builder(changedConstraint)
					.storageKey(CDOIDUtil.getLong(changedConstraint.cdoID()))
					.build());
		}

		deleteRevisions(SnomedConstraintDocument.class, commitChangeSet.getDetachedComponents(MrcmPackage.Literals.ATTRIBUTE_CONSTRAINT));
	}
	
}
