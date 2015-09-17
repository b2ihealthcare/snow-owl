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
package com.b2international.snowowl.snomed.datastore.index.refset;

import javax.annotation.OverridingMethodsMustInvokeSuper;

import org.apache.lucene.document.Document;
import org.eclipse.emf.cdo.view.CDOView;

import com.b2international.commons.StringUtils;
import com.b2international.snowowl.core.ApplicationContext;
import com.b2international.snowowl.datastore.BranchPathUtils;
import com.b2international.snowowl.datastore.cdo.CDOIDUtils;
import com.b2international.snowowl.datastore.index.AbstractIndexMappingStrategy;
import com.b2international.snowowl.snomed.datastore.index.mapping.SnomedMappings;
import com.b2international.snowowl.snomed.datastore.index.update.ComponentLabelProvider;
import com.b2international.snowowl.snomed.datastore.index.update.RefSetMemberLabelUpdater;
import com.b2international.snowowl.snomed.datastore.services.ISnomedComponentService;
import com.b2international.snowowl.snomed.datastore.services.SnomedConceptNameProvider;
import com.b2international.snowowl.snomed.snomedrefset.SnomedRefSetMember;
import com.google.common.base.Preconditions;

@Deprecated
public class SnomedRefSetMemberIndexMappingStrategy extends AbstractIndexMappingStrategy {
	
	private final SnomedRefSetMember member;
	private String label;
	
	public SnomedRefSetMemberIndexMappingStrategy(final SnomedRefSetMember refSetMember) {
		this(refSetMember, null);
	}

	public SnomedRefSetMemberIndexMappingStrategy(final SnomedRefSetMember refSetMember, String label) {
		this.label = label;
		this.member = Preconditions.checkNotNull(refSetMember, "Reference set member cannot be null.");
	}

	@Override
	@OverridingMethodsMustInvokeSuper
	public Document createDocument() {
		final ComponentLabelProvider labelProvider = new ComponentLabelProvider() {
			@Override
			public String getComponentLabel(String componentId) {
				return getCoreComponentLabel(componentId, member.cdoView());
			}
		};
		return SnomedMappings.doc()
				// immutable fields
				.with(new RefSetMemberImmutablePropertyUpdater(member))
				// mutable fields
				.with(new RefSetMemberMutablePropertyUpdater(member, labelProvider))
				.with(new RefSetMemberLabelUpdater(member, label, labelProvider))
				.build();
	}

	@Override
	protected long getStorageKey() {
		return CDOIDUtils.asLong(member.cdoID());
	}
	
	/*returns with the label of the SNOMED CT core component.*/
	private String getCoreComponentLabel(final String componentId, final CDOView view) {
		System.err.println("TODO merge this into a proper labelProvider with cache support: " + componentId);
		final ISnomedComponentService componentService = ApplicationContext.getInstance().getService(ISnomedComponentService.class);
		label = componentService.getLabels(BranchPathUtils.createPath(view), componentId)[0];

		if (!StringUtils.isEmpty(label)) {
			return label;
		}
		
		label = SnomedConceptNameProvider.INSTANCE.getText(componentId);
		
		if (!StringUtils.isEmpty(label)) {
			return label;
		}
		
		label = SnomedConceptNameProvider.INSTANCE.getText(componentId, view);
		
		if (!StringUtils.isEmpty(label)) {
			return label;
		}
		
		label = componentId;
		
		return label;
	}
}
