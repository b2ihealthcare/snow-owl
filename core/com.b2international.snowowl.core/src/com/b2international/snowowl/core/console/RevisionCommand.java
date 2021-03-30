/*
 * Copyright 2021 B2i Healthcare Pte Ltd, http://b2i.sg
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

import java.io.PrintStream;
import java.time.Instant;
import java.time.ZoneOffset;

import com.b2international.commons.exceptions.NotFoundException;
import com.b2international.index.Hits;
import com.b2international.index.query.Expressions;
import com.b2international.index.query.Query;
import com.b2international.index.revision.Revision;
import com.b2international.index.revision.RevisionBranch;
import com.b2international.index.revision.RevisionBranchPoint;
import com.b2international.index.revision.RevisionIndex;
import com.b2international.snowowl.core.RepositoryManager;
import com.b2international.snowowl.core.plugin.Component;
import com.b2international.snowowl.core.repository.RevisionDocument;
import com.fasterxml.jackson.databind.JsonNode;
import com.google.common.collect.HashMultiset;
import com.google.common.collect.Multiset;
import com.google.common.primitives.Longs;

import picocli.CommandLine;
import picocli.CommandLine.HelpCommand;
import picocli.CommandLine.Parameters;

/**
 * @since 7.17.0
 */
@Component
@picocli.CommandLine.Command(
	name = "revision",
	header = "Provides subcommands to check/access revision index content reflectively",
	description = "Provides subcommands to reflectively access revision index content",
	subcommands = {
		HelpCommand.class,
		RevisionCommand.CheckCommand.class,
		RevisionCommand.HistoryCommand.class
	}
)
public class RevisionCommand extends BaseCommand {

	@Override
	public void run(CommandLineStream out) {
		CommandLine.usage(this, (PrintStream) out);
	}
	
	@picocli.CommandLine.Command(
		name = "check",
		header = "Checks branch content integrity in a revision index",
		description = "Check revision index content"
	)
	private static final class CheckCommand extends Command {

		@Parameters(paramLabel = "REPOSITORY", description = "The repositoryId to access the underlying the revision index")
		String repositoryId;
		
		@Parameters(paramLabel = "BRANCH", description = "The branch to check in the given repository's revision index")
		String branch = "MAIN";
		
		@Override
		public void run(CommandLineStream out) {
			RevisionIndex index = getContext().service(RepositoryManager.class).get(repositoryId).service(RevisionIndex.class);
			out.println("Checking integrity of '%s/%s'...");
			index.read(branch, searcher -> {
				for (Class<?> type : index.admin().mappings().getTypes()) {
					if (Revision.class.isAssignableFrom(type)) {
						out.println("Searching duplicates in " + type);
						Iterable<Hits<String>> allDocumentsVisibleFromBranchSortedById = searcher.scroll(Query.select(String.class)
								.from(type)
								.fields(Revision.Fields.ID) // load only the revision ID, if an ID reported multiple times, then it has more than visible doc
								.where(Expressions.matchAll())
								.limit(50_000)
								.build());
						
						Multiset<String> ids = HashMultiset.create();
						for (Hits<String> hits : allDocumentsVisibleFromBranchSortedById) {
							hits.forEach(ids::add);
						}
						
						ids.entrySet().stream().filter(e -> e.getCount() > 1).forEach(out::println);
					}
				}
				return null;
			});
		}
		
	}
	
	@picocli.CommandLine.Command(
		name = "history",
		header = "Prints the history of a single component in a revision index",
		description = "Print document history from a revision index"
	)
	private static final class HistoryCommand extends Command {

		@Parameters(paramLabel = "REPOSITORY", description = "The repositoryId to access the underlying the revision index")
		String repositoryId;
		
		@Parameters(paramLabel = "ID", description = "The component id to load low-level history for")
		String id;
		
		@Override
		public void run(CommandLineStream out) {
			RevisionIndex index = getContext().service(RepositoryManager.class).get(repositoryId).service(RevisionIndex.class);
			out.println("Loading history of '%s/%s'...");
			index.index().read(searcher -> {
				// search in each mapping for now and break when we find something
				boolean found = false;
				for (Class<?> type : index.admin().mappings().getTypes()) {
					if (found) break;
					if (Revision.class.isAssignableFrom(type)) {
						Iterable<Hits<JsonNode>> hits = searcher.scroll(Query.select(JsonNode.class)
								.from(type)
								.where(RevisionDocument.Expressions.id(id))
								.limit(100)
								.build());
						
						for (Hits<JsonNode> revisions : hits) {
							found = true;
							revisions
								.stream()
								.sorted((j1, j2) -> {
									var j1Created = RevisionBranchPoint.valueOf(j1.get("created").asText());
									var j2Created = RevisionBranchPoint.valueOf(j2.get("created").asText());
									return Longs.compare(j1Created.getTimestamp(), j2Created.getTimestamp());
								})
								.forEach(c -> {
									var branchPoint = RevisionBranchPoint.valueOf(c.get("created").asText());
									try {
										RevisionBranch branch = index.branching().getBranch(branchPoint.getBranchId());
										out.println(Instant.ofEpochMilli(branchPoint.getTimestamp()).atOffset(ZoneOffset.UTC).toLocalDateTime() + " - " + branch.getPath() + " - " + c);
									} catch (NotFoundException e) {
										// unknown branch, probably something that was a test or had to be removed, ignore revision
									}
								});	
						}
						
					}
				}
				return null;
			});
		}
		
	}
	
	
}
