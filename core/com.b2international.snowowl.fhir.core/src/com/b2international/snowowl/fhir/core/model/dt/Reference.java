/*******************************************************************************
 * Copyright (c) 2018 B2i Healthcare. All rights reserved.
 *******************************************************************************/
package com.b2international.snowowl.fhir.core.model.dt;

import java.util.Date;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * FHIR reference
 * 
 * @see <a href="https://www.hl7.org/fhir/references.html">FHIR:References</a>
 * @since 6.6
 */
public class Reference {
	
	@JsonProperty
	private String reference;
	
	@JsonProperty
	private Identifier identifier;
	
	@JsonProperty
	private String display;

	/**
	 * @param reference
	 * @param identifier
	 * @param display
	 */
	public Reference(String reference, Identifier identifier, String display) {
		this.reference = reference;
		this.identifier = identifier;
		this.display = display;
	}

	/**
	 * @return the reference
	 */
	public String getReference() {
		return reference;
	}

	/**
	 * @return the identifier
	 */
	public Identifier getIdentifier() {
		return identifier;
	}

	/**
	 * @return the display
	 */
	public String getDisplay() {
		return display;
	}
	
	
}
