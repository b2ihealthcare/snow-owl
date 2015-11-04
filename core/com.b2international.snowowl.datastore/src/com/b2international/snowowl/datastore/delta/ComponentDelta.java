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
package com.b2international.snowowl.datastore.delta;

import static com.google.common.base.Strings.nullToEmpty;
import static com.google.common.primitives.Shorts.compare;

import java.io.Serializable;

import javax.annotation.Nullable;

import com.b2international.commons.Change;
import com.b2international.commons.ChangeKind;
import com.b2international.commons.pcj.LongSets;
import com.b2international.snowowl.core.api.IBranchPath;
import com.b2international.snowowl.core.api.component.IconIdProvider;
import com.b2international.snowowl.core.api.component.LabelProvider;
import com.b2international.snowowl.core.api.component.TerminologyComponentIdProvider;
import com.google.common.base.Objects;
import com.google.common.base.Preconditions;

import bak.pcj.set.LongSet;

/**
 * Abstract representation of a component modification.
 */
public abstract class ComponentDelta implements IChangedComponentCDOIDs, Serializable, Change, IconIdProvider<String>, LabelProvider, TerminologyComponentIdProvider, Comparable<ComponentDelta> {

	private static final long serialVersionUID = 2026011761057989030L;

	private final String id;
	private final String label;
	private final long cdoId;
	private final short terminologyComponentId;
	private final ChangeKind change;
	private final LongSet relatedCdoIds;
	private final IBranchPath branchPath;
	private final String iconId;
	private final String codeSystemOID;

	protected ComponentDelta(final String id, final long cdoId, final IBranchPath branchPath, final String label, final String iconId, 
			final short terminologyComponentId, 
			final String codeSystemOID) {

		this(id, cdoId, branchPath, label, iconId, terminologyComponentId, codeSystemOID, ChangeKind.UNCHANGED);
	}

	protected ComponentDelta(final String id, final long cdoId, final IBranchPath branchPath, final String label, final String iconId, 
			final short terminologyComponentId, 
			final String codeSystemOID, 
			final ChangeKind change) {

		this.codeSystemOID = codeSystemOID;
		this.id = Preconditions.checkNotNull(id, "ID argument cannot be null.");
		this.cdoId = cdoId;
		this.branchPath = Preconditions.checkNotNull(branchPath, "Branch path argument cannot be null.");
		this.label = Preconditions.checkNotNull(label, "Label argument cannot be null.");
		this.iconId = iconId;
		this.change = Preconditions.checkNotNull(change, "Component change kind argument cannot be null.");
		this.terminologyComponentId = terminologyComponentId;
		this.relatedCdoIds = LongSets.newLongSet(cdoId);
	}

	@Override
	public long getCdoId() {
		return cdoId;
	}

	@Override
	public LongSet getRelatedCdoIds() {
		return relatedCdoIds;
	}

	@Override
	public boolean isDirty() {
		return change.isDirty();
	}

	@Override
	public boolean isNew() {
		return change.isNew();
	}

	@Override
	public boolean isDeleted() {
		return change.isDeleted();
	}

	@Override
	public boolean hasChanged() {
		return change.hasChanged();
	}

	@Override
	public String getLabel() {
		return label;
	}

	@Override
	public String getIconId() {
		return iconId;
	}

	@Override
	public short getTerminologyComponentId() {
		return terminologyComponentId;
	}

	/**
	 * Returns with the {@link ChangeKind change} of the delta.
	 */
	public ChangeKind getChange() {
		return change;
	}

	/**
	 * Returns with the unique ID of the component delta.
	 */
	public String getId() {
		return id;
	}

	/**
	 * Returns with the {@link IBranchPath branch path} uniquely identifying the branch where the change represented 
	 * by the current instance was made.
	 */
	public IBranchPath getBranchPath() {
		return branchPath;
	}

	/**
	 * Returns with the code system OID of the component delta.
	 * <p>
	 * Can be {@code null}.
	 */
	@Nullable public String getCodeSystemOID() {
		return codeSystemOID;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((branchPath == null) ? 0 : branchPath.hashCode());
		result = prime * result + (int) (cdoId ^ (cdoId >>> 32));
		result = prime * result + ((codeSystemOID == null) ? 0 : codeSystemOID.hashCode());
		return result;
	}

	@Override
	public boolean equals(final Object obj) {
		if (this == obj) { return true; }
		if (obj == null) { return false; }
		if (getClass() != obj.getClass()) { return false; }

		final ComponentDelta other = (ComponentDelta) obj;

		if (branchPath == null && other.branchPath != null) { return false; }
		if (!branchPath.equals(other.branchPath)) { return false; }
		if (cdoId != other.cdoId) { return false; }
		if (codeSystemOID == null && other.codeSystemOID != null) { return false; }
		if (!codeSystemOID.equals(other.codeSystemOID)) { return false; }

		return true;
	}

	@Override
	public int compareTo(final ComponentDelta other) {
		if (null == other) {
			return -1;
		}

		int result = compareId(other);
		if (result != 0) {
			return result;
		}

		result = compareTerminologyComponentId(other);
		if (result != 0) {
			return result;
		}

		result = compareCodeSystemOID(other);
		if (result != 0) {
			return result;
		}

		return result;
	}

	private int compareId(final ComponentDelta other) {
		return null == other ? -1 : nullToEmpty(id).compareTo(nullToEmpty(other.id));
	}

	private int compareTerminologyComponentId(final ComponentDelta other) {
		return null == other ? -1 : compare(terminologyComponentId, other.terminologyComponentId);
	}

	private int compareCodeSystemOID(final ComponentDelta other) {
		return null == other ? -1 : nullToEmpty(codeSystemOID).compareTo(nullToEmpty(other.codeSystemOID));
	}

	@Override
	public String toString() {
		return Objects.toStringHelper(this)
				.add("ID", id)
				.add("CDO ID", cdoId)
				.add("Branch path", branchPath.getPath())
				.add("Label", label)
				.add("Icon ID", iconId)
				.add("Terminology component ID", terminologyComponentId)
				.add("Code system OID", codeSystemOID)
				.add("Change type", change)
				.toString();
	}
}
