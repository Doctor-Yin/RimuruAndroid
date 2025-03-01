package top.fanua.rimuruAndroid.client

import android.util.Log
import top.fanua.doctor.client.event.LoginSuccessEvent
import top.fanua.doctor.client.session.GameProfile
import top.fanua.doctor.core.api.event.EventEmitter
import top.fanua.doctor.core.api.event.EventListener
import top.fanua.doctor.network.event.ConnectionEvent
import top.fanua.doctor.network.event.NetLifeCycleEvent
import top.fanua.doctor.network.handler.onPacket
import top.fanua.doctor.network.handler.oncePacket
import top.fanua.doctor.network.utils.connection
import top.fanua.doctor.network.utils.setProtocolState
import top.fanua.doctor.protocol.api.ProtocolState
import top.fanua.doctor.protocol.definition.client.HandshakePacket
import top.fanua.doctor.protocol.definition.login.client.LoginStartPacket
import top.fanua.doctor.protocol.definition.login.server.DisconnectPacket
import top.fanua.doctor.protocol.definition.login.server.EncryptionRequestPacket
import top.fanua.doctor.protocol.definition.login.server.LoginSuccessPacket
import top.fanua.doctor.protocol.definition.login.server.SetCompressionPacket
import top.fanua.rimuruAndroid.models.RimuruViewModel

/**
 *
 * @author Doctor_Yin
 * @since 2021/8/14:9:22
 */
class LoginListener(
    private val protocolVersion: Int = 340,
    var suffix: String,
    val name: String,
    val viewModel: RimuruViewModel
) : EventListener {
    override fun initListen(emitter: EventEmitter) {
        emitter.on(NetLifeCycleEvent.BeforeConnect) {
            try {
                viewModel.validateYggdrasilSession()
            } catch (e: Exception) {
                Log.e("error",e.message.toString())
            }
        }
        emitter.on(ConnectionEvent.Connected) { (ctx) ->
            ctx!!
            val connection = ctx.connection()
            connection.sendPacket(
                HandshakePacket(
                    protocolVersion,
                    connection.host + suffix,
                    connection.port,
                    ProtocolState.LOGIN
                )
            )
            ctx.setProtocolState(ProtocolState.LOGIN)

            connection.sendPacket(LoginStartPacket(name))

            //监听登录事件
            emitter.oncePacket<LoginSuccessPacket> {
                ctx.setProtocolState(ProtocolState.PLAY)
                emitter.emit(LoginSuccessEvent, GameProfile(packet.uUID, packet.userName))
            }
        }
        emitter.onPacket<EncryptionRequestPacket> {
            viewModel.encryption(packet, connection)
        }

        emitter.onPacket<SetCompressionPacket> {
            connection.setCompressionEnabled(packet.threshold)
        }

        emitter.onPacket<DisconnectPacket> {
            Log.e("连接断开", packet.reason)
        }
    }

}
