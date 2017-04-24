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
package com.b2international.index.admin;

import java.util.List;

import org.elasticsearch.client.Client;
import org.elasticsearch.cluster.service.PendingClusterTask;
import org.slf4j.Logger;

/**
 * @since 5.10
 */
public final class AwaitPendingTasks {

	private static final int PENDING_CLUSTER_TASKS_RETRY_INTERVAL = 50;
	
	private AwaitPendingTasks() {}
	
	public static final void await(Client client, Logger log) {
		int pendingTaskCount = 0;
		do {
			List<PendingClusterTask> pendingTasks = client.admin()
					.cluster()
					.preparePendingClusterTasks()
					.get()
					.getPendingTasks();
			pendingTaskCount = pendingTasks.size();
			if (pendingTaskCount > 0) {
				log.info("Waiting for pending cluster tasks to finish.");
				try {
					Thread.sleep(PENDING_CLUSTER_TASKS_RETRY_INTERVAL);
				} catch (InterruptedException e) {
					Thread.currentThread().interrupt();
				}
			}
		} while (pendingTaskCount > 0);
	}
	
}
