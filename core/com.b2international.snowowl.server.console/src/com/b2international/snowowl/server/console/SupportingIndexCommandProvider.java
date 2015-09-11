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
package com.b2international.snowowl.server.console;

import java.io.IOException;
import java.util.Collection;
import java.util.List;

import org.eclipse.osgi.framework.console.CommandInterpreter;
import org.eclipse.osgi.framework.console.CommandProvider;

import com.b2international.commons.CompareUtils;
import com.b2international.commons.StringUtils;
import com.b2international.snowowl.core.ApplicationContext;
import com.b2international.snowowl.datastore.SingleDirectoryIndex;
import com.b2international.snowowl.datastore.server.index.SingleDirectoryIndexManager;
import com.google.common.base.Strings;

/**
 * OSGI command contribution with Snow Owl commands to manage supporting indexes.
 */
public class SupportingIndexCommandProvider implements CommandProvider {

	@Override
	public String getHelp() {
		final StringBuffer buffer = new StringBuffer();
		buffer.append("---Snow Owl supporting index commands---\n");
		buffer.append("\tindex list - List registered supporting index identifiers\n");
		buffer.append("\tindex listSnapshots [indexId] - List all consistent snapshots of a particular supporting index\n");
		buffer.append("\tindex createSnapshot [indexId] - Create a consistent snapshot of current supporting index contents\n");
		buffer.append("\tindex listSnapshotFiles [indexId] [snapshotId] - List files contained in a consistent snapshot\n");
		buffer.append("\tindex releaseSnapshot [indexId] [snapshotId] - Release a consistent snapshot for a supporting index\n");
		return buffer.toString();
	}

	/**
	 * Reflective template method declaratively registered. Needs to start with "_".
	 * @param interpreter
	 */
	public void _index(final CommandInterpreter interpreter) {

		try {
			
			final String cmd = Strings.nullToEmpty(interpreter.nextArgument()).toLowerCase();
			
			switch (cmd) {
				case "list":
					listServices(interpreter);
					break;
				case "listsnapshots":
					listSnapshots(interpreter);
					break;
				case "createsnapshot":
					createSnapshot(interpreter);
					break;
				case "listsnapshotfiles":
					listSnapshotFiles(interpreter);
					break;
				case "releasesnapshot":
					releaseSnapshot(interpreter);
					break;
				default:
					interpreter.println(getHelp());
					break;
			}

		} catch (final Exception ex) {
			interpreter.println(ex.getMessage());
		}
	}

	public synchronized void listServices(final CommandInterpreter interpreter) {

		final Collection<String> serviceIds = getSingleDirectoryIndexManager().getServiceIds();
		
		if (CompareUtils.isEmpty(serviceIds)) {
			interpreter.println("No supplementary index is present on the server.");
		} else {
			interpreter.println();
			interpreter.println(" Index service identifier");	
			printTable(interpreter, serviceIds);
		}
	}

	public synchronized void listSnapshots(final CommandInterpreter interpreter) {

		final String serviceId = interpreter.nextArgument();
		
		if (StringUtils.isEmpty(serviceId)) {
			interpreter.println("Command usage: index listSnapshots [indexId]");
			return;
		}

		final SingleDirectoryIndex service = getService(serviceId, interpreter);
		
		if (null == service) {
			return;
		}
		
		final List<String> snapshotIds = service.getSnapshotIds();
		
		if (CompareUtils.isEmpty(snapshotIds)) {
			interpreter.println("No snapshot has been created for '" + serviceId + "' on the server.");
		} else {
			interpreter.println();
			interpreter.println(" Index snapshot identifier");	
			printTable(interpreter, snapshotIds);
		}
	}

	public synchronized void createSnapshot(final CommandInterpreter interpreter) {

		final String serviceId = interpreter.nextArgument();
		
		if (StringUtils.isEmpty(serviceId)) {
			interpreter.println("Command usage: index createSnapshot [indexId]");
			return;
		}

		final SingleDirectoryIndex service = getService(serviceId, interpreter);
		
		if (null == service) {
			return;
		}
		
		try {
			final String snapshotId = service.snapshot();
			interpreter.println("Snapshot '" + snapshotId + "' for service '" + serviceId + "' has been successfully created.");
		} catch (final IOException e) {
			interpreter.println("Couldn't create snapshot: " + e.getMessage());
		}
	}
	
	public synchronized void listSnapshotFiles(final CommandInterpreter interpreter) {

		final String serviceId = interpreter.nextArgument();
		final String snapshotId = interpreter.nextArgument();

		if (StringUtils.isEmpty(serviceId) || StringUtils.isEmpty(snapshotId)) {
			interpreter.println("Command usage: index listSnapshotFiles [indexId] [snapshotId]");
			return;
		}

		final SingleDirectoryIndex service = getService(serviceId, interpreter);
		
		if (null == service) {
			return;
		}
		
		if (!service.getSnapshotIds().contains(snapshotId)) {
			interpreter.println("Snapshot '" + snapshotId + "' does not exist.");
			return;
		}

		try {
			
			final List<String> snapshotFiles = service.listFiles(snapshotId);
			if (snapshotFiles.isEmpty()) {
				interpreter.println("No files are associated with snapshot '" + snapshotId + "'.");
				return;
			}
			
			interpreter.println();
			interpreter.println(" Files in snapshot '" + snapshotId + "'");	
			printTable(interpreter, snapshotFiles);
			
		} catch (final IOException e) {
			interpreter.println("Couldn't collect list files for snapshot " + snapshotId + ": " + e.getMessage());
		}
	}

	public synchronized void releaseSnapshot(final CommandInterpreter interpreter) {
		
		final String serviceId = interpreter.nextArgument();
		final String snapshotId = interpreter.nextArgument();

		if (StringUtils.isEmpty(serviceId) || StringUtils.isEmpty(snapshotId)) {
			interpreter.println("Command usage: index releaseSnapshot [indexId] [snapshotId]");
			return;
		}

		final SingleDirectoryIndex service = getService(serviceId, interpreter);
		
		if (null == service) {
			return;
		}
		
		if (!service.getSnapshotIds().contains(snapshotId)) {
			interpreter.println("Snapshot '" + snapshotId + "' does not exist.");
			return;
		}

		try {
			service.releaseSnapshot(snapshotId);
			interpreter.println("Snapshot " + snapshotId + " has been successfully released.");
		} catch (final IOException e) {
			interpreter.println("Couldn't release snapshot " + snapshotId + ": " + e.getMessage());		
		}
	}

	private SingleDirectoryIndexManager getSingleDirectoryIndexManager() {
		return ApplicationContext.getInstance().getService(SingleDirectoryIndexManager.class);
	}

	private void printTable(final CommandInterpreter interpreter, final Collection<String> ids) {
		interpreter.println(Strings.repeat("-", 32));
		
		for (final String id : ids) {
			interpreter.println(" " + id);
		}
		
		interpreter.println(Strings.repeat("-", 32));
	}

	private SingleDirectoryIndex getService(final String serviceId, final CommandInterpreter interpreter) {
		
		final SingleDirectoryIndex service = getSingleDirectoryIndexManager().getService(serviceId);
		
		if (null == service) {
			interpreter.println("Supplementary index service with identifier '" + serviceId + "' could not be found. Available services are:");
			listServices(interpreter);
		}
		
		return service;
	}
}