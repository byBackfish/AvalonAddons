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
import de.bybackfish.avalonaddons.events.gui.ClickGuiEvent
import de.bybackfish.avalonaddons.events.gui.CloseGuiEvent
import de.bybackfish.avalonaddons.events.gui.InitGuiEvent
import de.bybackfish.avalonaddons.events.gui.RenderGuiEvent
import de.bybackfish.avalonaddons.features.utility.AdvancedItemInfo
import de.bybackfish.avalonaddons.mixins.accessors.AccessorScreen
import de.bybackfish.avalonaddons.screen.ItemOriginScreen
import gg.essential.universal.UKeyboard
import gg.essential.universal.UResolution
import gg.essential.universal.USound
import gg.essential.vigilance.data.PropertyType
import kotlinx.coroutines.*
import net.minecraft.client.MinecraftClient
import net.minecraft.client.gui.widget.TextFieldWidget
import net.minecraft.client.item.TooltipContext
import net.minecraft.item.ItemStack
import net.minecraft.text.Text
import java.awt.Color
import java.util.concurrent.ConcurrentHashMap

@Category("UI")
class ItemViewer: Feature() {


    private var itemStackMap = HashMap<Item, ItemStack>()
    private val itemMap = ConcurrentHashMap<Pair<Int, Int>, Pair<Item, ItemStack>>()


    private lateinit var searchBar: TextFieldWidget


    private var page = 0
    private val xOffset = 20
    private val yOffset = 20
    private val startYOffset = 30


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

    @Property(
        forceType = PropertyType.SWITCH,
        description = "Execute certain commands on item click (edit, give)",
    )
    var allowAdminCommands = false

    private var numberOfRows = 0
    private var filteredItems = emptyList<Pair<Item, ItemStack>>()

    companion object {
        var autoFocus = false
        var inOriginScreen = false
        var lastQuery = ""

        var searchMode = false

        private val filters = mapOf<String, (Pair<String, Pair<Item?, ItemStack>>) -> Boolean>(
            "id" to { entry ->
                val (condition, it) = entry
                val (item, itemStack) = it
                item?.id?.contains(condition, true) ?: false
            },
            "name" to { entry ->
                val (condition, it) = entry
                val (item, itemStack) = it
                item?.name?.contains(condition, true) ?: false
            },
            "type" to { entry ->
                val (condition, it) = entry
                val (item, itemStack) = it
                item?.type?.contains(condition, true) ?: false
            },
            "rarity" to { entry ->
                val (condition, it) = entry
                val (item, itemStack) = it
                item?.getNBT()?.getCompound("tag")
                    ?.getString("MMOITEMS_TIER")?.contains(condition, true)
                    ?: false
            },
            "lore" to { entry ->
                val (condition, it) = entry
                val (item, itemStack) = it
                itemStack.getTooltip(MinecraftClient.getInstance().player, getTooltipMode()).any {
                    it.string.contains(condition, true)
                }
            },
            "canuse" to { entry ->
                val (condition, it) = entry
                val (item, itemStack) = it
                val classReq =
                    item?.getNBT()?.getCompound("tag")?.getString("MMOITEMS_REQUIRED_CLASS")
                classReq.isNullOrEmpty() || classReq.contains(condition, true)
            },
            "class" to { entry ->
                val (condition, it) = entry
                val (item, itemStack) = it
                val classReq = item?.getNBT()?.getCompound("tag")
                    ?.getString("MMOITEMS_REQUIRED_CLASS")
                if (classReq.isNullOrEmpty()) return@to false
                classReq.contains(condition, true)
            },
            "level" to { entry ->
                val (condition, it) = entry
                val (item, itemStack) = it
                val level = condition.toIntOrNull() ?: 0
                val levelReq =
                    item?.getNBT()?.getCompound("tag")?.getDouble("MMOITEMS_REQUIRED_LEVEL") ?: 0.0
                levelReq <= level
            },
            "title" to { entry ->
                val (condition, it) = entry
                val (item, itemStack) = it
                itemStack.name.string.contains(condition, true)
            }
        )

        fun doesItemMatchFilter(
            filters: ArrayList<List<Pair<(Pair<String, Pair<Item?, ItemStack>>) -> Boolean, String>>>,
            stack: ItemStack,
            ignoreShowcase: Boolean? = false
        ): Boolean {
            val id = stack.nbt?.getString("MMOITEMS_ITEM_ID") ?: return false
            val type = stack.nbt?.getString("MMOITEMS_ITEM_TYPE") ?: return false

            val avalonItem = ItemConfig.get()["${type}:$id"]
            return doesItemMatchFilter(filters, avalonItem to stack, ignoreShowcase)
        }

        fun doesItemMatchFilter(
            filters: ArrayList<List<Pair<(Pair<String, Pair<Item?, ItemStack>>) -> Boolean, String>>>,
            entry: Pair<Item?, ItemStack>,
            ignoreShowcase: Boolean? = false
        ): Boolean {
            //if(entry.second.nbt?.getBoolean("itemShowcase") == true && ignoreShowcase != null) return ignoreShowcase
            return filters.any {
                it.all {
                    val (filter, condition) = it
                    filter(condition to entry)
                }
            }
        }

        fun getCurrentFilters(text: String): ArrayList<List<Pair<(Pair<String, Pair<Item?, ItemStack>>) -> Boolean, String>>> {
            if (text.isEmpty()) return arrayListOf()
            val searchTerms = text.split("|").map(String::trim)

            val foundFilters: ArrayList<List<Pair<(Pair<String, Pair<Item?, ItemStack>>) -> Boolean, String>>> =
                arrayListOf()
            searchTerms.forEach { searchTerm ->
                val conditions = searchTerm.split("&").map { it.trim() }.map {
                    var filterName = if (it.split(":").size == 1) "title" else it.split(":")[0]
                    val filterCondition = if (it.split(":").size == 1) it else it.split(":")[1]

                    if (!filters.containsKey(filterName))
                        filterName = "title"

                    val filter = filters[filterName]!!
                    filter to filterCondition
                }
                foundFilters.add(conditions)
            }

            return foundFilters
        }

        fun getTooltipMode(): TooltipContext.Default? {
            return if (MinecraftClient.getInstance().options.advancedItemTooltips) TooltipContext.Default.ADVANCED else TooltipContext.Default.BASIC
        }

    }

    override fun postInit() {
        itemStackMap.clear()
        ItemConfig.data.values.map {
            val item = ItemStack.fromNbt(it.getNBT())
            item.nbt?.putBoolean("hiderarity", true)
            item.nbt?.putBoolean("itemShowcase", true)

            if (!item.isEmpty) {
                itemStackMap[it] = item
            }
        }

        resetBar()
    }


    @Subscribe
    fun onInit(event: InitGuiEvent) {
        resetBar()
        event.accessor.invokeAddDrawableChild(searchBar)

        val height = UResolution.scaledHeight - (startYOffset * 2)
        numberOfRows = height / yOffset

        if (itemMap.isEmpty())
            onSearch(searchBar.text)
    }

    private fun reCalculateList(filter: (Pair<Item, ItemStack>) -> Boolean) {
        filteredItems = itemStackMap.filter { term ->
            filter(term.key to term.value)
        }.toList()

        val startIndex = page * itemsPerRow * numberOfRows
        val endIndex = startIndex + itemsPerRow * numberOfRows
        val lastIndex = filteredItems.lastIndex

        val itemList = if (startIndex <= lastIndex) {
            val safeEndIndex = minOf(endIndex, lastIndex + 1)
            filteredItems.subList(startIndex, safeEndIndex)
        } else {
            emptyList()
        }

        setItemsToRender(itemList)
    }

    private fun reloadPage() {
        onSearch(searchBar.text)
    }


    private fun onSearch(text: String) {
        val appliedFilters = getCurrentFilters(text)
        if (appliedFilters.isEmpty()) reCalculateList { true }
        else reCalculateList { entry ->
            doesItemMatchFilter(appliedFilters, entry, null)
        }
    }


    private var debounceJob: Job? = null

    @OptIn(DelicateCoroutinesApi::class)
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

        searchBar.setMaxLength(250)
        searchBar.setEditable(true)
        searchBar.isVisible = true
        searchBar.active = true

        searchBar.text = lastQuery

        searchBar.setChangedListener { query ->
            lastQuery = query
            page = 0

            debounceJob?.cancel()
            debounceJob = GlobalScope.launch {
                delay(100)
                onSearch(query)
            }
        }
    }

    @Subscribe()
    fun onClose(event: CloseGuiEvent) {
        hoveredItem = null
        nextRenderTick = false

        if (!inOriginScreen) autoFocus = false
        inOriginScreen = false
    }

    private var nextRenderTick = false
    private var hoveredItem: Pair<Item, ItemStack>? = null
    var mouseX = 0
    var mouseY = 0

    @Subscribe
    fun onRenderGUI(event: RenderGuiEvent) {
        if ((!searchBar.isFocused && !autoFocus) && !alwaysRender) {
            nextRenderTick = false
            return
        }
        val x = UResolution.scaledWidth - (xOffset * itemsPerRow) - 10
        val y = startYOffset

        mouseX = event.mouseX
        mouseY = event.mouseY


        var foundItem = false
        itemMap.forEach { (pos, pair) ->
            val (i, j) = pos
            val itemX = x + i * xOffset
            val itemY = y + j * yOffset

            mc.itemRenderer.renderInGui(pair.second, itemX, itemY)

            if (event.mouseX in itemX..itemX + 16 && event.mouseY in itemY..itemY + 16) {
                nextRenderTick = true;
                hoveredItem = pair
                foundItem = true
            } else if (hoveredItem?.second == pair.second) {
                hoveredItem = null
            }
        }

        if (!foundItem) {
            nextRenderTick = false
            hoveredItem = null
        }

    }

    @Subscribe
    fun onRenderForeground(event: ForegroundScreenRenderEvent) {
        if (!nextRenderTick) return
        if (inOriginScreen) return
        if (hoveredItem == null) return
        val itemStack = hoveredItem!!.second

        if (allowAdminCommands) {
            val advancedItemInfo =
                AvalonAddons.featureManager.getFeature<AdvancedItemInfo>() ?: return
            advancedItemInfo.extraTooltip[itemStack] = listOf(
                Text.of(""),
                Text.of("§7Shift + Left Click: Edit Item"),
                Text.of("§7Shift + Right Click: Give Item"),
                Text.of("§7Control + Left Click: Upload Item")
            )
        }

        (event.that as AccessorScreen).invokeRenderTooltip(
            event.matrixStack,
            itemStack,
            event.mouseX,
            event.mouseY
        )
    }


    var lastClickMillis = 0L
    @Subscribe
    fun onClick(event: ClickGuiEvent) {
        if (event.x.toInt() in searchBar.x..searchBar.x + searchBar.width && event.y.toInt() in searchBar.y..searchBar.y + searchBar.height) {
            autoFocus = true

            // check if the bar was clicked twice
            if (System.currentTimeMillis() - lastClickMillis < 500) {
                searchMode = !searchMode
                lastClickMillis = 0
            } else {
                lastClickMillis = System.currentTimeMillis()
            }

            return
        }

        // check if he even clicked on item viewer pane
        if (event.x.toInt() !in UResolution.scaledWidth - (xOffset * itemsPerRow) - 10..UResolution.scaledWidth) {
            return
        }

        val leftClick = event.button == 0

        val x = UResolution.scaledWidth - (xOffset * itemsPerRow) - 10
        val i = ((event.x.toInt() - x) / xOffset).coerceIn(0, itemsPerRow - 1)
        val j = ((event.y.toInt() - startYOffset) / yOffset).coerceIn(
            0,
            (itemMap.size - 1) / itemsPerRow
        )

        val itemStack = itemMap[Pair(i, j)] ?: return
        val item = itemStack.first

        hoveredItem = null
        nextRenderTick = false

        // check if SHIFT is pressed
        val shiftPressed = UKeyboard.isShiftKeyDown()
        if (shiftPressed && leftClick && allowAdminCommands) {
            USound.playExpSound()
            MinecraftClient.getInstance().player!!.closeHandledScreen()
            MinecraftClient.getInstance().player!!.networkHandler.sendCommand("mmoitems edit ${item.type} ${item.name}")
            nextRenderTick = false
            hoveredItem = null
            return
        } else if (shiftPressed && !leftClick && allowAdminCommands) {
            MinecraftClient.getInstance().player!!.networkHandler.sendCommand("mmoitems give ${item.type} ${item.name}")
            USound.playPlingSound()
            return
        }

        val ctrlPressed = UKeyboard.isCtrlKeyDown()
        if (ctrlPressed && leftClick && allowAdminCommands) {
            USound.playLevelupSound()
            MinecraftClient.getInstance().player!!.networkHandler.sendCommand("uploaditems ${item.name}")
            return
        }


        autoFocus = true
        val origin = when {
            item.origin.recipes.isNotEmpty() -> ItemOriginScreen.OriginType.CRAFTING_TABLE
            item.origin.bossDrops.isNotEmpty() -> ItemOriginScreen.OriginType.BOSS_DROP
            item.origin.craftingStations.isNotEmpty() -> ItemOriginScreen.OriginType.CRAFTING_STATION
            else -> null
        }

        if (origin == null) {
            USound.playPlingSound()
            return
        }
        inOriginScreen = true
        AvalonAddons.guiToOpen = ItemOriginScreen(
            item,
            itemStack.second,
            MinecraftClient.getInstance().currentScreen,
            origin
        )
    }

    fun setItemsToRender(items: List<Pair<Item, ItemStack>>) {
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

        if (event.key == UKeyboard.KEY_LEFT) {
            if (page == 0) return
            page--
            reloadPage()
        } else if (event.key == UKeyboard.KEY_RIGHT) {
            val itemsPerPage = itemsPerRow * numberOfRows
            val startIndex = page * itemsPerPage
            val endIndex = startIndex + itemsPerPage

            println("Start: $startIndex, End: $endIndex, Size: ${filteredItems.size} Page: $page | Can go to page: ${endIndex < filteredItems.size}")
            if (endIndex >= filteredItems.size) return
            page++
            reloadPage()
        }
    }


}

