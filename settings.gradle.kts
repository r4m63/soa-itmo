rootProject.name = "soa-itmo"

include(
    ":lib:contract:common",
    ":lib:contract:movie",
    ":lib:contract:oscar",
    ":lib:services-common",
    ":services:movie-service",
    ":services:oscar-service",
)
