package com.example.licenseservice

import org.modelmapper.ModelMapper
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.cloud.client.discovery.EnableDiscoveryClient
import org.springframework.cloud.client.loadbalancer.LoadBalanced
import org.springframework.cloud.context.config.annotation.RefreshScope
import org.springframework.cloud.openfeign.EnableFeignClients
import org.springframework.context.annotation.Bean
import org.springframework.web.client.RestTemplate

@SpringBootApplication
//reloads spring configuration by enabling /refresh endpoint
@RefreshScope
//enable eureka discovery usage
@EnableDiscoveryClient
//enable FeignClient
@EnableFeignClients
class LicenseServiceApplication {

    @Bean
    fun modelMapper() = ModelMapper()

    @Bean
    @LoadBalanced
    fun restTemplate () = RestTemplate()
}

fun main(args: Array<String>) {
    runApplication<LicenseServiceApplication>(*args)
}
