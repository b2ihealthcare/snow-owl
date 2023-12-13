/*
 * Copyright 2023 B2i Healthcare Pte Ltd, http://b2i.sg
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
package com.b2international.snowowl.fhir.core.model.converter;

import java.util.List;

import org.linuxforhealth.fhir.model.r5.resource.Bundle;
import org.linuxforhealth.fhir.model.r5.resource.Bundle.Entry;
import org.linuxforhealth.fhir.model.r5.resource.Bundle.Link;
import org.linuxforhealth.fhir.model.r5.resource.Resource;
import org.linuxforhealth.fhir.model.r5.type.Code;
import org.linuxforhealth.fhir.model.r5.type.code.BundleType;

import com.b2international.commons.CompareUtils;
import com.b2international.snowowl.fhir.core.model.ResourceResponseEntry;

/**
 * @since 9.0.0
 */
public class BundleConverter_50 extends AbstractConverter_50 implements BundleConverter<Bundle> {

	public static final BundleConverter<Bundle> INSTANCE = new BundleConverter_50();
	
	private BundleConverter_50() {
		super();
	}
	
	@Override
	public Bundle fromInternal(com.b2international.snowowl.fhir.core.model.Bundle bundle) {
		if (bundle == null) {
			return null;
		}
		
		Bundle.Builder builder = Bundle.builder();
		
		fromInternalResource(builder, bundle);

		// Bundle properties
		builder.identifier(fromInternal(bundle.getIdentifier()));
		
		Code type = fromInternal(bundle.getType());
		if (type != null) {
			builder.type(BundleType.of(type.getValue()));
		}
		
		builder.timestamp(fromInternal(bundle.getTimestamp()));
		builder.total(fromInternalToUnsignedInt(bundle.getTotal()));
		
		var links = bundle.getLink();
		if (!CompareUtils.isEmpty(links)) {
			for (var link : links) {
				if (link != null) {
					builder.link(fromInternal(link));
				}
			}
		}
		
		var entries = bundle.getEntry();
		if (!CompareUtils.isEmpty(entries)) {
			for (var entry : entries) {
				if (entry != null) {
					builder.entry(fromInternal(entry));
				}
			}
		}

		// "signature" is not converted
		
		return builder.build();
	}
	
	// Elements

	private Bundle.Link fromInternal(com.b2international.snowowl.fhir.core.model.Link link) {
		if (link == null) {
			return null;
		}
		
		Bundle.Link.Builder builder = Bundle.Link.builder();
		
		// XXX: Difference from R4B -- "relation" is now a code instead of a string!
		builder.relation(fromInternalToCode(link.getRelation()));
		builder.url(fromInternal(link.getUrl()));
		
		return builder.build();
	}
	
	private Bundle.Entry fromInternal(com.b2international.snowowl.fhir.core.model.Entry entry) {
		if (entry == null) {
			return null;
		}
		
		Bundle.Entry.Builder builder = Bundle.Entry.builder();
		
		// "link" is not converted because we only provide a single String instead of a Link
		
		builder.fullUrl(fromInternal(entry.getFullUrl()));
		
		if (entry instanceof com.b2international.snowowl.fhir.core.model.ResourceResponseEntry responseEntry) {
			builder.resource(fromInternal(responseEntry.getResponseResource()));
		}  else {
			throw new IllegalArgumentException("Unsupported bundle entry type '" + entry.getClass().getSimpleName() + "'.");
		}

		return builder.build();
	}
	
	
	@Override
	public com.b2international.snowowl.fhir.core.model.Bundle toInternal(Bundle bundle) {
		if (bundle == null) {
			return null;
		}
		
		var builder = com.b2international.snowowl.fhir.core.model.Bundle.builder();

		toInternalResource(builder, bundle);

		// Bundle properties
		builder.identifier(toInternal(bundle.getIdentifier()));
				
		var type = toInternal(bundle.getType());
		if (type != null) {
			builder.type(com.b2international.snowowl.fhir.core.codesystems.BundleType.forValue(type.getCodeValue()));
		}
				
		builder.timestamp(toInternal(bundle.getTimestamp()));
		builder.total(toInternal(bundle.getTotal()));
				
		List<Link> links = bundle.getLink();
		for (Link link : links) {
			var internalLink = toInternal(link);
			builder.addLink(internalLink.getRelation(), internalLink.getUrl().getUriValue());
		}
		
		List<Entry> entries = bundle.getEntry();
		for (Entry entry : entries) {
			builder.addEntry(toInternal(entry));
		}

		// "signature" is not converted
		
		return builder.build();
	}

	// Elements
	
	private com.b2international.snowowl.fhir.core.model.Link toInternal(Bundle.Link link) {
		if (link == null) {
			return null;
		}
		
		return new com.b2international.snowowl.fhir.core.model.Link(
			toInternalString(link.getRelation()), 
			toInternal(link.getUrl()));
	}

	private com.b2international.snowowl.fhir.core.model.Entry toInternal(Bundle.Entry entry) {
		if (entry == null) {
			return null;
		}
		
		final com.b2international.snowowl.fhir.core.model.Entry.Builder<?, ?> builder;
		
		// "link" is not converted because we only provide a single String instead of a Link
		
		final Resource resource = entry.getResource();
		
		if (entry.getRequest() == null && resource != null) {
			// This is a response with a resource
			builder = ResourceResponseEntry.builder().resource(toInternal(resource));
		} else {
			throw new IllegalArgumentException("Unsupported bundle entry content.");
		}
		
		builder.fullUrl(toInternal(entry.getFullUrl()));
	
		return builder.build();
	}
}
