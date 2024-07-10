import di_modules.database.databaseModule
import di_modules.json.serializationModule
import di_modules.ktor.ktorModule
import org.kodein.di.DI

val coreModиule = DI.Module("coreModule") {
    importAll(
        serializationModule,
        databaseModule,
        ktorModule,
//        settingsModule
    )
}