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
package org.rainboyan.plugins

import org.apache.commons.lang3.StringUtils
import org.springframework.context.ApplicationContext
import org.springframework.context.ApplicationListener
import org.springframework.context.event.ContextRefreshedEvent

import grails.plugins.DynamicPlugin
import grails.plugins.GrailsPlugin
import grails.plugins.GrailsPluginManager
import grails.plugins.module.ModuleDescriptor
import grails.plugins.module.ModuleType
import grails.util.GrailsNameUtils

/**
 * An {@link ApplicationListener} to load Dynamic Modules of all plugins.
 *
 * @author Michael Yan
 * @since 1.0.0
 */
class DynamicModulesApplicationListener implements ApplicationListener<ContextRefreshedEvent> {

    static final String MODULE_DESCRIPTOR = 'ModuleDescriptor'

    @Override
    void onApplicationEvent(ContextRefreshedEvent event) {
        ApplicationContext applicationContext = event.getApplicationContext()
        GrailsPluginManager pluginManager = applicationContext.getBean(GrailsPluginManager.BEAN_NAME, GrailsPluginManager)
        ModuleDescriptorFactory moduleDescriptorFactory = applicationContext.getBean(ModuleDescriptorFactory)

        providedModules(pluginManager, moduleDescriptorFactory)

        doWithDynamicModules(pluginManager)
    }

    private void providedModules(GrailsPluginManager pluginManager, ModuleDescriptorFactory moduleDescriptorFactory) {
        for (GrailsPlugin plugin : pluginManager.getAllPlugins()) {
            if (plugin.supportsCurrentScopeAndEnvironment()) {
                if (plugin.providedModules instanceof Collection) {
                    Collection<Class<?>> moduleTypes = (Collection<Class<?>>) plugin.providedModules
                    for (Class<?> clazz : moduleTypes) {
                        if (ModuleDescriptor.isAssignableFrom(clazz)) {
                            String type = null
                            ModuleType moduleType = clazz.getAnnotation(ModuleType)
                            if (moduleType != null) {
                                type = moduleType.value()
                            }
                            if (StringUtils.isBlank(type)) {
                                String shortName = GrailsNameUtils.getShortName(clazz)
                                if (shortName.endsWith(MODULE_DESCRIPTOR)) {
                                    type = StringUtils.uncapitalize(StringUtils.substringBefore(shortName, MODULE_DESCRIPTOR))
                                }
                                else {
                                    type = StringUtils.uncapitalize(shortName)
                                }
                            }
                            moduleDescriptorFactory.addModuleDescriptor(type, (Class<? extends ModuleDescriptor>) clazz)
                        }
                    }
                }
                else if (plugin.providedModules instanceof Map) {
                    Map<String, Class<?>> moduleTypesMap = (Map<String, Class<?>>) plugin.providedModules
                    for (Map.Entry<String, Class<?>> moduleType : moduleTypesMap.entrySet()) {
                        if (ModuleDescriptor.isAssignableFrom(moduleType.value)) {
                            moduleDescriptorFactory.addModuleDescriptor((String) moduleType.key,
                                    (Class<? extends ModuleDescriptor>) moduleType.value)
                        }
                    }
                }
            }
        }
    }

    private void doWithDynamicModules(GrailsPluginManager pluginManager) {
        for (GrailsPlugin plugin : pluginManager.getAllPlugins()) {
            if (plugin.instance instanceof DynamicPlugin) {
                DynamicPlugin dynamicPlugin = (DynamicPlugin) plugin.instance
                dynamicPlugin.doWithDynamicModules()
            }
        }
    }
}
