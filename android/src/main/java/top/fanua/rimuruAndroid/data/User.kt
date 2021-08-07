package top.fanua.rimuruAndroid.data

import kotlinx.serialization.Required
import kotlinx.serialization.Serializable

/**
 *
 * @author Doctor_Yin
 * @since 2021/8/8:0:26
 */

@Serializable
data class User(
    val id: String,
    val properties: List<Properties>
) {
    @Serializable
    data class Properties(
        val name: String,
        val value: String
    )
}

@Serializable
data class LoginRequest(
    val username: String,
    val password: String,
    val clientToken: String? = null,
    @Required
    val requestUser: Boolean = false,
    val agent: Agent = Agent()
) {
    @Serializable
    data class Agent(
        val name: String,
        val version: Int
    ) {
        constructor() : this(
            "Minecraft", 1
        )
    }
}

@Serializable
data class LoginResponse(
    val accessToken: String,
    val clientToken: String,
    val availableProfiles: List<UserProfile>,
    val selectedProfile: UserProfile? = null,
    val user: User? = null
)

@Serializable
data class UserProfile(
    val id: String,
    val name: String,
    val properties: List<Properties>? = null
) {
    @Serializable
    data class Properties(
        val name: String,
        val value: String,
        val signature: String? = null
    )
}

@Serializable
data class RefreshRequest(
    val accessToken: String,
    val clientToken: String,
    val requestUser: Boolean,
    val selectedProfile: UserProfile? = null
)

@Serializable
data class RefreshResponse(
    val accessToken: String,
    val clientToken: String,
    val selectedProfile: UserProfile?,
    val user: User
)

@Serializable
data class TokenRequest(
    val accessToken: String,
    val clientToken: String?
)

@Serializable
data class SignOutRequest(
    val username: String,
    val password: String
)
