/*
 * Licensed to Elasticsearch under one or more contributor
 * license agreements. See the NOTICE file distributed with
 * this work for additional information regarding copyright
 * ownership. Elasticsearch licenses this file to you under
 * the Apache License, Version 2.0 (the "License"); you may
 * not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.elasticsearch.index.reindex;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import org.elasticsearch.common.collect.Tuple;
import org.elasticsearch.common.unit.TimeValue;
import org.elasticsearch.common.xcontent.ObjectParser;
import org.elasticsearch.index.reindex.BulkByScrollTask.Status;
import org.elasticsearch.index.reindex.BulkByScrollTask.StatusOrException;

/**
 * This class acts as a builder for {@link Status}. Once the {@link Status} object is built by calling {@link #buildStatus()} it is immutable. Used by
 * an instance of {@link ObjectParser} when parsing from XContent.
 */
public class StatusBuilder {
	private Integer sliceId = null;
	private Long total = null;
	private long updated = 0; // Not present during deleteByQuery
	private long created = 0; // Not present during updateByQuery
	private Long deleted = null;
	private Integer batches = null;
	private Long versionConflicts = null;
	private Long noops = null;
	private Long bulkRetries = null;
	private Long searchRetries = null;
	private TimeValue throttled = null;
	private Float requestsPerSecond = null;
	private String reasonCancelled = null;
	private TimeValue throttledUntil = null;
	private List<StatusOrException> sliceStatuses = new ArrayList<>();

	public void setSliceId(Integer sliceId) {
		this.sliceId = sliceId;
	}

	public void setTotal(Long total) {
		this.total = total;
	}

	public void setUpdated(Long updated) {
		this.updated = updated;
	}

	public void setCreated(Long created) {
		this.created = created;
	}

	public void setDeleted(Long deleted) {
		this.deleted = deleted;
	}

	public void setBatches(Integer batches) {
		this.batches = batches;
	}

	public void setVersionConflicts(Long versionConflicts) {
		this.versionConflicts = versionConflicts;
	}

	public void setNoops(Long noops) {
		this.noops = noops;
	}

	public void setRetries(Tuple<Long, Long> retries) {
		if (retries != null) {
			setBulkRetries(retries.v1());
			setSearchRetries(retries.v2());
		}
	}

	public void setBulkRetries(Long bulkRetries) {
		this.bulkRetries = bulkRetries;
	}

	public void setSearchRetries(Long searchRetries) {
		this.searchRetries = searchRetries;
	}

	public void setThrottled(Long throttled) {
		if (throttled != null) {
			this.throttled = new TimeValue(throttled, TimeUnit.MILLISECONDS);
		}
	}

	public void setRequestsPerSecond(Float requestsPerSecond) {
		if (requestsPerSecond != null) {
			requestsPerSecond = requestsPerSecond == -1 ? Float.POSITIVE_INFINITY : requestsPerSecond;
			this.requestsPerSecond = requestsPerSecond;
		}
	}

	public void setReasonCancelled(String reasonCancelled) {
		this.reasonCancelled = reasonCancelled;
	}

	public void setThrottledUntil(Long throttledUntil) {
		if (throttledUntil != null) {
			this.throttledUntil = new TimeValue(throttledUntil, TimeUnit.MILLISECONDS);
		}
	}

	public void setSliceStatuses(List<StatusOrException> sliceStatuses) {
		if (sliceStatuses != null) {
			this.sliceStatuses.addAll(sliceStatuses);
		}
	}

	public void addToSliceStatuses(StatusOrException statusOrException) {
		this.sliceStatuses.add(statusOrException);
	}

	public Status buildStatus() {
		if (sliceStatuses.isEmpty()) {
			try {
				return new Status(sliceId, total, updated, created, deleted, batches, versionConflicts, noops, bulkRetries, searchRetries, throttled,
						requestsPerSecond, reasonCancelled, throttledUntil);
			} catch (NullPointerException npe) {
				throw new IllegalArgumentException("a required field is null when building Status");
			}
		} else {
			return new Status(sliceStatuses, reasonCancelled);
		}
	}
}