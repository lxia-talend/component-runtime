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
package org.talend.sdk.component.junit.http.internal.impl;

import org.talend.sdk.component.junit.http.api.HttpApiHandler;

import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpRequestDecoder;
import io.netty.handler.codec.http.HttpResponseEncoder;
import io.netty.handler.codec.http.HttpServerKeepAliveHandler;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import io.netty.handler.stream.ChunkedWriteHandler;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public class ProxyInitializer extends ChannelInitializer<SocketChannel> {

    private final HttpApiHandler api;

    @Override
    protected void initChannel(final SocketChannel channel) {
        final ChannelPipeline pipeline = channel.pipeline();
        pipeline
                .addLast("logging", new LoggingHandler(LogLevel.valueOf(api.getLogLevel())))
                .addLast("http-decoder", new HttpRequestDecoder())
                .addLast("http-keepalive", new HttpServerKeepAliveHandler())
                .addLast("aggregator", new HttpObjectAggregator(Integer.MAX_VALUE))
                .addLast("http-encoder", new HttpResponseEncoder())
                .addLast("chunked-writer", new ChunkedWriteHandler())
                .addLast("talend-junit-api-server",
                        !DefaultResponseLocatorCapturingHandler.isActive() ? new ServingProxyHandler(api)
                                : new DefaultResponseLocatorCapturingHandler(api));
    }
}