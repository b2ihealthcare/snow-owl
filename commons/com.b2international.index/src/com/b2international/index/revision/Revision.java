/*
 * Copyright 2011-2018 B2i Healthcare Pte Ltd, http://b2i.sg
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

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Preconditions.checkState;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import com.b2international.index.Script;
import com.b2international.index.mapping.DocumentMapping;
import com.b2international.index.revision.StagingArea.RevisionPropertyDiff;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.common.base.MoreObjects;
import com.google.common.base.MoreObjects.ToStringHelper;

import net.jodah.typetools.TypeResolver;

/**
 * @since 4.7
 */
@Script(name=Revision.UPDATE_REVISED, script=""
		+ "int idx = ctx._source.revised.indexOf(params.oldRevised);"
		+ "if (idx > -1) {"
		+ "    ctx._source.revised.set(idx, params.newRevised);"
		+ "} else {"
		+ "    ctx._source.revised.add(params.newRevised);"
		+ "}")
public abstract class Revision {
	
	public static class Fields {
		public static final String ID = "id";
		public static final String CREATED = "created";
		public static final String REVISED = "revised";
	}

	// scripts
	public static final String UPDATE_REVISED = "updateRevised";

	private String id;
	private RevisionBranchPoint created;
	private List<RevisionBranchPoint> revised = Collections.emptyList();

	public Revision(String id) {
		this.id = checkNotNull(id, "Logical identifier cannot be null");
	}
	
	public final String getId() {
		return id;
	}
	
	final void setCreated(RevisionBranchPoint created) {
		this.created = created;
	}
	
	final void setRevised(List<RevisionBranchPoint> revised) {
		this.revised = revised;
	}
	
	@JsonProperty
	final RevisionBranchPoint getCreated() {
		return created;
	}
	
	@JsonProperty
	final List<RevisionBranchPoint> getRevised() {
		return revised;
	}
	
	@JsonIgnore
	final ObjectId getObjectId() {
		return ObjectId.of(DocumentMapping.getType(getClass()), getId());
	}
	
	/**
	 * Provides high-level component identifier if this component is a subcomponent of the high-level component identifier. This method by default
	 * returns a {@link #ROOT} object ID therefore this object is a ROOT component in a given hierarchy.
	 * 
	 * @return the container identifier
	 * @see #isRoot()
	 */
	@JsonIgnore
	protected ObjectId getContainerId() {
		return ObjectId.rootOf(DocumentMapping.getType(getClass()));
	}
	
	/**
	 * @return whether this component is a root component in a hierarchy or a subcomponent of another component. By default returns <code>true</code>.
	 */
	@JsonIgnore
	final boolean isRoot() {
		return ObjectId.rootOf(DocumentMapping.getType(getClass())).equals(getContainerId());
	}
	
	public Revision withUpdates(DocumentMapping mapping, Collection<RevisionPropertyDiff> propertyDiffs) {
		Revision.Builder<?, ? extends Revision> builder = toBuilder();
		for (RevisionPropertyDiff diff : propertyDiffs) {
			builder = builder._setProperty(mapping, diff.getProperty(), diff.getNewValue()); 
		}
		return builder.build();
	}
	
	@Override
	public final String toString() {
		return doToString().toString();
	}
	
	protected ToStringHelper doToString() {
		return MoreObjects.toStringHelper(this)
				.add(Revision.Fields.ID, id)
				.add(Revision.Fields.CREATED, created)
				.add(Revision.Fields.REVISED, revised);
	}
	
	protected Builder<?, ? extends Revision> toBuilder() {
		throw new UnsupportedOperationException("TODO implement a custom builder for document: " + getClass());
	}
	
	/**
	 * @since 7.0
	 * @param <B>
	 * @param <T>
	 */
	public static abstract class Builder<B extends Builder<B, T>, T extends Revision> {
		
		// XXX only for JSON deserialization
		protected RevisionBranchPoint created;
		protected List<RevisionBranchPoint> revised = Collections.emptyList();
		
		@JsonProperty
		B revised(final List<RevisionBranchPoint> revised) {
			this.revised = revised;
			return getSelf();
		}
		
		@JsonProperty
		B created(final RevisionBranchPoint created) {
			this.created = created;
			return getSelf();
		}
		
		B _setProperty(DocumentMapping mapping, String property, String newValue) {
			final Class<?> fieldType = mapping.getFieldType(property);
			final Object value;
			if (String.class == fieldType) {
				value = newValue;
			} else if (Boolean.class == fieldType || boolean.class == fieldType) {
				value = Boolean.valueOf(newValue);
			} else if (Short.class == fieldType || short.class == fieldType) {
				value = Short.valueOf(newValue);
			} else if (Long.class == fieldType || long.class == fieldType) {
				value = Long.valueOf(newValue);
			} else {
				throw new UnsupportedOperationException("TODO reflective property setter is not supported for property: " + property + " > " + fieldType);
			}
			
			for (Method m : getClass().getMethods()) {
				if (m.getName().equals(property)) {
					try {
						m.invoke(this, value);
					} catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
						throw new RuntimeException(e);
					}
					return getSelf();
				}
			}
			throw new IllegalArgumentException("Couldn't find public builder method for property: " + property);
		}
		
		protected abstract B getSelf();
		
		public abstract T build();
		
		protected final Class<T> getDocumentType() {
			final Class<?>[] types = TypeResolver.resolveRawArguments(Revision.Builder.class, getClass());
			checkState(TypeResolver.Unknown.class != types[1], "Couldn't resolve document type parameter for builder class %s", getClass().getSimpleName());
			return (Class<T>) types[1];
		}
		
	}

}
