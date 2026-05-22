# ShopTech - E-commerce de Tecnología

## Descripción del Proyecto
ShopTech es una aplicación móvil nativa para el sistema operativo Android orientada al comercio electrónico de productos tecnológicos. La plataforma está diseñada para ofrecer una experiencia de compra moderna, segura e intuitiva, permitiendo a los usuarios explorar catálogos de smartphones, computadoras, periféricos y accesorios gamer.

## Características Principales
El aplicativo incluye las siguientes funcionalidades, soportadas por un diseño de arquitectura móvil moderno:
* Catálogo Técnico: Fichas de especificaciones detalladas, control de stock y garantías.
* Roles de Usuario: Interfaces y permisos diferenciados para Administrador, Vendedor y Comprador.
* Autenticación Segura: Inicio de sesión por correo, contraseña y autenticación biométrica (huella dactilar) nativa.
* Pasarela de Pagos: Integración con ePayco para el procesamiento de transacciones financieras digitales.
* Geolocalización: Captura de ubicación para la gestión de direcciones de entrega de los pedidos.

## Tecnologías Utilizadas
* Lenguaje: Java (Android SDK)
* Arquitectura: MVVM (Model-View-ViewModel) 
* Base de Datos: Firebase Firestore (Cloud NoSQL)
* Almacenamiento: Firebase Storage (para imágenes de productos)
* Autenticación: Firebase Authentication

## Estructura del Proyecto
El proyecto sigue el patrón de diseño MVVM para garantizar la separación de responsabilidades y la mantenibilidad del código:
* `/app/src/main/java/`: Contiene el código fuente organizado por actividades, fragmentos y modelos lógicos.
* `/app/src/main/res/`: Contiene los recursos visuales, divididos estrictamente en `layouts`, `drawables`, `values` (strings, colors, dimens) para evitar valores literales en el código.

## Autor
* John Fredy Cantor Murillo
* Proyecto final desarrollo app móvil.
