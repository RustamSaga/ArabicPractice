package di_modules

import di_modules.json.serializationModule
import di_modules.ktor.ktorModule
import org.kodein.di.DI

val coreModule = DI.Module("Core") {
    import(serializationModule)
    import(ktorModule)
}