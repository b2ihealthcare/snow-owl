/*
 * Copyright 2011-2018 B2i Healthcare Pte Ltd, http://b2i.sg
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
package com.b2international.snowowl.test.commons;

import java.io.File;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.b2international.commons.FileUtils;
import com.b2international.snowowl.core.api.SnowowlRuntimeException;
import com.b2international.snowowl.core.config.SnowOwlConfiguration;
import com.b2international.snowowl.core.setup.Environment;
import com.b2international.snowowl.core.setup.Plugin;
import com.google.common.base.Strings;

/**
 * A {@link Plugin} implementation to use for dataset copy before actually running the application in an integration test environment. The location
 * must be specified as a JVM argument in a {@value #LOC_PARAM_NAME} parameter.
 * <p>
 * <i>NOTE: make sure you do NOT clear the resources folder when running the application, see {@link SnowOwlAppRule#clearResources(boolean)}
 * </i>
 * </p>
 * <p>
 * Also the fragment throws exceptions in the following cases:
 * <ul>
 * <li>When the {@value #LOC_PARAM_NAME} JVM argument is missing</li>
 * <li>When the dataset archive defined in the {@value #LOC_PARAM_NAME} is not accessible, missing or invalid.</li>
 * </ul>
 * </p>
 *
 * Example usage:
 *
 * <pre>
 * &#064;RunWith(Suite.class)
 * public class SnowOwlAppTestSuite {
 *
 * 	&#064;ClassRule
 * 	public static final RuleChain appRule = SnowOwlAppRule.snowOwl().config(PlatformUtil.toAbsolutePath(SnowOwlAppTestSuite.class, &quot;config.json&quot;).fragments(new DatasetBootstrap()));
 *
 * }
 * </pre>
 *
 * @since 3.8
 */
public class DatasetBootstrap extends Plugin {

	private static final Logger LOG = LoggerFactory.getLogger(DatasetBootstrap.class);
	public static final String LOC_PARAM_NAME = "so.dataset.location";

	@Override
	public void init(SnowOwlConfiguration configuration, Environment env) throws Exception {
		final String datasetLocation = System.getProperty(LOC_PARAM_NAME);
		if (Strings.isNullOrEmpty(datasetLocation)) {
			throw new SnowowlRuntimeException(String.format("%s JVM argument is missing", LOC_PARAM_NAME));
		}
		final File datasetFile = new File(datasetLocation);
		if (!datasetFile.canRead()) {
			throw new SnowowlRuntimeException(String.format("Defined dataset location %s does not have a valid dataset archive", datasetLocation));
		}
		// delete resource directory first

		final File resourceDirectory = env.getDataPath().toFile();
		LOG.info("Deleting content of {}", resourceDirectory);
		FileUtils.cleanDirectory(resourceDirectory);
		LOG.info("Extracting dataset from {} to {}", datasetFile, resourceDirectory);
		FileUtils.decompressZipArchive(datasetFile, resourceDirectory);
	}

}
