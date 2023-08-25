package com.github.sheriff.kotlin.sun.red

import com.github.sheriff.kotlin.sun.red.KtsVerification.verifyKts
import org.junit.jupiter.api.Test

class ApplicationFactoryTest {

  @Test
  fun testEval() {
    val kts =
      """
      import java.time.Duration
      val x = System.getenv()
      println(x.javaClass.methods.toList())
      """.trimIndent()
    verifyKts(kts)
  }
}