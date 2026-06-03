package kr.app.calto.infrastructure.repository

import kr.app.calto.infrastructure.entities.RefreshTokenEntity
import org.springframework.data.jpa.repository.JpaRepository

interface RefreshTokenRepository : JpaRepository<RefreshTokenEntity, Long> {
    fun findByToken(token: String): RefreshTokenEntity?

    fun deleteByToken(token: String)
}
