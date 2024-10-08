package dev.inmo.tgbotapi.bot.ktor

import dev.inmo.tgbotapi.requests.GetUpdates
import dev.inmo.tgbotapi.requests.abstracts.Request
import io.ktor.client.plugins.*
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import kotlinx.coroutines.flow.MutableStateFlow

class HealthCheckKtorPipelineStepsHolder: KtorPipelineStepsHolder {

    init {
        embeddedServer(Netty, port = 81) {
            routing {
                get("/health") {
                    if (health.value) {
                        call.respond(HttpStatusCode.OK)
                    } else {
                        call.respond(HttpStatusCode.BadRequest)
                    }
                }
            }
        }.start(wait = false)
    }

    val health: MutableStateFlow<Boolean> = MutableStateFlow(false);

    override suspend fun <T : Any> onRequestException(request: Request<T>, t: Throwable): T? {
        health.value = t is HttpRequestTimeoutException &&
                t.message!!.startsWith("Request timeout has expired [url=https://api.telegram.org/bot")

        return super.onRequestException(request, t)
    }

    override suspend fun <T : Any> onRequestReturnResult(
        result: Result<T>,
        request: Request<T>,
        callsFactories: List<KtorCallFactory>
    ): T {
        health.value = request is GetUpdates && result.isSuccess
        return super.onRequestReturnResult(result, request, callsFactories)
    }

}