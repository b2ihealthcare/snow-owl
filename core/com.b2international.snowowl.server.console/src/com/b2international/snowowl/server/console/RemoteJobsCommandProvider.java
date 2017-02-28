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

import java.text.MessageFormat;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import org.eclipse.osgi.framework.console.CommandInterpreter;
import org.eclipse.osgi.framework.console.CommandProvider;

import com.b2international.commons.CompareUtils;
import com.b2international.commons.StringUtils;
import com.b2international.snowowl.core.ApplicationContext;
import com.b2international.snowowl.core.date.DateFormats;
import com.b2international.snowowl.core.date.Dates;
import com.b2international.snowowl.datastore.remotejobs.RemoteJobEntry;
import com.b2international.snowowl.datastore.request.job.JobRequests;
import com.b2international.snowowl.eventbus.IEventBus;
import com.google.common.base.Function;
import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import com.google.common.collect.Ordering;

/**
 * OSGI command contribution with Snow Owl commands to manage long-running operations on the server.
 * 
 */
public class RemoteJobsCommandProvider implements CommandProvider {

	private final List<RemoteJobEntry> lastRetrievedEntries = Lists.newArrayList();
	
	@Override
	public String getHelp() {
		final StringBuffer buffer = new StringBuffer();
		buffer.append("---Snow Owl remote job management commands---\n");
		buffer.append("\tremotejobs list - List currently scheduled or executing long-running operations\n");
		buffer.append("\tremotejobs cancel [jobId] - Cancel a scheduled or executing long-running operation\n");
		return buffer.toString();
	}

	public void _remotejobs(final CommandInterpreter interpreter) {

		try {
			
			final String cmd = Strings.nullToEmpty(interpreter.nextArgument()).toLowerCase();
			
			switch (cmd) {
				case "list":
					listJobs(interpreter);
					break;
				case "cancel":
					cancelJob(interpreter);
					break;
				default:
					interpreter.println(getHelp());
					break;
			}

		} catch (final Exception ex) {
			interpreter.println(ex.getMessage());
		}
	}

	private static final String COLUMN_FORMAT = "%4s | %-50s | %-16s | %-16s | %-16s | %-16s";

	public synchronized void listJobs(final CommandInterpreter interpreter) {

		lastRetrievedEntries.clear();
		lastRetrievedEntries.addAll(JobRequests.prepareSearch().all().buildAsync().execute(getBus()).getSync().getItems());
		
		if (CompareUtils.isEmpty(lastRetrievedEntries)) {
			interpreter.println("No remote jobs are currently scheduled or running on the server.");
			return;
		}
		
		sortEntriesByScheduleDate();

		interpreter.println();
		printHeader(interpreter);
		printSeparator(interpreter);
		
		for (int i = 0; i < lastRetrievedEntries.size(); i++) {
			final RemoteJobEntry entry = lastRetrievedEntries.get(i);
			printLineForEntry(interpreter, i, entry);
			printSeparator(interpreter);
		}
	}

	private void printHeader(final CommandInterpreter interpreter) {
		interpreter.println(String.format(COLUMN_FORMAT, "Id", "Description", "Owner", "Scheduled", "Started", "Status"));
	}

	private void sortEntriesByScheduleDate() {
		Collections.sort(lastRetrievedEntries, Ordering.natural().onResultOf(new Function<RemoteJobEntry, Date>() {
			@Override
			public Date apply(RemoteJobEntry input) {
				return input.getScheduleDate();
			}
		}));
	}

	private void printLineForEntry(final CommandInterpreter interpreter, int i, final RemoteJobEntry entry) {
		interpreter.println(String.format(COLUMN_FORMAT, 
				i,
				StringUtils.truncate(entry.getDescription(), 50),
				StringUtils.truncate(entry.getUser(), 16),
				entry.getScheduleDate() != null ? Dates.formatByHostTimeZone(entry.getScheduleDate(), DateFormats.MEDIUM) : "Unknown",
				entry.getStartDate() != null ? Dates.formatByHostTimeZone(entry.getStartDate(), DateFormats.MEDIUM) : "",
				StringUtils.truncate(StringUtils.capitalizeFirstLetter(entry.getState().toString().toLowerCase()), 16)));
	}

	private void printSeparator(final CommandInterpreter interpreter) {
		interpreter.println(Strings.repeat("-", 135));
	}

	public synchronized void cancelJob(final CommandInterpreter interpreter) {

		if (lastRetrievedEntries.isEmpty()) {
			interpreter.println("Please retrieve the list of currently scheduled or running jobs first.");
			return;
		}
		
		final String jobId = interpreter.nextArgument();
		
		if (StringUtils.isEmpty(jobId)) {
			interpreter.println("Command usage: remoteJobs cancel [jobId]");
			return;
		}
		
		final int parsedJobId;
		
		try {
			parsedJobId = Integer.parseInt(jobId);
		} catch (NumberFormatException e) {
			interpreter.println(MessageFormat.format("Job identifier ''{0}''is not a valid integer.", jobId));
			return;
		}

		if (parsedJobId < 0 || parsedJobId >= lastRetrievedEntries.size()) {
			interpreter.println(MessageFormat.format("Job identifier must be between 0 and {0}.", lastRetrievedEntries.size()));
		}
		
		final RemoteJobEntry jobToCancel = lastRetrievedEntries.get(parsedJobId);
		JobRequests.prepareCancel(jobToCancel.getId()).buildAsync().execute(getBus()).getSync();
		lastRetrievedEntries.clear();
		
		interpreter.println(MessageFormat.format("Requesting job {0} to cancel.", parsedJobId));
	}

	private IEventBus getBus() {
		return getApplicationContext().getService(IEventBus.class);
	}

	private ApplicationContext getApplicationContext() {
		return ApplicationContext.getInstance();
	}
}