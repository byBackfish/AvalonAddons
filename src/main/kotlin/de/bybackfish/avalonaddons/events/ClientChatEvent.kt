package de.bybackfish.avalonaddons.events

import de.bybackfish.avalonaddons.core.event.Event

open class ClientChatEvent(val message: String) : Event() {
    class Received(message: String) : ClientChatEvent(message) {}
    class Sent(message: String) : ClientChatEvent(message) {}
}