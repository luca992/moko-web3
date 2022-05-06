/*
 * Copyright 2020 IceRock MAG Inc. Use of this source code is governed by the Apache 2.0 license.
 */

package dev.icerock.moko.web3.serializer

import com.ionspin.kotlin.bignum.integer.BigInteger
import com.ionspin.kotlin.bignum.integer.toBigInteger
import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializer
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder

@Serializer(forClass = BigInteger::class)
object BigIntegerSerializer : KSerializer<BigInteger> {
    override val descriptor = PrimitiveSerialDescriptor(
        serialName = "dev.icerock.moko.web3.BigIntegerSerializer",
        kind = PrimitiveKind.STRING
    )

    override fun serialize(encoder: Encoder, value: BigInteger) {
        val string16 = value.toString(16)
        encoder.encodeString("0x$string16")
    }

    override fun deserialize(decoder: Decoder): BigInteger {
        val hexString = decoder.decodeString()
        return hexString.drop(2).toBigInteger(16)
    }
}
