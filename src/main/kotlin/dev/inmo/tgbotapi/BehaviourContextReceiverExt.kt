package dev.inmo.tgbotapi

import dev.inmo.kslog.common.KSLog
import dev.inmo.kslog.common.warning
import dev.inmo.tgbotapi.bot.TelegramBot
import dev.inmo.tgbotapi.extensions.behaviour_builder.BehaviourContextReceiver
import dev.inmo.tgbotapi.extensions.behaviour_builder.telegramBotWithBehaviourAndLongPolling
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job

val KSLogExceptionsHandler = { t: Throwable -> KSLog.warning("", t) }
val botToken = System.getenv("BOT_TOKEN")

val loggingMiddleware = LoggingMiddleware()
suspend fun longPolling(block: BehaviourContextReceiver<Unit>): Pair<TelegramBot, Job>  {
    val res = telegramBotWithBehaviourAndLongPolling(
        botToken,
        CoroutineScope(Dispatchers.IO),
        defaultExceptionsHandler = KSLogExceptionsHandler,
        autoSkipTimeoutExceptions = true,
        builder = { pipelineStepsHolder = loggingMiddleware },
        block = block)
    HealthCheck.addBot(res.first)
    return res
}
