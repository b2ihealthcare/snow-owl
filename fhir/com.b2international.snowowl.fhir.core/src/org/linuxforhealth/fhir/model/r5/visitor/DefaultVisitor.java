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
 * DefaultVisitor provides a default implementation of the Visitor interface which uses the
 * value of the passed {@code visitChildren} boolean to control whether or not to
 * visit the children of the Resource or Element being visited.
 * 
 * Subclasses can override the default behavior in a number of places, including:
 * <ul>
 * <li>preVisit methods to control whether a given Resource or Element gets visited
 * <li>visitStart methods to provide setup behavior prior to the visit
 * <li>supertype visit methods to perform some common action on all visited Resources and Elements
 * <li>subtype visit methods to perform unique behavior that varies by the type being visited
 * <li>visitEnd methods to provide initial cleanup behavior after a Resource or Element has been visited
 * <li>postVisit methods to provide final cleanup behavior after a Resource or Element has been visited
 * </ul>
 */
@Generated("org.linuxforhealth.fhir.tools.CodeGenerator")
public class DefaultVisitor implements Visitor {
    protected boolean visitChildren;

    /**
     * Subclasses can override this method to provide a default action for all visit methods.
     * @return
     *     whether to visit the children of this resource; returns the value of the {@code visitChildren} boolean by default
     */
    public boolean visit(java.lang.String elementName, int elementIndex, Visitable visitable) {
        return visitChildren;
    }

    /**
     * @param visitChildren
     *     Whether to visit children of a Resource or Element by default. Note that subclasses may override the visit methods 
     *     and/or the defaultAction methods and decide whether to use the passed boolean or not.
     */
    public DefaultVisitor(boolean visitChildren) {
        this.visitChildren = visitChildren;
    }

    @Override
    public boolean preVisit(Element element) {
        return true;
    }

    @Override
    public boolean preVisit(Resource resource) {
        return true;
    }

    @Override
    public void postVisit(Element element) {
    }

    @Override
    public void postVisit(Resource resource) {
    }

    @Override
    public void visitStart(java.lang.String elementName, int elementIndex, Element element) {
    }

    @Override
    public void visitStart(java.lang.String elementName, int elementIndex, Resource resource) {
    }

    @Override
    public void visitStart(java.lang.String elementName, java.util.List<? extends Visitable> visitables, Class<?> type) {
    }

    @Override
    public void visitEnd(java.lang.String elementName, int elementIndex, Element element) {
    }

    @Override
    public void visitEnd(java.lang.String elementName, int elementIndex, Resource resource) {
    }

    @Override
    public void visitEnd(java.lang.String elementName, java.util.List<? extends Visitable> visitables, Class<?> type) {
    }

    /**
     * Delegates to {@link #visit(elementName, elementIndex, DataType)}
     * @return
     *     {@inheritDoc}
     */
    @Override
    public boolean visit(java.lang.String elementName, int elementIndex, Attachment attachment) {
        return visit(elementName, elementIndex, (DataType) attachment);
    }

    /**
     * Delegates to {@link #visit(elementName, elementIndex, Element)}
     * @return
     *     {@inheritDoc}
     */
    @Override
    public boolean visit(java.lang.String elementName, int elementIndex, BackboneElement backboneElement) {
        return visit(elementName, elementIndex, (Element) backboneElement);
    }

    /**
     * Delegates to {@link #visit(elementName, elementIndex, DataType)}
     * @return
     *     {@inheritDoc}
     */
    @Override
    public boolean visit(java.lang.String elementName, int elementIndex, BackboneType backboneType) {
        return visit(elementName, elementIndex, (DataType) backboneType);
    }

    /**
     * Delegates to {@link #visit(elementName, elementIndex, PrimitiveType)}
     * @return
     *     {@inheritDoc}
     */
    @Override
    public boolean visit(java.lang.String elementName, int elementIndex, Base64Binary base64Binary) {
        return visit(elementName, elementIndex, (PrimitiveType) base64Binary);
    }

    /**
     * Delegates to {@link #visit(elementName, elementIndex, PrimitiveType)}
     * @return
     *     {@inheritDoc}
     */
    @Override
    public boolean visit(java.lang.String elementName, int elementIndex, Boolean _boolean) {
        return visit(elementName, elementIndex, (PrimitiveType) _boolean);
    }

    /**
     * Delegates to {@link #visit(elementName, elementIndex, Resource)}
     * @return
     *     {@inheritDoc}
     */
    @Override
    public boolean visit(java.lang.String elementName, int elementIndex, Bundle bundle) {
        return visit(elementName, elementIndex, (Resource) bundle);
    }

    /**
     * Delegates to {@link #visit(elementName, elementIndex, Uri)}
     * @return
     *     {@inheritDoc}
     */
    @Override
    public boolean visit(java.lang.String elementName, int elementIndex, Canonical canonical) {
        return visit(elementName, elementIndex, (Uri) canonical);
    }

    /**
     * Delegates to {@link #visit(elementName, elementIndex, DomainResource)}
     * @return
     *     {@inheritDoc}
     */
    @Override
    public boolean visit(java.lang.String elementName, int elementIndex, CanonicalResource canonicalResource) {
        return visit(elementName, elementIndex, (DomainResource) canonicalResource);
    }

    /**
     * Delegates to {@link #visit(elementName, elementIndex, DomainResource)}
     * @return
     *     {@inheritDoc}
     */
    @Override
    public boolean visit(java.lang.String elementName, int elementIndex, CapabilityStatement capabilityStatement) {
        return visit(elementName, elementIndex, (DomainResource) capabilityStatement);
    }

    /**
     * Delegates to {@link #visit(elementName, elementIndex, String)}
     * @return
     *     {@inheritDoc}
     */
    @Override
    public boolean visit(java.lang.String elementName, int elementIndex, Code code) {
        return visit(elementName, elementIndex, (String) code);
    }

    /**
     * Delegates to {@link #visit(elementName, elementIndex, DomainResource)}
     * @return
     *     {@inheritDoc}
     */
    @Override
    public boolean visit(java.lang.String elementName, int elementIndex, CodeSystem codeSystem) {
        return visit(elementName, elementIndex, (DomainResource) codeSystem);
    }

    /**
     * Delegates to {@link #visit(elementName, elementIndex, DataType)}
     * @return
     *     {@inheritDoc}
     */
    @Override
    public boolean visit(java.lang.String elementName, int elementIndex, CodeableConcept codeableConcept) {
        return visit(elementName, elementIndex, (DataType) codeableConcept);
    }

    /**
     * Delegates to {@link #visit(elementName, elementIndex, DataType)}
     * @return
     *     {@inheritDoc}
     */
    @Override
    public boolean visit(java.lang.String elementName, int elementIndex, CodeableReference codeableReference) {
        return visit(elementName, elementIndex, (DataType) codeableReference);
    }

    /**
     * Delegates to {@link #visit(elementName, elementIndex, DataType)}
     * @return
     *     {@inheritDoc}
     */
    @Override
    public boolean visit(java.lang.String elementName, int elementIndex, Coding coding) {
        return visit(elementName, elementIndex, (DataType) coding);
    }

    /**
     * Delegates to {@link #visit(elementName, elementIndex, DomainResource)}
     * @return
     *     {@inheritDoc}
     */
    @Override
    public boolean visit(java.lang.String elementName, int elementIndex, ConceptMap conceptMap) {
        return visit(elementName, elementIndex, (DomainResource) conceptMap);
    }

    /**
     * Delegates to {@link #visit(elementName, elementIndex, DataType)}
     * @return
     *     {@inheritDoc}
     */
    @Override
    public boolean visit(java.lang.String elementName, int elementIndex, ContactDetail contactDetail) {
        return visit(elementName, elementIndex, (DataType) contactDetail);
    }

    /**
     * Delegates to {@link #visit(elementName, elementIndex, DataType)}
     * @return
     *     {@inheritDoc}
     */
    @Override
    public boolean visit(java.lang.String elementName, int elementIndex, ContactPoint contactPoint) {
        return visit(elementName, elementIndex, (DataType) contactPoint);
    }

    /**
     * Delegates to {@link #visit(elementName, elementIndex, Element)}
     * @return
     *     {@inheritDoc}
     */
    @Override
    public boolean visit(java.lang.String elementName, int elementIndex, DataType dataType) {
        return visit(elementName, elementIndex, (Element) dataType);
    }

    /**
     * Delegates to {@link #visit(elementName, elementIndex, PrimitiveType)}
     * @return
     *     {@inheritDoc}
     */
    @Override
    public boolean visit(java.lang.String elementName, int elementIndex, Date date) {
        return visit(elementName, elementIndex, (PrimitiveType) date);
    }

    /**
     * Delegates to {@link #visit(elementName, elementIndex, PrimitiveType)}
     * @return
     *     {@inheritDoc}
     */
    @Override
    public boolean visit(java.lang.String elementName, int elementIndex, DateTime dateTime) {
        return visit(elementName, elementIndex, (PrimitiveType) dateTime);
    }

    /**
     * Delegates to {@link #visit(elementName, elementIndex, PrimitiveType)}
     * @return
     *     {@inheritDoc}
     */
    @Override
    public boolean visit(java.lang.String elementName, int elementIndex, Decimal decimal) {
        return visit(elementName, elementIndex, (PrimitiveType) decimal);
    }

    /**
     * Delegates to {@link #visit(elementName, elementIndex, Resource)}
     * @return
     *     {@inheritDoc}
     */
    @Override
    public boolean visit(java.lang.String elementName, int elementIndex, DomainResource domainResource) {
        return visit(elementName, elementIndex, (Resource) domainResource);
    }

    /**
     * Delegates to {@link #visit(elementName, elementIndex, Visitable)}
     * @return
     *     {@inheritDoc}
     */
    @Override
    public boolean visit(java.lang.String elementName, int elementIndex, Element element) {
        return visit(elementName, elementIndex, (Visitable) element);
    }
    
    /**
     * Delegates to {@link #visit(elementName, elementIndex, BackboneType)}
     * @return
     *     {@inheritDoc}
     */
    @Override
    public boolean visit(java.lang.String elementName, int elementIndex, ElementDefinition elementDefinition) {
        return visit(elementName, elementIndex, (BackboneType) elementDefinition);
    }

    /**
     * Delegates to {@link #visit(elementName, elementIndex, DataType)}
     * @return
     *     {@inheritDoc}
     */
    @Override
    public boolean visit(java.lang.String elementName, int elementIndex, Extension extension) {
        return visit(elementName, elementIndex, (DataType) extension);
    }

    /**
     * Delegates to {@link #visit(elementName, elementIndex, String)}
     * @return
     *     {@inheritDoc}
     */
    @Override
    public boolean visit(java.lang.String elementName, int elementIndex, Id id) {
        return visit(elementName, elementIndex, (String) id);
    }

    /**
     * Delegates to {@link #visit(elementName, elementIndex, DataType)}
     * @return
     *     {@inheritDoc}
     */
    @Override
    public boolean visit(java.lang.String elementName, int elementIndex, Identifier identifier) {
        return visit(elementName, elementIndex, (DataType) identifier);
    }

    /**
     * Delegates to {@link #visit(elementName, elementIndex, PrimitiveType)}
     * @return
     *     {@inheritDoc}
     */
    @Override
    public boolean visit(java.lang.String elementName, int elementIndex, Instant instant) {
        return visit(elementName, elementIndex, (PrimitiveType) instant);
    }

    /**
     * Delegates to {@link #visit(elementName, elementIndex, PrimitiveType)}
     * @return
     *     {@inheritDoc}
     */
    @Override
    public boolean visit(java.lang.String elementName, int elementIndex, Integer integer) {
        return visit(elementName, elementIndex, (PrimitiveType) integer);
    }

    /**
     * Delegates to {@link #visit(elementName, elementIndex, PrimitiveType)}
     * @return
     *     {@inheritDoc}
     */
    @Override
    public boolean visit(java.lang.String elementName, int elementIndex, Integer64 integer64) {
        return visit(elementName, elementIndex, (PrimitiveType) integer64);
    }

    /**
     * Delegates to {@link #visit(elementName, elementIndex, String)}
     * @return
     *     {@inheritDoc}
     */
    @Override
    public boolean visit(java.lang.String elementName, int elementIndex, Markdown markdown) {
        return visit(elementName, elementIndex, (String) markdown);
    }

    /**
     * Delegates to {@link #visit(elementName, elementIndex, DataType)}
     * @return
     *     {@inheritDoc}
     */
    @Override
    public boolean visit(java.lang.String elementName, int elementIndex, Meta meta) {
        return visit(elementName, elementIndex, (DataType) meta);
    }

    /**
     * Delegates to {@link #visit(elementName, elementIndex, DomainResource)}
     * @return
     *     {@inheritDoc}
     */
    @Override
    public boolean visit(java.lang.String elementName, int elementIndex, MetadataResource metadataResource) {
        return visit(elementName, elementIndex, (DomainResource) metadataResource);
    }

    /**
     * Delegates to {@link #visit(elementName, elementIndex, DataType)}
     * @return
     *     {@inheritDoc}
     */
    @Override
    public boolean visit(java.lang.String elementName, int elementIndex, Narrative narrative) {
        return visit(elementName, elementIndex, (DataType) narrative);
    }

    /**
     * Delegates to {@link #visit(elementName, elementIndex, Uri)}
     * @return
     *     {@inheritDoc}
     */
    @Override
    public boolean visit(java.lang.String elementName, int elementIndex, Oid oid) {
        return visit(elementName, elementIndex, (Uri) oid);
    }

    /**
     * Delegates to {@link #visit(elementName, elementIndex, DomainResource)}
     * @return
     *     {@inheritDoc}
     */
    @Override
    public boolean visit(java.lang.String elementName, int elementIndex, OperationDefinition operationDefinition) {
        return visit(elementName, elementIndex, (DomainResource) operationDefinition);
    }

    /**
     * Delegates to {@link #visit(elementName, elementIndex, DomainResource)}
     * @return
     *     {@inheritDoc}
     */
    @Override
    public boolean visit(java.lang.String elementName, int elementIndex, OperationOutcome operationOutcome) {
        return visit(elementName, elementIndex, (DomainResource) operationOutcome);
    }

    /**
     * Delegates to {@link #visit(elementName, elementIndex, Resource)}
     * @return
     *     {@inheritDoc}
     */
    @Override
    public boolean visit(java.lang.String elementName, int elementIndex, Parameters parameters) {
        return visit(elementName, elementIndex, (Resource) parameters);
    }

    /**
     * Delegates to {@link #visit(elementName, elementIndex, DataType)}
     * @return
     *     {@inheritDoc}
     */
    @Override
    public boolean visit(java.lang.String elementName, int elementIndex, Period period) {
        return visit(elementName, elementIndex, (DataType) period);
    }

    /**
     * Delegates to {@link #visit(elementName, elementIndex, Integer)}
     * @return
     *     {@inheritDoc}
     */
    @Override
    public boolean visit(java.lang.String elementName, int elementIndex, PositiveInt positiveInt) {
        return visit(elementName, elementIndex, (Integer) positiveInt);
    }

    /**
     * Delegates to {@link #visit(elementName, elementIndex, DataType)}
     * @return
     *     {@inheritDoc}
     */
    @Override
    public boolean visit(java.lang.String elementName, int elementIndex, PrimitiveType primitiveType) {
        return visit(elementName, elementIndex, (DataType) primitiveType);
    }

    /**
     * Delegates to {@link #visit(elementName, elementIndex, DataType)}
     * @return
     *     {@inheritDoc}
     */
    @Override
    public boolean visit(java.lang.String elementName, int elementIndex, Quantity quantity) {
        return visit(elementName, elementIndex, (DataType) quantity);
    }

    /**
     * Delegates to {@link #visit(elementName, elementIndex, DataType)}
     * @return
     *     {@inheritDoc}
     */
    @Override
    public boolean visit(java.lang.String elementName, int elementIndex, Reference reference) {
        return visit(elementName, elementIndex, (DataType) reference);
    }

    /**
     * Delegates to {@link #visit(elementName, elementIndex, DataType)}
     * @return
     *     {@inheritDoc}
     */
    @Override
    public boolean visit(java.lang.String elementName, int elementIndex, RelatedArtifact relatedArtifact) {
        return visit(elementName, elementIndex, (DataType) relatedArtifact);
    }

    /**
     * Delegates to {@link #visit(elementName, elementIndex, Visitable)}
     * @return
     *     {@inheritDoc}
     */
    @Override
    public boolean visit(java.lang.String elementName, int elementIndex, Resource resource) {
        return visit(elementName, elementIndex, (Visitable) resource);
    }
    
    /**
     * Delegates to {@link #visit(elementName, elementIndex, DataType)}
     * @return
     *     {@inheritDoc}
     */
    @Override
    public boolean visit(java.lang.String elementName, int elementIndex, Signature signature) {
        return visit(elementName, elementIndex, (DataType) signature);
    }

    /**
     * Delegates to {@link #visit(elementName, elementIndex, Quantity)}
     * @return
     *     {@inheritDoc}
     */
    @Override
    public boolean visit(java.lang.String elementName, int elementIndex, SimpleQuantity simpleQuantity) {
        return visit(elementName, elementIndex, (Quantity) simpleQuantity);
    }

    /**
     * Delegates to {@link #visit(elementName, elementIndex, PrimitiveType)}
     * @return
     *     {@inheritDoc}
     */
    @Override
    public boolean visit(java.lang.String elementName, int elementIndex, String string) {
        return visit(elementName, elementIndex, (PrimitiveType) string);
    }

    /**
     * Delegates to {@link #visit(elementName, elementIndex, DomainResource)}
     * @return
     *     {@inheritDoc}
     */
    @Override
    public boolean visit(java.lang.String elementName, int elementIndex, StructureDefinition structureDefinition) {
        return visit(elementName, elementIndex, (DomainResource) structureDefinition);
    }

    /**
     * Delegates to {@link #visit(elementName, elementIndex, DomainResource)}
     * @return
     *     {@inheritDoc}
     */
    @Override
    public boolean visit(java.lang.String elementName, int elementIndex, TerminologyCapabilities terminologyCapabilities) {
        return visit(elementName, elementIndex, (DomainResource) terminologyCapabilities);
    }

    /**
     * Delegates to {@link #visit(elementName, elementIndex, PrimitiveType)}
     * @return
     *     {@inheritDoc}
     */
    @Override
    public boolean visit(java.lang.String elementName, int elementIndex, Time time) {
        return visit(elementName, elementIndex, (PrimitiveType) time);
    }

    /**
     * Delegates to {@link #visit(elementName, elementIndex, Integer)}
     * @return
     *     {@inheritDoc}
     */
    @Override
    public boolean visit(java.lang.String elementName, int elementIndex, UnsignedInt unsignedInt) {
        return visit(elementName, elementIndex, (Integer) unsignedInt);
    }

    /**
     * Delegates to {@link #visit(elementName, elementIndex, PrimitiveType)}
     * @return
     *     {@inheritDoc}
     */
    @Override
    public boolean visit(java.lang.String elementName, int elementIndex, Uri uri) {
        return visit(elementName, elementIndex, (PrimitiveType) uri);
    }

    /**
     * Delegates to {@link #visit(elementName, elementIndex, Uri)}
     * @return
     *     {@inheritDoc}
     */
    @Override
    public boolean visit(java.lang.String elementName, int elementIndex, Url url) {
        return visit(elementName, elementIndex, (Uri) url);
    }

    /**
     * Delegates to {@link #visit(elementName, elementIndex, DataType)}
     * @return
     *     {@inheritDoc}
     */
    @Override
    public boolean visit(java.lang.String elementName, int elementIndex, UsageContext usageContext) {
        return visit(elementName, elementIndex, (DataType) usageContext);
    }

    /**
     * Delegates to {@link #visit(elementName, elementIndex, Uri)}
     * @return
     *     {@inheritDoc}
     */
    @Override
    public boolean visit(java.lang.String elementName, int elementIndex, Uuid uuid) {
        return visit(elementName, elementIndex, (Uri) uuid);
    }

    /**
     * Delegates to {@link #visit(elementName, elementIndex, DomainResource)}
     * @return
     *     {@inheritDoc}
     */
    @Override
    public boolean visit(java.lang.String elementName, int elementIndex, ValueSet valueSet) {
        return visit(elementName, elementIndex, (DomainResource) valueSet);
    }

    /**
     * Delegates to {@link #visit(elementName, elementIndex, Element)}
     * @return
     *     {@inheritDoc}
     */
    @Override
    public boolean visit(java.lang.String elementName, int elementIndex, Xhtml xhtml) {
        return visit(elementName, elementIndex, (Element) xhtml);
    }

    @Override
    public void visit(java.lang.String elementName, byte[] value) {
    }

    @Override
    public void visit(java.lang.String elementName, BigDecimal value) {
    }

    @Override
    public void visit(java.lang.String elementName, java.lang.Boolean value) {
    }

    @Override
    public void visit(java.lang.String elementName, java.lang.Integer value) {
    }

    @Override
    public void visit(java.lang.String elementName, LocalDate value) {
    }

    @Override
    public void visit(java.lang.String elementName, LocalTime value) {
    }

    @Override
    public void visit(java.lang.String elementName, java.lang.String value) {
    }

    @Override
    public void visit(java.lang.String elementName, Year value) {
    }

    @Override
    public void visit(java.lang.String elementName, YearMonth value) {
    }

    @Override
    public void visit(java.lang.String elementName, ZonedDateTime value) {
    }
}
