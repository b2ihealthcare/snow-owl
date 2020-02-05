package com.b2international.snowowl.fhir.core;

import org.osgi.framework.Bundle;
import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;

public class FhirCoreActivator implements BundleActivator {

	private static FhirCoreActivator activator;
	
	private BundleContext context;
	
	/**
	 * Get the default activator.
	 *
	 * @return a BundleActivator
	 */
	public static FhirCoreActivator getDefault() {
		return activator;
	}
	
	/**
	 * @return the bundle object
	 */
	public Bundle getBundle() {
		return context.getBundle();
	}

	@Override
	public void start(BundleContext context) throws Exception {
		activator = this;
		this.context = context;

	}

	@Override
	public void stop(BundleContext context) throws Exception {
		// TODO Auto-generated method stub

	}

}
