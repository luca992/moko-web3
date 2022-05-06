/*
 * Copyright 2021 IceRock MAG Inc. Use of this source code is governed by the Apache 2.0 license.
 */

package dev.icerock.moko.web3.contract

import com.ionspin.kotlin.bignum.integer.toBigInteger

object BoolParam : StaticEncoder<Boolean> {
    override fun encode(item: Boolean): ByteArray = when (item) {
        true -> UInt256Param.encode(item = 1.toBigInteger())
        false -> UInt256Param.encode(item = 0.toBigInteger())
    }

    override fun decode(source: ByteArray): Boolean =
        when (UInt256Param.decode(source)) {
            0.toBigInteger() -> false
            else -> true
        }
}
