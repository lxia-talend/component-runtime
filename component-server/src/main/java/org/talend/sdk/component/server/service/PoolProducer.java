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
package org.talend.sdk.component.server.service;

import static java.util.concurrent.TimeUnit.MILLISECONDS;

import java.time.Duration;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Disposes;
import javax.enterprise.inject.Produces;

import org.apache.commons.lang3.concurrent.BasicThreadFactory;
import org.talend.sdk.component.server.configuration.ComponentServerConfiguration;

import brave.http.HttpTracing;

@ApplicationScoped
public class PoolProducer {

    @Produces
    @ApplicationScoped
    public ExecutorService executorService(final ComponentServerConfiguration configuration,
            final HttpTracing tracing) {
        return tracing.tracing().currentTraceContext().executorService(Executors.newFixedThreadPool(
                configuration.executionPoolSize(),
                new BasicThreadFactory.Builder().namingPattern("talend-component-server-%d").daemon(false).build()));
    }

    public void release(@Disposes final ExecutorService executorService,
            final ComponentServerConfiguration configuration) {
        final long timeout = Duration.parse(configuration.executionPoolShutdownTimeout()).toMillis();
        executorService.shutdown();
        try {
            if (!executorService.awaitTermination(timeout, MILLISECONDS)) {
                executorService.shutdownNow();
            }
        } catch (final InterruptedException e) {
            Thread.interrupted();
        }
    }
}
