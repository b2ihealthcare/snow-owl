/*
 * Copyright 2011-2016 B2i Healthcare Pte Ltd, http://b2i.sg
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
package com.b2international.index.revision;

import java.util.Collection;
import java.util.Objects;
import java.util.Set;

import com.google.common.collect.ImmutableSet;

/**
 * @since 4.7
 */
public class RevisionBranch {
	
	public static final String MAIN_PATH = "MAIN";
	public static final String SEPARATOR = "/";
	
	private final String path;
	private final int segmentId;
	private final Set<Integer> segments;

	public RevisionBranch(String path, int segmentId, Collection<Integer> segments) {
		this.path = path;
		this.segmentId = segmentId;
		this.segments = ImmutableSet.copyOf(segments);
	}

	public String path() {
		return path;
	}
	
	public int segmentId() {
		return segmentId;
	}
	
	public Set<Integer> segments() {
		return segments;
	}
	
	@Override
	public int hashCode() {
		return Objects.hash(path, segments);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) return true;
		if (obj == null) return false;
		if (getClass() != obj.getClass()) return false;
		RevisionBranch other = (RevisionBranch) obj;
		return Objects.equals(path, other.path) && Objects.equals(segments, other.segments);
	}
	
}
