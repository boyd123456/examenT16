Feature: Gestion de citas del taller mecanico

  # TODO: escribir aqui los 4 escenariosv
  #
  # 1. Agendar un cambio de aceite de forma exitosa
  #    (la cita queda PROGRAMADA y se notifica el agendamiento)

  #
  # 2. Rechazar una reparacion de motor en la tarde
  #    (los servicios pesados solo se atienden entre las 08:00 y las 12:00)
  #
  # 3. Cancelar con penalidad por aviso tardio
  #    (cancelar con menos de 24 horas aplica una penalidad de 50.00)
  #
  # 4. Rechazar un agendamiento por horario ocupado
  #    (el mecanico ya tiene una cita programada que se superpone)


  Scenario: Agendar un cambio de aceite de forma exitosa
    Given existe un mecanico especializado en CAMBIO_ACEITE
    And la fecha actual es el 20 de septiembre de 2026 a las 09:00
    When registro una cita de CAMBIO_ACEITE para el 21 de septiembre de 2026 a las 10:00
    Then la cita queda en estado PROGRAMADA
    And se notifica el agendamiento

  Scenario: Rechazar una reparacion de transmision a las 16:00
    Given existe un mecanico especializado en REPARACION_TRANSMISION
    And la fecha actual es el 20 de septiembre de 2026 a las 10:00
    When intento registrar una REPARACION_TRANSMISION el 21 de septiembre de 2026 a las 16:00
    Then el servicio rechaza la cita por horario no permitido

  Scenario: Cancelar una cita programada para dentro de 3 dias
    Given existe una cita programada para dentro de 3 dias
    When cancelo la cita hoy
    Then la penalidad es 0.00
    And la cita queda en estado CANCELADA

  Scenario: Rechazar un agendamiento por horario ocupado
    Given el mecanico ya tiene una cita programada en ese horario
    When intento registrar otra cita en el mismo horario
    Then el servicio rechaza la cita por horario ocupado