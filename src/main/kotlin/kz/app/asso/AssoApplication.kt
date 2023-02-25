package kz.app.asso

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.data.r2dbc.repository.config.EnableR2dbcRepositories
import org.springframework.web.reactive.config.EnableWebFlux

@EnableR2dbcRepositories
@EnableWebFlux
@SpringBootApplication
class AssoApplication

fun main(args: Array<String>) {
    runApplication<AssoApplication>(*args)
}
