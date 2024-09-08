package com.example.licenseservice.service

import com.example.licenseservice.dto.LicenseDTO
import com.example.licenseservice.dto.OrganizationDTO
import com.example.licenseservice.model.License
import com.example.licenseservice.repository.LicenseRepository
import com.example.licenseservice.util.OrganizationDiscoveryClient
import com.netflix.hystrix.contrib.javanica.annotation.HystrixCommand
import com.netflix.hystrix.contrib.javanica.annotation.HystrixProperty
import org.modelmapper.ModelMapper
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDateTime

@Service
@Transactional
class LicenseService @Autowired constructor (val licenseRepository: LicenseRepository, val modelMapper: ModelMapper, val organizationDiscoveryClient: OrganizationDiscoveryClient) {

    fun getAllLicenses() : MutableList<LicenseDTO> {
        val licenseList: MutableList<License> = findAll()
        val licenseDTOList : MutableList<LicenseDTO> = licenseList.map {modelMapper.map(it, LicenseDTO::class.java)}.toMutableList()

        licenseDTOList.map { it.organization = organizationDiscoveryClient.getOrganizationWithFeignClient(it.organizationId ?: return licenseDTOList) }
        return licenseDTOList
    }

    //The circuit breaker will interrupt any call to the getLicensesByOrg() method any time the call takes longer than 1,000 milliseconds (default).
    //and throw com.nextflix.hystrix.exception.HystrixRuntimeException
    //Sizing of thread pool = (requests per second at peak when the service is healthy * 99th percentile latency in seconds) + small amount of extra threads for overhead
    @HystrixCommand(
        fallbackMethod = "buildFallbackLicenseList",
        commandProperties = [
            HystrixProperty (name = "execution.isolation.thread.timeoutImMilliseconds", value = "3000"),

            //Amount of consecutive calls that must occur within a 10-second window before Hystrix will consider tripping the circuit breaker for the call
            HystrixProperty (name = "circuitBreaker.requestVolumeThreshold", value = "10"),

            //Percentage of request that must fail after requestVolumeThreshold has been passed
            HystrixProperty (name = "circuitBreaker.errorThresholdPercentage", value = "75"),

            //Amount of time Hystrix will sleep once the circuit breaker is tripped before Hystrix will allow another call through to see if the service is healthy again
            HystrixProperty (name = "circuitBreaker.sleepWindowInMilliseconds", value = "7000"),

            //Size of the window that will be used by Hystrix to monitor for problems with a service call.
            HystrixProperty (name = "metrics.rollingStats.timeInMilliseconds", value = "15000"),

            //The number of times statistics are collected in the window
            HystrixProperty (name = "metrics.rollingStats.numBuckets", value = "5")
        ],
        //ThreadPoolKey attribute defines the unique name of thread pool
        threadPoolKey = "allLicensesThreadPool",

        threadPoolProperties = [
            //The coreSize attribute lets you define the maximum number of threads in the thread pool.
            HystrixProperty(
                name = "coreSize",
                value = "30"
            ),

            //maxQueueSize lets you define a queue that sits in front of your thread pool and
            //that can queue incoming requests.
            //if you set the value to -1, a Java SynchronousQueue will be used to hold all incoming requests.
            HystrixProperty (
                name="maxQueueSize",
                value = "10"
            )
        ],
    )
    fun findAll() : MutableList<License> = licenseRepository.findAll()

    //By default, when you specify a @HystrixCommand annotation without properties,
    //the annotation will place all remote service calls under the same thread pool.
    @HystrixCommand(
        commandProperties = [
            HystrixProperty(
                name = "execution.isolation.thread.timeoutInMilliseconds",
                value = "3000"
            )
        ]
    )
    fun findOrganizationById(organizationId : Int) : OrganizationDTO = organizationDiscoveryClient.getOrganizationWithFeignClient(organizationId)

    //Fallbacks are a mechanism to provide a course of action when a resource has timed out or failed
    fun buildFallbackLicenseList() : MutableList<License> {
        val license = License()

        license.name = "unavailable"
        license.organizationId = null
        license.expiresAt = LocalDateTime.now()

        return mutableListOf(License())
    }
}
