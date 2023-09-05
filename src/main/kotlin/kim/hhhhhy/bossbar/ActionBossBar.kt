package kim.hhhhhy.bossbar

import org.bukkit.boss.BarColor
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import taboolib.expansion.createHelper
import taboolib.common.LifeCycle
import taboolib.common.platform.Awake
import taboolib.common.platform.command.*
import taboolib.common.platform.function.getProxyPlayer
import taboolib.common5.Demand
import taboolib.module.kether.KetherLoader
import taboolib.module.kether.combinationParser
import java.util.UUID

/**
 * ExampleProject
 * com.github.username.ActionBossBar
 *
 * @author mical
 * @since 2023/6/23 12:10 AM
 */
// bossbar:bossbar -target YsGq_HY -id city -message "你妈死了" -color BLUE -time 10 -progress 1 -style PROGRESS
@Awake(LifeCycle.ENABLE)
fun initialize() {
    KetherLoader.registerParser(combinationParser {
        it.group(
            command("-target", then = text()),
            command("-id", then = text()),
            command("-message", then = text()),
            command("-color", then = text()),
            command("-time", then = int()),
            command("-progress", then = float()).option().defaultsTo(1f),
            command("-style", then = text()).option().defaultsTo("PROGRESS"),
        ).apply(it) { target, id, message, color, time, progress, style ->
            now {
                NMS.INSTANCE.sendBossBar(getProxyPlayer(target)?.castSafely<Player>() ?: return@now, message, progress, time, Overlay.valueOf(style), BarColor.valueOf(color), id)
            }
        }
    }, arrayOf("bossbar"), "bossbar", true)
}


// bbl send -target YsGq_HY -id city -message "你妈死了 111" -color BLUE -time 10 -progress 1 -style PROGRESS
@CommandHeader(name = "BossbarLite", aliases = ["bbl"], permission = "bbl.send")
object CommandBossbar {
    @CommandBody
    val main = mainCommand {
        createHelper()
    }

    @CommandBody
    val send = subCommand {
        dynamic("expression") {
            execute<CommandSender> { sender, _, argument ->
                val message = Demand("-message $argument").get("message") ?: ""
                val id = Demand("-id $argument").get("id") ?: UUID.randomUUID().toString()
                val target = Demand("-target $argument").get("target") ?: sender.name
                val color = Demand("-color $argument").get("color") ?: "RED"
                val time = Demand("-time $argument").get("time")?.toInt() ?: 0
                val progress = Demand("-progress $argument").get("progress")?.toFloat() ?: 1.toFloat()
                val style = Demand("-id $argument").get("style") ?: "PROGRESS"
                try {
                    NMS.INSTANCE.sendBossBar(getProxyPlayer(target)?.castSafely<Player>()!!, message, progress, time, Overlay.valueOf(style), BarColor.valueOf(color), id)
                } catch (_: Throwable) {
                }
            }
        }
    }
}
