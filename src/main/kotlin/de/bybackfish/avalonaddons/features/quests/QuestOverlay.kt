package de.bybackfish.avalonaddons.features.quests

import de.bybackfish.avalonaddons.core.annotations.Category
import de.bybackfish.avalonaddons.core.annotations.Property
import de.bybackfish.avalonaddons.core.feature.Feature
import gg.essential.vigilance.data.PropertyType

@Category("Map")
class QuestOverlay : Feature() {

    @Property(forceType = PropertyType.SWITCH, description = "Render quest details on screen")
    var renderQuestDetailsOnScreen = false

    @Property(
        forceType = PropertyType.SWITCH,
        description = "Render a beacon on the quest location"
    )
    var renderBeaconOnQuestLocation = false

    @Property(
        forceType = PropertyType.SWITCH,
        description = "Render location of friends on the map"
    )
    var renderFriendsOnMap = false

}