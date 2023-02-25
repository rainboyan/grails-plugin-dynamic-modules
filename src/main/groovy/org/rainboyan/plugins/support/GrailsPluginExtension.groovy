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
package org.rainboyan.plugins.support

import groovy.transform.CompileStatic

import org.rainboyan.plugins.DefaultDynamicModulesManager
import org.rainboyan.plugins.DynamicModulesManager
import org.springframework.beans.BeanWrapper
import org.springframework.beans.BeanWrapperImpl
import org.springframework.context.ApplicationContext

import grails.plugins.GrailsPlugin
import grails.plugins.module.ModuleDescriptor
import grails.util.GrailsClassUtils
import grails.util.GrailsMetaClassUtils

@CompileStatic
class GrailsPluginExtension {

    static final String PROVIDED_MODULES = 'providedModules'

    private static DynamicModulesManager getDynamicModulesManager(GrailsPlugin self) {
        ApplicationContext ctx = (ApplicationContext) GrailsMetaClassUtils.getPropertyIfExists(self, "applicationContext")
        if (ctx.containsBean(DynamicModulesManager.BEAN_NAME)) {
            return ctx.getBean(DynamicModulesManager)
        }
        DynamicModulesManager dynamicModulesManager = new DefaultDynamicModulesManager()
        dynamicModulesManager.setApplicationContext(ctx)
        dynamicModulesManager
    }

    static Object getProvidedModules(GrailsPlugin self) {
        BeanWrapper beanWrapper = new BeanWrapperImpl(self.instance)
        Object result = GrailsClassUtils.getPropertyOrStaticPropertyOrFieldValue(beanWrapper, self.instance, PROVIDED_MODULES)
        if (result instanceof Collection) {
            return (Collection<Class<?>>) result
        }
        else if (result instanceof Map) {
            return (Map<String, Class<?>>) result
        }
        Collections.emptyList()
    }

    static void addModuleDescriptor(GrailsPlugin self, ModuleDescriptor<?> moduleDescriptor) {
        getDynamicModulesManager(self).addModuleDescriptor(self, moduleDescriptor)
    }

    static Collection<ModuleDescriptor<?>> getModuleDescriptors(GrailsPlugin self) {
        getDynamicModulesManager(self).getModuleDescriptors(self)
    }

}
