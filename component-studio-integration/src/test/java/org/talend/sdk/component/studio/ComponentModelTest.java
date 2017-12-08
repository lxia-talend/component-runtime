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
package org.talend.sdk.component.studio;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.Arrays;
import java.util.List;

import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;
import org.talend.core.model.general.ModuleNeeded;
import org.talend.core.model.process.INodeConnector;
import org.talend.core.model.process.INodeReturn;
import org.talend.core.model.temp.ECodePart;
import org.talend.sdk.component.server.front.model.ComponentDetail;
import org.talend.sdk.component.server.front.model.ComponentId;
import org.talend.sdk.component.server.front.model.ComponentIndex;

/**
 * Unit-tests for {@link ComponentModel}
 */
public class ComponentModelTest {

    @Test
    public void getModuleNeeded() {
        final ComponentId id = new ComponentId("id", "plugin", "group:plugin:1", "XML", "XMLInput");
        final ComponentIndex idx =
                new ComponentIndex(id, "XML Input", null, null, 1, Arrays.asList("Local", "File"), null);
        final ComponentDetail detail =
                new ComponentDetail(id, "XML Input", null, "Processor", 1, null, null, null, null, null);
        final ComponentModel componentModel = new ComponentModel(idx, detail);
        final List<ModuleNeeded> modulesNeeded = componentModel.getModulesNeeded();
        assertEquals(13, modulesNeeded.size());
        // just assert a few
        assertTrue(modulesNeeded.stream().anyMatch(
                m -> "component-runtime-manager-1.0.0-SNAPSHOT.jar".equals(m.getModuleName())));
        assertTrue(modulesNeeded.stream().anyMatch(
                m -> "component-runtime-impl-1.0.0-SNAPSHOT.jar".equals(m.getModuleName())));
        assertTrue(modulesNeeded.stream().anyMatch(
                m -> "component-runtime-di-1.0.0-SNAPSHOT.jar".equals(m.getModuleName())));
        assertTrue(modulesNeeded.stream().anyMatch(m -> "xbean-reflect-4.6.jar".equals(m.getModuleName())));
        assertTrue(modulesNeeded.stream().anyMatch(m -> "plugin-1.jar".equals(m.getModuleName())));
    }

    @Test
    public void testGetOriginalFamilyName() {
        String expectedFamilyName = "Local/XML|File/XML";

        ComponentId id = new ComponentId("id", "plugin", "plugin", "XML", "XMLInput");
        ComponentIndex idx = new ComponentIndex(id, "XML Input", null, null, 1, Arrays.asList("Local", "File"), null);
        ComponentDetail detail =
                new ComponentDetail(id, "XML Input", null, "Processor", 1, null, null, null, null, null);
        ComponentModel componentModel = new ComponentModel(idx, detail);

        Assert.assertEquals(expectedFamilyName, componentModel.getOriginalFamilyName());
    }

    @Test
    public void testGetLongName() {
        String expectedName = "XML Input";

        ComponentId id = new ComponentId("id", "plugin", "plugin", "XML", "XMLInput");
        ComponentIndex idx = new ComponentIndex(id, "XML Input", null, null, 1, Arrays.asList("Local", "File"), null);
        ComponentDetail detail =
                new ComponentDetail(id, "XML Input", null, "Processor", 1, null, null, null, null, null);
        ComponentModel componentModel = new ComponentModel(idx, detail);

        Assert.assertEquals(expectedName, componentModel.getLongName());
    }

    @Ignore("Cannot be tested as CorePlugin is null")
    @Test
    public void testCreateConnectors() {

        ComponentId id = new ComponentId("id", "plugin", "plugin", "XML", "XMLInput");
        ComponentIndex idx = new ComponentIndex(id, "XML Input", null, null, 1, Arrays.asList("Local", "File"), null);
        ComponentDetail detail =
                new ComponentDetail(id, "XML Input", null, "Processor", 1, null, null, null, null, null);
        ComponentModel componentModel = new ComponentModel(idx, detail);

        List<? extends INodeConnector> connectors = componentModel.createConnectors(null);
        Assert.assertEquals(22, connectors.size());
    }

    @Test
    public void testCreateReturns() {

        ComponentId id = new ComponentId("id", "plugin", "plugin", "XML", "XMLInput");
        ComponentIndex idx = new ComponentIndex(id, "XML Input", null, null, 1, Arrays.asList("Local", "File"), null);
        ComponentDetail detail =
                new ComponentDetail(id, "XML Input", null, "Processor", 1, null, null, null, null, null);
        ComponentModel componentModel = new ComponentModel(idx, detail);

        List<? extends INodeReturn> returnVariables = componentModel.createReturns(null);
        Assert.assertEquals(2, returnVariables.size());
        INodeReturn errorMessage = returnVariables.get(0);
        Assert.assertEquals("Error Message", errorMessage.getDisplayName());
        Assert.assertEquals("!!!NodeReturn.Availability.AFTER!!!", errorMessage.getAvailability());
        Assert.assertEquals("String", errorMessage.getType());
        INodeReturn numberLines = returnVariables.get(1);
        Assert.assertEquals("Number of line", numberLines.getDisplayName());
        Assert.assertEquals("!!!NodeReturn.Availability.AFTER!!!", numberLines.getAvailability());
        Assert.assertEquals("int | Integer", numberLines.getType());
    }

    @Test
    public void testGetAvailableProcessorCodeParts() {

        ComponentId id = new ComponentId("id", "plugin", "plugin", "XML", "XMLInput");
        ComponentIndex idx = new ComponentIndex(id, "XML Input", null, null, 1, Arrays.asList("Local", "File"), null);
        ComponentDetail detail =
                new ComponentDetail(id, "XML Input", null, "Processor", 1, null, null, null, null, null);
        ComponentModel componentModel = new ComponentModel(idx, detail);

        List<ECodePart> codeParts = componentModel.getAvailableCodeParts();
        Assert.assertEquals(3, codeParts.size());
        Assert.assertTrue(codeParts.contains(ECodePart.BEGIN));
        Assert.assertTrue(codeParts.contains(ECodePart.MAIN));
        Assert.assertTrue(codeParts.contains(ECodePart.FINALLY));
    }

    @Test
    public void testGetAvailableInputCodeParts() {

        ComponentId id = new ComponentId("id", "plugin", "plugin", "XML", "XMLInput");
        ComponentIndex idx = new ComponentIndex(id, "XML Input", null, null, 1, Arrays.asList("Local", "File"), null);
        ComponentDetail detail = new ComponentDetail(id, "XML Input", null, "Input", 1, null, null, null, null, null);
        ComponentModel componentModel = new ComponentModel(idx, detail);

        List<ECodePart> codeParts = componentModel.getAvailableCodeParts();
        Assert.assertEquals(3, codeParts.size());
        Assert.assertTrue(codeParts.contains(ECodePart.BEGIN));
        Assert.assertTrue(codeParts.contains(ECodePart.END));
        Assert.assertTrue(codeParts.contains(ECodePart.FINALLY));
    }
}