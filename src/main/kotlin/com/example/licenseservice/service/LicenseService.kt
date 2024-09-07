package com.example.licenseservice.service

import com.example.licenseservice.dto.LicenseDTO
import com.example.licenseservice.model.License
import com.example.licenseservice.repository.LicenseRepository
import com.example.licenseservice.util.OrganizationDiscoveryClient
import org.modelmapper.ModelMapper
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional
class LicenseService @Autowired constructor (val licenseRepository: LicenseRepository, val modelMapper: ModelMapper, val organizationDiscoveryClient: OrganizationDiscoveryClient) {

    fun findAll() : MutableList<LicenseDTO> {
        val licenseList: MutableList<License> = licenseRepository.findAll()
        val licenseDTOList : MutableList<LicenseDTO> = licenseList.map {modelMapper.map(it, LicenseDTO::class.java)}.toMutableList()

        licenseDTOList.map { it.organization = organizationDiscoveryClient.getOrganizationWithFeignClient(it.organizationId ?: throw RuntimeException("Organization Id cannot be null")) }
        return licenseDTOList
    }
}
