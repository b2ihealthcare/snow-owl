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
import java.util.List;

import org.elasticsearch.Version;
import org.elasticsearch.cluster.service.PendingClusterTask;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.settings.Settings.Builder;
import org.elasticsearch.index.reindex.ReindexPlugin;
import org.elasticsearch.node.Node;
import org.elasticsearch.node.internal.InternalSettingsPreparer;
import org.elasticsearch.plugin.deletebyquery.DeleteByQueryPlugin;
import org.elasticsearch.plugins.Plugin;
import org.elasticsearch.script.groovy.GroovyPlugin;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.b2international.commons.FileUtils;

/**
 * @since 5.10
 */
public final class EmbeddedNode extends Node {

	private static final Logger LOG = LoggerFactory.getLogger("elastic.snowowl");
	private static final int PENDING_CLUSTER_TASKS_RETRY_INTERVAL = 100;
	private static final String CLUSTER_NAME = "elastic-snowowl";
	
	private static EmbeddedNode INSTANCE;
	
	static EmbeddedNode getInstance(File directory, boolean persistent) {
		if (INSTANCE == null) {
			synchronized (EmbeddedNode.class) {
				if (INSTANCE == null) {
					INSTANCE = null;
					final Builder esSettings = Settings.builder();
					esSettings.put(IndexClientFactory.RESULT_WINDOW_KEY, ""+IndexClientFactory.RESULT_WINDOW_KEY);
					// disable es refresh, we will do it manually on each commit
					esSettings.put("refresh_interval", "-1");
					// configure es home directory
					final String esHome = directory.toPath().resolve(CLUSTER_NAME).toString();
					LOG.info("homedir {}", esHome);
					esSettings.put("path.home", esHome);
					esSettings.put("cluster.name", CLUSTER_NAME);
					esSettings.put("node.name", CLUSTER_NAME);
					esSettings.put("index.translog.flush_threshold_period", "30m");
					esSettings.put("index.number_of_shards", 1);
					esSettings.put("index.number_of_replicas", 0);
					esSettings.put("node.client", false);
					esSettings.put("node.local", true);
					esSettings.put("script.inline", true);
					esSettings.put("script.indexed", true);
					
					INSTANCE = new EmbeddedNode(esSettings.build(), GroovyPlugin.class, ReindexPlugin.class, DeleteByQueryPlugin.class);
					INSTANCE.start();
					INSTANCE.awaitPendingTasks();
					Runtime.getRuntime().addShutdownHook(new Thread() {
						@Override
						public void run() {
							INSTANCE.awaitPendingTasks();
							INSTANCE.client().close();
							INSTANCE.close();
							if (persistent) {
								FileUtils.deleteDirectory(directory);
							}
						}
					});
					LOG.info("Index is up and running.");
				}
			}
		}
		return INSTANCE;
	}
	
	@SafeVarargs
	private EmbeddedNode(Settings settings, Class<? extends Plugin>...classpathPlugins) {
		super(InternalSettingsPreparer.prepareEnvironment(settings, null), Version.CURRENT, newArrayList(classpathPlugins));
	}
	
	public void awaitPendingTasks() {
		int pendingTaskCount = 0;
		do {
			LOG.info("Waiting for pending cluster tasks to finish.");
			List<PendingClusterTask> pendingTasks = client().admin().cluster().preparePendingClusterTasks().get()
					.getPendingTasks();
			pendingTaskCount = pendingTasks.size();
			try {
				Thread.sleep(PENDING_CLUSTER_TASKS_RETRY_INTERVAL);
			} catch (InterruptedException e) {
				Thread.currentThread().interrupt();
			}
		} while (pendingTaskCount > 0);
	}

}
