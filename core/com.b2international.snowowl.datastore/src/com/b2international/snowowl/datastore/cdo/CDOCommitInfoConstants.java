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

import java.util.Comparator;
import java.util.regex.Pattern;

import org.eclipse.emf.cdo.common.commit.CDOCommitInfo;
import org.eclipse.emf.cdo.server.IRepository;

import com.google.common.base.Function;

/**
 * Constants for {@link CDOCommitInfo commit info}s.
 */
public abstract class CDOCommitInfoConstants {

	/**Initializer commit comment. {@value}*/ 
	public static final String INITIALIZER_COMMIT_COMMENT = "<initialize>";
	
	/**System user ID. {@value}*/
	public static final String SYSTEM_USER_ID = IRepository.SYSTEM_USER_ID;
	
	/**{@link Pattern Pattern} for a UUID.*/
	public static final Pattern UUID_PATETRN = Pattern.compile("[0-9a-f]{8}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{12}");
	
	/**Comparator for comparing {@link CDOCommitInfo}s by their {@link CDOCommitInfo#getTimeStamp() timestamp} attribute.*/
	public static final Comparator<CDOCommitInfo> CDO_COMMIT_INFO_COMPARATOR = new Comparator<CDOCommitInfo>() {
		@Override public int compare(final CDOCommitInfo o1, final CDOCommitInfo o2) {
			return (int) (o1.getTimeStamp() - o2.getTimeStamp());
		}
	};
	
	/**Function for transforming {@link CDOCommitInfo commit info } instances to serializable instances.*/
	public static final Function<CDOCommitInfo, CDOCommitInfo> TO_SERIALIZABLE_FUNCTION = new Function<CDOCommitInfo, CDOCommitInfo>() {
		@Override public CDOCommitInfo apply(final CDOCommitInfo commitInfo) {
			return commitInfo instanceof SerializableCDOCommitInfo ? commitInfo : new SerializableCDOCommitInfo(commitInfo);
		}
	};
	
	private CDOCommitInfoConstants() { /*suppress instantiation*/ }
	
}