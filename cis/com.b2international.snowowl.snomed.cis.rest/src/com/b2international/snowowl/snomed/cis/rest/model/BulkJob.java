/*
 * Copyright 2019 B2i Healthcare Pte Ltd, http://b2i.sg
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
package com.b2international.snowowl.snomed.cis.rest.model;

import java.util.Date;

import com.b2international.snowowl.datastore.remotejobs.RemoteJobEntry;
import com.b2international.snowowl.datastore.remotejobs.RemoteJobState;
import com.b2international.snowowl.snomed.cis.client.JobStatus;

/**
 * @since 6.18
 */
public class BulkJob {

	private String id;
	private String name;
	private String status;
	private Date created_at;
	private Date modified_at;
	private Object request;
	private String log;
	
	public String getId() {
		return id;
	}
	
	public void setId(String id) {
		this.id = id;
	}
	
	public String getName() {
		return name;
	}
	
	public void setName(String name) {
		this.name = name;
	}
	
	public String getStatus() {
		return status;
	}
	
	public void setStatus(String status) {
		this.status = status;
	}
	
	public Date getCreated_at() {
		return created_at;
	}
	
	public void setCreated_at(Date created_at) {
		this.created_at = created_at;
	}
	
	public Date getModified_at() {
		return modified_at;
	}
	
	public void setModified_at(Date modified_at) {
		this.modified_at = modified_at;
	}
	
	public Object getRequest() {
		return request;
	}
	
	public void setRequest(Object request) {
		this.request = request;
	}
	
	public String getLog() {
		return log;
	}
	
	public void setLog(String log) {
		this.log = log;
	}
	
	public static BulkJob fromRemoteJob(RemoteJobEntry job) {
		BulkJob result = new BulkJob();
		result.setId(job.getId());
		result.setName(job.getDescription());
		result.setCreated_at(job.getScheduleDate());
		result.setStatus(Integer.toString(getBulkJobStatus(job.getState()).ordinal()));
		result.setModified_at(job.getFinishDate());
		return result;
	}

	private static JobStatus getBulkJobStatus(RemoteJobState state) {
		switch (state) {
		case SCHEDULED: 
			return JobStatus.PENDING;
		case CANCEL_REQUESTED:
		case RUNNING: 
			return JobStatus.RUNNING;
		case FINISHED: 
			return JobStatus.FINISHED;
		case CANCELED:
		case FAILED:
			return JobStatus.ERROR;
		default: throw new UnsupportedOperationException("Unhandled job state: " + state);
		}
	}
	
}
