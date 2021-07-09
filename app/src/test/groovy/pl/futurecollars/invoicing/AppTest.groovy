package pl.futurecollars.invoicing

import spock.lang.Specification

class AppTest extends Specification {

    def "test to cover main"() {
        setup:
        def app = new App()

        and:
        app.main()
    }
}
