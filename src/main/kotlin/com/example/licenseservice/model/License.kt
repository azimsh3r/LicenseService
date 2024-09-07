package com.example.licenseservice.model

import jakarta.persistence.*
import java.time.LocalDateTime

@Entity
@Table(name="license")
class License {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    var id: Int = 0

    @Column(name="name")
    lateinit var name: String

    @Column(name="expires_at")
    var expiresAt: LocalDateTime? = null

    @Column(name="organization_id")
    var organizationId: Int? = null
}
