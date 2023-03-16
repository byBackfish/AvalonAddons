package de.bybackfish.avalonaddons.features.ui

import com.mojang.blaze3d.systems.RenderSystem
import de.bybackfish.avalonaddons.AvalonAddons
import de.bybackfish.avalonaddons.core.annotations.Category
import de.bybackfish.avalonaddons.core.event.Subscribe
import de.bybackfish.avalonaddons.core.feature.Feature
import de.bybackfish.avalonaddons.events.ChestCloseEvent
import de.bybackfish.avalonaddons.events.ChestOpenEvent
import de.bybackfish.avalonaddons.events.RenderTooltipEvent
import de.bybackfish.avalonaddons.extensions.raw
import net.minecraft.client.MinecraftClient
import net.minecraft.client.font.TextRenderer
import net.minecraft.client.gui.screen.Screen
import net.minecraft.client.gui.screen.ingame.GenericContainerScreen
import net.minecraft.client.item.TooltipContext
import net.minecraft.client.render.item.ItemRenderer
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.inventory.Inventory
import net.minecraft.item.ItemStack
import net.minecraft.item.Items
import net.minecraft.nbt.*
import net.minecraft.util.Identifier
import java.io.File
import java.util.function.Consumer


@Category("UI")
class BackpackPreview : Feature() {

    private val TEXTURE: Identifier =
        Identifier(AvalonAddons.NAMESPACE, "textures/gui/inventory_background.png")

    val loadedBackpacks = mutableMapOf<String, BackpackData>()

    val saveDirectory = File("./config/AvalonAddons/backpacks/")

    val CHEST_REGEX = Regex("Backpack \\[(\\d+) Slots]", RegexOption.MULTILINE)

    var lastContainer: GenericContainerScreen? = null
    var lastId: String? = null

    init {
        if (!saveDirectory.exists()) saveDirectory.mkdirs()

        // load all backpacks
        for (file in saveDirectory.listFiles()) {
            if (!file.name.endsWith(".data")) continue
            println("LOADING FILE: ${file.name}")
            val id = file.nameWithoutExtension
            val data = loadFromFile(id, file!!) ?: continue
            println("LOADED FILE: ${file.name} | ID: $id | SIZE: ${data.size} | ITEMS: ${data.items.size} | DUMMY: ${data.dummyInventory.size()}")
            loadedBackpacks[id] = data
        }
    }

    @Subscribe
    fun onChestOpen(event: ChestOpenEvent) {
        lastContainer = event.container
        if (!CHEST_REGEX.matches(event.container.title.string.raw!!)) return
        val id =
            checkForItem(player!!.mainHandStack) ?: checkForItem(player!!.offHandStack) ?: return
        lastId = id
    }


    @Subscribe
    fun onChestClose(event: ChestCloseEvent) {
        if (lastContainer == null || lastId == null) return
        println("CHEST CLOSE!! Name: ${lastContainer?.title?.string?.raw} | ID: $lastId")

        val data = BackpackData(
            lastId!!,
            event.handler.inventory.size(),
            Array(event.handler.inventory.size()) { event.handler.inventory.getStack(it) },
            event.handler.inventory
        )
        loadedBackpacks[lastId!!] = data

        val items = loadedBackpacks[lastId!!]!!.items

        saveToFile(data)

        println("Serialized")

        lastContainer = null
        lastId = null
    }

    @Subscribe
    fun tryRenderBackpack(event: RenderTooltipEvent) {
        println("TRY RENDER BACKPACK")
        val matrices = event.matrices
        val mouseX = event.mouseX
        val mouseY = event.mouseY
        val item = event.item

        val id = checkForItem(item) ?: return
        println("RENDER BACKPACK: $id")
        val backpackData = loadedBackpacks[id] ?: return
        println("FOUND BACKPACK: $id | SIZE: ${backpackData.size} | ITEMS: ${backpackData.items.size} | DUMMY: ${backpackData.dummyInventory.size()}")

        val rows: Int = (backpackData.size) / 9

        val screen: Screen = MinecraftClient.getInstance().currentScreen ?: return
        val x: Int = if (mouseX + 184 >= screen.width) mouseX - 188 else mouseX + 8
        val y = 0.coerceAtLeast(mouseY - 16)

        RenderSystem.disableDepthTest()
        RenderSystem.setShaderTexture(0, TEXTURE)
        drawTexture(matrices, x, y, 0, 0, 176, 7)
        for (i in 0 until rows) drawTexture(matrices, x, y + i * 18 + 7, 0, 7, 176, 18)
        drawTexture(matrices, x, y + rows * 18 + 7, 0, 25, 176, 7)
        RenderSystem.enableDepthTest()

        val itemRenderer: ItemRenderer = mc.itemRenderer
        val textRenderer: TextRenderer = mc.textRenderer
        for (i in 0 until backpackData.size) {
            val itemX = x + (i) % 9 * 18 + 8
            val itemY = y + (i) / 9 * 18 + 8
            itemRenderer.zOffset = 200.0f
            itemRenderer.renderInGui(backpackData.dummyInventory.getStack(i), itemX, itemY)
            itemRenderer.renderGuiItemOverlay(
                textRenderer,
                backpackData.dummyInventory.getStack(i),
                itemX,
                itemY
            )
            itemRenderer.zOffset = 0.0f
        }

        event.isCancelled = true
    }


    fun checkForItem(item: ItemStack): String? {
        // check if its player head
        if (item.item != Items.PLAYER_HEAD) return null
        // check if the name contains "Backpack"
        if (!item.name.string.contains("Backpack")) return null

        // The Lore contains a String: ID: <text>
        val id = item.getTooltip(player, TooltipContext.Default.ADVANCED)
            .map { it.string.raw }
            .firstOrNull { it!!.contains("ID: ") }?.replace("ID: ", "") ?: return null
        return id
    }

    data class BackpackData(
        val id: String,

        val size: Int,
        val items: Array<ItemStack>,
        val dummyInventory: Inventory
    )

    fun saveToFile(data: BackpackData) {
        val root = NbtCompound()
        val list = NbtList()
        for (stack in data.items) {
            val item = NbtCompound()
            if (stack.isEmpty) {
                item.put("id", NbtString.of("minecraft:air"))
                item.put("Count", NbtInt.of(1))
            } else {
                item.put("id", NbtString.of(stack.item.toString()))
                item.put("Count", NbtInt.of(stack.count))
                item.put("tag", stack.nbt)
            }
            list.add(item)
        }
        root.put("list", list)
        root.put("size", NbtInt.of(data.size))

        kotlin.runCatching {
            NbtIo.writeCompressed(root, saveDirectory.resolve("${data.id}.data"))
        }.onFailure {
            println("Failed to save backpack data: ${it.message}")
            saveDirectory.resolve("${data.id}.data").delete()
        }
    }

    fun loadFromFile(id: String, file: File): BackpackData? {
        kotlin.runCatching {
            val root: NbtCompound = NbtIo.readCompressed(file)!!

            return BackpackData(
                id,
                size = root.getInt("size"),
                items = Array(root.getInt("size")) { ItemStack.EMPTY },
                dummyInventory = DummyInventory(root)
            )
        }.onFailure {
            println("Failed to load backpack data: ${it.message}")
            file.delete()

            return null
        }

        return null
    }

    class DummyInventory(root: NbtCompound) : Inventory {
        private val stacks: MutableList<ItemStack>

        init {
            stacks = ArrayList(root.getInt("size"))
            root.getList("list", NbtCompound.COMPOUND_TYPE.toInt()).forEach(
                Consumer { item: NbtElement? ->
                    stacks.add(
                        ItemStack.fromNbt(
                            item as NbtCompound?
                        )
                    )
                }
            )
        }

        override fun size(): Int {
            return stacks.size
        }

        override fun isEmpty(): Boolean {
            return false
        }

        override fun getStack(slot: Int): ItemStack {
            return stacks[slot]
        }

        override fun removeStack(slot: Int, amount: Int): ItemStack? {
            return null
        }

        override fun removeStack(slot: Int): ItemStack? {
            return null
        }

        override fun setStack(slot: Int, stack: ItemStack) {
            stacks[slot] = stack
        }

        override fun markDirty() {}
        override fun canPlayerUse(player: PlayerEntity): Boolean {
            return false
        }

        override fun clear() {}
    }
}