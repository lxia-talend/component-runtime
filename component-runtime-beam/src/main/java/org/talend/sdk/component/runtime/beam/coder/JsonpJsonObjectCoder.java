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
package org.talend.sdk.component.runtime.beam.coder;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import javax.json.JsonObject;
import javax.json.JsonReader;
import javax.json.JsonReaderFactory;
import javax.json.JsonWriterFactory;

import org.apache.beam.sdk.coders.CustomCoder;
import org.apache.beam.sdk.util.VarInt;
import org.talend.sdk.component.runtime.beam.io.CountingOutputStream;
import org.talend.sdk.component.runtime.beam.io.NoCloseInputStream;
import org.talend.sdk.component.runtime.serialization.ContainerFinder;
import org.talend.sdk.component.runtime.serialization.LightContainer;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public class JsonpJsonObjectCoder extends CustomCoder<JsonObject> {

    public static JsonpJsonObjectCoder of(final String plugin) {
        return new JsonpJsonObjectCoder(plugin, null, null);
    }

    private final String plugin;

    private volatile JsonReaderFactory readerFactory; // ensure you reuse the same instance for memory management

    private volatile JsonWriterFactory writerFactory;

    @Override
    public void encode(final JsonObject jsonObject, final OutputStream outputStream) throws IOException {
        ensureInit();
        final CountingOutputStream buffer = new CountingOutputStream();
        writerFactory.createWriter(buffer).write(jsonObject);
        VarInt.encode(buffer.getCounter(), outputStream);
        outputStream.write(buffer.toByteArray());
    }

    @Override
    public JsonObject decode(final InputStream inputStream) throws IOException {
        ensureInit();
        try (final JsonReader reader =
                readerFactory.createReader(new NoCloseInputStream(inputStream, VarInt.decodeLong(inputStream)))) {
            return reader.readObject();
        }
    }

    private void ensureInit() {
        if (readerFactory == null) {
            synchronized (this) {
                if (readerFactory == null) {
                    final LightContainer container = ContainerFinder.Instance.get().find(plugin);
                    readerFactory = container.findService(JsonReaderFactory.class);
                    writerFactory = container.findService(JsonWriterFactory.class);
                }
            }
        }
    }
}