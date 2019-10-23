/*
 * Copyright 2017-2019 B2i Healthcare Pte Ltd, http://b2i.sg
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
package com.b2international.snowowl.datastore.request.system;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.eclipse.core.runtime.Platform;
import org.osgi.framework.Version;

import com.b2international.index.es.client.EsClient;
import com.b2international.index.es.client.EsClusterStatus;
import com.b2international.index.es.client.EsIndexStatus;
import com.b2international.snowowl.core.Repositories;
import com.b2international.snowowl.core.RepositoryInfo;
import com.b2international.snowowl.core.ServerInfo;
import com.b2international.snowowl.core.ServiceProvider;
import com.b2international.snowowl.core.config.SnowOwlConfiguration;
import com.b2international.snowowl.core.events.Request;
import com.b2international.snowowl.datastore.DatastoreActivator;
import com.b2international.snowowl.datastore.request.RepositoryRequests;

/**
 * @since 5.8
 */
class ServerInfoGetRequest implements Request<ServiceProvider, ServerInfo> {

	private static final long serialVersionUID = 1L;

	@Override
	public ServerInfo execute(ServiceProvider context) {
		final Version version = Platform.getBundle(DatastoreActivator.PLUGIN_ID).getVersion();
		final String description = context.service(SnowOwlConfiguration.class).getDescription();
		final Repositories repositories = RepositoryRequests.prepareSearch().build().execute(context);
		final Set<String> repositoryIndices = repositories.stream()
				.map(RepositoryInfo::indices)
				.flatMap(List::stream)
				.map(EsIndexStatus::getIndex)
				.collect(Collectors.toSet());
		// this represents the current full cluster status with all global and terminology plugin specific indices
		EsClusterStatus clusterStatus = context.service(EsClient.class).status();
		// append global indices to the server info response
		final List<EsIndexStatus> globalIndices = clusterStatus.getIndices()
			.stream()
			.filter(indexStatus -> !repositoryIndices.contains(indexStatus.getIndex()))
			.collect(Collectors.toList());
		EsClusterStatus globalStatus = new EsClusterStatus(clusterStatus.isAvailable(), clusterStatus.getDiagnosis(), globalIndices);
		return new ServerInfo(version.toString(), description, repositories, globalStatus);
	}

}
