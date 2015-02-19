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
package com.b2international.snowowl.rpc.test.service;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.NullProgressMonitor;

/**
 * The test service interface to use for RPC test method calls that have an {@link IProgressMonitor} argument.
 * 
 */
public interface IProgressMonitorUsingService {

	/**
	 * The task name which, used in {@link IProgressMonitor#beginTask(String, int)} calls.
	 */
	String TASK_NAME = "working";
	
	/**
	 * The total work units, used in {@link IProgressMonitor#beginTask(String, int)} and {@link IProgressMonitor#worked(int)} calls.
	 */
	int TOTAL_WORK = 3;

	/**
	 * Runs by calling {@link IProgressMonitor#beginTask(String, int)} with 10 units of work, then signaling one unit of work in 60ms intervals.
	 * 
	 * @param monitor
	 */
	void reportWithIProgressMonitor(IProgressMonitor monitor);
	
	/**
	 * The method should fail when called over RPC. 
	 * 
	 * @param nullProgressMonitor
	 */
	void reportWithNullProgressMonitor(NullProgressMonitor nullProgressMonitor);
}