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
package com.b2international.snowowl.core.console;

import java.util.concurrent.TimeUnit;

import com.b2international.commons.exceptions.NotFoundException;
import com.b2international.commons.extension.Component;
import com.b2international.snowowl.core.ApplicationContext;
import com.b2international.snowowl.core.Repository;
import com.b2international.snowowl.core.RepositoryManager;
import com.b2international.snowowl.core.branch.Branch;
import com.b2international.snowowl.core.date.DateFormats;
import com.b2international.snowowl.core.date.Dates;
import com.b2international.snowowl.datastore.request.RepositoryRequests;
import com.google.common.base.Splitter;
import com.google.common.collect.Iterables;
import com.google.common.primitives.Longs;

import picocli.CommandLine;
import picocli.CommandLine.Option;
import picocli.CommandLine.Parameters;

/**
 * @since 7.0
 */
@Component
@CommandLine.Command(
	name = "branches",
	header = "Displays information about branches in a repository",
	description = ""
)
public final class BranchesCommand extends Command {

	private static final String DEFAULT_BRANCH_PREFIX = "|--";
	private static final String DEFAULT_INDENT = "   ";
	
	@Parameters(paramLabel = "REPOSITORY", description = "The repository identifier to display branches for")
	String repositoryId;
	
	@Option(names = { "-b", "--branch" }, description = "Select a single branch path to display. Has to be the full path of the branch (e.g. MAIN/PROJECT/TASK).", defaultValue = Branch.MAIN_PATH)
	String branchPath;
			
	@Override
	public void run(CommandLineStream out) {
		if (isValidRepositoryName(repositoryId, out)) {
			
			if (!branchPath.startsWith(Branch.MAIN_PATH)) {
				out.println("Specify branch with full path. i.e. MAIN/PROJECT/TASK1. Got: '%s'", branchPath);
				return;
			}
			
			
			try {
				Branch parentBranch = RepositoryRequests.branching()
						 			.prepareGet(branchPath)
						 			.setExpand("children(direct:false)")
						 			.build(repositoryId)
						 			.execute(getBus())
						 			.getSync(1, TimeUnit.MINUTES);
				out.println("Branch hierarchy for '%s' in repository '%s':", branchPath, repositoryId);
				print(parentBranch, getDepthOfBranch(parentBranch), out);
			} catch (NotFoundException e) {
				out.println("Unable to find branch '%s'", branchPath);
				return;
			}
			
		}
	}
	
	private void print(final Branch branch, final int parentDepth, CommandLineStream out) {
		printBranch(branch, getDepthOfBranch(branch) - parentDepth, out);
		
		branch.getChildren()
			.stream()
			.sorted((c1, c2) -> Longs.compare(c1.baseTimestamp(), c2.baseTimestamp()))
			.forEach(child -> {
				print(child, parentDepth, out);
			});
	}

	private void printBranch(Branch branch, int depth, CommandLineStream out) {
		out.println("%-30s %-12s B: %s H: %s",
				String.format("%s%s%s", 
				getIndentationForBranch(depth), 
				DEFAULT_BRANCH_PREFIX, 
				branch.name()),
				String.format("[%s]", branch.state()),
				Dates.formatByGmt(branch.baseTimestamp(), DateFormats.LONG), 
				Dates.formatByGmt(branch.headTimestamp(), DateFormats.LONG));
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
	
	private boolean isValidRepositoryName(String repositoryId, CommandLineStream out) {
		RepositoryManager repositoryManager = getRepositoryManager();
		Repository repository = repositoryManager.get(repositoryId);
		if (repository == null) {
			out.println("Could not find repository '%s'", repositoryId);
			out.println("Available repositories are:");
			repositoryManager
				.repositories()
				.forEach(r -> out.println("\t%s", r.id()));
			return false;
		}
		return true;
	}
	
	private RepositoryManager getRepositoryManager() {
		return ApplicationContext.getServiceForClass(RepositoryManager.class);
	}

}
