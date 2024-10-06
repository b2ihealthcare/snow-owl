/*
 * Copyright 2024 B2i Healthcare, https://b2ihealthcare.com
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
package com.b2international.snowowl.fhir.rest;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Objects;

import org.hl7.fhir.convertors.factory.VersionConvertorFactory_40_50;
import org.hl7.fhir.convertors.factory.VersionConvertorFactory_43_50;
import org.hl7.fhir.exceptions.FHIRFormatError;
import org.hl7.fhir.r5.elementmodel.Manager;
import org.hl7.fhir.r5.elementmodel.Manager.FhirFormat;
import org.hl7.fhir.r5.model.Enumerations.FHIRVersion;
import org.hl7.fhir.r5.model.Resource;
import org.springframework.http.MediaType;
import org.springframework.web.server.NotAcceptableStatusException;

import com.b2international.commons.StringUtils;
import com.b2international.commons.exceptions.NotImplementedException;
import com.google.common.collect.Maps;

/**
 * @since 9.4
 */
public final class FhirMediaType {

	// FHIR-specific media types should be supplied in "content-type" and "accept" headers
	// This always resolves to the available latest FHIR version in the server 
	protected static final String APPLICATION_FHIR_JSON_VALUE = "application/fhir+json";
	protected static final String APPLICATION_FHIR_XML_VALUE = "application/fhir+xml";
	
	// Versioned FHIR-specific media types should be supplied in "content-type" and "accept" headers
	private static final String MIME_TYPE_FHIR_VERSION_PARAMETER = "fhirVersion";
	protected static final String APPLICATION_FHIR_JSON_4_0_VALUE = "application/fhir+json; fhirVersion=4.0";
	protected static final String APPLICATION_FHIR_XML_4_0_VALUE = "application/fhir+xml; fhirVersion=4.0";
	
	protected static final String APPLICATION_FHIR_JSON_4_3_VALUE = "application/fhir+json; fhirVersion=4.3";
	protected static final String APPLICATION_FHIR_XML_4_3_VALUE = "application/fhir+xml; fhirVersion=4.3";
	
	protected static final String APPLICATION_FHIR_JSON_5_0_VALUE = "application/fhir+json; fhirVersion=5.0";
	protected static final String APPLICATION_FHIR_XML_5_0_VALUE = "application/fhir+xml; fhirVersion=5.0";

	// Short values are only admitted as _format parameters
	protected static final String FORMAT_JSON = "json";
	protected static final String FORMAT_XML = "xml";

	// More general media types are allowed both as a _format parameter as well as an "accept" header
	protected static final String TEXT_JSON_VALUE = "text/json";
	protected static final String TEXT_XML_VALUE = MediaType.TEXT_XML_VALUE;
	
	protected static final String APPLICATION_JSON_VALUE = MediaType.APPLICATION_JSON_VALUE;
	protected static final String APPLICATION_XML_VALUE = MediaType.APPLICATION_XML_VALUE;

	// Keep parsed forms of our custom media types around as well
	private static final MediaType TEXT_JSON = MediaType.parseMediaType(TEXT_JSON_VALUE);
	
	private static final MediaType APPLICATION_FHIR_JSON = MediaType.parseMediaType(APPLICATION_FHIR_JSON_VALUE);
	private static final MediaType APPLICATION_FHIR_XML = MediaType.parseMediaType(APPLICATION_FHIR_XML_VALUE);
	
	private static final MediaType APPLICATION_FHIR_JSON_4_0 = MediaType.parseMediaType(APPLICATION_FHIR_JSON_4_0_VALUE);
	private static final MediaType APPLICATION_FHIR_XML_4_0 = MediaType.parseMediaType(APPLICATION_FHIR_XML_4_0_VALUE);
	
	private static final MediaType APPLICATION_FHIR_JSON_4_3 = MediaType.parseMediaType(APPLICATION_FHIR_JSON_4_3_VALUE);
	private static final MediaType APPLICATION_FHIR_XML_4_3 = MediaType.parseMediaType(APPLICATION_FHIR_XML_4_3_VALUE);
	
	private static final MediaType APPLICATION_FHIR_JSON_5_0 = MediaType.parseMediaType(APPLICATION_FHIR_JSON_5_0_VALUE);
	private static final MediaType APPLICATION_FHIR_XML_5_0 = MediaType.parseMediaType(APPLICATION_FHIR_XML_5_0_VALUE);

	// Last ditch effort: return JSON when "*/*" media type is accepted by the client
	private static final String ALL_VALUE = MediaType.ALL_VALUE;
	
	// All currently supported media type, versioned and unversioned forms
	private static final List<MediaType> SUPPORTED_MEDIA_TYPES = List.of(
		APPLICATION_FHIR_JSON_5_0,
		APPLICATION_FHIR_XML_5_0,
		
		APPLICATION_FHIR_JSON_4_3,
		APPLICATION_FHIR_XML_4_3,
		
		APPLICATION_FHIR_JSON_4_0,
		APPLICATION_FHIR_XML_4_0,
		
		APPLICATION_FHIR_JSON,
		APPLICATION_FHIR_XML,
		
		TEXT_JSON,
		MediaType.TEXT_XML,
		
		MediaType.APPLICATION_JSON,
		MediaType.APPLICATION_XML,
		
		MediaType.ALL
	);
	
	// The FHIR Version Snow Owl by default uses (should match the model version used in snowowl.fhir.core)
	protected static final MediaType CURRENT_JSON_MEDIA_TYPE = APPLICATION_FHIR_JSON_5_0;
	protected static final MediaType CURRENT_XML_MEDIA_TYPE = APPLICATION_FHIR_XML_5_0;
	
	private final MediaType mediaType;
	private final FhirFormat fhirFormat;
	private final FHIRVersion fhirVersion;
	
	private FhirMediaType(MediaType mediaType) {
		this.mediaType = Objects.requireNonNull(mediaType);
		if (mediaType.getType().contains(FORMAT_JSON)) {
			this.fhirFormat = Manager.FhirFormat.JSON;
		} else if (mediaType.getType().contains(FORMAT_XML)) {
			this.fhirFormat = Manager.FhirFormat.XML;
		} else {
			throw new IllegalStateException();
		}
		
		final String fhirVersionValue = mediaType.getParameter(MIME_TYPE_FHIR_VERSION_PARAMETER);
		this.fhirVersion = FHIRVersion.fromCode(fhirVersionValue);
	}

	public MediaType getMediaType() {
		return this.mediaType;
	}
	
	public FhirFormat getFhirFormat() {
		return fhirFormat;
	}
	
	public FHIRVersion getFhirVersion() {
		return fhirVersion;
	}

	public Resource parseResource(InputStream in) throws FHIRFormatError, IOException {
		switch (fhirFormat) {
		case JSON:
			return parseResourceJson(in);
		case XML:
			return parseResourceXml(in); 
		default: 
			throw new NotImplementedException("No parser implementation found for format: " + fhirFormat);
		}
	}
	
	private Resource parseResourceJson(InputStream in) throws FHIRFormatError, IOException {
		switch (fhirVersion) {
		case _4_0:
			org.hl7.fhir.r4.model.Resource r4 = new org.hl7.fhir.r4.formats.JsonParser().parse(in);
			return VersionConvertorFactory_40_50.convertResource(r4);
		case _4_3:
			org.hl7.fhir.r4b.model.Resource r4b = new org.hl7.fhir.r4b.formats.JsonParser().parse(in);
			return VersionConvertorFactory_43_50.convertResource(r4b);
		case _5_0:
			org.hl7.fhir.r5.model.Resource r5 = new org.hl7.fhir.r5.formats.JsonParser().parse(in);
			return r5;
		default: 
			throw new NotImplementedException("No JSON parser implementation found for version: " + fhirVersion);
		}
	}
	
	private Resource parseResourceXml(InputStream in) throws FHIRFormatError, IOException {
		switch (fhirVersion) {
		case _4_0:
			org.hl7.fhir.r4.model.Resource r4 = new org.hl7.fhir.r4.formats.XmlParser().parse(in);
			return VersionConvertorFactory_40_50.convertResource(r4);
		case _4_3:
			org.hl7.fhir.r4b.model.Resource r4b = new org.hl7.fhir.r4b.formats.XmlParser().parse(in);
			return VersionConvertorFactory_43_50.convertResource(r4b);
		case _5_0:
			org.hl7.fhir.r5.model.Resource r5 = new org.hl7.fhir.r5.formats.XmlParser().parse(in);
			return r5;
		default: 
			throw new NotImplementedException("No XML parser implementation found for version: " + fhirVersion);
		}
	}

	public void writeResource(ByteArrayOutputStream baos, Resource resource, boolean pretty) throws IOException {
		switch (fhirFormat) {
		case JSON:
			writeResourceJson(baos, resource, pretty);
			break;
		case XML:
			writeResourceXml(baos, resource, pretty);
			break;
		default: 
			throw new NotImplementedException("No serializer implementation found for format: " + fhirFormat);
		}
	}
	
	private void writeResourceJson(ByteArrayOutputStream baos, Resource resource, boolean pretty) throws IOException {
		switch (fhirVersion) {
		case _4_0:
			org.hl7.fhir.r4.model.Resource r4 = VersionConvertorFactory_40_50.convertResource(resource);
			new org.hl7.fhir.r4.formats.JsonParser().setOutputStyle(pretty ? org.hl7.fhir.r4.formats.IParser.OutputStyle.PRETTY : org.hl7.fhir.r4.formats.IParser.OutputStyle.NORMAL).compose(baos, r4);
			break;
		case _4_3:
			org.hl7.fhir.r4b.model.Resource r4b = VersionConvertorFactory_43_50.convertResource(resource);
			new org.hl7.fhir.r4b.formats.JsonParser().setOutputStyle(pretty ? org.hl7.fhir.r4b.formats.IParser.OutputStyle.PRETTY : org.hl7.fhir.r4b.formats.IParser.OutputStyle.NORMAL).compose(baos, r4b);
			break;
		case _5_0:
			new org.hl7.fhir.r5.formats.JsonParser().setOutputStyle(pretty ? org.hl7.fhir.r5.formats.IParser.OutputStyle.PRETTY : org.hl7.fhir.r5.formats.IParser.OutputStyle.NORMAL).compose(baos, resource);
			break;
		default: 
			throw new NotImplementedException("No JSON serializer implementation found for version: " + fhirVersion);
		}
	}
	
	private void writeResourceXml(ByteArrayOutputStream baos, Resource resource, boolean pretty) throws IOException {
		switch (fhirVersion) {
		case _4_0:
			org.hl7.fhir.r4.model.Resource r4 = VersionConvertorFactory_40_50.convertResource(resource);
			new org.hl7.fhir.r4.formats.XmlParser().setOutputStyle(pretty ? org.hl7.fhir.r4.formats.IParser.OutputStyle.PRETTY : org.hl7.fhir.r4.formats.IParser.OutputStyle.NORMAL).compose(baos, r4);
			break;
		case _4_3:
			org.hl7.fhir.r4b.model.Resource r4b = VersionConvertorFactory_43_50.convertResource(resource);
			new org.hl7.fhir.r4b.formats.XmlParser().setOutputStyle(pretty ? org.hl7.fhir.r4b.formats.IParser.OutputStyle.PRETTY : org.hl7.fhir.r4b.formats.IParser.OutputStyle.NORMAL).compose(baos, r4b);
			break;
		case _5_0:
			new org.hl7.fhir.r5.formats.XmlParser().setOutputStyle(pretty ? org.hl7.fhir.r5.formats.IParser.OutputStyle.PRETTY : org.hl7.fhir.r5.formats.IParser.OutputStyle.NORMAL).compose(baos, resource);
			break;
		default: 
			throw new NotImplementedException("No XML serializer implementation found for version: " + fhirVersion);
		}
	}

	public static FhirMediaType parse(String header, String _format) {
		return new FhirMediaType(getMediaType(header, _format));
	}
	
	private static MediaType getMediaType(final String accept, final String _format) {
		/*
		 * The _format query parameter allows overriding whatever comes in as the "accept"
		 * header value (for scenarios where the client has no control over the header).
		 */
		if (!StringUtils.isEmpty(_format)) {
			return getFormat(_format);
		} else if (!StringUtils.isEmpty(accept)) {
			List<MediaType> mediaTypeCandidates = getMediaTypeCandidates(accept);
			
			if (!mediaTypeCandidates.isEmpty()) {
				return getFormat(mediaTypeCandidates.get(0).toString());
			} else {
				return getFormat(accept);
			}
		} else {
			return CURRENT_JSON_MEDIA_TYPE;
		}
	}
	
	private static List<MediaType> getMediaTypeCandidates(final String accept) {
		final List<MediaType> mediaTypeCandidates = MediaType.parseMediaTypes(accept);
		
		if (!mediaTypeCandidates.isEmpty()) {
			MediaType.sortBySpecificityAndQuality(mediaTypeCandidates);
			
			// Remove quality values and other (eg. charset) parameters once the list is sorted
			for (int i = 0; i < mediaTypeCandidates.size(); i++) {
				MediaType oldType = mediaTypeCandidates.get(i);
				
				// keep only the fhirVersion parameter if defined
				var reducedParametersMap = Maps.filterEntries(oldType.getParameters(), (entry) -> MIME_TYPE_FHIR_VERSION_PARAMETER.equals(entry.getKey()));
				mediaTypeCandidates.set(i, new MediaType(oldType, reducedParametersMap));
			}
			
			mediaTypeCandidates.retainAll(SUPPORTED_MEDIA_TYPES);
		}
		
		return mediaTypeCandidates;
	}
	
	private static MediaType getFormat(final String mediaType) {
		switch (mediaType) {
		
			case FORMAT_JSON: //$FALL-THROUGH$
			case TEXT_JSON_VALUE: //$FALL-THROUGH$
			case APPLICATION_FHIR_JSON_VALUE: //$FALL-THROUGH$
			case APPLICATION_JSON_VALUE: //$FALL-THROUGH$
			case ALL_VALUE:
				return CURRENT_JSON_MEDIA_TYPE;
				
			case APPLICATION_FHIR_JSON_4_0_VALUE:
				return APPLICATION_FHIR_JSON_4_0;
				
			case APPLICATION_FHIR_JSON_4_3_VALUE:
				return APPLICATION_FHIR_JSON_4_3;
			
			case APPLICATION_FHIR_JSON_5_0_VALUE:
				return APPLICATION_FHIR_JSON_5_0;
				
			case FORMAT_XML: //$FALL-THROUGH$
			case TEXT_XML_VALUE: //$FALL-THROUGH$
			case APPLICATION_FHIR_XML_VALUE: //$FALL-THROUGH$
			case APPLICATION_XML_VALUE:
				return CURRENT_XML_MEDIA_TYPE;
				
			case APPLICATION_FHIR_XML_4_0_VALUE: 
				return APPLICATION_FHIR_XML_4_0;
				
			case APPLICATION_FHIR_XML_4_3_VALUE:
				return APPLICATION_FHIR_XML_4_3;
				
			case APPLICATION_FHIR_XML_5_0_VALUE:
				return APPLICATION_FHIR_XML_5_0;
				
			default:
				throw new NotAcceptableStatusException(SUPPORTED_MEDIA_TYPES);
		}
	}

}
