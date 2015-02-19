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
package com.b2international.snowowl.datastore.history;

import java.io.Serializable;
import java.util.Collection;
import java.util.List;

import com.b2international.snowowl.core.api.IHistoryInfo;
import com.b2international.snowowl.core.api.IHistoryInfoDetails;
import com.google.common.collect.ImmutableList;

/**
 * Adapter for {@link IHistoryInfo}. Workaround for serialization.
 * */
public final class HistoryInfoAdapter implements IHistoryInfo, Serializable {
	private static final long serialVersionUID = -1214862698631632977L;
	private final Collection<? extends IHistoryInfoDetails> details;
	public HistoryInfoAdapter(final Collection<? extends IHistoryInfoDetails> details) { this.details = details; }
	@Override public boolean isIncomplete() { return false; }
	@Override public IVersion<?> getVersion() { return null; }
	@Override public long getTimeStamp() { return 0; }
	@Override public List<IHistoryInfoDetails> getDetails() { return ImmutableList.<IHistoryInfoDetails>copyOf(details); }
	@Override public String getComments() { return null; }
	@Override public String getAuthor() { return null; }
	
}