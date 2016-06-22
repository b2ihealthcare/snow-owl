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
package com.b2international.snowowl.snomed.datastore.services;

import java.io.Serializable;
import java.util.Collection;
import java.util.Map;
import java.util.Set;

import com.b2international.collections.longs.LongSet;
import com.b2international.snowowl.core.api.IBranchPath;
import com.b2international.snowowl.snomed.datastore.SnomedModuleDependencyRefSetMemberFragment;
import com.b2international.snowowl.snomed.datastore.snor.SnomedConstraintDocument;
import com.b2international.snowowl.snomed.mrcm.HierarchyInclusionType;
import com.b2international.snowowl.snomed.snomedrefset.DataType;
import com.google.common.base.Function;
import com.google.common.base.Preconditions;
import com.google.common.collect.Multimap;

/**
 * Interface for retrieving information about SNOMED&nbsp;CT core components on the server side.
 * 
 */
public interface ISnomedComponentService {
	
	/**
	 * Returns with the available concrete domain data type labels for a specified concrete domain data type.
	 * @param branchPath
	 * @param dataType the data type. E.g.: {@code BOOLEAN} or {@code DECIMAL}.
	 * @return a set of concrete domain data type labels for a specified data type.
	 */
	Set<String> getAvailableDataTypeLabels(IBranchPath branchPath, final DataType dataType);

	/**
	 * Returns with a collection of the reference set member's referenced component storage keys.  
	 * 
	 * @param branchPath the branch path.
	 * @param refSetId the Id of the reference set.
	 * @param referencedComponentType the type of the reference set member's referenced component.
	 * @return a collection of component storage keys.
	 */
	LongSet getComponentByRefSetIdAndReferencedComponent(final IBranchPath branchPath, final String refSetId, final short referencedComponentType);
	
	/**
	 * @param branchPath
	 * @return
	 */
	Map<HierarchyInclusionType, Multimap<String, SnomedConstraintDocument>> getPredicates(final IBranchPath branchPath);
	
	/**
	 * @param branchPath
	 * @param conceptId
	 * @return
	 */
	Map<String, Multimap<String, String>> getDescriptionPreferabilityMap(IBranchPath branchPath, String conceptId);
	
	/**
	 * Returns with all existing {@link SnomedModuleDependencyRefSetMemberFragment module dependency reference set member}s from the underling ontology.
	 * @param branchPath the branch path.
	 * @return a collection of existing module dependency reference set members.
	 */
	Collection<SnomedModuleDependencyRefSetMemberFragment> getExistingModules(final IBranchPath branchPath);
	
	/**
	 * Returns with a set of concept storage keys that have to be inactivated when retiring the concept concepts
	 * argument.
	 * @param branchPath the branch path for the calculation.
	 * @param focusConceptIds the concept IDs to retire with their descendants.
	 * @return a collection of concept storage keys to inactivate.
	 */
	LongSet getSelfAndAllSubtypeStorageKeysForInactivation(final IBranchPath branchPath, final String... focusConceptIds);
	
	/**
	 * Serializable representation of a component ID and storage key pair.
	 */
	public static final class IdStorageKeyPair implements Serializable {
		
		/**Function for extracting the component ID from the {@link IdStorageKeyPair}.*/
		public static final Function<IdStorageKeyPair, String> GET_ID_FUNCTION = new Function<ISnomedComponentService.IdStorageKeyPair, String>() {
			private static final String MSG = "ID with storage key pair argument cannot be null.";
			@Override public String apply(final IdStorageKeyPair idStorageKeyPair) {
				return Preconditions.checkNotNull(idStorageKeyPair, MSG).id;
			}
		};
		
		private static final long serialVersionUID = -2748053756520925845L;
		private final String id;
		private final long storageKey;
		/**Creates a new instance.*/
		public IdStorageKeyPair(final String id, final long storageKey) { this.id = id; this.storageKey = storageKey; }
		/**Returns with the terminology specific component ID.<p>Clients should not parse it to long as it could be UUID as well.*/
		public String getId() { return id; }
		/**The repository specific primary storage key of the component.*/
		public long getStorageKey() { return storageKey; }
		
		@Override
		public int hashCode() {
			final int prime = 31;
			int result = 1;
			result = prime * result + ((id == null) ? 0 : id.hashCode());
			result = prime * result + (int) (storageKey ^ (storageKey >>> 32));
			return result;
		}
		
		@Override
		public boolean equals(final Object obj) {
			if (this == obj)
				return true;
			if (obj == null)
				return false;
			if (!(obj instanceof IdStorageKeyPair))
				return false;
			final IdStorageKeyPair other = (IdStorageKeyPair) obj;
			if (id == null) {
				if (other.id != null)
					return false;
			} else if (!id.equals(other.id))
				return false;
			if (storageKey != other.storageKey)
				return false;
			return true;
		}
		
	}

}