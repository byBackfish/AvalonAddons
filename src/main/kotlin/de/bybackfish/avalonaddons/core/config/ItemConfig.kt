package de.bybackfish.avalonaddons.core.config

import de.bybackfish.avalonaddons.AvalonAddons
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import net.minecraft.client.MinecraftClient
import net.minecraft.item.ItemStack
import net.minecraft.item.Items
import net.minecraft.nbt.NbtCompound
import net.minecraft.nbt.StringNbtReader
import net.minecraft.text.Text
import java.io.Reader
import java.io.Writer
import java.net.URL
import java.nio.charset.StandardCharsets.UTF_8
import java.util.*
import java.util.zip.GZIPInputStream
import kotlin.reflect.full.memberProperties

object ItemConfig : PersistentSave
<HashMap<String, Item>>("items", HashMap()) {

    override fun read(json: Json, data: Reader) {
        this.data = json.decodeFromString(data.readText())
    }

    fun parse() {

    }

    override fun write(json: Json, writer: Writer) {
        writer.write(json.encodeToString(data))
    }

    override fun onCreate() {
        loadFromAPI()
    }

    fun loadFromAPI() {
        this.data.clear()
        val url =
            URL("http://185.2.102.91:5142/get/${MinecraftClient.getInstance().session.uuid}?key=${AvalonAddons.config.betaKey}")
        val items = AvalonAddons.json.decodeFromString<List<Item>>(url.readText())
        this.data.putAll(items.associateBy { it.id })

        dirty()
        println("Loaded ${items.size} items from API!")
    }
}

@Serializable
data class Item(
    val id: String,
    val name: String,
    val type: String,

    val itemData: String,

    val origin: OriginCompound
) {

    fun getNBT(): NbtCompound {
        return StringNbtReader.parse(deserializeNBT(itemData))
    }


    /* This is how it is gzipped

      public String gzipNbt(NBTTagCompound nbt) {
    return Base64.getEncoder().encodeToString(gzip(nbt.toString()));
     }

     */
    fun deserializeNBT(string: String): String {
        val bytes = Base64.getDecoder().decode(string)
        val gzip = GZIPInputStream(bytes.inputStream())
        val data = gzip.readBytes()
        return String(data, UTF_8)
    }

}

@Serializable
data class OriginCompound(
    val recipes: List<RecipeOrigin>,
    val bossDrops: List<BossDropOrigin>,
    val craftingStations: List<CraftingStationOrigin>
)

@Serializable
data class RecipeOrigin(
    val station: String,
    val type: String,
    val id: String,
    val amount: Int,

    val ingredients: List<CraftingStationIngredient>
)

@Serializable
data class BossDropOrigin(
    @SerialName("chest")
    val boss: String,

    @SerialName("amount")
    val dropAmount: Int,

    val chests: List<Chest>,

    val probability: Double,
)

@Serializable
data class Chest(
    val world: String,
    val x: Int,
    val y: Int,
    val z: Int
)

@Serializable
data class CraftingStationOrigin(
    val recipe: String,
    val station: String,

    val outputId: String,
    val outputType: String,
    val outputAmount: Int,

    val ingredients: List<CraftingStationIngredient>
)

@Serializable
data class CraftingStationIngredient(
    val id: String,
    val type: String,
    val amount: Int) {

    fun toItem(): ItemStack {
        val type = this.type.uppercase()
        val id = this.id.uppercase()

        if(type == "VANILLA" || type.isEmpty()) {
            stackFrom(id, amount)
        }
        if(id == "-" || id == "0" || id.isEmpty()) {
            return stackFrom(type, amount)
        }

        val item = ItemConfig.data["$type:$id"]
        if(item == null) {
            println("Failed to get item from config: $type:$id")
            return ItemStack(Items.BARRIER, amount).setCustomName(Text.of("$type:$id"))
        }

        val itemStack = ItemStack.fromNbt(item.getNBT())
        itemStack.count = amount
        return itemStack
    }
}


fun fromString(name: String): net.minecraft.item.Item? {
    return try {
        val fieldKt = Items::class.memberProperties.find { it.name == name.uppercase() } ?: return null
        return fieldKt.getter.call() as net.minecraft.item.Item
    } catch (e: Exception) {
        println("Failed to get item from string: $name")
        null
    }
}


fun stackFrom(name: String, amount: Int = 1): ItemStack {
    val item = fromString(name)
        ?: return ItemStack(Items.BARRIER, amount).setCustomName(Text.of(name))
    return ItemStack(item, amount)

}