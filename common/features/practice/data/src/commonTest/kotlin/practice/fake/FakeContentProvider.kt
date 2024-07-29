package practice.fake

import content.ContentProvider
import content.contract_models.ContentSummary
import content.contract_models.MainContent
import content.search.SearchContentFilter
import practice.fake.model.FaceReadingContent
import practice.reading.content.ReadingContent
import kotlin.reflect.KClass

@Suppress("UNCHECKED_CAST")
class FakeContentProvider: ContentProvider {

    private val content : MutableMap<Any, List<MainContent>> = mutableMapOf()

    init {
        content.putAll(faceReadingContent())
    }

    override suspend fun getSummary(filter: SearchContentFilter): List<ContentSummary> {
        return emptyList()
    }

    override suspend fun <T : MainContent> getFullContentById(id: Int, clazz: KClass<T>): T {
        return when(clazz) {
            ReadingContent::class -> {
                content.getValue(clazz)[id] as T
            }
            else -> {
                throw IllegalStateException("$clazz not found in FaceContentProvider")
            }
        }
    }
}

internal fun faceReadingContent(): Map<KClass<ReadingContent>, List<ReadingContent>>
    {
        val contents = listOf(
            FaceReadingContent(
                id = 0,
                text = "هذه جملة تجربية",
                wordsNumber = 3
            ),
            FaceReadingContent(
                id = 1,
                text = "دروس العربية لغير نطقين بها",
                wordsNumber = 5
            ),
            FaceReadingContent(
                id = 2,
                text = "هذه برنامج يحتوي على دروس نظرية وعملية هذه جملة تجربية",
                wordsNumber = 10
            ),
            FaceReadingContent(
                id = 3,
                text = "السلام عليكم و رحمة الله و بركاته",
                wordsNumber = 7
            )
        )
        return mapOf(ReadingContent::class to contents)
    }