# Usage in Applications

## Injecting the Principal
After the IdP flow completes, WildFly exposes `IdpPrincipal` as the caller principal. You can access it via standard Servlet APIs:
```java
Principal principal = request.getUserPrincipal();
if (principal instanceof IdpPrincipal) {
    IdpUserInfo info = ((IdpPrincipal) principal).getUserInfo();
    // use roles and attributes
}
```

## Programmatic role checks
Roles extracted from the IdP are propagated with `GroupPrincipalCallback`. Use servlet role checks:
```java
if (request.isUserInRole("admin")) {
    // protected admin logic
}
```

## Custom Token Validation
Replace the default token validator by wiring a custom `IdpResponseValidator` at initialization time (e.g., via CDI producer when embedding or by extending the module in future iterations). Implement `TokenValidator` to verify signatures and claims and return an `IdpUserInfo` enriched with roles and attributes.

## Extending Configurations
Add module options in WildFly login configuration to align with your IdP. Common properties include:
- `idp.authorize.endpoint`
- `idp.client.id`
- `idp.redirect.uri`
- `idp.requested.scopes`

## Testing
The project ships with JUnit 5 and Mockito tests illustrating:
- Redirect URL construction (`IdpAuthRequestBuilderTest`)
- Session persistence (`SessionManagerTest`)
- Response validation (`IdpResponseValidatorTest`)
- Full JASPIC flow orchestration (`IdpServerAuthModuleTest`)

Run the suite with:
```
mvn test
```
