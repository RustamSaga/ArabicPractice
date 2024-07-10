package lessons_analize_info

enum class ArabicLevel {
    A0, A1, A2, B1, B2, C1, C2;

    fun requiredHoursFromZero(): Pair<Int, Int> {
        return when (this) {
            A0 -> 0 to 0
            A1 -> 90 to 100
            A2 -> 190 to 250
            B1 -> 350 to 490
            B2 -> 530 to 750
            C1 -> 730 to 1050
            C2 -> 1030 to 1450
        }
    }
}