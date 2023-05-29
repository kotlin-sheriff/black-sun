package com.github.sheriff.kotlin.sun.red

import com.github.kotlintelegrambot.bot
import com.github.kotlintelegrambot.dispatch
import com.github.kotlintelegrambot.dispatcher.command
import com.github.kotlintelegrambot.dispatcher.handlers.CommandHandlerEnvironment
import com.github.kotlintelegrambot.dispatcher.handlers.MessageHandlerEnvironment
import com.github.kotlintelegrambot.dispatcher.message
import com.github.kotlintelegrambot.entities.ChatId.Companion.fromId
import com.sksamuel.hoplite.ConfigLoaderBuilder
import com.sksamuel.hoplite.addEnvironmentSource
import java.lang.Thread.startVirtualThread
import javax.script.ScriptEngineManager

data class ApplicationConfig(
  val telegramApiToken: String,
  val storageId: Long,
)

open class ApplicationFactory {

  open val config by lazy {
    ConfigLoaderBuilder.default()
      .addEnvironmentSource()
      .build()
      .loadConfigOrThrow<ApplicationConfig>()
  }

  open val bot by lazy {
    bot {
      token = config.telegramApiToken
      dispatch {
        message(::executeSpell)
        command("debug_next_spell", ::debugNextSpell)
      }
    }
  }

  open val storage by lazy {
    MagicStorage(bot, fromId(config.storageId))
  }

  open val engine by lazy {
    val engine = ScriptEngineManager().getEngineByExtension("kts")!!
    engine.put("storage", storage)
    engine.put("bot", TelegramBot(bot))
    engine
  }

  open val redSun by lazy {
    RedSun(engine)
  }

  open fun debugNextSpell(environment: CommandHandlerEnvironment) {
    redSun.debugNextSpell(environment)
  }

  open fun executeSpell(environment: MessageHandlerEnvironment) {
    startVirtualThread {
      redSun.executeSpell(environment)
    }
  }
}

fun main() {
  ApplicationFactory().bot.startPolling()
}