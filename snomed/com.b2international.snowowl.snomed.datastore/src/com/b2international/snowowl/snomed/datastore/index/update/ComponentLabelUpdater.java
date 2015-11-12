package com.b2international.snowowl.snomed.datastore.index.update;

import static com.google.common.base.Preconditions.checkNotNull;

import javax.annotation.OverridingMethodsMustInvokeSuper;

import com.b2international.snowowl.datastore.index.DocumentUpdaterBase;
import com.b2international.snowowl.datastore.index.mapping.DocumentBuilderBase;

/**
 * @since 4.3 
 * @param <D> - the type of the {@link DocumentBuilderBase}
 */
public class ComponentLabelUpdater<D extends DocumentBuilderBase<D>> extends DocumentUpdaterBase<D> {

	private String label;

	public ComponentLabelUpdater(String componentId, String label) {
		super(componentId);
		this.label = label;
	}

	@Override
	public final void doUpdate(D doc) {
		updateLabelFields(doc, getLabel());
	}
	
	@OverridingMethodsMustInvokeSuper
	protected void updateLabelFields(D doc, String label) {
		checkNotNull(label, "Label shouldn't be null %s", getComponentId());
		doc.label(label);
	}

	protected String getLabel() {
		return label;
	}

}
