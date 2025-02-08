package com.microservice.inventario.client;


import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "msvc-authservice", url = "localhost:8050")
public interface AuthenticatedClient {


    @GetMapping("/api/auth/is-token-valid/{token}")
    AuthenticationTokenResponse authenticationTokenResponse(@PathVariable String token);

}
