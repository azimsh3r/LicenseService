package com.example.licenseservice.util

import com.example.licenseservice.dto.OrganizationDTO
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.cloud.client.ServiceInstance
import org.springframework.cloud.client.discovery.DiscoveryClient
import org.springframework.http.HttpMethod
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Component
import org.springframework.web.client.RestTemplate

@Component
class OrganizationDiscoveryClient @Autowired constructor (val discoveryClient: DiscoveryClient, val restTemplate: RestTemplate, val organizationFeignClient: OrganizationFeignClient){

    val restTemplateWithoutRibbon = RestTemplate()

    //Manually choosing a service instance using Discovery Client
    fun getOrganizationWithDiscovery(orgId: Int): OrganizationDTO {
        val serviceInstances : List<ServiceInstance> = discoveryClient.getInstances("organizationservice")

        if (serviceInstances.isEmpty())
            throw RuntimeException("")

        val serviceUri = "${serviceInstances.first().uri}/organization/${orgId}"

        val restExchange : ResponseEntity<OrganizationDTO> = restTemplateWithoutRibbon.exchange(
            serviceUri,
            HttpMethod.GET,
            null,
            OrganizationDTO::class.java
        )
        return restExchange.body ?: OrganizationDTO()
    }

    //Template for Ribbon-enabled Rest Template (http://{applicationid}/v1/organizations/{organizationId})
    //It automatically inserts url inside
    fun getOrganizationWithRibbon(orgId : Int) : OrganizationDTO {
        val organizationDTO: ResponseEntity<OrganizationDTO> = restTemplate.exchange(
            "http://organizationservice/organization/{organizationId}",
            HttpMethod.GET,
            null,
            OrganizationDTO::class.java,
            orgId
        )
        return organizationDTO.body ?: OrganizationDTO()
    }

    fun getOrganizationWithFeignClient(orgId: Int) = organizationFeignClient.getOrganization(orgId)
}
