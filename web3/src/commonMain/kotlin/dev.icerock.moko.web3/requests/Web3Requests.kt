/*
 * Copyright 2021 IceRock MAG Inc. Use of this source code is governed by the Apache 2.0 license.
 */

package dev.icerock.moko.web3.requests

import com.ionspin.kotlin.bignum.integer.BigInteger
import dev.icerock.moko.web3.*
import dev.icerock.moko.web3.entity.LogEvent
import dev.icerock.moko.web3.entity.Transaction
import dev.icerock.moko.web3.entity.TransactionReceipt
import dev.icerock.moko.web3.hex.Hex32String
import dev.icerock.moko.web3.hex.HexString
import dev.icerock.moko.web3.serializer.BigIntegerSerializer
import kotlinx.serialization.DeserializationStrategy
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.builtins.ListSerializer
import kotlinx.serialization.builtins.nullable
import kotlinx.serialization.builtins.serializer
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonPrimitive
import kotlinx.serialization.json.encodeToJsonElement

object Web3Requests {
    fun send(signedTransaction: String) = Web3RpcRequest(
        method = "eth_sendRawTransaction",
        params = listOf(signedTransaction),
        paramsSerializer = String.serializer(),
        resultSerializer = TransactionHash.serializer()
    )

    fun <T> call(
        contractAddress: ContractAddress,
        callData: HexString,
        // deserialize from calldata to normal type
        dataDeserializer: DeserializationStrategy<T>,
        blockState: BlockState = BlockState.Latest
    ) = CallRpcRequest(contractAddress, callData, dataDeserializer, blockState)

    fun getNativeTransactionCount(
        walletAddress: WalletAddress,
        blockState: BlockState = BlockState.Pending
    ) = Web3RpcRequest(
        method = "eth_getTransactionCount",
        params = listOf(walletAddress.prefixed, blockState.toString()),
        paramsSerializer = String.serializer(),
        resultSerializer = BigIntegerSerializer
    )

    fun getTransactionReceipt(
        transactionHash: TransactionHash
    ) = Web3RpcRequest(
        method = "eth_getTransactionReceipt",
        params = listOf(transactionHash.prefixed),
        paramsSerializer = String.serializer(),
        resultSerializer = TransactionReceipt.serializer().nullable
    )

    fun getNativeBalance(
        walletAddress: WalletAddress,
        blockState: BlockState = BlockState.Latest
    ) = Web3RpcRequest(
        method = "eth_getBalance",
        params = listOf(walletAddress.prefixed, blockState.toString()),
        paramsSerializer = String.serializer(),
        resultSerializer = BigIntegerSerializer
    )

    fun getTransaction(
        transactionHash: TransactionHash
    ) = Web3RpcRequest(
        method = "eth_getTransactionByHash",
        params = listOf(transactionHash.prefixed),
        paramsSerializer = String.serializer(),
        resultSerializer = Transaction.serializer()
    )

    fun getGasPrice() = Web3RpcRequest(
        method = "eth_gasPrice",
        params = listOf(),
        paramsSerializer = ListSerializer(Unit.serializer()),
        resultSerializer = BigIntegerSerializer
    )

    @Serializable
    private data class GetEstimateGasObject(
        val from: EthereumAddress?,
        val to: EthereumAddress,
        @Serializable(with = BigIntegerSerializer::class)
        val gasPrice: BigInteger?,
        @SerialName("data")
        val callData: HexString?,
        @Serializable(with = BigIntegerSerializer::class)
        val value: BigInteger?
    )

    fun getEstimateGas(
        from: EthereumAddress?,
        gasPrice: BigInteger?,
        to: EthereumAddress,
        callData: HexString?,
        value: BigInteger?
    ): Web3RpcRequest<*, BigInteger> = Web3RpcRequest(
        method = "eth_estimateGas",
        params = listOf(
            GetEstimateGasObject(
                from = from,
                to = to,
                gasPrice = gasPrice,
                callData = callData,
                value = value
            )
        ),
        paramsSerializer = GetEstimateGasObject.serializer(),
        resultSerializer = BigIntegerSerializer
    )

    fun getEstimateGas(
        callRpcRequest: CallRpcRequest<*>,
        from: EthereumAddress?,
        gasPrice: BigInteger?,
        value: BigInteger?
    ): Web3RpcRequest<*, BigInteger> = getEstimateGas(
        from = from,
        gasPrice = gasPrice,
        to = callRpcRequest.contractAddress,
        callData = callRpcRequest.callData,
        value = value
    )

    fun getBlockNumber() = Web3RpcRequest(
        method = "eth_blockNumber",
        params = listOf(),
        paramsSerializer = ListSerializer(Unit.serializer()),
        resultSerializer = BigIntegerSerializer
    )

    fun getBlockByNumber(block: BlockState) = Web3RpcRequest(
        method = "eth_getBlockByNumber",
        params = listOf(Json.encodeToJsonElement(BlockStateSerializer, block), JsonPrimitive(value = true)),
        paramsSerializer = JsonElement.serializer(),
        resultSerializer = BlockInfo.serializer().nullable
    )

    @Serializable
    private data class GetLogsObject(
        val address: EthereumAddress?,
        val fromBlock: BlockState?,
        val toBlock: BlockState?,
        val topics: List<Hex32String?>?,
        val blockHash: BlockHash?
    )

    fun getLogs(
        address: EthereumAddress? = null,
        fromBlock: BlockState? = null,
        toBlock: BlockState? = null,
        topics: List<Hex32String?>? = null,
        blockHash: BlockHash? = null
    ) = Web3RpcRequest(
        method = "eth_getLogs",
        params = listOf(Json.encodeToJsonElement(GetLogsObject(address, fromBlock, toBlock, topics, blockHash))),
        paramsSerializer = JsonElement.serializer(),
        resultSerializer = ListSerializer(LogEvent.serializer())
    )
}
