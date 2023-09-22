/*
 * Copyright 2022-2023 the original author or authors.
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
package org.rainboyan.plugins.config;

import org.rainboyan.plugins.DefaultDynamicModulesManager;
import org.rainboyan.plugins.DefaultModuleDescriptorFactory;
import org.rainboyan.plugins.DynamicModulesManager;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import grails.plugins.ModuleDescriptorFactory;

/**
 * {@link EnableAutoConfiguration Auto-configuration} Dynamic Modules
 *
 * @author Michael Yan
 * @since 1.0.0
 */
@Configuration(proxyBeanMethods = false)
public class DynamicModulesAutoConfiguration {

    @Bean
    @ConditionalOnMissingBean
    public ModuleDescriptorFactory moduleDescriptorFactory() {
        return new DefaultModuleDescriptorFactory();
    }

    @Bean
    @ConditionalOnMissingBean
    public DynamicModulesManager dynamicModulesManager() {
        return new DefaultDynamicModulesManager();
    }

}
