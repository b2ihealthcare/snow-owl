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
package com.b2international.snowowl.datastore.server.snomed.index;

import static com.b2international.snowowl.core.ApplicationContext.getServiceForClass;

import java.util.Collection;

import org.eclipse.emf.cdo.CDOObject;
import org.eclipse.emf.ecore.EClass;

import com.b2international.commons.CompareUtils;
import com.b2international.snowowl.core.api.IBranchPath;
import com.b2international.snowowl.core.api.SnowowlServiceException;
import com.b2international.snowowl.datastore.ICDOChangeProcessor;
import com.b2international.snowowl.datastore.ICDOCommitChangeSet;
import com.b2international.snowowl.datastore.server.snomed.index.init.ImportIndexServerService;
import com.b2international.snowowl.snomed.SnomedPackage;
import com.b2international.snowowl.snomed.SnomedRelease;
import com.b2international.snowowl.snomed.SnomedVersion;
import com.b2international.snowowl.snomed.datastore.index.SnomedIndexService;
import com.b2international.snowowl.snomed.datastore.index.SnomedReleaseIndexMappingStrategy;
import com.b2international.snowowl.snomed.datastore.index.SnomedVersionIndexMappingStrategy;
import com.b2international.snowowl.snomed.snomedrefset.SnomedRefSetPackage;
import com.b2international.snowowl.terminologymetadata.CodeSystem;
import com.b2international.snowowl.terminologymetadata.CodeSystemVersion;

/**
 * CDO change processor for SNOMED CT import process. Does nothing but maps business 
 * IDs to the corresponding unique CDO IDs for all new objects. And persists {@link CodeSystem} and {@link CodeSystemVersion}
 * to the 'real' index directory.
 */
public class SnomedImportCDOChangeProcessor implements ICDOChangeProcessor {

	private static final String NAME = "SNOMED CT import index change processor.";
	private static final String CHANGE_DESCRIPTION = "";

	private Collection<CDOObject> newComponents;
	private final IBranchPath branchPath;
	private String userId;
	private final ImportIndexServerService indexService;

	public SnomedImportCDOChangeProcessor(final ImportIndexServerService indexService, final IBranchPath branchPath) {
		this.indexService = indexService;
		this.branchPath = branchPath;
	}
	
	/* (non-Javadoc)
	 * @see com.b2international.snowowl.datastore.ICDOChangeProcessor#process(com.b2international.snowowl.datastore.ICDOCommitChangeSet)
	 */
	@Override
	public void process(final ICDOCommitChangeSet commitChangeSet) throws SnowowlServiceException {
		newComponents = commitChangeSet.getNewComponents();
		userId = commitChangeSet.getUserId();
		
		for (final CDOObject newObject : newComponents) {
			
			final EClass eClass = newObject.eClass();

			//concepts, descriptions and relationships
			if (SnomedPackage.eINSTANCE.getComponent().isSuperTypeOf(eClass)) {
				indexService.registerComponent(String.valueOf(newObject.eGet(SnomedPackage.eINSTANCE.getComponent_Id())), newObject.cdoID());
				
			//reference sets
			} else if (SnomedRefSetPackage.eINSTANCE.getSnomedRefSet().isSuperTypeOf(eClass)) {
				indexService.registerRefSet(String.valueOf(newObject.eGet(SnomedRefSetPackage.eINSTANCE.getSnomedRefSet_IdentifierId())), newObject.cdoID());
				
			//reference set members
			} else if (SnomedRefSetPackage.eINSTANCE.getSnomedRefSetMember().isSuperTypeOf(eClass)) {
				indexService.registerMember(String.valueOf(newObject.eGet(SnomedRefSetPackage.eINSTANCE.getSnomedRefSetMember_Uuid())), newObject.cdoID());
				
			//code system goes to the 'real' index 
			} else if (SnomedPackage.eINSTANCE.getSnomedRelease().isSuperTypeOf(eClass)) {
				final SnomedIndexServerService mainIndexService = (SnomedIndexServerService) getServiceForClass(SnomedIndexService.class);
				mainIndexService.index(branchPath, new SnomedReleaseIndexMappingStrategy((SnomedRelease) newObject));
				
			//new version as well
			} else if (SnomedPackage.eINSTANCE.getSnomedVersion().isSuperTypeOf(eClass)) {
				final SnomedIndexServerService mainIndexService = (SnomedIndexServerService) getServiceForClass(SnomedIndexService.class);
				mainIndexService.index(branchPath, new SnomedVersionIndexMappingStrategy((SnomedVersion) newObject));
				
			}
			
		} 
		
	}

	/* (non-Javadoc)
	 * @see com.b2international.snowowl.datastore.ICDOChangeProcessor#commit()
	 */
	@Override
	public void commit() throws SnowowlServiceException {
		indexService.commit();
	}

	/* (non-Javadoc)
	 * @see com.b2international.snowowl.datastore.ICDOChangeProcessor#prepareCommit()
	 */
	@Override
	public void prepareCommit() throws SnowowlServiceException {
		throw new UnsupportedOperationException("Not implemented yet.");
	}

	/* (non-Javadoc)
	 * @see com.b2international.snowowl.datastore.ICDOChangeProcessor#rollback()
	 */
	@Override
	public void rollback() throws SnowowlServiceException {
		indexService.rollback();
	}

	/* (non-Javadoc)
	 * @see com.b2international.snowowl.datastore.ICDOChangeProcessor#getName()
	 */
	@Override
	public String getName() {
		return NAME;
	}

	/* (non-Javadoc)
	 * @see com.b2international.snowowl.datastore.ICDOChangeProcessor#getUserId()
	 */
	@Override
	public String getUserId() {
		return userId;
	}

	/* (non-Javadoc)
	 * @see com.b2international.snowowl.datastore.ICDOChangeProcessor#getBranchPath()
	 */
	@Override
	public IBranchPath getBranchPath() {
		return branchPath;
	}

	/* (non-Javadoc)
	 * @see com.b2international.snowowl.datastore.ICDOChangeProcessor#getChangeDescription()
	 */
	@Override
	public String getChangeDescription() {
		return CHANGE_DESCRIPTION;
	}

	/* (non-Javadoc)
	 * @see com.b2international.snowowl.datastore.ICDOChangeProcessor#hadChangesToProcess()
	 */
	@Override
	public boolean hadChangesToProcess() {
		return !CompareUtils.isEmpty(newComponents);
	}

	/* (non-Javadoc)
	 * @see com.b2international.snowowl.datastore.ICDOChangeProcessor#afterCommit()
	 */
	@Override
	public void afterCommit() {
		//ignored
	}

}
