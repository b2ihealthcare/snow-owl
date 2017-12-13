/*******************************************************************************
 * Copyright (c) 2017 B2i Healthcare. All rights reserved.
 *******************************************************************************/
package com.b2international.snowowl.core.validation;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.UUID;
import java.util.stream.Collectors;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.b2international.index.Index;
import com.b2international.index.Indexes;
import com.b2international.index.mapping.Mappings;
import com.b2international.snowowl.core.ComponentIdentifier;
import com.b2international.snowowl.core.IDisposableService;
import com.b2international.snowowl.core.ServiceProvider;
import com.b2international.snowowl.core.internal.validation.ValidationRepository;
import com.b2international.snowowl.core.validation.whitelist.ValidationWhiteList;
import com.b2international.snowowl.core.validation.whitelist.ValidationWhiteLists;
import com.b2international.snowowl.datastore.server.internal.JsonSupport;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * @since 6.1
 */
public class ValidationWhiteListApiTest {

	private ServiceProvider context;
	
	@Before
	public void setup() {
		final ObjectMapper mapper = JsonSupport.getDefaultObjectMapper();
		final Index index = Indexes.createIndex(UUID.randomUUID().toString(), mapper, new Mappings(ValidationWhiteList.class));
		index.admin().create();
		final ValidationRepository repository = new ValidationRepository(index);
		context = ServiceProvider.EMPTY.inject()
				.bind(ValidationRepository.class, repository)
				.build();
	}
	
	@After
	public void after() {
		context.service(ValidationRepository.class).admin().delete();
		if(context instanceof IDisposableService) {
			((IDisposableService) context).dispose();
		}
	}
	
	@Test
	public void createWhiteList() throws Exception {
		final String whiteListId = generateWhiteList("58");
		
		assertThat(whiteListId).isNotEmpty();
		
		final ValidationWhiteList whiteList = ValidationRequests.whiteList().prepareGet(whiteListId).buildAsync().getRequest().execute(context);
		assertThat(whiteList.getRuleId()).isEqualTo("58");
		assertThat(whiteList.getComponentIdentifier()).isEqualTo(ComponentIdentifier.unknown());
	}

	@Test
	public void deleteWhiteList() throws Exception {
		final String whiteList1 = generateWhiteList("1");
		final String whiteList2 = generateWhiteList("2");
		
		ValidationRequests.whiteList().prepareDelete(whiteList2).buildAsync().getRequest().execute(context);
		
		final ValidationWhiteLists WhiteLists = getAllWhiteLists();
		
		assertThat(WhiteLists.stream().map(ValidationWhiteList::getId).collect(Collectors.toList())).doesNotContain(whiteList2);
		assertThat(WhiteLists.getItems().size()).isEqualTo(1);
	}

	@Test
	public void filterByRuleId() throws Exception{
		generateWhiteList("3");
		generateWhiteList("4");
		
		final ValidationWhiteLists whiteLists = ValidationRequests.whiteList().prepareSearch()
			.filterByRuleId("3")
			.buildAsync().getRequest()
			.execute(context);
	
		assertThat(whiteLists).hasSize(1);
		assertThat(whiteLists.first().get().getRuleId()).isEqualTo("3");
	}
	
	@Test
	public void filterByComponentIdentifier() throws Exception{
		final ComponentIdentifier componentIdentifier = ComponentIdentifier.of((short) 100, "12345678");
		
		generateWhiteList("5");
		generateWhiteList("6", componentIdentifier);
		
		final ValidationWhiteLists whiteLists = ValidationRequests.whiteList().prepareSearch()
			.filterByComponentIdentifier(componentIdentifier)
			.buildAsync().getRequest()
			.execute(context);
		
		assertThat(whiteLists).hasSize(1);
		assertThat(whiteLists.first().get().getComponentIdentifier().getComponentId()).isEqualTo(componentIdentifier.getComponentId());
	}
	
	@Test
	public void getAllValidationWhiteLists() throws Exception {
		final ValidationWhiteLists validationWhiteLists = getAllWhiteLists();
		assertThat(validationWhiteLists).isEmpty();
	}
	
	
	private String generateWhiteList(final String ruleId) throws Exception {
		return generateWhiteList(ruleId, ComponentIdentifier.unknown());
	}
	
	private String generateWhiteList(final String ruleId, final ComponentIdentifier componentIdentifier) {
		return ValidationRequests.whiteList().prepareCreate()
			.setId(UUID.randomUUID().toString())
			.setRuleId(ruleId)
			.setComponentIdentifier(componentIdentifier)
			.buildAsync().getRequest()
			.execute(context);
	}
	
	private ValidationWhiteLists getAllWhiteLists() throws Exception {
		return ValidationRequests.whiteList().prepareSearch()
			.all()
			.buildAsync().getRequest()
			.execute(context);
	}
	
}