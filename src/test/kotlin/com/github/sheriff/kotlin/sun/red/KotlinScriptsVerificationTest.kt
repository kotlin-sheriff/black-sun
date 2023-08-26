package com.github.sheriff.kotlin.sun.red

import com.github.sheriff.kotlin.sun.red.KotlinScriptsVerification.verifyKotlinScript
import org.junit.jupiter.api.Test

class KotlinScriptsVerificationTest {

  @Test
  fun shouldVerifyKotlinScript() {
    val kts =
      """
      import java.time.Duration
      val x = System.getenv()
      println(x.javaClass.methods.toList())
      """.trimIndent()
    verifyKotlinScript(kts)
  }
}