/*******************************************************************************
 * Copyright 2011-2015 B2i Healthcare Pte Ltd, http://b2i.sg
 *******************************************************************************/
package com.b2international.snowowl.test.commons;

import static com.google.common.base.Preconditions.checkNotNull;

import org.eclipse.core.runtime.Platform;
import org.junit.Rule;
import org.junit.rules.ExternalResource;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Manages a bundle lifecycle in the lifecycle of a JUnit {@link Rule}, meaning it starts the specified bundle when the rule {@link #before()} method
 * called and stops the bundle when {@link #after()} called.
 * 
 * @since 3.7
 */
public class BundleStartRule extends ExternalResource {

	private static final Logger LOG = LoggerFactory.getLogger(BundleStartRule.class);
	private Bundle bundle;

	public BundleStartRule(String symbolicName) {
		this.bundle = checkNotNull(Platform.getBundle(symbolicName), "Bundle not found for %s", symbolicName);
	}

	@Override
	protected void before() throws Throwable {
		super.before();
		LOG.info("Starting bundle: {}", this.bundle.getSymbolicName());
		bundle.start();
	}

	@Override
	protected void after() {
		super.after();
		try {
			LOG.info("Stopping bundle: {}", this.bundle.getSymbolicName());
			bundle.stop();
		} catch (BundleException e) {
			throw new RuntimeException(e);
		}
	}

}
