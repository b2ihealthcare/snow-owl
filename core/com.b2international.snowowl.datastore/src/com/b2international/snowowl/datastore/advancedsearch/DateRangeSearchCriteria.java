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
package com.b2international.snowowl.datastore.advancedsearch;

import java.util.Date;

/**
 * @since 3.0.1
 */
public class DateRangeSearchCriteria extends AbstractSearchCriteria {

	private static final long serialVersionUID = 3659004671239320503L;
	private Date fromDate;
	private Date toDate;
	private boolean unpublished;

	public DateRangeSearchCriteria(String type) {
		super(type);
	}

	public Date getFromDate() {
		return fromDate;
	}

	public void setFromDate(Date fromDate) {
		this.fromDate = fromDate;
	}

	public Date getToDate() {
		return toDate;
	}

	public void setToDate(Date toDate) {
		this.toDate = toDate;
	}

	public boolean isUnpublished() {
		return unpublished;
	}

	public void setUnpublished(boolean unpublished) {
		this.unpublished = unpublished;
	}

}