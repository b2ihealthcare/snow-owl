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
package com.b2international.snowowl.core.api;

import java.lang.reflect.InvocationTargetException;

import org.eclipse.core.runtime.IProgressMonitor;

/**
 * Runnable with progress representation without any UI dependency.
 */
public interface ICoreRunnableWithProgress {

	/**
	 * Runs this operation with the specified progress monitor.
	 * @param monitor the progress monitor for the operation.
	 * @throws InvocationTargetException error occurred during the operation.
	 * @throws InterruptedException operation detects a CANCEL.	
	 */
	public void run(IProgressMonitor monitor) throws InvocationTargetException, InterruptedException;
	
}