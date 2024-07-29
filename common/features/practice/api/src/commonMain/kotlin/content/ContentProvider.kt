package content

import content.contract_models.ContentSummary
import content.contract_models.MainContent
import content.search.SearchContentFilter
import kotlin.reflect.KClass

interface ContentProvider {

    suspend fun getSummary(filter: SearchContentFilter): List<ContentSummary>

    suspend fun <T : MainContent> getFullContentById(id: Int, clazz: KClass<T>): T
}