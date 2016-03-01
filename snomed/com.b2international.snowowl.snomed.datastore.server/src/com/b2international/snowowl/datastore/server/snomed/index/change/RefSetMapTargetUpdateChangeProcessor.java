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

import java.io.IOException;
import java.util.Collection;
import java.util.Collections;

import org.apache.lucene.document.Document;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.search.TotalHitCountCollector;
import org.eclipse.emf.cdo.CDOObject;
import org.eclipse.emf.cdo.common.revision.delta.CDORevisionDelta;
import org.eclipse.emf.ecore.EStructuralFeature;

import com.b2international.snowowl.core.api.IBranchPath;
import com.b2international.snowowl.datastore.ICDOCommitChangeSet;
import com.b2international.snowowl.datastore.index.ChangeSetProcessorBase;
import com.b2international.snowowl.datastore.index.DocumentUpdaterBase;
import com.b2international.snowowl.datastore.index.IndexRead;
import com.b2international.snowowl.datastore.index.IndexUtils;
import com.b2international.snowowl.datastore.server.snomed.index.SnomedIndexServerService;
import com.b2international.snowowl.snomed.datastore.index.mapping.SnomedDocumentBuilder;
import com.b2international.snowowl.snomed.datastore.index.mapping.SnomedMappings;
import com.b2international.snowowl.snomed.snomedrefset.SnomedMappingRefSet;
import com.b2international.snowowl.snomed.snomedrefset.SnomedRefSetPackage;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableList.Builder;
import com.google.common.collect.Iterables;

/**
 * Updates map target on mapping reference set members if the map target component type has changed on the corresponding mapping refset.
 * 
 * @since 4.6
 */
public class RefSetMapTargetUpdateChangeProcessor extends ChangeSetProcessorBase<SnomedDocumentBuilder> {

	private final SnomedIndexServerService index;
	private final IBranchPath branchPath;

	public RefSetMapTargetUpdateChangeProcessor(IBranchPath branchPath, SnomedIndexServerService index) {
		super("mapping refset map target changes");
		this.branchPath = branchPath;
		this.index = index;
	}
	
	@Override
	protected void updateDocuments(ICDOCommitChangeSet commitChangeSet) {
		for (SnomedMappingRefSet refSet : Iterables.filter(commitChangeSet.getDirtyComponents(), SnomedMappingRefSet.class)) {
			if (hasChanged(commitChangeSet, refSet, SnomedRefSetPackage.Literals.SNOMED_MAPPING_REF_SET__MAP_TARGET_COMPONENT_TYPE)) {
				for (String memberId : getMemberIds(refSet)) {
					registerUpdate(memberId, new RefSetMemberMapTargetUpdater(memberId, refSet.getMapTargetComponentType()));
				}
			}
		}
	}

	private Collection<String> getMemberIds(final SnomedMappingRefSet refSet) {
		return index.executeReadTransaction(branchPath, new IndexRead<Collection<String>>() {
			@Override
			public Collection<String> execute(IndexSearcher index) throws IOException {
				final Query query = SnomedMappings.newQuery().memberRefSetId(refSet.getIdentifierId()).matchAll();
				final TotalHitCountCollector collector = new TotalHitCountCollector();
				index.search(query, collector);
				if (collector.getTotalHits() <= 0) {
					return Collections.emptySet();
				}
				final TopDocs topDocs = index.search(query, collector.getTotalHits());
				if (IndexUtils.isEmpty(topDocs)) {
					return Collections.emptySet();
				}
				final Builder<String> builder = ImmutableList.builder();
				for (int i = 0; i < topDocs.scoreDocs.length; i++) {
					final Document doc = index.doc(topDocs.scoreDocs[i].doc, SnomedMappings.fieldsToLoad().memberUuid().build());
					builder.add(SnomedMappings.memberUuid().getValue(doc));
				}
				return builder.build();
			}
		});
	}

	private boolean hasChanged(ICDOCommitChangeSet commitChangeSet, CDOObject object, final EStructuralFeature feature) {
		final CDORevisionDelta delta = commitChangeSet.getRevisionDeltas().get(object.cdoID());
		return delta != null && delta.getFeatureDelta(feature) != null;
	}
	
	private static class RefSetMemberMapTargetUpdater extends DocumentUpdaterBase<SnomedDocumentBuilder> {

		private final short mapTargetComponentType;

		public RefSetMemberMapTargetUpdater(String componentId, short mapTargetComponentType) {
			super(componentId);
			this.mapTargetComponentType = mapTargetComponentType;
		}

		@Override
		protected void doUpdate(SnomedDocumentBuilder doc) {
			doc.removeAll(SnomedMappings.memberMapTargetComponentType());
			doc.memberMapTargetComponentType(Integer.valueOf(mapTargetComponentType));
		}
		
	}
	
}
