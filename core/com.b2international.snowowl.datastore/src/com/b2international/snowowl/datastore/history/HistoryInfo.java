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
import java.util.Collections;
import java.util.List;
import java.util.Map.Entry;

import org.eclipse.emf.cdo.common.id.CDOID;

import com.b2international.commons.StringUtils;
import com.b2international.snowowl.core.api.IHistoryInfo;
import com.b2international.snowowl.core.api.IHistoryInfoDetails;
import com.b2international.snowowl.datastore.cdo.CDOCommitInfoUtils;
import com.google.common.collect.Lists;
import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamImplicit;

@XStreamAlias("HistoryInfo")
public final class HistoryInfo implements IHistoryInfo, Serializable {
	
	private static final long serialVersionUID = 5151323817548115050L;

	@SuppressWarnings("unchecked")
	public static final HistoryInfo EMPTY = new HistoryInfo(-1, (IVersion<CDOID>) IVersion.EMPTY, "", "", true);
	
	@XStreamAlias("timeStamp")
	private long timeStamp;
	
	@XStreamAlias("version")
	private final IVersion<CDOID> version;
	
	@XStreamAlias("author")
	private final String author;
	
	@XStreamAlias("comments")
	private final String comments;
	
	@XStreamAlias("incomplete")
	private boolean incomplete;
	
	@XStreamImplicit(itemFieldName="historyDetails")
	private final List<IHistoryInfoDetails> details;
	
	/***
	 * Performs a grouping between the current and the given history info. If the two history info 
	 * can be merged together since they can be grouped logically this method performs the merge,
	 * and returns with {@code true} otherwise this method returns with {@code false}.
	 */
	public boolean group(final HistoryInfo other) {
		
		if (null == other) {
			return false;
		}
		
		if (StringUtils.isEmpty(other.getComments())) {
			return false;
		}
		
		final String uuid = CDOCommitInfoUtils.getUuid(comments);
		final String otherUuid = CDOCommitInfoUtils.getUuid(other.getComments());
		
		if (uuid.equals(otherUuid)) {
			
			if (timeStamp > other.timeStamp) {
				timeStamp = other.timeStamp;
			}
			
			if (version.getMinorVersion() > other.getVersion().getMinorVersion()) {
				((Version) version).setMinorVersion(other.getVersion().getMinorVersion());
			}
			
			if (version.getMajorVersion() > other.getVersion().getMajorVersion()) {
				((Version) version).setMajorVersion(other.getVersion().getMajorVersion());
			}
			
			details.addAll(other.getDetails());
			
			for (Entry<CDOID, Long> entry : ((Version) other.getVersion()).getAffectedObjectIds().entrySet()) {
				((Version) version).addAffectedObjectId(entry.getKey(), entry.getValue());
			}
			
			return true;
			
		}
		
		return false;
	}
	
	public HistoryInfo(long timeStamp, IVersion<CDOID> version, String author,	String comments, boolean incomplete) {
		this(timeStamp, version, author, comments, incomplete, Lists.<IHistoryInfoDetails>newArrayList());
	}
	
	public HistoryInfo(long timeStamp, IVersion<CDOID> version, String author, String comments, boolean incomplete, List<IHistoryInfoDetails> details) {
		this.timeStamp = timeStamp;
		this.version = version;
		this.author = author;
		this.comments = comments;
		this.incomplete = incomplete;
		this.details = details;
	}

	@Override
	public IVersion<?> getVersion() {
		return version;
	}
	
	@Override
	public long getTimeStamp() {
		return timeStamp;
	}
	
	@Override
	public String getAuthor() {
		return author;
	}
	
	@Override
	public String getComments() {
		return comments;
	}
	
	@Override
	public List<IHistoryInfoDetails> getDetails() {
		return Collections.unmodifiableList(details);
	}
	
	@Override
	public boolean isIncomplete() {
		return incomplete;
	}
}