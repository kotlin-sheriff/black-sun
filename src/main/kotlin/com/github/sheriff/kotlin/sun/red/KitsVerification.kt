package com.github.sheriff.kotlin.sun.red

import kotlinx.ast.common.AstSource
import kotlinx.ast.common.ast.Ast
import kotlinx.ast.common.print
import kotlinx.ast.grammar.kotlin.common.summary
import kotlinx.ast.grammar.kotlin.target.antlr.kotlin.KotlinGrammarAntlrKotlinParser.parseKotlinFile

object KtsVerification {

  fun verifyKts(kts: String) {
    val modifiedCode = kts.toStandardKotlinCode()
    val astSource = AstSource.String(javaClass.simpleName, modifiedCode)
    val parsedFile = parseKotlinFile(astSource)
    val astResult = parsedFile.summary(true)

    astResult.onSuccess {
      it.forEach(Ast::print)
    }

    astResult.onFailure {
      it.forEach(::println)
    }
  }


  private fun String.toStandardKotlinCode(): String {
    var builder = ImportsBuilder() as KtsTransformerToKotlinFile
    for (l in lines()) {
      builder = builder.appendKtsLine(l)
    }
    return builder.buildKotlinFile()
  }

  private sealed interface KtsTransformerToKotlinFile {

    fun appendKtsLine(l: String): KtsTransformerToKotlinFile

    fun buildKotlinFile(): String
  }


  private class ImportsBuilder : KtsTransformerToKotlinFile {

    private val builder = StringBuilder()

    override fun appendKtsLine(l: String): KtsTransformerToKotlinFile {
      if (isImport(l)) {
        builder.appendLine(l)
        return this
      }
      return TypeAliasesBuilder(builder).appendKtsLine(l)
    }

    override fun buildKotlinFile(): String {
      return builder.toString()
    }

    private fun isImport(l: String): Boolean {
      return l.trimStart().startsWith("import ")
    }
  }

  private class TypeAliasesBuilder(private val builder: StringBuilder) : KtsTransformerToKotlinFile {

    override fun appendKtsLine(l: String): KtsTransformerToKotlinFile {
      if (isTypeAlias(l)) {
        builder.appendLine(l)
        return this
      }
      return MainFunctionBuilder(builder).appendKtsLine(l)
    }

    override fun buildKotlinFile(): String {
      return builder.toString()
    }

    private fun isTypeAlias(l: String): Boolean {
      return l.trimStart().startsWith("typealias ")
    }
  }

  private class MainFunctionBuilder(private val builder: StringBuilder) : KtsTransformerToKotlinFile {

    init {
      builder.appendLine("fun main() {")
    }

    override fun appendKtsLine(l: String): KtsTransformerToKotlinFile {
      builder.appendLine(l)
      return this
    }

    override fun buildKotlinFile(): String {
      builder.appendLine("}")
      return builder.toString()
    }
  }
}