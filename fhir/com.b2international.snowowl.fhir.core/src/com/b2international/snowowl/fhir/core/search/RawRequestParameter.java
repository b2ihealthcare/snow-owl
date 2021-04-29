package com.b2international.snowowl.fhir.core.search;

import java.util.Collection;

import org.hibernate.validator.constraints.NotEmpty;

/**
 * 
 * FHIR search request URI parameter part representation<br> 
 *  
 *  Examples:<br>
 *  <pre>
 *  GET [base]/Observation?_lastUpdated=gt2010-10-01
 *  GET [base]/Condition?_tag=http://acme.org/codes|needs-review
 *  GET [base]/Patient?given:contains=eve
 *  GET [base]/ValueSet?url:above=http://acme.org/fhir/ValueSet/123/_history/5
 *  
 *  GET [base]/Patient?gender:not=male
 *  GET [base]/DiagnosticReport?subject.name=peter
 *  
 *  GET [base]/DiagnosticReport?subject.height=lt100 - larger than
 *  </pre>
 *
 */
public class RawRequestParameter {
	
	private String parameterKey;
	private Collection<String> parameterValues;
	
	private String parameterName;
	private String parameterModifier;

	public RawRequestParameter(@NotEmpty String parameterKey, Collection<String> parameterValues) {
		this.parameterKey = parameterKey;
		this.parameterValues = parameterValues;
		parse();
	}
	
	private void parse() {
		String[] parameterKeyArray = parameterKey.split(":");
		if (parameterKeyArray.length == 1) {
			parameterName = parameterKeyArray[0];
		} else if (parameterKeyArray.length == 2) {
			parameterName = parameterKeyArray[0];
			parameterModifier = parameterKeyArray[1];
		} else {
			//BadRequest instead
			throw new IllegalArgumentException(String.format("Too many modifiers in parameter key '%s'", parameterKey));
		}
		
	}
	
	public String getParameterName() {
		return parameterName;
	}
	
	public String getParameterModifier() {
		return parameterModifier;
	}

	public boolean hasModifier() {
		return parameterModifier !=null;
	}
	
	public Collection<String> getParameterValues() {
		return parameterValues;
	}

	
}
