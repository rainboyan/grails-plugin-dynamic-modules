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

import grails.plugins.*

class DynamicModulesGrailsPlugin extends Plugin {

    // the version or versions of Grails the plugin is designed for
    def grailsVersion = "4.0.0 > *"
    // resources that are excluded from plugin packaging
    def pluginExcludes = [
        "grails-app/views/error.gsp"
    ]

    def title = "Grails Dynamic Modules Plugin"
    def author = "Michael Yan"
    def authorEmail = "rain@rainboyan.com"
    def description = '''\
Grails Dynamic Modules Plugin offer new ways of creating modular and maintainable Grails applications.
'''

    def profiles = ['web']

    // URL to the plugin's documentation
    def documentation = "http://grails.org/plugin/grails-plugin-dynamic-modules"

    // Extra (optional) plugin metadata

    // License: one of 'APACHE', 'GPL2', 'GPL3'
    def license = "APACHE"

    // Details of company behind the plugin (if there is one)
//    def organization = [ name: "My Company", url: "http://www.my-company.com/" ]

    // Any additional developers beyond the author specified above.
    def developers = [ [ name: "Michael Yan", email: "rain@rainboyan.com" ]]

    // Location of the plugin's issue tracker.
    def issueManagement = [ system: "GitHub", url: "https://github.com/rainboyan/grails-plugin-dynamic-modules/issues" ]

    // Online location of the plugin's browseable source code.
    def scm = [ url: "https://github.com/rainboyan/grails-plugin-dynamic-modules.git" ]

    Closure doWithSpring() {
        {->
            def ctx = applicationContext
            moduleDescriptorFactory(DefaultModuleDescriptorFactory) {
                applicationContext = ctx
            }
            dynamicModulesManager(DefaultDynamicModulesManager)
        }
    }

}
