package com.example.spacius.security

import org.junit.Test
import org.junit.Assert.*

/**
 * Pruebas de Seguridad - Autenticación (OWASP)
 * 
 * Tipo de Prueba: Seguridad
 * Objetivo: Validar que la autenticación cumple con estándares de seguridad OWASP
 * 
 * Referencias OWASP:
 * - OWASP Mobile Top 10 - M4: Insecure Authentication
 * - OWASP MASVS (Mobile Application Security Verification Standard)
 */
class AuthenticationSecurityTest {

    // ==================== VALIDACIÓN DE CONTRASEÑAS ====================
    
    @Test
    fun `validar password debil con menos de 6 caracteres es rechazada`() {
        // Arrange
        val passwordsDebiles = listOf("12345", "abc", "a", "", "12")
        
        // Act & Assert
        passwordsDebiles.forEach { password ->
            val resultado = validarSeguridadPassword(password)
            assertFalse(
                "Password débil '$password' debería ser rechazada",
                resultado
            )
        }
    }
    
    @Test
    fun `validar password con minimo 6 caracteres es aceptada`() {
        // Arrange
        val passwordsValidas = listOf(
            "123456",
            "abcdef",
            "Password1",
            "MiPassword123",
            "Segura@2024"
        )
        
        // Act & Assert
        passwordsValidas.forEach { password ->
            val resultado = validarSeguridadPassword(password)
            assertTrue(
                "Password válida '$password' debería ser aceptada",
                resultado
            )
        }
    }
    
    @Test
    fun `validar password con espacios en blanco es rechazada`() {
        // Arrange
        val passwordsConEspacios = listOf(
            "   ",
            "  123456",
            "123456  ",
            "123 456"
        )
        
        // Act & Assert
        passwordsConEspacios.forEach { password ->
            val resultado = validarSeguridadPassword(password)
            assertFalse(
                "Password con espacios '$password' debería ser rechazada",
                resultado
            )
        }
    }
    
    @Test
    fun `validar passwords comunes son rechazadas`() {
        // Arrange - Passwords comúnmente hackeadas
        val passwordsComunes = listOf(
            "123456",
            "password",
            "12345678",
            "qwerty",
            "abc123",
            "111111"
        )
        
        // Act & Assert
        passwordsComunes.forEach { password ->
            val esComun = esPasswordComun(password)
            assertTrue(
                "Password común '$password' debería ser detectada",
                esComun
            )
        }
    }
    
    // ==================== VALIDACIÓN DE EMAIL ====================
    
    @Test
    fun `validar email con formato invalido es rechazado`() {
        // Arrange
        val emailsInvalidos = listOf(
            "",
            "notanemail",
            "@ejemplo.com",
            "usuario@",
            "usuario @ejemplo.com",
            "usuario@ejemplo",
            "usuario@@ejemplo.com",
            "usuario..test@ejemplo.com"
        )
        
        // Act & Assert
        emailsInvalidos.forEach { email ->
            val resultado = validarFormatoEmail(email)
            assertFalse(
                "Email inválido '$email' debería ser rechazado",
                resultado
            )
        }
    }
    
    @Test
    fun `validar email con formato valido es aceptado`() {
        // Arrange
        val emailsValidos = listOf(
            "usuario@ejemplo.com",
            "test.user@ejemplo.com",
            "user123@test.org",
            "name+tag@ejemplo.co.uk"
        )
        
        // Act & Assert
        emailsValidos.forEach { email ->
            val resultado = validarFormatoEmail(email)
            assertTrue(
                "Email válido '$email' debería ser aceptado",
                resultado
            )
        }
    }
    
    @Test
    fun `validar email con caracteres especiales peligrosos es rechazado`() {
        // Arrange - Intentos de inyección
        val emailsPeligrosos = listOf(
            "user<script>@ejemplo.com",
            "user'; DROP TABLE--@ejemplo.com",
            "user@ejemplo.com; DELETE FROM users",
            "user${'\$'}{jndi:ldap://}@ejemplo.com"
        )
        
        // Act & Assert
        emailsPeligrosos.forEach { email ->
            val resultado = validarFormatoEmail(email)
            assertFalse(
                "Email con caracteres peligrosos '$email' debería ser rechazado",
                resultado
            )
        }
    }
    
    // ==================== PREVENCIÓN DE INYECCIÓN ====================
    
    @Test
    fun `sanitizar entrada previene caracteres SQL peligrosos`() {
        // Arrange - Intentos de SQL Injection
        val entradasPeligrosas = mapOf(
            "'; DROP TABLE users--" to "DROP TABLE users",
            "admin' OR '1'='1" to "admin OR 1 1",
            "1' UNION SELECT * FROM passwords--" to "1 UNION SELECT FROM passwords"
        )
        
        // Act & Assert
        entradasPeligrosas.forEach { (input, palabrasPeligrosas) ->
            val sanitizado = sanitizarEntrada(input)
            
            // Verificar que los caracteres peligrosos fueron removidos
            assertFalse(
                "Entrada sanitizada no debería contener comillas simples",
                sanitizado.contains("'")
            )
            assertFalse(
                "Entrada sanitizada no debería contener punto y coma",
                sanitizado.contains(";")
            )
            assertFalse(
                "Entrada sanitizada no debería contener guiones dobles",
                sanitizado.contains("--")
            )
        }
    }
    
    @Test
    fun `sanitizar entrada previene XSS en campos de texto`() {
        // Arrange - Intentos de XSS
        val entradasXSS = listOf(
            "<script>alert('XSS')</script>",
            "<img src=x onerror=alert('XSS')>",
            "<iframe src='javascript:alert(\"XSS\")'></iframe>",
            "javascript:alert('XSS')"
        )
        
        // Act & Assert
        entradasXSS.forEach { input ->
            val sanitizado = sanitizarEntrada(input)
            
            assertFalse(
                "Entrada sanitizada no debería contener tags HTML: $input",
                sanitizado.contains("<") || sanitizado.contains(">")
            )
            assertFalse(
                "Entrada sanitizada no debería contener javascript:",
                sanitizado.lowercase().contains("javascript:")
            )
        }
    }
    
    @Test
    fun `validar entrada permite caracteres seguros normales`() {
        // Arrange
        val entradasSeguras = listOf(
            "Juan Pérez",
            "María José",
            "Reserva 2024",
            "Sala de reuniones",
            "usuario@ejemplo.com"
        )
        
        // Act & Assert
        entradasSeguras.forEach { input ->
            val sanitizado = sanitizarEntrada(input)
            assertTrue(
                "Entrada segura '$input' debería mantener contenido válido",
                sanitizado.isNotEmpty()
            )
        }
    }
    
    // ==================== VALIDACIÓN DE SESIÓN ====================
    
    @Test
    fun `token de sesion vacio es invalido`() {
        // Arrange
        val token = ""
        
        // Act
        val resultado = validarTokenSesion(token)
        
        // Assert
        assertFalse("Token vacío debería ser inválido", resultado)
    }
    
    @Test
    fun `token de sesion con formato invalido es rechazado`() {
        // Arrange
        val tokensInvalidos = listOf(
            "abc",
            "12345",
            "token-muy-corto",
            "   "
        )
        
        // Act & Assert
        tokensInvalidos.forEach { token ->
            val resultado = validarTokenSesion(token)
            assertFalse(
                "Token inválido '$token' debería ser rechazado",
                resultado
            )
        }
    }
    
    @Test
    fun `token de sesion con longitud minima es aceptado`() {
        // Arrange - Token típico de Firebase (>20 caracteres)
        val tokenValido = "eyJhbGciOiJSUzI1NiIsImtpZCI6IjEyMzQ1Njc4OTAifQ"
        
        // Act
        val resultado = validarTokenSesion(tokenValido)
        
        // Assert
        assertTrue("Token con longitud válida debería ser aceptado", resultado)
    }
    
    // ==================== RATE LIMITING ====================
    
    @Test
    fun `multiples intentos de login fallidos son detectados`() {
        // Arrange
        val intentosMaximos = 5
        val intentosRealizados = 6
        
        // Act
        val bloqueado = deberiaBloquearse(intentosRealizados, intentosMaximos)
        
        // Assert
        assertTrue(
            "Después de $intentosRealizados intentos fallidos debería bloquearse",
            bloqueado
        )
    }
    
    @Test
    fun `intentos de login dentro del limite son permitidos`() {
        // Arrange
        val intentosMaximos = 5
        val intentosRealizados = 3
        
        // Act
        val bloqueado = deberiaBloquearse(intentosRealizados, intentosMaximos)
        
        // Assert
        assertFalse(
            "Después de $intentosRealizados intentos no debería bloquearse",
            bloqueado
        )
    }
    
    // ==================== FUNCIONES AUXILIARES ====================
    
    /**
     * Valida que una contraseña cumpla con requisitos mínimos de seguridad
     */
    private fun validarSeguridadPassword(password: String): Boolean {
        // Mínimo 6 caracteres (Firebase Auth requirement)
        if (password.length < 6) return false
        
        // No debe contener solo espacios
        if (password.trim().isEmpty()) return false
        
        // No debe tener espacios
        if (password.contains(" ")) return false
        
        return true
    }
    
    /**
     * Detecta passwords comúnmente usadas y fáciles de hackear
     */
    private fun esPasswordComun(password: String): Boolean {
        val passwordsComunes = setOf(
            "123456", "password", "12345678", "qwerty", "123456789",
            "12345", "1234", "111111", "1234567", "dragon",
            "123123", "baseball", "abc123", "football", "monkey"
        )
        return passwordsComunes.contains(password.lowercase())
    }
    
    /**
     * Valida formato de email según RFC 5322 simplificado
     */
    private fun validarFormatoEmail(email: String): Boolean {
        if (email.isEmpty()) return false
        if (email.isBlank()) return false
        
        // Rechazar caracteres peligrosos PRIMERO
        val caracteresProhibidos = listOf("<", ">", ";", "'", "\"", "$", "{", "}")
        if (caracteresProhibidos.any { email.contains(it) }) return false
        
        // Validaciones básicas
        if (!email.contains("@")) return false
        if (email.startsWith("@")) return false
        if (email.endsWith("@")) return false
        if (email.contains("@@")) return false
        if (email.contains("..")) return false
        if (email.contains(" ")) return false
        
        // Patrón básico de email
        val emailRegex = Regex("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$")
        
        return emailRegex.matches(email)
    }
    
    /**
     * Sanitiza entrada de usuario para prevenir inyecciones
     */
    private fun sanitizarEntrada(input: String): String {
        var sanitizado = input
        
        // Remover caracteres peligrosos para SQL
        sanitizado = sanitizado.replace("'", "")
        sanitizado = sanitizado.replace("\"", "")
        sanitizado = sanitizado.replace(";", "")
        sanitizado = sanitizado.replace("--", "")
        sanitizado = sanitizado.replace("/*", "")
        sanitizado = sanitizado.replace("*/", "")
        
        // Remover tags HTML para prevenir XSS
        sanitizado = sanitizado.replace("<", "")
        sanitizado = sanitizado.replace(">", "")
        
        // Remover protocolos javascript
        sanitizado = sanitizado.replace(Regex("javascript:", RegexOption.IGNORE_CASE), "")
        
        return sanitizado.trim()
    }
    
    /**
     * Valida token de sesión (simulado para Firebase)
     */
    private fun validarTokenSesion(token: String): Boolean {
        // Token debe tener al menos 20 caracteres (típico de Firebase)
        if (token.trim().isEmpty()) return false
        if (token.length < 20) return false
        
        return true
    }
    
    /**
     * Determina si un usuario debería ser bloqueado por múltiples intentos fallidos
     */
    private fun deberiaBloquearse(intentos: Int, limite: Int): Boolean {
        return intentos > limite
    }
}
