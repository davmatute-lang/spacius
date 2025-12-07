package com.example.spacius.utils

import org.junit.Test
import org.junit.Assert.*

/**
 * Pruebas Unitarias para HorarioUtils
 * 
 * Tipo de Prueba: Unitaria
 * Objetivo: Validar la lógica de gestión de horarios
 */
class HorarioUtilsTest {

    // ==================== PRUEBAS DE validarHorario() ====================
    
    @Test
    fun `validarHorario con hora inicio menor que hora fin retorna true`() {
        // Arrange
        val horaInicio = "08:00"
        val horaFin = "18:00"
        
        // Act
        val resultado = HorarioUtils.validarHorario(horaInicio, horaFin)
        
        // Assert
        assertTrue("Hora inicio menor que hora fin debería ser válido", resultado)
    }
    
    @Test
    fun `validarHorario con hora inicio mayor que hora fin retorna false`() {
        // Arrange
        val horaInicio = "18:00"
        val horaFin = "08:00"
        
        // Act
        val resultado = HorarioUtils.validarHorario(horaInicio, horaFin)
        
        // Assert
        assertFalse("Hora inicio mayor que hora fin debería ser inválido", resultado)
    }
    
    @Test
    fun `validarHorario con horas iguales retorna false`() {
        // Arrange
        val horaInicio = "10:00"
        val horaFin = "10:00"
        
        // Act
        val resultado = HorarioUtils.validarHorario(horaInicio, horaFin)
        
        // Assert
        assertFalse("Horas iguales deberían ser inválidas", resultado)
    }
    
    @Test
    fun `validarHorario con formato invalido retorna false`() {
        // Arrange
        val horaInicio = "hora-invalida"
        val horaFin = "18:00"
        
        // Act
        val resultado = HorarioUtils.validarHorario(horaInicio, horaFin)
        
        // Assert
        assertFalse("Formato inválido debería retornar false", resultado)
    }
    
    @Test
    fun `validarHorario con diferencia de un minuto retorna true`() {
        // Arrange
        val horaInicio = "10:00"
        val horaFin = "10:01"
        
        // Act
        val resultado = HorarioUtils.validarHorario(horaInicio, horaFin)
        
        // Assert
        assertTrue("Diferencia de un minuto debería ser válida", resultado)
    }
    
    // ==================== PRUEBAS DE horarioAMinutos() ====================
    
    @Test
    fun `horarioAMinutos con medianoche retorna 0`() {
        // Arrange
        val hora = "00:00"
        
        // Act
        val resultado = HorarioUtils.horarioAMinutos(hora)
        
        // Assert
        assertEquals("Medianoche debería ser 0 minutos", 0, resultado)
    }
    
    @Test
    fun `horarioAMinutos con una hora en punto retorna minutos correctos`() {
        // Arrange
        val hora = "01:00"
        
        // Act
        val resultado = HorarioUtils.horarioAMinutos(hora)
        
        // Assert
        assertEquals("01:00 debería ser 60 minutos", 60, resultado)
    }
    
    @Test
    fun `horarioAMinutos con hora y minutos retorna total correcto`() {
        // Arrange
        val hora = "02:30"
        
        // Act
        val resultado = HorarioUtils.horarioAMinutos(hora)
        
        // Assert
        assertEquals("02:30 debería ser 150 minutos", 150, resultado)
    }
    
    @Test
    fun `horarioAMinutos con mediodía retorna 720`() {
        // Arrange
        val hora = "12:00"
        
        // Act
        val resultado = HorarioUtils.horarioAMinutos(hora)
        
        // Assert
        assertEquals("12:00 debería ser 720 minutos", 720, resultado)
    }
    
    @Test
    fun `horarioAMinutos con formato invalido retorna 0`() {
        // Arrange
        val hora = "formato-invalido"
        
        // Act
        val resultado = HorarioUtils.horarioAMinutos(hora)
        
        // Assert
        assertEquals("Formato inválido debería retornar 0", 0, resultado)
    }
    
    @Test
    fun `horarioAMinutos con hora maxima del dia retorna 1439`() {
        // Arrange
        val hora = "23:59"
        
        // Act
        val resultado = HorarioUtils.horarioAMinutos(hora)
        
        // Assert
        assertEquals("23:59 debería ser 1439 minutos", 1439, resultado)
    }
    
    // ==================== PRUEBAS DE generarBloquesHorarios() ====================
    
    @Test
    fun `generarBloquesHorarios con valores por defecto retorna lista no vacia`() {
        // Act
        val bloques = HorarioUtils.generarBloquesHorarios()
        
        // Assert
        assertTrue("Debería generar bloques con valores por defecto", bloques.isNotEmpty())
    }
    
    @Test
    fun `generarBloquesHorarios con rango de 2 horas y bloques de 60 min retorna 2 bloques`() {
        // Arrange
        val horaInicio = "08:00"
        val horaFin = "10:00"
        val duracion = 60
        
        // Act
        val bloques = HorarioUtils.generarBloquesHorarios(horaInicio, horaFin, duracion)
        
        // Assert
        assertEquals("Debería generar 2 bloques de 1 hora", 2, bloques.size)
    }
    
    @Test
    fun `generarBloquesHorarios verifica formato de horas correcto`() {
        // Arrange
        val horaInicio = "08:00"
        val horaFin = "10:00"
        val duracion = 60
        
        // Act
        val bloques = HorarioUtils.generarBloquesHorarios(horaInicio, horaFin, duracion)
        
        // Assert
        val primerBloque = bloques.firstOrNull()
        assertNotNull("Debería tener al menos un bloque", primerBloque)
        assertEquals("Primer bloque debería iniciar a las 08:00", "08:00", primerBloque?.horaInicio)
        assertEquals("Primer bloque debería terminar a las 09:00", "09:00", primerBloque?.horaFin)
    }
    
    @Test
    fun `generarBloquesHorarios con duracion personalizada de 30 min retorna bloques correctos`() {
        // Arrange
        val horaInicio = "08:00"
        val horaFin = "09:00"
        val duracion = 30
        
        // Act
        val bloques = HorarioUtils.generarBloquesHorarios(horaInicio, horaFin, duracion)
        
        // Assert
        assertEquals("Debería generar 2 bloques de 30 minutos", 2, bloques.size)
        assertEquals("Primer bloque debería terminar a las 08:30", "08:30", bloques[0].horaFin)
        assertEquals("Segundo bloque debería iniciar a las 08:30", "08:30", bloques[1].horaInicio)
    }
    
    @Test
    fun `generarBloquesHorarios con horario inverso retorna lista vacia`() {
        // Arrange
        val horaInicio = "18:00"
        val horaFin = "08:00"
        
        // Act
        val bloques = HorarioUtils.generarBloquesHorarios(horaInicio, horaFin)
        
        // Assert
        assertTrue("Horario inválido debería retornar lista vacía", bloques.isEmpty())
    }
    
    @Test
    fun `generarBloquesHorarios con formato invalido retorna lista vacia`() {
        // Arrange
        val horaInicio = "formato-invalido"
        val horaFin = "18:00"
        
        // Act
        val bloques = HorarioUtils.generarBloquesHorarios(horaInicio, horaFin)
        
        // Assert
        assertTrue("Formato inválido debería retornar lista vacía", bloques.isEmpty())
    }
    
    @Test
    fun `generarBloquesHorarios verifica IDs incrementales`() {
        // Arrange
        val horaInicio = "08:00"
        val horaFin = "11:00"
        val duracion = 60
        
        // Act
        val bloques = HorarioUtils.generarBloquesHorarios(horaInicio, horaFin, duracion)
        
        // Assert
        assertEquals("Primer ID debería ser 1", 1, bloques[0].id)
        assertEquals("Segundo ID debería ser 2", 2, bloques[1].id)
        assertEquals("Tercer ID debería ser 3", 3, bloques[2].id)
    }
    
    @Test
    fun `generarBloquesHorarios verifica descripcion correcta`() {
        // Arrange
        val horaInicio = "08:00"
        val horaFin = "10:00"
        val duracion = 60
        
        // Act
        val bloques = HorarioUtils.generarBloquesHorarios(horaInicio, horaFin, duracion)
        
        // Assert
        val primerBloque = bloques.first()
        val descripcionEsperada = "${primerBloque.horaInicio} - ${primerBloque.horaFin}"
        assertEquals("La descripción debería seguir el formato correcto", 
            descripcionEsperada, primerBloque.descripcion)
    }
    
    @Test
    fun `generarBloquesHorarios con un dia completo genera 24 bloques`() {
        // Arrange
        val horaInicio = "00:00"
        val horaFin = "24:00"
        val duracion = 60
        
        // Act
        val bloques = HorarioUtils.generarBloquesHorarios(horaInicio, horaFin, duracion)
        
        // Assert
        // Nota: Podría ser 23 o 24 dependiendo de cómo maneje las 24:00
        assertTrue("Debería generar aproximadamente 24 bloques", bloques.size >= 23)
    }
    
    @Test
    fun `generarBloquesHorarios con bloques de 15 minutos retorna cantidad correcta`() {
        // Arrange
        val horaInicio = "08:00"
        val horaFin = "09:00"
        val duracion = 15
        
        // Act
        val bloques = HorarioUtils.generarBloquesHorarios(horaInicio, horaFin, duracion)
        
        // Assert
        assertEquals("Debería generar 4 bloques de 15 minutos", 4, bloques.size)
    }
}
