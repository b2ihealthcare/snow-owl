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
package com.b2international.snowowl.fhir.core.search;

import java.util.Collection;
import java.util.Optional;

import javax.validation.constraints.NotNull;

import com.b2international.commons.StringUtils;
import com.b2international.snowowl.fhir.core.model.ValidatingBuilder;
import com.google.common.collect.Sets;

/**
 * Class to encapsulate a FHIR Request Search parameter
 *  
 * @since 6.7
 */
public class SearchRequestParameter {
	
	public enum SearchRequestParameterKey  {
		
		_id, //Resource, type token
		_lastUpdated, //Resource
		_tag, //Resource
		_profile, //Resource
		_security, //Resource
		_text, //DomainResource
		_content, //Resource
		_list, //
		_has,
		_type,
		_query,
		
		//result parameters, probably should be moved somewhere else
		_sort,
		_count,
		_include,
		_revinclude,
		_summary,
		_elements, 
		_contained,
		_containedType;

		public static SearchRequestParameterKey fromRequestParameter(String requestParam) {
			return valueOf(requestParam.toLowerCase());
		}
	}
	
	/**
	 * Request parameter types
	 * 	<li>number (missing)
	 * 	<li>date (missing)
	 * 	<li>string (missing, exact, contains)
	 * 	<li>token (missing, text, in, below, above, not-in)
	 * 	<li>reference (missing, type)
	 * 	<li>composite (missing)
	 * 	<li>quantity (missing)
	 * 	<li>uri (missing, below, above) 
	 */
	public enum SearchRequestParameterType {
		NUMBER,
		DATE,
		DATETIME,
		STRING,
		TOKEN,
		REFERENCE,
		COMPOSITE,
		QUANTITY,
		URI
	}
	
	public enum SearchRequestParameterModifier {
		missing,
		exact,
		contains,
		text,
		in,
		below,
		above,
		not_in,
		type;
		
		public String getParameterString() {
			return name().replaceAll("_", "-");
		}
		
		public static SearchRequestParameterModifier fromRequestParameter(String requestParam) {
			return valueOf(requestParam.replaceAll("-", "_"));
		}
	}
	
	public enum SummaryParameterValue {
		
		TRUE, //	Return only those elements marked as "summary" in the base definition of the resource(s) (see ElementDefinition.isSummary)
		TEXT, //	Return only the "text" element, the 'id' element, the 'meta' element, and only top-level mandatory elements
		DATA	, //Remove the text element
		COUNT, //Search only: just return a count of the matching resources, without returning the actual matches
		FALSE; //Return all parts of the resource(s)
		
		public static SummaryParameterValue fromRequestParameter(String requestParam) {
			return valueOf(requestParam.toUpperCase());
		}
	}
	
	public enum SearchRequestParameterValuePrefix {
		eq, //equal
		ne, //not equal
		gt, // greater than
		lt, //less than
		ge, //greater or equal
		le, //less or equal
		sa, //starts after
		eb, //ends before
		ap //approximately the same (within 10%)
		
	}
	
	@NotNull
	private final SearchRequestParameterKey name;
	
	@NotNull
	private final SearchRequestParameterType type;
	
	private final SearchRequestParameterModifier modifier;
	
	private final SearchRequestParameterValuePrefix prefix;
	
	//@NotEmpty - this can be empty due to the :missing modifier
	private final Collection<String> values;
	
	public SearchRequestParameter(SearchRequestParameterKey name, SearchRequestParameterType type, SearchRequestParameterModifier modifier, SearchRequestParameterValuePrefix prefix, Collection<String> values) {
		this.name = name;
		this.type = type;
		this.modifier = modifier;
		this.prefix = prefix;
		this.values = values;
	}
	
	public SearchRequestParameterKey getName() {
		return name;
	}

	public SearchRequestParameterType getType() {
		return type;
	}

	public SearchRequestParameterModifier getModifier() {
		return modifier;
	}

	public SearchRequestParameterValuePrefix getPrefix() {
		return prefix;
	}

	public Collection<String> getValues() {
		return values;
	}
	
	public static SearchRequestParameter idParameter(String value) {
		
		return SearchRequestParameter.builder()
			.name(SearchRequestParameterKey._id)
			.type(SearchRequestParameterType.TOKEN)
			.value(value)
			.build();
	}
	
	public static SearchRequestParameter lastUpdatedParameter(String lastUpdatedKey, String value) {
		
		
		Builder builder = SearchRequestParameter.builder()
			.name(SearchRequestParameterKey._lastUpdated)
			.type(SearchRequestParameterType.DATE);
		
		//modifier
		addModifier(builder, lastUpdatedKey);
		
		//prefix
		addPrefixedValue(builder, value);
		
		return builder.build();
	}

	private static void addPrefixedValue(Builder builder, String value) {
		
		if (!StringUtils.isEmpty(value) && value.length() > 2) {
			String prefixCandidate = value.substring(0, 2);
			
			Optional<SearchRequestParameterValuePrefix> optionalPrefix = Sets.newHashSet(SearchRequestParameterValuePrefix.values()).stream()
				.map(SearchRequestParameterValuePrefix::name)
				.filter(v -> v.equals(prefixCandidate))
				.map(SearchRequestParameterValuePrefix::valueOf)
				.findFirst();
			
			if (optionalPrefix.isPresent()) {
				builder.prefix(optionalPrefix.get());
				builder.value(value.substring(2));
			} else {
				builder.prefix(SearchRequestParameterValuePrefix.eq);
				builder.value(value);
			}
		} else {
			builder.prefix(SearchRequestParameterValuePrefix.eq);
			builder.value(value);
		}
		
	}

	private static void addModifier(Builder builder, String key) {
		String[] keyParts = key.split(":");
		
		if (keyParts.length > 2) {
			throw new IllegalArgumentException("Invalid key: " + key);
		}
		if (keyParts.length == 2) {
			builder.modifier(keyParts[1]);
		}
		
	}

	public static Builder builder() {
		return new Builder();
	}
	
	public static class Builder extends ValidatingBuilder<SearchRequestParameter> {
		
		private SearchRequestParameterKey name;
		private SearchRequestParameterType type;
		private SearchRequestParameterModifier modifier;
		private SearchRequestParameterValuePrefix prefix;
		private Collection<String> values = Sets.newHashSet();

		public Builder name(final SearchRequestParameterKey name) {
			this.name = name;
			return this;
		}
		
		public Builder name(final String name) {
			this.name = SearchRequestParameterKey.fromRequestParameter(name);
			return this;
		}

		public Builder type(final SearchRequestParameterType type) {
			this.type = type;
			return this;
		}
		
		public Builder type(final String typeName) {
			this.type = SearchRequestParameterType.valueOf(typeName.toUpperCase());
			return this;
		}
		
		public Builder modifier(final SearchRequestParameterModifier modifier) {
			this.modifier = modifier;
			return this;
		}

		public Builder modifier(final String modifier) {
			this.modifier = SearchRequestParameterModifier.fromRequestParameter(modifier);
			return this;
		}
		
		public Builder prefix(final SearchRequestParameterValuePrefix prefix) {
			this.prefix = prefix;
			return this;
		}
		
		public Builder prefix(final String prefixString) {
			this.prefix = SearchRequestParameterValuePrefix.valueOf(prefixString);
			return this;
		}
		
		public Builder values(final Collection<String> values) {
			this.values = values;
			return this;
		}
		
		public Builder value(final String value) {
			this.values = Sets.newHashSet(value);
			return this;
		}
		
		/**
		 * Request parameter types
		 * 	<li>number (missing)
		 * 	<li>date (missing)
		 * 	<li>string (missing, exact, contains)
		 * 	<li>token (missing, text, in, below, above, not-in)
		 * 	<li>reference (missing, type)
		 * 	<li>composite (missing)
		 * 	<li>quantity (missing)
		 * 	<li>uri (missing, below, above) 
		 */
		@Override
		protected SearchRequestParameter doBuild() {
			//do additional validation here
			if (modifier !=null) {
				if (type == SearchRequestParameterType.NUMBER) {
					if (modifier != SearchRequestParameterModifier.missing) {
						throw new IllegalArgumentException(String.format("Invalid modifier [%s] for number type parameter [%s].", modifier, name));
					}
					
				} else if (type == SearchRequestParameterType.DATE || type == SearchRequestParameterType.DATETIME) {
					if (modifier != SearchRequestParameterModifier.missing) {
						throw new IllegalArgumentException(String.format("Invalid modifier [%s] for date/datetime type parameter [%s].", modifier, name));
					}
				} else if (type == SearchRequestParameterType.STRING) {
					if (modifier != SearchRequestParameterModifier.missing && modifier != SearchRequestParameterModifier.exact && modifier != SearchRequestParameterModifier.contains) {
						throw new IllegalArgumentException(String.format("Invalid modifier [%s] for string type parameter [%s].", modifier, name));
					}
				} else if (type == SearchRequestParameterType.TOKEN) {
					if (modifier != SearchRequestParameterModifier.missing 
							&& modifier != SearchRequestParameterModifier.text 
							&& modifier != SearchRequestParameterModifier.in 
							&& modifier != SearchRequestParameterModifier.below 
							&& modifier != SearchRequestParameterModifier.above 
							&& modifier != SearchRequestParameterModifier.not_in) {
						throw new IllegalArgumentException(String.format("Invalid modifier [%s] for token type parameter [%s].", modifier, name));
					}
				} else if (type == SearchRequestParameterType.REFERENCE) {
					if (modifier != SearchRequestParameterModifier.missing 
							&& modifier != SearchRequestParameterModifier.type) {
						throw new IllegalArgumentException(String.format("Invalid modifier [%s] for reference type parameter [%s].", modifier, name));
					}
				} else if (type == SearchRequestParameterType.COMPOSITE) {
					if (modifier != SearchRequestParameterModifier.missing) {
						throw new IllegalArgumentException(String.format("Invalid modifier [%s] for composite type parameter [%s].", modifier, name));
					}
				} else if (type == SearchRequestParameterType.QUANTITY) {
					if (modifier != SearchRequestParameterModifier.missing) {
						throw new IllegalArgumentException(String.format("Invalid modifier [%s] for quantity type parameter [%s].", modifier, name));
					}
				} else if (type == SearchRequestParameterType.URI) {
					if (modifier != SearchRequestParameterModifier.missing 
							&& modifier != SearchRequestParameterModifier.below 
							&& modifier != SearchRequestParameterModifier.above) {
						throw new IllegalArgumentException(String.format("Invalid modifier [%s] for URI type parameter [%s].", modifier, name));
					}
				}
			}
			
			
			return new SearchRequestParameter(name, type, modifier, prefix, values);
		}
		
	}

}
