/*
 * Copyright 2011-2017 B2i Healthcare Pte Ltd, http://b2i.sg
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

import org.eclipse.emf.cdo.common.revision.delta.CDOAddFeatureDelta
import org.eclipse.emf.cdo.common.revision.delta.CDOListFeatureDelta
import org.eclipse.emf.cdo.common.revision.delta.CDORemoveFeatureDelta
import org.eclipse.emf.cdo.internal.common.revision.delta.CDOListFeatureDeltaImpl
import org.eclipse.emf.cdo.internal.common.revision.delta.CDORemoveFeatureDeltaImpl
import org.eclipse.emf.cdo.spi.common.revision.InternalCDOList
import org.eclipse.emf.cdo.spi.common.revision.InternalCDORevision
import org.eclipse.emf.cdo.spi.common.revision.InternalCDORevisionDelta;
import org.eclipse.emf.ecore.EReference
//import org.eclipse.emf.cdo.internal.server.TransactionCommitContext
//TransactionCommitContext ctx = null

def newCDOIds= ctx.newObjects.collect { it.getID() }
def detachedCDOIds = ctx.detachedObjects.collect {it}

println("Processing commit on branch ${ctx.getBranchPoint().getBranch().getID()} - ${ctx.getBranchPoint().getBranch().getName()}")
println("Dirty objects before:")
ctx.dirtyObjectDeltas.each { println("\t" + it) }

ctx.dirtyObjectDeltas.each{ delta ->
	
	def listFeatureDeltas = delta.getFeatureDeltas()
		.findAll { it instanceof CDOListFeatureDelta && it.getFeature().many  && (it.getFeature() as EReference).isContainment() }

	def dirtyObjectRevision = ctx.getTransaction().getRevision(delta.getID()) as InternalCDORevision
			
	listFeatureDeltas.each { CDOListFeatureDelta listFeature ->
		
		def InternalCDOList originalList = dirtyObjectRevision.getList(listFeature.getFeature()) as InternalCDOList
		def originalListCopy = originalList.clone(listFeature.getFeature().eType)
		
		def fixedListFeature = new CDOListFeatureDeltaImpl(listFeature.getFeature())
		delta.getFeatureDeltaMap().remove(listFeature.getFeature())
		
		listFeature.getListChanges().each { listChange ->
			
			if (listChange instanceof CDOAddFeatureDelta && newCDOIds.contains(listChange.getValue())) {
				
				if (listChange.index != originalListCopy.size()) {
					println("Changing add feature delta from index $listChange.index to ${originalListCopy.size()}: " + listChange)
				}
				
				listChange.index = originalListCopy.size()
				originalListCopy.add(listChange.getValue())
				fixedListFeature.add(listChange)
				
			} else if (listChange instanceof CDORemoveFeatureDelta && detachedCDOIds.contains(listChange.getValue())) {
				
				def removeIndex = originalListCopy.indexOf(listChange.getValue())

				if (listChange.index != removeIndex) {
					println("Changing remove feature delta from index $listChange.index to $removeIndex: " + listChange)
				}
				
				listChange.index = removeIndex
				originalListCopy.remove(removeIndex)
				fixedListFeature.add(listChange)
				
			}
			
		}
		
		def visited = [] as Set
		
		originalListCopy.reverse().eachWithIndex { element, index ->
			if (!visited.add(element)) {
				fixedListFeature.add(new CDORemoveFeatureDeltaImpl(listFeature.getFeature(), originalListCopy.size() - 1 - index))
			}
		}
		
		delta.addFeatureDelta(fixedListFeature)
	}
	
}

ctx.dirtyObjectDeltas = ctx.dirtyObjectDeltas.findAll {
	if (it.getFeatureDeltas().isEmpty()) {
		println("Removing empty feature delta: " + it)
	}  else {
		return true
	}
} as InternalCDORevisionDelta[]

println("Dirty objects after:")
ctx.dirtyObjectDeltas.each { println("\t" + it) }
