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
package com.b2international.snowowl.snomed.datastore;

import java.util.Collection;

import org.eclipse.emf.cdo.view.CDOView;
import org.eclipse.net4j.util.lifecycle.LifecycleUtil;

import com.b2international.commons.CompareUtils;
import com.b2international.commons.StringUtils;
import com.b2international.snowowl.core.ApplicationContext;
import com.b2international.snowowl.core.api.IBranchPath;
import com.b2international.snowowl.core.api.IBranchPoint;
import com.b2international.snowowl.datastore.BranchPathUtils;
import com.b2international.snowowl.datastore.cdo.ICDOConnection;
import com.b2international.snowowl.datastore.cdo.ICDOConnectionManager;
import com.b2international.snowowl.snomed.Concept;
import com.b2international.snowowl.snomed.Relationship;
import com.b2international.snowowl.snomed.SnomedConstants.Concepts;
import com.b2international.snowowl.snomed.SnomedPackage;
import com.google.common.base.Preconditions;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;

/**
 * Icon ID provider for SNOMED&nbsp;CT concepts.
 * @deprecated
 */
class SnomedConceptIconIdProvider {

	/* (non-Javadoc)
	 * @see com.b2international.snowowl.core.api.IComponentIconIdProvider#getIconId(com.b2international.snowowl.core.api.IBranchPath, java.lang.Object)
	 */
	public String getIconId(final IBranchPath branchPath, final String componentId) {
		
		Preconditions.checkNotNull(branchPath, "Branch path argument cannot be null.");
		Preconditions.checkNotNull(componentId, "SNOMED CT concept ID argument cannot be null.");
		
		return getIconIdUnsafe(branchPath, componentId);
	}
	
	/* (non-Javadoc)
	 * @see com.b2international.snowowl.core.api.IComponentIconIdProvider#getIconId(com.b2international.snowowl.core.api.IBranchPoint, java.lang.Object)
	 */
	public String getIconId(final IBranchPoint branchPoint, final String componentId) {
		
		Preconditions.checkNotNull(branchPoint, "Branch point argument cannot be null.");
		Preconditions.checkNotNull(componentId, "SNOMED CT concept ID argument cannot be null.");
		
		String iconId = getIconIdUnsafe(branchPoint.getBranchPath(), componentId);
		
		//probably a detached concept
		if (StringUtils.isEmpty(iconId)) {
			
			//XXX its quite ugly to check concept existence on the parent branch, but believe me, PM will be happy with performance numbers 
			//note: we should not check parent branch if we are the root branch
			if (!BranchPathUtils.isMain(branchPoint.getBranchPath())) {

				iconId = getIconIdUnsafe(branchPoint.getBranchPath().getParent(), componentId);
				if (!StringUtils.isEmpty(iconId)) {
					
					//XXX I told you. Happy PM. Happy customers!!!
					return iconId; 
					
				}
				//Doh! No :(
			}
			
			
			final ICDOConnection connection = ApplicationContext.getInstance().getService(ICDOConnectionManager.class).get(SnomedPackage.eINSTANCE);
			
			CDOView view = null;
			
			try {
				
				view = connection.createView(branchPoint);
				final Concept concept = new SnomedConceptLookupService().getComponent(componentId, view);
				
				if (null != concept) {
					
					return getParentIconId(branchPoint, concept);
					
				}
				
			} finally {
				
				LifecycleUtil.deactivate(view);
				
			}
			
			
		}
		
		return iconId;
	}
	
	/*returns with the associated icon ID of a concept. may return with null.*/
	private String getIconIdUnsafe(final IBranchPath branchPath, final String conceptId) {
		throw new UnsupportedOperationException("Implement proper icon id state");
	}
	
	/*returns with the icon ID of the first found existing parent concept*/
	private String getParentIconId(final IBranchPoint branchPoint, final Concept... concepts) {
		
		Preconditions.checkNotNull(concepts, "Concepts argument cannot be null.");
		
		if (CompareUtils.isEmpty(concepts)) {
			
			return Concepts.ROOT_CONCEPT;
			
		}
		
		String parentIconId = null;
		
		final Collection<Concept> parents = Lists.newArrayList();
		for (final Concept concept : concepts) {
			
			for (final Relationship sourceRelationship : concept.getOutboundRelationships()) {
				
				//ignore non IS_As
				if (!Concepts.IS_A.equals(sourceRelationship.getType().getId())) {
					continue;
				}
				
				final Concept destination = sourceRelationship.getDestination();
				//check for first existing parent
				parentIconId = getIconIdUnsafe(branchPoint.getBranchPath(), destination.getId());
				
				if (!StringUtils.isEmpty(parentIconId)) {
					
					return parentIconId;
					
				} else {
					
					//collect parents
					parents.add(destination);
					
				}
				
			}
			
		}
		
		//from bottom to top get the first icon ID
		return getParentIconId(branchPoint, Iterables.toArray(parents, Concept.class));
		
	}

}