/**
 * Copyright (C) 2006-2018 Talend Inc. - www.talend.com
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.talend.sdk.component.form.internal.converter.impl.widget;

import static java.util.Collections.emptyList;
import static java.util.stream.Collectors.toList;

import java.util.Collection;
import java.util.concurrent.CompletionStage;

import org.talend.sdk.component.form.internal.converter.PropertyContext;
import org.talend.sdk.component.form.model.jsonschema.JsonSchema;
import org.talend.sdk.component.form.model.uischema.UiSchema;
import org.talend.sdk.component.server.front.model.ActionReference;
import org.talend.sdk.component.server.front.model.SimplePropertyDefinition;

public class DataListWidgetConverter extends AbstractWidgetConverter {

    public DataListWidgetConverter(final Collection<UiSchema> schemas,
            final Collection<SimplePropertyDefinition> properties, final Collection<ActionReference> actions) {
        super(schemas, properties, actions);
    }

    @Override
    public CompletionStage<PropertyContext> convert(final CompletionStage<PropertyContext> cs) {
        return cs.thenApply(context -> {
            final UiSchema schema = newUiSchema(context);
            schema.setWidget("datalist");

            final JsonSchema jsonSchema = new JsonSchema();
            jsonSchema.setType("string");
            schema.setSchema(jsonSchema);

            if (context.getProperty().getValidation().getEnumValues() != null) {
                schema.setTitleMap(context.getProperty().getProposalDisplayNames() != null
                        ? context.getProperty().getProposalDisplayNames().entrySet().stream().map(v -> {
                            final UiSchema.NameValue nameValue = new UiSchema.NameValue();
                            nameValue.setName(v.getKey());
                            nameValue.setValue(v.getValue());
                            return nameValue;
                        }).collect(toList())
                        : context.getProperty().getValidation().getEnumValues().stream().sorted().map(v -> {
                            final UiSchema.NameValue nameValue = new UiSchema.NameValue();
                            nameValue.setName(v);
                            nameValue.setValue(v);
                            return nameValue;
                        }).collect(toList()));
                jsonSchema.setEnumValues(context.getProperty().getValidation().getEnumValues());
            } else {
                schema.setTitleMap(emptyList());
                jsonSchema.setEnumValues(emptyList());
            }
            return context;
        });
    }
}
