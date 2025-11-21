# Architecture

## Overview
The `idp-jaspi-module` project delivers a standalone JASPIC authentication module ready for WildFly 31. It exposes a small set of cohesive components to orchestrate IdP redirects, validate responses, and propagate principals to the application server.

## Components
- **IdpServerAuthModule**: JASPIC entry point handling redirect, validation, and propagation.
- **IdpAuthRequestBuilder**: Builds authorization URLs for SAML or OIDC style IdPs.
- **IdpResponseValidator**: Delegates token verification to a `TokenValidator` strategy and returns `IdpUserInfo`.
- **IdpPrincipal**: Wraps `IdpUserInfo` as a `Principal` exposed to the application layer.
- **SessionManager**: Persists `IdpUserInfo` in `HttpSession` to avoid repeated IdP round trips.
- **ConfigProperties**: Immutable configuration collected from WildFly module options or properties.
- **LoggerUtils**: Centralized logger factory and helpers.
- **Exceptions**: Domain-specific error types for configuration, communication, and validation issues.
- **SPI Interfaces**: `IdpClient` and `TokenValidator` permit plugging custom IdP and token logic.

## Authentication Flow
1. **Initialization**: WildFly instantiates `IdpServerAuthModule`, passing configuration options that become `ConfigProperties`.
2. **Request Handling** (`validateRequest`):
   - If session already holds `IdpUserInfo`, the principal is propagated and authentication succeeds.
   - If `SAMLResponse` or `id_token` is present, `IdpResponseValidator` validates it, builds `IdpPrincipal`, stores identity in session, and returns `SUCCESS`.
   - Otherwise, `IdpAuthRequestBuilder` constructs a redirect URL and `SEND_CONTINUE` is returned.
3. **Response Security** (`secureResponse`): No additional actions; returns `SEND_SUCCESS`.
4. **Cleanup** (`cleanSubject`): Clears subject and session-stored identity when requested.

## Extension Points
- Swap `TokenValidator` inside `IdpResponseValidator` to perform cryptographic checks for SAML assertions or JWT tokens.
- Implement `IdpClient` if richer IdP interactions are required (e.g., dynamic parameters, back-channel exchanges).

## Dependencies
- JDK 17
- Jakarta Security 2.x
- Jakarta Servlet 5.x
- org.jboss.logging
