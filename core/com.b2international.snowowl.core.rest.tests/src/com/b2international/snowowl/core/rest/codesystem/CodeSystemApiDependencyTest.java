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
package com.b2international.snowowl.core.rest.codesystem;

import static com.b2international.snowowl.test.commons.rest.CodeSystemApiAssert.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.hasItem;
import static org.junit.Assert.fail;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

import org.junit.Test;

import com.b2international.commons.exceptions.NotFoundException;
import com.b2international.commons.json.Json;
import com.b2international.snowowl.core.Dependency;
import com.b2international.snowowl.core.ResourceURI;
import com.b2international.snowowl.core.branch.Branch;
import com.b2international.snowowl.core.codesystem.CodeSystem;
import com.b2international.snowowl.core.codesystem.CodeSystems;
import com.b2international.snowowl.core.id.IDs;
import com.b2international.snowowl.core.repository.RepositoryRequests;
import com.b2international.snowowl.core.rest.BaseResourceApiTest;
import com.b2international.snowowl.test.commons.Services;
import com.google.common.collect.Maps;

/**
 * @since 8.12
 */
public class CodeSystemApiDependencyTest extends BaseResourceApiTest {

	@Test
	public void createWithExtensionOfOldModel() {
		final String parentCodeSystemId = "cs11";
		final Json parentRequestBody = prepareCodeSystemCreateRequestBody(parentCodeSystemId);
		createCodeSystem(parentRequestBody);
		assertCodeSystemGet(parentCodeSystemId).statusCode(200);
		
		final Json versionRequestBody = prepareVersionCreateRequestBody(CodeSystem.uri(parentCodeSystemId), "v1", "2020-04-15");
		assertVersionCreated(versionRequestBody).statusCode(201);

		final String codeSystemId = "cs12";
		
		final Json requestBody = prepareCodeSystemCreateRequestBody(codeSystemId)
				.without("branchPath")
				.with("extensionOf", CodeSystem.uri(parentCodeSystemId, "v1"));
		
		createCodeSystem(requestBody);
		
		final String expectedBranchPath = Branch.get(Branch.MAIN_PATH, parentCodeSystemId, "v1", codeSystemId);
		
		try {
			
			// Check if the branch has been created
			RepositoryRequests.branching()
				.prepareGet(expectedBranchPath)
				.build(TOOLING_ID)
				.execute(Services.bus())
				.getSync();
			
			assertCodeSystemGet(codeSystemId)
				.body("extensionOf", equalTo("codesystems/cs11/v1"));
			
		} catch (NotFoundException e) {
			fail("Branch " + expectedBranchPath + " did not get created as part of code system creation");
		}
	}
	
	@Test
	public void createWithExtensionOfNewModel() {
		final String parentCodeSystemId = "cs34";
		final Json parentRequestBody = prepareCodeSystemCreateRequestBody(parentCodeSystemId);
		createCodeSystem(parentRequestBody);
		assertCodeSystemGet(parentCodeSystemId).statusCode(200);
		
		final Json versionRequestBody = prepareVersionCreateRequestBody(CodeSystem.uri(parentCodeSystemId), "v1", "2023-07-14");
		assertVersionCreated(versionRequestBody).statusCode(201);

		final String codeSystemId = "cs35";
		
		final Json requestBody = prepareCodeSystemCreateRequestBody(codeSystemId)
				.without("branchPath")
				.with("dependencies", List.of(Dependency.of(CodeSystem.uri(parentCodeSystemId, "v1"), "extensionOf")));
		
		createCodeSystem(requestBody);
		
		final String expectedBranchPath = Branch.get(Branch.MAIN_PATH, parentCodeSystemId, "v1", codeSystemId);
		
		try {
			
			// Check if the branch has been created
			RepositoryRequests.branching()
				.prepareGet(expectedBranchPath)
				.build(TOOLING_ID)
				.execute(Services.bus())
				.getSync();
			
			// check extensionOf value comes back as part of both dependencies and extensionOf
			assertCodeSystemGet(codeSystemId)
				.statusCode(200)
				.body("extensionOf", equalTo("codesystems/cs34/v1"))
				.body("dependencies", hasItem(Map.of("uri", "codesystems/cs34/v1", "scope", "extensionOf")));
			
		} catch (NotFoundException e) {
			fail("Branch " + expectedBranchPath + " did not get created as part of code system creation");
		}
	}
	
	@Test
	public void updateExtensionOfOldModel() {
		final String parentCodeSystemId = "cs13";
		final Json parentRequestBody = prepareCodeSystemCreateRequestBody(parentCodeSystemId);
		createCodeSystem(parentRequestBody);
		assertCodeSystemGet(parentCodeSystemId).statusCode(200);
		
		final Json v3RequestBody = prepareVersionCreateRequestBody(CodeSystem.uri(parentCodeSystemId), "v3", "2020-04-16");
		assertVersionCreated(v3RequestBody).statusCode(201);
		final Json v4RequestBody = prepareVersionCreateRequestBody(CodeSystem.uri(parentCodeSystemId), "v4", "2020-04-17");
		assertVersionCreated(v4RequestBody).statusCode(201);
		
		final String codeSystemId = "cs14";
		final Json requestBody = prepareCodeSystemCreateRequestBody(codeSystemId)
				.without("branchPath")
				.with("extensionOf", CodeSystem.uri("cs13/v3"));
		
		createCodeSystem(requestBody);
		assertCodeSystemUpdated(codeSystemId, Json.object("extensionOf", CodeSystem.uri("cs13/v4")));
		
		final String expectedBranchPath = Branch.get(Branch.MAIN_PATH, "cs13", "v4", codeSystemId);
		assertCodeSystemHasAttributeValue(codeSystemId, "extensionOf", "codesystems/cs13/v4");
		assertCodeSystemHasAttributeValue(codeSystemId, "branchPath", expectedBranchPath);
	}
	
	@Test
	public void updateExtensionOf_NewModel() {
		final String parentCodeSystemId = "cs35";
		final Json parentRequestBody = prepareCodeSystemCreateRequestBody(parentCodeSystemId);
		createCodeSystem(parentRequestBody);
		assertCodeSystemGet(parentCodeSystemId).statusCode(200);
		
		final Json v3RequestBody = prepareVersionCreateRequestBody(CodeSystem.uri(parentCodeSystemId), "v3", "2020-04-16");
		assertVersionCreated(v3RequestBody).statusCode(201);
		final Json v4RequestBody = prepareVersionCreateRequestBody(CodeSystem.uri(parentCodeSystemId), "v4", "2020-04-17");
		assertVersionCreated(v4RequestBody).statusCode(201);
		
		final String codeSystemId = "cs36";
		final Json requestBody = prepareCodeSystemCreateRequestBody(codeSystemId)
				.without("branchPath")
				.with("dependencies", List.of(Dependency.of(CodeSystem.uri("cs35/v3"), "extensionOf")));
		
		createCodeSystem(requestBody);
		assertCodeSystemUpdated(codeSystemId, Json.object("dependencies", List.of(Dependency.of(CodeSystem.uri("cs35/v4"), "extensionOf"))));
		
		final String expectedBranchPath = Branch.get(Branch.MAIN_PATH, "cs35", "v4", codeSystemId);

		assertCodeSystemGet(codeSystemId)
			.statusCode(200)
			.body("extensionOf", equalTo("codesystems/cs35/v4"))
			.body("dependencies", hasItem(Map.of("uri", "codesystems/cs35/v4", "scope", "extensionOf")))
			.body("branchPath", equalTo(expectedBranchPath));	}
	
	@Test
	public void createExtensionOfDependency_NonExistentReference() {
		assertCodeSystemCreate(
			prepareCodeSystemCreateRequestBody("cs36")
				.with("dependencies", List.of(Dependency.of(CodeSystem.uri("nonexistent/v1"), "extensionOf")))
		).statusCode(400).body("message", equalTo("Couldn't find base terminology resource version for 'extensionOf' dependency 'codesystems/nonexistent/v1'."));
	}
	
	@Test
	public void createNonExtensionOfScope_NonExistentReference() {
		assertCodeSystemCreate(
			prepareCodeSystemCreateRequestBody("cs37")
				.with("dependencies", List.of(Dependency.of(CodeSystem.uri("nonexistent/v1"), "any_scope")))
		).statusCode(400).body("message", equalTo("Some of the requested dependencies are not present in the system. Missing dependencies are: '[nonexistent]'."));
	}
	
	@Test
	public void create_DuplicateReference_SameScope() {
		final String parentCodeSystemId = IDs.base62UUID();
		final Json parentRequestBody = prepareCodeSystemCreateRequestBody(parentCodeSystemId);
		createCodeSystem(parentRequestBody);
		assertCodeSystemGet(parentCodeSystemId).statusCode(200);
		
		final Json versionRequestBody = prepareVersionCreateRequestBody(CodeSystem.uri(parentCodeSystemId), "v1", "2020-04-16");
		assertVersionCreated(versionRequestBody).statusCode(201);
		
		assertCodeSystemCreate(
			prepareCodeSystemCreateRequestBody("duplicateDependencies_SameScope")
				.with("dependencies", List.of(Dependency.of(CodeSystem.uri(parentCodeSystemId, "v1"), "extensionOf"), Dependency.of(CodeSystem.uri(parentCodeSystemId, "v1"), "extensionOf")))
		).statusCode(400).body("message", equalTo("Some of the requested dependencies ('["+parentCodeSystemId+"]') are listed more than once. Correct the dependencies array and try again."));
	}
	
	@Test
	public void create_DuplicateReference_DifferentScope() {
		final String parentCodeSystemId = IDs.base62UUID();
		final Json parentRequestBody = prepareCodeSystemCreateRequestBody(parentCodeSystemId);
		createCodeSystem(parentRequestBody);
		assertCodeSystemGet(parentCodeSystemId).statusCode(200);
		
		final Json versionRequestBody = prepareVersionCreateRequestBody(CodeSystem.uri(parentCodeSystemId), "v1", "2020-04-16");
		assertVersionCreated(versionRequestBody).statusCode(201);
		
		assertCodeSystemCreate(
			prepareCodeSystemCreateRequestBody("duplicateDependencies_DiffScope")
			.with("dependencies", List.of(Dependency.of(CodeSystem.uri(parentCodeSystemId, "v1"), "extensionOf"), Dependency.of(CodeSystem.uri(parentCodeSystemId, "v1"), "someOtherScope")))
		).statusCode(400).body("message", equalTo("Some of the requested dependencies ('["+parentCodeSystemId+"]') are listed more than once. Correct the dependencies array and try again."));
	}
	
	@Test
	public void create_DuplicateReference_DifferentVersion() {
		final String parentCodeSystemId = IDs.base62UUID();
		final Json parentRequestBody = prepareCodeSystemCreateRequestBody(parentCodeSystemId);
		createCodeSystem(parentRequestBody);
		assertCodeSystemGet(parentCodeSystemId).statusCode(200);
		
		final Json versionRequestBody = prepareVersionCreateRequestBody(CodeSystem.uri(parentCodeSystemId), "v1", "2020-04-16");
		assertVersionCreated(versionRequestBody).statusCode(201);
		
		assertCodeSystemCreate(
			prepareCodeSystemCreateRequestBody("duplicateDependencies_DiffScope")
			.with("dependencies", List.of(Dependency.of(CodeSystem.uri(parentCodeSystemId, "v1"), "extensionOf"), Dependency.of(CodeSystem.uri(parentCodeSystemId, "v2"), "extensionOf")))
		).statusCode(400).body("message", equalTo("Some of the requested dependencies ('["+parentCodeSystemId+"]') are listed more than once. Correct the dependencies array and try again."));
	}
	
	@Test
	public void delete_resourceWithDependants() throws Exception {
		final String dependencyResourceId = IDs.base62UUID();
		final Json parentRequestBody = prepareCodeSystemCreateRequestBody(dependencyResourceId);
		createCodeSystem(parentRequestBody);
		
		final String dependantResourceId = IDs.base62UUID();
		assertCodeSystemCreate(
			prepareCodeSystemCreateRequestBody(dependantResourceId)
			.with("dependencies", List.of(Map.of("uri", CodeSystem.uri(dependencyResourceId))))
		).statusCode(201);
		
		assertCodeSystemDelete(dependencyResourceId)
			.statusCode(400)
			.body("message", equalTo("Resource '"+dependencyResourceId+"' is being referenced by other resources and it cannot be deleted. References are ["+CodeSystem.uri(dependantResourceId)+"]."));
	}
	
	@Test
	public void search_dependency() throws Exception {
		final String parentCodeSystemId = IDs.base62UUID();
		final Json parentRequestBody = prepareCodeSystemCreateRequestBody(parentCodeSystemId);
		createCodeSystem(parentRequestBody);
		
		final Json versionRequestBody = prepareVersionCreateRequestBody(CodeSystem.uri(parentCodeSystemId), "v1", "2020-04-16");
		assertVersionCreated(versionRequestBody).statusCode(201);
		
		final String codeSystemWithZeroDependencies = "codeSystemWithZeroDependencies";
		final String codeSystemWithOneDependency = "codeSystemWithOneDependency";
		final String codeSystemWithTwoDependencies = "codeSystemWithTwoDependencies";
		final String codeSystemWithHEADDependency = "codeSystemWithHEADDependency";
		
		assertCodeSystemCreate(
			prepareCodeSystemCreateRequestBody(codeSystemWithZeroDependencies)
			.with("dependencies", List.of())
		).statusCode(201);
		assertCodeSystemCreate(
			prepareCodeSystemCreateRequestBody(codeSystemWithOneDependency)
			.with("dependencies", List.of(
				Dependency.of(CodeSystem.uri(parentCodeSystemId, "v1"), "extensionOf")
			))
		).statusCode(201);
		assertCodeSystemCreate(
			prepareCodeSystemCreateRequestBody(codeSystemWithTwoDependencies)
			.with("dependencies", List.of(
				Dependency.of(CodeSystem.uri(parentCodeSystemId, "v1"), "source"),
				Dependency.of(CodeSystem.uri(codeSystemWithZeroDependencies), "target")
			))
		).statusCode(201);
		assertCodeSystemCreate(
			prepareCodeSystemCreateRequestBody(codeSystemWithHEADDependency)
			.with("dependencies", List.of(
				Dependency.of(CodeSystem.uri(parentCodeSystemId), "source")
			))
		).statusCode(201);
		
		// simple search, using URI only without any specific syntax
		CodeSystems matches = codeSystemSearch(Map.of(
			"dependency", CodeSystem.uri(parentCodeSystemId, "v1").toString()
		)).extract().as(CodeSystems.class);
		
		assertThat(matches)
			.extracting(CodeSystem::getId)
			.containsOnly(codeSystemWithOneDependency, codeSystemWithTwoDependencies);
		
		// explicit uri search
		matches = codeSystemSearch(Map.of(
			"dependency", "uri:" + CodeSystem.uri(codeSystemWithZeroDependencies).toString()
		)).extract().as(CodeSystems.class);
		
		assertThat(matches)
			.extracting(CodeSystem::getId)
			.containsOnly(codeSystemWithTwoDependencies);
		
		// scope search
		matches = codeSystemSearch(Map.of(
			"dependency", "scope:source"
		)).extract().as(CodeSystems.class);
		
		assertThat(matches)
			.extracting(CodeSystem::getId)
			.containsOnly(codeSystemWithHEADDependency, codeSystemWithTwoDependencies);
		
		// uri and scope search, match
		matches = codeSystemSearch(Map.of(
			"dependency", String.format("uri:%s AND scope:target", CodeSystem.uri(codeSystemWithZeroDependencies))
		)).extract().as(CodeSystems.class);
		
		assertThat(matches)
			.extracting(CodeSystem::getId)
			.containsOnly(codeSystemWithTwoDependencies);
	
		// uri and scope search, no match
		matches = codeSystemSearch(Map.of(
			"dependency", String.format("uri:%s AND scope:source", CodeSystem.uri(codeSystemWithZeroDependencies))
		)).extract().as(CodeSystems.class);
		
		assertThat(matches)
			.isEmpty();
		
		// uri regex match should return both versioned and unversioned dependencies
		matches = codeSystemSearch(Map.of(
			"dependency", String.format("uri:%s OR uri:%s/*", CodeSystem.uri(parentCodeSystemId), CodeSystem.uri(parentCodeSystemId))
		)).extract().as(CodeSystems.class);
		
		assertThat(matches)
			.extracting(CodeSystem::getId)
			.containsOnly(codeSystemWithOneDependency, codeSystemWithTwoDependencies, codeSystemWithHEADDependency);
	}
	
	@Test
	public void expandDependencyUpgrades() throws Exception {
		final String parentCodeSystemId = IDs.base62UUID();
		final Json parentRequestBody = prepareCodeSystemCreateRequestBody(parentCodeSystemId);
		createCodeSystem(parentRequestBody);
		
		assertVersionCreated(prepareVersionCreateRequestBody(CodeSystem.uri(parentCodeSystemId), "v1", "2020-04-16")).statusCode(201);
		
		assertVersionCreated(prepareVersionCreateRequestBody(CodeSystem.uri(parentCodeSystemId), "v2", "2021-04-16")).statusCode(201);
		
		final String codeSystemWithZeroDependencies = "codeSystemWithZeroDependencies";
		final String codeSystemWithOneDependency = "codeSystemWithOneDependency";
		final String codeSystemWithTwoDependencies = "codeSystemWithTwoDependencies";
		final String codeSystemWithHEADDependency = "codeSystemWithHEADDependency";
		
		assertCodeSystemCreate(
			prepareCodeSystemCreateRequestBody(codeSystemWithZeroDependencies)
			.with("dependencies", List.of())
		).statusCode(201);
		assertCodeSystemCreate(
			prepareCodeSystemCreateRequestBody(codeSystemWithOneDependency)
			.with("dependencies", List.of(
				Dependency.of(CodeSystem.uri(parentCodeSystemId, "v1"), "extensionOf")
			))
		).statusCode(201);
		assertCodeSystemCreate(
			prepareCodeSystemCreateRequestBody(codeSystemWithTwoDependencies)
			.with("dependencies", List.of(
				Dependency.of(CodeSystem.uri(parentCodeSystemId, "v1"), "source"),
				Dependency.of(CodeSystem.uri(codeSystemWithZeroDependencies), "target")
			))
		).statusCode(201);
		assertCodeSystemCreate(
			prepareCodeSystemCreateRequestBody(codeSystemWithHEADDependency)
			.with("dependencies", List.of(
				Dependency.of(CodeSystem.uri(parentCodeSystemId), "source")
			))
		).statusCode(201);
		
		CodeSystems codeSystems = codeSystemSearch(Map.of(
			"id", List.of(codeSystemWithZeroDependencies, codeSystemWithOneDependency, codeSystemWithTwoDependencies, codeSystemWithHEADDependency),
			"expand", "dependencies_upgrades()"
		)).extract().as(CodeSystems.class);

		var codeSystemsById = Maps.uniqueIndex(codeSystems, CodeSystem::getId);
		
		assertThat(codeSystemsById.get(codeSystemWithZeroDependencies).getDependencies())
			.flatExtracting(Dependency::getUpgrades)
			.isEmpty();
		
		assertThat(codeSystemsById.get(codeSystemWithOneDependency).getDependencies())
			.flatExtracting(Dependency::getUpgrades)
			.contains(CodeSystem.uri(parentCodeSystemId, "v2"));
		
		assertThat(codeSystemsById.get(codeSystemWithTwoDependencies).getDependencies())
			.flatExtracting(Dependency::getUpgrades)
			.contains(CodeSystem.uri(parentCodeSystemId, "v2"));
		
		assertThat(codeSystemsById.get(codeSystemWithHEADDependency).getDependencies())
			.flatExtracting(Dependency::getUpgrades)
			.isEmpty();
		
	}
	
	@Test
	public void preventUsingSpecialPathSegments() throws Exception {
		final String parentCodeSystemId = IDs.base62UUID();
		final Json parentRequestBody = prepareCodeSystemCreateRequestBody(parentCodeSystemId);
		createCodeSystem(parentRequestBody);
		
		assertVersionCreated(prepareVersionCreateRequestBody(CodeSystem.uri(parentCodeSystemId), "v1", "2020-04-16")).statusCode(201);
		
		ResourceURI latestVersionUri = CodeSystem.uri(parentCodeSystemId, "LATEST");
		assertCodeSystemCreate(
			prepareCodeSystemCreateRequestBody(IDs.base62UUID())
			.with("dependencies", List.of(
				Dependency.of(latestVersionUri, "extensionOf")
			))
		).statusCode(400)
		.body("message", equalTo("Some of the requested dependencies ('["+latestVersionUri.toString()+"]') are referencing a special URI path segment, which is forbidden when forming a dependency between two resources. Correct the dependencies array and try again."));
	}
	
	@Test
	public void updateCodeSystemPropertyWithUsingBothDependenciesAndSettings() throws Exception {
		final String parentCodeSystemId = IDs.base62UUID();
		final Json parentRequestBody = prepareCodeSystemCreateRequestBody(parentCodeSystemId);
		createCodeSystem(parentRequestBody);
		
		final Json versionRequestBody = prepareVersionCreateRequestBody(CodeSystem.uri(parentCodeSystemId), "v1", LocalDate.now().toString());
		assertVersionCreated(versionRequestBody).statusCode(201);
		
		final String codeSystemWithZeroDependencies = IDs.base62UUID();
		final String codeSystemWithTwoDependencies = IDs.base62UUID();
		
		assertCodeSystemCreate(
			prepareCodeSystemCreateRequestBody(codeSystemWithZeroDependencies)
		).statusCode(201);
		// create codesystem using settings, this will register dependency values in both settings and in dependency array
		assertCodeSystemCreate(
			prepareCodeSystemCreateRequestBody(codeSystemWithTwoDependencies)
			.with("settings", Map.of(
				"source", CodeSystem.uri(parentCodeSystemId, "v1").toString(),
				"target", CodeSystem.uri(codeSystemWithZeroDependencies)
			))
		).statusCode(201);
		
		// update status to active using the CodeSystem
		assertCodeSystemUpdated(codeSystemWithTwoDependencies, Json.object(
			"status", "something",
			"dependencies", List.of(Dependency.of(CodeSystem.uri(parentCodeSystemId, "v1"), "source"), Dependency.of(CodeSystem.uri(codeSystemWithZeroDependencies), "target")),
			"settings", Map.of(
				"source", CodeSystem.uri(parentCodeSystemId, "v1").toString(),
				"target", CodeSystem.uri(codeSystemWithZeroDependencies)
			)
		));
	}
}
