package io.leeseongbok

import org.springframework.boot.SpringApplication;
import org.springframework.boot.WebApplicationType
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.runApplication
import org.springframework.context.annotation.Bean
import org.springframework.web.reactive.function.server.RequestPredicates
import org.springframework.web.reactive.function.server.RouterFunction
import org.springframework.web.reactive.function.server.RouterFunctions
import org.springframework.web.reactive.function.server.ServerResponse

@SpringBootApplication
class WsApp

fun main(args: Array<String>) {

    runApplication<WsApp>(*args);
}
