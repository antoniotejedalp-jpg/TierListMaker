\# 🏆 TierListMaker - Proyecto UV (LISTI)



Sistema de creación de Tier Lists con gestión de usuarios y roles, desarrollado en JavaFX y MySQL.



\## 🛠️ Requisitos

\* JDK 17 o superior.

\* MySQL Server.

\* NetBeans o IntelliJ.



\## 🗄️ Configuración de la Base de Datos

Para que el proyecto funcione, debes importar el script SQL:

1\. Abre MySQL Workbench.

2\. Ejecuta el archivo `Tier\_List.sql` que se encuentra en la raíz de este proyecto.

3\. Asegúrate de que el usuario sea `root` y la contraseña en `ConexionDB.java` coincida con la tuya.



\## 🚀 Funcionalidades Actuales

\* \*\*Login Seguro:\*\* Diferenciación entre usuarios normales y administradores.

\* \*\*Panel de Administración:\*\* El admin puede:

&nbsp;   \* Ver lista completa de usuarios.

&nbsp;   \* Promover usuarios a administradores.

&nbsp;   \* Eliminar cuentas (usando procedimientos almacenados).

&nbsp;   \* Modificar nombres de usuario.

