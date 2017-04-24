/*
 * Copyright 2017 B2i Healthcare Pte Ltd, http://b2i.sg
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

import static com.google.common.collect.Lists.newArrayList;

import java.io.File;
import java.nio.file.Path;

import org.elasticsearch.Version;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.index.reindex.ReindexPlugin;
import org.elasticsearch.node.Node;
import org.elasticsearch.node.internal.InternalSettingsPreparer;
import org.elasticsearch.plugin.deletebyquery.DeleteByQueryPlugin;
import org.elasticsearch.plugins.Plugin;
import org.elasticsearch.script.groovy.GroovyPlugin;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.b2international.commons.FileUtils;
import com.b2international.index.admin.AwaitPendingTasks;

/**
 * @since 5.10
 */
public final class EsNode extends Node {

	private static final String CONFIG_FILE = "elasticsearch.yml";
	private static final String CLUSTER_NAME = "elastic-snowowl";
	private static final Logger LOG = LoggerFactory.getLogger("elastic.snowowl");
	
	private static Node INSTANCE;
	
	static Node getInstance(Path configPath, File directory, boolean persistent) {
		if (INSTANCE == null) {
			synchronized (EsNode.class) {
				if (INSTANCE == null) {
					final Settings esSettings = configureSettings(configPath.resolve(CONFIG_FILE), directory);
					final Node node = new EsNode(esSettings, GroovyPlugin.class, ReindexPlugin.class, DeleteByQueryPlugin.class);
					node.start();
					AwaitPendingTasks.await(node.client(), LOG);
					INSTANCE = node;
					Runtime.getRuntime().addShutdownHook(new Thread() {
						@Override
						public void run() {
							AwaitPendingTasks.await(INSTANCE.client(), LOG);
							INSTANCE.client().close();
							INSTANCE.close();
							if (!persistent) {
								FileUtils.deleteDirectory(directory);
							}
						}
					});
					LOG.info("Embedded elasticsearch is up and running.");
				}
			}
		}
		return INSTANCE;
	}
	
	private static Settings configureSettings(Path configPath, File directory) {
		final Settings.Builder esSettings;
		if (configPath.toFile().exists()) {
			LOG.info("Loading configuration settings from file {}", configPath);
			esSettings = Settings.builder().loadFromPath(configPath);
		} else {
			esSettings = Settings.builder();
		}

		putSettingIfAbsent(esSettings, IndexClientFactory.RESULT_WINDOW_KEY, ""+IndexClientFactory.DEFAULT_RESULT_WINDOW);
		// disable es refresh, we will do it manually on each commit
		putSettingIfAbsent(esSettings, "refresh_interval", "-1");
		// configure es home directory
		putSettingIfAbsent(esSettings, "path.home", directory.toPath().resolve(CLUSTER_NAME).toString());
		putSettingIfAbsent(esSettings, "cluster.name", CLUSTER_NAME);
		putSettingIfAbsent(esSettings, "node.name", CLUSTER_NAME);
		putSettingIfAbsent(esSettings, "index.number_of_shards", 1);
		putSettingIfAbsent(esSettings, "index.number_of_replicas", 0);
		putSettingIfAbsent(esSettings, "script.inline", true);
		putSettingIfAbsent(esSettings, "script.indexed", true);
		
		// local mode if not set
		putSettingIfAbsent(esSettings, "node.client", false);
		putSettingIfAbsent(esSettings, "node.local", true);
		return esSettings.build();
	}
	
	private static void putSettingIfAbsent(Settings.Builder settings, String key, Object value) {
		if (!settings.internalMap().containsKey(key)) {
			settings.put(key, value);
		}
	}

	@SafeVarargs
	private EsNode(Settings settings, Class<? extends Plugin>...classpathPlugins) {
		super(InternalSettingsPreparer.prepareEnvironment(settings, null), Version.CURRENT, newArrayList(classpathPlugins));
	}
	
}
