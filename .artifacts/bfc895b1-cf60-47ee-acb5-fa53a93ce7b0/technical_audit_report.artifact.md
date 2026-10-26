# Auditoría Técnica, Funcional y Lógica - Requisitos Funcionales (RF-01 a RF-40)
**Proyecto:** MiCarro
**Rol:** Arquitecto de Software Senior, Ingeniero de Calidad (QA Lead) y Especialista en Desarrollo Android con Kotlin.
**Enfoque:** Arquitectura Offline-First, Clean Architecture, MVVM, Room Database, WorkManager, Jetpack Compose.

---

## 2.1 Gestión de vehículos

### RF-01: El sistema debe permitir registrar un vehículo con placa, tipo, marca, línea, modelo, año y kilometraje actual.
- **1. Especificación del Paso a Paso (Checklist QA):**
  1. Abrir la aplicación y navegar a la pantalla de Gestión de Vehículos.
  2. Pulsar el botón flotante (FAB) de adición ("+").
  3. Seleccionar el tipo de vehículo (Automóvil, Camioneta, Motocicleta).
  4. Ingresar la placa (ej. `ABC-123` o `ABC123`).
  5. Seleccionar o ingresar marca y línea/modelo.
  6. Ingresar el año de fabricación (ej. `2023`) y el kilometraje actual (ej. `15000`).
  7. Pulsar el botón "Guardar".
  8. Verificar en la base de datos local (Room) y en la UI que el registro aparezca correctamente persistido.
- **2. Veredicto de Cumplimiento:** [CUMPLE PARCIALMENTE]
  *Justificación:* El proyecto actual implementa los campos básicos en `Vehiculo` y `VehiculosScreen`, pero falta separar formalmente el campo "línea" de "modelo" y validar restricciones estrictas de rangos de años (ej. desde 1900 hasta año actual + 1).
- **3. Trazabilidad con Reglas de Negocio y Arquitectura:**
  Interactúa directamente con la entidad `Vehiculo` en `Models.kt`, el repositorio `VehiculoRepository.kt` y el ViewModel `VehiculoViewModel.kt`. Requiere persistencia local con Room y sincronización opcional con Firestore. Se alinea con la RN de unicidad de placa.
- **4. Casos Borde, Excepciones y Escenarios Límite (Edge Cases):**
  - *Caso de Éxito (Happy Path):* Ingresar placa válida `XYZ-789`, marca `Toyota`, modelo `Corolla`, año `2022`, kilometraje `30000` y guardar exitosamente.
  - *Caso de Error o Límite (Unhappy Path):* Ingresar año futuro imposible (`3025`), kilometraje negativo (`-500`), o campos obligatorios en blanco (placa o marca vacías). El sistema debe rechazar la operación y mostrar errores descriptivos.
- **5. Vacíos Técnicos, Riesgos o Mejoras Propuestas:**
  Definir un validador centralizado en Kotlin (`VehiculoValidator`) para sanitizar y validar tipos de datos antes de impactar el repositorio.

---

### RF-02: El sistema debe permitir incluir datos opcionales: color, número VIN, tipo de combustible, cilindraje y fotografía.
- **1. Especificación del Paso a Paso (Checklist QA):**
  1. Abrir el formulario de registro/edición de vehículo.
  2. Llenar los campos obligatorios (RF-01).
  3. Desplegar la sección de datos opcionales.
  4. Ingresar color (`Rojo`), número VIN (17 caracteres alfanuméricos), tipo de combustible (`Gasolina`, `Diesel`, `Eléctrico`, `Híbrido`), cilindraje (`2000 cc`) y seleccionar una fotografía local o URI.
  5. Guardar y verificar que los campos opcionales se almacenen correctamente en la base de datos y se muestren en el detalle.
- **2. Veredicto de Cumplimiento:** [NO CUMPLE / FALTA ESPECIFICAR]
  *Justificación:* Actualmente el modelo `Vehiculo` y la UI solo disponen de `fotoUrl`, careciendo de los campos color, VIN, tipo de combustible y cilindraje.
- **3. Trazabilidad con Reglas de Negocio y Arquitectura:**
  Requiere expandir la entidad Room `VehiculoEntity` agregando columnas opcionales (nullable) y actualizar los mappers hacia el dominio.
- **4. Casos Borde, Excepciones y Escenarios Límite (Edge Cases):**
  - *Caso de Éxito:* Guardar un vehículo con todos los campos opcionales poblados correctamente.
  - *Caso de Error:* Ingresar un número VIN con longitud incorrecta (ej. 12 o 18 caracteres en lugar de los 17 estándar ISO 3779), o cilindraje negativo.
- **5. Vacíos Técnicos, Riesgos o Mejoras Propuestas:**
  Implementar validación de formato VIN mediante Regex y selector de tipo de combustible mediante Enum para evitar inconsistencias de datos.

---

### RF-03: El sistema debe permitir consultar, editar, archivar y reactivar un vehículo.
- **1. Especificación del Paso a Paso (Checklist QA):**
  1. Desde la pantalla principal de vehículos, consultar la lista completa.
  2. Seleccionar un vehículo para editar sus datos y guardar los cambios.
  3. Pulsar la opción de "Archivar" (cambiar estado lógico a inactivo/archivado).
  4. Verificar que el vehículo desaparezca de los selectores activos pero permanezca en la base de datos.
  5. Filtrar por vehículos archivados y pulsar "Reactivar".
- **2. Veredicto de Cumplimiento:** [CUMPLE PARCIALMENTE]
  *Justificación:* Se permite consultar, editar y eliminar físicamente (`eliminarVehiculo`), pero falta implementar el concepto de "archivar" y "reactivar" (soft-delete / estado activo/inactivo).
- **3. Trazabilidad con Reglas de Negocio y Arquitectura:**
  Involucra añadir un campo booleano `activo` o `archivado` en la entidad Room y adaptar las consultas (Queries) del DAO para excluir archivados por defecto en los selectores operativos.
- **4. Casos Borde, Excepciones y Escenarios Límite (Edge Cases):**
  - *Caso de Éxito:* Archivar un vehículo vendido y reactivarlo posteriormente sin perder su historial de mantenimientos.
  - *Caso de Error:* Intentar seleccionar un vehículo archivado como vehículo activo principal.
- **5. Vacíos Técnicos, Riesgos o Mejoras Propuestas:**
  Sustituir el borrado físico (`DELETE`) por un borrado lógico (`archivado = true`) para salvaguardar la integridad referencial del historial financiero y de mantenimiento.

---

### RF-04: El sistema debe soportar varios vehículos y permitir seleccionar uno como principal.
- **1. Especificación del Paso a Paso (Checklist QA):**
  1. Registrar al menos dos vehículos en el sistema.
  2. Verificar que el sistema asigne o permita marcar uno de ellos como "Vehículo Principal".
  3. Navegar al Dashboard y comprobar que la información mostrada corresponde al vehículo principal.
  4. Cambiar el vehículo principal desde el selector de la UI y comprobar que el Dashboard se actualiza instantáneamente.
- **2. Veredicto de Cumplimiento:** [CUMPLE]
  *Justificación:* Implementado recientemente mediante la propiedad `esPrincipal` en el modelo `Vehiculo`, la ordenación automática en el repositorio y la selección prioritaria en el ViewModel y Dashboard.
- **3. Trazabilidad con Reglas de Negocio y Arquitectura:**
  Garantiza que el flujo reactivo basado en `StateFlow` en `VehiculoViewModel` emita el vehículo principal por defecto al iniciar la aplicación.
- **4. Casos Borde, Excepciones y Escenarios Límite (Edge Cases):**
  - *Caso de Éxito:* Tener 3 vehículos y alternar el principal dinámicamente sin conflictos de estado.
  - *Caso de Error:* Eliminar el vehículo principal actual; el sistema debe reasignar automáticamente el rol principal al siguiente vehículo disponible en la lista.
- **5. Vacíos Técnicos, Riesgos o Mejoras Propuestas:**
  Asegurar mediante una transacción de base de datos que solo exista un único registro con `esPrincipal = true` en todo momento (Constraint o lógica de repositorio atómica).

---

### RF-05: El sistema debe impedir placas duplicadas dentro de la cuenta o instalación.
- **1. Especificación del Paso a Paso (Checklist QA):**
  1. Registrar un vehículo con la placa `XYZ-123`.
  2. Intentar registrar un segundo vehículo con la misma placa `XYZ-123` (probando variaciones como `xyz-123`, `XYZ123` o `xyz123`).
  3. Verificar que el sistema rechace el registro y muestre un mensaje de error indicando que la placa ya está registrada.
- **2. Veredicto de Cumplimiento:** [CUMPLE]
  *Justificación:* Implementado mediante normalización de placa (`trim().uppercase()`), comprobación en `VehiculoRepository.guardarVehiculo` y visualización de error en `VehiculosScreen`.
- **3. Trazabilidad con Reglas de Negocio y Arquitectura:**
  Corresponde a la Regla de Negocio de Unidades y Placas Únicas, soportada a nivel de índice único en la base de datos local (Room Unique Index).
- **4. Casos Borde, Excepciones y Escenarios Límite (Edge Cases):**
  - *Caso de Éxito:* Registrar placas distintas (`ABC-123` y `ABC-124`) sin inconvenientes.
  - *Caso de Error:* Intentar actualizar un vehículo existente asignándole la placa de otro vehículo ya registrado en la base de datos.
- **5. Vacíos Técnicos, Riesgos o Mejoras Propuestas:**
  Añadir un índice único (`@Index(value = ["placa"], unique = true)`) en la entidad Room para garantizar robustez a nivel de motor de base de datos, más allá de la validación en memoria.

---

## 2.2 Kilometraje

### RF-06: El usuario debe poder actualizar el kilometraje indicando fecha y valor del odómetro.
- **1. Especificación del Paso a Paso (Checklist QA):**
  1. Desde el Dashboard o detalle del vehículo, pulsar la opción "Actualizar Kilometraje".
  2. Ingresar la nueva lectura del odómetro (ej. `46000`).
  3. Seleccionar la fecha en la que se realizó la lectura (por defecto fecha actual).
  4. Confirmar y guardar. Verificar que el kilometraje actual del vehículo se actualice.
- **2. Veredicto de Cumplimiento:** [CUMPLE PARCIALMENTE]
  *Justificación:* Actualmente se actualiza el kilometraje y se registra `fechaActualizacionKm`, pero falta un diálogo o pantalla dedicada para ingresar explícitamente la fecha histórica de la lectura del odómetro (permite retrocompatibilidad y registros pasados).
- **3. Trazabilidad con Reglas de Negocio y Arquitectura:**
  Se conecta con el método `actualizarKilometraje` en `VehiculoRepository` y dispara la reevaluación de alertas de mantenimiento (`MantenimientoViewModel`).
- **4. Casos Borde, Excepciones y Escenarios Límite (Edge Cases):**
  - *Caso de Éxito:* Registrar un kilometraje mayor con la fecha de hoy.
  - *Caso de Error:* Ingresar un valor de odómetro extremadamente alto (desbordamiento de entero o número ilógico como `999999999`).
- **5. Vacíos Técnicos, Riesgos o Mejoras Propuestas:**
  Crear una tabla dedicada `HistorialKilometraje` en Room para asociar cada lectura con su respectiva fecha, odómetro y opcionalmente una nota u origen (ej. "Tanqueo", "Mantenimiento", "Lectura manual").

---

### RF-07: El sistema debe conservar el historial de lecturas de kilometraje.
- **1. Especificación del Paso a Paso (Checklist QA):**
  1. Realizar múltiples actualizaciones de kilometraje en fechas distintas para un vehículo.
  2. Navegar a la sección de Historial o Gráfica de Kilometraje del vehículo.
  3. Verificar que aparezca el listado cronológico completo con cada fecha, valor registrado y variación parcial.
- **2. Veredicto de Cumplimiento:** [NO CUMPLE / FALTA ESPECIFICAR]
  *Justificación:* Actualmente la app solo sobrescribe el campo `kilometrajeActual` y almacena un único timestamp `fechaActualizacionKm`, sin mantener una tabla de historial de lecturas independientes.
- **3. Trazabilidad con Reglas de Negocio y Arquitectura:**
  Requiere la creación de la entidad Room `HistorialKilometrajeEntity`, su DAO y su repositorio correspondiente, consumido por componentes analíticos o de reportes.
- **4. Casos Borde, Excepciones y Escenarios Límite (Edge Cases):**
  - *Caso de Éxito:* Consultar el historial de 12 meses de lecturas ordenadas de forma descendente.
  - *Caso de Error:* Vehículo sin lecturas previas; el historial debe mostrar estado vacío informativo.
- **5. Vacíos Técnicos, Riesgos o Mejoras Propuestas:**
  Implementar almacenamiento persistente de cada evento de kilometraje para alimentar futuros gráficos de uso mensual y estimación de desgaste.

---

### RF-08: El sistema debe advertir cuando una nueva lectura sea menor que la anterior y exigir confirmación o corrección.
- **1. Especificación del Paso a Paso (Checklist QA):**
  1. Registrar un vehículo con kilometraje actual de `50,000 km`.
  2. Intentar actualizar el kilometraje ingresando un valor menor, por ejemplo, `48,000 km`.
  3. Pulsar guardar y verificar que el sistema despliegue un diálogo de advertencia (Alerta de Odómetro Menor).
  4. Comprobar que se ofrecen dos opciones: "Corregir valor" o "Confirmar lectura anómala (ej. reemplazo de tablero)".
- **2. Veredicto de Cumplimiento:** [NO CUMPLE / FALTA ESPECIFICAR]
  *Justificación:* El código actual actualiza el kilometraje directamente sin validar si el nuevo valor es inferior al actual registrado.
- **3. Trazabilidad con Reglas de Negocio y Arquitectura:**
  Regla de validación de integridad de odómetro en la capa de ViewModel antes de invocar la persistencia en repositorio.
- **4. Casos Borde, Excepciones y Escenarios Límite (Edge Cases):**
  - *Caso de Éxito:* El usuario detecta el error al recibir la advertencia, corrige el número y procede con un kilometraje válido (`51,000 km`).
  - *Caso de Error:* Omitir la validación permitiría corromper los cálculos de mantenimientos basados en kilometraje (RF-09).
- **5. Vacíos Técnicos, Riesgos o Mejoras Propuestas:**
  Añadir lógica de comparación `nuevoKm < kilometrajeActual` en el ViewModel y disparar un estado de UI `UiState.OdometroMenorAdvertencia`.

---

### RF-09: El sistema debe recalcular las alertas relacionadas con kilometraje después de registrar una lectura válida.
- **1. Especificación del Paso a Paso (Checklist QA):**
  1. Configurar un mantenimiento programado a los `55,000 km` para un vehículo con `50,000 km` actuales (Estado: Al día).
  2. Actualizar el kilometraje a `54,500 km`.
  3. Verificar automáticamente que el estado del mantenimiento cambie a "Próximo a vencer" o "Vencido" según corresponda.
- **2. Veredicto de Cumplimiento:** [CUMPLE]
  *Justificación:* Implementado mediante llamadas a `mantenimientoViewModel.evaluarAlertas(vehiculoId, nuevoKm)` tanto al actualizar el kilometraje como al iniciar el Dashboard.
- **3. Trazabilidad con Reglas de Negocio y Arquitectura:**
  Se apoya en la reactividad de corrutinas de Kotlin y la lógica de negocio centralizada en `MantenimientoRepository` y `AlertaMantenimientoWorker`.
- **4. Casos Borde, Excepciones y Escenarios Límite (Edge Cases):**
  - *Caso de Éxito:* Al superar el kilometraje objetivo, el estado pasa instantáneamente de Próximo a Vencido.
  - *Caso de Error:* Actualizar kilometraje sin disparar la evaluación de alertas dejaría la UI desactualizada hasta reiniciar la app.
- **5. Vacíos Técnicos, Riesgos o Mejoras Propuestas:**
  Garantizar que la evaluación de alertas se ejecute dentro de una transacción o Observer de base de datos para máxima robustez offline-first.

---

## 2.3 Plan de mantenimiento

### RF-10: El usuario debe poder crear una actividad de mantenimiento con nombre, categoría, descripción y vehículo asociado.
- **1. Especificación del Paso a Paso (Checklist QA):**
  1. Navegar a la pantalla de Programación de Mantenimiento (`ProgramarMantenimientoScreen`).
  2. Seleccionar el vehículo asociado.
  3. Ingresar el título o nombre del mantenimiento (ej. "Cambio de pastillas de freno").
  4. Seleccionar la categoría y escribir una descripción detallada.
  5. Guardar la programación y verificar que se refleje en la lista de mantenimientos programados.
- **2. Veredicto de Cumplimiento:** [CUMPLE PARCIALMENTE]
  *Justificación:* Se permite crear con título, descripción y vehículo, pero falta un selector explícito de categoría de mantenimiento dentro del formulario de programación.
- **3. Trazabilidad con Reglas de Negocio y Arquitectura:**
  Interactúa con la entidad `MantenimientoProgramado` en `Models.kt` y `MantenimientoRepository`.
- **4. Casos Borde, Excepciones y Escenarios Límite (Edge Cases):**
  - *Caso de Éxito:* Crear un mantenimiento asociado al vehículo principal con todos sus campos completos.
  - *Caso de Error:* Intentar programar un mantenimiento sin haber seleccionado un vehículo o con el título vacío.
- **5. Vacíos Técnicos, Riesgos o Mejoras Propuestas:**
  Incluir un campo `categoria` (usando un Enum o entidad relacional) en el modelo `MantenimientoProgramado`.

---

### RF-11: La actividad debe poder programarse por fecha, kilometraje o ambos criterios.
- **1. Especificación del Paso a Paso (Checklist QA):**
  1. Abrir el formulario de programación de mantenimiento.
  2. Seleccionar el criterio de alerta mediante el desplegable (`Por Fecha`, `Por Kilometraje`, `Por Fecha y Kilometraje`).
  3. Validar que la UI muestre u oculte dinámicamente los campos de fecha objetivo y/o kilometraje objetivo según el criterio seleccionado.
  4. Guardar y verificar la correcta persistencia del criterio.
- **2. Veredicto de Cumplimiento:** [CUMPLE]
  *Justificación:* Implementado mediante el Enum `CriterioAlerta` (`FECHA`, `KILOMETRAJE`, `AMBOS`) y renderizado condicional en `ProgramarMantenimientoScreen`.
- **3. Trazabilidad con Reglas de Negocio y Arquitectura:**
  Utilizado por el motor de evaluación de alertas para determinar si una actividad requiere aviso por odómetro, por calendario o por ambos factores simultáneamente.
- **4. Casos Borde, Excepciones y Escenarios Límite (Edge Cases):**
  - *Caso de Éxito:* Configurar criterio `AMBOS` y verificar que salte la alerta por el criterio que se cumpla primero.
  - *Caso de Error:* Seleccionar criterio `KILOMETRAJE` pero dejar el kilometraje objetivo en blanco o en cero.
- **5. Vacíos Técnicos, Riesgos o Mejoras Propuestas:**
  Añadir validaciones cruzadas en el formulario para asegurar que si se elige `FECHA`, la fecha objetivo sea estrictamente futura.

---

### RF-12: El sistema debe incluir categorías iniciales editables: aceite, filtros, frenos, llantas, batería, refrigeración, suspensión, transmisión y otros.
- **1. Especificación del Paso a Paso (Checklist QA):**
  1. Ir a la configuración o sección de gestión de categorías de mantenimiento.
  2. Verificar que aparezcan por defecto: Aceite, Filtros, Frenos, Llantas, Batería, Refrigeración, Suspensión, Transmisión y Otros.
  3. Editar el nombre de una categoría o agregar una nueva categoría personalizada.
  4. Verificar que la categoría editada aparezca disponible en los selectores de programación.
- **2. Veredicto de Cumplimiento:** [NO CUMPLE / FALTA ESPECIFICAR]
  *Justificación:* Actualmente el sistema maneja títulos libres y plantillas fijas, pero no posee una entidad o tabla configurable de "Categorías de Mantenimiento editables".
- **3. Trazabilidad con Reglas de Negocio y Arquitectura:**
  Requiere una entidad Room `CategoriaMantenimientoEntity` precargada con valores semilla (seed data) en el primer arranque de la aplicación.
- **4. Casos Borde, Excepciones y Escenarios Límite (Edge Cases):**
  - *Caso de Éxito:* Renombrar la categoría "Frenos" a "Sistema de Frenado y ABS".
  - *Caso de Error:* Intentar eliminar una categoría que está siendo utilizada activamente por múltiples mantenimientos programados.
- **5. Vacíos Técnicos, Riesgos o Mejoras Propuestas:**
  Implementar la entidad de categorías con relaciones de clave foránea y protección contra eliminación de categorías en uso.

---

### RF-13: El usuario debe poder definir anticipación de la alerta en días o kilómetros.
- **1. Especificación del Paso a Paso (Checklist QA):**
  1. Al programar un mantenimiento, ubicar los campos de anticipación (ej. avisar con `500 km` o `15 días` de anticipación al objetivo).
  2. Ingresar valores válidos y guardar.
  3. Comprobar que el estado de la alerta cambie a "Próximo" cuando se alcance el umbral de anticipación configurado.
- **2. Veredicto de Cumplimiento:** [CUMPLE PARCIALMENTE]
  *Justificación:* Se manejan frecuencias (`frecuenciaKm`, `frecuenciaMeses`), pero falta un parámetro diferenciado y explícito para la "anticipación de alerta" independiente del intervalo de repetición.
- **3. Trazabilidad con Reglas de Negocio y Arquitectura:**
  Utilizado en la lógica de cálculo de estado (`AL_DIA`, `PROXIMO`, `VENCIDO`) dentro de `MantenimientoRepository`.
- **4. Casos Borde, Excepciones y Escenarios Límite (Edge Cases):**
  - *Caso de Éxito:* Configurar anticipación de 500 km para un cambio de aceite programado a los 10,000 km, de modo que avise a los 9,500 km.
  - *Caso de Error:* Configurar una anticipación mayor o igual al intervalo total de mantenimiento.
- **5. Vacíos Técnicos, Riesgos o Mejoras Propuestas:**
  Agregar los campos `anticipacionKm` y `anticipacionDias` al modelo `MantenimientoProgramado`.

---

### RF-14: El sistema debe mostrar el estado de cada actividad: al día, próxima, vencida o sin programación.
- **1. Especificación del Paso a Paso (Checklist QA):**
  1. Crear mantenimientos bajo distintas condiciones (vencido, próximo, al día).
  2. Abrir la pantalla de Dashboard o Historial de Mantenimientos.
  3. Verificar que cada elemento muestre una etiqueta (badge) con su estado visual correspondiente y colores distintivos (Verde = Al día, Amarillo = Próximo, Rojo = Vencido).
- **2. Veredicto de Cumplimiento:** [CUMPLE]
  *Justificación:* Implementado mediante el Enum `EstadoMantenimiento` (`AL_DIA`, `PROXIMO`, `VENCIDO`, `REALIZADO`) y renderizado dinámico en componentes como `ItemAlertaCard` y `DashboardScreen`.
- **3. Trazabilidad con Reglas de Negocio y Arquitectura:**
  Resultado directo del motor de evaluación de alertas ejecutado por `MantenimientoViewModel`.
- **4. Casos Borde, Excepciones y Escenarios Límite (Edge Cases):**
  - *Caso de Éxito:* Visualizar correctamente mantenimientos vencidos por fecha y próximos por kilometraje simultáneamente.
  - *Caso de Error:* Desincronización de estados si la fecha del dispositivo es alterada manualmente por el usuario.
- **5. Vacíos Técnicos, Riesgos o Mejoras Propuestas:**
  Asegurar que la evaluación de estados temporales se ejecute mediante una tarea de fondo (`WorkManager`) diaria para actualizar estados aunque la app esté cerrada.

---

### RF-15: El usuario debe poder editar, pausar, reactivar y eliminar una actividad que no tenga historial asociado.
- **1. Especificación del Paso a Paso (Checklist QA):**
  1. Seleccionar un mantenimiento programado sin ejecuciones previas (sin historial).
  2. Pulsar "Editar" y modificar sus parámetros.
  3. Pulsar "Pausar" (desactivar temporalmente las alertas) y verificar su estado.
  4. Pulsar "Eliminar" y confirmar el borrado definitivo.
- **2. Veredicto de Cumplimiento:** [CUMPLE PARCIALMENTE]
  *Justificación:* Se permite editar y eliminar, pero no existe el estado explícito de "Pausar" y "Reactivar" actividades programadas.
- **3. Trazabilidad con Reglas de Negocio y Arquitectura:**
  Requiere control de integridad referencial para impedir eliminar mantenimientos que posean registros de ejecución asociados en `MantenimientoRealizado`.
- **4. Casos Borde, Excepciones y Escenarios Límite (Edge Cases):**
  - *Caso de Éxito:* Eliminar una programación de prueba que nunca se ejecutó.
  - *Caso de Error:* Intentar eliminar una actividad programada que ya cuenta con 3 mantenimientos realizados en su historial. El sistema debe denegarlo o sugerir archivarla.
- **5. Vacíos Técnicos, Riesgos o Mejoras Propuestas:**
  Añadir el campo booleano `pausado` en `MantenimientoProgramado` y validar la existencia de historial antes de permitir la eliminación física.

---

### RF-16: El sistema debe ofrecer plantillas de mantenimiento comunes para agilizar la creación del plan.
- **1. Especificación del Paso a Paso (Checklist QA):**
  1. Abrir la pantalla de programación de mantenimiento.
  2. Desplegar el selector de plantillas predeterminadas (Aceite, Filtros, Frenos, etc.).
  3. Seleccionar una plantilla (ej. "Cambio de Aceite de Motor").
  4. Verificar que los campos de título, descripción, frecuencia en kilómetros y meses se auto-completen instantáneamente.
- **2. Veredicto de Cumplimiento:** [CUMPLE]
  *Justificación:* Implementado recientemente mediante `plantillasMantenimientoPredefinidas` y el menú desplegable en `ProgramarMantenimientoScreen`.
- **3. Trazabilidad con Reglas de Negocio y Arquitectura:**
  Facilita la experiencia de usuario (UX) reduciendo fricción en la entrada de datos offline.
- **4. Casos Borde, Excepciones y Escenarios Límite (Edge Cases):**
  - *Caso de Éxito:* Seleccionar plantilla de aceite y ajustar ligeramente el kilometraje objetivo según el tipo de aceite sintético o mineral.
  - *Caso de Error:* Modificar los campos auto-completados por la plantilla con valores incoherentes (frecuencia negativa).
- **5. Vacíos Técnicos, Riesgos o Mejoras Propuestas:**
  Permitir que el usuario cree sus propias plantillas personalizadas guardadas localmente en Room.

---

## 2.4 Registro de mantenimientos

### RF-17: El usuario debe poder registrar un mantenimiento preventivo o correctivo.
- **1. Especificación del Paso a Paso (Checklist QA):**
  1. Navegar a la pantalla de Registro de Mantenimiento (`RegistrarMantenimientoScreen`).
  2. Seleccionar el tipo de servicio: `Preventivo` o `Correctivo`.
  3. Completar los datos requeridos y guardar.
  4. Verificar en el historial que el mantenimiento quede debidamente categorizado con su tipo correspondiente.
- **2. Veredicto de Cumplimiento:** [CUMPLE]
  *Justificación:* Implementado mediante el Enum `TipoMantenimiento` (`PREVENTIVO`, `CORRECTIVO`) y selección en la UI de registro.
- **3. Trazabilidad con Reglas de Negocio y Arquitectura:**
  Persistido en la entidad `MantenimientoRealizado` y gestionado por `MantenimientoViewModel` y `MantenimientoRepository`.
- **4. Casos Borde, Excepciones y Escenarios Límite (Edge Cases):**
  - *Caso de Éxito:* Registrar un correctivo por reparación imprevista de alternador.
  - *Caso de Error:* Registrar un mantenimiento sin especificar si fue preventivo o correctivo (debe existir un valor por defecto validado).
- **5. Vacíos Técnicos, Riesgos o Mejoras Propuestas:**
  Añadir filtros analíticos en la pantalla de reportes para diferenciar costos de mantenimientos preventivos vs. correctivos.

---

### RF-18: Cada registro debe incluir como mínimo vehículo, fecha, kilometraje, tipo de servicio y descripción.
- **1. Especificación del Paso a Paso (Checklist QA):**
  1. Abrir el formulario de registro de mantenimiento.
  2. Comprobar que los campos de vehículo, fecha, kilometraje, tipo y descripción sean obligatorios (marcar asterisco o validación al enviar).
  3. Dejar un campo obligatorio vacío y pulsar guardar; verificar que se muestre error de validación.
- **2. Veredicto de Cumplimiento:** [CUMPLE]
  *Justificación:* Cubierto en la estructura de `MantenimientoRealizado` y validado en el formulario de la UI.
- **3. Trazabilidad con Reglas de Negocio y Arquitectura:**
  Garantiza la integridad mínima de auditoría del historial técnico del vehículo.
- **4. Casos Borde, Excepciones y Escenarios Límite (Edge Cases):**
  - *Caso de Éxito:* Llenar todos los campos obligatorios correctamente y registrar el mantenimiento.
  - *Caso de Error:* Intentar registrar con kilometraje en cero cuando el vehículo ya tiene acumulado un kilometraje alto.
- **5. Vacíos Técnicos, Riesgos o Mejoras Propuestas:**
  Implementar validación estricta para evitar que el kilometraje del mantenimiento registrado sea menor al último kilometraje registrado del vehículo.

---

### RF-19: El registro debe aceptar taller o responsable, costo de mano de obra, costo de repuestos y costo total.
- **1. Especificación del Paso a Paso (Checklist QA):**
  1. En el formulario de registro, ingresar el nombre del taller (ej. "Autoservicios El Maestro").
  2. Ingresar el costo de mano de obra (ej. `$50,000`).
  3. Ingresar el costo de repuestos (ej. `$120,000`).
  4. Verificar que el costo total se calcule automáticamente (`Mano de obra + Repuestos`) o se valide al guardar.
- **2. Veredicto de Cumplimiento:** [CUMPLE PARCIALMENTE]
  *Justificación:* El modelo acepta taller, costo total y lista de repuestos, pero falta separar explícitamente en la UI los campos de "costo de mano de obra" y "costo de repuestos" para autocalcular el total.
- **3. Trazabilidad con Reglas de Negocio y Arquitectura:**
  Alimenta directamente los módulos de costos y reportes financieros por vehículo (RF-36).
- **4. Casos Borde, Excepciones y Escenarios Límite (Edge Cases):**
  - *Caso de Éxito:* Registrar mano de obra de $50k y repuestos de $150k, resultando en un costo total de $200k.
  - *Caso de Error:* Ingresar valores monetarios negativos o caracteres alfanuméricos en campos numéricos de moneda.
- **5. Vacíos Técnicos, Riesgos o Mejoras Propuestas:**
  Implementar lógica reactiva en el ViewModel para autocalcular el `costoTotal` sumando mano de obra y la suma de los costos de los repuestos asociados.

---

### RF-20: El usuario debe poder adjuntar fotografías o documentos de soporte.
- **1. Especificación del Paso a Paso (Checklist QA):**
  1. En el formulario de registro de mantenimiento, pulsar "Adjuntar Evidencia / Factura".
  2. Seleccionar una imagen de la galería o tomar una foto con la cámara.
  3. Verificar que la URI de la imagen se almacene en la lista `evidenciasUrls` del registro.
  4. Consultar el detalle del mantenimiento y visualizar la vista previa de la factura o soporte.
- **2. Veredicto de Cumplimiento:** [CUMPLE]
  *Justificación:* El modelo `MantenimientoRealizado` incluye `evidenciasUrls: List<String>`, soportando rutas locales o remotas.
- **3. Trazabilidad con Reglas de Negocio y Arquitectura:**
  Requiere gestión de almacenamiento de archivos locales en el directorio de la aplicación (Scoped Storage) o almacenamiento en caché/nube.
- **4. Casos Borde, Excepciones y Escenarios Límite (Edge Cases):**
  - *Caso de Éxito:* Adjuntar factura escaneada en formato JPG o PDF y consultarla offline.
  - *Caso de Error:* Archivo adjunto eliminado del almacenamiento interno del dispositivo por el sistema operativo (limpieza de caché).
- **5. Vacíos Técnicos, Riesgos o Mejoras Propuestas:**
  Implementar copias de seguridad de las imágenes adjuntas en el directorio de documentos privado de la app para evitar pérdida de archivos por gestión de caché.

---

### RF-21: Al completar una actividad periódica, el sistema debe calcular la siguiente fecha o kilometraje según la regla configurada.
- **1. Especificación del Paso a Paso (Checklist QA):**
  1. Tener un mantenimiento programado con intervalo de `5,000 km` o `6 meses`.
  2. Registrar la ejecución de dicho mantenimiento.
  3. Verificar automáticamente que el sistema cree o actualice la siguiente programación sumando el intervalo configurado al kilometraje/fecha actual de ejecución.
- **2. Veredicto de Cumplimiento:** [CUMPLE PARCIALMENTE]
  *Justificación:* El modelo y algunos repositorios almacenan la programación, pero la lógica de autorenovación / reprogramación automática al registrar un mantenimiento periódico necesita mayor robustez en la transacción de guardado.
- **3. Trazabilidad con Reglas de Negocio y Arquitectura:**
  Crucial para la automatización del plan de mantenimiento preventivo continuo sin intervención manual repetitiva.
- **4. Casos Borde, Excepciones y Escenarios Límite (Edge Cases):**
  - *Caso de Éxito:* Se completa el cambio de aceite a los 50,000 km; el sistema programa automáticamente el siguiente a los 55,000 km.
  - *Caso de Error:* El mantenimiento se realiza muy tarde (a los 58,000 km); el siguiente debe calcularse a partir del kilometraje real de ejecución (`58,000 + 5,000 = 63,000 km`).
- **5. Vacíos Técnicos, Riesgos o Mejoras Propuestas:**
  Añadir un método transaccional en `MantenimientoRepository` que al registrar un mantenimiento vinculado a un `mantenimientoProgramadoId`, recalcule y avance automáticamente la fecha/kilogramo objetivo.

---

### RF-22: El usuario debe poder editar un registro y eliminarlo previa confirmación.
- **1. Especificación del Paso a Paso (Checklist QA):**
  1. Abrir el historial de mantenimientos realizados.
  2. Seleccionar un registro existente, pulsar "Editar", modificar datos y guardar.
  3. Seleccionar un registro, pulsar "Eliminar".
  4. Comprobar que aparezca un diálogo de confirmación ("¿Está seguro de eliminar este registro?").
  5. Confirmar y verificar que el registro sea eliminado de la base de datos y se descuente de los costos totales.
- **2. Veredicto de Cumplimiento:** [CUMPLE]
  *Justificación:* Los repositorios y pantallas permiten la eliminación y edición de registros con manejo de estado local.
- **3. Trazabilidad con Reglas de Negocio y Arquitectura:**
  Afecta los cálculos agregados de costos y estadísticas financieras del vehículo.
- **4. Casos Borde, Excepciones y Escenarios Límite (Edge Cases):**
  - *Caso de Éxito:* Editar el costo de un mantenimiento de $100k a $120k y ver reflejado el cambio instantáneamente en los reportes de gastos.
  - *Caso de Error:* Eliminar un registro histórico alterando las estadísticas acumuladas sin verificar dependencias de repuestos asociados.
- **5. Vacíos Técnicos, Riesgos o Mejoras Propuestas:**
  Implementar borrado en cascada (Cascade Delete) o desvinculación segura de repuestos asociados al mantenimiento eliminado.

---

### RF-23: El sistema debe distinguir los registros pendientes de sincronización cuando corresponda.
- **1. Especificación del Paso a Paso (Checklist QA):**
  1. Desactivar la conexión a Internet (Modo Avión).
  2. Registrar un nuevo mantenimiento o vehículo.
  3. Verificar que la UI muestre un indicador visual de "Pendiente de Sincronización" (ícono de nube con flecha o punto naranja).
  4. Reactivar internet y comprobar que el sistema sincronice con Firebase y retire el indicador.
- **2. Veredicto de Cumplimiento:** [NO CUMPLE / FALTA ESPECIFICAR]
  *Justificación:* El proyecto utiliza Firestore mediante Listeners en tiempo real, pero carece de una estrategia formal de sincronización offline (bandeja de salida / outbox pattern o flags de sincronización local `sincronizado = false`).
- **3. Trazabilidad con Reglas de Negocio y Arquitectura:**
  Esencial para una arquitectura robusta Offline-First combinada con almacenamiento remoto (Firebase Firestore / Room).
- **4. Casos Borde, Excepciones y Escenarios Límite (Edge Cases):**
  - *Caso de Éxito:* Operar durante 3 días sin internet y sincronizar exitosamente al reconectar sin pérdida de datos.
  - *Caso de Error:* Conflicto de versiones al editar el mismo registro offline y online simultáneamente (Last-Write-Wins vs Resolución de conflictos).
- **5. Vacíos Técnicos, Riesgos o Mejoras Propuestas:**
  Implementar una tabla `SyncQueue` en Room y un `WorkManager` dedicado para gestionar la cola de cambios pendientes cuando se recupere la conectividad.

---

## 2.5 Repuestos

### RF-24: El usuario debe poder asociar uno o varios repuestos a un mantenimiento.
- **1. Especificación del Paso a Paso (Checklist QA):**
  1. Durante la creación o edición de un registro de mantenimiento, acceder a la sección de Repuestos.
  2. Pulsar "Agregar Repuesto", ingresar los datos del componente y agregarlo a la lista temporal.
  3. Repetir para asociar múltiples repuestos (ej. filtro de aceite, aceite sintético, arandela).
  4. Guardar el mantenimiento y verificar la relación uno a muchos en la base de datos.
- **2. Veredicto de Cumplimiento:** [CUMPLE]
  *Justificación:* El modelo `MantenimientoRealizado` contiene una lista de objetos `Repuesto` (`val repuestos: List<Repuesto>`).
- **3. Trazabilidad con Reglas de Negocio y Arquitectura:**
  Modelado mediante relaciones Room (Embedded o tabla relacional con Foreign Key) para garantizar integridad referencial.
- **4. Casos Borde, Excepciones y Escenarios Límite (Edge Cases):**
  - *Caso de Éxito:* Asociar 5 repuestos distintos a un mantenimiento correctivo mayor de motor.
  - *Caso de Error:* Intentar guardar repuestos con costos unitarios negativos o cantidades en cero.
- **5. Vacíos Técnicos, Riesgos o Mejoras Propuestas:**
  Migrar la lista embebida de repuestos a una entidad relacional independiente `RepuestoEntity` con clave foránea hacia `MantenimientoRealizadoEntity` para permitir consultas avanzadas de inventario y garantías.

---

### RF-25: Cada repuesto debe permitir registrar nombre, marca, referencia, cantidad, valor unitario y proveedor.
- **1. Especificación del Paso a Paso (Checklist QA):**
  1. Al agregar un repuesto al mantenimiento, llenar los campos: Nombre (`Pastillas de freno`), Marca (`Brembo`), Referencia (`BR-450`), Cantidad (`2`), Valor Unitario (`$85,000`) y Proveedor (`Almacén Repuesttos S.A.`).
  2. Verificar que el sistema calcule el subtotal multiplicando cantidad por valor unitario.
  3. Guardar y verificar que todos los atributos persistan correctamente.
- **2. Veredicto de Cumplimiento:** [CUMPLE PARCIALMENTE]
  *Justificación:* El modelo `Repuesto` actual incluye id, nombre, marca, costo (total o unitario), garantía en meses y km, pero carece de campos explícitos para **referencia**, **cantidad** y **proveedor**.
- **3. Trazabilidad con Reglas de Negocio y Arquitectura:**
  Permite un control de inventario detallado y trazabilidad de los componentes instalados en cada vehículo.
- **4. Casos Borde, Excepciones y Escenarios Límite (Edge Cases):**
  - *Caso de Éxito:* Registrar cantidad 4 de bujías a $25,000 c/u, calculando subtotal $100,000.
  - *Caso de Error:* Ingresar cantidad con número negativo (`-1`) o flotante inválido.
- **5. Vacíos Técnicos, Riesgos o Mejoras Propuestas:**
  Actualizar el data class `Repuesto` para incluir `referencia: String`, `cantidad: Int` y `proveedor: String`.

---

### RF-26: El usuario debe poder registrar fecha de instalación, garantía en tiempo o kilometraje y observaciones.
- **1. Especificación del Paso a Paso (Checklist QA):**
  1. Al registrar un repuesto, configurar su fecha de instalación.
  2. Definir parámetros de garantía: en tiempo (ej. `12 meses`) y/o en kilometraje (ej. `20,000 km`).
  3. Agregar observaciones adicionales (ej. "Garantía válida únicamente con factura original").
  4. Guardar y consultar el estado de las garantías activas.
- **2. Veredicto de Cumplimiento:** [CUMPLE]
  *Justificación:* El modelo `Repuesto` ya contempla `fechaInstalacion`, `garantiaMeses`, `garantiaKm` y se integra con los registros de mantenimiento.
- **3. Trazabilidad con Reglas de Negocio y Arquitectura:**
  Permite al sistema calcular automáticamente si un repuesto instalado aún se encuentra bajo cobertura de garantía al momento de reportar una falla.
- **4. Casos Borde, Excepciones y Escenarios Límite (Edge Cases):**
  - *Caso de Éxito:* Consultar repuestos instalados hace 6 meses con garantía de 1 año (Garantía vigente).
  - *Caso de Error:* Registrar garantía con valores negativos de meses o kilometraje de cobertura.
- **5. Vacíos Técnicos, Riesgos o Mejoras Propuestas:**
  Implementar una vista de alertas de garantía próxima a vencer basada en la fecha de instalación y el kilometraje actual del vehículo.

---

### RF-27: El sistema debe mostrar qué repuestos están instalados actualmente y en qué mantenimiento fueron registrados.
- **1. Especificación del Paso a Paso (Checklist QA):**
  1. Navegar a la sección de Inventario de Repuestos o Componentes del Vehículo.
  2. Visualizar el listado de repuestos actualmente activos en el vehículo.
  3. Pulsar sobre un repuesto para ver el detalle del mantenimiento exacto en el que fue instalado, fecha y taller responsable.
- **2. Veredicto de Cumplimiento:** [CUMPLE PARCIALMENTE]
  *Justificación:* Se guardan los repuestos dentro del mantenimiento, pero falta una vista o pantalla consolidada de "Repuestos Activos / Inventario Instalado" que agrupe y muestre esta información de forma independiente.
- **3. Trazabilidad con Reglas de Negocio y Arquitectura:**
  Se apoya en consultas SQL con JOIN entre `MantenimientoRealizado` y `Repuesto` en Room.
- **4. Casos Borde, Excepciones y Escenarios Límite (Edge Cases):**
  - *Caso de Éxito:* Consultar la batería instalada actualmente y verificar la fecha exacta de su montaje.
  - *Caso de Error:* Repuesto reemplazado previamente que sigue apareciendo como instalado (falta de lógica de baja/desinstalación de repuestos).
- **5. Vacíos Técnicos, Riesgos o Mejoras Propuestas:**
  Desarrollar una pantalla dedicada `InventarioRepuestosScreen` que consulte los repuestos activos y su trazabilidad histórica.

---

## 2.6 Alertas y documentos

### RF-28: El sistema debe generar alertas locales para mantenimientos próximos y vencidos.
- **1. Especificación del Paso a Paso (Checklist QA):**
  1. Configurar mantenimientos con fechas o kilometrajes cercanos y vencidos.
  2. Verificar que la aplicación genere notificaciones locales mediante `WorkManager` y `NotificationHelper`.
  3. Comprobar que aparezca el listado de alertas activas en la pantalla de inicio (Dashboard).
- **2. Veredicto de Cumplimiento:** [CUMPLE]
  *Justificación:* Implementado mediante `AlertaMantenimientoWorker`, `NotificationHelper` y la lógica de estados en `MantenimientoViewModel`.
- **3. Trazabilidad con Reglas de Negocio y Arquitectura:**
  Uso de Android WorkManager para la ejecución confiable de tareas en segundo plano en arquitecturas offline-first.
- **4. Casos Borde, Excepciones y Escenarios Límite (Edge Cases):**
  - *Caso de Éxito:* Recibir notificación push local a la hora programada indicando que el cambio de aceite está próximo.
  - *Caso de Error:* Dispositivo con ahorro de batería extremo que restringe la ejecución de los workers en segundo plano.
- **5. Vacíos Técnicos, Riesgos o Mejoras Propuestas:**
  Permitir configurar la hora exacta de ejecución de las notificaciones diarias mediante preferencias de usuario.

---

### RF-29: El usuario debe poder activar o desactivar alertas y configurar su anticipación.
- **1. Especificación del Paso a Paso (Checklist QA):**
  1. Ir a la pantalla de Configuración / Ajustes de la aplicación.
  2. Desactivar el interruptor general de notificaciones y alertas locales.
  3. Verificar que WorkManager cancele las tareas periódicas programadas.
  4. Volver a activar y comprobar que se reprogramen correctamente.
- **2. Veredicto de Cumplimiento:** [CUMPLE PARCIALMENTE]
  *Justificación:* Existen alertas automáticas, pero falta una pantalla de configuración global de notificaciones donde el usuario pueda activar/desactivar y ajustar umbrales generales de anticipación.
- **3. Trazabilidad con Reglas de Negocio y Arquitectura:**
  Interactúa con `SharedPreferences` o `DataStore` para almacenar las preferencias de usuario y controlar la programación de `WorkManager`.
- **4. Casos Borde, Excepciones y Escenarios Límite (Edge Cases):**
  - *Caso de Éxito:* Desactivar alertas temporalmente durante un viaje o venta del vehículo sin desinstalar la app.
  - *Caso de Error:* Estado inconsistente donde las notificaciones están desactivadas en ajustes pero los workers siguen disparándose.
- **5. Vacíos Técnicos, Riesgos o Mejoras Propuestas:**
  Implementar `Jetpack DataStore Preferences` para manejar las preferencias de notificación de forma reactiva.

---

### RF-30: Al abrir una alerta, el sistema debe mostrar el vehículo, la actividad y la causa del aviso.
- **1. Especificación del Paso a Paso (Checklist QA):**
  1. Tocar una tarjeta de alerta en el Dashboard o una notificación local en la barra de estado.
  2. Verificar que la aplicación navegue al detalle del mantenimiento o despliegue un modal.
  3. Comprobar que se muestre claramente: el vehículo afectado, el nombre de la actividad y la causa exacta (ej. "Vencido por kilometraje: faltan -200 km" o "Vencido por fecha").
- **2. Veredicto de Cumplimiento:** [CUMPLE]
  *Justificación:* Las tarjetas `ItemAlertaCard` muestran título, descripción, vehículo, kilometraje objetivo y fecha objetivo con sus respectivos estados.
- **3. Trazabilidad con Reglas de Negocio y Arquitectura:**
  Navegación basada en Jetpack Navigation con paso de argumentos hacia pantallas de detalle.
- **4. Casos Borde, Excepciones y Escenarios Límite (Edge Cases):**
  - *Caso de Éxito:* Pulsar notificación con Deep Link y abrir directamente la alerta correspondiente.
  - *Caso de Error:* Tocar una alerta de un vehículo que acaba de ser eliminado de la base de datos (manejo de estado nulo).
- **5. Vacíos Técnicos, Riesgos o Mejoras Propuestas:**
  Implementar Deep Linking desde las notificaciones locales de `NotificationHelper` hacia la pantalla específica de la alerta.

---

### RF-31: El usuario debe poder posponer una alerta sin marcar el mantenimiento como realizado.
- **1. Especificación del Paso a Paso (Checklist QA):**
  1. Visualizar una alerta activa en el Dashboard.
  2. Pulsar la opción "Posponer / Sifferences" (Snooze).
  3. Seleccionar el tiempo de aplazamiento (ej. 7 días o 500 km adicionales).
  4. Verificar que la alerta desaparezca temporalmente sin alterar el registro histórico de mantenimientos realizados.
- **2. Veredicto de Cumplimiento:** [NO CUMPLE / FALTA ESPECIFICAR]
  *Justificación:* El sistema actual permite ver las alertas y registrar mantenimientos, pero no cuenta con la funcionalidad de "posponer (snooze)" una alerta específica por un tiempo determinado.
- **3. Trazabilidad con Reglas de Negocio y Arquitectura:**
  Requiere agregar un campo `fechaPosponesHasta` o `kmPosponesHasta` en la entidad `MantenimientoProgramado`.
- **4. Casos Borde, Excepciones y Escenarios Límite (Edge Cases):**
  - *Caso de Éxito:* Posponer una alerta de filtro de aire por 7 días mientras llega la cita con el taller.
  - *Caso de Error:* Posponer infinitamente una alerta crítica de frenos comprometiendo la seguridad vial.
- **5. Vacíos Técnicos, Riesgos o Mejoras Propuestas:**
  Añadir un botón de posponer en las tarjetas de alerta y gestionar el re-cálculo temporal en el ViewModel.

---

### RF-32: El sistema debe permitir registrar vencimientos de SOAT, revisión técnico-mecánica, seguro y otros documentos.
- **1. Especificación del Paso a Paso (Checklist QA):**
  1. Navegar al perfil o sección de documentación del vehículo (`VehiculosScreen`).
  2. Ingresar las fechas de vencimiento del seguro SOAT y de la Revisión Técnico-Mecánica mediante el componente `DatePickerField`.
  3. Guardar y verificar que las fechas queden asociadas al vehículo.
- **2. Veredicto de Cumplimiento:** [CUMPLE]
  *Justificación:* Implementado mediante los campos `fechaSeguroSoat` y `fechaTecnoMecanica` en el modelo `Vehiculo` y su validación de estado.
- **3. Trazabilidad con Reglas de Negocio y Arquitectura:**
  Crucial para las reglas de negocio de circulación legal del vehículo y bloqueo por falta de documentación obligatoria.
- **4. Casos Borde, Excepciones y Escenarios Límite (Edge Cases):**
  - *Caso de Éxito:* Registrar SOAT con vigencia de 1 año exacto y verificar estado habilitado.
  - *Caso de Error:* Ingresar fecha de vencimiento anterior a la fecha actual al registrar un documento nuevo.
- **5. Vacíos Técnicos, Riesgos o Mejoras Propuestas:**
  Ampliar el modelo para permitir documentos personalizados ilimitados (ej. póliza todo riesgo, tarjeta de operación) en lugar de tener campos fijos en la tabla de vehículos.

---

### RF-33: El sistema debe alertar sobre documentos próximos a vencer o vencidos.
- **1. Especificación del Paso a Paso (Checklist QA):**
  1. Configurar la fecha de SOAT con vencimiento en los próximos 15 días o ya vencida.
  2. Abrir el Dashboard y verificar que se despliegue una tarjeta de alerta de color rojo ("DOCUMENTACIÓN PENDIENTE" o "No puede circular").
  3. Comprobar que el estado general del vehículo refleje la restricción legal.
- **2. Veredicto de Cumplimiento:** [CUMPLE]
  *Justificación:* Implementado mediante las propiedades `documentacionVencidaOFaltante` y `mensajeDocumentacion` en `Vehiculo.kt` y mostrado en `DashboardScreen`.
- **3. Trazabilidad con Reglas de Negocio y Arquitectura:**
  Evalúa en tiempo real las marcas de tiempo (`System.currentTimeMillis()`) frente a las fechas almacenadas en la entidad.
- **4. Casos Borde, Excepciones y Escenarios Límite (Edge Cases):**
  - *Caso de Éxito:* SOAT vence hoy a las 11:59 PM; el sistema emite alerta roja de prohibición de circulación.
  - *Caso de Error:* Desfase horario (Timezone) que altere por unas horas la fecha límite de vencimiento del documento.
- **5. Vacíos Técnicos, Riesgos o Mejoras Propuestas:**
  Normalizar las comparaciones de fechas a medianoche (UTC / Local) para evitar falsos positivos por diferencias de horas en las marcas de tiempo.

---

## 2.7 Consultas, costos y respaldo

### RF-34: El sistema debe mostrar el historial cronológico de cada vehículo.
- **1. Especificación del Paso a Paso (Checklist QA):**
  1. Navegar a la pantalla de Historial (`HistorialScreen`).
  2. Seleccionar un vehículo en el filtro superior.
  3. Verificar que se despliegue la lista cronológica inversa (desde el evento más reciente hasta el más antiguo) combinando mantenimientos, repuestos y actualizaciones.
- **2. Veredicto de Cumplimiento:** [CUMPLE]
  *Justificación:* Implementado en `HistorialScreen.kt` con filtrado por vehículo y listados reactivos.
- **3. Trazabilidad con Reglas de Negocio y Arquitectura:**
  Consultas eficientes en Room con ordenamiento por fecha (`ORDER BY fecha DESC`).
- **4. Casos Borde, Excepciones y Escenarios Límite (Edge Cases):**
  - *Caso de Éxito:* Consultar historial de un vehículo con más de 100 eventos sin ralentización de UI (LazyColumn).
  - *Caso de Error:* Vehículo recién creado sin historial; mostrar mensaje vacío amigable.
- **5. Vacíos Técnicos, Riesgos o Mejoras Propuestas:**
  Asegurar paginación (Paging 3) en caso de historiales muy extensos para optimizar consumo de memoria RAM.

---

### RF-35: El usuario debe poder buscar y filtrar por fecha, categoría, tipo, taller y rango de costo.
- **1. Especificación del Paso a Paso (Checklist QA):**
  1. En la pantalla de historial o reportes, abrir el panel de filtros avanzados.
  2. Aplicar un filtro por rango de fechas, tipo de mantenimiento (`Preventivo`), taller específico y rango de costo (ej. entre $50k y $200k).
  3. Verificar que el listado se reduzca dinámicamente mostrando únicamente los registros que cumplen todos los criterios.
- **2. Veredicto de Cumplimiento:** [CUMPLE PARCIALMENTE]
  *Justificación:* Existen filtros básicos por vehículo y búsqueda general, pero falta un panel completo de filtros cruzados por fecha, categoría, tipo, taller y rango de costo en la UI.
- **3. Trazabilidad con Reglas de Negocio y Arquitectura:**
  Requiere consultas SQL dinámicas (Queries con cláusulas `WHERE` opcionales) en el DAO de Room o filtrado en memoria reactiva mediante Flows en el ViewModel.
- **4. Casos Borde, Excepciones y Escenarios Límite (Edge Cases):**
  - *Caso de Éxito:* Filtrar mantenimientos correctivos realizados en "Taller ABC" con costo mayor a $100,000.
  - *Caso de Error:* Rango de costo con valor mínimo mayor al valor máximo (ej. $500k a $100k).
- **5. Vacíos Técnicos, Riesgos o Mejoras Propuestas:**
  Implementar un componente UI de filtros avanzados colapsable que actualice los parámetros de consulta en el `ReportesViewModel`.

---

### RF-36: El sistema debe presentar un resumen de gastos por vehículo, categoría y periodo.
- **1. Especificación del Paso a Paso (Checklist QA):**
  1. Navegar a la pantalla de Reportes / Estadísticas (`ReportesScreen`).
  2. Seleccionar el vehículo y el periodo de tiempo (ej. mes actual, año en curso, histórico).
  3. Verificar que se muestre un resumen gráfico o tabular de los costos totales agrupados por vehículo y categoría.
- **2. Veredicto de Cumplimiento:** [CUMPLE]
  *Justificación:* Implementado en `ReportesScreen.kt` con cálculos agregados y métricas financieras detalladas.
- **3. Trazabilidad con Reglas de Negocio y Arquitectura:**
  Uso de funciones de agregación SQL (`SUM`, `GROUP BY`) o procesamiento funcional en Kotlin sobre las colecciones de mantenimientos.
- **4. Casos Borde, Excepciones y Escenarios Límite (Edge Cases):**
  - *Caso de Éxito:* Calcular correctamente el gasto total anual de una flota de dos vehículos.
  - *Caso de Error:* Periodo de tiempo sin ningún registro de gasto (división por cero o valores nulos en gráficos).
- **5. Vacíos Técnicos, Riesgos o Mejoras Propuestas:**
  Añadir gráficos circulares o de barras (usando librerías como Vico o Canvas nativo de Compose) para mejorar la visualización de gastos por categoría.

---

### RF-37: El sistema debe mostrar en el inicio vehículos con alertas, próximos mantenimientos y gastos recientes.
- **1. Especificación del Paso a Paso (Checklist QA):**
  1. Abrir la aplicación (Pantalla de Inicio / Dashboard).
  2. Verificar que se muestre el resumen del vehículo activo, indicadores de estado de la flota, mantenimientos próximos/vencidos y accesos rápidos.
- **2. Veredicto de Cumplimiento:** [CUMPLE]
  *Justificación:* Implementado de forma robusta en `DashboardScreen.kt`.
- **3. Trazabilidad con Reglas de Negocio y Arquitectura:**
  Pantalla central de la arquitectura MVVM que combina estados reactivos provenientes de múltiples repositorios (`VehiculoViewModel` y `MantenimientoViewModel`).
- **4. Casos Borde, Excepciones y Escenarios Límite (Edge Cases):**
  - *Caso de Éxito:* Carga inicial fluida y sin bloqueos de UI (main thread) al arrancar la app.
  - *Caso de Error:* Falla al cargar los datos si la base de datos local está bloqueada o corrupta.
- **5. Vacíos Técnicos, Riesgos o Mejoras Propuestas:**
  Optimizar los estados de carga (Shimmer Effects o Skeletons) mientras se resuelven las consultas asíncronas iniciales de Room.

---

### RF-38: El usuario debe poder exportar el historial de un vehículo a un formato compartible como PDF o CSV.
- **1. Especificación del Paso a Paso (Checklist QA):**
  1. Desde el detalle del vehículo o pantalla de reportes, pulsar "Exportar Historial".
  2. Seleccionar el formato de salida (`PDF` o `CSV`).
  3. Comprobar que el archivo se genere en el almacenamiento temporal del dispositivo y se invoque el selector nativo de Android (`Intent.ACTION_SEND`) para compartirlo por WhatsApp, Correo, etc.
- **2. Veredicto de Cumplimiento:** [NO CUMPLE / FALTA ESPECIFICAR]
  *Justificación:* El código actual no contiene generadores de archivos PDF ni exportadores de CSV integrados.
- **3. Trazabilidad con Reglas de Negocio y Arquitectura:**
  Requiere la incorporación de librerías de generación de PDF (ej. PdfDocument nativo de Android o iText) y generadores de cadenas CSV.
- **4. Casos Borde, Excepciones y Escenarios Límite (Edge Cases):**
  - *Caso de Éxito:* Exportar exitosamente un reporte PDF de 3 páginas con el historial completo de mantenimientos y costos del vehículo.
  - *Caso de Error:* Intentar exportar un vehículo sin ningún registro histórico (generación de archivo vacío o error controlado).
- **5. Vacíos Técnicos, Riesgos o Mejoras Propuestas:**
  Implementar un servicio de exportación en segundo plano usando corrutinas para evitar congelamiento de la interfaz al generar archivos PDF pesados.

---

### RF-39: El usuario debe poder crear y restaurar una copia de respaldo.
- **1. Especificación del Paso a Paso (Checklist QA):**
  1. Navegar a Ajustes / Respaldo de Datos.
  2. Pulsar "Crear Copia de Respaldo" y seleccionar la ubicación de destino (ej. archivo JSON o copia cifrada de la base de datos SQLite/Room).
  3. Pulsar "Restaurar Copia de Respaldo", seleccionar un archivo previo y verificar que los datos se recuperen íntegramente tras reiniciar la app.
- **2. Veredicto de Cumplimiento:** [NO CUMPLE / FALTA ESPECIFICAR]
  *Justificación:* Aunque existe integración con Firestore para persistencia en la nube, no existe una utilidad local de exportación/importación de backups (archivos .db o .json).
- **3. Trazabilidad con Reglas de Negocio y Arquitectura:**
  Fundamental para garantizar la portabilidad y seguridad de los datos en aplicaciones offline-first.
- **4. Casos Borde, Excepciones y Escenarios Límite (Edge Cases):**
  - *Caso de Éxito:* Migrar todos los datos de un dispositivo antiguo a uno nuevo mediante un archivo de respaldo local.
  - *Caso de Error:* Intentar restaurar un archivo de respaldo corrupto o con una versión de esquema de base de datos incompatible.
- **5. Vacíos Técnicos, Riesgos o Mejoras Propuestas:**
  Implementar un serializador/deserializador JSON (ej. Kotlinx Serialization o Gson) para exportar e importar todas las entidades de Room de forma segura.

---

### RF-40: El usuario debe poder eliminar definitivamente todos sus datos previa confirmación.
- **1. Especificación del Paso a Paso (Checklist QA):**
  1. Ir a Ajustes / Zona de Peligro / Borrar Datos.
  2. Pulsar "Eliminar Todos los Datos".
  3. Comprobar que aparezca un diálogo de advertencia severo requiriendo confirmación explícita (ej. escribir "BORRAR" o doble confirmación).
  4. Confirmar y verificar que la base de datos local y remota (si aplica) queden completamente vacías y la app regrese al estado inicial.
- **2. Veredicto de Cumplimiento:** [CUMPLE PARCIALMENTE]
  *Justificación:* Se pueden eliminar vehículos y mantenimientos individualmente, pero falta una función global de "Factory Reset" o borrado masivo de toda la base de datos con doble confirmación de seguridad.
- **3. Trazabilidad con Reglas de Negocio y Arquitectura:**
  Involucra la ejecución de `supportSQLiteDatabase.execSQL("VACUUM")` y el borrado de todas las tablas de Room y colecciones de Firebase.
- **4. Casos Borde, Excepciones y Escenarios Límite (Edge Cases):**
  - *Caso de Éxito:* Borrado total exitoso tras doble confirmación, dejando la aplicación como recién instalada.
  - *Caso de Error:* Interrupción abrupta durante el proceso de borrado masivo (riesgo de corrupción parcial de base de datos).
- **5. Vacíos Técnicos, Riesgos o Mejoras Propuestas:**
  Implementar un método `database.clearAllTables()` en Room junto con la purga de credenciales y archivos locales en caché.

---
**Conclusión de la Auditoría:** El proyecto "MiCarro" cuenta con una base sólida en arquitectura MVVM, Jetpack Compose y manejo offline-first, cumpliendo satisfactoriamente con los flujos críticos de gestión vehicular, alertas y registros básicos. Se recomienda priorizar el desarrollo de los vacíos técnicos identificados (exportación PDF/CSV, backups locales, historial de kilometraje y categorías editables) para alcanzar la madurez funcional total del producto.
