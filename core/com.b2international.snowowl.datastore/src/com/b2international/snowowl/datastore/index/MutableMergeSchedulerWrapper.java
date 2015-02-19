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
package com.b2international.snowowl.datastore.index;

import java.io.IOException;

import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.MergeScheduler;
import org.apache.lucene.index.MergeTrigger;
import org.apache.lucene.index.NoMergeScheduler;

import com.b2international.snowowl.core.api.IMutable;
import com.google.common.base.Preconditions;

/**
 * Mutable {@link MergeScheduler} implementation.
 * @see IMutable
 */
public class MutableMergeSchedulerWrapper extends MergeScheduler implements IMutable {

	private final MergeScheduler delegate;
	private volatile boolean enabled = true;

	public MutableMergeSchedulerWrapper(final MergeScheduler scheduler) {
		this.delegate = Preconditions.checkNotNull(scheduler, "Merge scheduler argument cannot be null.");
	}
	
	@Override
	public void enable() {
		enabled = true;
	}

	@Override
	public void disable() {
		enabled = false;
	}

	@Override
	public void merge(IndexWriter writer, MergeTrigger trigger, boolean newMergesFound) throws IOException {
		if (enabled) {
			delegate.merge(writer, trigger, newMergesFound);
		} else {
			NoMergeScheduler.INSTANCE.merge(writer, trigger, newMergesFound);
		}
	}

	@Override
	public void close() throws IOException {
		delegate.close();
	}

}