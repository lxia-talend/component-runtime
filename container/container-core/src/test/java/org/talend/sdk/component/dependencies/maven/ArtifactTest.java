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

package org.talend.sdk.component.dependencies.maven;

import static java.util.Arrays.asList;
import static org.junit.Assert.assertEquals;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

@RunWith(Parameterized.class)
public class ArtifactTest {

    @Parameterized.Parameters(name = "{0}")
    public static Iterable<Artifact> samples() {
        return asList(new Artifact("g", "a", "jar", null, "1", "compile"),
                new Artifact("g", "a", "jar", "c", "1", "compile"));
    }

    @Parameterized.Parameter
    public Artifact artifact;

    @Test
    public void toCoordinateFrom() {
        assertEquals(artifact, Artifact.from(artifact.toCoordinate()));
    }
}