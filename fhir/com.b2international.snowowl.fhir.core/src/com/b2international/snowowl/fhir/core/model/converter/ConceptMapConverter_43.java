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

import org.linuxforhealth.fhir.model.r4b.resource.ConceptMap;
import org.linuxforhealth.fhir.model.r4b.type.Canonical;
import org.linuxforhealth.fhir.model.r4b.type.Code;
import org.linuxforhealth.fhir.model.r4b.type.ContactDetail;
import org.linuxforhealth.fhir.model.r4b.type.Uri;
import org.linuxforhealth.fhir.model.r4b.type.code.ConceptMapEquivalence;
import org.linuxforhealth.fhir.model.r4b.type.code.ConceptMapGroupUnmappedMode;
import org.linuxforhealth.fhir.model.r4b.type.code.PublicationStatus;

import com.b2international.commons.CompareUtils;

/**
 * @since 9.0
 */
public class ConceptMapConverter_43 extends AbstractConverter_43 implements ConceptMapConverter<ConceptMap> {

	public static final ConceptMapConverter<ConceptMap> INSTANCE = new ConceptMapConverter_43();
	
	private ConceptMapConverter_43() {
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
		
		// ConceptMap properties
		
		builder.url(fromInternal(conceptMap.getUrl()));
		
		var identifiers = conceptMap.getIdentifiers();
		if (!CompareUtils.isEmpty(identifiers)) {
			for (var identifier : identifiers) {
				if (identifier != null) {
					// XXX: In R4B only a single identifier is allowed for ConceptMaps!
					builder.identifier(fromInternal(identifier));
					break;
				}
			}
		}
		
		builder.version(conceptMap.getVersion());
		builder.name(conceptMap.getName());
		builder.title(conceptMap.getTitle());
		
		Code status = fromInternal(conceptMap.getStatus());
		if (status != null) {
			builder.status(PublicationStatus.of(status.getValue()));
		}
		
		builder.experimental(conceptMap.getExperimental());
		builder.date(fromInternal(conceptMap.getDate()));
		builder.publisher(conceptMap.getPublisher());
		
		var contacts = conceptMap.getContacts();
		if (!CompareUtils.isEmpty(contacts)) {
			for (var contact : contacts) {
				if (contact != null) {
					builder.contact(fromInternal(contact));
				}
			}
		}
		
		builder.description(fromInternalToMarkdown(conceptMap.getDescription()));
		// "useContext" is not converted
		// "jurisdiction" is not converted
		builder.purpose(fromInternalToMarkdown(conceptMap.getPurpose()));
		builder.copyright(fromInternalToMarkdown(conceptMap.getCopyright()));
		
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
			builder.source(fromInternal(conceptMap.getSourceUri()));
		}
		
		if (conceptMap.getSourceCanonical() != null) {
			builder.source(fromInternalToCanonical(conceptMap.getSourceCanonical()));
		}
		
		if (conceptMap.getTargetUri() != null) {
			builder.source(fromInternal(conceptMap.getTargetUri()));
		}
		
		if (conceptMap.getTargetCanonical() != null) {
			builder.source(fromInternalToCanonical(conceptMap.getTargetCanonical()));
		}
		
		var groups = conceptMap.getGroups();
		if (!CompareUtils.isEmpty(groups)) {
			for (var group : groups) {
				if (group != null) {
					builder.group(fromInternal(group));
				}
			}
		}
		
		return builder.build();
	}

	// Elements

	private ConceptMap.Group fromInternal(com.b2international.snowowl.fhir.core.model.conceptmap.Group group) {
		if (group == null) {
			return null;
		}
		
		ConceptMap.Group.Builder builder = ConceptMap.Group.builder();
		
		builder.source(fromInternal(group.getSource()));
		builder.sourceVersion(fromInternal(group.getSourceVersion()));
		builder.target(fromInternal(group.getTarget()));
		builder.targetVersion(fromInternal(group.getTargetVersion()));
		
		var elements = group.getElements();
		if (!CompareUtils.isEmpty(elements)) {
			for (var element : elements) {
				if (element != null) {
					builder.element(fromInternal(element));
				}
			}
		}

		builder.unmapped(fromInternal(group.getUnmapped()));
		
		return builder.build();
	}
	
	private ConceptMap.Group.Element fromInternal(com.b2international.snowowl.fhir.core.model.conceptmap.ConceptMapElement element) {
		if (element == null) {
			return null;
		}
		
		ConceptMap.Group.Element.Builder builder = ConceptMap.Group.Element.builder();
		
		builder.code(fromInternal(element.getCode()));
		builder.display(fromInternal(element.getDisplay()));
		
		var targets = element.getTargets();
		if (!CompareUtils.isEmpty(targets)) {
			for (var target : targets) {
				if (target != null) {
					builder.target(fromInternal(target));
				}
			}
		}
		
		return builder.build();
	}
	
	
	private ConceptMap.Group.Element.Target fromInternal(com.b2international.snowowl.fhir.core.model.conceptmap.Target target) {
		if (target == null) {
			return null;
		}
		
		ConceptMap.Group.Element.Target.Builder builder = ConceptMap.Group.Element.Target.builder();
		
		builder.code(fromInternal(target.getCode()));
		builder.display(target.getDisplay());
		
		Code equivalenceCode = fromInternal(target.getEquivalence());
		if (equivalenceCode != null) {
			builder.equivalence(ConceptMapEquivalence.of(equivalenceCode.getValue()));
		}
		
		builder.comment(fromInternal(target.getComment()));
		
		var dependsOnElements = target.getDependsOnElements();
		if (!CompareUtils.isEmpty(dependsOnElements)) {
			for (var dependsOn : dependsOnElements) {
				if (dependsOn != null) {
					builder.dependsOn(fromInternal(dependsOn));
				}
			}
		}

		var products = target.getProducts();
		if (!CompareUtils.isEmpty(products)) {
			for (var product : products) {
				if (product != null) {
					builder.product(fromInternal(product));
				}
			}
		}
		
		return builder.build();
	}

	private ConceptMap.Group.Element.Target.DependsOn fromInternal(com.b2international.snowowl.fhir.core.model.conceptmap.DependsOn dependsOn) {
		if (dependsOn == null) {
			return null;
		}

		ConceptMap.Group.Element.Target.DependsOn.Builder builder = ConceptMap.Group.Element.Target.DependsOn.builder();
		
		builder.property(fromInternal(dependsOn.getProperty()));
		builder.system(fromInternalToCanonical(dependsOn.getSystem()));
		builder.value(fromInternal(dependsOn.getValue()));
		builder.display(fromInternal(dependsOn.getDisplay()));
		
		return builder.build();
	}
	
	private ConceptMap.Group.Unmapped fromInternal(com.b2international.snowowl.fhir.core.model.conceptmap.UnMapped unmapped) {
		if (unmapped == null) {
			return null;
		}
		
		ConceptMap.Group.Unmapped.Builder builder = ConceptMap.Group.Unmapped.builder();
		
		Code modeCode = fromInternal(unmapped.getMode());
		if (modeCode != null) {
			builder.mode(ConceptMapGroupUnmappedMode.of(modeCode.getValue()));
		}

		builder.code(fromInternal(unmapped.getCode()));
		builder.display(fromInternal(unmapped.getDisplay()));
		builder.url(fromInternalToCanonical(unmapped.getUrl()));
		
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
		
		// ConceptMap properties
		
		builder.url(toInternal(conceptMap.getUrl()));
		// XXX: In R4B at most one identifier exists for a ConceptMap
		builder.addIdentifier(toInternal(conceptMap.getIdentifier()));
		builder.version(toInternal(conceptMap.getVersion()));
		builder.name(toInternal(conceptMap.getName()));
		builder.title(toInternal(conceptMap.getTitle()));
		
		var status = toInternal(conceptMap.getStatus());
		if (status != null) {
			builder.status(com.b2international.snowowl.fhir.core.codesystems.PublicationStatus.getByCodeValue(status.getCodeValue()));
		}
		
		builder.experimental(toInternal(conceptMap.getExperimental()));
		builder.date(toInternal(conceptMap.getDate()));
		builder.publisher(toInternal(conceptMap.getPublisher()));
		
		List<ContactDetail> contacts = conceptMap.getContact();
		for (ContactDetail contact : contacts) {
			builder.addContact(toInternal(contact));
		}
		
		builder.description(toInternal(conceptMap.getDescription()));
		// "useContext" is not converted
		// "jurisdiction" is not converted
		builder.purpose(toInternal(conceptMap.getPurpose()));
		builder.copyright(toInternal(conceptMap.getCopyright()));
		
		if (conceptMap.getSource() instanceof Canonical sourceCanonical) {
			builder.sourceCanonical(toInternal(sourceCanonical));
		} else if (conceptMap.getSource() instanceof Uri sourceUri) {
			builder.sourceUri(toInternal(sourceUri));
		}
		
		if (conceptMap.getTarget() instanceof Canonical sourceCanonical) {
			builder.targetCanonical(toInternal(sourceCanonical));
		} else if (conceptMap.getTarget() instanceof Uri sourceUri) {
			builder.targetUri(toInternal(sourceUri));
		}
		
		List<ConceptMap.Group> groups = conceptMap.getGroup();
		for (ConceptMap.Group group : groups) {
			builder.addGroup(toInternal(group));
		}
		
		return builder.build();
	}
	
	// Elements
	
	private com.b2international.snowowl.fhir.core.model.conceptmap.Group toInternal(ConceptMap.Group group) {
		if (group == null) {
			return null;
		}
		
		var builder = com.b2international.snowowl.fhir.core.model.conceptmap.Group.builder();
		
		builder.source(toInternal(group.getSource()));
		builder.sourceVersion(toInternal(group.getSourceVersion()));
		builder.target(toInternal(group.getTarget()));
		builder.targetVersion(toInternal(group.getTargetVersion()));

		List<ConceptMap.Group.Element> elements = group.getElement();
		for (ConceptMap.Group.Element element : elements) {
			builder.addElement(toInternal(element));
		}
		
		builder.unmapped(toInternal(group.getUnmapped()));
		
		return builder.build();
	}
	
	private com.b2international.snowowl.fhir.core.model.conceptmap.ConceptMapElement toInternal(ConceptMap.Group.Element element) {
		if (element == null) {
			return null;
		}
		
		var builder = com.b2international.snowowl.fhir.core.model.conceptmap.ConceptMapElement.builder();
		
		builder.code(toInternal(element.getCode()));
		builder.display(toInternal(element.getDisplay()));
		
		List<ConceptMap.Group.Element.Target> targets = element.getTarget();
		for (ConceptMap.Group.Element.Target target : targets) {
			builder.addTarget(toInternal(target));
		}
		
		return builder.build();
	}
	
	
	private com.b2international.snowowl.fhir.core.model.conceptmap.Target toInternal(ConceptMap.Group.Element.Target target) {
		if (target == null) {
			return null;
		}
		
		var builder = com.b2international.snowowl.fhir.core.model.conceptmap.Target.builder();
		
		builder.code(toInternal(target.getCode()));
		builder.display(toInternal(target.getDisplay()));
		builder.equivalence(target.getEquivalence().getValue());
		builder.comment(toInternal(target.getComment()));
		
		List<ConceptMap.Group.Element.Target.DependsOn> dependsOnElements = target.getDependsOn();
		for (ConceptMap.Group.Element.Target.DependsOn dependsOn : dependsOnElements) {
			builder.addDependsOn(toInternal(dependsOn));
		}
		
		List<ConceptMap.Group.Element.Target.DependsOn> products = target.getProduct();
		for (ConceptMap.Group.Element.Target.DependsOn product : products) {
			builder.addProduct(toInternal(product));
		}
		
		return builder.build();
	}
	
	private com.b2international.snowowl.fhir.core.model.conceptmap.DependsOn toInternal(ConceptMap.Group.Element.Target.DependsOn dependsOn) {
		if (dependsOn == null) {
			return null;
		}
		
		var builder = com.b2international.snowowl.fhir.core.model.conceptmap.DependsOn.builder();

		builder.property(toInternal(dependsOn.getProperty()));
		builder.system(toInternal(dependsOn.getSystem()));
		builder.value(toInternal(dependsOn.getValue()));
		builder.display(toInternal(dependsOn.getDisplay()));
		
		return builder.build();
	}
	
	private com.b2international.snowowl.fhir.core.model.conceptmap.UnMapped toInternal(ConceptMap.Group.Unmapped unmapped) {
		if (unmapped == null) {
			return null;
		}
		
		var builder = com.b2international.snowowl.fhir.core.model.conceptmap.UnMapped.builder();
	
		builder.mode(unmapped.getMode().getValue());
		builder.code(toInternal(unmapped.getCode()));
		builder.display(toInternal(unmapped.getDisplay()));
		builder.url(toInternal(unmapped.getUrl()));
		
		return builder.build();
	}
}
