/*
 * Copyright 2023 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.springframework.boot.actuate.autoconfigure.metrics.export.prometheusnative;

import io.micrometer.core.instrument.Clock;
import io.micrometer.prometheusnative.PrometheusConfig;
import io.micrometer.prometheusnative.PrometheusMeterRegistry;
import io.prometheus.metrics.model.registry.PrometheusRegistry;
import org.springframework.boot.actuate.autoconfigure.endpoint.condition.ConditionalOnAvailableEndpoint;
import org.springframework.boot.actuate.autoconfigure.metrics.CompositeMeterRegistryAutoConfiguration;
import org.springframework.boot.actuate.autoconfigure.metrics.MetricsAutoConfiguration;
import org.springframework.boot.actuate.autoconfigure.metrics.export.ConditionalOnEnabledMetricsExport;
import org.springframework.boot.actuate.autoconfigure.metrics.export.simple.SimpleMetricsExportAutoConfiguration;
import org.springframework.boot.actuate.metrics.export.prometheus.PrometheusNativeScrapeEndpoint;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@AutoConfiguration(
		before = { CompositeMeterRegistryAutoConfiguration.class, SimpleMetricsExportAutoConfiguration.class },
		after = MetricsAutoConfiguration.class)
@ConditionalOnClass(PrometheusMeterRegistry.class) // PrometheusMeterRegistry from the
													// micrometer-registry-prometheus_native
													// runtime dependency
@ConditionalOnEnabledMetricsExport("prometheus")
@EnableConfigurationProperties(PrometheusNativeProperties.class)
public class PrometheusNativeMetricsExportAutoConfiguration {

	@Bean
	@ConditionalOnMissingBean
	public PrometheusConfig prometheusNativeConfig(PrometheusNativeProperties prometheusProperties) {
		return new PrometheusNativePropertiesConfigAdapter(prometheusProperties);
	}

	@Bean
	@ConditionalOnMissingBean
	public PrometheusMeterRegistry prometheusNativeMeterRegistry(PrometheusConfig prometheusConfig,
			PrometheusRegistry registry, Clock clock) {
		return new PrometheusMeterRegistry(prometheusConfig, registry, clock);
	}

	@Bean
	@ConditionalOnMissingBean
	public PrometheusRegistry prometheusNativeRegistry() {
		return new PrometheusRegistry();
	}

	@Configuration(proxyBeanMethods = false)
	@ConditionalOnAvailableEndpoint(endpoint = PrometheusNativeScrapeEndpoint.class)
	public static class PrometheusNativeScrapeEndpointConfiguration {

		@Bean
		@ConditionalOnMissingBean
		public PrometheusNativeScrapeEndpoint prometheusNativeEndpoint(PrometheusRegistry registry) {
			return new PrometheusNativeScrapeEndpoint(registry,
					io.prometheus.metrics.config.PrometheusProperties.get());
		}

	}

}
