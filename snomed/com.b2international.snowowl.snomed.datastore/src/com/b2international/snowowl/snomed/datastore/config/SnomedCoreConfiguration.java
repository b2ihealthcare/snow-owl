/*
 * Copyright 2011-2021 B2i Healthcare Pte Ltd, http://b2i.sg
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
package com.b2international.snowowl.snomed.datastore.config;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;

import org.hibernate.validator.constraints.NotEmpty;

import com.b2international.snowowl.snomed.common.SnomedConstants.Concepts;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * SNOMED CT related application level configuration parameters.
 * 
 * @since 3.4
 */
public class SnomedCoreConfiguration {
	
	public static final String ELK_REASONER_ID = "org.semanticweb.elk.elk.reasoner.factory"; //$NON-NLS-1$
	public static final String DEFAULT_REASONER = ELK_REASONER_ID;
	public static final int DEFAULT_MAXIMUM_REASONER_COUNT = 2;
	public static final int DEFAULT_MAXIMUM_REASONER_RESULTS = 10;
	public static final int DEFAULT_MAXIMUM_REASONER_RUNS = 1000;
	
	public static final String MAXIMUM_REASONER_RUNS = "maxReasonerRuns";
	
	@Min(1)
	@Max(3)
	private int maxReasonerCount = DEFAULT_MAXIMUM_REASONER_COUNT;
	
	@Min(1)
	@Max(1_000_000)
	private int maxReasonerRuns = DEFAULT_MAXIMUM_REASONER_RUNS;
	
	@NotEmpty
	private String concreteDomainTypeRefsetIdentifier = Concepts.REFSET_CONCRETE_DOMAIN_TYPE;
	
	@NotEmpty
	private String stringDatatypeRefsetIdentifier = Concepts.REFSET_STRING_DATATYPE;
	
	@NotEmpty
	private String booleanDatatypeRefsetIdentifier = Concepts.REFSET_BOOLEAN_DATATYPE;
	
	@NotEmpty
	private String floatDatatypeRefsetIdentifier = Concepts.REFSET_FLOAT_DATATYPE;
	
	@NotEmpty
	private String integerDatatypeRefsetIdentifier = Concepts.REFSET_INTEGER_DATATYPE;
	
	@NotEmpty
	private String datetimeDatatypeRefsetIdentifier = Concepts.REFSET_DATETIME_DATATYPE;
	
	private boolean concreteDomainSupport = false;
	
	
	/**
	 * @return the number of reasoners that are permitted to run simultaneously.
	 */
	@JsonProperty
	public int getMaxReasonerCount() {
		return maxReasonerCount;
	}
	
	/**
	 * @param maxReasonerCount the maxReasonerCount to set
	 */
	@JsonProperty
	public void setMaxReasonerCount(int maxReasonerCount) {
		this.maxReasonerCount = maxReasonerCount;
	}
	
	/**
	 * @return the number of classification run details to preserve. Details include inferred and redundant 
	 *         relationships, the list of equivalent concepts found during classification, and job metadata
	 *         (creation, start and end times, final state, requesting user). 
	 */
	@JsonProperty
	public int getMaxReasonerRuns() {
		return maxReasonerRuns;
	}
	
	@JsonProperty
	public void setMaxReasonerRuns(int maxReasonerRuns) {
		this.maxReasonerRuns = maxReasonerRuns;
	}
	
	@Deprecated
	@JsonProperty("concreteDomainSupport")
	public boolean isConcreteDomainSupported() {
		return concreteDomainSupport;
	}
	
	@Deprecated
	@JsonProperty("concreteDomainSupport")
	public void setConcreteDomainSupported(boolean concreteDomainSupport) {
		this.concreteDomainSupport = concreteDomainSupport;
	}
		
	/**
	 * The ID of the concrete domain type reference set identifier concept
	 * 
	 * @return the concreteDomainTypeRefsetIdentifier
	 */
	@Deprecated
	@JsonProperty("concreteDomainTypeRefsetIdentifier")
	public String getConcreteDomainTypeRefsetIdentifier() {
		return concreteDomainTypeRefsetIdentifier;
	}

	/**
	 * Sets the ID of the concrete domain type reference set identifier concept
	 * 
	 * @param concreteDomainTypeRefsetIdentifier the concreteDomainTypeRefsetIdentifier to set
	 */
	@Deprecated
	@JsonProperty("concreteDomainTypeRefsetIdentifier")
	public void setConcreteDomainTypeRefsetIdentifier(String concreteDomainTypeRefsetIdentifier) {
		this.concreteDomainTypeRefsetIdentifier = concreteDomainTypeRefsetIdentifier;
	}

	/**
	 * The ID of the string datatype reference set identifier concept
	 * 
	 * @return the stringDatatypeRefsetIdentifier
	 */
	@Deprecated
	@JsonProperty("stringDataTypeRefsetIdentifier")
	public String getStringDatatypeRefsetIdentifier() {
		return stringDatatypeRefsetIdentifier;
	}

	/**
	 * Sets the ID of the string datatype reference set identifier concept
	 * 
	 * @param stringDatatypeRefsetIdentifier the stringDatatypeRefsetIdentifier to set
	 */
	@Deprecated
	@JsonProperty("stringDataTypeRefsetIdentifier")
	public void setStringDatatypeRefsetIdentifier(String stringDatatypeRefsetIdentifier) {
		this.stringDatatypeRefsetIdentifier = stringDatatypeRefsetIdentifier;
	}

	/**
	 * The ID of the boolean datatype reference set identifier concept
	 * 
	 * @return the booleanDatatypeRefsetIdentifier
	 */
	@Deprecated
	@JsonProperty("booleanDataTypeRefsetIdentifier")
	public String getBooleanDatatypeRefsetIdentifier() {
		return booleanDatatypeRefsetIdentifier;
	}

	/**
	 * Sets the ID of the boolean datatype reference set identifier concept
	 * 
	 * @param booleanDatatypeRefsetIdentifier the booleanDatatypeRefsetIdentifier to set
	 */
	@Deprecated
	@JsonProperty("booleanDataTypeRefsetIdentifier")
	public void setBooleanDatatypeRefsetIdentifier(String booleanDatatypeRefsetIdentifier) {
		this.booleanDatatypeRefsetIdentifier = booleanDatatypeRefsetIdentifier;
	}

	/**
	 * The ID of the float datatype reference set identifier concept
	 * 
	 * @return the floatDatatypeRefsetIdentifier
	 */
	@Deprecated
	@JsonProperty("floatDataTypeRefsetIdentifier")
	public String getFloatDatatypeRefsetIdentifier() {
		return floatDatatypeRefsetIdentifier;
	}

	/**
	 * Sets the ID of the float datatype reference set identifier concept
	 * 
	 * @param floatDatatypeRefsetIdentifier the floatDatatypeRefsetIdentifier to set
	 */
	@Deprecated
	@JsonProperty("floatDataTypeRefsetIdentifier")
	public void setFloatDatatypeRefsetIdentifier(String floatDatatypeRefsetIdentifier) {
		this.floatDatatypeRefsetIdentifier = floatDatatypeRefsetIdentifier;
	}

	/**
	 * The ID of the integer datatype reference set identifier concept
	 * 
	 * @return the integerDatatypeRefsetIdentifier
	 */
	@Deprecated
	@JsonProperty("integerDataTypeRefsetIdentifier")
	public String getIntegerDatatypeRefsetIdentifier() {
		return integerDatatypeRefsetIdentifier;
	}

	/**
	 * Sets the ID of the integer datatype reference set identifier concept
	 * 
	 * @param integerDatatypeRefsetIdentifier the integerDatatypeRefsetIdentifier to set
	 */
	@Deprecated
	@JsonProperty("integerDataTypeRefsetIdentifier")
	public void setIntegerDatatypeRefsetIdentifier(String integerDatatypeRefsetIdentifier) {
		this.integerDatatypeRefsetIdentifier = integerDatatypeRefsetIdentifier;
	}

	/**
	 * The ID of the datetime datatype reference set identifier concept
	 * 
	 * @return the datetimeDatatypeRefsetIdentifier
	 */
	@Deprecated
	@JsonProperty("datetimeDataTypeRefsetIdentifier")
	public String getDatetimeDatatypeRefsetIdentifier() {
		return datetimeDatatypeRefsetIdentifier;
	}

	/**
	 * Sets the ID of the datetime datatype reference set identifier concept
	 * 
	 * @param datetimeDatatypeRefsetIdentifier the datetimeDatatypeRefsetIdentifier to set
	 */
	@Deprecated
	@JsonProperty("datetimeDataTypeRefsetIdentifier")
	public void setDatetimeDatatypeRefsetIdentifier(String datetimeDatatypeRefsetIdentifier) {
		this.datetimeDatatypeRefsetIdentifier = datetimeDatatypeRefsetIdentifier;
	}
	
}
