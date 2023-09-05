package kim.hhhhhy.bossbar

import kim.hhhhhy.bossbar.BossbarLite.playerDataMap
import org.bukkit.boss.BarColor
import org.bukkit.entity.Player
import taboolib.common.io.getClass
import taboolib.common.platform.function.info
import taboolib.common.platform.function.submit
import taboolib.common.platform.function.submitAsync
import taboolib.common5.util.parseUUID
import taboolib.library.reflex.Reflex.Companion.getProperty
import taboolib.library.reflex.Reflex.Companion.setProperty
import taboolib.library.reflex.Reflex.Companion.unsafeInstance
import taboolib.module.chat.colored
import taboolib.module.nms.MinecraftVersion
import java.util.UUID

/**
 * SplendidEnchants
 * me.icebear03.splendidenchants.api.nms.NMSImpl
 *
 * @author mical
 * @since 2023/6/21 10:28 PM
 */
class NMSImpl : NMS() {

    override fun sendBossBar(player: Player, message: String, progress: Float, time: Int, overlay: Overlay, color: BarColor, id: String) {
        val uuid = UUID.nameUUIDFromBytes(id.toByteArray())
        when (MinecraftVersion.major) {
            // 1.9, 1.10, 1.11, 1.12, 1.13, 1.14, 1.15, 1.16
            1, 2, 3, 4, 5, 6, 7, 8 -> {
                sendPacket(
                    player,
                    NMS16PacketPlayOutBoss(),
                    "a" to uuid,
                    "b" to NMS16PacketPlayOutBossAction.ADD,
                    "c" to CraftChatMessage16.fromString(message.colored()).first(),
                    "d" to progress,
                    "e" to NMS16BossBattleBarColor.valueOf(color.name.uppercase()),
                    "f" to NMS16BossBattleBarStyle.valueOf(overlay.name.uppercase()),
                    "g" to false,
                    "h" to false,
                    "i" to false
                )
                val playerData = playerDataMap.computeIfAbsent(player) { PlayerBarData(player) }
                playerData.delete[uuid] = System.currentTimeMillis() + (time * 50L)
                if (time >= 0) {
                    submitAsync(period = 10L) {
                        val playerDeleteData = playerDataMap[player]
                        playerDeleteData?.delete?.forEach { (k, v) ->
                            if (v < System.currentTimeMillis()) {
                                playerDeleteData.delete.remove(k)

                                sendPacket(
                                    player,
                                    NMS16PacketPlayOutBoss(),
                                    "a" to uuid,
                                    "b" to NMS16PacketPlayOutBossAction.REMOVE
                                )
                            }
                        }
                    }
                }
            }
            // 1.17, 1.18, 1.19, 1.20
            9, 10, 11, 12 -> {
                sendPacket(
                    player,
                    NMSPacketPlayOutBoss::class.java.unsafeInstance(),
                    "id" to uuid,
                    "operation" to getClass("net.minecraft.network.protocol.game.PacketPlayOutBoss\$a").unsafeInstance().also {
                        it.setProperty("name", CraftChatMessage19.fromString(message.colored()).first())
                        it.setProperty("progress", progress)
                        it.setProperty("color", NMSBossBattleBarColor.valueOf(color.name.uppercase()))
                        it.setProperty("overlay", NMSBossBattleBarStyle.valueOf(overlay.name.uppercase()))
                        it.setProperty("darkenScreen", false)
                        it.setProperty("playMusic", false)
                        it.setProperty("createWorldFog", false)
                    }
                )
                val playerData = playerDataMap.computeIfAbsent(player) { PlayerBarData(player) }
                playerData.delete[uuid] = System.currentTimeMillis() + (time * 50L)
                if (time >= 0){
                    submitAsync(period = 10L) {
                        val playerDeleteData = playerDataMap[player]
                        playerDeleteData?.delete?.forEach { (k, v) ->
                            if (v < System.currentTimeMillis()) {
                                playerDeleteData.delete.remove(k)

                                sendPacket(
                                    player,
                                    NMSPacketPlayOutBoss::class.java.unsafeInstance(),
                                    "id" to uuid,
                                    "operation" to NMSPacketPlayOutBoss::class.java.getProperty<Any>(
                                        "REMOVE_OPERATION",
                                        true
                                    )!!
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}