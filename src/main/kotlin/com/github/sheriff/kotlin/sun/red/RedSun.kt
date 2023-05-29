package com.github.sheriff.kotlin.sun.red

import com.github.kotlintelegrambot.dispatcher.handlers.CommandHandlerEnvironment
import com.github.kotlintelegrambot.dispatcher.handlers.MessageHandlerEnvironment
import com.github.kotlintelegrambot.entities.ChatId.Companion.fromId
import com.github.kotlintelegrambot.entities.ParseMode.MARKDOWN_V2
import java.lang.Exception
import java.util.concurrent.ConcurrentHashMap
import javax.script.Bindings
import javax.script.ScriptEngine
import javax.script.ScriptException
import javax.script.SimpleBindings

class RedSun(private val engine: ScriptEngine) {

  private val debugUsersIdsToDebugMarksIds = ConcurrentHashMap<Long, Long>()

  fun debugNextSpell(env: CommandHandlerEnvironment) {
    val userId = env.message.from?.id ?: return
    val messageId = env.message.messageId
    debugUsersIdsToDebugMarksIds[userId] = messageId
  }

  fun executeSpell(env: MessageHandlerEnvironment) {
    val spell = env.message.text ?: return
    try {
      engine.eval(spell, bindingsOf(env))
    } catch (e: ScriptException) {
      debug(e, env)
    } finally {
      turnOffDebugger(env)
    }
  }

  private fun debug(exception: Exception, env: MessageHandlerEnvironment) {
    if (isDebuggerEnabled(env)) {
      sendStackTrace(exception, env)
    }
  }

  private fun turnOffDebugger(env: MessageHandlerEnvironment) {
    if (isDebuggerEnabled(env)) {
      val userId = env.message.from?.id
      val debugMarkId = debugUsersIdsToDebugMarksIds[userId]
      debugUsersIdsToDebugMarksIds.remove(userId, debugMarkId)
    }
  }

  private fun sendStackTrace(e: Exception, to: MessageHandlerEnvironment) {
    val bot = to.bot
    val chatId = fromId(to.message.chat.id)
    val originalMessageId = to.message.messageId
    val stackTrace = // language=markdown
      """
      `${e.stackTraceToString().trim()}`
      """.trimIndent()
    bot.sendMessage(chatId, stackTrace, MARKDOWN_V2, replyToMessageId = originalMessageId)
  }

  private fun isDebuggerEnabled(env: MessageHandlerEnvironment): Boolean {
    val userId = env.message.from?.id
    val messageId = env.message.messageId
    val debugMarkId = debugUsersIdsToDebugMarksIds[userId]
    return debugMarkId != null && debugMarkId != messageId
  }

  private fun bindingsOf(env: MessageHandlerEnvironment): Bindings {
    return SimpleBindings(
      mapOf(
        "message" to env.message,
        "update" to env.update
      )
    )
  }
}