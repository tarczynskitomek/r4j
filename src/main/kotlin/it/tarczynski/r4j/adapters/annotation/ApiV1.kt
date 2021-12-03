package it.tarczynski.r4j.adapters.annotation

import org.springframework.core.annotation.AliasFor
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping(produces = ["application/acme.v1+json"])
annotation class ApiV1(

    @get:AliasFor(
        annotation = RequestMapping::class,
        attribute = "path"
    )
    val path: String = ""

)
