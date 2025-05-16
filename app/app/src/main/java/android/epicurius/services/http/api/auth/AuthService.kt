package android.epicurius.services.http.api.auth

import android.epicurius.services.http.HttpService
import android.epicurius.services.http.api.auth.models.input.LoginInputModel
import android.epicurius.services.http.api.auth.models.input.SignUpInputModel
import android.epicurius.services.http.utils.APIResult
import android.epicurius.services.http.utils.Uris

class AuthService(private val httpService: HttpService) {

    suspend fun signUp(
        signUpInfo: SignUpInputModel
    ): APIResult<Unit> =
        httpService.post<Unit>(
            Uris.User.SIGNUP,
            signUpInfo
        )

    suspend fun login(
        loginInfo: LoginInputModel
    ): APIResult<Unit> =
        httpService.post<Unit>(
            Uris.User.LOGIN,
            loginInfo
        )

    suspend fun logout(
        token: String
    ): APIResult<Unit> =
        httpService.post<Unit>(
            Uris.User.LOGOUT,
            token = token
        )
}