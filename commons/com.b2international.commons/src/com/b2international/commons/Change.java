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
package com.b2international.commons;

import static com.google.common.base.Preconditions.checkNotNull;

import com.google.common.base.Predicate;

/**
 * Represents a change based on the {@link ChangeKind change kind} enumeration.
 *
 */
public interface Change {

	/**
	 * Returns {@code true} if the current change represents a modification. Otherwise {@code false}. 
	 */
	public boolean isDirty();

	/**
	 * Returns {@code true} if the change represents an addition. Otherwise returns {@code false}. 
	 */
	public boolean isNew();

	/**
	 * Returns {@code true} if the change is representing a deletion. Otherwise returns with {@code false}. 
	 */
	public boolean isDeleted();

	/**
	 * Returns {@code true} if the change has modification.
	 * <p>More precisely one of the followings returns with {@code true}:
	 * {@link #isDirty()}, {@link #isNew()} or {@link #isDeleted()}.; 
	 */
	public boolean hasChanged();

	/**
	 * Returns with the {@link ChangeKind change} of the current change.
	 */
	public ChangeKind getChange();
	
	/**Dirty predicate. Gives {@code true} only and if only the {@link Change#isDirty()} is {@code true}. Otherwise {@code false}.*/
	Predicate<Change> DIRTY_PREDICATE = new Predicate<Change>() {
		public boolean apply(final Change change) {
			return checkNotNull(change, "change").isDirty();
		}
	};
	
	/**Changed predicate. Gives {@code true} only and if only the {@link Change#hasChanged()} is {@code true}. Otherwise {@code false}.*/
	Predicate<Change> HAS_CHANGED_PREDICATE = new Predicate<Change>() {
		public boolean apply(final Change change) {
			return checkNotNull(change, "change").hasChanged();
		}
	};
	
	/**New predicate. Gives {@code true} only and if only the {@link Change#isNew()} is {@code true}. Otherwise {@code false}.*/
	Predicate<Change> IS_NEW_PREDICATE = new Predicate<Change>() {
		public boolean apply(final Change change) {
			return checkNotNull(change, "change").isNew();
		}
	};
	
	/**Deleted predicate. Gives {@code true} only and if only the {@link Change#isDeleted()} is {@code true}. Otherwise {@code false}.*/
	Predicate<Change> IS_DELETED_PREDICATE = new Predicate<Change>() {
		public boolean apply(final Change change) {
			return checkNotNull(change, "change").isDeleted();
		}
	};
	
}