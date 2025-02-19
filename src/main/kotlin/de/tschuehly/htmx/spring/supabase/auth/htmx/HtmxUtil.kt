package de.tschuehly.htmx.spring.supabase.auth.htmx

import de.tschuehly.htmx.spring.supabase.auth.exception.HxCurrentUrlHeaderNotFound
import io.github.wimdeblauwe.htmx.spring.boot.mvc.HtmxRequestHeader
import io.github.wimdeblauwe.htmx.spring.boot.mvc.HtmxResponseHeader
import io.github.wimdeblauwe.htmx.spring.boot.mvc.HxSwapType
import jakarta.servlet.http.Cookie
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.web.context.request.RequestContextHolder
import org.springframework.web.context.request.ServletRequestAttributes

object HtmxUtil {
    fun idSelector(id: String): String {
        return "#$id"
    }

    fun retarget(cssSelector: String?) {
        setHeader(HtmxResponseHeader.HX_RETARGET.getValue(), cssSelector)
    }

    fun getCurrentUrl() = getRequest().getHeader("HX-Current-URL")
        ?: throw HxCurrentUrlHeaderNotFound()

    fun retargetToId(id: String) {
        val request: HttpServletRequest = getRequest()
        if (request.getHeader(HtmxRequestHeader.HX_REQUEST.value) != null) {
            setHeader(
                headerName = HtmxResponseHeader.HX_RETARGET.value,
                headerValue = if (id.startsWith("#")) id else "#$id"
            )
        }
    }

    fun swap(hxSwapType: HxSwapType) {
        setHeader(HtmxResponseHeader.HX_RESWAP.value, hxSwapType.value)
    }


    fun trigger(event: String?) {
        val header: String? = getResponse().getHeader(HtmxResponseHeader.HX_TRIGGER.value)
        setHeader(HtmxResponseHeader.HX_TRIGGER.value, listOfNotNull(header, event).joinToString(", "))
    }


    fun setHeader(headerName: String?, headerValue: String?) {
        getResponse().setHeader(headerName, headerValue)
    }

    fun setHeader(htmxResponseHeader: HtmxResponseHeader, headerValue: String?) {
        getResponse().setHeader(htmxResponseHeader.value, headerValue)
    }

    fun isHtmxRequest(): Boolean {
        return getRequest().getHeader(HtmxRequestHeader.HX_REQUEST.value) == "true"
    }

    fun getCookie(name: String): Cookie? {
        return getRequest().cookies?.find { it.name == name }
    }

    fun getResponse(): HttpServletResponse {
        return (RequestContextHolder.getRequestAttributes() as? ServletRequestAttributes)?.response
            ?: throw RuntimeException("No response found in RequestContextHolder")
    }

    fun getRequest(): HttpServletRequest {
        return (RequestContextHolder.getRequestAttributes() as? ServletRequestAttributes)?.request
            ?: throw RuntimeException("No response found in RequestContextHolder")
    }

    fun redirect(path: String?) {
        if (path != null) {
            setHeader(HtmxResponseHeader.HX_REDIRECT, path)
            setHeader(HtmxResponseHeader.HX_RESWAP, HxSwapType.NONE.value)
        }
    }

}