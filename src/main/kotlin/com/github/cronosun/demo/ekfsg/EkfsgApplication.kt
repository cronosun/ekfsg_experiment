package com.github.cronosun.demo.ekfsg

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class EkfsgApplication

fun main(args: Array<String>) {
    runApplication<EkfsgApplication>(*args)
}
