package edu.pe.cibertec.taller.servicio;

import edu.pe.cibertec.taller.excepcion.HorarioOcupadoException;
import edu.pe.cibertec.taller.repositorio.RepositorioCitas;
import edu.pe.cibertec.taller.repositorio.RepositorioMecanicos;
import edu.pe.cibertec.taller.servicio.impl.ServicioCitasImpl;
import edu.pe.cibertec.taller.util.ProveedorFechaHora;
import edu.pe.cibertec.taller.util.ServicioNotificaciones;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;


import edu.pe.cibertec.taller.excepcion.FechaInvalidaException;
import edu.pe.cibertec.taller.modelo.Cita;
import edu.pe.cibertec.taller.modelo.EstadoCita;
import edu.pe.cibertec.taller.modelo.Mecanico;
import edu.pe.cibertec.taller.modelo.TipoServicio;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.*;


@ExtendWith(MockitoExtension.class)
class ServicioCitasImplTest {

	@Mock
	private RepositorioMecanicos repositorioMecanicos;

	@Mock
	private RepositorioCitas repositorioCitas;

	@Mock
	private ProveedorFechaHora proveedorFechaHora;

	@Mock
	private ServicioNotificaciones servicioNotificaciones;

	private ServicioCitasImpl servicioCitas;

	@BeforeEach
	void inicializar() {
		servicioCitas = new ServicioCitasImpl(repositorioMecanicos, repositorioCitas,
				proveedorFechaHora, servicioNotificaciones);





	}

	@Test
	@DisplayName("Agendar una cita valida la guarda, notifica y la retorna en estado PROGRAMADA")
	void agendarCitaExitosa() {

        // Arrange
        String ambarPlacaExitosa = "BOY-649";
        Long idMecanico = 1L;

        LocalDateTime fechaReloj =
                LocalDateTime.of(2026, 9, 20, 10, 0);

        LocalDateTime fechaCita =
                LocalDateTime.of(2026, 9, 21, 10, 0);

        Mecanico mecanico = new Mecanico();
        mecanico.setId(idMecanico);
        mecanico.setNombre("Carlos Boyd");
        mecanico.setEspecialidad(TipoServicio.CAMBIO_ACEITE);

        when(repositorioMecanicos.findById(idMecanico))
                .thenReturn(Optional.of(mecanico));

        when(proveedorFechaHora.ahora())
                .thenReturn(fechaReloj);

        when(repositorioCitas.findByMecanicoIdAndEstado(
                idMecanico,
                EstadoCita.PROGRAMADA
        ))
                .thenReturn(Collections.emptyList());

        when(repositorioCitas.save(any(Cita.class)))
                .thenAnswer(invocacion -> invocacion.getArgument(0));

        // Act
        Cita citaResultado = servicioCitas.agendarCita(
                idMecanico,
                ambarPlacaExitosa,
                TipoServicio.CAMBIO_ACEITE,
                fechaCita
        );

        // Assert
        assertEquals(
                EstadoCita.PROGRAMADA,
                citaResultado.getEstado()
        );

        assertEquals(
                1,
                citaResultado.getDuracionHoras()
        );

        verify(repositorioCitas)
                .save(any(Cita.class));

        verify(servicioNotificaciones)
                .notificarCitaAgendada(any(Cita.class));


	}

	@Test
	@DisplayName("Agendar con un mecanico inexistente lanza MecanicoNoEncontradoException")
	void agendarConMecanicoInexistente() {
		// Arrange
		// TODO

		// Act y Assert
		// TODO
	}

	@Test
	@DisplayName("Agendar cuando la especialidad no coincide lanza EspecialidadIncorrectaException")
	void agendarConEspecialidadIncorrecta() {
		// Arrange
		// TODO

		// Act y Assert
		// TODO
	}

	@Test
	@DisplayName("Un servicio pesado a las 15:00 se rechaza con HorarioNoPermitidoException")
	void agendarServicioPesadoEnLaTarde() {
		// Arrange
		// TODO

		// Act y Assert
		// TODO
	}

	@Test
	@DisplayName("Un servicio pesado a las 09:00 se acepta y se guarda")
	void agendarServicioPesadoEnLaManana() {
		// Arrange
		// TODO

		// Act
		// TODO

		// Assert
		// TODO
	}

	@Test
	@DisplayName("Agendar en una fecha del pasado lanza FechaInvalidaException")
	void agendarConFechaEnElPasado() {
        // Arrange
        String ambarPlaca = "BOY-649";
        Long idMecanico = 1L;

        LocalDateTime fechaReloj = LocalDateTime.of(2026, 9, 20, 9, 0);
        LocalDateTime fechaCita = LocalDateTime.of(2026, 9, 19, 10, 0);

        Mecanico mecanico = new Mecanico();
        mecanico.setId(idMecanico);
        mecanico.setNombre("Carlos Boyd");
        mecanico.setEspecialidad(TipoServicio.CAMBIO_ACEITE);

        when(repositorioMecanicos.findById(idMecanico))
                .thenReturn(Optional.of(mecanico));

        when(proveedorFechaHora.ahora())
                .thenReturn(fechaReloj);

        // Act
        FechaInvalidaException excepcion = assertThrows(
                FechaInvalidaException.class,
                () -> servicioCitas.agendarCita(
                        idMecanico,
                        ambarPlaca,
                        TipoServicio.CAMBIO_ACEITE,
                        fechaCita
                )
        );

        // Assert
        assertEquals(
                "La fecha de la cita debe ser posterior a la fecha actual",
                excepcion.getMessage()
        );

        verify(repositorioCitas, never()).save(any(Cita.class));
	}

    @Test
    @DisplayName("Agendar una cita igual a la hora actual lanza FechaInvalidaException")
    void agendarConFechaIgualAlReloj() {

        // Arrange
        String ambarPlacaIgual = "BOY-649";
        Long idMecanico = 1L;

        LocalDateTime fechaReloj = LocalDateTime.of(2026, 9, 20, 10, 0);
        LocalDateTime fechaCita = LocalDateTime.of(2026, 9, 20, 10, 0);

        Mecanico mecanico = new Mecanico();
        mecanico.setId(idMecanico);
        mecanico.setNombre("Carlos Boyd");
        mecanico.setEspecialidad(TipoServicio.CAMBIO_ACEITE);

        when(repositorioMecanicos.findById(idMecanico))
                .thenReturn(Optional.of(mecanico));

        when(proveedorFechaHora.ahora())
                .thenReturn(fechaReloj);

        // Act y Assert
        assertThrows(
                FechaInvalidaException.class,
                () -> servicioCitas.agendarCita(
                        idMecanico,
                        ambarPlacaIgual,
                        TipoServicio.CAMBIO_ACEITE,
                        fechaCita
                )
        );

        verify(repositorioCitas, never()).save(any(Cita.class));
    }

	@Test
	@DisplayName("Agendar sobre una cita ya programada se rechaza con HorarioOcupadoException")
	void agendarConSuperposicion() {
        // Arrange
        String ambarPlacaSuperpuesta = "BOY-649";
        Long idMecanico = 1L;

        LocalDateTime fechaReloj =
                LocalDateTime.of(2026, 9, 19, 8, 0);

        LocalDateTime inicioCitaExistente =
                LocalDateTime.of(2026, 9, 20, 10, 0);

        LocalDateTime inicioNuevaCita =
                LocalDateTime.of(2026, 9, 20, 11, 0);

        Mecanico mecanico = new Mecanico();
        mecanico.setId(idMecanico);
        mecanico.setNombre("Carlos Boyd");
        mecanico.setEspecialidad(TipoServicio.MANTENIMIENTO_LIGERO);

        Cita citaExistente = new Cita();
        citaExistente.setMecanico(mecanico);
        citaExistente.setPlacaVehiculo("ABC-123");
        citaExistente.setTipoServicio(TipoServicio.MANTENIMIENTO_LIGERO);
        citaExistente.setFechaHoraInicio(inicioCitaExistente);
        citaExistente.setDuracionHoras(2);
        citaExistente.setEstado(EstadoCita.PROGRAMADA);

        when(repositorioMecanicos.findById(idMecanico))
                .thenReturn(Optional.of(mecanico));

        when(proveedorFechaHora.ahora())
                .thenReturn(fechaReloj);

        when(repositorioCitas.findByMecanicoIdAndEstado(
                idMecanico,
                EstadoCita.PROGRAMADA
        ))
                .thenReturn(Collections.singletonList(citaExistente));

        // Act y Assert
        assertThrows(
                HorarioOcupadoException.class,
                () -> servicioCitas.agendarCita(
                        idMecanico,
                        ambarPlacaSuperpuesta,
                        TipoServicio.MANTENIMIENTO_LIGERO,
                        inicioNuevaCita
                )
        );

        verify(repositorioCitas, never()).save(any(Cita.class));
	}


    @Test
    @DisplayName("Agendar una cita a las 12:00 se permite porque la cita anterior ya termino")
    void agendarSinSuperposicionALasDoce() {

        // Arrange
        String ambarPlacaDoce = "BOY-649";
        Long idMecanico = 1L;

        LocalDateTime fechaReloj =
                LocalDateTime.of(2026, 9, 19, 8, 0);

        LocalDateTime inicioCitaExistente =
                LocalDateTime.of(2026, 9, 20, 10, 0);

        LocalDateTime inicioNuevaCita =
                LocalDateTime.of(2026, 9, 20, 12, 0);

        Mecanico mecanico = new Mecanico();
        mecanico.setId(idMecanico);
        mecanico.setNombre("Carlos Boyd");
        mecanico.setEspecialidad(TipoServicio.MANTENIMIENTO_LIGERO);

        Cita citaExistente = new Cita();
        citaExistente.setMecanico(mecanico);
        citaExistente.setPlacaVehiculo("ABC-123");
        citaExistente.setTipoServicio(TipoServicio.MANTENIMIENTO_LIGERO);
        citaExistente.setFechaHoraInicio(inicioCitaExistente);
        citaExistente.setDuracionHoras(2);
        citaExistente.setEstado(EstadoCita.PROGRAMADA);

        when(repositorioMecanicos.findById(idMecanico))
                .thenReturn(Optional.of(mecanico));

        when(proveedorFechaHora.ahora())
                .thenReturn(fechaReloj);

        when(repositorioCitas.findByMecanicoIdAndEstado(
                idMecanico,
                EstadoCita.PROGRAMADA
        )).thenReturn(Collections.singletonList(citaExistente));

        when(repositorioCitas.save(any(Cita.class)))
                .thenAnswer(invocacion -> invocacion.getArgument(0));

        // Act
        Cita resultado = servicioCitas.agendarCita(
                idMecanico,
                ambarPlacaDoce,
                TipoServicio.MANTENIMIENTO_LIGERO,
                inicioNuevaCita
        );

        // Assert
        assertNotNull(resultado);
        assertEquals(inicioNuevaCita, resultado.getFechaHoraInicio());
        assertEquals(EstadoCita.PROGRAMADA, resultado.getEstado());

        verify(repositorioCitas).save(any(Cita.class));
    }



    @Test
    @DisplayName("Agendar una cita al dia siguiente a las 11:00 se permite")
    void agendarSinSuperposicionAlDiaSiguiente() {

        // Arrange
        String ambarPlacaDiaSiguiente = "BOY-649";
        Long idMecanico = 1L;

        LocalDateTime fechaReloj =
                LocalDateTime.of(2026, 9, 19, 8, 0);

        LocalDateTime inicioCitaExistente =
                LocalDateTime.of(2026, 9, 20, 10, 0);

        LocalDateTime inicioNuevaCita =
                LocalDateTime.of(2026, 9, 21, 11, 0);

        Mecanico mecanico = new Mecanico();
        mecanico.setId(idMecanico);
        mecanico.setNombre("Carlos Boyd");
        mecanico.setEspecialidad(TipoServicio.MANTENIMIENTO_LIGERO);

        Cita citaExistente = new Cita();
        citaExistente.setMecanico(mecanico);
        citaExistente.setPlacaVehiculo("ABC-123");
        citaExistente.setTipoServicio(TipoServicio.MANTENIMIENTO_LIGERO);
        citaExistente.setFechaHoraInicio(inicioCitaExistente);
        citaExistente.setDuracionHoras(2);
        citaExistente.setEstado(EstadoCita.PROGRAMADA);

        when(repositorioMecanicos.findById(idMecanico))
                .thenReturn(Optional.of(mecanico));

        when(proveedorFechaHora.ahora())
                .thenReturn(fechaReloj);

        when(repositorioCitas.findByMecanicoIdAndEstado(
                idMecanico,
                EstadoCita.PROGRAMADA
        )).thenReturn(Collections.singletonList(citaExistente));

        when(repositorioCitas.save(any(Cita.class)))
                .thenAnswer(invocacion -> invocacion.getArgument(0));

        // Act
        Cita resultado = servicioCitas.agendarCita(
                idMecanico,
                ambarPlacaDiaSiguiente,
                TipoServicio.MANTENIMIENTO_LIGERO,
                inicioNuevaCita
        );

        // Assert
        assertNotNull(resultado);
        assertEquals(inicioNuevaCita, resultado.getFechaHoraInicio());
        assertEquals(EstadoCita.PROGRAMADA, resultado.getEstado());

        verify(repositorioCitas).save(any(Cita.class));
    }


	@Test
	@DisplayName("Una cita que empieza justo cuando termina otra se acepta")
	void agendarCitaContigua() {
		// Arrange
		// TODO: una cita existente que termina a las 10:00 y la nueva que empieza a las 10:00

		// Act
		// TODO

		// Assert
		// TODO
	}

	@Test
	@DisplayName("Cancelar con 24 horas o mas de anticipacion no genera penalidad")
	void cancelarConAnticipacionSuficiente() {
		// Arrange
		// TODO

		// Act
		// TODO

		// Assert
		// TODO: penalidad 0, estado CANCELADA, notificacion
	}

	@Test
	@DisplayName("Cancelar con menos de 24 horas aplica una penalidad de 50.00")
	void cancelarConAvisoTardio() {
		// Arrange
		// TODO

		// Act
		// TODO

		// Assert
		// TODO
	}

	@Test
	@DisplayName("Cancelar una cita inexistente lanza CitaNoEncontradaException")
	void cancelarCitaInexistente() {
		// Arrange
		// TODO

		// Act y Assert
		// TODO
	}

	@Test
	@DisplayName("Cancelar una cita que ya fue cancelada lanza CitaNoCancelableException")
	void cancelarCitaYaCancelada() {
		// Arrange
		// TODO

		// Act y Assert
		// TODO
	}

	@Test
	@DisplayName("Buscar mecanico disponible retorna el primero sin citas superpuestas")
	void buscarMecanicoDisponibleRetornaPrimeroLibre() {
		// Arrange
		// TODO: dos mecanicos de la misma especialidad, el primero ocupado

		// Act
		// TODO

		// Assert
		// TODO
	}

	@Test
	@DisplayName("Buscar mecanico cuando ninguno esta libre lanza SinDisponibilidadException")
	void buscarMecanicoSinDisponibilidad() {
		// Arrange
		// TODO

		// Act y Assert
		// TODO
	}
}
