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
package com.b2international.snowowl.core;

import java.io.Serializable;
import java.util.List;
import java.util.Objects;

import com.b2international.snowowl.core.internal.DependencyDocument;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

/**
 * Describes a dependency to another {@link Resource}, usually a {@link TerminologyResource}.
 * 
 * @since 8.12.0
 */
@JsonPropertyOrder({ "uri", "scope", "resource", "upgrades" })
public final class Dependency implements Serializable {

	private static final long serialVersionUID = 1L;
	
	private final ResourceURIWithQuery uri;
	private final String scope;

	// expandable props
	private TerminologyResource resource;
	private List<ResourceURI> upgrades;

	@JsonCreator
	Dependency(@JsonProperty("uri") ResourceURIWithQuery uri, @JsonProperty("scope") String scope) {
		this.uri = Objects.requireNonNull(uri);
		this.scope = scope;
	}

	/**
	 * @return the {@link ResourceURIWithQuery} this dependency points to, never <code>null</code>
	 */
	public ResourceURIWithQuery getUri() {
		return uri;
	}

	/**
	 * @return the scope of the dependency, usually a resource type specific value, may not be <code>null</code>
	 */
	public String getScope() {
		return scope;
	}

	/**
	 * @return the {@link TerminologyResource} object denoted by the {@link #getUri()} property, <code>null</code> if not requested to be
	 *         expanded, never <code>null</code> if requested to be expanded
	 */
	public TerminologyResource getResource() {
		return resource;
	}

	/**
	 * @param resource - the expanded resource to attach to this {@link Dependency} instance
	 */
	public void setResource(TerminologyResource resource) {
		this.resource = resource;
	}

	/**
	 * @return a {@link List} of version URIs where each denoted version is newer than the current dependency's version. If not requested to be
	 *         included in the response, this may return <code>null</code>.
	 */
	public List<ResourceURI> getUpgrades() {
		return upgrades;
	}

	/**
	 * @param upgrades - the expanded list of version URIs to attach to this {@link Dependency} instance that represent possible upgrades for this dependency
	 */
	public void setUpgrades(List<ResourceURI> upgrades) {
		this.upgrades = upgrades;
	}
	
	/**
	 * @return a {@link DependencyDocument document} version of this domain model
	 */
	@JsonIgnore
	public DependencyDocument toDocument() {
		return new DependencyDocument(uri, scope);
	}
	
	@JsonIgnore
	public boolean isExtensionOf() {
		return TerminologyResource.DependencyScope.EXTENSION_OF.equals(scope);
	}
	
	@JsonIgnore
	public boolean isUpgradeOf() {
		return TerminologyResource.DependencyScope.UPGRADE_OF.equals(scope);
	}
	
	@Override
	public int hashCode() {
		return Objects.hash(uri, scope);
	}
	
	@Override
	public boolean equals(Object obj) {
		if (this == obj) return true;
		if (obj == null) return false;
		if (getClass() != obj.getClass()) return false;
		Dependency other = (Dependency) obj;
		return Objects.equals(uri, other.uri) && Objects.equals(scope, other.scope);
	}

	/**
	 * Creates a new unscoped {@link Dependency} instance with the given {@link ResourceURI uri}.
	 * 
	 * @param resourceUri - the URI this dependency instance will point to
	 * @return a new {@link Dependency} instance
	 */
	public static Dependency of(ResourceURI resourceUri) {
		return of(ResourceURIWithQuery.of(resourceUri.getResourceType(), resourceUri.withoutResourceType()), null);
	}
	
	/**
	 * Creates a new {@link Dependency} instance with the given {@link ResourceURI} uri and optional scope value.
	 * 
	 * @param resourceUri - the URI this dependency instance will point to
	 * @param scope - optional scope of the dependency, may be <code>null</code>\
	 * @return a new {@link Dependency} instance
	 */
	public static final Dependency of(ResourceURI resourceUri, String scope) {
		return of(ResourceURIWithQuery.of(resourceUri.getResourceType(), resourceUri.withoutResourceType()), scope);
	}
	
	/**
	 * Creates a new {@link Dependency} instance with the given {@link ResourceURIWithQuery} uri and optional scope value.
	 * 
	 * @param resourceUri - the URI this dependency instance will point to
	 * @param scope - optional scope of the dependency
	 * @return a new {@link Dependency} instance
	 */
	public static final Dependency of(ResourceURIWithQuery resourceUri, String scope) {
		return new Dependency(resourceUri, scope);
	}
	
	/**
	 * Creates and returns a new instance of {@link Dependency} built from the given {@link DependencyDocument}. 
	 * @param doc
	 * @return a new instance of {@link Dependency}, never <code>null</code>
	 */
	public static final Dependency from(DependencyDocument doc) {
		return new Dependency(doc.getUri(), doc.getScope());
	}

}
