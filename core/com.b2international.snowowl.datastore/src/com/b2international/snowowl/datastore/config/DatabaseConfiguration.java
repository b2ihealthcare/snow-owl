/*
 * Copyright 2011-2015 B2i Healthcare Pte Ltd, http://b2i.sg
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

import java.io.File;

import javax.validation.constraints.NotNull;

import org.hibernate.validator.constraints.NotEmpty;

import com.b2international.snowowl.core.SnowOwlApplication;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.common.base.Strings;

/**
 * @since 3.4
 */
public class DatabaseConfiguration {

	@NotEmpty
	private String directory = "store";

	@NotEmpty
	private String type = "h2";

	@NotEmpty
	private String driverClass = "org.h2.Driver";

	@NotEmpty
	private String datasourceClass = "org.h2.jdbcx.JdbcDataSource";

	/*
	 * bbanfai: OSX h2 fails with 'result too large' error message for the snomedStore.db file that is getting close to 30Gb
	 * changed the default "jdbc:h2:" scheme to the one below. More on the topic please see:
	 * http://www.h2database.com/html/advanced.html#file_system
	 */
	@NotEmpty
	private String scheme = "jdbc:h2:split:24:nio:";

	@NotNull
	private String location = "";
	
	@NotNull
	private String username = "";

	@NotNull
	private String password = "";

	private String settings = ";DB_CLOSE_DELAY=-1;LOCK_TIMEOUT=600000;CACHE_SIZE=131072;LOCK_MODE=3";
	
	/**
	 * Returns with the JDBC settings as a string.
	 * @return the JDBC settings.
	 */
	@JsonProperty
	public String getSettings() {
		return settings;
	}

	/**
	 * @param settings
	 *            the JDBC settings to set
	 */
	@JsonProperty
	public void setSettings(String settings) {
		this.settings = settings;
	}
	
	/**
	 * @return the JDBC user name
	 */
	@JsonProperty
	public String getUsername() {
		return username;
	}

	/**
	 * @param username
	 *            the JDBC databaseUsername to set
	 */
	@JsonProperty
	public void setUsername(String username) {
		this.username = username;
	}

	/**
	 * @return the JDBC password
	 */
	@JsonProperty
	public String getPassword() {
		return password;
	}

	/**
	 * @param password
	 *            the JDBC password to set
	 */
	@JsonProperty
	public void setPassword(String password) {
		this.password = password;
	}

	/**
	 * @return the datasourceClass
	 */
	@JsonProperty("datasource")
	public String getDatasourceClass() {
		return datasourceClass;
	}

	/**
	 * @param datasourceClass
	 *            the datasourceClass to set
	 */
	@JsonProperty("datasource")
	public void setDatasourceClass(String datasourceClass) {
		this.datasourceClass = datasourceClass;
	}

	/**
	 * @return the JDBC driver class fully qualified name
	 */
	@JsonProperty("driver")
	public String getDriverClass() {
		return driverClass;
	}

	/**
	 * @param driverClass
	 *            the JDBC driver class fully qualified name to set
	 */
	@JsonProperty("driver")
	public void setDriverClass(String driverClass) {
		this.driverClass = driverClass;
	}

	/**
	 * @return the database adapter's name (will be looked up using Eclipse's
	 *         extension mechanism), the type of database you want to use.
	 */
	@JsonProperty
	public String getType() {
		return type;
	}

	/**
	 * @param type
	 *            the database type/adapter to set
	 */
	@JsonProperty
	public void setType(String type) {
		this.type = type;
	}

	/**
	 * @return the databaseDirectory
	 */
	@JsonProperty
	public String getDirectory() {
		return directory;
	}

	/**
	 * @param directory
	 *            the database directory to set
	 */
	@JsonProperty
	public void setDirectory(String directory) {
		this.directory = directory;
	}

	/**
	 * Returns with the URL scheme. E.g.: {@code jdbc:h2}.
	 * @return the scheme from the JDBC URL.
	 */
	@JsonProperty
	public String getScheme() {
		return scheme;
	}

	/**
	 * @param scheme
	 *            the JDBC database scheme to set
	 */
	@JsonProperty
	public void setScheme(String scheme) {
		this.scheme = scheme;
	}
	
	/**
	 * @param location
	 *            the JDBC database location/domain. It can be a
	 *            <code>host:port/</code> or a <code>local path</code>.
	 */
	@JsonProperty
	public void setLocation(String location) {
		this.location = location;
	}
	
	/**
	 * @return the JDBC database location, domain
	 */
	@JsonProperty
	public String getLocation() {
		if (Strings.isNullOrEmpty(this.location)) {
			setLocation(getDefaultLocation());
		}
		return location;
	}

	private String getDefaultLocation() {
		final File dataDir = SnowOwlApplication.INSTANCE.getEnviroment().getDataDirectory();
		final File cdoDataDir = new File(dataDir, getDirectory());
		// Add trailing separator here
		return cdoDataDir.getAbsolutePath() + File.separator;
	}
	
}