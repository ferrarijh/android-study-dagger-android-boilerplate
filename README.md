# Boilerplate Project for dagger-android
* Principle - do not let 'clients' know of its properties getting injected from outside.
* Think of 'component' as service provider and our app as client.
* With dagger android there's no need to inject your activity into components manually.
-- (in `MainActivity`, you don't need to write `component.inject(this)` anymore)

For example,
```kotlin
//in MainActivity..
val component = (application as BaseApplication).appComponent
    .mainActivityComponentFactory
    .create()

component.inject(this)
```

Such boilerplate code had better be not present in `MainActivity` here.
Dagger android relieves us of such pain.

## Setup

build.gradle:
```
    def dagger_version = '2.xx'
    implementation "com.google.dagger:dagger:$dagger_version"
    implementation "com.google.dagger:dagger-android:$dagger_version"
    implementation "com.google.dagger:dagger-android-support:$dagger_version"
    kapt "com.google.dagger:dagger-compiler:$dagger_version"
    kapt "com.google.dagger:dagger-android-processor:$dagger_version"
```

### AppComponent with AndroidInjector<T>
`AppComponent` extends special class and a special module.
Also define builder(or factory) to inject `BaseApplication` into the component.

AppComponent.kt:
```kotlin
@Component(modules=[
    AndroidInjectionModule::class   //always put this in the top-level component - usually AppComponent
    ActivityBuildersModule::class   //will be introduced soon
])
interface AppComponent: AndroidInjector<BaseApplication>{
    @Component.Builder
    interface Builder{
        @BindsInstance
        fun application(app: BaseApplication): Builder

        fun build(): AppComponent
    }
}
```

Now `AppComponent` implements `AndroidInjector<T>` and T is your base application class - 
Base application of type `T` is injected into `AppComponent`.

### ActivityBuildersModule

Define `ActivityBuildersModule` and add it to `AppComponent`'s modules list to let Dagger know
which activities will be served by dependency graph. 

ActivityBuildersModule.kt:
```kotlin
@Module
abstract class ActivityBuildersModule{
    @ContributesAndroidInjector
    abstract fun contributeMainActivity(): MainActivity

    //add any other activities here
}
```

Upon methods annotated with `@ContributesAndroidInjector` an `AndroidInjector` for the
return type of this method is generated. (return type is `MainActivity` here)
The injector is implemented with a Subcomponent and will be a child of the Module's component.

### BaseApplication and Activity
They both extends special Dagger class.

BaseApplication.kt:
```kotlin
class BaseApplication: DaggerApplicaton(){
    override fun applicationInjector(): AndroidInjector<out DaggerApplication>{
        return DaggerAppCompatActivity.builder().application(this)
    }
}
```

MyActivity.kt:
```kotlin
class MyActivity: DaggerAppCompatActivity{
    //...
}
```

### Setting modules for subcomponents generated with @ContributesAndroidInjector
If custom modules are to be added to the generated subcomponent which serves specific activity
simply add `(modules=[MyModule::class])` next to annotation like below.

ActivityBuildersModule.kt:
```kotlin
@Module
class ActivityBuildersModule{
    @ContributesAndroidInjector(modules=[MyModule::class])    
    fun contributesMainActivity(): MainActivity                    
}
```

## Solved Issues
* Error ```Map<K, V> cannot be provided without an @Provides-annotated method``` occurred still after
adding `@JvmSuppressWildcards` in front of `Providers<>` parameter
- solved by changing return types of methods annotated `@Provides`/`@Binds` to top parent class from
exact parameter type.

```kotlin
    @Module
    abstract class AuthViewModelModule {
        @Binds
        @IntoMap
        @ViewModelKey(AuthViewModel::class)
        abstract fun bindAuthViewModel(vm: AuthViewModel): ViewModel    //return type should NOT be AuthViewModel
    }
```