package de.tschuehly.htmx.spring.supabase.auth.exception.info

class MissingCredentialsException(message: String) : Exception(message) {
    companion object {
        enum class MissingCredentials(val message: String) {
            PASSWORD_AND_EMAIL_MISSING("User needs to submit both a password and a email"),
            EMAIL_MISSING("User needs to submit a email"),
            PASSWORD_MISSING("User needs to submit a password"),
            OTP_MISSING("User needs to submit a OTP");

            fun throwExc() {
                throw MissingCredentialsException(message)
            }
        }
    }
}
