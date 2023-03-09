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
package org.rainboyan.plugins.extensions

import groovy.transform.CompileStatic

import org.rainboyan.plugins.DefaultDynamicModulesManager
import org.rainboyan.plugins.DynamicModulesManager
import org.springframework.context.ApplicationContext

import grails.plugins.*
import grails.plugins.ModuleDescriptor
import grails.util.GrailsMetaClassUtils

@CompileStatic
class GrailsPluginManagerExtension {

    protected static DynamicModulesManager getDynamicModulesManager(GrailsPluginManager self) {
        ApplicationContext ctx = (ApplicationContext) GrailsMetaClassUtils.getPropertyIfExists(self, "applicationContext")
        if (ctx.containsBean(DynamicModulesManager.BEAN_NAME)) {
            return ctx.getBean(DynamicModulesManager)
        }
        DynamicModulesManager dynamicPluginManager = new DefaultDynamicModulesManager()
        dynamicPluginManager.setApplicationContext(ctx)
        dynamicPluginManager
    }

    static Collection<ModuleDescriptor<?>> getModuleDescriptors(GrailsPluginManager self) {
        getDynamicModulesManager(self).getModuleDescriptors()
    }

    static <D extends ModuleDescriptor<?>> List<D> getEnabledModuleDescriptorsByClass(GrailsPluginManager self, Class<D> descriptorClazz) {
        getDynamicModulesManager(self).getEnabledModuleDescriptorsByClass(descriptorClazz)
    }

}
