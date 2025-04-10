package epicurius.repository.jdbi.utils

import java.sql.Array

@Suppress("UNCHECKED_CAST")
fun <T> getArray(array: Array): kotlin.Array<T> {
    return array.array as kotlin.Array<T>
}

fun addCondition(
    query: StringBuilder,
    params: MutableMap<String, Any?>,
    condition: String,
    paramName: String,
    paramValue: Any?
) {
    if (paramValue != null) {
        query.append(" $condition")
        params[paramName] = paramValue
    }
}