/**
 * Copyright (C) 2006-2018 Talend Inc. - www.talend.com
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on
 * an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations under the License.
 */
package org.talend.sdk.component.studio.ui.composite;

import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.widgets.Composite;
import org.talend.commons.exception.ExceptionHandler;
import org.talend.core.model.process.EComponentCategory;
import org.talend.core.model.process.Element;
import org.talend.sdk.component.studio.metadata.TaCoKitElementParameter;
import org.talend.sdk.component.studio.metadata.TaCoKitElementParameter.IValueChangedListener;
import org.talend.sdk.component.studio.metadata.model.TaCoKitConfigurationModel;
import org.talend.sdk.component.studio.metadata.model.TaCoKitConfigurationModel.ValueModel;

/**
 * Stores {@link TaCoKitConfigurationModel} and sets Configuration Model Updater listener for each
 * {@link TaCoKitElementParameter}
 */
public class TaCoKitWizardComposite extends TaCoKitComposite {

    private TaCoKitConfigurationModel configurationModel;

    private IValueChangedListener configurationUpdater;

    public TaCoKitWizardComposite(final Composite parentComposite, final int styles, final EComponentCategory section,
            final Element element, final TaCoKitConfigurationModel model, final boolean isCompactView,
            final Color backgroundColor) {
        super(parentComposite, styles, section, element, isCompactView, backgroundColor);
        this.configurationModel = model;
        configurationUpdater = new ConfigurationModelUpdater();
        init();
    }

    private void init() {

        elem
                .getElementParameters()
                .stream()
                .filter(p -> p instanceof TaCoKitElementParameter)
                .map(p -> (TaCoKitElementParameter) p)
                .forEach(parameter -> {
                    parameter.addValueChangeListener(configurationUpdater);
                    try {
                        ValueModel valueModel = configurationModel.getValue(parameter.getName());
                        if (valueModel != null) {
                            if (valueModel.getConfigurationModel() != configurationModel) {
                                parameter.setReadOnly(true);
                            }
                            parameter.setValue(valueModel.getValue());
                        }
                    } catch (Exception e) {
                        ExceptionHandler.process(e);
                    }
                });
    }

    /**
     * Removes {@link ConfigurationModelUpdater} from every {@link TaCoKitElementParameter}, where it was registered
     */
    @Override
    public synchronized void dispose() {
        elem
                .getElementParameters()
                .stream()
                .filter(p -> p instanceof TaCoKitElementParameter)
                .map(p -> (TaCoKitElementParameter) p)
                .forEach(p -> p.removeValueChangeListener(configurationUpdater));
        super.dispose();
    }

    private class ConfigurationModelUpdater implements IValueChangedListener {

        /**
         * Updates {@link TaCoKitConfigurationModel} each time some of {@link TaCoKitElementParameter} is changed
         * 
         * @param elementParameter changed {@link TaCoKitElementParameter}
         * @param oldValue parameter old value
         * @param newValue parameter new value
         */
        @Override
        public void onValueChanged(final TaCoKitElementParameter elementParameter, final Object oldValue,
                final Object newValue) {
            configurationModel.setValue(elementParameter.getName(), newValue);
        }

    }

}