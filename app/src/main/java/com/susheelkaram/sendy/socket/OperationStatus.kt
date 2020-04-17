package com.susheelkaram.sendy.socket

/**
 * Created by Susheel Kumar Karam
 * Website - SusheelKaram.com
 */

enum class OperationType {
    SERVER_START, CLIENT_CONNECT, DATA_TRANSFER
}
interface OperationStatus {
    fun onSuccess(type: OperationType)
    fun onFail(type: OperationType)
    fun onProgress(type: OperationType, progress: Double)
}