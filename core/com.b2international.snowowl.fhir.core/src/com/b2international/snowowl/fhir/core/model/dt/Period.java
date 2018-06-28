/*******************************************************************************
 * Copyright (c) 2018 B2i Healthcare. All rights reserved.
 *******************************************************************************/
package com.b2international.snowowl.fhir.core.model.dt;

import java.util.Collection;
import java.util.Date;

import com.b2international.snowowl.fhir.core.model.Element;
import com.b2international.snowowl.fhir.core.model.Extension;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * FHIR period complex datatype
 * 
 * If the start element is missing, the start of the period is not known. 
 * If the end element is missing, it means that the period is ongoing, or the start may be in the past, 
 * and the end date in the future, which means that period is expected/planned to end at the specified time.
 *
 * @see <a href="https://www.hl7.org/fhir/datatypes.html#period">FHIR:Data Types:Period</a>
 * @since 6.6
 */
public class Period extends Element {
	
	@JsonProperty
	private Date start;
	
	@JsonProperty
	private Date end;
	
	public Period(final Date start, final Date end) {
		this(start, end, null, null);
	}
	
	public Period(final Date start, final Date end, final String id, final Collection<Extension> extensions) {
		super(id, extensions);
		this.start = start;
		this.end = end;
	}

	/**
	 * Returns the start date with inclusive boundary
	 * @return
	 */
	public Date getStart() {
		return start;
	}
	
	/**
	 * Returns the end date with inclusive boundary
	 * @return
	 */
	public Date getEnd() {
		return end;
	}

}
