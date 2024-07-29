package content.search

class SearchContentFilter(
    val practices: List<Practice>,
    val selected: SelectedContent,
    val inProgress: ProgressContent,
    val level: ContentLevel,
    val titleName: String,
    val completed: CompletedContent,
)