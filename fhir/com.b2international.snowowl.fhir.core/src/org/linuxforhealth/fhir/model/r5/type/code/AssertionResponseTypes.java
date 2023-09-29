/*
 * (C) Copyright IBM Corp. 2019, 2022
 *
 * SPDX-License-Identifier: Apache-2.0
 */

package org.linuxforhealth.fhir.model.r5.type.code;

import org.linuxforhealth.fhir.model.annotation.System;
import org.linuxforhealth.fhir.model.r5.type.Code;
import org.linuxforhealth.fhir.model.r5.type.Extension;
import org.linuxforhealth.fhir.model.r5.type.String;

import java.util.Collection;
import java.util.Objects;

import javax.annotation.Generated;

@System("http://hl7.org/fhir/assert-response-code-types")
@Generated("org.linuxforhealth.fhir.tools.CodeGenerator")
public class AssertionResponseTypes extends Code {
    /**
     * Continue
     * 
     * <p>Response code is 100.
     */
    public static final AssertionResponseTypes CONTINUE = AssertionResponseTypes.builder().value(Value.CONTINUE).build();

    /**
     * Switching Protocols
     * 
     * <p>Response code is 101.
     */
    public static final AssertionResponseTypes SWITCHING_PROTOCOLS = AssertionResponseTypes.builder().value(Value.SWITCHING_PROTOCOLS).build();

    /**
     * OK
     * 
     * <p>Response code is 200.
     */
    public static final AssertionResponseTypes OKAY = AssertionResponseTypes.builder().value(Value.OKAY).build();

    /**
     * Created
     * 
     * <p>Response code is 201.
     */
    public static final AssertionResponseTypes CREATED = AssertionResponseTypes.builder().value(Value.CREATED).build();

    /**
     * Accepted
     * 
     * <p>Response code is 202.
     */
    public static final AssertionResponseTypes ACCEPTED = AssertionResponseTypes.builder().value(Value.ACCEPTED).build();

    /**
     * Non-Authoritative Information
     * 
     * <p>Response code is 203.
     */
    public static final AssertionResponseTypes NON_AUTHORITATIVE_INFORMATION = AssertionResponseTypes.builder().value(Value.NON_AUTHORITATIVE_INFORMATION).build();

    /**
     * No Content
     * 
     * <p>Response code is 204.
     */
    public static final AssertionResponseTypes NO_CONTENT = AssertionResponseTypes.builder().value(Value.NO_CONTENT).build();

    /**
     * Reset Content
     * 
     * <p>Response code is 205.
     */
    public static final AssertionResponseTypes RESET_CONTENT = AssertionResponseTypes.builder().value(Value.RESET_CONTENT).build();

    /**
     * Partial Content
     * 
     * <p>Response code is 206.
     */
    public static final AssertionResponseTypes PARTIAL_CONTENT = AssertionResponseTypes.builder().value(Value.PARTIAL_CONTENT).build();

    /**
     * Multiple Choices
     * 
     * <p>Response code is 300.
     */
    public static final AssertionResponseTypes MULTIPLE_CHOICES = AssertionResponseTypes.builder().value(Value.MULTIPLE_CHOICES).build();

    /**
     * Moved Permanently
     * 
     * <p>Response code is 301.
     */
    public static final AssertionResponseTypes MOVED_PERMANENTLY = AssertionResponseTypes.builder().value(Value.MOVED_PERMANENTLY).build();

    /**
     * Found
     * 
     * <p>Response code is 302.
     */
    public static final AssertionResponseTypes FOUND = AssertionResponseTypes.builder().value(Value.FOUND).build();

    /**
     * See Other
     * 
     * <p>Response code is 303.
     */
    public static final AssertionResponseTypes SEE_OTHER = AssertionResponseTypes.builder().value(Value.SEE_OTHER).build();

    /**
     * Not Modified
     * 
     * <p>Response code is 304.
     */
    public static final AssertionResponseTypes NOT_MODIFIED = AssertionResponseTypes.builder().value(Value.NOT_MODIFIED).build();

    /**
     * Use Proxy
     * 
     * <p>Response code is 305.
     */
    public static final AssertionResponseTypes USE_PROXY = AssertionResponseTypes.builder().value(Value.USE_PROXY).build();

    /**
     * Temporary Redirect
     * 
     * <p>Response code is 307.
     */
    public static final AssertionResponseTypes TEMPORARY_REDIRECT = AssertionResponseTypes.builder().value(Value.TEMPORARY_REDIRECT).build();

    /**
     * Permanent Redirect
     * 
     * <p>Response code is 308.
     */
    public static final AssertionResponseTypes PERMANENT_REDIRECT = AssertionResponseTypes.builder().value(Value.PERMANENT_REDIRECT).build();

    /**
     * Bad Request
     * 
     * <p>Response code is 400.
     */
    public static final AssertionResponseTypes BAD_REQUEST = AssertionResponseTypes.builder().value(Value.BAD_REQUEST).build();

    /**
     * Unauthorized
     * 
     * <p>Response code is 401.
     */
    public static final AssertionResponseTypes UNAUTHORIZED = AssertionResponseTypes.builder().value(Value.UNAUTHORIZED).build();

    /**
     * Payment Required
     * 
     * <p>Response code is 402.
     */
    public static final AssertionResponseTypes PAYMENT_REQUIRED = AssertionResponseTypes.builder().value(Value.PAYMENT_REQUIRED).build();

    /**
     * Forbidden
     * 
     * <p>Response code is 403.
     */
    public static final AssertionResponseTypes FORBIDDEN = AssertionResponseTypes.builder().value(Value.FORBIDDEN).build();

    /**
     * Not Found
     * 
     * <p>Response code is 404.
     */
    public static final AssertionResponseTypes NOT_FOUND = AssertionResponseTypes.builder().value(Value.NOT_FOUND).build();

    /**
     * Method Not Allowed
     * 
     * <p>Response code is 405.
     */
    public static final AssertionResponseTypes METHOD_NOT_ALLOWED = AssertionResponseTypes.builder().value(Value.METHOD_NOT_ALLOWED).build();

    /**
     * Not Acceptable
     * 
     * <p>Response code is 406.
     */
    public static final AssertionResponseTypes NOT_ACCEPTABLE = AssertionResponseTypes.builder().value(Value.NOT_ACCEPTABLE).build();

    /**
     * Proxy Authentication Required
     * 
     * <p>Response code is 407.
     */
    public static final AssertionResponseTypes PROXY_AUTHENTICATION_REQUIRED = AssertionResponseTypes.builder().value(Value.PROXY_AUTHENTICATION_REQUIRED).build();

    /**
     * Request Timeout
     * 
     * <p>Response code is 408.
     */
    public static final AssertionResponseTypes REQUEST_TIMEOUT = AssertionResponseTypes.builder().value(Value.REQUEST_TIMEOUT).build();

    /**
     * Conflict
     * 
     * <p>Response code is 409.
     */
    public static final AssertionResponseTypes CONFLICT = AssertionResponseTypes.builder().value(Value.CONFLICT).build();

    /**
     * Gone
     * 
     * <p>Response code is 410.
     */
    public static final AssertionResponseTypes GONE = AssertionResponseTypes.builder().value(Value.GONE).build();

    /**
     * Length Required
     * 
     * <p>Response code is 411.
     */
    public static final AssertionResponseTypes LENGTH_REQUIRED = AssertionResponseTypes.builder().value(Value.LENGTH_REQUIRED).build();

    /**
     * Precondition Failed
     * 
     * <p>Response code is 412.
     */
    public static final AssertionResponseTypes PRECONDITION_FAILED = AssertionResponseTypes.builder().value(Value.PRECONDITION_FAILED).build();

    /**
     * Content Too Large
     * 
     * <p>Response code is 413.
     */
    public static final AssertionResponseTypes CONTENT_TOO_LARGE = AssertionResponseTypes.builder().value(Value.CONTENT_TOO_LARGE).build();

    /**
     * URI Too Long
     * 
     * <p>Response code is 414.
     */
    public static final AssertionResponseTypes URI_TOO_LONG = AssertionResponseTypes.builder().value(Value.URI_TOO_LONG).build();

    /**
     * Unsupported Media Type
     * 
     * <p>Response code is 415.
     */
    public static final AssertionResponseTypes UNSUPPORTED_MEDIA_TYPE = AssertionResponseTypes.builder().value(Value.UNSUPPORTED_MEDIA_TYPE).build();

    /**
     * Range Not Satisfiable
     * 
     * <p>Response code is 416.
     */
    public static final AssertionResponseTypes RANGE_NOT_SATISFIABLE = AssertionResponseTypes.builder().value(Value.RANGE_NOT_SATISFIABLE).build();

    /**
     * Expectation Failed
     * 
     * <p>Response code is 417.
     */
    public static final AssertionResponseTypes EXPECTATION_FAILED = AssertionResponseTypes.builder().value(Value.EXPECTATION_FAILED).build();

    /**
     * Misdirected Request
     * 
     * <p>Response code is 421.
     */
    public static final AssertionResponseTypes MISDIRECTED_REQUEST = AssertionResponseTypes.builder().value(Value.MISDIRECTED_REQUEST).build();

    /**
     * Unprocessable Content
     * 
     * <p>Response code is 422.
     */
    public static final AssertionResponseTypes UNPROCESSABLE_CONTENT = AssertionResponseTypes.builder().value(Value.UNPROCESSABLE_CONTENT).build();

    /**
     * Upgrade Required
     * 
     * <p>Response code is 426.
     */
    public static final AssertionResponseTypes UPGRADE_REQUIRED = AssertionResponseTypes.builder().value(Value.UPGRADE_REQUIRED).build();

    /**
     * Internal Server Error
     * 
     * <p>Response code is 500.
     */
    public static final AssertionResponseTypes INTERNAL_SERVER_ERROR = AssertionResponseTypes.builder().value(Value.INTERNAL_SERVER_ERROR).build();

    /**
     * Not Implemented
     * 
     * <p>Response code is 501.
     */
    public static final AssertionResponseTypes NOT_IMPLEMENTED = AssertionResponseTypes.builder().value(Value.NOT_IMPLEMENTED).build();

    /**
     * Bad Gateway
     * 
     * <p>Response code is 502.
     */
    public static final AssertionResponseTypes BAD_GATEWAY = AssertionResponseTypes.builder().value(Value.BAD_GATEWAY).build();

    /**
     * Service Unavailable
     * 
     * <p>Response code is 503.
     */
    public static final AssertionResponseTypes SERVICE_UNAVAILABLE = AssertionResponseTypes.builder().value(Value.SERVICE_UNAVAILABLE).build();

    /**
     * Gateway Timeout
     * 
     * <p>Response code is 504.
     */
    public static final AssertionResponseTypes GATEWAY_TIMEOUT = AssertionResponseTypes.builder().value(Value.GATEWAY_TIMEOUT).build();

    /**
     * HTTP Version Not Supported
     * 
     * <p>Response code is 505.
     */
    public static final AssertionResponseTypes HTTP_VERSION_NOT_SUPPORTED = AssertionResponseTypes.builder().value(Value.HTTP_VERSION_NOT_SUPPORTED).build();

    private volatile int hashCode;

    private AssertionResponseTypes(Builder builder) {
        super(builder);
    }

    /**
     * Get the value of this AssertionResponseTypes as an enum constant.
     */
    public Value getValueAsEnum() {
        return (value != null) ? Value.from(value) : null;
    }

    /**
     * Factory method for creating AssertionResponseTypes objects from a passed enum value.
     */
    public static AssertionResponseTypes of(Value value) {
        switch (value) {
        case CONTINUE:
            return CONTINUE;
        case SWITCHING_PROTOCOLS:
            return SWITCHING_PROTOCOLS;
        case OKAY:
            return OKAY;
        case CREATED:
            return CREATED;
        case ACCEPTED:
            return ACCEPTED;
        case NON_AUTHORITATIVE_INFORMATION:
            return NON_AUTHORITATIVE_INFORMATION;
        case NO_CONTENT:
            return NO_CONTENT;
        case RESET_CONTENT:
            return RESET_CONTENT;
        case PARTIAL_CONTENT:
            return PARTIAL_CONTENT;
        case MULTIPLE_CHOICES:
            return MULTIPLE_CHOICES;
        case MOVED_PERMANENTLY:
            return MOVED_PERMANENTLY;
        case FOUND:
            return FOUND;
        case SEE_OTHER:
            return SEE_OTHER;
        case NOT_MODIFIED:
            return NOT_MODIFIED;
        case USE_PROXY:
            return USE_PROXY;
        case TEMPORARY_REDIRECT:
            return TEMPORARY_REDIRECT;
        case PERMANENT_REDIRECT:
            return PERMANENT_REDIRECT;
        case BAD_REQUEST:
            return BAD_REQUEST;
        case UNAUTHORIZED:
            return UNAUTHORIZED;
        case PAYMENT_REQUIRED:
            return PAYMENT_REQUIRED;
        case FORBIDDEN:
            return FORBIDDEN;
        case NOT_FOUND:
            return NOT_FOUND;
        case METHOD_NOT_ALLOWED:
            return METHOD_NOT_ALLOWED;
        case NOT_ACCEPTABLE:
            return NOT_ACCEPTABLE;
        case PROXY_AUTHENTICATION_REQUIRED:
            return PROXY_AUTHENTICATION_REQUIRED;
        case REQUEST_TIMEOUT:
            return REQUEST_TIMEOUT;
        case CONFLICT:
            return CONFLICT;
        case GONE:
            return GONE;
        case LENGTH_REQUIRED:
            return LENGTH_REQUIRED;
        case PRECONDITION_FAILED:
            return PRECONDITION_FAILED;
        case CONTENT_TOO_LARGE:
            return CONTENT_TOO_LARGE;
        case URI_TOO_LONG:
            return URI_TOO_LONG;
        case UNSUPPORTED_MEDIA_TYPE:
            return UNSUPPORTED_MEDIA_TYPE;
        case RANGE_NOT_SATISFIABLE:
            return RANGE_NOT_SATISFIABLE;
        case EXPECTATION_FAILED:
            return EXPECTATION_FAILED;
        case MISDIRECTED_REQUEST:
            return MISDIRECTED_REQUEST;
        case UNPROCESSABLE_CONTENT:
            return UNPROCESSABLE_CONTENT;
        case UPGRADE_REQUIRED:
            return UPGRADE_REQUIRED;
        case INTERNAL_SERVER_ERROR:
            return INTERNAL_SERVER_ERROR;
        case NOT_IMPLEMENTED:
            return NOT_IMPLEMENTED;
        case BAD_GATEWAY:
            return BAD_GATEWAY;
        case SERVICE_UNAVAILABLE:
            return SERVICE_UNAVAILABLE;
        case GATEWAY_TIMEOUT:
            return GATEWAY_TIMEOUT;
        case HTTP_VERSION_NOT_SUPPORTED:
            return HTTP_VERSION_NOT_SUPPORTED;
        default:
            throw new IllegalStateException(value.name());
        }
    }

    /**
     * Factory method for creating AssertionResponseTypes objects from a passed string value.
     * 
     * @param value
     *     A string that matches one of the allowed code values
     * @throws IllegalArgumentException
     *     If the passed string cannot be parsed into an allowed code value
     */
    public static AssertionResponseTypes of(java.lang.String value) {
        return of(Value.from(value));
    }

    /**
     * Inherited factory method for creating AssertionResponseTypes objects from a passed string value.
     * 
     * @param value
     *     A string that matches one of the allowed code values
     * @throws IllegalArgumentException
     *     If the passed string cannot be parsed into an allowed code value
     */
    public static String string(java.lang.String value) {
        return of(Value.from(value));
    }

    /**
     * Inherited factory method for creating AssertionResponseTypes objects from a passed string value.
     * 
     * @param value
     *     A string that matches one of the allowed code values
     * @throws IllegalArgumentException
     *     If the passed string cannot be parsed into an allowed code value
     */
    public static Code code(java.lang.String value) {
        return of(Value.from(value));
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        AssertionResponseTypes other = (AssertionResponseTypes) obj;
        return Objects.equals(id, other.id) && Objects.equals(extension, other.extension) && Objects.equals(value, other.value);
    }

    @Override
    public int hashCode() {
        int result = hashCode;
        if (result == 0) {
            result = Objects.hash(id, extension, value);
            hashCode = result;
        }
        return result;
    }

    public Builder toBuilder() {
        return new Builder().from(this);
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder extends Code.Builder {
        private Builder() {
            super();
        }

        @Override
        public Builder id(java.lang.String id) {
            return (Builder) super.id(id);
        }

        @Override
        public Builder extension(Extension... extension) {
            return (Builder) super.extension(extension);
        }

        @Override
        public Builder extension(Collection<Extension> extension) {
            return (Builder) super.extension(extension);
        }

        @Override
        public Builder value(java.lang.String value) {
            return (value != null) ? (Builder) super.value(Value.from(value).value()) : this;
        }

        /**
         * Primitive value for code
         * 
         * @param value
         *     An enum constant for AssertionResponseTypes
         * 
         * @return
         *     A reference to this Builder instance
         */
        public Builder value(Value value) {
            return (value != null) ? (Builder) super.value(value.value()) : this;
        }

        @Override
        public AssertionResponseTypes build() {
            AssertionResponseTypes assertionResponseTypes = new AssertionResponseTypes(this);
            if (validating) {
                validate(assertionResponseTypes);
            }
            return assertionResponseTypes;
        }

        protected void validate(AssertionResponseTypes assertionResponseTypes) {
            super.validate(assertionResponseTypes);
        }

        protected Builder from(AssertionResponseTypes assertionResponseTypes) {
            super.from(assertionResponseTypes);
            return this;
        }
    }

    public enum Value {
        /**
         * Continue
         * 
         * <p>Response code is 100.
         */
        CONTINUE("continue"),

        /**
         * Switching Protocols
         * 
         * <p>Response code is 101.
         */
        SWITCHING_PROTOCOLS("switchingProtocols"),

        /**
         * OK
         * 
         * <p>Response code is 200.
         */
        OKAY("okay"),

        /**
         * Created
         * 
         * <p>Response code is 201.
         */
        CREATED("created"),

        /**
         * Accepted
         * 
         * <p>Response code is 202.
         */
        ACCEPTED("accepted"),

        /**
         * Non-Authoritative Information
         * 
         * <p>Response code is 203.
         */
        NON_AUTHORITATIVE_INFORMATION("nonAuthoritativeInformation"),

        /**
         * No Content
         * 
         * <p>Response code is 204.
         */
        NO_CONTENT("noContent"),

        /**
         * Reset Content
         * 
         * <p>Response code is 205.
         */
        RESET_CONTENT("resetContent"),

        /**
         * Partial Content
         * 
         * <p>Response code is 206.
         */
        PARTIAL_CONTENT("partialContent"),

        /**
         * Multiple Choices
         * 
         * <p>Response code is 300.
         */
        MULTIPLE_CHOICES("multipleChoices"),

        /**
         * Moved Permanently
         * 
         * <p>Response code is 301.
         */
        MOVED_PERMANENTLY("movedPermanently"),

        /**
         * Found
         * 
         * <p>Response code is 302.
         */
        FOUND("found"),

        /**
         * See Other
         * 
         * <p>Response code is 303.
         */
        SEE_OTHER("seeOther"),

        /**
         * Not Modified
         * 
         * <p>Response code is 304.
         */
        NOT_MODIFIED("notModified"),

        /**
         * Use Proxy
         * 
         * <p>Response code is 305.
         */
        USE_PROXY("useProxy"),

        /**
         * Temporary Redirect
         * 
         * <p>Response code is 307.
         */
        TEMPORARY_REDIRECT("temporaryRedirect"),

        /**
         * Permanent Redirect
         * 
         * <p>Response code is 308.
         */
        PERMANENT_REDIRECT("permanentRedirect"),

        /**
         * Bad Request
         * 
         * <p>Response code is 400.
         */
        BAD_REQUEST("badRequest"),

        /**
         * Unauthorized
         * 
         * <p>Response code is 401.
         */
        UNAUTHORIZED("unauthorized"),

        /**
         * Payment Required
         * 
         * <p>Response code is 402.
         */
        PAYMENT_REQUIRED("paymentRequired"),

        /**
         * Forbidden
         * 
         * <p>Response code is 403.
         */
        FORBIDDEN("forbidden"),

        /**
         * Not Found
         * 
         * <p>Response code is 404.
         */
        NOT_FOUND("notFound"),

        /**
         * Method Not Allowed
         * 
         * <p>Response code is 405.
         */
        METHOD_NOT_ALLOWED("methodNotAllowed"),

        /**
         * Not Acceptable
         * 
         * <p>Response code is 406.
         */
        NOT_ACCEPTABLE("notAcceptable"),

        /**
         * Proxy Authentication Required
         * 
         * <p>Response code is 407.
         */
        PROXY_AUTHENTICATION_REQUIRED("proxyAuthenticationRequired"),

        /**
         * Request Timeout
         * 
         * <p>Response code is 408.
         */
        REQUEST_TIMEOUT("requestTimeout"),

        /**
         * Conflict
         * 
         * <p>Response code is 409.
         */
        CONFLICT("conflict"),

        /**
         * Gone
         * 
         * <p>Response code is 410.
         */
        GONE("gone"),

        /**
         * Length Required
         * 
         * <p>Response code is 411.
         */
        LENGTH_REQUIRED("lengthRequired"),

        /**
         * Precondition Failed
         * 
         * <p>Response code is 412.
         */
        PRECONDITION_FAILED("preconditionFailed"),

        /**
         * Content Too Large
         * 
         * <p>Response code is 413.
         */
        CONTENT_TOO_LARGE("contentTooLarge"),

        /**
         * URI Too Long
         * 
         * <p>Response code is 414.
         */
        URI_TOO_LONG("uriTooLong"),

        /**
         * Unsupported Media Type
         * 
         * <p>Response code is 415.
         */
        UNSUPPORTED_MEDIA_TYPE("unsupportedMediaType"),

        /**
         * Range Not Satisfiable
         * 
         * <p>Response code is 416.
         */
        RANGE_NOT_SATISFIABLE("rangeNotSatisfiable"),

        /**
         * Expectation Failed
         * 
         * <p>Response code is 417.
         */
        EXPECTATION_FAILED("expectationFailed"),

        /**
         * Misdirected Request
         * 
         * <p>Response code is 421.
         */
        MISDIRECTED_REQUEST("misdirectedRequest"),

        /**
         * Unprocessable Content
         * 
         * <p>Response code is 422.
         */
        UNPROCESSABLE_CONTENT("unprocessableContent"),

        /**
         * Upgrade Required
         * 
         * <p>Response code is 426.
         */
        UPGRADE_REQUIRED("upgradeRequired"),

        /**
         * Internal Server Error
         * 
         * <p>Response code is 500.
         */
        INTERNAL_SERVER_ERROR("internalServerError"),

        /**
         * Not Implemented
         * 
         * <p>Response code is 501.
         */
        NOT_IMPLEMENTED("notImplemented"),

        /**
         * Bad Gateway
         * 
         * <p>Response code is 502.
         */
        BAD_GATEWAY("badGateway"),

        /**
         * Service Unavailable
         * 
         * <p>Response code is 503.
         */
        SERVICE_UNAVAILABLE("serviceUnavailable"),

        /**
         * Gateway Timeout
         * 
         * <p>Response code is 504.
         */
        GATEWAY_TIMEOUT("gatewayTimeout"),

        /**
         * HTTP Version Not Supported
         * 
         * <p>Response code is 505.
         */
        HTTP_VERSION_NOT_SUPPORTED("httpVersionNotSupported");

        private final java.lang.String value;

        Value(java.lang.String value) {
            this.value = value;
        }

        /**
         * @return
         *     The java.lang.String value of the code represented by this enum
         */
        public java.lang.String value() {
            return value;
        }

        /**
         * Factory method for creating AssertionResponseTypes.Value values from a passed string value.
         * 
         * @param value
         *     A string that matches one of the allowed code values
         * @return
         *     The corresponding AssertionResponseTypes.Value or null if a null value was passed
         * @throws IllegalArgumentException
         *     If the passed string is not null and cannot be parsed into an allowed code value
         */
        public static Value from(java.lang.String value) {
            if (value == null) {
                return null;
            }
            switch (value) {
            case "continue":
                return CONTINUE;
            case "switchingProtocols":
                return SWITCHING_PROTOCOLS;
            case "okay":
                return OKAY;
            case "created":
                return CREATED;
            case "accepted":
                return ACCEPTED;
            case "nonAuthoritativeInformation":
                return NON_AUTHORITATIVE_INFORMATION;
            case "noContent":
                return NO_CONTENT;
            case "resetContent":
                return RESET_CONTENT;
            case "partialContent":
                return PARTIAL_CONTENT;
            case "multipleChoices":
                return MULTIPLE_CHOICES;
            case "movedPermanently":
                return MOVED_PERMANENTLY;
            case "found":
                return FOUND;
            case "seeOther":
                return SEE_OTHER;
            case "notModified":
                return NOT_MODIFIED;
            case "useProxy":
                return USE_PROXY;
            case "temporaryRedirect":
                return TEMPORARY_REDIRECT;
            case "permanentRedirect":
                return PERMANENT_REDIRECT;
            case "badRequest":
                return BAD_REQUEST;
            case "unauthorized":
                return UNAUTHORIZED;
            case "paymentRequired":
                return PAYMENT_REQUIRED;
            case "forbidden":
                return FORBIDDEN;
            case "notFound":
                return NOT_FOUND;
            case "methodNotAllowed":
                return METHOD_NOT_ALLOWED;
            case "notAcceptable":
                return NOT_ACCEPTABLE;
            case "proxyAuthenticationRequired":
                return PROXY_AUTHENTICATION_REQUIRED;
            case "requestTimeout":
                return REQUEST_TIMEOUT;
            case "conflict":
                return CONFLICT;
            case "gone":
                return GONE;
            case "lengthRequired":
                return LENGTH_REQUIRED;
            case "preconditionFailed":
                return PRECONDITION_FAILED;
            case "contentTooLarge":
                return CONTENT_TOO_LARGE;
            case "uriTooLong":
                return URI_TOO_LONG;
            case "unsupportedMediaType":
                return UNSUPPORTED_MEDIA_TYPE;
            case "rangeNotSatisfiable":
                return RANGE_NOT_SATISFIABLE;
            case "expectationFailed":
                return EXPECTATION_FAILED;
            case "misdirectedRequest":
                return MISDIRECTED_REQUEST;
            case "unprocessableContent":
                return UNPROCESSABLE_CONTENT;
            case "upgradeRequired":
                return UPGRADE_REQUIRED;
            case "internalServerError":
                return INTERNAL_SERVER_ERROR;
            case "notImplemented":
                return NOT_IMPLEMENTED;
            case "badGateway":
                return BAD_GATEWAY;
            case "serviceUnavailable":
                return SERVICE_UNAVAILABLE;
            case "gatewayTimeout":
                return GATEWAY_TIMEOUT;
            case "httpVersionNotSupported":
                return HTTP_VERSION_NOT_SUPPORTED;
            default:
                throw new IllegalArgumentException(value);
            }
        }
    }
}
