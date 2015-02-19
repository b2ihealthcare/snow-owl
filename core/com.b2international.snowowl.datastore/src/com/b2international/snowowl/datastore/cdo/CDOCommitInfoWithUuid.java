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
package com.b2international.snowowl.datastore.cdo;

import org.eclipse.emf.cdo.common.commit.CDOCommitInfo;

import com.google.common.base.Preconditions;
import com.google.common.base.Strings;

/**
 * {@link ICDOCommitInfoWithUuid} implementation.
 *
 */
@SuppressWarnings("restriction")
public class CDOCommitInfoWithUuid extends org.eclipse.emf.cdo.internal.common.commit.DelegatingCommitInfo implements ICDOCommitInfoWithUuid {

	private final CDOCommitInfo delegate;
	private final String uuid;
	private final String modifiedComment; 

	public CDOCommitInfoWithUuid(final CDOCommitInfo commitInfo) {
		this.delegate = Preconditions.checkNotNull(commitInfo, "Commit info argument cannot be null.");
		
		if (commitInfo instanceof ICDOCommitInfoWithUuid) {
			uuid = ((ICDOCommitInfoWithUuid) commitInfo).getUuid();
			modifiedComment = commitInfo.getComment(); // already modified
		} else {
			uuid = CDOCommitInfoUtils.getUuid(commitInfo);
			modifiedComment = CDOCommitInfoUtils.removeUuidPrefix(Strings.nullToEmpty(commitInfo.getComment()));
		}
		
	}
	
	/* (non-Javadoc)
	 * @see com.b2international.snowowl.datastore.cdo.ICDOCommitInfoWithUuid#getUuid()
	 */
	@Override
	public String getUuid() {
		return uuid;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.emf.cdo.internal.common.commit.DelegatingCommitInfo#getComment()
	 */
	@Override
	public String getComment() {
		return modifiedComment;
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.emf.cdo.internal.common.commit.DelegatingCommitInfo#getDelegate()
	 */
	@Override
	protected CDOCommitInfo getDelegate() {
		return delegate;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((uuid == null) ? 0 : uuid.hashCode());
		return result;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(final Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (!(obj instanceof CDOCommitInfoWithUuid))
			return false;
		final CDOCommitInfoWithUuid other = (CDOCommitInfoWithUuid) obj;
		if (uuid == null) {
			if (other.uuid != null)
				return false;
		} else if (!uuid.equals(other.uuid))
			return false;
		return true;
	}
	
	

}