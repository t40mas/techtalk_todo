package de.piller.techtalks.springboothtmx

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity


@SpringBootApplication
class SpringBootHtmxApplication

fun main(args: Array<String>) {
    runApplication<SpringBootHtmxApplication>(*args)
}

