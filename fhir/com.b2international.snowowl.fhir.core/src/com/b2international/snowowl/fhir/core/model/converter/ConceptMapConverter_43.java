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
import org.linuxforhealth.fhir.model.r4b.resource.Parameters;
import org.linuxforhealth.fhir.model.r4b.type.*;
import org.linuxforhealth.fhir.model.r4b.type.Boolean;
import org.linuxforhealth.fhir.model.r4b.type.String;
import org.linuxforhealth.fhir.model.r4b.type.code.ConceptMapEquivalence;
import org.linuxforhealth.fhir.model.r4b.type.code.ConceptMapGroupUnmappedMode;
import org.linuxforhealth.fhir.model.r4b.type.code.PublicationStatus;

import com.b2international.commons.CompareUtils;
import com.b2international.commons.StringUtils;
import com.b2international.snowowl.fhir.core.exceptions.BadRequestException;
import com.b2international.snowowl.fhir.core.model.conceptmap.Dependency;
import com.b2international.snowowl.fhir.core.model.conceptmap.TranslateRequest;
import com.b2international.snowowl.fhir.core.model.conceptmap.TranslateResult;

/**
 * @since 9.0
 */
public class ConceptMapConverter_43 extends AbstractConverter_43 implements ConceptMapConverter<ConceptMap, Parameters> {

	public static final ConceptMapConverter<ConceptMap, Parameters> INSTANCE = new ConceptMapConverter_43();
	
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
	
	@Override
	public Parameters fromTranslateResult(TranslateResult translateResult) {
		if (translateResult == null) {
			return null;
		}
		
		Parameters.Builder builder = Parameters.builder();
		
		addParameter(builder, "result", fromInternal(translateResult.getResult()));
		addParameter(builder, "message", fromInternal(translateResult.getMessage()));
		
		var matches = translateResult.getMatches();
		if (!CompareUtils.isEmpty(matches)) {
			for (var match : matches) {
				addMatchPart(builder, match);
			}
		}
		
		return builder.build();
	}
	
	private void addMatchPart(
		Parameters.Builder builder, 
		com.b2international.snowowl.fhir.core.model.conceptmap.Match match
	) {
		if (match == null) {
			return;
		}
		
		Parameters.Parameter.Builder matchBuilder = Parameters.Parameter.builder();
		matchBuilder.name("match");
		
		addPart(matchBuilder, "equivalence", fromInternal(match.getEquivalence()));
		addPart(matchBuilder, "concept", fromInternal(match.getConcept()));
		
		var products = match.getProduct();
		if (!CompareUtils.isEmpty(products)) {
			for (var product : products) {
				addProductPart(matchBuilder, product);
			}
		}
		
		addPart(matchBuilder, "source", fromInternal(match.getSource()));
		
		builder.parameter(matchBuilder.build());
	}

	private void addProductPart(
		Parameters.Parameter.Builder matchBuilder, 
		com.b2international.snowowl.fhir.core.model.conceptmap.Product product
	) {
		if (product == null) {
			return;
		}

		Parameters.Parameter.Builder productBuilder = Parameters.Parameter.builder();
		productBuilder.name("product");
		
		addPart(productBuilder, "element", fromInternal(product.getElement()));
		addPart(productBuilder, "concept", fromInternal(product.getConcept()));
		
		matchBuilder.part(productBuilder.build());
	}

	@Override
	public TranslateRequest toTranslateRequest(Parameters parameters) {
		if (parameters == null) {
			return null;
		}
		
		var builder = TranslateRequest.builder();
		
		List<Parameters.Parameter> parameterElements = parameters.getParameter();
		for (Parameters.Parameter parameter : parameterElements) {
			java.lang.String parameterName = toInternal(parameter.getName());
			
			switch (parameterName) {
				case "url":
					var url = toInternal(parameter.getValue().as(Uri.class));
					if (url != null) {
						builder.url(url.getUriValue());
					}
					break;

				case "conceptMap":
					throw new BadRequestException("Inline input parameter 'conceptMap' is not supported.");
					
				case "conceptMapVersion":
					var conceptMapVersion = toInternal(parameter.getValue().as(String.class));
					if (!StringUtils.isEmpty(conceptMapVersion)) {
						builder.conceptMapVersion(conceptMapVersion);
					}
					break;

				case "code":
					var code = toInternal(parameter.getValue().as(Code.class));
					if (code != null) {
						builder.code(code.getCodeValue());
					}
					break;
	
				case "system":
					var system = toInternal(parameter.getValue().as(Uri.class));
					if (system != null) {
						builder.system(system.getUriValue());
					}
					break;
	
				case "version":
					var version = toInternal(parameter.getValue().as(String.class));
					if (!StringUtils.isEmpty(version)) {
						builder.version(version);
					}
					break;
	
				case "source":
					var source = toInternal(parameter.getValue().as(Uri.class));
					if (source != null) {
						builder.source(source.getUriValue());
					}
					break;

				case "coding":
					var coding = toInternal(parameter.getValue().as(Coding.class));
					if (coding != null) {
						builder.coding(coding);
					}
					break;
					
				case "codeableConcept":
					var codeableConcept = toInternal(parameter.getValue().as(CodeableConcept.class));
					if (codeableConcept != null) {
						builder.codeableConcept(codeableConcept);
					}
					break;
					
				case "target":
					var target = toInternal(parameter.getValue().as(Code.class));
					if (target != null) {
						builder.target(target.getCodeValue());
					}
					break;

				case "targetSystem":
					var targetSystem = toInternal(parameter.getValue().as(Uri.class));
					if (targetSystem != null) {
						builder.targetSystem(targetSystem.getUriValue());
					}
					break;

				case "dependency":
					addDependency(builder, parameter);
					break;
					
				case "reverse":
					var isReverse = toInternal(parameter.getValue().as(Boolean.class));
					if (isReverse != null) {
						builder.isReverse(isReverse);
					}
					break;
	
				default:
					throw new IllegalStateException("Unexpected in parameter '" + parameterName + "'.");
			}
		}
		
		return builder.build();
	}

	private void addDependency(TranslateRequest.Builder builder, Parameters.Parameter parameter) {
		if (parameter == null) {
			return;
		}
		
		Dependency.Builder dependencyBuilder = Dependency.builder();
		
		List<Parameters.Parameter> parts = parameter.getPart();
		for (Parameters.Parameter part : parts) {
			java.lang.String partName = toInternal(part.getName());

			switch (partName) {
				case "element":
					var element = toInternal(parameter.getValue().as(Uri.class));
					if (element != null) {
						dependencyBuilder.element(element);
					}
					break;

				case "concept":
					var concept = toInternal(parameter.getValue().as(CodeableConcept.class));
					if (concept != null) {
						dependencyBuilder.concept(concept);
					}
					break;

				default:
					throw new IllegalStateException("Unexpected dependency part name '" + partName + "'.");
			}
		}
		
		builder.addDependency(dependencyBuilder.build());
	}	
}
