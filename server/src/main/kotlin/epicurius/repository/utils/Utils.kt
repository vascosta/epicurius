package epicurius.repository.utils

import java.sql.Array

@Suppress("UNCHECKED_CAST")
fun <T> getArray(array: Array): kotlin.Array<T> {
    return array.array as kotlin.Array<T>
}