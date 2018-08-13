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

import static com.google.common.collect.Lists.newArrayList;

import com.b2international.commons.extension.Component;
import com.b2international.snowowl.core.Repositories;
import com.b2international.snowowl.core.RepositoryInfo;
import com.b2international.snowowl.datastore.request.RepositoryRequests;
import com.b2international.snowowl.datastore.request.repository.RepositorySearchRequestBuilder;
import com.google.common.base.Function;
import com.google.common.base.Strings;
import com.google.common.collect.ImmutableList;
import com.google.common.primitives.Ints;

import picocli.CommandLine;
import picocli.CommandLine.Option;

/**
 * @since 7.0
 */
@Component
@CommandLine.Command(
	name = "repositories",
	header = "Displays information about the available repositories",
	description = { "Display a table containing all available repositories and their health statuses along with possible diagnosis information" }
)
public final class RepositoriesCommand extends Command {

	private static final String COLUMN_FORMAT = "|%-16s|%-16s|%-16s|";
	
	@Option(names = { "-r", "--repository" }, required = false, description = { "Displays only the selected repository" })
	String repositoryId;
	
	@Override
	public void run(CommandLineStream out) {
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
		
		printSeparator(out, maxLength);
		printHeader(out, "id", "health", Strings.padEnd("diagnosis", maxDiagLength, ' '));
		printSeparator(out, maxLength);
		repositories.forEach(repository -> {
			printLine(out, repository, RepositoryInfo::id, RepositoryInfo::health, repo -> Strings.isNullOrEmpty(repo.diagnosis()) ? "-" : null);
			printSeparator(out, maxLength);
		});
	}
	
	
	private static void printHeader(final CommandLineStream out, Object...columns) {
		out.println(COLUMN_FORMAT, columns);
	}
	
	private static void printSeparator(final CommandLineStream out, int length) {
		out.println(Strings.repeat("-", length));
	}
	
	@SafeVarargs
	private static <T> void printLine(final CommandLineStream out, T item, Function<T, Object>...values) {
		out.println(COLUMN_FORMAT, newArrayList(values).stream().map(func -> func.apply(item)).toArray());
	}

}
