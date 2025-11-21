# Application Usage

## Adding the Module
1. Install the module into WildFly as described in `integration-wildfly31.md`.
2. Reference the security domain in your WAR `web.xml`.

## Accessing the Principal
After authentication, the servlet request will contain the propagated principal:
```java
IdpPrincipal principal = (IdpPrincipal) request.getUserPrincipal();
Set<String> roles = principal.getRoles();
String email = principal.getAttributes().get("email");
```

## Handling Roles
Roles returned by the IdP (claim name configured by `idp.role.claim`) are mapped through `GroupPrincipalCallback`. Authorization checks can be performed with standard Servlet APIs:
```java
if (request.isUserInRole("admin")) {
    // protected logic
}
```

## Customizing Validation
Provide custom implementations and wire them through module options or a custom initializer:
- Implement `IdpClient` to call your IdP token endpoint or parse SAML assertions.
- Implement `TokenValidator` to verify signatures, expiration, and audience before creating `IdpUserInfo`.

## Logging
Use WildFly logging categories starting with `idp.module.*`. Configure in `standalone.xml` to adjust verbosity.

## Requirements
- JDK 17
- Maven 3.9+
- WildFly 31.x with Jakarta Servlet 5.x and Jakarta Authentication (JASPIC) 2.x modules available.
