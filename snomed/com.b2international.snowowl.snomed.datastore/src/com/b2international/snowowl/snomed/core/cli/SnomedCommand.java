/*
 * Copyright 2018-2020 B2i Healthcare Pte Ltd, http://b2i.sg
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

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintStream;
import java.time.Instant;
import java.time.ZoneOffset;
import java.util.Iterator;
import java.util.UUID;

import com.b2international.commons.exceptions.NotFoundException;
import com.b2international.index.Hits;
import com.b2international.index.query.Query;
import com.b2international.index.revision.Commit;
import com.b2international.index.revision.RevisionBranch;
import com.b2international.index.revision.RevisionBranchPoint;
import com.b2international.index.revision.RevisionIndex;
import com.b2international.index.util.JsonDiff;
import com.b2international.snowowl.core.ApplicationContext;
import com.b2international.snowowl.core.RepositoryManager;
import com.b2international.snowowl.core.attachments.AttachmentRegistry;
import com.b2international.snowowl.core.console.Command;
import com.b2international.snowowl.core.console.CommandLineStream;
import com.b2international.snowowl.core.identity.Permission;
import com.b2international.snowowl.core.identity.User;
import com.b2international.snowowl.core.plugin.Component;
import com.b2international.snowowl.core.repository.RevisionDocument;
import com.b2international.snowowl.core.request.io.ImportResponse;
import com.b2international.snowowl.snomed.common.SnomedTerminologyComponentConstants;
import com.b2international.snowowl.snomed.core.domain.Rf2ReleaseType;
import com.b2international.snowowl.snomed.datastore.SnomedDatastoreActivator;
import com.b2international.snowowl.snomed.datastore.index.entry.SnomedConceptDocument;
import com.b2international.snowowl.snomed.datastore.request.SnomedRequests;
import com.fasterxml.jackson.databind.JsonNode;
import com.google.common.primitives.Longs;

import picocli.CommandLine;
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
		CommandLine.usage(this, (PrintStream) out);
	}
	
	@picocli.CommandLine.Command(
		name = "import",
		header = "Imports SNOMED CT content",
		description = "Imports SNOMED CT content"
	)
	private static final class ImportCommand extends Command {
		
		private static final String SUPPORTED_FORMAT = "rf2";
		
		@Option(names = { "-b", "--branch" }, description = "The target branch. After a successful import all importable content will be accessible from this branch.", defaultValue = "SNOMEDCT/HEAD", required = true)
		String branch = SnomedTerminologyComponentConstants.SNOMED_SHORT_NAME;
		
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
			
			if (user == null || !user.hasPermission(Permission.toImport(SnomedDatastoreActivator.REPOSITORY_UUID, branch))) {
				out.println("User is unauthorized to import SNOMED CT content.");
				return;
			}
			
			UUID rf2ArchiveId = UUID.randomUUID();
			try (FileInputStream in = new FileInputStream(new File(path))) {
				ApplicationContext.getServiceForClass(AttachmentRegistry.class).upload(rf2ArchiveId, in);
			} catch (IOException e) {
				if (e instanceof FileNotFoundException) {
					out.println("Cannot find the path specified. '%s'", path);
				} else {
					out.println("Error reading the path specified. '%s'. Message: '%s'", path, e.getMessage());
				}
				return;
			}
			
			final ImportResponse response = SnomedRequests.rf2().prepareImport()
					.setCreateVersions(createVersions)
					.setRf2ArchiveId(rf2ArchiveId)
					.setReleaseType(rf2ReleaseType)
					.build(SnomedDatastoreActivator.REPOSITORY_UUID, branch)
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
		
		@Option(names = {"-p", "--pretty"}, description = "To make the output pretty print all JSON objects")
		boolean pretty = false;
		
		@Override
		public void run(CommandLineStream out) {
			RevisionIndex index = getContext().service(RepositoryManager.class).get("snomedStore").service(RevisionIndex.class);
			index.read(branch, searcher -> {
				out.println("Fetching revisions visible from branch...");

				Hits<JsonNode> hits = searcher.search(Query.select(JsonNode.class)
						.from(SnomedConceptDocument.class)
						.where(RevisionDocument.Expressions.id(conceptId))
						.limit(Integer.MAX_VALUE)
						.build());
				
				hits.forEach(rev -> {
					out.println(pretty ? rev.toPrettyString() : rev.toString());
				});
				
				if (hits.getTotal() > 1) {
					out.println("Found more than revision for the diagnosed document, fetching all revisions in chronological order:");
					
					if (hits.getTotal() == 2) {
						out.println("Performing a diff between the two revisions:");
						Iterator<JsonNode> it = hits.iterator();
						JsonDiff.diff(it.next(), it.next()).forEach(change -> {
							out.println("\t%s", change);
						});
					} else {
						out.println("More than two revisions found for the same ID, skipping diff generation.");
					}
					
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
								out.println("%s - %s - %s", Instant.ofEpochMilli(branchPoint.getTimestamp()).atOffset(ZoneOffset.UTC).toLocalDateTime(), branch.getPath(), c);
								// fetch commit detail for each revision to see what's registered in the commit object for compare and merge processes
								searcher.search(Query.select(Commit.class)
										.where(Commit.Expressions.timestamp(branchPoint.getTimestamp()))
										.limit(1)
										.build())
										.stream()
										.findFirst()
										.ifPresent(commit -> {
											out.println("\t-> %s pushed commit '%s' with details '%s'", commit.getAuthor(), commit.getComment(), commit.getDetailsByObject(conceptId));
										});
							} catch (Exception e) {
								if (e instanceof NotFoundException) {
									// unknown branch, probably something that was a test or had to be removed, ignore revision
								} else {
									// print error otherwise
									out.print(e);
								}
							}
						});
				}
				
				return null;
			});			
		}
	}
}
