package kr.app.calto.infrastructure.repository

import kr.app.calto.infrastructure.entities.BlogEntity
import org.springframework.data.jpa.repository.JpaRepository

interface BlogRepository : JpaRepository<BlogEntity, Long>
