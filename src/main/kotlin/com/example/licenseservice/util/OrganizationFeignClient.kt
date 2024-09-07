package com.example.licenseservice.util

import com.example.licenseservice.dto.OrganizationDTO
import org.springframework.cloud.openfeign.FeignClient
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable

@FeignClient(name="organizationservice", url="http://localhost:7070")
interface OrganizationFeignClient {
    @GetMapping("/organization/{organizationId}")
    fun getOrganization(@PathVariable("organizationId") organizationId: Int) : OrganizationDTO
}
