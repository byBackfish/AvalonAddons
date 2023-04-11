package de.bybackfish.avalonaddons.screen

import de.bybackfish.avalonaddons.AvalonAddons
import de.bybackfish.avalonaddons.core.config.CraftingStationIngredient
import de.bybackfish.avalonaddons.core.config.Item
import de.bybackfish.avalonaddons.features.ui.ItemViewer
import net.minecraft.client.MinecraftClient
import net.minecraft.client.gui.DrawableHelper
import net.minecraft.client.gui.screen.Screen
import net.minecraft.client.gui.widget.ButtonWidget
import net.minecraft.client.util.math.MatrixStack
import net.minecraft.item.ItemStack
import net.minecraft.text.OrderedText
import net.minecraft.text.Text
import net.minecraft.util.Identifier
import java.lang.Math.ceil

class ItemOriginScreen(
    val item: Item,
    val itemStack: ItemStack,
    val itemViewer: Screen?,
    var selectedOrigin: OriginType
) : Screen(Text.of(item.name)) {

    private val CRAFTING_TABLE_GUI_WIDTH = 176
    private val CRAFTING_TABLE_GUI_HEIGHT = 166
    private val RESULT_ITEM_SPACING = 30


    private val ITEM_SPACING = 22
    private var currentPage = 0;

    var previousPageButton: ButtonWidget? = null
    var nextPageButton: ButtonWidget? = null

    enum class OriginType {
        CRAFTING_STATION,
        BOSS_DROP,
        CRAFTING_TABLE
    }

    override fun init() {
        super.init()

        val buttonWidth = 100
        val buttonHeight = 20
        val buttonSpacing = 10

        val buttonX = (this.width - 3 * buttonWidth - 2 * buttonSpacing) / 2
        val buttonY = this.height / 2 - 120

        val craftingStationButton = ButtonWidget.builder(Text.of("Crafting Station")) {
            selectedOrigin = OriginType.CRAFTING_STATION
            currentPage = 0
            checkForPageButton()
        }.size(buttonWidth, buttonHeight).position(buttonX, buttonY).build()
        if (item.origin.craftingStations.isEmpty()) craftingStationButton.active = false

        val bossDropButton = ButtonWidget.builder(Text.of("Boss Drop")) {
            selectedOrigin = OriginType.BOSS_DROP
            currentPage = 0
            checkForPageButton()
        }
            .size(buttonWidth, buttonHeight).position(buttonX + buttonWidth + buttonSpacing, buttonY).build()
        if (item.origin.bossDrops.isEmpty()) bossDropButton.active = false

        val craftingTableButton = ButtonWidget.builder(Text.of("Crafting Table")) {
            selectedOrigin = OriginType.CRAFTING_TABLE
            currentPage = 0
            checkForPageButton()
        }.size(buttonWidth, buttonHeight).position(buttonX + 2 * buttonWidth + 2 * buttonSpacing, buttonY).build()
        if (item.origin.recipes.isEmpty()) craftingTableButton.active = false

        previousPageButton = ButtonWidget.builder(
            Text.of("Previous Page")) {
            pageDown()
        }.size(buttonWidth, buttonHeight).position(this.width / 2 - buttonWidth - buttonSpacing / 2, this.height - 50).build()

        nextPageButton = ButtonWidget.builder(
            Text.of("Next Page")) {
            pageUp()
        }.size(buttonWidth, buttonHeight).position(this.width / 2 + buttonSpacing / 2, this.height - 50).build()

        this.addDrawableChild(craftingStationButton)
        this.addDrawableChild(bossDropButton)
        this.addDrawableChild(craftingTableButton)
        this.addDrawableChild(previousPageButton)
        this.addDrawableChild(nextPageButton)


        checkForPageButton()
    }

    fun pageDown() {
        if (currentPage > 0)
            currentPage--

        if(currentPage == 0)
            nextPageButton!!.active = true

        checkForPageButton()
    }

    fun pageUp() {
        val pages = when (selectedOrigin) {
            OriginType.CRAFTING_TABLE -> item.origin.recipes.size
            OriginType.BOSS_DROP -> item.origin.bossDrops.size
            OriginType.CRAFTING_STATION -> item.origin.craftingStations.size
        }

        if (currentPage < pages - 1)
            currentPage++

        checkForPageButton()
    }

    fun checkForPageButton() {
        val pages = when (selectedOrigin) {
            OriginType.CRAFTING_TABLE -> item.origin.recipes.size
            OriginType.BOSS_DROP -> item.origin.bossDrops.size
            OriginType.CRAFTING_STATION -> item.origin.craftingStations.size
        }

        previousPageButton!!.active = currentPage != 0

        nextPageButton!!.active = currentPage != pages - 1
    }


    override fun render(matrices: MatrixStack?, mouseX: Int, mouseY: Int, delta: Float) {
        // blur entire screen
        val blurColor = 0x7F000000
        DrawableHelper.fill(matrices!!, 0, 0, this.width, this.height, blurColor)

        // Placeholder code for displaying selected origin
        when (selectedOrigin) {
            OriginType.CRAFTING_TABLE -> {
                item.origin.recipes[currentPage].let {
                    val centerX = this.width / 2
                    val centerY = this.height / 2 - 30

                    drawCenteredTextWithShadow(
                        matrices,
                        textRenderer,
                        Text.of("Station: ${it.station}").asOrderedText(),
                        centerX,
                        centerY - 50,
                        0xFFFFFF
                    )
                    drawCenteredTextWithShadow(
                        matrices,
                        textRenderer,
                        Text.of("ID: ${it.id}").asOrderedText(),
                        centerX,
                        centerY - 35,
                        0xFFFFFF
                    )
                }
                itemStack.count = item.origin.recipes[currentPage].amount
                renderIngredientList(
                     matrices,
                    itemStack,
                    rotateList(item.origin.recipes[currentPage].ingredients),
                    mouseX,
                    mouseY
                )
            }
            OriginType.CRAFTING_STATION -> {
                item.origin.craftingStations[currentPage].let {
                    val centerX = this.width / 2
                    val centerY = this.height / 2 - 30

                    drawCenteredTextWithShadow(
                        matrices,
                        textRenderer,
                        Text.of("Station: ${it.station}").asOrderedText(),
                        centerX,
                        centerY - 50,
                        0xFFFFFF
                    )

                    drawCenteredTextWithShadow(
                        matrices,
                        textRenderer,
                        Text.of("ID: ${it.recipe}").asOrderedText(),
                        centerX,
                        centerY - 35,
                        0xFFFFFF
                    )
                }

                itemStack.count = item.origin.craftingStations[currentPage].outputAmount
                renderIngredientList(
                    matrices,
                    itemStack,
                    item.origin.craftingStations[currentPage].ingredients,
                    mouseX,
                    mouseY
                )
            }
            OriginType.BOSS_DROP -> {
                val bossDrop = item.origin.bossDrops[currentPage]

                val centerX = this.width / 2
                val centerY = this.height / 2 - 30

                val probability  = bossDrop.probability * 100;

                val roundedProbability = kotlin.math.ceil(probability * 100000) / 100000
                drawCenteredTextWithShadow(matrices, textRenderer, Text.of("Chest: ${bossDrop.boss}").asOrderedText(), centerX, centerY - 30, 0xFFFFFF)
                drawCenteredTextWithShadow(matrices, textRenderer, Text.of("Probability: $roundedProbability%").asOrderedText(), centerX, centerY - 20, 0xFFFFFF)
                drawCenteredTextWithShadow(matrices, textRenderer, Text.of("Amount of chests: ${bossDrop.chests.size}").asOrderedText(), centerX, centerY - 10, 0xFFFFFF)

                itemStack.count = bossDrop.dropAmount
                drawItemStack(matrices, itemStack, centerX - 8, centerY + 10, mouseX, mouseY)
            }

        }

        super.render(matrices, mouseX, mouseY, delta)
    }

    fun <T> rotateList(list: List<T>, matrixSize: Int = 3): List<T> {
        val rotatedList = mutableListOf<T>()

        for (i in 0 until matrixSize) {
            for (j in 0 until matrixSize) {
                val index = i + j * matrixSize
                if (index < list.size) {
                    rotatedList.add(list[index])
                }
            }
        }

        return rotatedList
    }

    fun renderIngredientList(matrices: MatrixStack, result: ItemStack, items: List<CraftingStationIngredient>, mouseX: Int, mouseY: Int){
        val x = (this.width - CRAFTING_TABLE_GUI_WIDTH) / 2
        val y = (this.height - CRAFTING_TABLE_GUI_HEIGHT) / 2
        val ingredientListWidth = 3 * ITEM_SPACING
        val ingredientListHeight = (items.size + 2) / 3 * 18
        val centerX = x + CRAFTING_TABLE_GUI_WIDTH / 2
        val ingredientListX = centerX - (ingredientListWidth + RESULT_ITEM_SPACING) / 2 - ITEM_SPACING
        val ingredientListY = y + (CRAFTING_TABLE_GUI_HEIGHT - ingredientListHeight) / 2


        for (i in items.indices) {
            val itemStack = items[i].toItem()
            val slotX = ingredientListX + (i % 3) * ITEM_SPACING
            val slotY = ingredientListY + (i / 3) * ITEM_SPACING
            drawItemStack(matrices, itemStack, slotX, slotY, mouseX, mouseY)
        }

        // draw an arrow between the ingredients and the result
        val arrowText = "->"
        val arrowWidth = MinecraftClient.getInstance().textRenderer.getWidth(arrowText)
        val arrowX = centerX - arrowWidth / 2 + 15
        val arrowY = ingredientListY + (ingredientListHeight - 8) / 2
        MinecraftClient.getInstance().textRenderer.draw(matrices, arrowText, arrowX.toFloat(), arrowY.toFloat(), 0xFFFFFF)


        drawItemStack(matrices, result, centerX + ITEM_SPACING*2, ingredientListY + (ingredientListHeight - ITEM_SPACING) / 2, mouseX, mouseY)
    }

    private fun drawItemStack(matrices: MatrixStack, stack: ItemStack, x: Int, y: Int, mouseX: Int, mouseY: Int) {
        val color = 0x80_000000.toInt()
        zOffset = -100
        fillGradient(matrices, x - 2, y - 2, x + 18, y + 18, color, color)

        MinecraftClient.getInstance().itemRenderer.renderInGuiWithOverrides(stack, x, y)
        MinecraftClient.getInstance().itemRenderer.renderGuiItemOverlay(MinecraftClient.getInstance().textRenderer, stack, x, y)
        if (mouseX >= x && mouseY >= y && mouseX < x + 16 && mouseY < y + 16) {
            renderTooltip(matrices, stack, mouseX, mouseY)
        }

    }

    override fun close() {
        ItemViewer.inOriginScreen = false
        AvalonAddons.guiToOpen = itemViewer
    }
}