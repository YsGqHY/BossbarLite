package kim.hhhhhy.bossbar

import org.bukkit.entity.Player
import java.util.UUID
import java.util.concurrent.ConcurrentHashMap


class PlayerBarData(val player: Player) {
    val delete = ConcurrentHashMap<UUID, Long>()
}