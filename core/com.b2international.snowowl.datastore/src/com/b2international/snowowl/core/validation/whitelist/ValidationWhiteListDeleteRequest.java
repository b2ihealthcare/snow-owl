package com.b2international.snowowl.core.validation.whitelist;

import com.b2international.snowowl.core.ServiceProvider;
import com.b2international.snowowl.core.events.Request;
import com.b2international.snowowl.core.internal.validation.ValidationRepository;

final class ValidationWhiteListDeleteRequest implements Request<ServiceProvider, Boolean> {

	private final String id;

	ValidationWhiteListDeleteRequest(final String id) {
		this.id = id;
	}
	
	@Override
	public Boolean execute(ServiceProvider context) {
		context.service(ValidationRepository.class).remove(ValidationWhiteList.class, id);
		return Boolean.TRUE;
	}

}
