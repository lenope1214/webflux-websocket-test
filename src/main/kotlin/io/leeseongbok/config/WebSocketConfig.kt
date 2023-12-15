package io.leeseongbok.config

import io.leeseongbok.handler.ReactiveWebSocketEchoHandler
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.web.reactive.HandlerMapping
import org.springframework.web.reactive.HandlerResult
import org.springframework.web.reactive.function.server.RequestPredicates
import org.springframework.web.reactive.function.server.RouterFunction
import org.springframework.web.reactive.function.server.RouterFunctions
import org.springframework.web.reactive.function.server.ServerRequest
import org.springframework.web.reactive.function.server.ServerResponse
import org.springframework.web.reactive.handler.SimpleUrlHandlerMapping
import org.springframework.web.reactive.socket.WebSocketHandler
import org.springframework.web.reactive.socket.server.support.HandshakeWebSocketService
import org.springframework.web.reactive.socket.server.support.WebSocketHandlerAdapter
import org.springframework.web.server.ServerWebExchange
import org.springframework.web.server.WebSession
import reactor.core.publisher.Mono
import java.util.function.Consumer
import java.util.function.Predicate

@Configuration
class WebSocketConfig(
    private val reactiveWebSocketEchoHandler: ReactiveWebSocketEchoHandler,
) {
    private val log: Logger = LoggerFactory.getLogger(WebSocketConfig::class.java)

    @Bean
    fun handlerMapping(): HandlerMapping {
        val map: Map<String, WebSocketHandler> = mapOf(
            "/echo" to reactiveWebSocketEchoHandler,
//            "/channels/{channel}" to reactiveWebSocketChannelHandler,
        )

        return SimpleUrlHandlerMapping(map)
    }

    @Bean
    fun websocketHandlerAdapter():WebSocketHandlerAdapter{
            val handshakeWebSocketService: HandshakeWebSocketService = HandshakeWebSocketService()
            handshakeWebSocketService.setSessionAttributePredicate(Predicate { k: String? -> true })

            val wsha: WebSocketHandlerAdapter = object : WebSocketHandlerAdapter(handshakeWebSocketService) {
                override fun handle(exchange: ServerWebExchange, handler: Any): Mono<HandlerResult> {
                    val attributes: Map<String, Any> = exchange.getAttributes()

                    exchange.getSession().subscribe(Consumer { session: WebSession ->
                        session.attributes.putAll(attributes)
                    })

                    return super.handle(exchange, handler)
                }
            }

            return wsha
        }

    @Bean
    fun helloRouterFunction(): RouterFunction<ServerResponse> {
        return RouterFunctions.route(RequestPredicates.path("/")) { _: ServerRequest? -> ServerResponse.ok().build() }
    }
}
