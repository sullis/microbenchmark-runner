/*
 * Copyright 2018-2019 the original author or authors.
 *
 * All rights reserved. This program and the accompanying materials are
 * made available under the terms of the Eclipse Public License v2.0 which
 * accompanies this distribution and is available at
 *
 * http://www.eclipse.org/legal/epl-v20.html
 */
package jmh.mbr.junit5.execution;

import static org.assertj.core.api.Assertions.*;

import java.util.Collections;
import java.util.Optional;

import jmh.mbr.core.model.BenchmarkClass;
import jmh.mbr.junit5.descriptor.BenchmarkClassDescriptor;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.EnabledOnOs;
import org.junit.jupiter.api.condition.OS;
import org.junit.jupiter.api.extension.ConditionEvaluationResult;
import org.junit.jupiter.engine.extension.ExtensionRegistry;
import org.junit.platform.engine.ConfigurationParameters;
import org.junit.platform.engine.UniqueId;
import org.openjdk.jmh.annotations.Benchmark;

/**
 * @author Mark Paluch
 */
public class ConditionEvaluatorUnitTests {

	@Test
	void shouldRunWithoutCondition() {

		ExtensionRegistry registry = ExtensionRegistry.createRegistryWithDefaultExtensions(EmptyConfigurationParameters.INSTANCE);

		BenchmarkClassDescriptor descriptor = createDescriptor(SimpleBenchmarkClass.class);

		ConditionEvaluator evaluator = new ConditionEvaluator();
		ConditionEvaluationResult result = evaluator.evaluate(registry, descriptor.getExtensionContext(null, null, EmptyConfigurationParameters.INSTANCE));

		assertThat(result.isDisabled()).isFalse();
	}

	@Test
	void shouldSkipDisabledBenchmarkClass() {

		ExtensionRegistry registry = ExtensionRegistry.createRegistryWithDefaultExtensions(EmptyConfigurationParameters.INSTANCE);

		BenchmarkClassDescriptor descriptor = createDescriptor(DisabledBenchmark.class);

		ConditionEvaluator evaluator = new ConditionEvaluator();
		ConditionEvaluationResult result = evaluator.evaluate(registry, descriptor.getExtensionContext(null, null, EmptyConfigurationParameters.INSTANCE));

		assertThat(result.isDisabled()).isTrue();
	}

	@Test
	void shouldSkipDisabledThroughExtensionClass() {

		ExtensionRegistry parentRegistry = ExtensionRegistry.createRegistryWithDefaultExtensions(EmptyConfigurationParameters.INSTANCE);

		BenchmarkClassDescriptor descriptor = createDescriptor(AixBenchmark.class);
		ExtensionRegistry registry = descriptor.getExtensionRegistry(parentRegistry);

		ConditionEvaluator evaluator = new ConditionEvaluator();
		ConditionEvaluationResult result = evaluator.evaluate(registry, descriptor.getExtensionContext(null, null, EmptyConfigurationParameters.INSTANCE));

		assertThat(result.isDisabled()).isTrue();
		assertThat(result.getReason()).hasValueSatisfying(actual -> {

			assertThat(actual).startsWith("Disabled on operating system");
		});
	}

	private BenchmarkClassDescriptor createDescriptor(Class<?> javaClass) {
		BenchmarkClass benchmarkClass = BenchmarkClass.create(javaClass, Collections.emptyList());
		return new BenchmarkClassDescriptor(UniqueId.root("root", "root"), benchmarkClass);
	}


	public static class SimpleBenchmarkClass {

		@Benchmark
		public void justOne() {
		}
	}

	@Disabled
	public static class DisabledBenchmark {

		@Benchmark
		public void justOne() {
		}
	}

	@EnabledOnOs(OS.AIX) // let's hope this test is never executed on AIX
	public static class AixBenchmark {

		@Benchmark
		public void justOne() {
		}
	}

	enum EmptyConfigurationParameters implements ConfigurationParameters {
		INSTANCE;

		@Override
		public Optional<String> get(String key) {
			return Optional.empty();
		}

		@Override
		public Optional<Boolean> getBoolean(String key) {
			return Optional.empty();
		}

		@Override
		public int size() {
			return 0;
		}
	}
}
