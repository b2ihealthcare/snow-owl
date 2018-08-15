/*
 * Copyright 2018 B2i Healthcare Pte Ltd, http://b2i.sg
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
package com.b2international.snowowl.datastore.remotejobs;

import java.util.List;
import java.util.concurrent.TimeUnit;

import com.b2international.commons.CompareUtils;
import com.b2international.commons.StringUtils;
import com.b2international.snowowl.core.console.Command;
import com.b2international.snowowl.core.console.CommandLineStream;
import com.b2international.snowowl.core.date.DateFormats;
import com.b2international.snowowl.core.date.Dates;
import com.b2international.snowowl.core.request.SearchResourceRequest.SortField;
import com.b2international.snowowl.datastore.request.job.JobRequests;
import com.google.common.base.Strings;

import picocli.CommandLine.HelpCommand;
import picocli.CommandLine.Parameters;

/**
 * @since 7.0
 */
@picocli.CommandLine.Command(
	name = "jobs",
	header = "Display and manage long-running operations",
	description = "Displays and provides subcommands to manage long-running operations",
	subcommands = {
		HelpCommand.class,
		JobsCommand.CancelCommand.class
	}
)
public final class JobsCommand extends Command {

	@Override
	public void run(CommandLineStream out) {
		List<RemoteJobEntry> entries = JobRequests.prepareSearch().all()
				.sortBy(SortField.ascending(RemoteJobEntry.Fields.SCHEDULE_DATE))
				.buildAsync()
				.execute(getBus())
				.getSync()
				.getItems();
		
		if (CompareUtils.isEmpty(entries)) {
			out.println("No remote jobs are currently scheduled or running on the server.");
			return;
		}
		
		out.println();
		printHeader(out);
		printSeparator(out);
		
		for (int i = 0; i < entries.size(); i++) {
			final RemoteJobEntry entry = entries.get(i);
			printLineForEntry(out, i, entry);
			printSeparator(out);
		}
	}
	
	private static final String COLUMN_FORMAT = "%4s | %-50s | %-16s | %-16s | %-16s | %-16s";

	private void printHeader(final CommandLineStream interpreter) {
		interpreter.println(COLUMN_FORMAT, "Id", "Description", "Owner", "Scheduled", "Started", "Status");
	}
	
	private void printLineForEntry(final CommandLineStream interpreter, int i, final RemoteJobEntry entry) {
		interpreter.println(COLUMN_FORMAT, 
				i,
				StringUtils.truncate(entry.getDescription(), 50),
				StringUtils.truncate(entry.getUser(), 16),
				entry.getScheduleDate() != null ? Dates.formatByHostTimeZone(entry.getScheduleDate(), DateFormats.MEDIUM) : "Unknown",
				entry.getStartDate() != null ? Dates.formatByHostTimeZone(entry.getStartDate(), DateFormats.MEDIUM) : "",
				StringUtils.truncate(StringUtils.capitalizeFirstLetter(entry.getState().toString().toLowerCase()), 16));
	}

	private void printSeparator(final CommandLineStream interpreter) {
		interpreter.println(Strings.repeat("-", 135));
	}
	
	@picocli.CommandLine.Command(
		name = "cancel",
		header = "",
		description = ""
	)
	public static final class CancelCommand extends Command {

		@Parameters(paramLabel = "JOB_ID", description = "The identifier of the job to cancel")
		String jobId;
		
		@Override
		public void run(CommandLineStream out) {
			final RemoteJobEntry job = JobRequests.prepareGet(jobId).buildAsync().execute(getBus()).getSync(1, TimeUnit.MINUTES);
			out.println("Cancelling job ['%s' created by '%s']...", job.getDescription(), job.getUser());
			JobRequests.prepareCancel(jobId).buildAsync().execute(getBus()).getSync();
			out.println("Cancelled job ['%s' created by '%s'] successfully.", job.getDescription(), job.getUser());
		}
		
	}

}
