/*
 * Copyright 2018-2024 B2i Healthcare, https://b2ihealthcare.com
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

import static com.google.common.base.Preconditions.checkNotNull;
import static org.junit.Assume.assumeTrue;

import java.net.URL;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collection;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Consumer;
import java.util.function.Supplier;

import org.eclipse.core.runtime.FileLocator;
import org.junit.rules.ExternalResource;
import org.osgi.framework.FrameworkUtil;
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

	/**
	 * Java system property to configure the use of a testcontainer Elasticsearch Docker container and optionally configure the actual image as well. By default it uses the 8.1.3 image.
	 */
	public static final String ES_USE_TEST_CONTAINER_VARIABLE = "so.index.es.useDocker";
	
	public static final String DEFAULT_ES_DOCKER_IMAGE = "docker.elastic.co/elasticsearch/elasticsearch:8.12.2";

	private static final AtomicBoolean INIT = new AtomicBoolean(false);
	
	private static ObjectMapper mapper;
	private static Index index;
	private static IndexClient client;
	private static DefaultRevisionIndex revisionIndex;
	private static ElasticsearchContainer container;

	private final Collection<Class<?>> types;
	private final Consumer<ObjectMapper> objectMapperConfigurator;
	private final Supplier<Map<String, Object>> indexSettings;
	private final Supplier<String> supportedVersion;
	
	private IndexResource(Collection<Class<?>> types, Consumer<ObjectMapper> objectMapperConfigurator, Supplier<Map<String, Object>> indexSettings, Supplier<String> supportedVersion) {
		this.types = types;
		this.objectMapperConfigurator = objectMapperConfigurator;
		this.indexSettings = indexSettings;
		this.supportedVersion = supportedVersion;
	}
	
	@Override
	protected void before() throws Throwable {
		if (INIT.compareAndSet(false, true)) {
			final Map<String, Object> settings;
			
			// fire up an Elasticsearch test container if requested via useDocker system prop
			String testElasticsearchContainer = System.getProperty(ES_USE_TEST_CONTAINER_VARIABLE);
			if (testElasticsearchContainer != null) {
				if (testElasticsearchContainer.isEmpty()) {
					testElasticsearchContainer = DEFAULT_ES_DOCKER_IMAGE;
				}
				container = new ElasticsearchContainer(testElasticsearchContainer);
				// XXX elasticsearch-default-memory-vm.options is a classpath resource in the testcontainers:elasticsearch jar since 7.17.4
				// loading it from the classpath won't work because testcontainers is not ready to handle bundleresource URLs specific to Eclipse OSGi 
				// remove the entry and replace it with ours
				container.getCopyToFileContainerPathMap().keySet().removeIf(file -> file.getFilesystemPath().startsWith("bundleresource://") && file.getFilesystemPath().contains("elasticsearch-default-memory-vm.options"));
				container.withCopyFileToContainer(MountableFile.forHostPath(toAbsolutePathBundleEntry(IndexResource.class, "elasticsearch-default-memory-vm.options")), "/usr/share/elasticsearch/config/jvm.options.d/elasticsearch-default-memory-vm.options");
				
				container.withEnv("rest.action.multi.allow_explicit_index", "false");
				container.start();
				
				settings = Maps.newHashMap(this.indexSettings.get());
				settings.putIfAbsent(IndexClientFactory.CLUSTER_URL, "https://" + container.getHttpHostAddress());
				settings.putIfAbsent(IndexClientFactory.CLUSTER_SSL_CONTEXT, container.createSslContextFromCa());
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
		
		// when init is ready check version and ignore test if connected cluster is not supported
		assumeTrue(supportedVersion.get().equals("*") || index.admin().client().version().startsWith(supportedVersion.get()));
		
		if (container != null) {
			// make sure we update the synonyms.txt inside the test container
			final MountableFile localSynonymFilePath = MountableFile.forHostPath(EsIndexClientFactory.DEFAULT_PATH.resolve(IndexClientFactory.DEFAULT_CLUSTER_NAME).resolve(EsNode.CONFIG_DIR).resolve(EsNode.SYNONYMS_FILE));
			final String containerSynonymFilePath = "/usr/share/elasticsearch/config/" + EsNode.SYNONYMS_FILE;
			container.copyFileToContainer(localSynonymFilePath, containerSynonymFilePath);
		}
		
		// apply mapper changes first
		objectMapperConfigurator.accept(mapper);
		
		// then mapping changes
		revisionIndex.admin().updateMappings(new Mappings(types));
		
		// then update settings changes for existing indices (TODO move this into create? or updateMappings?)
		revisionIndex.admin().updateSettings(indexSettings.get());
		
		// then make sure we have all indexes ready for tests
		revisionIndex.admin().create();
	}

	private static Path toAbsolutePathBundleEntry(Class<?> contextClass, String path) throws Exception {
		var bundle = checkNotNull(FrameworkUtil.getBundle(contextClass), "Bundle not found for %s", contextClass);
		var fileURL = new URL(FileLocator.toFileURL(bundle.getEntry(path)).toString().replaceAll(" ", "%20"));
		return Paths.get(fileURL.toURI());
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
	
	public static IndexResource create(Collection<Class<?>> types, Consumer<ObjectMapper> objectMapperConfigurator, Supplier<Map<String, Object>> indexSettings, Supplier<String> supportedVersion) {
		return new IndexResource(types, objectMapperConfigurator, indexSettings, supportedVersion);
	}

}
