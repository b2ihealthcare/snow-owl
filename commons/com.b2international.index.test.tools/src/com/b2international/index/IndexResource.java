/*
 * Copyright 2018-2022 B2i Healthcare Pte Ltd, http://b2i.sg
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
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Consumer;
import java.util.function.Supplier;

import org.junit.rules.ExternalResource;
import org.testcontainers.elasticsearch.ElasticsearchContainer;
import org.testcontainers.utility.MountableFile;

import com.b2international.index.es.EsIndexClientFactory;
import com.b2international.index.es.EsNode;
import com.b2international.index.mapping.Mappings;
import com.b2international.index.revision.Commit;
import com.b2international.index.revision.DefaultRevisionIndex;
import com.b2international.index.revision.RevisionBranch;
import com.b2international.index.revision.TimestampProvider;
import com.fasterxml.jackson.annotation.JsonAutoDetect.Visibility;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Maps;

/**
 * @since 7.1
 */
public final class IndexResource extends ExternalResource {

	private static final AtomicBoolean INIT = new AtomicBoolean(false);
	
	private static ObjectMapper mapper;
	private static Index index;
	private static IndexClient client;
	private static DefaultRevisionIndex revisionIndex;
	private static ElasticsearchContainer container;

	private final Collection<Class<?>> types;
	private final Consumer<ObjectMapper> objectMapperConfigurator;
	private final Supplier<Map<String, Object>> indexSettings;
	
	private IndexResource(Collection<Class<?>> types, Consumer<ObjectMapper> objectMapperConfigurator, Supplier<Map<String, Object>> indexSettings) {
		this.types = types;
		this.objectMapperConfigurator = objectMapperConfigurator;
		this.indexSettings = indexSettings;
	}
	
	@Override
	protected void before() throws Throwable {
		if (INIT.compareAndSet(false, true)) {
			final Map<String, Object> settings;
			
			// fire up an Elasticsearch test container if requested via useDocker system prop
			if (System.getProperty("so.index.es.useDocker") != null) {
				container = new ElasticsearchContainer("docker.elastic.co/elasticsearch/elasticsearch:8.1.2");
				container.withEnv("rest.action.multi.allow_explicit_index", "false");
				container.start();
				
				settings = Maps.newHashMap(this.indexSettings.get());
				settings.putIfAbsent(IndexClientFactory.CLUSTER_URL, "https://" + container.getHttpHostAddress());
				settings.putIfAbsent("ssl", container.createSslContextFromCa());
				settings.putIfAbsent(IndexClientFactory.CLUSTER_USERNAME, "elastic");
				settings.putIfAbsent(IndexClientFactory.CLUSTER_PASSWORD, ElasticsearchContainer.ELASTICSEARCH_DEFAULT_PASSWORD);
			} else {
				settings = this.indexSettings.get();
			}
			
			mapper = new ObjectMapper();
			mapper.setVisibility(PropertyAccessor.FIELD, Visibility.ANY);
			client = Indexes.createIndexClient(UUID.randomUUID().toString(), mapper, new Mappings(), settings);
			index = new DefaultIndex(client);
			revisionIndex = new DefaultRevisionIndex(index, new TimestampProvider.Default(), mapper);
		}
		
		if (container != null) {
			// make sure we update the synonyms.txt inside the test container
			container.copyFileToContainer(MountableFile.forHostPath(EsIndexClientFactory.DEFAULT_PATH.resolve(IndexClientFactory.DEFAULT_CLUSTER_NAME).resolve(EsNode.CONFIG_DIR).resolve(EsNode.SYNONYMS_FILE)), "/usr/share/elasticsearch/config/" + EsNode.SYNONYMS_FILE);
		}
		
		// apply mapper changes first
		objectMapperConfigurator.accept(mapper);
		
		// then mapping changes
		revisionIndex.admin().updateMappings(new Mappings(types));
		
		// then settings changes
		revisionIndex.admin().updateSettings(indexSettings.get());
		
		// then make sure we have all indexes ready for tests
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
	
	public ElasticsearchContainer getContainer() {
		return container;
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
	
	public static IndexResource create(Collection<Class<?>> types, Consumer<ObjectMapper> objectMapperConfigurator, Supplier<Map<String, Object>> indexSettings) {
		return new IndexResource(types, objectMapperConfigurator, indexSettings);
	}

}
