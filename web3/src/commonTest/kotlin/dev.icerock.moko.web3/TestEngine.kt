/*
 * Copyright 2021 IceRock MAG Inc. Use of this source code is governed by the Apache 2.0 license.
 */

package dev.icerock.moko.web3

import io.ktor.client.*
import io.ktor.client.engine.mock.*
import io.ktor.client.plugins.logging.*
import io.ktor.client.request.*

// When new test added, it should be tested with this real client,
// And then mocked request should be added to client below,
// since networks are unstable (especially test networks)

fun createRealClient(): HttpClient = HttpClient {
    install(Logging){
        logger = object : Logger {
            override fun log(message: String) {
                println(message)
            }
        }
        level = LogLevel.ALL
    }
}

fun createMockClient(handler: suspend MockRequestHandleScope.(HttpRequestData) -> HttpResponseData): HttpClient =
    HttpClient(MockEngine) {
        engine {
            addHandler(handler)
        }
    }
