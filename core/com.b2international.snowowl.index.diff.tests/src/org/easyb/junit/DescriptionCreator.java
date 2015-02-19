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
package org.easyb.junit;

import org.easyb.domain.Behavior;
import org.junit.runner.Description;

import java.io.File;

import static org.junit.runner.Description.createSuiteDescription;

public class DescriptionCreator {
	private final String basePath;

	public DescriptionCreator(final File baseDir) {
		this.basePath = baseDir.getAbsolutePath();
	}

	public Description create(final Behavior behavior) {
		final String name = replaceFileSeparatorsWithDots(getPathRelativeToBaseDir(behavior));
		return createSuiteDescription(name.substring(0, name.lastIndexOf('.')));
	}

	private String getPathRelativeToBaseDir(final Behavior behavior) {
		return behavior.getFile().getAbsolutePath().substring(basePath.length() + 1);
	}

	private String replaceFileSeparatorsWithDots(final String relPath) {
		return relPath.replace(System.getProperty("file.separator").charAt(0), '.');
	}
}