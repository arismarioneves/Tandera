package com.mari05lim.tandera.modules.library;

import com.google.common.base.Preconditions;
import com.mari05lim.tandera.model.util.Injector;

import dagger.Module;
import dagger.Provides;

/**
 * DEV Mari05liM
 */
@Module(
        library = true
)
public class InjectorModule {

    private final Injector mInjector;

    public InjectorModule(Injector injector) {
        mInjector = Preconditions.checkNotNull(injector, "injector cannot be null");
    }

    @Provides
    public Injector provideMainInjector() {
        return mInjector;
    }

}