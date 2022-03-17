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
package com.b2international.snowowl.core.system;

import java.util.List;
import java.util.stream.Collectors;

import org.eclipse.core.runtime.Platform;
import org.osgi.framework.Version;

import com.b2international.index.ClusterStatus;
import com.b2international.index.IndexStatus;
import com.b2international.snowowl.core.*;
import com.b2international.snowowl.core.authorization.Unprotected;
import com.b2international.snowowl.core.config.SnowOwlConfiguration;
import com.b2international.snowowl.core.events.Request;
import com.b2international.snowowl.core.repository.RepositoryRequests;

/**
 * @since 5.8
 */
@Unprotected
class ServerInfoGetRequest implements Request<ServiceProvider, ServerInfo> {

	private static final long serialVersionUID = 1L;

	@Override
	public ServerInfo execute(ServiceProvider context) {
		final Version version = Platform.getBundle(CoreActivator.PLUGIN_ID).getVersion();
		final String description = context.service(SnowOwlConfiguration.class).getDescription();
		final Repositories repositories = RepositoryRequests.prepareSearch().build().execute(context);

		// FIXME: indices outside repositories are not listed and their status can not be taken into account
		final List<IndexStatus> indexStatusList = repositories.stream()
			.map(RepositoryInfo::indices)
			.flatMap(List::stream)
			.collect(Collectors.toList());
		
		// isAvailable should be false if at least one RED status is encountered
		final boolean isAvailable = indexStatusList.stream()
			.reduce(
				true, 
				(b, s) -> b && !IndexStatus.HealthStatus.RED.equals(s.getStatus()), 
				(b1, b2) -> b1 && b2);
		
		// diagnosis is a non-empty diagnosis from any repository
		final String diagnosis = indexStatusList.stream()
			.reduce(
				"",
				(d, s) -> d.isEmpty() ? s.getDiagnosis() : d,
				(s1, s2) -> s1.isEmpty() ? s2 : s1);
		
		final ClusterStatus globalStatus = new ClusterStatus(isAvailable, diagnosis, indexStatusList);
		return new ServerInfo(version.toString(), description, repositories, globalStatus);
	}
}
