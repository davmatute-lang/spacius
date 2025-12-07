package com.example.spacius.utils

import org.junit.Test
import org.junit.Assert.*
import java.text.SimpleDateFormat
import java.util.*

/**
 * Pruebas Unitarias para DateTimeUtils
 * 
 * Tipo de Prueba: Unitaria
 * Objetivo: Validar la lógica de utilidades de fecha y hora
 */
class DateTimeUtilsTest {

    // ==================== PRUEBAS DE esFechaHoraFutura() ====================
    
    @Test
    fun `esFechaHoraFutura con fecha pasada retorna false`() {
        // Arrange - Preparar datos de prueba
        val fechaPasada = "2020-01-01"
        val horaPasada = "10:00"
        
        // Act - Ejecutar la función
        val resultado = DateTimeUtils.esFechaHoraFutura(fechaPasada, horaPasada)
        
        // Assert - Verificar resultado esperado
        assertFalse("Una fecha pasada debería retornar false", resultado)
    }
    
    @Test
    fun `esFechaHoraFutura con fecha futura retorna true`() {
        // Arrange
        val fechaFutura = "2030-12-31"
        val horaFutura = "15:00"
        
        // Act
        val resultado = DateTimeUtils.esFechaHoraFutura(fechaFutura, horaFutura)
        
        // Assert
        assertTrue("Una fecha futura debería retornar true", resultado)
    }
    
    @Test
    fun `esFechaHoraFutura con fecha actual pero hora pasada retorna false`() {
        // Arrange - Usar fecha de hoy pero hora pasada
        val calendar = Calendar.getInstance()
        val formatoFecha = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        val fechaHoy = formatoFecha.format(calendar.time)
        val horaPasada = "00:00" // Medianoche
        
        // Act
        val resultado = DateTimeUtils.esFechaHoraFutura(fechaHoy, horaPasada)
        
        // Assert
        assertFalse("Hora pasada del día actual debería retornar false", resultado)
    }
    
    @Test
    fun `esFechaHoraFutura con formato invalido retorna false`() {
        // Arrange
        val fechaInvalida = "fecha-invalida"
        val horaInvalida = "hora-invalida"
        
        // Act
        val resultado = DateTimeUtils.esFechaHoraFutura(fechaInvalida, horaInvalida)
        
        // Assert
        assertFalse("Formato inválido debería retornar false", resultado)
    }
    
    @Test
    fun `esFechaHoraFutura con fecha vacia retorna false`() {
        // Arrange
        val fechaVacia = ""
        val horaVacia = ""
        
        // Act
        val resultado = DateTimeUtils.esFechaHoraFutura(fechaVacia, horaVacia)
        
        // Assert
        assertFalse("Fecha vacía debería retornar false", resultado)
    }
    
    // ==================== PRUEBAS DE hayConflictoHorario() ====================
    
    @Test
    fun `hayConflictoHorario con horarios superpuestos retorna true`() {
        // Arrange
        val inicio1 = "10:00"
        val fin1 = "12:00"
        val inicio2 = "11:00"
        val fin2 = "13:00"
        
        // Act
        val resultado = DateTimeUtils.hayConflictoHorario(inicio1, fin1, inicio2, fin2)
        
        // Assert
        assertTrue("Horarios superpuestos deberían generar conflicto", resultado)
    }
    
    @Test
    fun `hayConflictoHorario con horarios separados retorna false`() {
        // Arrange
        val inicio1 = "08:00"
        val fin1 = "10:00"
        val inicio2 = "11:00"
        val fin2 = "13:00"
        
        // Act
        val resultado = DateTimeUtils.hayConflictoHorario(inicio1, fin1, inicio2, fin2)
        
        // Assert
        assertFalse("Horarios separados no deberían generar conflicto", resultado)
    }
    
    @Test
    fun `hayConflictoHorario con horario2 completamente dentro de horario1 retorna true`() {
        // Arrange
        val inicio1 = "08:00"
        val fin1 = "18:00"
        val inicio2 = "10:00"
        val fin2 = "12:00"
        
        // Act
        val resultado = DateTimeUtils.hayConflictoHorario(inicio1, fin1, inicio2, fin2)
        
        // Assert
        assertTrue("Horario contenido dentro de otro debería generar conflicto", resultado)
    }
    
    @Test
    fun `hayConflictoHorario con horario1 completamente dentro de horario2 retorna true`() {
        // Arrange
        val inicio1 = "10:00"
        val fin1 = "12:00"
        val inicio2 = "08:00"
        val fin2 = "18:00"
        
        // Act
        val resultado = DateTimeUtils.hayConflictoHorario(inicio1, fin1, inicio2, fin2)
        
        // Assert
        assertTrue("Horario contenido dentro de otro debería generar conflicto", resultado)
    }
    
    @Test
    fun `hayConflictoHorario con horarios consecutivos exactos retorna false`() {
        // Arrange - El fin de uno es el inicio del otro
        val inicio1 = "08:00"
        val fin1 = "10:00"
        val inicio2 = "10:00"
        val fin2 = "12:00"
        
        // Act
        val resultado = DateTimeUtils.hayConflictoHorario(inicio1, fin1, inicio2, fin2)
        
        // Assert
        assertFalse("Horarios consecutivos no deberían generar conflicto", resultado)
    }
    
    @Test
    fun `hayConflictoHorario con formato invalido retorna false`() {
        // Arrange
        val inicio1 = "formato-invalido"
        val fin1 = "12:00"
        val inicio2 = "11:00"
        val fin2 = "13:00"
        
        // Act
        val resultado = DateTimeUtils.hayConflictoHorario(inicio1, fin1, inicio2, fin2)
        
        // Assert
        assertFalse("Formato inválido debería retornar false sin lanzar excepción", resultado)
    }
    
    @Test
    fun `hayConflictoHorario con inicio parcialmente superpuesto retorna true`() {
        // Arrange - El inicio2 cae dentro del rango 1
        val inicio1 = "10:00"
        val fin1 = "12:00"
        val inicio2 = "11:30"
        val fin2 = "13:30"
        
        // Act
        val resultado = DateTimeUtils.hayConflictoHorario(inicio1, fin1, inicio2, fin2)
        
        // Assert
        assertTrue("Inicio superpuesto debería generar conflicto", resultado)
    }
    
    @Test
    fun `hayConflictoHorario con fin parcialmente superpuesto retorna true`() {
        // Arrange - El fin2 cae dentro del rango 1
        val inicio1 = "11:00"
        val fin1 = "13:00"
        val inicio2 = "09:00"
        val fin2 = "12:00"
        
        // Act
        val resultado = DateTimeUtils.hayConflictoHorario(inicio1, fin1, inicio2, fin2)
        
        // Assert
        assertTrue("Fin superpuesto debería generar conflicto", resultado)
    }
    
    @Test
    fun `hayConflictoHorario con horarios identicos retorna true`() {
        // Arrange
        val inicio1 = "10:00"
        val fin1 = "12:00"
        val inicio2 = "10:00"
        val fin2 = "12:00"
        
        // Act
        val resultado = DateTimeUtils.hayConflictoHorario(inicio1, fin1, inicio2, fin2)
        
        // Assert
        assertTrue("Horarios idénticos deberían generar conflicto", resultado)
    }
}
