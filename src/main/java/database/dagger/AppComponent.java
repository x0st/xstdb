package database.dagger;

import javax.inject.Singleton;

import dagger.Component;
import database.Engine;

@Singleton
@Component(modules = {AppModule.class})
public interface AppComponent {
    void inject(Engine engine);
}
