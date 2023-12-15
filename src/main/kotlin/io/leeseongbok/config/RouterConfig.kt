package io.leeseongbok.config

import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.core.io.Resource
import org.springframework.http.MediaType
import org.springframework.web.reactive.function.server.RequestPredicates
import org.springframework.web.reactive.function.server.RouterFunction
import org.springframework.web.reactive.function.server.RouterFunctions
import org.springframework.web.reactive.function.server.ServerRequest
import org.springframework.web.reactive.function.server.ServerResponse

@Configuration
class RouterConfig {
    @Bean
    fun indexRouter(@Value("classpath:/static/index.html") indexHtml: Resource?): RouterFunction<ServerResponse> {
        return RouterFunctions.route(RequestPredicates.GET("/")) { request: ServerRequest? ->
            ServerResponse.ok().contentType(
                MediaType.TEXT_HTML
            ).syncBody(indexHtml)
        }
    }
}
