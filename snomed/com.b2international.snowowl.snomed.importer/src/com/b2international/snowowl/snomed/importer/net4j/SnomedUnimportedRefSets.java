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
package com.b2international.snowowl.snomed.importer.net4j;

import java.util.List;

import com.google.common.collect.Lists;

/**
 * Contains information about the imported reference set and stores the
 * unimported members from the reference set in a {@link StoreRefSetMember}
 * format.
 * 
 * 
 */
public class SnomedUnimportedRefSets {

	private final String fileName;
	private final String refSetName;
	private final String nameSpace;
	private final String effectiveTime;
	private List<StoreRefSetMember> unimportedRefSetMembers;

	public SnomedUnimportedRefSets(String fileName, String refSetName, String nameSpace, String effectiveTime) {
		this.fileName = fileName;
		this.refSetName = refSetName;
		this.nameSpace = nameSpace;
		this.effectiveTime = effectiveTime;
		this.unimportedRefSetMembers = Lists.newArrayList();
	}

	public String getRefSetName() {
		return refSetName;
	}

	public String getNameSpace() {
		return nameSpace;
	}

	public String getEffectiveTime() {
		return effectiveTime;
	}

	public List<StoreRefSetMember> getUnimportedRefSetMembers() {
		return unimportedRefSetMembers;
	}

	/**
	 * Creates a {@link StoreRefSetMember} from the given parameters and stores
	 * it in the {@link SnomedUnimportedRefSets}.
	 * 
	 * @param reason
	 *            why the refset member was not imported
	 * @param conceptId
	 *            the concept id for the refset member
	 * @param fullySpecifiedName
	 *            the fully specified name for the refset member
	 */
	public void addRefSetMember(String reason, String conceptId, String fullySpecifiedName) {
		StoreRefSetMember storeRefSetMembers = new StoreRefSetMember(this, reason, conceptId, fullySpecifiedName);
		unimportedRefSetMembers.add(storeRefSetMembers);
	}

	public String getFileName() {
		return fileName;
	}

	/**
	 * Contains information about a reference set member.
	 */
	public final class StoreRefSetMember {
		private String reason;
		private String conceptId;
		private String fullySpecifiedName;
		private SnomedUnimportedRefSets parent;

		private StoreRefSetMember(SnomedUnimportedRefSets parent, String reason, String conceptId, String fullySpecifiedName) {
			this.parent = parent;
			this.reason = reason;
			this.conceptId = conceptId;
			this.fullySpecifiedName = fullySpecifiedName;
		}

		public String getReason() {
			return reason;
		}

		public String getConceptId() {
			return conceptId;
		}

		public String getFullySpecifiedName() {
			return fullySpecifiedName;
		}

		public SnomedUnimportedRefSets getParent() {
			return parent;
		}

	}

}