import com.google.gson.Gson
import de.bybackfish.avalonaddons.avalon.Lootable
import de.bybackfish.avalonaddons.core.config.PersistentSave
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.decodeFromJsonElement
import net.minecraft.client.MinecraftClient
import java.io.Reader
import java.io.Writer


enum class TestEnum {
    A, B, C
}
object Test : PersistentSave<MutableMap<TestEnum?, List<Int>>>("enumTest", mutableMapOf()) {

    override fun read(data: Reader) {
        this.data = Json.decodeFromString(data.readText())
    }

    override fun write(writer: Writer) {
        writer.write(Json.encodeToString(data))
    }
}

fun main() {
    println(Lootable.values().map { it.name }.joinToString(", "))
}

fun set() {
    Test.get()[TestEnum.A] = listOf(1,2,3,4)
    PersistentSave.markDirty<Test>()
}