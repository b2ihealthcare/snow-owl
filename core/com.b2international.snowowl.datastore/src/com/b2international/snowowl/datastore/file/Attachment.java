/*
 * Copyright 2019 B2i Healthcare Pte Ltd, http://b2i.sg
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
package com.b2international.snowowl.datastore.file;

import java.io.Serializable;
import java.util.Objects;
import java.util.UUID;

import javax.validation.constraints.NotNull;

import org.hibernate.validator.constraints.NotEmpty;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Descriptor for a file registry attachment encapsulating the uuid - file name pair
 * @since 6.17
 */
public class Attachment implements Serializable {
	
	private static final long serialVersionUID = 1L;

	/*
	 * Stored as String as it is serialized between the server and client
	 */
	@NotNull
	private UUID uuid;
	
	@NotEmpty
	private String name;

	/**
	 * Creates an attachment descriptor
	 * @param uuid of the attachment
	 * @param name of the attachment
	 */
	public Attachment(UUID uuid, String name) {
		this.uuid = uuid;
		this.name = name;
	}
	
	/**
	 * Creates an attachment descriptor
	 * @param uuid as a string of the attachment
	 * @param name of the attachment
	 */
	@JsonCreator
	public Attachment(@JsonProperty("uuid") String uuid, @JsonProperty("name") String name) {
		this(UUID.fromString(uuid), name);
	}
	
	/**
	 * Creates an attachment with a random UUID and the given name
	 * @param name
	 */
	public Attachment(String name) {
		this(UUID.randomUUID(), name);
	}
	
	public UUID getUuid() {
		return uuid;
	}
	
	@JsonIgnore
	public String getUuidString() {
		return uuid.toString();
	}
	
	public String getName() {
		return name;
	}

	@Override
	public int hashCode() {
		return Objects.hash(uuid);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Attachment other = (Attachment) obj;
		if (uuid == null) {
			if (other.uuid != null)
				return false;
		} else if (!uuid.equals(other.uuid))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "Attachment [uuid=" + uuid + ", name=" + name + "]";
	}

}
