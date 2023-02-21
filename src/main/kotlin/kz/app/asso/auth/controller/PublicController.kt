package kz.app.asso.auth.controller

import org.springframework.web.bind.annotation.*
import reactor.core.publisher.Mono

@RestController
@RequestMapping("/public")
class PublicController {

    @GetMapping("/version")
    fun version(): Mono<String> {
        return Mono.just("1.0.0")
    }
}
