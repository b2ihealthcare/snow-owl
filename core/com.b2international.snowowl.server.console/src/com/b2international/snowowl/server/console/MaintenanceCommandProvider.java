/*
 * Copyright 2011-2016 B2i Healthcare Pte Ltd, http://b2i.sg
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

import static com.google.common.base.Strings.isNullOrEmpty;
import static com.google.common.collect.Lists.newArrayList;

import java.nio.file.Paths;
import java.util.Comparator;
import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import org.eclipse.osgi.framework.console.CommandInterpreter;
import org.eclipse.osgi.framework.console.CommandProvider;
import org.slf4j.LoggerFactory;

import com.b2international.commons.StringUtils;
import com.b2international.index.revision.Purge;
import com.b2international.snowowl.core.ApplicationContext;
import com.b2international.snowowl.core.Repositories;
import com.b2international.snowowl.core.RepositoryInfo;
import com.b2international.snowowl.core.branch.Branch;
import com.b2international.snowowl.core.date.DateFormats;
import com.b2international.snowowl.core.date.Dates;
import com.b2international.snowowl.core.exceptions.NotFoundException;
import com.b2international.snowowl.core.request.SearchResourceRequest.SortField;
import com.b2international.snowowl.datastore.cdo.ICDORepositoryManager;
import com.b2international.snowowl.datastore.commitinfo.CommitInfo;
import com.b2international.snowowl.datastore.commitinfo.CommitInfoDocument;
import com.b2international.snowowl.datastore.request.RepositoryRequests;
import com.b2international.snowowl.datastore.request.repository.RepositorySearchRequestBuilder;
import com.b2international.snowowl.datastore.server.ServerDbUtils;
import com.b2international.snowowl.datastore.server.migrate.MigrateRequest;
import com.b2international.snowowl.datastore.server.migrate.MigrateRequestBuilder;
import com.b2international.snowowl.datastore.server.migrate.MigrationResult;
import com.b2international.snowowl.datastore.server.reindex.OptimizeRequest;
import com.b2international.snowowl.datastore.server.reindex.PurgeRequest;
import com.b2international.snowowl.datastore.server.reindex.ReindexRequest;
import com.b2international.snowowl.datastore.server.reindex.ReindexRequestBuilder;
import com.b2international.snowowl.datastore.server.reindex.ReindexResult;
import com.b2international.snowowl.eventbus.IEventBus;
import com.google.common.base.Function;
import com.google.common.base.Joiner;
import com.google.common.base.Predicate;
import com.google.common.base.Splitter;
import com.google.common.base.Strings;
import com.google.common.collect.FluentIterable;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.google.common.primitives.Ints;
import com.google.common.primitives.Longs;

/**
 * OSGI command contribution with Snow Owl maintenance type commands.
 */
public class MaintenanceCommandProvider implements CommandProvider {

	private static final String DEFAULT_BRANCH_PREFIX = "|--";
	private static final String DEFAULT_INDENT = "   ";
	private static final String LISTBRANCHES_COMMAND = "listbranches";
	private static final String DBCREATEINDEX_COMMAND = "dbcreateindex";
	private static final String REPOSITORIES_COMMAND = "repositories";
	private static final String VERSION_COMMAND = "--version";
	
	@Override
	public String getHelp() {
		StringBuffer buffer = new StringBuffer();
		buffer.append("---Snow Owl commands---\n");
		buffer.append("\tsnowowl --version - returns the current version\n");
		buffer.append("\tsnowowl dbcreateindex [nsUri] - creates the CDO_CREATED index on the proper DB tables for all classes contained by a package identified by its unique namespace URI.\n");
		buffer.append("\tsnowowl listrepositories - prints all the repositories in the system. \n");
		buffer.append("\tsnowowl listbranches [repository] [branchPath] - prints all the child branches of the specified branch path in the system for a repository. Branch path is MAIN by default and has to be full path (e.g. MAIN/PROJECT/TASK)\n");
		buffer.append("\tsnowowl reindex [repositoryId] [failedCommitTimestamp] - reindexes the content for the given repository ID from the given failed commit timestamp (optional, default timestamp is 1 which means no failed commit).\n");
		buffer.append("\tsnowowl optimize [repositoryId] [maxSegments] - optimizes the underlying index for the repository to have the supplied maximum number of segments (default number is 1)\n");
		buffer.append("\tsnowowl purge [repositoryId] [branchPath] [ALL|LATEST|HISTORY] - optimizes the underlying index by deleting unnecessary documents from the given branch using the given purge strategy (default strategy is LATEST)\n");
		buffer.append("\tsnowowl migrate [repositoryId] [remoteLocation] [-s scriptLocation] [-t commitTimestamp]"
				+ " - migrates content from a remote database into the given repository (optionally you can specify a script to run before each"
				+ " commit and/or the start commit timestamp). If commitTimestamp is not specified the latest commit of the current dataset will be used \n");
		buffer.append("\tsnowowl repositories [repositoryId] - prints all currently available repositories and their health statuses");
		return buffer.toString();
	}

	/**
	 * Reflective template method declaratively registered. Needs to start with
	 * "_".
	 * 
	 * @param interpreter
	 * @throws InterruptedException
	 */
	public void _snowowl(CommandInterpreter interpreter) throws InterruptedException {
		String cmd = interpreter.nextArgument();
		try {
			if (DBCREATEINDEX_COMMAND.equals(cmd)) {
				createDbIndex(interpreter);
				return;
			}

			if (LISTBRANCHES_COMMAND.equals(cmd)) {
				listBranches(interpreter);
				return;
			}
			
			if (REPOSITORIES_COMMAND.equals(cmd)) {
				repositories(interpreter);
				return;
			}
			
			if (VERSION_COMMAND.equals(cmd)) {
				String version = RepositoryRequests.prepareGetServerInfo().buildAsync().execute(getBus()).getSync().version();
				interpreter.println(version);
				return;
			}

			if ("reindex".equals(cmd)) {
				reindex(interpreter);
				return;
			}
			
			if ("optimize".equals(cmd)) {
				optimize(interpreter);
				return; 
			}
			
			if ("purge".equals(cmd)) {
				purge(interpreter);
				return;
			}
			
			if ("migrate".equals(cmd)) {
				migrate(interpreter);
				return;
			}
			
			interpreter.println(getHelp());
		} catch (Exception ex) {
			LoggerFactory.getLogger("console").error("Failed to execute command", ex);
			if (Strings.isNullOrEmpty(ex.getMessage())) {
				interpreter.println("Something went wrong during the processing of your request.");
			} else {
				interpreter.println(ex.getMessage());
			}
		}
	}

	private static final String COLUMN_FORMAT = "|%-16s|%-16s|%-16s|";
	
	private void repositories(CommandInterpreter interpreter) {
		final String repositoryId = interpreter.nextArgument();
		RepositorySearchRequestBuilder req = RepositoryRequests.prepareSearch();
		if (!Strings.isNullOrEmpty(repositoryId)) {
			req.one().filterById(repositoryId);
		} else {
			req.all();
		}
		final Repositories repositories = req.buildAsync().execute(getBus()).getSync();
		
		final int maxDiagLength = ImmutableList.copyOf(repositories)
			.stream()
			.map(RepositoryInfo::diagnosis)
			.map(Strings::nullToEmpty)
			.map(diag -> diag.length())
			.max(Ints::compare)
			.orElse(16);

		final int maxLength = Math.max(maxDiagLength + 36, 52);
		
		printSeparator(interpreter, maxLength);
		printHeader(interpreter, "id", "health", Strings.padEnd("diagnosis", maxDiagLength, ' '));
		printSeparator(interpreter, maxLength);
		repositories.forEach(repository -> {
			printLine(interpreter, repository, RepositoryInfo::id, RepositoryInfo::health, repo -> Strings.isNullOrEmpty(repo.diagnosis()) ? "-" : null);
			printSeparator(interpreter, maxLength);
		});
	}
	
	private void printHeader(final CommandInterpreter interpreter, Object...columns) {
		interpreter.println(String.format(COLUMN_FORMAT, columns));
	}
	
	private void printSeparator(final CommandInterpreter interpreter, int length) {
		interpreter.println(Strings.repeat("-", length));
	}
	
	private <T> void printLine(final CommandInterpreter interpreter, T item, Function<T, Object>...values) {
		interpreter.println(String.format(COLUMN_FORMAT, newArrayList(values).stream().map(func -> func.apply(item)).toArray()));
	}

	private List<String> resolveArguments(CommandInterpreter interpreter) {
		List<String> results = Lists.newArrayList();
		String argument = interpreter.nextArgument();
		while(!isNullOrEmpty(argument)) {
			results.add(argument);
			argument = interpreter.nextArgument();
		}
		
		return results;
	}

	public synchronized void createDbIndex(CommandInterpreter interpreter) {
		String nsUri = interpreter.nextArgument();
		if (!Strings.isNullOrEmpty(nsUri)) {
			ServerDbUtils.createCdoCreatedIndexOnTables(nsUri);
		} else {
			interpreter.println("Namespace URI should be specified.");
		}
	}

	private void purge(CommandInterpreter interpreter) {
		final String repositoryId = interpreter.nextArgument();
		
		if (Strings.isNullOrEmpty(repositoryId)) {
			interpreter.println("RepositoryId parameter is required");
			return;
		}
		
		final String branchPath = interpreter.nextArgument();
		
		if (Strings.isNullOrEmpty(branchPath)) {
			interpreter.print("BranchPath parameter is required");
			return;
		}
		
		
		final String purgeArg = interpreter.nextArgument();
		final Purge purge = Strings.isNullOrEmpty(purgeArg) ? Purge.LATEST : Purge.valueOf(purgeArg);
		if (purge == null) {
			interpreter.print("Invalid purge parameter. Select one of " + Joiner.on(",").join(Purge.values()));
			return;
		}
		
		PurgeRequest.builder()
			.setBranchPath(branchPath)
			.setPurge(purge)
			.build(repositoryId)
			.execute(getBus())
			.getSync();
	}

	private void reindex(CommandInterpreter interpreter) {
		final String repositoryId = interpreter.nextArgument();
		
		if (Strings.isNullOrEmpty(repositoryId)) {
			interpreter.println("RepositoryId parameter is required");
			return;
		}
		
		final ReindexRequestBuilder req = ReindexRequest.builder();
		
		final String failedCommitTimestamp = interpreter.nextArgument();
		if (!StringUtils.isEmpty(failedCommitTimestamp)) {
			req.setFailedCommitTimestamp(Long.parseLong(failedCommitTimestamp));
		}
		
		final ReindexResult result = req
				.build(repositoryId)
				.execute(getBus())
				.getSync();
		
		interpreter.println(result.getMessage());
	}
	
	private void migrate(CommandInterpreter interpreter) {
		final String repositoryId = interpreter.nextArgument();
		
		if (Strings.isNullOrEmpty(repositoryId)) {
			interpreter.println("RepositoryId parameter is required");
			return;
		}
		
		final String remoteLocation = interpreter.nextArgument();
		
		if (Strings.isNullOrEmpty(remoteLocation)) {
			interpreter.println("Remote location parameter is required (host:[port])");
			return;
		}

		final MigrateRequestBuilder req = MigrateRequest.builder(remoteLocation);
		
		Long providedTimestamp = null;
		
		String nextArg;
		while (!Strings.isNullOrEmpty((nextArg = interpreter.nextArgument()))) {
			final String value = interpreter.nextArgument();
			switch (nextArg) {
			case "-t":
				try {
					providedTimestamp = Long.parseLong(value);
				} catch (NumberFormatException e) {
					interpreter.println(String.format("Error: Invalid commitTimestamp value (was: '%s', expected long number)", value));
					return;
				}
				break;
			case "-s":
				if (Strings.isNullOrEmpty(value)) {
					interpreter.println("Error: Path to script is missing");
					return;
				}
				if (!Paths.get(value).toFile().exists()) {
					interpreter.println(String.format("Error: Script at '%s' cannot be found", value));
					return;
				}
				req.setScriptLocation(value);
				break;
			default: 
				interpreter.println("Error: Unknown optional parameter " + nextArg);
				return;
			}
		}
		
		if (providedTimestamp != null) {
			req.setCommitTimestamp(providedTimestamp);
		} else {
			
			RepositoryRequests.commitInfos().prepareSearchCommitInfo()
				.one()
				.sortBy(new SortField(CommitInfoDocument.Fields.TIME_STAMP, false))
				.build(repositoryId)
				.execute(getBus())
				.getSync()
				.first()
				.map(CommitInfo::getTimeStamp)
				.map(value -> value + 1)
				.ifPresent(req::setCommitTimestamp);
			
		}
		
		MigrationResult result = req.build(repositoryId)
			.execute(getBus())
			.getSync();
		
		interpreter.println(String.format("Migration of '%s' repository successfully completed from source '%s'. Result: %s", repositoryId, remoteLocation, result.getMessage()));
	}

	private static IEventBus getBus() {
		return ApplicationContext.getServiceForClass(IEventBus.class);
	}
	
	private void optimize(CommandInterpreter interpreter) {
		final String repositoryId = interpreter.nextArgument();
		if (Strings.isNullOrEmpty(repositoryId)) {
			interpreter.println("RepositoryId parameter is required.");
			return;
		}
		
		// default max segments is 1
		int maxSegments = 1;
		final String maxSegmentsArg = interpreter.nextArgument();
		if (!Strings.isNullOrEmpty(maxSegmentsArg)) {
			maxSegments = Integer.parseInt(maxSegmentsArg);
		}

		interpreter.println("Optimizing index to max. " + maxSegments + " number of segments...");
		OptimizeRequest.builder()
			.setMaxSegments(maxSegments)
			.build(repositoryId)
			.execute(getBus())
			.getSync();
		interpreter.println("Index optimization completed.");
	}

	public synchronized void listBranches(CommandInterpreter interpreter) {
		String repositoryUUID = interpreter.nextArgument();
		
		if (isValidRepositoryName(repositoryUUID, interpreter)) {
			
			String parentBranchPath = interpreter.nextArgument();
			
			if (Strings.isNullOrEmpty(parentBranchPath)) {
				interpreter.println("Parent branch path was not specified, falling back to MAIN");
				parentBranchPath = Branch.MAIN_PATH;
			} else if (!parentBranchPath.startsWith(Branch.MAIN_PATH)) {
				interpreter.println("Specify parent branch with full path. i.e. MAIN/PROJECT/TASK1");
				return;
			}
			
			Branch parentBranch = null;
			
			try {
				 parentBranch = RepositoryRequests.branching()
						 			.prepareGet(parentBranchPath)
						 			.build(repositoryUUID)
						 			.execute(getBus())
						 			.getSync(1000, TimeUnit.MILLISECONDS);
			} catch (NotFoundException e) {
				interpreter.println(String.format("Unable to find %s", parentBranchPath));
				return;
			}
			
			if (parentBranch != null) {
				interpreter.println(String.format("Branch hierarchy for %s in repository %s:", parentBranchPath, repositoryUUID));
				print(parentBranch, getDepthOfBranch(parentBranch), interpreter);
			}
			
		}
	}
	
	private void print(final Branch branch, final int parentDepth, CommandInterpreter interpreter) {
		
		printBranch(branch, getDepthOfBranch(branch) - parentDepth, interpreter);
		
		List<? extends Branch> children = FluentIterable.from(branch.children()).filter(new Predicate<Branch>() {
			@Override
			public boolean apply(Branch input) {
				return input.parentPath().equals(branch.path());
			}
		}).toSortedList(new Comparator<Branch>() {
			@Override
			public int compare(Branch o1, Branch o2) {
				return Longs.compare(o1.baseTimestamp(), o2.baseTimestamp());
			}
		});
		
		if (children.size() != 0) {
			for (Branch child : children) {
				print(child, parentDepth, interpreter);
			}
		}
		
	}

	private void printBranch(Branch branch, int depth, CommandInterpreter interpreter) {
		interpreter.println(String.format("%-30s %-12s B: %s H: %s",
				String.format("%s%s%s", 
				getIndentationForBranch(depth), 
				DEFAULT_BRANCH_PREFIX, 
				branch.name()),
				String.format("[%s]", branch.state()),
				Dates.formatByGmt(branch.baseTimestamp(), DateFormats.LONG), 
				Dates.formatByGmt(branch.headTimestamp(), DateFormats.LONG)));
	}
		
	private String getIndentationForBranch(int depth) {
		String indent = "";
		for (int i = 0; i < depth; i++) {
			indent += DEFAULT_INDENT;
		}
		return indent;
	}

	private int getDepthOfBranch(Branch currentBranch) {
		return Iterables.size(Splitter.on(Branch.SEPARATOR).split(currentBranch.path()));
	}
	
	private boolean isValidRepositoryName(String repositoryName, CommandInterpreter interpreter) {
		Set<String> uuidKeySet = getRepositoryManager().uuidKeySet();
		if (!uuidKeySet.contains(repositoryName)) {
			interpreter.println("Could not find repository called: " + repositoryName);
			interpreter.println("Available repository names are: " + uuidKeySet);
			return false;
		}
		return true;
	}

	private ICDORepositoryManager getRepositoryManager() {
		return ApplicationContext.getServiceForClass(ICDORepositoryManager.class);
	}

}