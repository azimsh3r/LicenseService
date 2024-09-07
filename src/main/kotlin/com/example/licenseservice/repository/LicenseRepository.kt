package com.example.licenseservice.repository

import com.example.licenseservice.model.License
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface LicenseRepository : JpaRepository<License, Int> {
    override fun findAll() : MutableList<License>
}
