/*
 * Copyright 2018-2019 the original author or authors.
 *
 * All rights reserved. This program and the accompanying materials are
 * made available under the terms of the Eclipse Public License v2.0 which
 * accompanies this distribution and is available at
 *
 * http://www.eclipse.org/legal/epl-v20.html
 */
package jmh.mbr.junit5.descriptor;

import static java.util.stream.Collectors.*;
import static org.junit.platform.commons.util.AnnotationUtils.*;

import java.lang.reflect.AnnotatedElement;
import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.extension.Extension;
import org.junit.jupiter.engine.extension.ExtensionRegistry;

/**
 * @author Mark Paluch
 */
class ExtensionUtils {

	static ExtensionRegistry populateNewExtensionRegistryFromExtendWithAnnotation(ExtensionRegistry parentRegistry,
																				  AnnotatedElement annotatedElement) {

		List<Class<? extends Extension>> extensionTypes = findRepeatableAnnotations(annotatedElement, ExtendWith.class).stream()
				.map(ExtendWith::value)
				.flatMap(Arrays::stream)
				.collect(toList());

		return ExtensionRegistry.createRegistryFrom(parentRegistry, extensionTypes);
	}
}
