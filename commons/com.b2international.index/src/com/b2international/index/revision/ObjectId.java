/*
 * Copyright 2018 B2i Healthcare Pte Ltd, http://b2i.sg
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

import java.util.Objects;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonValue;

/**
 * @since 7.0
 */
public final class ObjectId {

	static final String ROOT = "-1";
	
	private final String type;
	private final String id;

	private ObjectId(String type, String id) {
		this.type = type;
		this.id = id;
	}
	
	public String type() {
		return type;
	}
	
	public String id() {
		return id;
	}
	
	@JsonIgnore
	public boolean isRoot() {
		return ROOT.equals(id);
	}
	
	@Override
	public int hashCode() {
		return Objects.hash(type, id);
	}
	
	@Override
	public boolean equals(Object obj) {
		if (this == obj) return true;
		if (obj == null) return false;
		if (getClass() != obj.getClass()) return false;
		ObjectId other = (ObjectId) obj;
		return Objects.equals(type, other.type)
				&& Objects.equals(id, other.id);
	}
	
	@JsonValue
	@Override
	public String toString() {
		return String.format("%s/%s", type, id);
	}
	
	@JsonCreator
	public static ObjectId fromString(String value) {
		final String[] parts = value.split("/");
		return of(parts[0], parts[1]);
	}

	public static ObjectId of(String type, String id) {
		return new ObjectId(type, id);
	}
	
	public static ObjectId rootOf(String type) {
		return of(type, ROOT);
	}

}
