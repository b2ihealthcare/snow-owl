/*
 * Copyright 2011-2020 B2i Healthcare Pte Ltd, http://b2i.sg
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
package com.b2international.snowowl.core.monitoring;

import org.junit.Test;

import com.b2international.snowowl.core.monitoring.ConsoleProgressMonitor;

public class ConsoleProgressMonitorTest {

	@Test
	public void testProgressMonitor3Steps() {
		
		ConsoleProgressMonitor monitor = new ConsoleProgressMonitor();
		
		monitor.beginTask("Test", 3);
		monitor.worked(1);
		monitor.worked(1);
		monitor.done();
	}
	
	@Test
	public void testProgressMonitor3Plus2Steps() {
		
		ConsoleProgressMonitor monitor = new ConsoleProgressMonitor();
		
		monitor.beginTask("Test", 3);
		monitor.worked(1);
		monitor.worked(1);
		monitor.worked(1);
		monitor.worked(1);
		monitor.done();
	}	
	
	@Test
	public void testProgressMonitor10Steps() {
		
		ConsoleProgressMonitor monitor = new ConsoleProgressMonitor();
		
		monitor.beginTask("Test", 10);
		monitor.worked(1);
		monitor.worked(1);
		monitor.worked(1);
		monitor.worked(1);
		monitor.worked(1);
		monitor.worked(1);
		monitor.worked(1);
		monitor.worked(1);
		monitor.worked(1);
		monitor.done();
	}

	@Test
	public void testProgressMonitor20StepsMax4Echoes() {
		
		ConsoleProgressMonitor monitor = new ConsoleProgressMonitor(4);
		
		monitor.beginTask("Test", 20);
		monitor.worked(1);
		monitor.worked(1);
		monitor.worked(1);
		monitor.worked(1);
		monitor.worked(1);
		monitor.worked(1);
		monitor.worked(1);
		monitor.worked(1);
		monitor.worked(1);
		monitor.worked(1);
		monitor.worked(1);
		monitor.worked(1);
		monitor.worked(1);
		monitor.worked(1);
		monitor.worked(1);
		monitor.worked(1);
		monitor.worked(1);
		monitor.worked(1);
		monitor.done();
	}
}