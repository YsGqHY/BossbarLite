package kim.hhhhhy.bossbar

import org.bukkit.entity.Player
import taboolib.common.platform.Plugin
import taboolib.common.platform.function.info
import java.util.concurrent.ConcurrentHashMap

object BossbarLite : Plugin() {

    val playerDataMap = ConcurrentHashMap<Player, PlayerBarData>()

    override fun onEnable() {
        info("成功加载BossbarLite！")
    }
}