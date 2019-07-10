/*
 * Copyright 2012-2019 the original author or authors.
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

package com.example;

import java.util.function.Function;

import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.function.context.FunctionRegistration;
import org.springframework.cloud.function.context.FunctionType;
import org.springframework.cloud.function.context.FunctionalSpringApplication;
import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.support.GenericApplicationContext;

// @checkstyle:off
@SpringBootApplication
public class SampleApplication implements ApplicationContextInitializer<GenericApplicationContext> {

	public static void main(String[] args) throws Exception {
		FunctionalSpringApplication.run(SampleApplication.class, args);
	}

	@Override
	public void initialize(GenericApplicationContext context) {
		context.registerBean("uppercase", FunctionRegistration.class, () -> new FunctionRegistration<>(uppercase())
				.type(FunctionType.from(String.class).to(String.class).getType()));
	}

	@Bean
	public Function<String, String> uppercase() {
		return value -> value.toUpperCase();
	}

}
// @checkstyle:on
