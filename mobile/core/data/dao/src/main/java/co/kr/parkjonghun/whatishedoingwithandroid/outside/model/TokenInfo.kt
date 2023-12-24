package co.kr.parkjonghun.whatishedoingwithandroid.outside.model

data class TokenInfo(
    val accessToken: String,
    val refreshToken: String,
    val expiresIn: Long,
    val tokenType: String,
)
