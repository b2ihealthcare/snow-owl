/*
 * Copyright 2017-2018 B2i Healthcare Pte Ltd, http://b2i.sg
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
package com.b2international.index.es;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Comparator;

import org.elasticsearch.analysis.common.CommonAnalysisPlugin;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.index.reindex.ReindexPlugin;
import org.elasticsearch.node.InternalSettingsPreparer;
import org.elasticsearch.node.Node;
import org.elasticsearch.painless.PainlessPlugin;
import org.elasticsearch.plugins.Plugin;
import org.elasticsearch.transport.Netty4Plugin;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.b2international.index.Activator;
import com.google.common.collect.ImmutableList;

/**
 * @since 5.10
 */
public final class EsNode extends Node {

	private static final String CONFIG_FILE = "elasticsearch.yml";
	private static final Logger LOG = LoggerFactory.getLogger("elastic.snowowl");
	
	private static EsNode INSTANCE;

	private final Path dataPath;
	private final boolean persistent;
	
	public static Node getInstance(String clusterName, Path configPath, Path dataPath, boolean persistent) {
		if (INSTANCE == null) {
			synchronized (EsNode.class) {
				if (INSTANCE == null) {
					// XXX: Adjust the thread context classloader while ES is initializing
					Activator.withTccl(() -> {
						try {
							System.setProperty("es.logs.base_path", configPath.toString());
							final Settings esSettings = configureSettings(clusterName, configPath.resolve(CONFIG_FILE), dataPath);
							final EsNode node = new EsNode(esSettings, dataPath, persistent);
							node.start();
							
							AwaitPendingTasks.await(node.client(), LOG);
							INSTANCE = node;
							LOG.info("Embedded elasticsearch is up and running.");
						} catch (Exception e) {
							throw new RuntimeException("Couldn't start embedded elasticsearch", e);
						}
					});
				}
			}
		}
		return INSTANCE;
	}
	
	public static void stop() {
		if (INSTANCE == null) {
			return;
		}

		// XXX: Adjust the thread context classloader while ES is closing
		Activator.withTccl(() -> {
			try {
				
				AwaitPendingTasks.await(INSTANCE.client(), LOG);
				INSTANCE.close();
				
				if (!INSTANCE.persistent) {
					Files.walk(INSTANCE.dataPath)
				      .sorted(Comparator.reverseOrder())
				      .map(Path::toFile)
				      .forEach(File::delete);
				}
				
			} catch (Exception e) {
				LOG.error("Failed to stop embedded Elasticsearch instance.", e);
			}
		});
	}
	
	private static Settings configureSettings(String clusterName, Path configPath, Path dataPath) throws IOException {
		final Settings.Builder esSettings;
		if (configPath.toFile().exists()) {
			LOG.info("Loading configuration settings from file {}", configPath);
			esSettings = Settings.builder().loadFromPath(configPath);
		} else {
			esSettings = Settings.builder();
		}

		// configure es home directory
		putSettingIfAbsent(esSettings, "path.home", dataPath.resolve(clusterName).toString());
		putSettingIfAbsent(esSettings, "cluster.name", clusterName);
		putSettingIfAbsent(esSettings, "node.name", clusterName);
		
		// this node is always the master node
		putSettingIfAbsent(esSettings, "node.master", true);
		putSettingIfAbsent(esSettings, "http.type", "netty4");
		putSettingIfAbsent(esSettings, "http.cors.enabled", true);
		putSettingIfAbsent(esSettings, "http.cors.allow-origin", "/https?:\\/\\/localhost(:[0-9]+)?/");
		putSettingIfAbsent(esSettings, "rest.action.multi.allow_explicit_index", false);
		putSettingIfAbsent(esSettings, "discovery.type", "single-node");
		
		return esSettings.build();
	}
	
	private static void putSettingIfAbsent(Settings.Builder settings, String key, String value) {
		if (!settings.keys().contains(key)) {
			settings.put(key, value);
		}
	}

	private static void putSettingIfAbsent(Settings.Builder settings, String key, boolean value) {
		if (!settings.keys().contains(key)) {
			settings.put(key, value);
		}
	}
	
	protected EsNode(Settings settings, Path dataPath, boolean persistent) {
		super(InternalSettingsPreparer.prepareEnvironment(settings, null), ImmutableList.<Class<? extends Plugin>>builder()
				.add(Netty4Plugin.class)
				.add(ReindexPlugin.class)
				.add(PainlessPlugin.class)
				.add(CommonAnalysisPlugin.class)
				.build());

		this.dataPath = dataPath;
		this.persistent = persistent;
	}
}
