/*
 * Copyright 2021 IceRock MAG Inc. Use of this source code is governed by the Apache 2.0 license.
 */

@file:Suppress("EXPERIMENTAL_API_USAGE")

package dev.icerock.moko.web3

import dev.icerock.moko.web3.websockets.SubscriptionParam
import dev.icerock.moko.web3.websockets.createHttpClientEngine
import io.ktor.client.*
import io.ktor.client.plugins.websocket.*
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.take
import kotlinx.coroutines.runBlocking
import kotlin.test.BeforeTest

class Web3SocketTest {

    private lateinit var web3Socket: Web3Socket

    @BeforeTest
    fun `create socket`() {
        val httpClient = HttpClient(createHttpClientEngine()) {
            install(WebSockets) {
                pingInterval = 30
            }
        }

        web3Socket = Web3Socket(
            httpClient = httpClient,
            webSocketUrl = "wss://rinkeby.infura.io/ws/v3/59d7fae3226b40e09d84d713e588305b",
            coroutineScope = GlobalScope
        )
    }

//    @Test
    fun `test web socket flow`() {
        runBlocking {
            web3Socket.subscribeWebSocketWithFilter(SubscriptionParam.Logs)
                .onEach(::println)
                .take(2)
                .launchIn(scope = this)

            web3Socket.subscribeWebSocketWithFilter(SubscriptionParam.Logs)
                .onEach(::println)
                .take(2)
                .launchIn(scope = this)

            web3Socket.subscribeWebSocketWithFilter(SubscriptionParam.Logs)
                .onEach(::println)
                .take(2)
                .launchIn(scope = this)
        }
    }
}