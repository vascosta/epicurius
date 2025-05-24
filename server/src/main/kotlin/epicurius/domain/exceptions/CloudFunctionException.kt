package epicurius.domain.exceptions

class CloudFunctionException(message: String) : RuntimeException("Error on Cloud Function: $message")
