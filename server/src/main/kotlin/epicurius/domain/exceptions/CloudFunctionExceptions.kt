package epicurius.domain.exceptions

class ErrorOnCloudFunction(message: String) : RuntimeException("Error on Cloud Function: $message")