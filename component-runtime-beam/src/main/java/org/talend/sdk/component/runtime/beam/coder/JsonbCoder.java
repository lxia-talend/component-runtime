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
import java.lang.reflect.Type;

import javax.json.bind.Jsonb;

import org.apache.beam.sdk.coders.CustomCoder;
import org.apache.beam.sdk.util.VarInt;
import org.talend.sdk.component.runtime.beam.io.CountingOutputStream;
import org.talend.sdk.component.runtime.beam.io.NoCloseInputStream;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public class JsonbCoder<T> extends CustomCoder<T> {

    public static <T> JsonbCoder<T> of(final Class<T> type, final Jsonb jsonb) {
        return new JsonbCoder<>(type, jsonb);
    }

    public static JsonbCoder of(final Type type, final Jsonb jsonb) {
        return new JsonbCoder<>(type, jsonb);
    }

    private final Type type;

    private final Jsonb jsonb;

    @Override
    public void encode(final T object, final OutputStream outputStream) throws IOException {
        final CountingOutputStream buffer = new CountingOutputStream();
        jsonb.toJson(object, buffer);
        VarInt.encode(buffer.getCounter(), outputStream);
        outputStream.write(buffer.toByteArray());
        outputStream.flush();
    }

    @Override
    public T decode(final InputStream inputStream) throws IOException {
        return jsonb.fromJson(new NoCloseInputStream(inputStream, VarInt.decodeLong(inputStream)), type);
    }
}