# Grails Dynamic Modules Plugin

Grails Dynamic Modules Plugin (GDMP) offer new ways of creating modular and maintainable Grails applications.

A Grails plugin can implement one or more plugin modules to develop and extend Grails applications.
We can use Dynamic Modules to maximize the use of Grails plugins and create an open, shared, and reusable plugin market.

I will provide more Module Types in later releases, and also welcome to contribute more types of modules, as well as plugins for these modules.

## Grails Version

- Grails **4.1.2**

## Usage

This plugin has been released to [Maven Central](https://central.sonatype.com/artifact/org.rainboyan.plugins/grails-plugin-dynamic-modules).

Since this plugin is for building multiple modules, I highly recommend that you read this guide [Grails Multi-Project Build](https://guides.grails.org/grails-multi-project-build/guide/index.html) first. 


```bash
.
├── gradle
│   └── wrapper
├── grails-app
│   ├── assets
│   ├── conf
│   ├── controllers
│   ├── domain
│   ├── i18n
│   ├── init
│   ├── services
│   ├── taglib
│   ├── utils
│   └── views
├── plugins
│   └── menu
├── src
│   ├── integration-test
│   ├── main
│   └── test
├── build.gradle
├── gradle.properties
├── gradlew
├── gradlew.bat
├── grails-wrapper.jar
├── grailsw
├── grailsw.bat
└── settings.gradle
```

First, you should add the dependency to your app `build.gradle`,

```gradle

repositories {
    mavenCentral()
}

dependencies {

    // Grails 4
    compile "org.rainboyan.plugins:grails-plugin-dynamic-modules:0.1.0"

    // Grails 5
    implementation "org.rainboyan.plugins:grails-plugin-dynamic-modules:0.1.0"
}

```

Then in your Grails plugin project: `menu`, create your first Module descriptor: `MenuModuleDescriptor`,

```groovy
@ModuleType('menu')
class MenuModuleDescriptor extends AbstractModuleDescriptor {

    String i18n
    String title
    String link
    String location
    int order

    MenuModuleDescriptor() {
    }

    @Override
    void init(GrailsPlugin plugin, Map args) throws PluginException {
        super.init(plugin, args)
        this.i18n = args.i18n
        this.title = args.title
        this.link = args.link
        this.location = args.location
    }
}
```

Update the `MenuGrailsPlugin` to extend `grails.plugins.DynamicPlugin`,

```groovy
class MenuGrailsPlugin extends DynamicPlugin {

    // 1. add your new module types
    def providedModules = [
            MenuModuleDescriptor
    ]

    // 2. define 'menu' modules in doWithDynamicModules
    void doWithDynamicModules() {
        menu(key: 'about', i18n: 'menu.about', title: 'About US', link: '/about', location: 'topnav')
        menu(key: 'product', i18n: 'menu.product', title: 'Products', link: '/product', location: 'topnav', enabled: "${Environment.isDevelopmentMode()}") {
            description = "This menu enabled: ${Environment.isDevelopmentMode()}"
            order = 2
        }
        menu(key: 'contact', i18n: 'menu.contact', title: 'Contact', link: '/contact', location: 'topnav', enabled: false)
        menu(key: 'help', i18n: 'menu.help', title: 'Help', link: '/help', location: 'footer')
    }
}
```

`DynamicModules` plugin support more methods of `GrailsPluginManager`,

you can get all the `ModuleDescriptor` in your Grails application throug the two methods of `GrailsPluginManager`,

```groovy

// Get all the ModuleDescriptors
Collection<ModuleDescriptor<?>> allDescriptors = pluginManager.getModuleDescriptors()

// Get all the enabled MenuModuleDescriptor
List<MenuModuleDescriptor> menuDescriptors = pluginManager.getEnabledModuleDescriptorsByClass(MenuModuleDescriptor)

```

I've written a demo app for you that you can clone to your local computer, run it, and go through the code to learn more.

* [Grails Dynamic Modules Demo](https://github.com/rainboyan/grails-dynamic-modules-demo)

## Development

### Build from source

```
git clone https://github.com/rainboyan/grails-plugin-dynamic-modules.git
cd grails-plugin-dynamic-modules
./gradlew publishToMavenLocal
```

## Support Grails Version

* Grails 4.0, 4.1
* Grails 5.0, 5.1, 5.2, 5.3
* Grails 6.0

## Known issues

Grails has a bug that has been around since 2.0.0. I have submitted a patch for this bug, you can learn about it here, hope to fix it in the next release.

* [Grails 4.0.x#12891](https://github.com/grails/grails-core/pull/12891)
* [Grails 4.1.x#12892](https://github.com/grails/grails-core/pull/12892)
* [Grails 5.0.x#12893](https://github.com/grails/grails-core/pull/12893)
* [Grails 5.1.x#12894](https://github.com/grails/grails-core/pull/12894)
* [Grails 5.2.x#12895](https://github.com/grails/grails-core/pull/12895)

In your `MyNewGrailsPlugin`(which extends `DynamicPlugin`), when you using `doWithSpring()`,
there will be an error reporting that `A component required a bean of type 'org.rainboyan.plugins.ModuleDescriptorFactory' that could not be found.`,
this is because the Closure doWithSpring's delegation strategy was not set, `Closure.OWNER_FIRST` is the default strategy.

```groovy

    Closure doWithSpring() { {->
        // Grails bugs here, because doWithSpring's delegation strategy not set
           webMenuManager(DefaultWebMenuManager)
        }
    }

```

Although 6 months have passed and the [PR#12892](https://github.com/grails/grails-core/pull/12892) for this issue has not been merged [grails-core](https://github.com/grails/grails-core), there is still a way to solve this problem.

That is to use [Java-based Container Configuration](https://docs.spring.io/spring-framework/reference/core/beans/java.html) instead of Grails `Plugin.doWithSpring()`.

```java
@Configuration(proxyBeanMethods = false)
public class MenuModuleAutoConfiguration {

    @Bean
    @ConditionalOnMissingBean
    public WebMenuManager webMenuManager() {
        return new DefaultWebMenuManager();
    }

}
```

## License

This plugin is available as open source under the terms of the [APACHE LICENSE, VERSION 2.0](http://apache.org/Licenses/LICENSE-2.0)

## Links

- [Grails Website](https://grails.org)
- [Grails Plugins](https://docs.grails.org/4.0.0/guide/plugins.html)
- [Grails Github](https://github.com/grails)
- [Grails Dynamic Modules Plugin](https://github.com/rainboyan/grails-plugin-dynamic-modules)
- [Grails Dynamic Modules Demo](https://github.com/rainboyan/grails-dynamic-modules-demo)
- [Project Jigsaw](https://openjdk.org/projects/jigsaw/)
- [OSGi Specifications](https://docs.osgi.org/specification/)
- [Spring Dynamic Modules Reference Guide](https://docs.spring.io/spring-osgi/docs/current/reference/html/)
- [What's a plugin-oriented architecture?](https://spring.io/blog/2010/06/01/what-s-a-plugin-oriented-architecture)
