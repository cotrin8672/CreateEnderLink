package io.github.cotrin8672.cel.util

import io.github.cotrin8672.cel.CreateEnderLink
import net.createmod.catnip.lang.LangBuilder
import net.createmod.catnip.lang.LangNumberFormat

object CelLang {
    private fun builder(): LangBuilder {
        return LangBuilder(CreateEnderLink.MOD_ID)
    }

    fun translate(langKey: String, vararg args: Any?): LangBuilder {
        return builder().translate(langKey, *args)
    }

    fun number(d: Double): LangBuilder {
        return builder().text(LangNumberFormat.format(d))
    }
}
