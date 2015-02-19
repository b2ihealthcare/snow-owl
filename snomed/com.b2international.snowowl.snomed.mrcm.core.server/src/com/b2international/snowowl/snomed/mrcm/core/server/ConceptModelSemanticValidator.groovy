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
package com.b2international.snowowl.snomed.mrcm.core.server;

import static com.b2international.commons.StringUtils.isEmpty
import static com.b2international.snowowl.datastore.cdo.CDOUtils.apply
import static com.b2international.snowowl.snomed.datastore.SnomedDatastoreActivator.REPOSITORY_UUID
import static com.b2international.snowowl.snomed.mrcm.core.ConceptModelUtils.*
import static com.google.common.base.Preconditions.checkNotNull
import static com.google.common.collect.Sets.newHashSet

import com.b2international.commons.tree.NoopTreeVisitor
import com.b2international.commons.tree.emf.EObjectWalker
import com.b2international.commons.tree.emf.EObjectWalker.EObjectContainmentTreeNodeProvider
import com.b2international.snowowl.core.ApplicationContext
import com.b2international.snowowl.datastore.server.snomed.index.init.ImportIndexServerService
import com.b2international.snowowl.snomed.datastore.SnomedConceptLookupService
import com.b2international.snowowl.snomed.mrcm.ConstraintBase

/**
 * {@link ConceptModel} semantic validator.
 */
class ConceptModelSemanticValidator {

	/**Returns with a collection of {@link ConstraintBase} which cannot be interpreted on the given branch.*/
	public Collection<ConstraintBase> validateModel(def branchPath, def model) {
		checkNotNull(branchPath, "branchPath")
		checkNotNull(model, "model")
		
		def invalidConstraints = [] as Set
		def existsInIndex = { bp, id -> new SnomedConceptLookupService().exists(bp, id) }
		def exists = { bp, id ->
			def exist = existsInIndex(bp, id)
			if (!exist) {
				if (ApplicationContext.getInstance().exists(ImportIndexServerService.class)) {
					def importIndexService = (ImportIndexServerService) ApplicationContext.getInstance().getService(ImportIndexServerService.class)
					exist = importIndexService.componentExists(id)
				}
			}
			return exist
		}
		def accept = { node -> 
			CONCEPT_ID_FEATURES.contains(node?.feature) && 
			!isEmpty(node.featureValue) && 
			!exists(branchPath, node.featureValue)}
		
		new EObjectWalker(new NoopTreeVisitor() {
			 void doVisit(def node) {
				 if (accept(node)) {
					 invalidConstraints << getContainerConstraint(node.eObject)
				 }
			 }
			
		}, new EObjectContainmentTreeNodeProvider()).walk(model)
	
		
		return invalidConstraints.asImmutable()
		
	}
	
	
	
	
}