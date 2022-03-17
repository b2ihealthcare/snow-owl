/*
 * Copyright 2017-2022 B2i Healthcare Pte Ltd, http://b2i.sg
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
package com.b2international.index.os;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.temporal.ChronoUnit;
import java.util.Collections;
import java.util.Comparator;

import org.opensearch.analysis.common.CommonAnalysisPlugin;
import org.opensearch.client.Client;
import org.opensearch.common.settings.Settings;
import org.opensearch.index.rankeval.RankEvalPlugin;
import org.opensearch.index.reindex.ReindexPlugin;
import org.opensearch.join.ParentJoinPlugin;
import org.opensearch.node.InternalSettingsPreparer;
import org.opensearch.node.Node;
import org.opensearch.painless.PainlessPlugin;
import org.opensearch.percolator.PercolatorPlugin;
import org.opensearch.plugins.Plugin;
import org.opensearch.search.aggregations.matrix.MatrixAggregationPlugin;
import org.opensearch.transport.Netty4Plugin;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.ImmutableList;

import net.jodah.failsafe.Failsafe;
import net.jodah.failsafe.RetryPolicy;

/**
 * @since 5.10
 */
public final class OsNode extends Node {

	private static final String CONFIG_FILE = "opensearch.yml";
	private static final Logger LOG = LoggerFactory.getLogger("opensearch.snowowl");
	
	private static volatile OsNode INSTANCE;

	private final Path dataPath;
	private final boolean persistent;
	
	public static Node getInstance(String clusterName, Path configPath, Path dataPath, boolean persistent) {
		if (INSTANCE == null) {
			synchronized (OsNode.class) {
				if (INSTANCE == null) {
					// XXX: Adjust the thread context classloader while OS is initializing
					OsIndexClientFactory.withTccl(() -> {
						try {
							System.setProperty("os.logs.base_path", configPath.toString());
							final Settings esSettings = configureSettings(clusterName, configPath.resolve(CONFIG_FILE), dataPath);
							final OsNode node = new OsNode(esSettings, dataPath, persistent);
							node.start();
							
							waitForPendingTasks(node.client());
							INSTANCE = node;
							LOG.info("Embedded OpenSearch instance is up and running.");
						} catch (Exception e) {
							throw new RuntimeException("Couldn't start embedded OpenSearch instance", e);
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

		// XXX: Adjust the thread context classloader while OS is closing
		OsIndexClientFactory.withTccl(() -> {
			try {
				
				waitForPendingTasks(INSTANCE.client());
				INSTANCE.close();
				
				if (!INSTANCE.persistent) {
					Files.walk(INSTANCE.dataPath)
				      .sorted(Comparator.reverseOrder())
				      .map(Path::toFile)
				      .forEach(File::delete);
				}
				
			} catch (Exception e) {
				LOG.error("Failed to stop embedded OpenSearch instance.", e);
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
		
		// node.master is no longer supported, node.roles can be set here, but the default for the embedded mode is good enough
		// see https://opensearch.org/docs/latest/opensearch/cluster/#step-2-set-node-attributes-for-each-node-in-a-cluster
		putSettingIfAbsent(esSettings, "http.type", "netty4");
		putSettingIfAbsent(esSettings, "http.cors.enabled", true);
		putSettingIfAbsent(esSettings, "http.cors.allow-origin", "/https?:\\/\\/localhost(:[0-9]+)?/");
		putSettingIfAbsent(esSettings, "rest.action.multi.allow_explicit_index", false);
		putSettingIfAbsent(esSettings, "discovery.type", "single-node");
		putSettingIfAbsent(esSettings, "search.max_buckets", 1_500_000); // TODO hardcoded max buckets value to allow large aggregations to complete, fix and remove the config in 7.5
		putSettingIfAbsent(esSettings, "cluster.routing.allocation.disk.watermark.low", "20gb");
		putSettingIfAbsent(esSettings, "cluster.routing.allocation.disk.watermark.high", "10gb");
		putSettingIfAbsent(esSettings, "cluster.routing.allocation.disk.watermark.flood_stage", "5gb");
//		putSettingIfAbsent(esSettings, "cluster.info.update.interval", "1m");
		
		return esSettings.build();
	}
	
	private static void putSettingIfAbsent(Settings.Builder settings, String key, int value) {
		if (!settings.keys().contains(key)) {
			settings.put(key, value);
		}
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
	
	protected OsNode(Settings settings, Path dataPath, boolean persistent) {
		super(InternalSettingsPreparer.prepareEnvironment(settings, Collections.emptyMap(), null, null), ImmutableList.<Class<? extends Plugin>>builder()
				.add(CommonAnalysisPlugin.class)
				.add(MatrixAggregationPlugin.class)
				.add(Netty4Plugin.class)
				.add(PainlessPlugin.class)
				.add(ParentJoinPlugin.class)
				.add(PercolatorPlugin.class)
				.add(RankEvalPlugin.class)
				.add(ReindexPlugin.class)
				.build(), true);
		this.dataPath = dataPath;
		this.persistent = persistent;
	}
	
	private static void waitForPendingTasks(Client client) {
		RetryPolicy<Integer> retryPolicy = new RetryPolicy<Integer>();
		retryPolicy
			.handleResultIf(pendingTasks -> pendingTasks > 0)
			.withMaxAttempts(-1)
			.withBackoff(50, 1000, ChronoUnit.MILLIS);
		Failsafe.with(retryPolicy).get(() -> {
			LOG.info("Waiting for pending cluster tasks to finish.");
			return client.admin()
					.cluster()
					.preparePendingClusterTasks()
					.get()
					.getPendingTasks()
					.size();
		});
	}
	
}
