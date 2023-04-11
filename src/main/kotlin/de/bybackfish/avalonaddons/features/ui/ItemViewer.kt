package de.bybackfish.avalonaddons.features.ui

import de.bybackfish.avalonaddons.AvalonAddons
import de.bybackfish.avalonaddons.core.annotations.Category
import de.bybackfish.avalonaddons.core.annotations.Property
import de.bybackfish.avalonaddons.core.config.Item
import de.bybackfish.avalonaddons.core.config.ItemConfig
import de.bybackfish.avalonaddons.core.event.Subscribe
import de.bybackfish.avalonaddons.core.feature.Feature
import de.bybackfish.avalonaddons.events.ForegroundScreenRenderEvent
import de.bybackfish.avalonaddons.events.GUIKeyPressEvent
import de.bybackfish.avalonaddons.events.RenderScreenEvent
import de.bybackfish.avalonaddons.events.gui.ClickGuiEvent
import de.bybackfish.avalonaddons.events.gui.CloseGuiEvent
import de.bybackfish.avalonaddons.events.gui.InitGuiEvent
import de.bybackfish.avalonaddons.events.gui.RenderGuiEvent
import de.bybackfish.avalonaddons.extensions.accessor
import de.bybackfish.avalonaddons.screen.ItemOriginScreen
import gg.essential.universal.UKeyboard
import gg.essential.universal.UResolution
import gg.essential.universal.USound
import gg.essential.vigilance.data.PropertyType
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback
import net.minecraft.client.MinecraftClient
import net.minecraft.client.gui.widget.TextFieldWidget
import net.minecraft.client.item.TooltipContext
import net.minecraft.item.ItemStack
import net.minecraft.text.Text

@Category("UI")
class ItemViewer: Feature() {


    private var itemStackMap = HashMap<Item, ItemStack>()
    private val itemMap = mutableMapOf<Pair<Int, Int>, ItemStack>()

    private lateinit var searchBar: TextFieldWidget


    private var page = 0
    private val xOffset = 20
    private val yOffset = 20
    private val startYOffset = 30

    private var lastQuery = ""

    @Property(
        forceType = PropertyType.NUMBER,
        description = "Items per row",
        min = 3,
        max = 20
    )
    var itemsPerRow = 8

    @Property(
        forceType = PropertyType.SWITCH,
        description = "Should the Item Page be always rendered? \n(or only if the search bar is focused)",
    )
    var alwaysRender = false

    private var numberOfRows = 0

    companion object {
        var autoFocus = false
        var inOriginScreen = false
    }

    override fun postInit() {
        itemStackMap.clear()
        ItemConfig.data.values.map {
            val item = ItemStack.fromNbt(it.getNBT())
            item.nbt?.putBoolean("hiderarity", true)

            if (!item.isEmpty) {
                itemStackMap[it] = item
            }
        }
    }

    @Subscribe
    fun onInit(event: InitGuiEvent) {
        resetBar()
        event.accessor.invokeAddDrawableChild(searchBar)

        val height = UResolution.scaledHeight - (startYOffset * 2)
        numberOfRows = height / yOffset

        onSearch(searchBar.text)

        println("Rows: $numberOfRows")
        println("ItemsPerRow: $itemsPerRow")
    }

    fun reCalculateList(filter: (Map.Entry<Item, ItemStack>) -> Boolean) {
        val items = itemStackMap.filter(filter).values.toList()


        val startIndex = page * itemsPerRow * numberOfRows
        val endIndex = startIndex + itemsPerRow * numberOfRows
        val lastIndex = items.lastIndex

        val itemList = if (startIndex <= lastIndex) {
            val safeEndIndex = minOf(endIndex, lastIndex + 1)
            items.subList(startIndex, safeEndIndex)
        } else {
            emptyList()
        }

        setItemsToRender(itemList)
    }

    private fun reloadPage() {
        onSearch(searchBar.text)
    }

    private fun onSearch(text: String) {
        if (text.isEmpty()) {
            reCalculateList { true }
        } else {
            val searchTerms = text.split("||").map(String::trim)
            println("Re-Searching for: $text")
            reCalculateList {
                searchTerms.any { term ->
                    // if the term starts with id: then search for the id
                    // if the term starts with name: then search for the name
                    // if the term starts with type: then search for the type

                    // if the term is "type:gem_stone berserk" then search for the type gem_stone and the name berserk
                    val conditions = term.split(" ").map { condition ->
                        if (condition.startsWith("id:")) {
                            it.key.id.contains(condition.substring(3), true)
                        } else if (condition.startsWith("name:")) {
                            it.key.name.contains(condition.substring(5), true)
                        } else if (condition.startsWith("type:")) {
                            it.key.type.contains(condition.substring(5), true)
                        } else if(condition.startsWith("rarity:")) {
                            it.key.name.contains(condition.substring(7), true)
                        } else if(condition.startsWith("lore:")) {
                            it.value.getTooltip(player, TooltipContext.Default.ADVANCED).any {
                                it.string.contains(condition.substring(5), true)
                            }
                        } else {
                            it.value.name.string.contains(condition, true)
                        }
                    }
                    conditions.all { it }
                }
            }
        }
    }

    fun resetBar() {
        val centerX = UResolution.scaledWidth / 2
        val width = 250
        val height = 20

        val x = centerX - width / 2
        val y = UResolution.scaledHeight - 20 - height

        searchBar = TextFieldWidget(
            MinecraftClient.getInstance().textRenderer,
            x,
            y,
            width,
            height,
            Text.of("Search")
        )

        searchBar.setChangedListener {
            lastQuery = it
            page = 0
            onSearch(it)
        }
        searchBar.setMaxLength(50)
        searchBar.setEditable(true)
        searchBar.isVisible = true
        searchBar.active = true

        searchBar.text = lastQuery
    }


    @Subscribe()
    fun onClose(event: CloseGuiEvent) {
        if(!inOriginScreen) autoFocus = false
        inOriginScreen = false
    }

    var nextRenderTick = false
    var hoveredItem: ItemStack? = null
    var mouseX = 0
    var mouseY = 0

    @Subscribe
    fun onRender(event: RenderGuiEvent) {
        if((!searchBar.isFocused && !autoFocus) && !alwaysRender) {
            nextRenderTick = false
            return
        }
        val x = UResolution.scaledWidth - (xOffset * itemsPerRow) - 10
        val y = startYOffset

        mouseX = event.mouseX
        mouseY = event.mouseY

        itemMap.forEach { (pos, itemStack) ->
            val (i, j) = pos
            val itemX = x + i * xOffset
            val itemY = y + j * yOffset

            mc.itemRenderer.renderInGui(itemStack, itemX, itemY)

            if (event.mouseX in itemX..itemX + 16 && event.mouseY in itemY..itemY + 16) {
                nextRenderTick = true;
                hoveredItem = itemStack
            } else if(hoveredItem == itemStack) {
                hoveredItem = null
            }
        }
    }

    @Subscribe
    fun onRender(event: ForegroundScreenRenderEvent) {
        if(!nextRenderTick) return
        if(hoveredItem == null) return
        val tooltip = hoveredItem!!.getTooltip(player, TooltipContext.Default.BASIC)
        event.that.renderTooltip(event.matrixStack, tooltip, mouseX, mouseY)
    }

    @Subscribe
    fun onClick(event: ClickGuiEvent) {
        if (event.x.toInt() in searchBar.x..searchBar.x + searchBar.width && event.y.toInt() in searchBar.y..searchBar.y + searchBar.height) {
            autoFocus = true
            return
        }

        // check if he even clicked on item viewer pane
        if (event.x.toInt() !in UResolution.scaledWidth - (xOffset * itemsPerRow) - 10..UResolution.scaledWidth) {
            return
        }

        autoFocus = true

        val x = UResolution.scaledWidth - (xOffset * itemsPerRow) - 10
        val i = ((event.x.toInt() - x) / xOffset).coerceIn(0, itemsPerRow - 1)
        val j = ((event.y.toInt() - startYOffset) / yOffset).coerceIn(0, (itemMap.size - 1) / itemsPerRow)

        println("Clicked: $i, $j")
        val itemStack = itemMap[Pair(i, j)] ?: return
        println("Clicked: ${itemStack.name.string}")
        val item = itemStackMap.entries.firstOrNull { it.value == itemStack }?.key ?: return

        println("Clicked: ${item.id}")

        val origin = when {
            item.origin.recipes.isNotEmpty() -> ItemOriginScreen.OriginType.CRAFTING_TABLE
            item.origin.bossDrops.isNotEmpty() -> ItemOriginScreen.OriginType.BOSS_DROP
            item.origin.craftingStations.isNotEmpty() -> ItemOriginScreen.OriginType.CRAFTING_STATION
            else -> null
        }

        if(origin == null) {
            USound.playPlingSound()
            return
        }
        inOriginScreen = true
        AvalonAddons.guiToOpen = ItemOriginScreen(item, itemStack, MinecraftClient.getInstance().currentScreen, origin)
    }

    fun setItemsToRender(items: List<ItemStack>) {
        itemMap.clear()
        items.forEachIndexed { index, itemStack ->
            itemMap[Pair(index % itemsPerRow, index / itemsPerRow)] = itemStack
        }
    }

    @Subscribe
    fun onKey(event: GUIKeyPressEvent) {
        // whitelist esc, enter, backspace, delete, left, right, home, end
        val whitelistedKeys = listOf(
            UKeyboard.KEY_ESCAPE,
            UKeyboard.KEY_ENTER,
            UKeyboard.KEY_BACKSPACE,
            UKeyboard.KEY_DELETE,
            UKeyboard.KEY_LEFT,
            UKeyboard.KEY_RIGHT,
            UKeyboard.KEY_HOME,
            UKeyboard.KEY_END
        )
        if (searchBar.isFocused && event.key !in whitelistedKeys && !UKeyboard.isCtrlKeyDown()) {
            event.isCancelled = true
            return
        }
        // page up/down -> left/mouse up -> right/mouse down
        if (event.key == UKeyboard.KEY_LEFT) {
            // check if page is 0
            if (page == 0) return
            page--
            reloadPage()
        } else if (event.key == UKeyboard.KEY_RIGHT) {
            val itemsPerPage = itemsPerRow * numberOfRows
            val startIndex = page * itemsPerPage
            val endIndex = startIndex + itemsPerPage

            println("Start: $startIndex, End: $endIndex, Size: ${itemStackMap.size} Page: $page | Can go to page: ${endIndex < itemStackMap.size}")
            if (endIndex >= itemStackMap.size) return
            page++
            reloadPage()
        }
    }
}

