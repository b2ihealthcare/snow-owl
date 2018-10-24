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
package com.b2international.index;

import java.util.Collection;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Consumer;

import org.junit.rules.ExternalResource;

import com.b2international.index.mapping.Mappings;
import com.b2international.index.revision.Commit;
import com.b2international.index.revision.DefaultRevisionIndex;
import com.b2international.index.revision.RevisionBranch;
import com.b2international.index.revision.TimestampProvider;
import com.fasterxml.jackson.annotation.JsonAutoDetect.Visibility;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.ImmutableSet;

/**
 * @since 7.1
 */
public final class IndexResource extends ExternalResource {

	private static final AtomicBoolean INIT = new AtomicBoolean(false);
	
	private static ObjectMapper mapper;
	private static Index index;
	private static IndexClient client;
	private static DefaultRevisionIndex revisionIndex;

	private final Collection<Class<?>> types;
	private final Consumer<ObjectMapper> objectMapperConfigurator;
	
	private IndexResource(Collection<Class<?>> types, Consumer<ObjectMapper> objectMapperConfigurator) {
		this.types = types;
		this.objectMapperConfigurator = objectMapperConfigurator;
	}
	
	@Override
	protected void before() throws Throwable {
		if (INIT.compareAndSet(false, true)) {
			mapper = new ObjectMapper();
			mapper.setVisibility(PropertyAccessor.FIELD, Visibility.ANY);
			client = Indexes.createIndexClient(UUID.randomUUID().toString(), mapper, new Mappings());
			index = new DefaultIndex(client);
			revisionIndex = new DefaultRevisionIndex(index, new TimestampProvider.Default(), mapper);
		}
		
		objectMapperConfigurator.accept(mapper);
		
		// update mapping before executing tests
		types.forEach(index.admin().mappings()::putMapping);
		
		// make sure we have all indexes ready for consumption
		revisionIndex.admin().create();
	}
	
	@Override
	protected void after() {
		// make sure we clear each index after we've used them
		revisionIndex.admin().clear(ImmutableSet.<Class<?>>builder()
				.addAll(types)
				.add(RevisionBranch.class)
				.add(Commit.class)
				.build());
	}
	
	public IndexClient getClient() {
		return client;
	}
	
	public Index getIndex() {
		return index;
	}
	
	public DefaultRevisionIndex getRevisionIndex() {
		return revisionIndex;
	}
	
	public ObjectMapper getMapper() {
		return mapper;
	}
	
	public static IndexResource create(Collection<Class<?>> types, Consumer<ObjectMapper> objectMapperConfigurator) {
		return new IndexResource(types, objectMapperConfigurator);
	}

}
