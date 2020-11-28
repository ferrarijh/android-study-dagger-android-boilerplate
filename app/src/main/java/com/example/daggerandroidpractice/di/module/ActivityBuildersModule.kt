package com.example.daggerandroidpractice.di.module

import com.example.daggerandroidpractice.MainActivity
import dagger.Module
import dagger.Provides
import dagger.android.ContributesAndroidInjector

@Module
abstract class ActivityBuildersModule {
    @ContributesAndroidInjector
    abstract fun contributeMainActivity(): MainActivity

    companion object {
        @Provides
        fun provideString() = "Hello World!"
    }
}