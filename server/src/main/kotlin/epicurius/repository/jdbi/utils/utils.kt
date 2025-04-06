package epicurius.repository.jdbi.utils

import java.sql.Array

@Suppress("UNCHECKED_CAST")
fun <T> getArray(array: Array): kotlin.Array<T> {
    return array.array as kotlin.Array<T>
}
