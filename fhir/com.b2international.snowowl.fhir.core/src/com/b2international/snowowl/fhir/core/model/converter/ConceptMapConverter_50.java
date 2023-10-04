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

import static com.google.common.collect.Sets.newLinkedHashSet;

import java.util.List;
import java.util.Map;
import java.util.Set;

import org.linuxforhealth.fhir.model.r5.resource.ConceptMap;
import org.linuxforhealth.fhir.model.r5.type.*;
import org.linuxforhealth.fhir.model.r5.type.Boolean;
import org.linuxforhealth.fhir.model.r5.type.String;
import org.linuxforhealth.fhir.model.r5.type.code.ConceptMapGroupUnmappedMode;
import org.linuxforhealth.fhir.model.r5.type.code.ConceptMapRelationship;

import com.b2international.commons.CompareUtils;
import com.b2international.commons.StringUtils;
import com.google.common.collect.Maps;

/**
 * @since 9.0
 */
public class ConceptMapConverter_50 extends AbstractConverter_50 implements ConceptMapConverter<ConceptMap> {

	public static final ConceptMapConverter<ConceptMap> INSTANCE = new ConceptMapConverter_50();
	
	private ConceptMapConverter_50() {
		super();
	}
	
	@Override
	public ConceptMap fromInternal(com.b2international.snowowl.fhir.core.model.conceptmap.ConceptMap conceptMap) {
		if (conceptMap == null) {
			return null;
		}
		
		ConceptMap.Builder builder = ConceptMap.builder();
		
		fromInternalResource(builder, conceptMap);
		fromInternalDomainResource(builder, conceptMap);
		fromInternalCanonicalResource(builder, conceptMap);
		
		// CanonicalResource properties not handled above
		
		var identifiers = conceptMap.getIdentifiers();
		if (!CompareUtils.isEmpty(identifiers)) {
			for (var identifier : identifiers) {
				if (identifier != null) {
					builder.identifier(fromInternal(identifier));
				}
			}
		}
		
		builder.copyright(fromInternalToMarkdown(conceptMap.getCopyright()));

		// MetadataResource properties (none of them are converted)
		
		// ConceptMap properties
		
		/*
		 * XXX: We rely on the following behavior when specifying source and target
		 * scopes (in Snow Owl clients can always refer to code systems as the
		 * "all concepts" value set even if a corresponding "all concepts" value set URI
		 * is given):
		 * 
		 * "Every code system has an implicit value set that is "all the concepts
		 * defined in the code system" (CodeSystem.valueSet). For some code systems,
		 * these value set URIs are defined in advance (e.g. for LOINC icon, it is
		 * http://loinc.org/vs). However, for some code systems, they are not known.
		 * 
		 * Clients can refer to these implicit value sets by providing the URI for the
		 * code system itself."
		 */
		if (conceptMap.getSourceUri() != null) {
			builder.sourceScope(fromInternal(conceptMap.getSourceUri()));
		}
		
		if (conceptMap.getSourceCanonical() != null) {
			builder.sourceScope(fromInternalToCanonical(conceptMap.getSourceCanonical()));
		}
		
		if (conceptMap.getTargetUri() != null) {
			builder.sourceScope(fromInternal(conceptMap.getTargetUri()));
		}
		
		if (conceptMap.getTargetCanonical() != null) {
			builder.sourceScope(fromInternalToCanonical(conceptMap.getTargetCanonical()));
		}
		
		// "property" is not converted (new in R5)

		Set<ConceptMap.AdditionalAttribute> additionalAttributes = newLinkedHashSet();

		var groups = conceptMap.getGroups();
		if (!CompareUtils.isEmpty(groups)) {
			for (var group : groups) {
				if (group != null) {
					builder.group(fromInternal(group, additionalAttributes));
				}
			}
		}
		
		builder.additionalAttribute(additionalAttributes);
		
		return builder.build();
	}

	// Elements

	private ConceptMap.Group fromInternal(
		com.b2international.snowowl.fhir.core.model.conceptmap.Group group, 
		Set<ConceptMap.AdditionalAttribute> additionalAttributes
	) {
		if (group == null) {
			return null;
		}
		
		ConceptMap.Group.Builder builder = ConceptMap.Group.builder();
		
		// In R5 "source" and "target" are canonical URIs, so we need to combine version information with the URI
		if (group.getSource() != null) {
			java.lang.String sourceUriValue = group.getSource().getUriValue();
			java.lang.String sourceVersion = group.getSourceVersion();
			
			if (!StringUtils.isEmpty(sourceVersion)
				&& !StringUtils.isEmpty(sourceUriValue) 
				&& !sourceUriValue.contains("|")) {
				
				builder.source(Canonical.of(sourceUriValue, sourceVersion));
			} else {
				builder.source(fromInternalToCanonical(group.getSource()));
			}
		}
		
		if (group.getTarget() != null) {
			java.lang.String targetUriValue = group.getTarget().getUriValue();
			java.lang.String targetVersion = group.getTargetVersion();
			
			if (!StringUtils.isEmpty(targetVersion)
					&& !StringUtils.isEmpty(targetUriValue) 
					&& !targetUriValue.contains("|")) {
				
				builder.target(Canonical.of(targetUriValue, targetVersion));
			} else {
				builder.target(fromInternalToCanonical(group.getTarget()));
			}
		}
		
		
		var elements = group.getElements();
		if (!CompareUtils.isEmpty(elements)) {
			for (var element : elements) {
				if (element != null) {
					builder.element(fromInternal(element, additionalAttributes));
				}
			}
		}

		builder.unmapped(fromInternal(group.getUnmapped()));
		
		return builder.build();
	}
	
	private ConceptMap.Group.Element fromInternal(
		com.b2international.snowowl.fhir.core.model.conceptmap.ConceptMapElement element, 
		Set<ConceptMap.AdditionalAttribute> additionalAttributes
	) {
		if (element == null) {
			return null;
		}
		
		ConceptMap.Group.Element.Builder builder = ConceptMap.Group.Element.builder();
		
		builder.code(fromInternal(element.getCode()));
		builder.display(fromInternal(element.getDisplay()));
		// "valueSet" is not mapped (new in R5)
		// "noMap" is not mapped (new in R5)
		
		var targets = element.getTargets();
		if (!CompareUtils.isEmpty(targets)) {
			for (var target : targets) {
				if (target != null) {
					builder.target(fromInternal(target, additionalAttributes));
				}
			}
		}
		
		return builder.build();
	}
	
	
	private ConceptMap.Group.Element.Target fromInternal(com.b2international.snowowl.fhir.core.model.conceptmap.Target target, Set<ConceptMap.AdditionalAttribute> additionalAttributes) {
		if (target == null) {
			return null;
		}
		
		ConceptMap.Group.Element.Target.Builder builder = ConceptMap.Group.Element.Target.builder();
		
		builder.code(fromInternal(target.getCode()));
		builder.display(target.getDisplay());
		// "valueSet" is not mapped (new in R5)
		
		var equivalenceCode = target.getEquivalence();
		if (equivalenceCode != null) {
			var equivalence = com.b2international.snowowl.fhir.core.codesystems.ConceptMapEquivalence.forValue(equivalenceCode.getCodeValue());
			final ConceptMapRelationship relationship;
			
			switch (equivalence) {
				case RELATEDTO: //$FALL-THROUGH$
				case INEXACT:
					relationship = ConceptMapRelationship.RELATED_TO;
					break;

				case EQUAL: //$FALL-THROUGH$
				case EQUIVALENT:
					relationship = ConceptMapRelationship.EQUIVALENT;
					break;

				// "target" is wider -> "source" is narrower
				case WIDER: //$FALL-THROUGH$
				case SUBSUMES:
					relationship = ConceptMapRelationship.SOURCE_IS_NARROWER_THAN_TARGET;
					break;
				
				// "target" is narrower -> "source" is broader
				case NARROWER:
				case SPECIALIZES:
					relationship = ConceptMapRelationship.SOURCE_IS_BROADER_THAN_TARGET;
					break;
					
				// XXX: "disjoint" does not appear in the specification's conversion table, but I think it fits here
				case DISJOINT:
				case UNMATCHED:
					relationship = ConceptMapRelationship.NOT_RELATED_TO;
					break;
				
				default:
					throw new IllegalArgumentException("Unexpected concept map equivalence value '" + equivalence + "'.");
			}
			
			builder.relationship(relationship);
		}
		
		builder.comment(fromInternal(target.getComment()));
		// "property" is not mapped (new in R5)
		
		var dependsOnElements = target.getDependsOnElements();
		if (!CompareUtils.isEmpty(dependsOnElements)) {
			for (var dependsOn : dependsOnElements) {
				if (dependsOn != null) {
					builder.dependsOn(fromInternal(dependsOn));
					additionalAttributes.add(fromInternalToAttribute(dependsOn));
				}
			}
		}

		var products = target.getProducts();
		if (!CompareUtils.isEmpty(products)) {
			for (var product : products) {
				if (product != null) {
					builder.product(fromInternal(product));
					additionalAttributes.add(fromInternalToAttribute(product));
				}
			}
		}
		
		return builder.build();
	}

	/*
	 * Conversion steps are described in "pseudocode" (FML) at http://hl7.org/fhir/extensions/conversions-ConceptMap.html#Target-ConceptMap4Bto5
	 */
	private ConceptMap.Group.Element.Target.DependsOn fromInternal(com.b2international.snowowl.fhir.core.model.conceptmap.DependsOn dependsOn) {
		if (dependsOn == null) {
			return null;
		}

		ConceptMap.Group.Element.Target.DependsOn.Builder builder = ConceptMap.Group.Element.Target.DependsOn.builder();
		
		var property = dependsOn.getProperty();
		if (property != null && !StringUtils.isEmpty(property.getUriValue())) {
			// XXX: Use the entire "property" URI from R4 as an "attribute" Code in R5
			builder.attribute(Code.of(property.getUriValue()));
		}
				
		var system = dependsOn.getSystem();
		if (system == null || StringUtils.isEmpty(system.getUriValue())) {
			// XXX: "value" is assumed to be a String if "system" is not present
			builder.value(fromInternal(dependsOn.getValue()));
		} else {
			// Otherwise we build a Coding out of the information that we have
			builder.value(Coding.builder()
				.system(fromInternal(system))
				.display(fromInternal(dependsOn.getDisplay()))
				.code(fromInternalToCode(dependsOn.getValue()))
				.build());
		}
		
		// "valueSet" is not converted (new in R5)
		
		return builder.build();
	}
	
	private ConceptMap.AdditionalAttribute fromInternalToAttribute(com.b2international.snowowl.fhir.core.model.conceptmap.DependsOn dependsOn) {
		if (dependsOn == null) {
			return null;
		}
		
		ConceptMap.AdditionalAttribute.Builder builder = ConceptMap.AdditionalAttribute.builder();
		
		return builder.build();
	}
	
	private ConceptMap.Group.Unmapped fromInternal(com.b2international.snowowl.fhir.core.model.conceptmap.UnMapped unmapped) {
		if (unmapped == null) {
			return null;
		}
		
		ConceptMap.Group.Unmapped.Builder builder = ConceptMap.Group.Unmapped.builder();
		
		Code mode = fromInternal(unmapped.getMode());
		if (mode != null) {
			var modeEnum = com.b2international.snowowl.fhir.core.codesystems.ConceptMapGroupUnmappedMode.forValue(mode.getValue());
			final ConceptMapGroupUnmappedMode mappedMode;
			
			switch (modeEnum) {
				case FIXED:
					mappedMode = ConceptMapGroupUnmappedMode.FIXED;
					break;
				case OTHER_MAP:
					mappedMode = ConceptMapGroupUnmappedMode.OTHER_MAP;
					break;
				case PROVIDED:
					mappedMode = ConceptMapGroupUnmappedMode.USE_SOURCE_CODE;
					break;
				default:
					throw new IllegalArgumentException("Unexpected unmapped mode '" + modeEnum + "'.");
			}
			
			builder.mode(mappedMode);
		}

		builder.code(fromInternal(unmapped.getCode()));
		builder.display(fromInternal(unmapped.getDisplay()));
		builder.otherMap(fromInternalToCanonical(unmapped.getUrl()));
		
		// "valueSet" is not converted (new in R5)
		// "relationship" is not converted (new in R5)
		
		
		return builder.build();
	}
	
	@Override
	public com.b2international.snowowl.fhir.core.model.conceptmap.ConceptMap toInternal(ConceptMap conceptMap) {
		if (conceptMap == null) {
			return null;
		}
		
		var builder = com.b2international.snowowl.fhir.core.model.conceptmap.ConceptMap.builder();
		
		toInternalResource(builder, conceptMap);
		toInternalDomainResource(builder, conceptMap);
		toInternalCanonicalResource(builder, conceptMap);
		
		// CanonicalResource properties not handled above
		
		List<Identifier> identifiers = conceptMap.getIdentifier();
		for (Identifier identifier : identifiers) {
			builder.addIdentifier(toInternal(identifier));
		}
		
		builder.copyright(toInternal(conceptMap.getCopyright()));
		
		// MetadataResource properties (none of them are converted)
		
		// ConceptMap properties
		
		if (conceptMap.getSourceScope() instanceof Canonical sourceCanonical) {
			builder.sourceCanonical(toInternal(sourceCanonical));
		} else if (conceptMap.getSourceScope() instanceof Uri sourceUri) {
			builder.sourceUri(toInternal(sourceUri));
		}
		
		if (conceptMap.getTargetScope() instanceof Canonical sourceCanonical) {
			builder.targetCanonical(toInternal(sourceCanonical));
		} else if (conceptMap.getTargetScope() instanceof Uri sourceUri) {
			builder.targetUri(toInternal(sourceUri));
		}
		
		// "property" is not converted (new in R5)
		
		// XXX: "additionalAttribute" is not converted, only indexed (new in R5) 
		Map<java.lang.String, ConceptMap.AdditionalAttribute> attributesByCode = Maps.uniqueIndex(
			conceptMap.getAdditionalAttribute(), 
			attribute -> attribute.getCode().getValue());
		
		List<ConceptMap.Group> groups = conceptMap.getGroup();
		for (ConceptMap.Group group : groups) {
			builder.addGroup(toInternal(group, attributesByCode));
		}
		
		return builder.build();
	}
	
	// Elements
	
	private com.b2international.snowowl.fhir.core.model.conceptmap.Group toInternal(
		ConceptMap.Group group, 
		Map<java.lang.String, ConceptMap.AdditionalAttribute> attributesByCode
	) {
		if (group == null) {
			return null;
		}
		
		var builder = com.b2international.snowowl.fhir.core.model.conceptmap.Group.builder();
		
		/*
		 * In R5 "source" and "target" are canonical URIs, so we need to split version
		 * information from the URI (if present).
		 * 
		 * We assume that the URI contains the fragment portion after the version (there
		 * aren't any clear indications whether they could appear in reverse order).
		 */
		Canonical source = group.getSource();
		if (source != null) {
			java.lang.String sourceValue = source.getValue();
			java.lang.String fragment = "";
			
			int fragmentIdx = sourceValue.lastIndexOf('#');
			if (fragmentIdx > 0) {
				// Save fragment if it was not the last character in the string
				if (fragmentIdx < sourceValue.length() - 1) {
					fragment = sourceValue.substring(fragmentIdx);
				}
				
				// Remove fragment from URI
				sourceValue = sourceValue.substring(0, fragmentIdx);
			}
			
			int versionIdx = sourceValue.lastIndexOf('|');
			if (versionIdx > 0) {
				if (versionIdx < sourceValue.length() - 1) {
					builder.sourceVersion(sourceValue.substring(versionIdx + 1));
				}
				
				// Remove version from URI as well
				sourceValue = sourceValue.substring(0, versionIdx);
			}
			
			// Recombine URI and fragment after removing version from the middle
			builder.source(sourceValue + fragment);
		}
		
		Canonical target = group.getTarget();
		if (target != null) {
			java.lang.String targetValue = target.getValue();
			java.lang.String fragment = "";
			
			int fragmentIdx = targetValue.lastIndexOf('#');
			if (fragmentIdx > 0) {
				// Save fragment if it was not the last character in the string
				if (fragmentIdx < targetValue.length() - 1) {
					fragment = targetValue.substring(fragmentIdx);
				}
				
				// Remove fragment from URI
				targetValue = targetValue.substring(0, fragmentIdx);
			}
			
			int versionIdx = targetValue.lastIndexOf('|');
			if (versionIdx > 0) {
				if (versionIdx < targetValue.length() - 1) {
					builder.targetVersion(targetValue.substring(versionIdx + 1));
				}
				
				// Remove version from URI as well
				targetValue = targetValue.substring(0, versionIdx);
			}
			
			// Recombine URI and fragment after removing version from the middle
			builder.target(targetValue + fragment);
		}
		
		List<ConceptMap.Group.Element> elements = group.getElement();
		for (ConceptMap.Group.Element element : elements) {
			builder.addElement(toInternal(element, attributesByCode));
		}
		
		builder.unmapped(toInternal(group.getUnmapped()));
		
		return builder.build();
	}
	
	private com.b2international.snowowl.fhir.core.model.conceptmap.ConceptMapElement toInternal(
		ConceptMap.Group.Element element,
		Map<java.lang.String, ConceptMap.AdditionalAttribute> attributesByCode
	) {
		if (element == null) {
			return null;
		}
		
		var builder = com.b2international.snowowl.fhir.core.model.conceptmap.ConceptMapElement.builder();
		
		builder.code(toInternal(element.getCode()));
		builder.display(toInternal(element.getDisplay()));
		// "valueSet" is not mapped (new in R5)
		// "noMap" is not mapped (new in R5)
		
		List<ConceptMap.Group.Element.Target> targets = element.getTarget();
		for (ConceptMap.Group.Element.Target target : targets) {
			builder.addTarget(toInternal(target, attributesByCode));
		}
		
		return builder.build();
	}
	
	
	private com.b2international.snowowl.fhir.core.model.conceptmap.Target toInternal(
		ConceptMap.Group.Element.Target target,
		Map<java.lang.String, ConceptMap.AdditionalAttribute> attributesByCode
	) {
		if (target == null) {
			return null;
		}
		
		var builder = com.b2international.snowowl.fhir.core.model.conceptmap.Target.builder();
		
		builder.code(toInternal(target.getCode()));
		builder.display(toInternal(target.getDisplay()));
		// "valueSet" is not mapped (new in R5)
		
		ConceptMapRelationship.Value relationship = target.getRelationship().getValueAsEnum();
		final com.b2international.snowowl.fhir.core.codesystems.ConceptMapEquivalence equivalence;
			
		switch (relationship) {
			case RELATED_TO:
				equivalence = com.b2international.snowowl.fhir.core.codesystems.ConceptMapEquivalence.RELATEDTO;
				break;
				
			case EQUIVALENT:
				equivalence = com.b2international.snowowl.fhir.core.codesystems.ConceptMapEquivalence.EQUIVALENT;
				break;
				
			// "source" is narrower -> "target" is wider
			case SOURCE_IS_NARROWER_THAN_TARGET:
				equivalence = com.b2international.snowowl.fhir.core.codesystems.ConceptMapEquivalence.WIDER;
				break;
				
			// "source" is broader -> "target" is narrower 
			case SOURCE_IS_BROADER_THAN_TARGET:
				equivalence = com.b2international.snowowl.fhir.core.codesystems.ConceptMapEquivalence.NARROWER;
				break;
				
			case NOT_RELATED_TO:
				equivalence = com.b2international.snowowl.fhir.core.codesystems.ConceptMapEquivalence.UNMATCHED;
				break;
				
			default:
				throw new IllegalArgumentException("Unexpected concept map relationship value '" + relationship + "'.");
		}
			
		builder.equivalence(equivalence);
		builder.comment(toInternal(target.getComment()));
		// "property" is not mapped (new in R5)
		
		List<ConceptMap.Group.Element.Target.DependsOn> dependsOnElements = target.getDependsOn();
		for (ConceptMap.Group.Element.Target.DependsOn dependsOn : dependsOnElements) {
			builder.addDependsOn(toInternal(dependsOn, attributesByCode));
		}
		
		List<ConceptMap.Group.Element.Target.DependsOn> products = target.getProduct();
		for (ConceptMap.Group.Element.Target.DependsOn product : products) {
			builder.addProduct(toInternal(product, attributesByCode));
		}
		
		return builder.build();
	}
	
	private com.b2international.snowowl.fhir.core.model.conceptmap.DependsOn toInternal(
		ConceptMap.Group.Element.Target.DependsOn dependsOn,
		Map<java.lang.String, ConceptMap.AdditionalAttribute> attributesByCode
	) {
		if (dependsOn == null) {
			return null;
		}
		
		/* 
		 * "valueSet" is not converted (new in R5); if there is one present, the "dependsOn" element
		 * will not have a value so we skip this instance entirely.
		 */
		if (dependsOn.getValueSet() != null) {
			return null;
		}
		
		// The same holds true for Quantity values
		if (dependsOn.getValue() instanceof Quantity) {
			return null;
		}
		
		var builder = com.b2international.snowowl.fhir.core.model.conceptmap.DependsOn.builder();
		
		java.lang.String attribute = dependsOn.getAttribute().getValue();
		if (!attributesByCode.containsKey(attribute)) {
			// Use "attribute" directly as a URI
			builder.property(attribute);
		} else {
			// Retrieve information from the "additionalAttribute" list instead
			builder.property(toInternal(attributesByCode.get(attribute).getUri()));
		}
		
		Element element = dependsOn.getValue();
		
		if (element instanceof Code code) {
			builder.value(code.getValue());
		} else if (element instanceof Coding coding) {
			builder.system(toInternal(coding.getSystem()));
			builder.display(toInternal(coding.getDisplay()));
			builder.value(toInternalString(coding.getCode()));
		} else if (element instanceof String string) {
			builder.value(toInternal(string));
		} else if (element instanceof Boolean boolElement) {
			builder.value(toInternalString(boolElement));
//		} else if (element instanceof Quantity quantity) {
			// TODO: what kind of String representation would be acceptable here?
		} else {
			throw new IllegalArgumentException("Unexpected dependsOn element type '" + element.getClass().getSimpleName() + "'.");
		}
		
		return builder.build();
	}
	
	private com.b2international.snowowl.fhir.core.model.conceptmap.UnMapped toInternal(ConceptMap.Group.Unmapped unmapped) {
		if (unmapped == null) {
			return null;
		}
		
		var builder = com.b2international.snowowl.fhir.core.model.conceptmap.UnMapped.builder();
		
		ConceptMapGroupUnmappedMode.Value modeEnum = unmapped.getMode().getValueAsEnum();
		final com.b2international.snowowl.fhir.core.codesystems.ConceptMapGroupUnmappedMode mappedMode;
		
		switch (modeEnum) {
			case FIXED:
				mappedMode = com.b2international.snowowl.fhir.core.codesystems.ConceptMapGroupUnmappedMode.FIXED;
				break;
			case OTHER_MAP:
				mappedMode = com.b2international.snowowl.fhir.core.codesystems.ConceptMapGroupUnmappedMode.OTHER_MAP;
				break;
			case USE_SOURCE_CODE:
				mappedMode = com.b2international.snowowl.fhir.core.codesystems.ConceptMapGroupUnmappedMode.PROVIDED;
				break;
			default:
				throw new IllegalArgumentException("Unexpected unmapped mode '" + modeEnum + "'.");
		}
	
		builder.mode(mappedMode);
		builder.code(toInternal(unmapped.getCode()));
		builder.display(toInternal(unmapped.getDisplay()));
		builder.url(toInternal(unmapped.getOtherMap()));
		
		// "valueSet" is not converted (new in R5)
		// "relationship" on an Unmapped element is not converted (new in R5)
		
		return builder.build();
	}
}
