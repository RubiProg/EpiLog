# EpiLog 🌿

Aplicación Android para el registro y seguimiento de crisis epilépticas.

## Cómo abrir en Android Studio

1. Descomprime el archivo `EpiLog.zip`
2. Abre Android Studio
3. Selecciona **File → Open**
4. Navega hasta la carpeta `EpiLog` y pulsa **OK**
5. Espera a que Gradle sincronice (puede tardar unos minutos la primera vez)
6. Conecta un dispositivo Android o usa el emulador
7. Pulsa el botón **Run ▶**

## Requisitos

- Android Studio Hedgehog o superior
- Android SDK 34
- Gradle 8.6
- Kotlin 1.9.x

## Estructura del proyecto

```
app/src/main/java/com/epilog/app/
├── data/
│   ├── entity/        → Crisis, Medicamento, Toma, PruebaMedica, DocumentoPrueba, Perfil
│   ├── dao/           → Acceso a la base de datos Room
│   ├── database/      → EpiLogDatabase (singleton)
│   └── repository/    → EpiLogRepository (fuente única de verdad)
├── ui/
│   ├── inicio/        → Pantalla principal
│   ├── crisis/        → Registro de crisis (clasificación ILAE)
│   ├── historial/     → Lista de crisis con etiquetas ILAE
│   ├── medicacion/    → Gestión de medicamentos y alarmas
│   └── perfil/        → Datos médicos, pruebas e informes
└── util/
    └── MedicacionReceiver.kt  → Notificaciones de medicación
```

## Notas de desarrollo

- Arquitectura: **MVVM** con LiveData y Room
- Clasificación de crisis según **ILAE** (3 niveles: origen, consciencia, manifestación)
- Notificaciones de medicación con **AlarmManager**
- Compatible con **modo oscuro**
- Mínimo Android 10 (API 29)
