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

import static java.util.Collections.singletonList;
import static java.util.Locale.ROOT;
import static java.util.Optional.ofNullable;
import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toSet;

import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Stream;

import org.talend.sdk.component.form.internal.converter.PropertyContext;
import org.talend.sdk.component.form.internal.converter.PropertyConverter;
import org.talend.sdk.component.form.model.uischema.UiSchema;
import org.talend.sdk.component.server.front.model.ActionReference;
import org.talend.sdk.component.server.front.model.SimplePropertyDefinition;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public abstract class AbstractWidgetConverter implements PropertyConverter {

    protected final Collection<UiSchema> schemas;

    protected final Collection<SimplePropertyDefinition> properties;

    protected final Collection<ActionReference> actions;

    protected UiSchema.Trigger toTrigger(final Collection<SimplePropertyDefinition> properties,
            final SimplePropertyDefinition prop, final ActionReference ref) {
        final UiSchema.Trigger trigger = new UiSchema.Trigger();
        trigger.setAction(ref.getName());
        trigger.setFamily(ref.getFamily());
        trigger.setType(ref.getType());
        trigger.setParameters(
                toParams(properties, prop, ref, prop.getMetadata().get("action::" + ref.getType() + "::parameters")));
        return trigger;
    }

    protected List<UiSchema.Parameter> toParams(final Collection<SimplePropertyDefinition> properties,
            final SimplePropertyDefinition prop, final ActionReference ref, final String parameters) {
        final Iterator<SimplePropertyDefinition> expectedProperties = ref.getProperties().iterator();
        return ofNullable(parameters).map(params -> Stream.of(params.split(",")).flatMap(paramRef -> {
            if (!expectedProperties.hasNext()) {
                return Stream.empty();
            }
            final String parameterPrefix = expectedProperties.next().getPath();
            final String propertiesPrefix = resolveProperty(prop, paramRef);
            final List<UiSchema.Parameter> resolvedParams = properties
                    .stream()
                    .filter(p -> p.getPath().startsWith(propertiesPrefix))
                    .filter(o -> !"object".equalsIgnoreCase(o.getType()) && !"array".equalsIgnoreCase(o.getType()))
                    .map(o -> {
                        final UiSchema.Parameter parameter = new UiSchema.Parameter();
                        final String key = parameterPrefix + o.getPath().substring(propertiesPrefix.length());
                        parameter.setKey(key.replace("[]", "")); // not a jsonpath otherwise
                        parameter.setPath(o.getPath().replace("[]", ""));
                        return parameter;
                    })
                    .collect(toList());
            if (resolvedParams.isEmpty()) {
                throw new IllegalArgumentException("No resolved parameters for " + prop.getPath() + " in "
                        + ref.getFamily() + "/" + ref.getType() + "/" + ref.getName());
            }
            return resolvedParams.stream();
        }).collect(toList())).orElse(null);
    }

    // ensure it is aligned with org.talend.sdk.component.studio.model.parameter.SettingsCreator.computeTargetPath()
    private String resolveProperty(final SimplePropertyDefinition prop, final String paramRef) {
        if (".".equals(paramRef)) {
            return prop.getPath();
        }
        if (paramRef.startsWith("..")) {
            String current = prop.getPath();
            String ref = paramRef;
            while (ref.startsWith("..")) {
                final int lastDot = current.lastIndexOf('.');
                if (lastDot < 0) {
                    break;
                }
                current = current.substring(0, lastDot);
                ref = ref.substring("..".length(), ref.length());
                if (ref.startsWith("/")) {
                    ref = ref.substring(1);
                }
            }
            return current + (!ref.isEmpty() ? "." : "") + ref.replace('/', '.');
        }
        if (paramRef.startsWith(".") || paramRef.startsWith("./")) {
            return prop.getPath() + '.' + paramRef.replaceFirst("\\./?", "").replace('/', '/');
        }
        return paramRef;
    }

    protected UiSchema newUiSchema(final PropertyContext ctx) {
        final UiSchema schema = newOrphanSchema(ctx);
        synchronized (schemas) {
            schemas.add(schema);
        }
        return schema;
    }

    protected UiSchema newOrphanSchema(final PropertyContext ctx) {
        final UiSchema schema = new UiSchema();
        schema.setTitle(ctx.getProperty().getDisplayName());
        schema.setKey(ctx.getProperty().getPath());
        schema.setRequired(ctx.isRequired());
        schema.setPlaceholder(ctx.getProperty().getPlaceholder());
        if (actions != null) {
            ofNullable(ctx.getProperty().getMetadata().get("action::validation"))
                    .flatMap(v -> actions
                            .stream()
                            .filter(a -> a.getName().equals(v) && "validation".equals(a.getType()))
                            .findFirst())
                    .ifPresent(ref -> schema.setTriggers(singletonList(toTrigger(properties, ctx.getProperty(), ref))));
        }
        schema.setConditions(createConditions(ctx));
        return schema;
    }

    protected List<UiSchema.Condition> createConditions(final PropertyContext ctx) {
        return ctx
                .getProperty()
                .getMetadata()
                .entrySet()
                .stream()
                .filter(e -> e.getKey().startsWith("condition::if::target"))
                .map(e -> {
                    final String[] split = e.getKey().split("::");
                    final String valueKey =
                            "condition::if::value" + (split.length == 4 ? "::" + split[split.length - 1] : "");
                    final String path = resolveProperty(ctx.getProperty(),
                            (!e.getValue().contains(".") ? "../" : "") + e.getValue());
                    final SimplePropertyDefinition definition =
                            properties.stream().filter(p -> p.getPath().equals(path)).findFirst().orElse(null);
                    final Function<String, Object> converter = findStringValueMapper(definition);
                    return new UiSchema.Condition.Builder()
                            .withPath(path)
                            .withValues(Stream
                                    .of(ctx.getProperty().getMetadata().getOrDefault(valueKey, "true").split(","))
                                    .map(converter)
                                    .collect(toSet()))
                            .build();
                })
                .collect(toList());
    }

    private Function<String, Object> findStringValueMapper(final SimplePropertyDefinition definition) {
        if (definition == null) {
            return s -> s;
        }
        switch (definition.getType().toLowerCase(ROOT)) {
        case "boolean":
            return Boolean::parseBoolean;
        case "number":
            return Double::parseDouble;

        // assume object and array are not supported
        default:
            return s -> s;
        }
    }
}
