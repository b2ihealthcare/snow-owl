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
package com.b2international.snowowl.core;

import org.eclipse.core.runtime.QualifiedName;
import org.eclipse.core.runtime.jobs.Job;

/**
 * Abstract superclass which provides support for common functionality in Snow
 * Owl's jobs: a {@link #belongsTo(Object)} implementation with an Object
 * family, and a type-sensitive getter for job properties.
 * 
 */
public abstract class SimpleFamilyJob extends Job {

	protected final Object family;

	public SimpleFamilyJob(String name, Object family) {
		super(name);
		this.family = family;
	}
	
	public SimpleFamilyJob(String name, Object family, boolean isUserJob, int priority) {
		this(name, family);
		setUser(isUserJob);
		setPriority(priority);
	}

	protected <T> T getProperty(QualifiedName key, Class<T> clazz) {
		return clazz.cast(getProperty(key));
	}
	
	@Override
	public boolean belongsTo(Object family) {
		return (this.family == null || family == null) ? false : this.family.equals(family);
	}
}