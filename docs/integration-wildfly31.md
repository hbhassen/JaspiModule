# Intégration dans WildFly 31

## Pré-requis
- JDK 17
- WildFly 31.x
- Maven 3.9+

## Installation du module
1. Compiler le projet : `mvn package`.
2. Copier `target/idp-jaspi-module-1.0.0.jar` dans `$WILDFLY_HOME/modules/com/example/idp/main/`.
3. Copier également `module.xml` dans le même répertoire.

## module.xml
```
<module xmlns="urn:jboss:module:1.9" name="com.example.idp">
    <resources>
        <resource-root path="idp-jaspi-module.jar"/>
    </resources>
    <dependencies>
        <module name="jakarta.servlet.api"/>
        <module name="jakarta.security.auth.message.api"/>
        <module name="org.jboss.logging"/>
    </dependencies>
</module>
```

## Configuration du domaine de sécurité
Ajoutez un domaine de sécurité qui délègue à JASPIC :
```
<security-domain name="idp-jaspic-domain" cache-type="default">
    <authentication>
        <login-module code="org.jboss.security.auth.spi.DelegateLoginModule" flag="required">
            <module-option name="delegateLoginModule" value="org.jboss.security.auth.spi.JASPILoginModule"/>
        </login-module>
    </authentication>
</security-domain>
```

## Déclaration du module JASPIC
Dans `login-config.xml` :
```
<login-config>
    <auth-module code="com.example.idp.IdpServerAuthModule" module="com.example.idp"/>
</login-config>
```

## Exemple web.xml
```
<login-config>
    <auth-method>FORM</auth-method>
    <realm-name>idp-jaspic-domain</realm-name>
</login-config>
```
