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

import java.util.Set;

import org.eclipse.emf.cdo.common.id.CDOIDUtil;
import org.eclipse.emf.cdo.common.revision.delta.CDOAddFeatureDelta;
import org.eclipse.emf.cdo.common.revision.delta.CDOClearFeatureDelta;
import org.eclipse.emf.cdo.common.revision.delta.CDOContainerFeatureDelta;
import org.eclipse.emf.cdo.common.revision.delta.CDOFeatureDelta;
import org.eclipse.emf.cdo.common.revision.delta.CDOFeatureDeltaVisitor;
import org.eclipse.emf.cdo.common.revision.delta.CDOListFeatureDelta;
import org.eclipse.emf.cdo.common.revision.delta.CDOMoveFeatureDelta;
import org.eclipse.emf.cdo.common.revision.delta.CDORemoveFeatureDelta;
import org.eclipse.emf.cdo.common.revision.delta.CDOSetFeatureDelta;
import org.eclipse.emf.cdo.common.revision.delta.CDOUnsetFeatureDelta;
import org.eclipse.emf.ecore.EStructuralFeature;

import com.b2international.snowowl.datastore.ICDOCommitChangeSet;
import com.b2international.snowowl.datastore.index.ChangeSetProcessorBase;
import com.b2international.snowowl.datastore.index.ComponentBaseUpdater;
import com.b2international.snowowl.datastore.index.ComponentCompareFieldsUpdater;
import com.b2international.snowowl.snomed.Concept;
import com.b2international.snowowl.snomed.SnomedPackage;
import com.b2international.snowowl.snomed.common.SnomedTerminologyComponentConstants;
import com.b2international.snowowl.snomed.datastore.index.mapping.SnomedDocumentBuilder;
import com.b2international.snowowl.snomed.datastore.index.refset.RefSetMutablePropertyUpdater;
import com.b2international.snowowl.snomed.datastore.index.update.ComponentModuleUpdater;
import com.b2international.snowowl.snomed.datastore.index.update.ConceptDoiUpdater;
import com.b2international.snowowl.snomed.datastore.index.update.ConceptMutablePropertyUpdater;
import com.b2international.snowowl.snomed.datastore.index.update.ConceptNamespaceUpdater;
import com.b2international.snowowl.snomed.snomedrefset.SnomedRefSet;
import com.google.common.base.Predicate;
import com.google.common.collect.FluentIterable;
import com.google.common.collect.ImmutableSet;

/**
 * @since 4.3
 */
public class ConceptChangeProcessor extends ChangeSetProcessorBase<SnomedDocumentBuilder> {
	
	public ConceptChangeProcessor() {
		super("concept changes");
	}
	
	@Override
	protected void indexDocuments(ICDOCommitChangeSet commitChangeSet) {
		for (final Concept concept : getNewComponents(commitChangeSet, Concept.class)) {
			registerImmutablePropertyUpdates(concept);
			registerMutablePropertyUpdates(concept);
		}
		
		// index/store reference set properties on concept document
		final Iterable<SnomedRefSet> newRefSets = FluentIterable.from(commitChangeSet.getNewComponents()).filter(SnomedRefSet.class);
		for (SnomedRefSet refSet : newRefSets) {
			registerUpdate(refSet.getIdentifierId(), new RefSetMutablePropertyUpdater(refSet));
		}
	}

	@Override
	protected void updateDocuments(final ICDOCommitChangeSet commitChangeSet) {
		final Set<Concept> dirtyConcepts = FluentIterable.from(getDirtyComponents(commitChangeSet, Concept.class)).filter(new Predicate<Concept>() {
			@Override
			public boolean apply(Concept input) {
				final DirtyConceptFeatureDeltaVisitor visitor = new DirtyConceptFeatureDeltaVisitor();
				commitChangeSet.getRevisionDeltas().get(input.cdoID()).accept(visitor);
				return visitor.hasAllowedChanges();
			}
		}).toSet();
		
		for (final Concept concept : dirtyConcepts) {
			registerMutablePropertyUpdates(concept);
		}
	}
	
	private void registerImmutablePropertyUpdates(final Concept concept) {
		final String id = concept.getId();
		registerUpdate(id, new ComponentBaseUpdater<SnomedDocumentBuilder>(id, SnomedTerminologyComponentConstants.CONCEPT_NUMBER, concept.cdoID()));
		registerUpdate(id, new ConceptNamespaceUpdater(id));
		registerUpdate(id, new ConceptDoiUpdater(id));
	}

	private void registerMutablePropertyUpdates(final Concept concept) {
		final String id = concept.getId();
		registerUpdate(id, new ConceptMutablePropertyUpdater(concept));
		registerUpdate(id, new ComponentModuleUpdater(concept));
		registerUpdate(id, new ComponentCompareFieldsUpdater<SnomedDocumentBuilder>(id, CDOIDUtil.getLong(concept.cdoID())));
	}
	
	@Override
	protected void deleteDocuments(ICDOCommitChangeSet commitChangeSet) {
		registerDeletions(getDetachedComponents(commitChangeSet, SnomedPackage.Literals.CONCEPT));
	}
	
	/**
	 * @since 4.3
	 */
	private static class DirtyConceptFeatureDeltaVisitor implements CDOFeatureDeltaVisitor {
		
		private static final Set<EStructuralFeature> ALLOWED_CONCEPT_CHANGE_FEATURES = ImmutableSet.<EStructuralFeature>builder()
				.add(SnomedPackage.Literals.COMPONENT__ACTIVE)
				.add(SnomedPackage.Literals.COMPONENT__EFFECTIVE_TIME)
				.add(SnomedPackage.Literals.COMPONENT__RELEASED)
				.add(SnomedPackage.Literals.COMPONENT__MODULE)
				.add(SnomedPackage.Literals.CONCEPT__DEFINITION_STATUS)
				.add(SnomedPackage.Literals.CONCEPT__EXHAUSTIVE)
				.add(SnomedPackage.Literals.CONCEPT__DESCRIPTIONS)
				.add(SnomedPackage.Literals.CONCEPT__OUTBOUND_RELATIONSHIPS)
				.build();
		private boolean hasAllowedChanges;

		@Override
		public void visit(CDOSetFeatureDelta delta) {
			visitDelta(delta);
		}
		
		@Override
		public void visit(CDOListFeatureDelta delta) {
			visitDelta(delta);
		}
		
		@Override
		public void visit(CDOAddFeatureDelta delta) {
			visitDelta(delta);
		}
		
		@Override
		public void visit(CDOClearFeatureDelta delta) {
			visitDelta(delta);
		}
		
		@Override
		public void visit(CDOMoveFeatureDelta delta) {
			visitDelta(delta);
		}
		
		@Override
		public void visit(CDORemoveFeatureDelta delta) {
			visitDelta(delta);
		}
		
		@Override
		public void visit(CDOUnsetFeatureDelta delta) {
			visitDelta(delta);
		}
		
		@Override
		public void visit(CDOContainerFeatureDelta delta) {
			visitDelta(delta);
		}
		
		private void visitDelta(CDOFeatureDelta delta) {
			hasAllowedChanges |= ALLOWED_CONCEPT_CHANGE_FEATURES.contains(delta.getFeature());
		}

		public boolean hasAllowedChanges() {
			return hasAllowedChanges;
		}
		
	}

}
