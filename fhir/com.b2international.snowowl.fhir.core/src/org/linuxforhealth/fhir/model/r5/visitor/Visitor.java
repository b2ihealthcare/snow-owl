/*
 * (C) Copyright IBM Corp. 2019, 2022
 *
 * SPDX-License-Identifier: Apache-2.0
 */

package org.linuxforhealth.fhir.model.r5.visitor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.Year;
import java.time.YearMonth;
import java.time.ZonedDateTime;
import javax.annotation.Generated;

import org.linuxforhealth.fhir.model.r5.resource.*;
import org.linuxforhealth.fhir.model.r5.type.*;
import org.linuxforhealth.fhir.model.r5.type.Boolean;
import org.linuxforhealth.fhir.model.r5.type.Integer;
import org.linuxforhealth.fhir.model.r5.type.String;

/**
 * Visitor interface for visiting FHIR model objects that implement Visitable.
 * 
 * Each model object can accept a visitor and contains logic for invoking the corresponding visit method for itself and all its members.
 * 
 * At each level, the visitor can control traversal by returning true or false as indicated in the following snippet:
 * <pre>
 * if (visitor.preVisit(this)) {
 *     visitor.visitStart(elementName, elementIndex, this);
 *     if (visitor.visit(elementName, elementIndex, this)) {
 *         // visit children
 *     }
 *     visitor.visitEnd(elementName, elementIndex, this);
 *     visitor.postVisit(this);
 * }
 * </pre>
 */
@Generated("org.linuxforhealth.fhir.tools.CodeGenerator")
public interface Visitor {
    /**
     * @return true if this Element should be visited; otherwise false
     */
    boolean preVisit(Element element);

    /**
     * @return true if this Resource should be visited; otherwise false
     */
    boolean preVisit(Resource resource);

    void postVisit(Element element);
    void postVisit(Resource resource);

    void visitStart(java.lang.String elementName, int elementIndex, Element element);
    void visitStart(java.lang.String elementName, int elementIndex, Resource resource);
    void visitStart(java.lang.String elementName, java.util.List<? extends Visitable> visitables, Class<?> type);

    void visitEnd(java.lang.String elementName, int elementIndex, Element element);
    void visitEnd(java.lang.String elementName, int elementIndex, Resource resource);
    void visitEnd(java.lang.String elementName, java.util.List<? extends Visitable> visitables, Class<?> type);

    /**
     * @return
     *     true if the children of this visitable should be visited; otherwise false
     */
    boolean visit(java.lang.String elementName, int elementIndex, Visitable visitable);

    /**
     * @return
     *     true if the children of this address should be visited; otherwise false
     */
    boolean visit(java.lang.String elementName, int elementIndex, Address address);

    /**
     * @return
     *     true if the children of this age should be visited; otherwise false
     */
    boolean visit(java.lang.String elementName, int elementIndex, Age age);

    /**
     * @return
     *     true if the children of this annotation should be visited; otherwise false
     */
    boolean visit(java.lang.String elementName, int elementIndex, Annotation annotation);

    /**
     * @return
     *     true if the children of this attachment should be visited; otherwise false
     */
    boolean visit(java.lang.String elementName, int elementIndex, Attachment attachment);

    /**
     * @return
     *     true if the children of this availability should be visited; otherwise false
     */
    boolean visit(java.lang.String elementName, int elementIndex, Availability availability);

    /**
     * @return
     *     true if the children of this backboneElement should be visited; otherwise false
     */
    boolean visit(java.lang.String elementName, int elementIndex, BackboneElement backboneElement);

    /**
     * @return
     *     true if the children of this backboneType should be visited; otherwise false
     */
    boolean visit(java.lang.String elementName, int elementIndex, BackboneType backboneType);

    /**
     * @return
     *     true if the children of this base64Binary should be visited; otherwise false
     */
    boolean visit(java.lang.String elementName, int elementIndex, Base64Binary base64Binary);

    /**
     * @return
     *     true if the children of this _boolean should be visited; otherwise false
     */
    boolean visit(java.lang.String elementName, int elementIndex, Boolean _boolean);

    /**
     * @return
     *     true if the children of this bundle should be visited; otherwise false
     */
    boolean visit(java.lang.String elementName, int elementIndex, Bundle bundle);

    /**
     * @return
     *     true if the children of this canonical should be visited; otherwise false
     */
    boolean visit(java.lang.String elementName, int elementIndex, Canonical canonical);

    /**
     * @return
     *     true if the children of this canonicalResource should be visited; otherwise false
     */
    boolean visit(java.lang.String elementName, int elementIndex, CanonicalResource canonicalResource);

    /**
     * @return
     *     true if the children of this capabilityStatement should be visited; otherwise false
     */
    boolean visit(java.lang.String elementName, int elementIndex, CapabilityStatement capabilityStatement);

    /**
     * @return
     *     true if the children of this code should be visited; otherwise false
     */
    boolean visit(java.lang.String elementName, int elementIndex, Code code);

    /**
     * @return
     *     true if the children of this codeSystem should be visited; otherwise false
     */
    boolean visit(java.lang.String elementName, int elementIndex, CodeSystem codeSystem);

    /**
     * @return
     *     true if the children of this codeableConcept should be visited; otherwise false
     */
    boolean visit(java.lang.String elementName, int elementIndex, CodeableConcept codeableConcept);

    /**
     * @return
     *     true if the children of this codeableReference should be visited; otherwise false
     */
    boolean visit(java.lang.String elementName, int elementIndex, CodeableReference codeableReference);

    /**
     * @return
     *     true if the children of this coding should be visited; otherwise false
     */
    boolean visit(java.lang.String elementName, int elementIndex, Coding coding);

    /**
     * @return
     *     true if the children of this conceptMap should be visited; otherwise false
     */
    boolean visit(java.lang.String elementName, int elementIndex, ConceptMap conceptMap);

    /**
     * @return
     *     true if the children of this contactDetail should be visited; otherwise false
     */
    boolean visit(java.lang.String elementName, int elementIndex, ContactDetail contactDetail);

    /**
     * @return
     *     true if the children of this contactPoint should be visited; otherwise false
     */
    boolean visit(java.lang.String elementName, int elementIndex, ContactPoint contactPoint);

    /**
     * @return
     *     true if the children of this contributor should be visited; otherwise false
     */
    boolean visit(java.lang.String elementName, int elementIndex, Contributor contributor);

    /**
     * @return
     *     true if the children of this count should be visited; otherwise false
     */
    boolean visit(java.lang.String elementName, int elementIndex, Count count);

    /**
     * @return
     *     true if the children of this dataRequirement should be visited; otherwise false
     */
    boolean visit(java.lang.String elementName, int elementIndex, DataRequirement dataRequirement);

    /**
     * @return
     *     true if the children of this dataType should be visited; otherwise false
     */
    boolean visit(java.lang.String elementName, int elementIndex, DataType dataType);

    /**
     * @return
     *     true if the children of this date should be visited; otherwise false
     */
    boolean visit(java.lang.String elementName, int elementIndex, Date date);

    /**
     * @return
     *     true if the children of this dateTime should be visited; otherwise false
     */
    boolean visit(java.lang.String elementName, int elementIndex, DateTime dateTime);

    /**
     * @return
     *     true if the children of this decimal should be visited; otherwise false
     */
    boolean visit(java.lang.String elementName, int elementIndex, Decimal decimal);

    /**
     * @return
     *     true if the children of this distance should be visited; otherwise false
     */
    boolean visit(java.lang.String elementName, int elementIndex, Distance distance);

    /**
     * @return
     *     true if the children of this domainResource should be visited; otherwise false
     */
    boolean visit(java.lang.String elementName, int elementIndex, DomainResource domainResource);

    /**
     * @return
     *     true if the children of this dosage should be visited; otherwise false
     */
    boolean visit(java.lang.String elementName, int elementIndex, Dosage dosage);

    /**
     * @return
     *     true if the children of this duration should be visited; otherwise false
     */
    boolean visit(java.lang.String elementName, int elementIndex, Duration duration);

    /**
     * @return
     *     true if the children of this element should be visited; otherwise false
     */
    boolean visit(java.lang.String elementName, int elementIndex, Element element);

    /**
     * @return
     *     true if the children of this elementDefinition should be visited; otherwise false
     */
    boolean visit(java.lang.String elementName, int elementIndex, ElementDefinition elementDefinition);

    /**
     * @return
     *     true if the children of this expression should be visited; otherwise false
     */
    boolean visit(java.lang.String elementName, int elementIndex, Expression expression);

    /**
     * @return
     *     true if the children of this extendedContactDetail should be visited; otherwise false
     */
    boolean visit(java.lang.String elementName, int elementIndex, ExtendedContactDetail extendedContactDetail);

    /**
     * @return
     *     true if the children of this extension should be visited; otherwise false
     */
    boolean visit(java.lang.String elementName, int elementIndex, Extension extension);

    /**
     * @return
     *     true if the children of this humanName should be visited; otherwise false
     */
    boolean visit(java.lang.String elementName, int elementIndex, HumanName humanName);

    /**
     * @return
     *     true if the children of this id should be visited; otherwise false
     */
    boolean visit(java.lang.String elementName, int elementIndex, Id id);

    /**
     * @return
     *     true if the children of this identifier should be visited; otherwise false
     */
    boolean visit(java.lang.String elementName, int elementIndex, Identifier identifier);

    /**
     * @return
     *     true if the children of this instant should be visited; otherwise false
     */
    boolean visit(java.lang.String elementName, int elementIndex, Instant instant);

    /**
     * @return
     *     true if the children of this integer should be visited; otherwise false
     */
    boolean visit(java.lang.String elementName, int elementIndex, Integer integer);

    /**
     * @return
     *     true if the children of this integer64 should be visited; otherwise false
     */
    boolean visit(java.lang.String elementName, int elementIndex, Integer64 integer64);

    /**
     * @return
     *     true if the children of this markdown should be visited; otherwise false
     */
    boolean visit(java.lang.String elementName, int elementIndex, Markdown markdown);

    /**
     * @return
     *     true if the children of this marketingStatus should be visited; otherwise false
     */
    boolean visit(java.lang.String elementName, int elementIndex, MarketingStatus marketingStatus);

    /**
     * @return
     *     true if the children of this meta should be visited; otherwise false
     */
    boolean visit(java.lang.String elementName, int elementIndex, Meta meta);

    /**
     * @return
     *     true if the children of this metadataResource should be visited; otherwise false
     */
    boolean visit(java.lang.String elementName, int elementIndex, MetadataResource metadataResource);

    /**
     * @return
     *     true if the children of this monetaryComponent should be visited; otherwise false
     */
    boolean visit(java.lang.String elementName, int elementIndex, MonetaryComponent monetaryComponent);

    /**
     * @return
     *     true if the children of this money should be visited; otherwise false
     */
    boolean visit(java.lang.String elementName, int elementIndex, Money money);

    /**
     * @return
     *     true if the children of this moneyQuantity should be visited; otherwise false
     */
    boolean visit(java.lang.String elementName, int elementIndex, MoneyQuantity moneyQuantity);

    /**
     * @return
     *     true if the children of this narrative should be visited; otherwise false
     */
    boolean visit(java.lang.String elementName, int elementIndex, Narrative narrative);

    /**
     * @return
     *     true if the children of this oid should be visited; otherwise false
     */
    boolean visit(java.lang.String elementName, int elementIndex, Oid oid);

    /**
     * @return
     *     true if the children of this operationDefinition should be visited; otherwise false
     */
    boolean visit(java.lang.String elementName, int elementIndex, OperationDefinition operationDefinition);

    /**
     * @return
     *     true if the children of this operationOutcome should be visited; otherwise false
     */
    boolean visit(java.lang.String elementName, int elementIndex, OperationOutcome operationOutcome);

    /**
     * @return
     *     true if the children of this parameterDefinition should be visited; otherwise false
     */
    boolean visit(java.lang.String elementName, int elementIndex, ParameterDefinition parameterDefinition);

    /**
     * @return
     *     true if the children of this parameters should be visited; otherwise false
     */
    boolean visit(java.lang.String elementName, int elementIndex, Parameters parameters);

    /**
     * @return
     *     true if the children of this period should be visited; otherwise false
     */
    boolean visit(java.lang.String elementName, int elementIndex, Period period);

    /**
     * @return
     *     true if the children of this positiveInt should be visited; otherwise false
     */
    boolean visit(java.lang.String elementName, int elementIndex, PositiveInt positiveInt);

    /**
     * @return
     *     true if the children of this primitiveType should be visited; otherwise false
     */
    boolean visit(java.lang.String elementName, int elementIndex, PrimitiveType primitiveType);

    /**
     * @return
     *     true if the children of this productShelfLife should be visited; otherwise false
     */
    boolean visit(java.lang.String elementName, int elementIndex, ProductShelfLife productShelfLife);

    /**
     * @return
     *     true if the children of this quantity should be visited; otherwise false
     */
    boolean visit(java.lang.String elementName, int elementIndex, Quantity quantity);

    /**
     * @return
     *     true if the children of this range should be visited; otherwise false
     */
    boolean visit(java.lang.String elementName, int elementIndex, Range range);

    /**
     * @return
     *     true if the children of this ratio should be visited; otherwise false
     */
    boolean visit(java.lang.String elementName, int elementIndex, Ratio ratio);

    /**
     * @return
     *     true if the children of this ratioRange should be visited; otherwise false
     */
    boolean visit(java.lang.String elementName, int elementIndex, RatioRange ratioRange);

    /**
     * @return
     *     true if the children of this reference should be visited; otherwise false
     */
    boolean visit(java.lang.String elementName, int elementIndex, Reference reference);

    /**
     * @return
     *     true if the children of this relatedArtifact should be visited; otherwise false
     */
    boolean visit(java.lang.String elementName, int elementIndex, RelatedArtifact relatedArtifact);

    /**
     * @return
     *     true if the children of this resource should be visited; otherwise false
     */
    boolean visit(java.lang.String elementName, int elementIndex, Resource resource);

    /**
     * @return
     *     true if the children of this sampledData should be visited; otherwise false
     */
    boolean visit(java.lang.String elementName, int elementIndex, SampledData sampledData);

    /**
     * @return
     *     true if the children of this signature should be visited; otherwise false
     */
    boolean visit(java.lang.String elementName, int elementIndex, Signature signature);

    /**
     * @return
     *     true if the children of this simpleQuantity should be visited; otherwise false
     */
    boolean visit(java.lang.String elementName, int elementIndex, SimpleQuantity simpleQuantity);

    /**
     * @return
     *     true if the children of this string should be visited; otherwise false
     */
    boolean visit(java.lang.String elementName, int elementIndex, String string);

    /**
     * @return
     *     true if the children of this structureDefinition should be visited; otherwise false
     */
    boolean visit(java.lang.String elementName, int elementIndex, StructureDefinition structureDefinition);

    /**
     * @return
     *     true if the children of this terminologyCapabilities should be visited; otherwise false
     */
    boolean visit(java.lang.String elementName, int elementIndex, TerminologyCapabilities terminologyCapabilities);

    /**
     * @return
     *     true if the children of this time should be visited; otherwise false
     */
    boolean visit(java.lang.String elementName, int elementIndex, Time time);

    /**
     * @return
     *     true if the children of this timing should be visited; otherwise false
     */
    boolean visit(java.lang.String elementName, int elementIndex, Timing timing);

    /**
     * @return
     *     true if the children of this triggerDefinition should be visited; otherwise false
     */
    boolean visit(java.lang.String elementName, int elementIndex, TriggerDefinition triggerDefinition);

    /**
     * @return
     *     true if the children of this unsignedInt should be visited; otherwise false
     */
    boolean visit(java.lang.String elementName, int elementIndex, UnsignedInt unsignedInt);

    /**
     * @return
     *     true if the children of this uri should be visited; otherwise false
     */
    boolean visit(java.lang.String elementName, int elementIndex, Uri uri);

    /**
     * @return
     *     true if the children of this url should be visited; otherwise false
     */
    boolean visit(java.lang.String elementName, int elementIndex, Url url);

    /**
     * @return
     *     true if the children of this usageContext should be visited; otherwise false
     */
    boolean visit(java.lang.String elementName, int elementIndex, UsageContext usageContext);

    /**
     * @return
     *     true if the children of this uuid should be visited; otherwise false
     */
    boolean visit(java.lang.String elementName, int elementIndex, Uuid uuid);

    /**
     * @return
     *     true if the children of this valueSet should be visited; otherwise false
     */
    boolean visit(java.lang.String elementName, int elementIndex, ValueSet valueSet);

    /**
     * @return
     *     true if the children of this virtualServiceDetail should be visited; otherwise false
     */
    boolean visit(java.lang.String elementName, int elementIndex, VirtualServiceDetail virtualServiceDetail);

    /**
     * @return
     *     true if the children of this xhtml should be visited; otherwise false
     */
    boolean visit(java.lang.String elementName, int elementIndex, Xhtml xhtml);

    void visit(java.lang.String elementName, byte[] value);
    void visit(java.lang.String elementName, BigDecimal value);
    void visit(java.lang.String elementName, java.lang.Boolean value);
    void visit(java.lang.String elementName, java.lang.Integer value);
    void visit(java.lang.String elementName, LocalDate value);
    void visit(java.lang.String elementName, LocalTime value);
    void visit(java.lang.String elementName, java.lang.String value);
    void visit(java.lang.String elementName, Year value);
    void visit(java.lang.String elementName, YearMonth value);
    void visit(java.lang.String elementName, ZonedDateTime value);
}
