package com.example.licenseservice.dto

import com.fasterxml.jackson.annotation.JsonIgnore
import java.time.LocalDateTime

class LicenseDTO {
    lateinit var name: String

    lateinit var expiresAt: LocalDateTime

    var organization : OrganizationDTO = OrganizationDTO()

    @JsonIgnore
    var organizationId : Int? = null
}