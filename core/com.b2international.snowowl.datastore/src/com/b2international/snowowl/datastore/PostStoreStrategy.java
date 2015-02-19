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
package com.b2international.snowowl.datastore;

import javax.annotation.Nullable;

import org.eclipse.emf.cdo.common.commit.CDOCommitInfo;


/**
 * Abstract strategy superclass for actions that should be done when receiving a post-store update notification.
 *
 * @param <E> the editor type
 */
public abstract class PostStoreStrategy<E> {

	@Nullable private final CDOCommitInfo commitInfo;
	
	protected PostStoreStrategy(@Nullable final CDOCommitInfo commitInfo) {
		this.commitInfo = commitInfo;
	}
	
	public static final PostStoreStrategy<Object> NONE = new PostStoreStrategy<Object>(null) {
		// Empty implementation
	};
	
	/**
	 * Displays a message dialog with details about the update. The default implementation is empty.
	 */
	public void notifyUser() {
		// Subclasses should override
	}
	
	public void refreshEditor(E editor) {
		// Subclasses should override
	}
	
	/**
	 * Returns with the commit info, if any. Can be {@code null}.
	 * @return the commit info.
	 */
	@Nullable protected CDOCommitInfo getCommitInfo() {
		return commitInfo;
	}
	
	
}