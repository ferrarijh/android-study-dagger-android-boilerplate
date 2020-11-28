package com.example.daggerandroidpractice.di.component

import android.app.Application
import com.example.daggerandroidpractice.application.BaseApplication
import com.example.daggerandroidpractice.di.module.ActivityBuildersModule
import dagger.BindsInstance
import dagger.Component
import dagger.android.AndroidInjectionModule
import dagger.android.AndroidInjector

@Component(modules=[
    AndroidInjectionModule::class,
    ActivityBuildersModule::class
])
interface AppComponent: AndroidInjector<BaseApplication>{

    @Component.Builder
    interface Builder{
        @BindsInstance
        fun application(app: Application): Builder
        fun build(): AppComponent
    }
}