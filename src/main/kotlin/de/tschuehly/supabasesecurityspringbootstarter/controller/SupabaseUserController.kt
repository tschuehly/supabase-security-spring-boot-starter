package de.tschuehly.supabasesecurityspringbootstarter.controller

import de.tschuehly.supabasesecurityspringbootstarter.exception.MissingCredentialsException
import de.tschuehly.supabasesecurityspringbootstarter.exception.MissingCredentialsException.Companion.MissingCredentials
import de.tschuehly.supabasesecurityspringbootstarter.service.ISupabaseUserService
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.*
import org.springframework.web.server.ResponseStatusException
import java.util.function.Supplier

@Controller
@RequestMapping("api/user")
class SupabaseUserController(
    val supabaseUserService: ISupabaseUserService,
) {
    val logger: Logger = LoggerFactory.getLogger(SupabaseUserController::class.java)

    @PostMapping("/register")
    fun register(
        request: HttpServletRequest
    ) {
        request.checkCredentialsAndExecute { email, password ->
            logger.debug("User with the email $email is trying to register")
            supabaseUserService.registerWithEmail(email, password)
        }
    }

    @PostMapping("/login")
    fun login(
        @RequestParam credentials: Map<String, String>,
        response: HttpServletResponse,
        request: HttpServletRequest
    ) {
        request.checkCredentialsAndExecute { email, password ->
            logger.debug("User with the email $email is trying to register")
            supabaseUserService.loginWithEmail(email.trim(), password.trim(), response)
        }
    }

    private fun HttpServletRequest.checkCredentialsAndExecute(function: (String, String) -> Unit) {

        val email = this.parameterMap["email"]?.firstOrNull()
        val password = this.parameterMap["password"]?.firstOrNull()

        when {
            email.isNullOrBlank() && password.isNullOrBlank() ->
                MissingCredentials.PASSWORD_AND_EMAIL_MISSING.throwExc()

            email.isNullOrBlank() ->
                MissingCredentials.EMAIL_MISSING.throwExc()

            password.isNullOrBlank() ->
                MissingCredentials.PASSWORD_MISSING.throwExc()

            else ->
                function(email.trim(), password.trim())
        }
    }

    @PostMapping("/jwt")
    fun authorizeWithJwtOrResetPassword(request: HttpServletRequest, response: HttpServletResponse) {
        supabaseUserService.authorizeWithJwtOrResetPassword(request, response)
    }

    @GetMapping("/logout")
    fun logout(request: HttpServletRequest, response: HttpServletResponse) {
        supabaseUserService.logout(request, response)
    }

    @PutMapping("/setRoles")
    @ResponseBody
    fun setRoles(
        @RequestParam
        roles: List<String>?,
        request: HttpServletRequest,
        @RequestParam
        userId: String,
    ) {
        if (userId == "") {
            throw ResponseStatusException(HttpStatus.BAD_REQUEST, "UserId required")
        }
        supabaseUserService.setRolesWithRequest(request, userId, roles)
    }

    @PostMapping("/sendPasswordResetEmail")
    @ResponseBody
    fun sendPasswordResetEmail(
        @RequestParam
        email: String
    ) {
        logger.debug("User with the email $email requested a password reset")
        supabaseUserService.sendPasswordRecoveryEmail(email)
    }

    @PostMapping("/updatePassword")
    @ResponseBody
    fun updatePassword(
        request: HttpServletRequest,
        @RequestParam
        password: String
    ) {
        supabaseUserService.updatePassword(request, password)
    }
}
