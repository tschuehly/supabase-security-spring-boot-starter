package de.tschuehly.supabasesecurityspringbootstarter.service

import de.tschuehly.supabasesecurityspringbootstarter.security.SupabaseAuthenticationToken
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse

interface ISupabaseUserService {
    fun registerWithEmail(email: String, password: String)
    fun loginWithEmail(email: String, password: String, response: HttpServletResponse)

    fun authorizeWithJwtOrResetPassword(
        request: HttpServletRequest,
        response: HttpServletResponse
    ): HttpServletResponse

    fun logout(request: HttpServletRequest, response: HttpServletResponse)
    fun setRolesWithRequest(request: HttpServletRequest, userId: String, roles: List<String>?)
    fun setRoles(serviceRoleJWT: String, userId: String, roles: List<String>?)
    fun sendPasswordRecoveryEmail(email: String)
    fun updatePassword(request: HttpServletRequest, password: String)
}