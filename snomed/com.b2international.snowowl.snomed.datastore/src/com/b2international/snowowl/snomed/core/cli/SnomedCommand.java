/*
 * Copyright 2018-2021 B2i Healthcare Pte Ltd, http://b2i.sg
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
package com.b2international.snowowl.snomed.core.cli;

import java.nio.file.Paths;
import java.time.Instant;
import java.time.ZoneOffset;

import com.b2international.commons.exceptions.NotFoundException;
import com.b2international.index.query.Query;
import com.b2international.index.revision.RevisionBranch;
import com.b2international.index.revision.RevisionBranchPoint;
import com.b2international.index.revision.RevisionIndex;
import com.b2international.snowowl.core.RepositoryManager;
import com.b2international.snowowl.core.attachments.Attachment;
import com.b2international.snowowl.core.console.Command;
import com.b2international.snowowl.core.console.CommandLineStream;
import com.b2international.snowowl.core.identity.Permission;
import com.b2international.snowowl.core.identity.User;
import com.b2international.snowowl.core.plugin.Component;
import com.b2international.snowowl.core.repository.RevisionDocument;
import com.b2international.snowowl.core.request.io.ImportResponse;
import com.b2international.snowowl.snomed.core.domain.Rf2ReleaseType;
import com.b2international.snowowl.snomed.datastore.index.entry.SnomedConceptDocument;
import com.b2international.snowowl.snomed.datastore.request.SnomedRequests;
import com.fasterxml.jackson.databind.JsonNode;
import com.google.common.primitives.Longs;

import picocli.CommandLine.HelpCommand;
import picocli.CommandLine.Option;
import picocli.CommandLine.Parameters;

/**
 * @since 7.0
 */
@Component
@picocli.CommandLine.Command(
	name = "snomed",
	header = "Provides subcommands to manage SNOMED CT content",
	description = "Provides subcommands to manage SNOMED CT content",
	subcommands = {
		HelpCommand.class,
		SnomedCommand.ImportCommand.class,
		SnomedCommand.RevisionCheckCommand.class
	}
)
public final class SnomedCommand extends Command {

	@Override
	public void run(CommandLineStream out) {
//		CommandLine.usage(this, (PrintStream) out);
	}

	@picocli.CommandLine.Command(
		name = "import",
		header = "Imports SNOMED CT content",
		description = "Imports SNOMED CT content"
	)
	private static final class ImportCommand extends Command {
		
		private static final String SUPPORTED_FORMAT = "rf2";
		
		@Option(names = { "-r", "--resource" }, description = "The target branch. After a successful import all importable content will be accessible from this branch.", required = true)
		String resource;
		
		@Option(names = { "-f", "--format" }, description = "The import file format. Currently 'rf2' is supported only.", defaultValue = SUPPORTED_FORMAT)
		String format = SUPPORTED_FORMAT;
		
		@Option(names = { "-t" }, description = "The importable release type from an RF2 compatible file.", defaultValue = "FULL")
		Rf2ReleaseType rf2ReleaseType = Rf2ReleaseType.FULL;
		
		@Option(names = { "-v" }, description = "Whether to create versions for the underlying code system or just import the content.", defaultValue = "true")
		boolean createVersions;
		
		@Parameters(paramLabel = "PATH", description = "The absolute path to the importable file")
		String path;
		
		@Override
		public void run(CommandLineStream out) {
			if (!SUPPORTED_FORMAT.equalsIgnoreCase(format)) {
				out.println("Unrecognized import format: '%s'. Supported formats are: %s", format, SUPPORTED_FORMAT);
			}
			
			final User user = out.authenticate(getBus());
			
			if (user == null || !user.hasPermission(Permission.requireAll(Permission.OPERATION_IMPORT, resource))) {
				out.println("User is unauthorized to import SNOMED CT content.");
				return;
			}
			
			final Attachment rf2Archive = Attachment.upload(getContext(), Paths.get(path));
			final ImportResponse response = SnomedRequests.rf2().prepareImport()
					.setCreateVersions(createVersions)
					.setRf2Archive(rf2Archive)
					.setReleaseType(rf2ReleaseType)
					.build(resource)
					.execute(getBus())
					.getSync();
			
			if (response.isSuccess()) {
				out.println("Successfully imported SNOMED CT content from file '%s'.", path);
			} else {
				out.println("Failed to import SNOMED CT content from file '%s'. %s", path, response.getError());
			}
		}
	}
	
	@picocli.CommandLine.Command(
		name = "revision",
		header = "Prints revision information about a SNOMED CT concept",
		description = "Prints revision information about a SNOMED CT concept"
	)
	private static final class RevisionCheckCommand extends Command {
		
		@Parameters(index = "0", paramLabel = "BRANCH", description = "The branch to filter the revisions")
		String branch;
		
		@Parameters(index = "1", paramLabel = "CONCEPT_ID", description = "The concept id to show revisions for")
		String conceptId;
		
		@Override
		public void run(CommandLineStream out) {
			RevisionIndex index = getContext().service(RepositoryManager.class).get("snomedStore").service(RevisionIndex.class);
			index.read(branch, searcher -> {
				System.err.println("Fetching revisions...");
				searcher.search(Query.select(JsonNode.class)
						.from(SnomedConceptDocument.class)
						.where(RevisionDocument.Expressions.id(conceptId))
						.limit(Integer.MAX_VALUE)
						.build())
						.forEach(System.err::println);
				
//				searcher.search(Query.select(JsonNode.class)
//						.from(Commit.class)
//						.where(Commit.Expressions.id("d7bb66bf-4d5f-463e-a5e2-007bb2af36d3"))
//						.build())
//						.forEach(System.err::println);
				
				searcher.searcher().search(Query.select(JsonNode.class)
						.from(SnomedConceptDocument.class)
						.where(RevisionDocument.Expressions.id(conceptId))
						.limit(Integer.MAX_VALUE)
						.build())
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
								System.err.println(Instant.ofEpochMilli(branchPoint.getTimestamp()).atOffset(ZoneOffset.UTC).toLocalDateTime() + " - " + branch.getPath() + " - " + c);
							} catch (NotFoundException e) {
								// unknown branch, probably something that was a test or had to be removed, ignore revision
							}
						});
				
				System.err.println("Done");
				return null;
			});			
		}
		
	}
}
