package dev.artisra.dailyappkt.config

import io.swagger.v3.oas.annotations.OpenAPIDefinition
import io.swagger.v3.oas.annotations.info.Contact
import io.swagger.v3.oas.annotations.info.Info
import io.swagger.v3.oas.annotations.servers.Server
import org.springframework.context.annotation.Configuration

@Configuration
@OpenAPIDefinition(
    info = Info(
        title = "Daily App API",
        description = "Task, subtask, blocker, and note management API",
        version = "v1",
        contact = Contact(name = "Daily App Team")
    ),
    servers = [
        Server(url = "/", description = "Default server")
    ]
)
class OpenApiConfig