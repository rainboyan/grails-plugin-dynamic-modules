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
package grails.plugins

import grails.plugins.ModuleDescriptor
import grails.plugins.ModuleDescriptorFactory
import org.springframework.beans.factory.NoSuchBeanDefinitionException

/**
 * A dynamic plugin is one that can be loaded into an application and used without restarting the application.
 * Plugin implementations should define the plugin hooks doWithDynamicModules.
 *
 * @author Michael Yan
 * @since 1.0.0
 */
abstract class DynamicPlugin extends Plugin {

    ModuleDescriptorFactory getModuleDescriptorFactory() {
        if(applicationContext != null) {
            return applicationContext.getBean(ModuleDescriptorFactory)
        }
        throw new NoSuchBeanDefinitionException(ModuleDescriptorFactory)
    }

    /**
     * Invoked in a phase where plugins can add dynamic modules.
     * Subclasses should override
     */
    void doWithDynamicModules() {
        // TODO Implement registering dynamic modules to application (optional)
    }

    @Override
    Object invokeMethod(String name, Object args) {
        if (!plugin) {
            return false
        }

        Object[] array = (Object[]) args
        if (array.length > 0) {
            ModuleDescriptor<?> moduleDescriptor = getModuleDescriptorFactory().getModuleDescriptor(name)
            moduleDescriptor.init(plugin, array[0] as Map<String, Object>)
            if (array.length > 1) {
                Closure closure = array[1] as Closure
                closure.setDelegate(moduleDescriptor)
                closure.setResolveStrategy(Closure.DELEGATE_ONLY)
                closure.call()
            }
            plugin.addModuleDescriptor(moduleDescriptor)
        }
        true
    }

}
