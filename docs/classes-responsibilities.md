# Classes and Responsibilities

## IdpServerAuthModule
- Implements JASPIC `ServerAuthModule`.
- Manages the authentication lifecycle: redirect → validate response → propagate principal.
- Emits structured logs and registers container callbacks.

## IdpAuthRequestBuilder
- Builds authorization URLs for the Identity Provider.
- Encodes parameters and preserves state tokens.

## IdpResponseValidator
- Coordinates `IdpClient` and `TokenValidator` to validate remote assertions.
- Normalizes responses into `IdpUserInfo`.

## IdpClient
- Interface for performing remote exchanges (token, SAML, code).
- Allows mocking in tests.

## TokenValidator
- Interface for signature/expiration validation and role extraction.
- Produces `IdpUserInfo` instances.

## IdpPrincipal
- Security principal injected into WildFly.
- Holds roles and arbitrary attributes.

## IdpUserInfo
- Immutable DTO representing user claims from the IdP.
- Built via a fluent builder.

## SessionManager
- Persists principals in HTTP sessions and clears them on logout.

## ConfigProperties
- Validates and exposes configuration options such as endpoints and client IDs.

## LoggerUtils
- Factory for JBoss loggers used throughout the module.

## Exceptions
- `IdpValidationException`: thrown when IdP payload fails validation.
- `IdpConfigurationException`: indicates missing configuration.
- `IdpCommunicationException`: wraps network issues.
