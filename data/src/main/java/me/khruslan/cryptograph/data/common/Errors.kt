package me.khruslan.cryptograph.data.common

internal open class RemoteException(cause: Throwable? = null) : Exception(cause)

internal class NetworkConnectionException(cause: Throwable) : RemoteException(cause)

internal class UnsuccessfulResponseException(httpCode: Int, errorBody: String) : RemoteException()

internal class ResponseDeserializationException(cause: Throwable) : RemoteException(cause)

internal class DatabaseException(cause: Throwable) : Exception(cause)