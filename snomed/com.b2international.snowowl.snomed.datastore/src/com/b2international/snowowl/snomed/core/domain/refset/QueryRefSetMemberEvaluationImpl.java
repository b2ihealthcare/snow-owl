/*
 * Copyright 2011-2022 B2i Healthcare Pte Ltd, http://b2i.sg
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
package com.b2international.snowowl.snomed.core.domain.refset;

import java.io.ObjectStreamException;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import com.google.common.collect.Iterables;
import com.google.common.collect.Streams;

/**
 * @since 4.5
 */
public class QueryRefSetMemberEvaluationImpl implements QueryRefSetMemberEvaluation {

	private static final long serialVersionUID = 1L;
	
	private final String memberId;
	private final String referenceSetId;
	
	private final AtomicBoolean streamCollected;
	private volatile Collection<MemberChange> changes;
	private transient Stream<List<MemberChange>> changesAsStream;

	public QueryRefSetMemberEvaluationImpl(final String memberId, final String referenceSetId) {
		this.memberId = memberId;
		this.referenceSetId = referenceSetId;
		this.streamCollected = new AtomicBoolean(true);
		this.changes = List.of();
		this.changesAsStream = Stream.empty();
	}
	
	public QueryRefSetMemberEvaluationImpl(final String memberId, final String referenceSetId, final Stream<List<MemberChange>> changesAsStream) {
		this.memberId = memberId;
		this.referenceSetId = referenceSetId;
		this.streamCollected = new AtomicBoolean(false);
		this.changes = null;
		this.changesAsStream = changesAsStream;
	}

	@Override
	public String getMemberId() {
		return memberId;
	}
	
	@Override
	public String getReferenceSetId() {
		return referenceSetId;
	}

	@Override
	public Collection<MemberChange> getChanges() {
		if (streamCollected.compareAndExchange(false, true) == false) {
			changes = changesAsStream
				.flatMap(List::stream)
				.collect(Collectors.toList());
		}
		
		return changes;
	}
	
	public Stream<List<MemberChange>> getChangesAsStream() {
		return changesAsStream;
	}

	private Object writeReplace() throws ObjectStreamException {
		getChanges();
		return this;
	}
	
	private Object readResolve() throws ObjectStreamException {
		changesAsStream = Streams.stream(Iterables.partition(changes, 10_000));
		return this;
	}
}
