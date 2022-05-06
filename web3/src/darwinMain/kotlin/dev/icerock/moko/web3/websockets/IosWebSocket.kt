/*
 * Copyright 2021 IceRock MAG Inc. Use of this source code is governed by the Apache 2.0 license.
 */

package dev.icerock.moko.web3.websockets


import io.ktor.client.plugins.websocket.*
import io.ktor.util.InternalAPI
import io.ktor.websocket.*
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.cancel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.channels.ReceiveChannel
import kotlinx.coroutines.channels.SendChannel
import kotlinx.coroutines.channels.consumeEach
import kotlinx.coroutines.launch
import platform.Foundation.NSData
import platform.Foundation.NSOperationQueue
import platform.Foundation.NSURL
import platform.Foundation.NSURLSession
import platform.Foundation.NSURLSessionConfiguration
import platform.Foundation.NSURLSessionWebSocketCloseCode
import platform.Foundation.NSURLSessionWebSocketDelegateProtocol
import platform.Foundation.NSURLSessionWebSocketMessage
import platform.Foundation.NSURLSessionWebSocketTask
import platform.darwin.NSObject
import kotlin.coroutines.CoroutineContext

internal class IosWebSocket(
    socketEndpoint: NSURL,
    override val coroutineContext: CoroutineContext
) : DefaultWebSocketSession {
    internal val originResponse: CompletableDeferred<String?> = CompletableDeferred()

    private val webSocket: NSURLSessionWebSocketTask

    private val _incoming = Channel<Frame>()
    private val _outgoing = Channel<Frame>()
    private val _closeReason = CompletableDeferred<CloseReason?>()

    override val incoming: ReceiveChannel<Frame> = _incoming
    override var masking: Boolean = false
    override val outgoing: SendChannel<Frame> = _outgoing
    override var pingIntervalMillis: Long = -1
    override var timeoutMillis: Long = -1
    override val closeReason: Deferred<CloseReason?> = _closeReason

    override val extensions: List<WebSocketExtension<*>>
        get() = emptyList()

    override var maxFrameSize: Long
        get() = throw WebSocketException("websocket doesn't support max frame size.")
        set(_) = throw WebSocketException("websocket doesn't support max frame size.")

    override suspend fun flush() = Unit

    override suspend fun send(frame: Frame) = Unit


    @InternalAPI
    override fun start(negotiatedExtensions: List<WebSocketExtension<*>>) {
        require(negotiatedExtensions.isEmpty()) { "Extensions are not supported." }
    }

    init {
        val urlSession = NSURLSession.sessionWithConfiguration(
            configuration = NSURLSessionConfiguration.defaultSessionConfiguration(),
            delegate = object : NSObject(), NSURLSessionWebSocketDelegateProtocol {
                override fun URLSession(
                    session: NSURLSession,
                    webSocketTask: NSURLSessionWebSocketTask,
                    didOpenWithProtocol: String?
                ) {
                    originResponse.complete(didOpenWithProtocol)
                }

                override fun URLSession(
                    session: NSURLSession,
                    webSocketTask: NSURLSessionWebSocketTask,
                    didCloseWithCode: NSURLSessionWebSocketCloseCode,
                    reason: NSData?
                ) {
                    val closeReason = CloseReason(
                        code = CloseReason.Codes.PROTOCOL_ERROR,
                        message = "$didCloseWithCode : ${reason.toString()}"
                    )
                    _closeReason.complete(closeReason)
                }
            },
            delegateQueue = NSOperationQueue.currentQueue()
        )
        log("urlSession was built: $urlSession")
        webSocket = urlSession.webSocketTaskWithURL(socketEndpoint)

        CoroutineScope(coroutineContext).launch {
            _outgoing.consumeEach { frame ->
                if (frame is Frame.Text) {
                    val message = NSURLSessionWebSocketMessage(frame.readText())
                    webSocket.sendMessage(message) { nsError ->
                        if (nsError != null) throw Exception(nsError.description)
                    }
                }

            }
        }

        listenMessages()
    }

    fun start() {
        log("urlSession will resume")
        webSocket.resume()
        log("urlSession did resume")
    }

    private fun listenMessages() {
        webSocket.receiveMessageWithCompletionHandler { message, nsError ->
            when {
                nsError != null -> {
                    throw Exception(nsError.description)
                }
                message != null -> {
                    message.string?.let { _incoming.trySend(Frame.Text(it)) }
                }
            }
            listenMessages()
        }
    }

    override fun terminate() {
        coroutineContext.cancel()
    }
}