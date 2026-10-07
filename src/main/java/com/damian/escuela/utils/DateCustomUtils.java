package com.damian.escuela.utils;

import com.damian.escuela.exceptions.DatoInvalidoException;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

public class DateCustomUtils {

    public static void validarHora(String hora, String mensaje) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm");

        try {
            LocalDateTime.parse(hora, formatter);
        } catch (DateTimeParseException e) {
            throw new DatoInvalidoException(mensaje);
        }
    }

    public static void validarFormatoPeriodo(String periodo, String mensaje) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("uuuu-MM");

        try {
            YearMonth.parse(periodo, formatter);
        } catch (DateTimeParseException e) {
            throw new DatoInvalidoException(mensaje);
        }
    }

    public static void compararHoras(String horaInicio, String horaFin, String mensaje) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm");
        LocalTime timeInicio = LocalTime.parse(horaInicio, formatter);
        LocalTime timeFin = LocalTime.parse(horaFin, formatter);

        if (!timeInicio.isBefore(timeFin))
            throw new DatoInvalidoException(mensaje);
    }
}
