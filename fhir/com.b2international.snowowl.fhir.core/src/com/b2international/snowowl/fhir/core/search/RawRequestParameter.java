package com.b2international.snowowl.fhir.core.search;

import java.util.Collection;
import java.util.List;

import org.hibernate.validator.constraints.NotEmpty;

import com.b2international.commons.StringUtils;
import com.b2international.snowowl.fhir.core.exceptions.BadRequestException;
import com.google.common.collect.Lists;

/**
 * FHIR URI request URI parameter raw (unprocessed) representation<br> 
 *  
 *  Format:<br>
 *  <pre>
 *  parameterName[:modifier] = value1, value2, value3...
 *  </pre>
 *  
 *  @since 7.17
 */
public class RawRequestParameter {
	
	private String name;
	private String modifier;
	private Collection<String> values;

	public RawRequestParameter(@NotEmpty String parameterKey, Collection<String> parameterValues) {
		String[] parameterKeyArray = parameterKey.split(":");
		if (parameterKeyArray.length == 1) {
			name = parameterKeyArray[0];
		} else if (parameterKeyArray.length == 2) {
			name = parameterKeyArray[0];
			modifier = parameterKeyArray[1];
		} else {
			throw new BadRequestException(String.format("Too many modifiers in parameter key '%s'", parameterKey));
		}
		
		values = splitParameterValues(parameterValues);
	}
	
	/*
	 * org.springframework.util.MultiValueMap does not split the commma separated values.
	 * We have to do it manually here.
	 */
	private List<String> splitParameterValues(Collection<String> unsplitValues) {
		
		List<String> splitParameters = Lists.newArrayList();
		for (String element : unsplitValues) {
			
			//Spring can return a collection with nulls as elements
			if (StringUtils.isEmpty(element)) continue;
			
			element = element.replaceAll(" ", "");
			if (element.contains(",")) {
				String requestedFields[] = element.split(",");
				splitParameters.addAll(Lists.newArrayList(requestedFields));
			} else {
				splitParameters.add(element);
			}
		}
		return splitParameters;
	}
	
	public String getName() {
		return name;
	}
	
	public String getModifier() {
		return modifier;
	}

	public boolean hasModifier() {
		return modifier !=null;
	}
	
	public Collection<String> getValues() {
		return values;
	}
}
