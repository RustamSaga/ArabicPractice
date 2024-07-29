package practice.fake

import kotlinx.datetime.LocalDateTime
import utils.CurrentDateProvider

class FakeCurrentDateProvider : CurrentDateProvider {
    override val today: LocalDateTime = LocalDateTime(2024, 4, 4, 12, 12)

}