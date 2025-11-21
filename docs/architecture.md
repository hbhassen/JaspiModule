# Architecture

## Overview
This module implements a JASPIC authentication flow dedicated to delegating authentication to an external Identity Provider. It targets **JDK 17** and **WildFly 31** using **Jakarta EE 10** APIs.

```
Client Browser --> WildFly 31 --> IdpServerAuthModule --> IdpAuthRequestBuilder --> Identity Provider
                                              \--> IdpResponseValidator --> IdpClient + TokenValidator
                                              \--> SessionManager --> HTTP session
```

### Key Components
- **IdpServerAuthModule**: Entry point for the container. Controls request validation, response validation, and session propagation.
- **IdpAuthRequestBuilder**: Crafts redirect URLs for the IdP.
- **IdpResponseValidator**: Delegates payload validation to pluggable collaborators.
- **IdpClient**: Optional HTTP client abstraction to contact the IdP.
- **TokenValidator**: Validates and parses IdP payloads.
- **SessionManager**: Handles session persistence of the authenticated principal.
- **IdpPrincipal/IdpUserInfo**: Representation of authenticated subjects and claims.
- **ConfigProperties**: Encapsulates configuration passed from WildFly module options.
- **LoggerUtils**: Provides consistent logging categories.

## Data Flow
1. A protected resource triggers `validateRequest` in `IdpServerAuthModule`.
2. The module checks the session for an existing `IdpPrincipal`.
3. If no principal exists, the module evaluates incoming parameters (`SAMLResponse`, `id_token`).
4. When a response exists, `IdpResponseValidator` converts it into `IdpUserInfo` which becomes `IdpPrincipal` injected into WildFly.
5. If no response exists, the module redirects the user to the IdP using `IdpAuthRequestBuilder` and returns `SEND_CONTINUE`.
6. `secureResponse` and `cleanSubject` finish the contract by allowing post-processing and logout support.

## Extensibility Points
- Replace the `IdpClient` implementation to call a SAML or OIDC endpoint.
- Provide a `TokenValidator` that performs cryptographic checks and extracts roles from custom claims.
- Override role claim mapping through `ConfigProperties#ROLE_CLAIM`.

## Thread Safety
Each instance is stateless aside from injected collaborators. `SessionManager` relies on servlet sessions and stores only serializable objects.
