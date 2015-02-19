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
package com.b2international.snowowl.datastore.validation;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

import java.text.MessageFormat;
import java.util.Date;

import org.eclipse.core.databinding.validation.IValidator;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;

import com.b2international.commons.status.SerializableStatus;
import com.b2international.snowowl.core.date.Dates;
import com.b2international.snowowl.datastore.DatastoreActivator;

/**
 * Validator to check whether a given {@link Date date} is before or after a given time.
 */
public class TimeValidator implements IValidator {

	private static final String DEFAULT_ERROR_MESSAGE_TEMPLATE = "Date should be after ''{0}''.";
	private static final String DEFAULT_ERROR_MESSAGE = MessageFormat.format(DEFAULT_ERROR_MESSAGE_TEMPLATE, Dates.formatByGmt(Dates.MIN_DATE_LONG));
	private static final String NULL_VALUE_ERROR_MESSAGE = "Date should be specified.";
	private Date minDate;

	/**Creates a validator with the given minimum time.*/
	public TimeValidator(final long minTime) {
		this(new Date(minTime));
	}
	
	/**Creates a validator with the given minimum date.*/
	public TimeValidator(final Date minDate) {
		this.minDate = checkNotNull(minDate, "minDate");
		checkArgument(this.minDate.getTime() >= Dates.MIN_DATE_LONG, DEFAULT_ERROR_MESSAGE);
	}
	
	/**Creates a validator with the <a href="http://en.wikipedia.org/wiki/Unix_time">epoch time</a>.*/
	public TimeValidator() {
		this(Dates.MIN_DATE_LONG);
	}

	/* (non-Javadoc)
	 * @see org.eclipse.core.databinding.validation.IValidator#validate(java.lang.Object)
	 */
	@Override
	public final IStatus validate(Object value) {
		if (value instanceof Date) {
			final Date date = (Date) value;
			return isValid(date) ? new SerializableStatus(Status.OK_STATUS) : handleBeforeMinDateValue();
		}
		return null == value ? handleNullValue() : handledNonDateValue(value);
	}

	/**Handles {@code null} value. By default this method returns with a {@link IStatus status} with error severity.*/
	protected IStatus handleNullValue() {
		return new SerializableStatus(new Status(IStatus.ERROR, DatastoreActivator.PLUGIN_ID, NULL_VALUE_ERROR_MESSAGE));
	}

	/**Handlers non {@link Date} value. By default returns with {@link Status#OK_STATUS OK status}.*/
	protected IStatus handledNonDateValue(final Object value) {
		return new SerializableStatus(Status.OK_STATUS);
	}
	
	/**Handles if the given {@link Date date} value is before epoch time.*/
	protected IStatus handleBeforeMinDateValue() {
		return createErrorStatus();
	}
	
	/**Creates an error status.*/
	protected IStatus createErrorStatus() {
		return new SerializableStatus(new Status(IStatus.ERROR, DatastoreActivator.PLUGIN_ID, getErrorMessage()));
	}

	/**Returns with the error message for the {@link IStatus status}.*/
	protected String getErrorMessage() {
		return MessageFormat.format(DEFAULT_ERROR_MESSAGE_TEMPLATE, Dates.formatByGmt(minDate));
	}

	/**Checks whether the date argument is valid. Returns {@code true} if the date argument is after the epoch time. Otherwise returns with {@code false}.*/
	private boolean isValid(final Date date) {
		return date.after(minDate);
	}
	
}