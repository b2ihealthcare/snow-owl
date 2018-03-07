/*
 * Copyright 2011-2018 B2i Healthcare Pte Ltd, http://b2i.sg
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
package com.b2international.snowowl.fhir.api.model;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.Collection;

import com.b2international.snowowl.fhir.api.model.serialization.FhirLookupResult;
import com.b2international.snowowl.fhir.api.model.serialization.FhirParameter;
import com.google.common.collect.Lists;

/**
 * 
 * @author bbanfai
 *
 */
public class LookupResult {
	
	public enum FhirType {
		
		URI("Uri"),
		CODE("Code");
		
		private String serializedName;
		
		FhirType(String serializedName) {
			this.serializedName = serializedName;
		}
		
		public String getSerializedName() {
			return serializedName;
		}
	}
	
	//A display name for the code system (1..1)
	@FhirDataType(type = FhirType.URI)
	private String name;
	
	//The version that these details are based on (0..1)
	private String version;
	
	//The preferred display for this concept (1..1)
	private String display;
	
	//Additional representations for this concept (0..*)
	private Collection<Designation> designations = Lists.newArrayList();   
	
	/*
	 * One or more properties that contain additional information about the code, 
	 * including status. For complex terminologies (e.g. SNOMED CT, LOINC, medications), these properties serve to decompose the code
	 * 0..*
	 */
	private Collection<Property> properties = Lists.newArrayList();;
	
	private LookupResult(final String name, final String version, final String display, Collection<Designation> designations) {
		this.name = name;
		this.version = version;
		this.display = display;
		this.designations = designations;
	}
	
	public static Builder builder() {
		return new Builder();
	}
	
	public static class Builder {
		
		private String name;
		private String version;
		private String display;
		
		//Additional representations for this concept (0..*)
		private Collection<Designation> designations = Lists.newArrayList();   

		public Builder name(final String name) {
			this.name = name;
			return this;
		}
		
		public Builder use(String version) {
			this.version = version;
			return this;
		}
		
		public Builder value(String display) {
			this.display = display;
			return this;
		}
		
		public Builder addDesignation(Designation designation) {
			designations.add(designation);
			return this;
		}

		public LookupResult build() {
			return new LookupResult(name, version, display, designations);
		}
		
		/**
		 * This method builds the object of the serialized representation
		 * of a lookup result:
		 * <pre>{@code
  			}
  			</pre>
  		 *  @return
		 * @throws Exception
		 */
		public FhirLookupResult buildSerializableBean() throws Exception {
			LookupResult result =  new LookupResult(name, version, display, designations);
			
			FhirLookupResult lookupResult = new FhirLookupResult();
			
			Field[] fields = LookupResult.class.getDeclaredFields();
			Arrays.sort(fields, new FieldOrderComparator());
			
			for (Field field : fields) {
				field.setAccessible(true);
				if (field.getType().equals(Collection.class)) {
					System.out.println("Collection " + field.getName());
					Collection values = (Collection) field.get(result);
					for (Object object : values) {
						if (object instanceof Designation) {
							Collection<FhirParameter> designationParams = ((Designation) object).toSerializedBean();
							FhirParameter fhirParam = new FhirParameter("designation", "part", designationParams);
							lookupResult.add(fhirParam);
						}
					}
				} else {
					Object value = field.get(result);
					
					String type = getDataType(field);
					FhirParameter parameter = new FhirParameter(field.getName(), type, value);
					lookupResult.add(parameter);
				}
			}
			return lookupResult;
		}

		private String getDataType(Field field) {
			FhirDataType fhirDataType = field.getAnnotation(FhirDataType.class);
			String typePostFix = null;
			if (fhirDataType == null) {
				typePostFix = field.getType().getSimpleName();
			} else {
				typePostFix = fhirDataType.type().getSerializedName();
			}
			return "value" + typePostFix;
		}
	}
	
}
