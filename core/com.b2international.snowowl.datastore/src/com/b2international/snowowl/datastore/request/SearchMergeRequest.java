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
package com.b2international.snowowl.datastore.request;

import java.util.List;

import com.b2international.commons.options.Options;
import com.b2international.snowowl.core.domain.RepositoryContext;
import com.b2international.snowowl.core.events.Request;
import com.b2international.snowowl.core.merge.Merge;
import com.b2international.snowowl.core.merge.MergeCollection;
import com.b2international.snowowl.core.merge.MergeService;
import com.google.common.base.Predicate;
import com.google.common.base.Predicates;
import com.google.common.collect.Lists;

/**
 * @since 4.6
 */
public class SearchMergeRequest implements Request<RepositoryContext, MergeCollection> {

	public static class SourcePredicate implements Predicate<Merge> {
		
		private final String source;

		public SourcePredicate(final String source) {
			this.source = source;
		}
	
		@Override
		public boolean apply(final Merge input) {
			return input.getSource().equals(source);
		}
	}

	public static class TargetPredicate implements Predicate<Merge> {
	
		private final String target;

		public TargetPredicate(final String target) {
			this.target = target;
		}
	
		@Override
		public boolean apply(final Merge input) {
			return input.getTarget().equals(target);
		}
	}

	public static class StatusPredicate implements Predicate<Merge> {

		private final Merge.Status status;

		public StatusPredicate(final Merge.Status status) {
			this.status = status;
		}

		@Override
		public boolean apply(final Merge input) {
			return input.getStatus().equals(status);
		}
	}

	private Options options;

	public SearchMergeRequest(Options options) {
		this.options = options;
	}

	@Override
	public MergeCollection execute(RepositoryContext context) {

		final List<Predicate<Merge>> predicates = Lists.newArrayList();
		
		if (options.containsKey("source")) {  predicates.add(new SourcePredicate(options.getString("source"))); }
		if (options.containsKey("target")) {  predicates.add(new TargetPredicate(options.getString("target"))); }
		if (options.containsKey("status")) {  predicates.add(new StatusPredicate(options.get("status", Merge.Status.class))); }
		
		return context.service(MergeService.class).search(Predicates.and(predicates));
	}

}
