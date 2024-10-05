package me.khruslan.cryptograph.data.core

abstract class DataException(
    val errorType: ErrorType,
    cause: Throwable? = null,
    message: String? = null,
) : Exception(message, cause)

enum class ErrorType {
    Network,
    Server,
    Database,
    Internal
}

internal class NetworkConnectionException(cause: Throwable) :
    DataException(ErrorType.Network, cause)

internal class UnsuccessfulResponseException(responseBody: String) :
    DataException(ErrorType.Server, message = buildUnsuccessfulResponseMessage(responseBody))

internal class ResponseDeserializationException(cause: Throwable) :
    DataException(ErrorType.Server, cause)

internal class DatabaseException(cause: Throwable) :
    DataException(ErrorType.Database, cause)

internal class DataValidationException(cause: Throwable) :
    DataException(ErrorType.Internal, cause)

private fun buildUnsuccessfulResponseMessage(responseBody: String): String? {
    return "Response body: ${responseBody.trimWhitespace()}".takeIf { responseBody.isNotBlank() }
}

private fun String.trimWhitespace(): String {
    val pattern = Regex("\\s+")
    return pattern.replace(this, " ")
}