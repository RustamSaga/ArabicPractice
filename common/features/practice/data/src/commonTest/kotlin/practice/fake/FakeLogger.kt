package practice.fake

import utils.Logger

class FakeLogger: Logger {
    override fun log(message: String) {
        println(message)
    }

    override fun error(message: String) {
        println(message)
    }
}