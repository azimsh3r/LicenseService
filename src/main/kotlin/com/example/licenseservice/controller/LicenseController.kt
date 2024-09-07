package com.example.licenseservice.controller

import com.example.licenseservice.service.LicenseService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/all")
class LicenseController @Autowired constructor (val licenseService: LicenseService){

    @GetMapping
    fun getAllLicenses() : ResponseEntity<Any> {
        val licenseList = licenseService.findAll()

        if (licenseList.isNotEmpty())
            return ResponseEntity(licenseList, HttpStatus.OK)

        return ResponseEntity(HttpStatus.NO_CONTENT)
    }
}