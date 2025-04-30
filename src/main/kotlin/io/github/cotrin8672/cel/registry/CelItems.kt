package io.github.cotrin8672.cel.registry

import com.tterrag.registrate.util.entry.ItemEntry
import io.github.cotrin8672.cel.CreateEnderLink.REGISTRATE
import io.github.cotrin8672.cel.content.item.ScopeFilterItem

object CelItems {
    init {
        REGISTRATE.setCreativeTab(CelCreativeModeTabs.CEL_CREATIVE_TAB)
    }

    val SCOPE_FILTER: ItemEntry<ScopeFilterItem> = REGISTRATE
        .item<ScopeFilterItem>("scope_filter", ::ScopeFilterItem)
        .register()

    fun register() {}
}
