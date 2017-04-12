/*
 * Copyright 2011-2016 B2i Healthcare Pte Ltd, http://b2i.sg
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
package com.b2international.snowowl.datastore.config;

import java.util.Map;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

import org.hibernate.validator.constraints.NotEmpty;

import com.b2international.commons.db.JdbcUrl;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.common.collect.ImmutableMap;
import com.google.common.net.HostAndPort;

/**
 * Repository configuration is the central place where database connection
 * parameters, repository settings can be configured and retrieved.
 * 
 * @since 3.4
 */
public class RepositoryConfiguration extends ConnectionPoolConfiguration {
	
	@NotEmpty
	private String host = "0.0.0.0";

	@Min(0)
	@Max(65535)
	private int port = 2036;

	@NotNull
	private DatabaseConfiguration databaseConfiguration = new DatabaseConfiguration();
	
	@NotNull
	private IndexConfiguration indexConfiguration = new IndexConfiguration();
	
	@Min(0)
	@Max(100)
	private int numberOfWorkers = 3 * Runtime.getRuntime().availableProcessors();
	
	@Min(10)
	@Max(1000)
	private int mergeMaxResults = 100;

	private boolean revisionCacheEnabled = true;
	
	/**
	 * @return the host
	 */
	@JsonProperty
	public String getHost() {
		return host;
	}

	/**
	 * @return the port
	 */
	@JsonProperty
	public int getPort() {
		return port;
	}

	/**
	 * @param host
	 *            the host to set
	 */
	@JsonProperty
	public void setHost(String host) {
		this.host = host;
	}

	/**
	 * @param port
	 *            the port to set
	 */
	@JsonProperty
	public void setPort(int port) {
		this.port = port;
	}

	/**
	 * Returns the currently set {@link HostAndPort}.
	 * 
	 * @return
	 */
	public HostAndPort getHostAndPort() {
		return HostAndPort.fromParts(getHost(), getPort());
	}

	/**
	 * @return the number of workers threads per repository
	 */
	@JsonProperty
	public int getNumberOfWorkers() {
		return numberOfWorkers;
	}
	
	/**
	 * @param numberOfWorkers the number of workers per repository to set
	 */
	@JsonProperty
	public void setNumberOfWorkers(int numberOfWorkers) {
		this.numberOfWorkers = numberOfWorkers;
	}
	
	/**
	 * @return the databaseConfiguration
	 */
	@JsonProperty("database")
	public DatabaseConfiguration getDatabaseConfiguration() {
		return databaseConfiguration;
	}
	
	/**
	 * @param databaseConfiguration the databaseConfiguration to set
	 */
	@JsonProperty("database")
	public void setDatabaseConfiguration(DatabaseConfiguration databaseConfiguration) {
		this.databaseConfiguration = databaseConfiguration;
	}
	
	@JsonProperty("index")
	public IndexConfiguration getIndexConfiguration() {
		return indexConfiguration;
	}
	
	@JsonProperty("index")
	public void setIndexConfiguration(IndexConfiguration indexConfiguration) {
		this.indexConfiguration = indexConfiguration;
	}

	/**
	 * Constructs a JDBC type database URL from the current configuration
	 * parameters.
	 * 
	 * @return the JDBC URL of the database for the repository
	 */
	public JdbcUrl getDatabaseUrl() {
		return new JdbcUrl(getDatabaseConfiguration().getScheme(), getDatabaseConfiguration().getLocation(), getDatabaseConfiguration().getSettings());
	}
	
	/**
	 * @return the maximum number of completed merge job results to keep
	 */
	@JsonProperty
	public int getMergeMaxResults() {
		return mergeMaxResults;
	}
	
	@JsonProperty
	public void setMergeMaxResults(int mergeMaxResults) {
		this.mergeMaxResults = mergeMaxResults;
	}
	
	/**
	 * Returns a property map for the given repository name.
	 * 
	 * @param repositoryName
	 * @return
	 */
	public Map<Object, Object> getDatasourceProperties(String repositoryName) {
		final ImmutableMap.Builder<Object, Object> properties = ImmutableMap.builder();
		properties.put("class", getDatabaseConfiguration().getDatasourceClass());
		properties.put("uRL", getDatabaseUrl().build(repositoryName)); // XXX: strange casing required by net4j's uncapitalizer method when inspecting setters!
		properties.put("user", getDatabaseConfiguration().getUsername());
		properties.put("password", getDatabaseConfiguration().getPassword());
		return properties.build();
	}

	@JsonProperty("revisionCache")
	public boolean isRevisionCacheEnabled() {
		return revisionCacheEnabled ;
	}
	
	@JsonProperty("revisionCache")
	public void setRevisionCacheEnabled(boolean revisionCacheEnabled) {
		this.revisionCacheEnabled = revisionCacheEnabled;
	}

}