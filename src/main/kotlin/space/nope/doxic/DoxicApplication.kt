package space.nope.doxic

import com.vladsch.flexmark.ast.FencedCodeBlock
import com.vladsch.flexmark.parser.Parser
import org.springframework.boot.CommandLineRunner
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import javax.script.ScriptContext
import javax.script.ScriptEngineManager
import javax.script.SimpleBindings

class Kilt {
	fun dance() = println("I'm dancing in my kilt")
}

@SpringBootApplication
class DoxicApplication: CommandLineRunner {
	override fun run(vararg args: String?) {
        val parser = Parser.builder().build()
		val engine = ScriptEngineManager().getEngineByExtension("kts")

		val markdown = """
			Hello you guys
			```gunner
			println("I am the script")
			```
		""".trimIndent()

		val simpleBindings = SimpleBindings(mapOf("kilt" to Kilt()))
		engine.setBindings(simpleBindings, ScriptContext.GLOBAL_SCOPE)

		val document = parser.parse(markdown)
		document.children.forEach {
			when(it) {
				is FencedCodeBlock -> {
					println("Fenced block ${it.info}")
					println(engine.eval(it.contentChars.toString()))
				}
			}
		}

		val script = """
			println("Hello from this inner script")
			println(kilt)
			kilt
			""".trimIndent()

		println(engine.eval(script))

	}
}

fun main(args: Array<String>) {
	runApplication<DoxicApplication>(*args)
}
