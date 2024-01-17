/*
 * Copyright 2011-2015 B2i Healthcare, https://b2ihealthcare.com
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
package com.b2international.commons.config;

import java.io.IOException;
import java.io.InputStream;

/**
 * Capable of opening configuration sources.
 * 
 * @since 3.3
 */
public interface ConfigurationSourceProvider {

	/**
	 * Opens the given configuration source path and returns with an
	 * {@link InputStream}.
	 * 
	 * @param path
	 * @return
	 * @throws IOException
	 *             - if the given path is invalid or the configuration source
	 *             cannot be read.
	 */
	InputStream open(final String path) throws IOException;

}