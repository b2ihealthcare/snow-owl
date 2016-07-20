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
package com.b2international.snowowl.datastore;

import java.io.File;

import com.b2international.snowowl.core.SnowOwlApplication;
import com.google.common.io.Files;

/**
 * Abstract implementation of the UI independent {@link IComponentIconProvider component icon provider}. 
 * @see IComponentIconProvider
 * @param <K> - type of the unique ID of the terminology independent component.
 */
public abstract class ComponentIconProvider<K> implements IComponentIconProvider<K> {

	protected boolean disposed = false;
	
	@Override
	public boolean isDisposed() {
		return disposed;
	}
	
	@Override
	public void dispose() {
		this.disposed = true;
	}
	
	/**
	 * Returns with the file instance pointing the the absolute location of the icon directory.
	 * @return the file instance describing the folder containing the icons for the components.
	 */
	protected File getIconDirectory() {
		// sometimes in test environment the whole app is not available, just partially
		if (SnowOwlApplication.INSTANCE.isRunning()){
			return new File(SnowOwlApplication.INSTANCE.getEnviroment().getDataDirectory(), getFolderName());
		} else {
			// create a tmpDir without any files and delete it on JVM shutdown
			final File tmpDir = Files.createTempDir();
			tmpDir.deleteOnExit();
			return tmpDir;
		}
	}
	
	/**
	 * Returns with the name of the folder containing the icons for the components.
	 * @return the folder name.
	 */
	protected abstract String getFolderName();
	
}