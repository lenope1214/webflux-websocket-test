package io.leeseongbok.handler

import org.reactivestreams.Publisher
import org.reactivestreams.Subscriber
import org.reactivestreams.Subscription
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component
import org.springframework.web.reactive.socket.CloseStatus
import org.springframework.web.reactive.socket.WebSocketHandler
import org.springframework.web.reactive.socket.WebSocketMessage
import org.springframework.web.reactive.socket.WebSocketSession
import reactor.core.publisher.Mono
import reactor.core.publisher.SignalType

@Component
class ReactiveWebSocketEchoHandler : WebSocketHandler {
    private val log: Logger = LoggerFactory.getLogger(ReactiveWebSocketEchoHandler::class.java)


    override fun handle(webSocketSession: WebSocketSession): Mono<Void> {
        log.info("HANDLE --> $webSocketSession")

        log.info("handShakeInfo : {}", webSocketSession.handshakeInfo)

        log.info("attribute : {}", webSocketSession.attributes)


        val echoSubPublisher = EchoSubPublisher(webSocketSession)
        webSocketSession.receive()
            .limitRate(25)
            .doOnError { t: Throwable ->
                log.error("session receive error : {}", t)
                echoSubPublisher.onError(t)
            }
            .doOnTerminate {
                log.debug("doOnTerminate")
                webSocketSession.close(CloseStatus.NORMAL)
            }
            .doFinally { a: SignalType? ->
                log.debug("doFinally")
            }
            .subscribe(echoSubPublisher)



        webSocketSession.textMessage("")


        val sendMono = webSocketSession.send(echoSubPublisher)


        return sendMono
    }

    private class EchoSubPublisher(var webSocketSession: WebSocketSession) : Publisher<WebSocketMessage>,
        Subscriber<WebSocketMessage>, Subscription {
        private val log: Logger = LoggerFactory.getLogger(EchoSubPublisher::class.java)

        var subscriber: Subscriber<in WebSocketMessage>? = null

        var receiveSubscrition: Subscription? = null

        override fun subscribe(s: Subscriber<in WebSocketMessage>) {
            log.debug("subscribe : $s")
            subscriber = s
            subscriber!!.onSubscribe(this)
        }

        override fun onSubscribe(receiveSubscrition: Subscription) {
            log.debug("onSubscribe : $receiveSubscrition")
            this.receiveSubscrition = receiveSubscrition
            this.receiveSubscrition!!.request(1)
        }

        fun customSend(text: String?) {
            subscriber!!.onNext(webSocketSession.textMessage(text))
        }

        override fun onNext(message: WebSocketMessage) {
            val text = message.payloadAsText

            log.debug("TEXT : {}", text)
            subscriber!!.onNext(webSocketSession.textMessage(text))

            receiveSubscrition!!.request(1)
        }

        override fun onError(t: Throwable) {
            log.error("subpub receive error : {}", t)

            subscriber!!.onError(t)
            subscriber!!.onComplete()
        }

        override fun onComplete() {
            subscriber!!.onComplete()
        }

        override fun request(n: Long) {
            log.debug("from outbound send subscriber request : {}", n)
        }

        override fun cancel() {
            log.debug("from outbound send subscriber cancel")
        }
    }
}
