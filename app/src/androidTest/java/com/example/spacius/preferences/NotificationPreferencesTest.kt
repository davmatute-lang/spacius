package com.example.spacius.preferences

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.Assert.*

/**
 * Pruebas Unitarias para NotificationPreferences
 * 
 * Tipo de Prueba: Unitaria (con Context)
 * Objetivo: Validar el correcto almacenamiento y recuperación de preferencias
 */
@RunWith(AndroidJUnit4::class)
class NotificationPreferencesTest {

    private lateinit var preferences: NotificationPreferences
    private lateinit var context: Context

    @Before
    fun setUp() {
        context = ApplicationProvider.getApplicationContext()
        preferences = NotificationPreferences(context)
        
        // Limpiar preferencias antes de cada test
        context.getSharedPreferences("notification_prefs", Context.MODE_PRIVATE)
            .edit()
            .clear()
            .apply()
    }

    // ==================== PRUEBAS DE ALL NOTIFICATIONS ====================
    
    @Test
    fun areAllNotificationsEnabled_valorPorDefecto_retornaTrue() {
        // Act
        val resultado = preferences.areAllNotificationsEnabled()
        
        // Assert
        assertTrue("El valor por defecto debería ser true", resultado)
    }
    
    @Test
    fun setAllNotificationsEnabled_guardaTrue_retornaTrue() {
        // Act
        preferences.setAllNotificationsEnabled(true)
        val resultado = preferences.areAllNotificationsEnabled()
        
        // Assert
        assertTrue("Debería guardar y recuperar true", resultado)
    }
    
    @Test
    fun setAllNotificationsEnabled_guardaFalse_retornaFalse() {
        // Act
        preferences.setAllNotificationsEnabled(false)
        val resultado = preferences.areAllNotificationsEnabled()
        
        // Assert
        assertFalse("Debería guardar y recuperar false", resultado)
    }
    
    // ==================== PRUEBAS DE BOOKING CONFIRMATIONS ====================
    
    @Test
    fun isBookingConfirmationsEnabled_valorPorDefecto_retornaTrue() {
        // Act
        val resultado = preferences.isBookingConfirmationsEnabled()
        
        // Assert
        assertTrue("El valor por defecto debería ser true", resultado)
    }
    
    @Test
    fun setBookingConfirmationsEnabled_guardaValor_loRecupera() {
        // Act
        preferences.setBookingConfirmationsEnabled(false)
        val resultado = preferences.isBookingConfirmationsEnabled()
        
        // Assert
        assertFalse("Debería guardar y recuperar el valor false", resultado)
    }
    
    @Test
    fun setBookingConfirmationsEnabled_cambiaMultiplesVeces_mantienelUltimoValor() {
        // Act
        preferences.setBookingConfirmationsEnabled(false)
        preferences.setBookingConfirmationsEnabled(true)
        preferences.setBookingConfirmationsEnabled(false)
        val resultado = preferences.isBookingConfirmationsEnabled()
        
        // Assert
        assertFalse("Debería mantener el último valor guardado", resultado)
    }
    
    // ==================== PRUEBAS DE BOOKING REMINDERS ====================
    
    @Test
    fun isBookingRemindersEnabled_valorPorDefecto_retornaTrue() {
        // Act
        val resultado = preferences.isBookingRemindersEnabled()
        
        // Assert
        assertTrue("El valor por defecto debería ser true", resultado)
    }
    
    @Test
    fun setBookingRemindersEnabled_guardaValor_loRecupera() {
        // Act
        preferences.setBookingRemindersEnabled(false)
        val resultado = preferences.isBookingRemindersEnabled()
        
        // Assert
        assertFalse("Debería guardar y recuperar el valor false", resultado)
    }
    
    // ==================== PRUEBAS DE NEW SPACES ====================
    
    @Test
    fun isNewSpacesEnabled_valorPorDefecto_retornaTrue() {
        // Act
        val resultado = preferences.isNewSpacesEnabled()
        
        // Assert
        assertTrue("El valor por defecto debería ser true", resultado)
    }
    
    @Test
    fun setNewSpacesEnabled_guardaValor_loRecupera() {
        // Act
        preferences.setNewSpacesEnabled(false)
        val resultado = preferences.isNewSpacesEnabled()
        
        // Assert
        assertFalse("Debería guardar y recuperar el valor false", resultado)
    }
    
    // ==================== PRUEBAS DE INTEGRACIÓN ====================
    
    @Test
    fun todasLasPreferencias_guardanYRecuperanIndependientemente() {
        // Act - Configurar diferentes valores
        preferences.setAllNotificationsEnabled(true)
        preferences.setBookingConfirmationsEnabled(false)
        preferences.setBookingRemindersEnabled(true)
        preferences.setNewSpacesEnabled(false)
        
        // Assert - Verificar que cada una mantiene su valor
        assertTrue("All notifications debería ser true", 
            preferences.areAllNotificationsEnabled())
        assertFalse("Booking confirmations debería ser false", 
            preferences.isBookingConfirmationsEnabled())
        assertTrue("Booking reminders debería ser true", 
            preferences.isBookingRemindersEnabled())
        assertFalse("New spaces debería ser false", 
            preferences.isNewSpacesEnabled())
    }
    
    @Test
    fun preferencias_persisten_entreDiferentesInstancias() {
        // Arrange - Crear primera instancia y guardar valores
        val preferences1 = NotificationPreferences(context)
        preferences1.setAllNotificationsEnabled(false)
        preferences1.setBookingRemindersEnabled(false)
        
        // Act - Crear segunda instancia
        val preferences2 = NotificationPreferences(context)
        
        // Assert - Verificar que los valores persisten
        assertFalse("Los valores deberían persistir entre instancias", 
            preferences2.areAllNotificationsEnabled())
        assertFalse("Los valores deberían persistir entre instancias", 
            preferences2.isBookingRemindersEnabled())
    }
    
    @Test
    fun cambiarAllNotifications_noAfectaOtrasPreferencias() {
        // Arrange
        preferences.setBookingConfirmationsEnabled(false)
        preferences.setBookingRemindersEnabled(false)
        
        // Act
        preferences.setAllNotificationsEnabled(false)
        
        // Assert - Las otras preferencias no deberían cambiar
        assertFalse("Booking confirmations no debería cambiar", 
            preferences.isBookingConfirmationsEnabled())
        assertFalse("Booking reminders no debería cambiar", 
            preferences.isBookingRemindersEnabled())
    }
}
