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
package org.rainboyan.plugins;

import java.util.Collection;
import java.util.List;
import java.util.function.Predicate;

import grails.plugins.GrailsPlugin;
import grails.plugins.ModuleDescriptor;

/**
 * DynamicModulesManager
 *
 * @author Michael Yan
 * @since 1.0.0
 */
public interface DynamicModulesManager {

    String BEAN_NAME = "dynamicModulesManager";

    void addModuleDescriptor(GrailsPlugin plugin, ModuleDescriptor<?> moduleDescriptor);

    /**
     * Gets all module descriptors of installed modules.
     * @return all module descriptors
     */
    Collection<ModuleDescriptor<?>> getModuleDescriptors();

    /**
     * Gets the module descriptors of grails plugin.
     * @param grailsPlugin the grails plugin
     * @return the module descriptors of grails plugin
     */
    Collection<ModuleDescriptor<?>> getModuleDescriptors(GrailsPlugin grailsPlugin);

    /**
     * Gets all module descriptors of installed modules that match the given predicate.
     *
     * @param <M> module class
     * @param moduleDescriptorPredicate describes which modules to match
     * @return a collection of {@link ModuleDescriptor}s that match the given predicate.
     */
    <M> Collection<ModuleDescriptor<M>> getModuleDescriptors(Predicate<ModuleDescriptor<M>> moduleDescriptorPredicate);

    /**
     * Get all enabled module descriptors that have a specific descriptor class.
     *
     * @param <D> class of the module descriptor
     * @param descriptorClazz module descriptor class
     * @return List of {@link ModuleDescriptor}s that implement or extend the given class.
     */
    <D extends ModuleDescriptor<?>> List<D> getEnabledModuleDescriptorsByClass(Class<D> descriptorClazz);

}
