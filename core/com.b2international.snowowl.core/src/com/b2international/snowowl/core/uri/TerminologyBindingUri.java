/*******************************************************************************
 * Copyright (c) 2020 B2i Healthcare. All rights reserved.
 *******************************************************************************/
package com.b2international.snowowl.core.uri;

import java.util.StringTokenizer;

import com.b2international.commons.exceptions.BadRequestException;
import com.b2international.snowowl.core.terminology.TerminologyRegistry;

/**
 * URI to represent a terminology component binding.
 * The binding unambiguously defines a set of component within Snow Owl
 * <br>protocol:codeSystem/version/componentType?queryPart
 * @since 7.11
 */
public class TerminologyBindingUri {
	
	public static final String SNOW_OWL_PROTOCOL = "snowowl";  //reserved string to represent Snow Owl's internal protocol for this URI
	
	private String protocol;
	private String codeSystem;
	private String versionTag;
	private short terminologyComponentId;
	private String queryPart;

	TerminologyBindingUri(String protocol, String codeSystem, String versionTag, short terminologyComponentId, String queryPart) {
		this.protocol = protocol;
		this.codeSystem = codeSystem;
		this.versionTag = versionTag;
		this.terminologyComponentId = terminologyComponentId;
		this.queryPart = queryPart;
	}

	public static Builder builder() {
		return new Builder();
	}
	
	public static class Builder {
		
		private String protocol;
		private String codeSystem;
		private String versionTag;
		private short terminologyComponentId;
		private String queryPart;
		
		public Builder protocol(final String protocol) {
			this.protocol = protocol;
			return this;
		}
		
		public Builder codeSystem(final String codeSystem) {
			this.codeSystem = codeSystem;
			return this;
		}
		
		public Builder versionTag(final String versionTag) {
			this.versionTag = versionTag;
			return this;
		}
		
		public Builder terminologyComponentId(final short terminologyComponentId) {
			this.terminologyComponentId = terminologyComponentId;
			return this;
		}
		
		public Builder queryPart(final String queryPart) {
			this.queryPart = queryPart;
			return this;
		}
		
		public TerminologyBindingUri build() {
			return new TerminologyBindingUri(protocol, codeSystem, versionTag, terminologyComponentId, queryPart);
		}
		
	}
	
	/**
	 * Factory method to create a new terminology binding URI from a valid URI String.
	 * @param uriString
	 * @return new {@link TerminologyBindingUri}
	 */
	public static TerminologyBindingUri fromUriString(String uriString) {
		
		
		Builder builder = builder();
		
		if (!uriString.startsWith(SNOW_OWL_PROTOCOL)) {
			throw new BadRequestException(String.format("URI '%s' is not a valid Snow Owl terminology binding URI. It should start with '%s'.", uriString, SNOW_OWL_PROTOCOL));
		}
		
		String[] splitUri = uriString.split("\\?");
		

		if (splitUri.length > 2) {
			throw new BadRequestException(String.format("Invalid Terminology binding URI [%s].", uriString));
		}
		
		//Includes the query part
		if (splitUri.length == 2) {
			parseQueryPart(builder, uriString, splitUri[1]);
		}
		
		//Path part
		StringTokenizer tokenizer = new StringTokenizer(splitUri[0], "/");

		//This should not happen
		if (!tokenizer.hasMoreTokens()) {
			throw new BadRequestException(String.format("Invalid Terminology binding URI [%s].", uriString));
		}
		
		//Code system path part	
		String codeSystemToken = tokenizer.nextToken();
		builder.codeSystem(codeSystemToken);
		
		if (!tokenizer.hasMoreTokens()) {
			return builder
				.versionTag(CodeSystemURI.HEAD)
				.terminologyComponentId(TerminologyRegistry.UNSPECIFIED_NUMBER_SHORT)
				.build();
		}
		
		String versionOrComponentPart = tokenizer.nextToken();
		
		if (tokenizer.hasMoreTokens()) {
			builder.versionTag(versionOrComponentPart);
		} else {
			//try to parse it
			try {
				short parsedShort = Short.parseShort(versionOrComponentPart);
				return builder
						.versionTag(CodeSystemURI.HEAD)
						.terminologyComponentId(parsedShort)
						.build();
				
			} catch (Exception e) {
				return builder
						.versionTag(versionOrComponentPart)
						.terminologyComponentId(TerminologyRegistry.UNSPECIFIED_NUMBER_SHORT)
						.build();
				
			}
		}
		
		String componentPart = tokenizer.nextToken();
		if (!tokenizer.hasMoreTokens()) {
			//try to parse it
			try {
				short parsedShort = Short.parseShort(componentPart);
				return builder
						.terminologyComponentId(parsedShort)
						.build();
			} catch (Exception e) {
				throw new BadRequestException(String.format("Invalid Terminology binding URI [%s].", uriString));
			}
		} else {
			throw new BadRequestException(String.format("Invalid Terminology binding URI [%s].", uriString));
		}
	}
	
	private static void parseQueryPart(Builder builder, String uriString, String string) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder(protocol);
		sb.append(":");
		sb.append(codeSystem);
		if (versionTag !=null) {
			sb.append("/");
			sb.append(versionTag);
		}
		sb.append("/");
		sb.append(terminologyComponentId);
		
		if (queryPart != null) {
			sb.append("?");
			sb.append(queryPart);
		}
		return sb.toString();
	}

}
