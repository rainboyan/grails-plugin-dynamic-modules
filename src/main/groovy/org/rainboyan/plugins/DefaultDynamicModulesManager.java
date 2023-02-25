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

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.function.Predicate;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

import grails.plugins.GrailsPlugin;
import grails.plugins.GrailsPluginManager;
import grails.plugins.PluginManagerAware;
import grails.plugins.module.ModuleDescriptor;

/**
 * DynamicModulesManager
 *
 * @author Michael Yan
 * @since 1.0.0
 */
public class DefaultDynamicModulesManager implements DynamicModulesManager, ApplicationContextAware, InitializingBean, PluginManagerAware {

    private ApplicationContext applicationContext;
    private GrailsPluginManager pluginManager;
    private Cache<String, Collection<ModuleDescriptor<?>>> cachedModuleDescriptors;

    @Override
    public void addModuleDescriptor(GrailsPlugin plugin, ModuleDescriptor<?> moduleDescriptor) {
        Collection<ModuleDescriptor<?>> moduleDescriptorCollection = this.cachedModuleDescriptors.getIfPresent(plugin.getName());
        if (moduleDescriptorCollection != null) {
            moduleDescriptorCollection.add(moduleDescriptor);
        }
        else {
            moduleDescriptorCollection = new ArrayList<>();
            moduleDescriptorCollection.add(moduleDescriptor);
            this.cachedModuleDescriptors.put(plugin.getName(), moduleDescriptorCollection);
        }
    }

    @Override
    public Collection<ModuleDescriptor<?>> getModuleDescriptors() {
        Collection<ModuleDescriptor<?>> moduleDescriptors = new ArrayList<>();
        for (GrailsPlugin plugin : this.pluginManager.getAllPlugins()) {
            Collection<ModuleDescriptor<?>> moduleDescriptorsOfPlugin = cachedModuleDescriptors.getIfPresent(plugin.getName());
            if (moduleDescriptorsOfPlugin != null && moduleDescriptorsOfPlugin.size() > 0) {
                moduleDescriptors.addAll(moduleDescriptorsOfPlugin);
            }
        }
        return moduleDescriptors;
    }

    @Override
    public Collection<ModuleDescriptor<?>> getModuleDescriptors(GrailsPlugin grailsPlugin) {
        Collection<ModuleDescriptor<?>> moduleDescriptors = new ArrayList<>();
        for (GrailsPlugin plugin : this.pluginManager.getAllPlugins()) {
            if (plugin.equals(grailsPlugin)) {
                Collection<ModuleDescriptor<?>> moduleDescriptorsOfPlugin = cachedModuleDescriptors.getIfPresent(plugin.getName());
                if (moduleDescriptorsOfPlugin != null && moduleDescriptorsOfPlugin.size() > 0) {
                    moduleDescriptors.addAll(moduleDescriptorsOfPlugin);
                }
            }
        }
        return moduleDescriptors;
    }

    @Override
    @SuppressWarnings("unchecked")
    public <M> Collection<ModuleDescriptor<M>> getModuleDescriptors(Predicate<ModuleDescriptor<M>> moduleDescriptorPredicate) {
        Collection<ModuleDescriptor<M>> moduleDescriptors = new ArrayList<>();
        for (GrailsPlugin plugin : this.pluginManager.getAllPlugins()) {
            Collection<ModuleDescriptor<?>> moduleDescriptorsOfPlugin = cachedModuleDescriptors.getIfPresent(plugin.getName());
            if (moduleDescriptorsOfPlugin != null && moduleDescriptorsOfPlugin.size() > 0) {
                for (ModuleDescriptor<?> moduleDescriptor : moduleDescriptorsOfPlugin) {
                    if (moduleDescriptorPredicate.test((ModuleDescriptor<M>) moduleDescriptor)) {
                        moduleDescriptors.add((ModuleDescriptor<M>) moduleDescriptor);
                    }
                }
            }
        }
        return moduleDescriptors;
    }

    @Override
    @SuppressWarnings("unchecked")
    public <D extends ModuleDescriptor<?>> List<D> getEnabledModuleDescriptorsByClass(Class<D> descriptorClazz) {
        List<D> moduleDescriptors = new ArrayList<>();
        for (GrailsPlugin plugin : this.pluginManager.getAllPlugins()) {
            Collection<ModuleDescriptor<?>> moduleDescriptorsOfPlugin = cachedModuleDescriptors.getIfPresent(plugin.getName());
            if (moduleDescriptorsOfPlugin != null && moduleDescriptorsOfPlugin.size() > 0) {
                for (ModuleDescriptor<?> moduleDescriptor : moduleDescriptorsOfPlugin) {
                    if (descriptorClazz.isInstance(moduleDescriptor) && moduleDescriptor.isEnabled()) {
                        moduleDescriptors.add((D) moduleDescriptor);
                    }
                }
            }
        }
        return moduleDescriptors;
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        this.cachedModuleDescriptors = Caffeine.newBuilder().initialCapacity(500).maximumSize(1000).build();
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }

    @Override
    public void setPluginManager(GrailsPluginManager pluginManager) {
        this.pluginManager = pluginManager;
    }

}
