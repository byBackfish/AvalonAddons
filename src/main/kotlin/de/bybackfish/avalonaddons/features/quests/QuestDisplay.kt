package de.bybackfish.avalonaddons.features.quests

import de.bybackfish.avalonaddons.core.annotations.Category
import de.bybackfish.avalonaddons.core.annotations.Property
import de.bybackfish.avalonaddons.core.event.Subscribe
import de.bybackfish.avalonaddons.core.feature.Feature
import de.bybackfish.avalonaddons.events.RenderScreenEvent
import de.bybackfish.avalonaddons.utils.drawTextInWorld
import de.bybackfish.avalonaddons.utils.renderBeaconBeam
import gg.essential.vigilance.data.PropertyType
import net.minecraft.text.Text
import net.minecraft.util.math.BlockPos
import net.minecraft.util.math.Vec3d
import net.minecraft.world.Heightmap
import java.awt.Color
import kotlin.math.roundToInt


@Category("Quests")
class QuestDisplay: Feature() {

    @Property(
        forceType = PropertyType.COLOR,
        description = "Color of the beacon target text"
    )
    var color: Color = Color.WHITE

    companion object {
        @JvmStatic
        var compassTarget: BlockPos? = null
    }

    @Subscribe
    fun onRender(event: RenderScreenEvent) {
        if(compassTarget == null) return


        val pos = mc.world!!.getTopPosition(Heightmap.Type.MOTION_BLOCKING, compassTarget)

        val distance = mc.player?.pos!!.distanceTo(Vec3d(pos.x.toDouble() + 0.5, pos.y.toDouble(), pos.z.toDouble() + 0.5)) ?: return

     //   renderBeaconBeam(event.stack, pos)
        drawTextInWorld(Text.of("Quest Location"), pos.x.toDouble() + 0.5, pos.y.toDouble(), pos.z.toDouble() + 0.5, 0.0, 0.0, 2.0,
            background = true,
            shadow = true,
            color = color
        )
        drawTextInWorld(Text.of("${distance.roundToInt()}m away"), pos.x.toDouble() + 0.5, pos.y.toDouble()-0.5, pos.z.toDouble() + 0.5, 0.0, 0.0, 1.5,
            background = true,
            shadow = true,
            color = color
        )

        println("Rendering Beacon Beam at $pos ($distance blocks away)")

    }

}