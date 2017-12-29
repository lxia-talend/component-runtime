/**
 * Copyright (C) 2006-2017 Talend Inc. - www.talend.com
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
package org.talend.sdk.component.form.model;

import static javax.json.bind.config.PropertyOrderStrategy.LEXICOGRAPHICAL;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;
import static org.talend.sdk.component.form.model.Ui.ui;
import static org.talend.sdk.component.form.model.jsonschema.JsonSchema.jsonSchema;
import static org.talend.sdk.component.form.model.uischema.UiSchema.uiSchema;

import javax.json.bind.Jsonb;
import javax.json.bind.JsonbBuilder;
import javax.json.bind.JsonbConfig;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.talend.sdk.component.form.model.jsonschema.JsonSchema;

import lombok.Data;

public class UiTest {

    private Jsonb jsonb;

    @Before
    public void init() {
        jsonb = JsonbBuilder.create(new JsonbConfig().withPropertyOrderStrategy(LEXICOGRAPHICAL));
    }

    @After
    public void destroy() {
        try {
            jsonb.close();
        } catch (final Exception e) {
            fail(e.getMessage());
        }
    }

    @Test
    public void jsonSchemaTest() {
        final Ui form1 = ui()
                .withJsonSchema(jsonSchema()
                        .withType("object")
                        .withTitle("Comment")
                        .withProperty("lastname", jsonSchema().withType("string").build())
                        .withProperty("firstname", jsonSchema().withType("string").build())
                        .withProperty("age", jsonSchema().withType("number").build())
                        .build())
                .build();
        final String json = jsonb.toJson(form1);
        assertEquals("{\"jsonSchema\":{\"properties\":{\"firstname\":{\"type\":\"string\"},"
                + "\"age\":{\"type\":\"number\"},\"lastname\":{\"type\":\"string\"}},\"title\":\"Comment\","
                + "\"type\":\"object\"}}", json);
    }

    @Test
    public void uiSchemaTest() {
        final Ui form1 = ui()
                .withUiSchema(uiSchema()
                        .withKey("multiSelectTag")
                        .withRestricted(false)
                        .withTitle("Simple multiSelectTag")
                        .withDescription("This datalist accepts values that are not in the list of suggestions")
                        .withWidget("multiSelectTag")
                        .build())
                .build();
        final String json = jsonb.toJson(form1);
        assertEquals("{\"uiSchema\":[{\"description\":\"This datalist accepts values that are not in the list of "
                + "suggestions\",\"key\":\"multiSelectTag\",\"restricted\":false,\"title\":\"Simple multiSelectTag\","
                + "\"widget\":\"multiSelectTag\"}]}", json);
    }

    @Test
    public void propertiesTest() {
        final Ui form1 =
                ui().withJsonSchema(JsonSchema.jsonSchemaFrom(Form1.class).build()).withProperties(new Form1()).build();
        final String json = jsonb.toJson(form1);
        assertEquals("{\"jsonSchema\":{\"properties\":{\"name\":{\"type\":\"string\"}},\"title\":\"Form1\","
                + "\"type\":\"object\"},\"properties\":{\"name\":\"foo\"}}", json);
    }

    @Data
    public static class Form1 {

        private String name = "foo";
    }
}