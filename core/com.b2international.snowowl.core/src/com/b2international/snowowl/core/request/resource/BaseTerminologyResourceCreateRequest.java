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
package com.b2international.snowowl.core.request.resource;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.TreeSet;
import java.util.stream.Collectors;

import javax.annotation.OverridingMethodsMustInvokeSuper;

import org.hibernate.validator.constraints.NotEmpty;

import com.b2international.commons.CompareUtils;
import com.b2international.commons.StringUtils;
import com.b2international.commons.collections.Collections3;
import com.b2international.commons.exceptions.AlreadyExistsException;
import com.b2international.commons.exceptions.BadRequestException;
import com.b2international.commons.json.Json;
import com.b2international.snowowl.core.Dependency;
import com.b2international.snowowl.core.Repository;
import com.b2international.snowowl.core.RepositoryManager;
import com.b2international.snowowl.core.Resource;
import com.b2international.snowowl.core.ResourceURI;
import com.b2international.snowowl.core.ResourceURIWithQuery;
import com.b2international.snowowl.core.ServiceProvider;
import com.b2international.snowowl.core.TerminologyResource;
import com.b2international.snowowl.core.branch.Branch;
import com.b2international.snowowl.core.branch.Branches;
import com.b2international.snowowl.core.bundle.Bundle;
import com.b2international.snowowl.core.collection.TerminologyResourceCollection;
import com.b2international.snowowl.core.collection.TerminologyResourceCollectionToolingSupport;
import com.b2international.snowowl.core.domain.IComponent;
import com.b2international.snowowl.core.domain.RepositoryContext;
import com.b2international.snowowl.core.domain.TransactionContext;
import com.b2international.snowowl.core.identity.User;
import com.b2international.snowowl.core.internal.ResourceDocument.Builder;
import com.b2international.snowowl.core.repository.RepositoryRequests;
import com.b2international.snowowl.core.request.BaseResourceCreateRequest;
import com.b2international.snowowl.core.request.ResourceRequests;
import com.b2international.snowowl.core.uri.ResourceURIPathResolver;
import com.b2international.snowowl.core.version.Version;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.common.base.Strings;
import com.google.common.collect.HashMultiset;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSortedSet;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;

/**
 * @since 8.12.0
 */
public abstract class BaseTerminologyResourceCreateRequest extends BaseResourceCreateRequest {

	private static final long serialVersionUID = 3L;

	// specialized resource fields
	// optional OID, but if defined it must be unique
	@JsonProperty
	private String oid;
	
	@JsonProperty
	private String branchPath;
	
	@JsonProperty
	@NotEmpty
	private String toolingId;
	
	@Deprecated
	@JsonProperty
	private ResourceURI extensionOf;
	
	@Deprecated
	@JsonProperty
	private ResourceURI upgradeOf;
	
	@JsonProperty
	private List<Dependency> dependencies;

	// runtime fields
	private transient List<Dependency> mergedDependencies;
	private transient String parentPath;
	
	public final void setOid(String oid) {
		this.oid = oid;
	}
	
	public final void setBranchPath(String branchPath) {
		this.branchPath = branchPath;
	}
	
	public final void setToolingId(String toolingId) {
		this.toolingId = toolingId;
	}

	/**
	 * @param extensionOf
	 * @deprecated - replaced by {@link #setDependencies(List)}, will be removed in 9.0
	 */
	public final void setExtensionOf(ResourceURI extensionOf) {
		this.extensionOf = extensionOf;
	}
	
	/**
	 * @param upgradeOf
	 * @deprecated - replaced by {@link #setDependencies(List)}, will be removed in 9.0
	 */
	public final void setUpgradeOf(ResourceURI upgradeOf) {
		this.upgradeOf = upgradeOf;
	}
	
	public final void setDependencies(List<Dependency> dependencies) {
		this.dependencies = dependencies;
	}
	
	protected final String getOid() {
		return oid;
	}
	
	protected final String getBranchPath() {
		return branchPath;
	}
	
	protected final String getToolingId() {
		return toolingId;
	}
	
	protected final List<Dependency> getDependencies() {
		return dependencies;
	}
	
	/**
	 * @deprecated - replaced by {@link #getDependencies(List)}, will be removed in 9.0
	 * @return
	 */
	protected final ResourceURI getExtensionOf() {
		return extensionOf;
	}
	
	/**
	 * @deprecated - replaced by {@link #getDependencies(List)}, will be removed in 9.0
	 * @return
	 */
	protected final ResourceURI getUpgradeOf() {
		return upgradeOf;
	}
	
	@Override
	@OverridingMethodsMustInvokeSuper
	protected Builder completeResource(Builder builder) {
		return builder
				.oid(oid)
				.branchPath(branchPath)
				.toolingId(toolingId)
				// we accept deprecated fields as input until 9.0, but they are no longer being maintained in the index
				// old data will be automatically converted to the new format upon write
				.extensionOf(null)
				.upgradeOf(null)
				// extensionOf and upgradeOf will be merged into the new dependency array when creating new resources
				.dependencies(mergedDependencies == null ? null : mergedDependencies.stream().map(Dependency::toDocument).collect(Collectors.toCollection(TreeSet::new)));
	}

	@Override
	@OverridingMethodsMustInvokeSuper
	protected void preExecute(final TransactionContext context) {
		checkOid(context);
		
		// perform dependency and branch checks last to avoid generating unnecessary branch data
		// Create branch if null or empty path was specified in the request
		final boolean createBranch = StringUtils.isEmpty(branchPath);
		
		final Optional<Version> extensionOfVersion = checkDependencies(context, createBranch);
		
		// Set the parent path if a branch needs to be created
		if (createBranch) {
			parentPath = extensionOfVersion
				.map(Version::getBranchPath)
				.orElse(Branch.MAIN_PATH); // TODO totally separate branching system?? MAIN could be removed
		}

		checkBranchPath(context, createBranch);
		
		// Set branchPath to the path of the created branch 
		if (createBranch) {
			branchPath = RepositoryRequests.branching()
				.prepareCreate()
				.setParent(parentPath)
				.setName(getId())
				.build(toolingId)
				.getRequest()
				.execute(context);
		}
	}
	
	@Override
	@OverridingMethodsMustInvokeSuper
	protected void checkParentCollection(TransactionContext context, Resource parentCollection) {
		if (parentCollection instanceof TerminologyResourceCollection resourceCollection) {
			// find the resource collection's tooling support for this child resource type
			TerminologyResourceCollectionToolingSupport toolingSupport = context.service(TerminologyResourceCollectionToolingSupport.Registry.class).getToolingSupport(resourceCollection.getToolingId(), getResourceType());
			
			// append parent collection as a dependency of this new resource
			final Dependency collectionDependency = Dependency.of(resourceCollection.getResourceURI());
			setDependencies(dependencies == null ? List.of(collectionDependency) : ImmutableList.<Dependency>builder().addAll(dependencies).add(collectionDependency).build());
			
			// in case of matching child resource type, validate and inherit settings
			inheritParentCollectionSettings(resourceCollection, toolingSupport.getInheritedSettingKeys());
		} else if (parentCollection instanceof Bundle) {
			// fetch all resources matching any of the current 
			List<String> nonRootAncestorResourceIds = Collections3.toImmutableList(parentCollection.getResourcePathSegments()).stream().filter(path -> !IComponent.ROOT_ID.equals(path)).toList();
			
			if (!nonRootAncestorResourceIds.isEmpty()) {
				// find and check the first matching terminology resource collection in the ancestor array (only one should exists)
				ResourceRequests.prepareSearch()
					.filterByResourceType(TerminologyResourceCollection.RESOURCE_TYPE)
					.filterByIds(nonRootAncestorResourceIds)
					.setLimit(nonRootAncestorResourceIds.size())
					.build()
					.execute(context)
					.stream()
					.filter(ancestorResource -> ancestorResource instanceof TerminologyResourceCollection)
					.findFirst()
					.ifPresent(ancestorTerminologyResourceCollection -> checkParentCollection(context, ancestorTerminologyResourceCollection));
			}
			
		} else {
			throw new BadRequestException("Selected parent resource '%s' is not a valid, recognizable collection resource.", parentCollection.getResourceURI());
		}
	}

	private void inheritParentCollectionSettings(TerminologyResourceCollection resourceCollection, Set<String> inheritedSettingKeys) {
		if (CompareUtils.isEmpty(resourceCollection.getSettings()) || CompareUtils.isEmpty(inheritedSettingKeys)) {
			return;
		}
		
		// take the filtered resource collection settings with the inherited keys and override it with the current settings if they are specified
		setSettings(Json.assign(Maps.filterKeys(resourceCollection.getSettings(), inheritedSettingKeys::contains), getSettings()));
	}
	
	private Optional<Version> checkDependencies(RepositoryContext context, boolean create) {
		Map<String, ResourceURIWithQuery> deprecatedDependencies = getDeprecatedDependencies();
		if (!deprecatedDependencies.isEmpty()) {
			// throw error if using both the old and the new way of attaching dependencies to a resource
			if (!CompareUtils.isEmpty(dependencies) && !Dependency.isEqual(dependencies, deprecatedDependencies)) {
				throw new BadRequestException("Using both deprecated dependency parameters (%s) and the new dependencies array is not supported. Stick to the old format or migrate to the new.", ImmutableSortedSet.copyOf(deprecatedDependencies.keySet()));
			}

			// merge old, deprecated dependencies into a single mergedDependencies List, detect duplicates and old API usage
			this.mergedDependencies = deprecatedDependencies.entrySet().stream().map(entry -> Dependency.of(entry.getValue(), entry.getKey())).toList(); 
		} else {
			this.mergedDependencies = dependencies;
		}
		
		// check dependency duplication first before fetching any data from the server
		checkDuplicateDependencies(context, mergedDependencies);
		
		checkDependenciesWithSpecialURIs(context, mergedDependencies);
		
		// check extensionOf dependency first, if configured
		Optional<Dependency> extensionOfDependency = mergedDependencies != null ? mergedDependencies.stream().filter(Dependency::isExtensionOf).findFirst() : Optional.empty();
		var extensionOfUri = extensionOfDependency.map(Dependency::getUri).orElse(null);
		Optional<Version> extensionOfVersion = Optional.empty(); 
		if (extensionOfUri != null) {
			if (extensionOfUri.isHead() || extensionOfUri.isLatest()) {
				throw new BadRequestException("Base terminology resource version was not expicitly given (can not be empty, "
					+ "LATEST or HEAD) in 'extensionOf' dependency '%s'.", extensionOfUri);
			}
			
			final String versionId = extensionOfUri.getResourceUri().getPath();
			
			extensionOfVersion = ResourceRequests.prepareSearchVersion()
					.one()
					.filterByResource(extensionOfUri.getResourceUri().withoutPath())
					.filterByVersionId(versionId)
					.build()
					.execute(context)
					.first();
			
			if (!extensionOfVersion.isPresent()) {
				throw new BadRequestException("Couldn't find base terminology resource version for 'extensionOf' dependency '%s'.", extensionOfUri);
			}
			
			// The working branch prefix is determined by the extensionOf code system version's path
			final String newResourceBranchPath = Branch.get(extensionOfVersion.get().getBranchPath(), getId());
			
			// CodeSystem Upgrade branches are managed by CodeSystemUpgradeRequest and they can have different paths than the usual extension branch paths, skip check
			Optional<Dependency> upgradeOfDependency = mergedDependencies != null ? mergedDependencies.stream().filter(Dependency::isUpgradeOf).findFirst() : Optional.empty();
			var upgradeOfUri = upgradeOfDependency.map(Dependency::getUri).orElse(null);
			
			if (upgradeOfUri == null && !create && !branchPath.equals(newResourceBranchPath)) {
				throw new BadRequestException("Branch path is inconsistent with 'extensionOf' dependency ('%s' given, should be '%s').", branchPath, newResourceBranchPath);
			}
		}

		// then check any other dependency reference if present
		checkNonExtensionOfDependencyReferences(context, mergedDependencies);
		
		return extensionOfVersion;
	}

	protected final Map<String, ResourceURIWithQuery> getDeprecatedDependencies() {
		final Map<String, ResourceURIWithQuery> deprecatedDependencies = new HashMap<>();
		
		if (extensionOf != null) {
			deprecatedDependencies.put(TerminologyResource.DependencyScope.EXTENSION_OF, ResourceURIWithQuery.of(extensionOf));
		}
		
		if (upgradeOf != null) {
			deprecatedDependencies.put(TerminologyResource.DependencyScope.UPGRADE_OF, ResourceURIWithQuery.of(upgradeOf));
		}
		
		collectDeprecatedDependencies(deprecatedDependencies);
		
		return deprecatedDependencies;
	}

	/**
	 * Subclasses may optionally override this method to provide any deprecated dependency fields they accepted pre-8.12.
	 * 
	 * @param deprecatedDependencies - the map to populate with deprecated dependency field keys and values
	 */
	protected void collectDeprecatedDependencies(Map<String, ResourceURIWithQuery> deprecatedDependencies) {
	}

	static void checkNonExtensionOfDependencyReferences(RepositoryContext context, List<Dependency> dependenciesToCheck) {
		// validate non-extensionOf dependency entries
		if (CompareUtils.isEmpty(dependenciesToCheck)) {
			return;
		}
			
		final Set<String> resourceReferencesToCheck = dependenciesToCheck.stream()
				.filter(dep -> !dep.isExtensionOf())
				.map(dep -> dep.getUri().getResourceUri().getResourceId())
				.collect(Collectors.toSet());
		
		
		// fetch all resources (TODO verify version references in a single search somehow)
		Set<String> existingResourceIds = ResourceRequests.prepareSearch()
			.filterByIds(resourceReferencesToCheck)
			.setLimit(resourceReferencesToCheck.size())
			.setFields(Resource.Fields.RESOURCE_TYPE, Resource.Fields.ID)
			.build()
			.execute(context)
			.stream()
			.map(Resource::getId)
			.collect(Collectors.toSet());

		Set<String> missingDependencies = Sets.difference(resourceReferencesToCheck, existingResourceIds);
		if (!missingDependencies.isEmpty()) {
			throw new BadRequestException("Some of the requested dependencies are not present in the system. Missing dependencies are: '%s'.", ImmutableSortedSet.copyOf(missingDependencies));
		}
	}

	static void checkDuplicateDependencies(RepositoryContext context, List<Dependency> dependenciesToCheck) {
		if (CompareUtils.isEmpty(dependenciesToCheck)) {
			return;
		}
		
		final Set<String> duplicateResourceIdReferences = dependenciesToCheck.stream()
				.map(dep -> dep.getUri().getResourceUri().getResourceId())
				.collect(Collectors.toCollection(HashMultiset::create))
				.entrySet()
				.stream()
				.filter(entry -> entry.getCount() > 1)
				.map(entry -> entry.getElement())
				.collect(Collectors.toSet());
		
		if (!duplicateResourceIdReferences.isEmpty()) {
			throw new BadRequestException("Some of the requested dependencies ('%s') are listed more than once. Correct the dependencies array and try again.", ImmutableSortedSet.copyOf(duplicateResourceIdReferences));
		}
	}
	
	static void checkDependenciesWithSpecialURIs(RepositoryContext context, List<Dependency> dependenciesToCheck) {
		if (CompareUtils.isEmpty(dependenciesToCheck)) {
			return;
		}
		
		ResourceURIPathResolver resolver = context.service(ResourceURIPathResolver.class);
		final List<ResourceURIWithQuery> resourceUrisWithSpecialPathSegment = dependenciesToCheck.stream()
				.filter(dep -> resolver.isSpecialURI(dep.getUri().getResourceUri()))
				.map(Dependency::getUri)
				.toList();
		
		if (!resourceUrisWithSpecialPathSegment.isEmpty()) {
			throw new BadRequestException("Some of the requested dependencies ('%s') are referencing a special URI path segment, which is forbidden when forming a dependency between two resources. Correct the dependencies array and try again.", resourceUrisWithSpecialPathSegment);
		}
	}

	private void checkBranchPath(final RepositoryContext context, final boolean create) {
		// If no branch is created, the branch should already exist
		if (!create && !branchExists(branchPath, context)) {
			throw new BadRequestException("Branch path '%s' should point to an existing branch if given.", branchPath);
		}
		
		// If the branch should be created, it should not exist already
		if (create) {
			final String newBranchPath = Branch.get(parentPath, getId());
			if (branchExists(newBranchPath, context)) {
				throw new AlreadyExistsException("CodeSystem Branch", newBranchPath);
			}
		}
	}

	private void checkOid(final RepositoryContext context) {
		// OID must be unique if defined
		if (!Strings.isNullOrEmpty(oid)) {
			/*
			 * Impersonate the system user to be able to look at all resources having the
			 * same OID, even the ones the requesting user does not have access to
			 */
			final RepositoryContext adminContext = context.inject()
				.bind(User.class, User.SYSTEM)
				.build();
			
			final boolean existingOid = ResourceRequests.prepareSearch()
					.setLimit(0)
					.filterByOid(oid)
					.build()
					.execute(adminContext)
					.getTotal() > 0;
			if (existingOid) {
				throw new AlreadyExistsException("Resource", "oid", oid);
			}
		}
	}
	
	private boolean branchExists(final String path, final ServiceProvider context) {
		Branches branches = RepositoryRequests.branching()
				.prepareSearch()
				.setLimit(1)
				.filterById(path)
				.build(toolingId)
				.getRequest()
				.execute(context);
		
		if (branches.isEmpty()) {
			return false;
		}
		
		return branches.stream().filter(b -> !b.isDeleted()).findFirst().isPresent();
		
	}
	
	protected final Repository validateAndGetToolingRepository(final ServiceProvider context) {
		// toolingId must be supported
		return context.service(RepositoryManager.class)
			.repositories()
			.stream()
			.filter(repository -> repository.id().equals(getToolingId()))
			.findFirst()
			.orElseThrow(() -> new BadRequestException("ToolingId '%s' is not supported by this server.", getToolingId()));
	}
	
}
