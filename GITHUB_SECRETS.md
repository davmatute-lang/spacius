# 🔐 Configuración de GitHub Secrets para CI/CD

Este proyecto usa **GitHub Secrets** para proteger credenciales sensibles en el pipeline de CI/CD.

## 📋 Secrets Requeridos

### 1. MAPS_API_KEY (REQUERIDO para builds)

**Descripción:** Google Maps API Key para la integración de mapas

**Cómo configurarlo:**

1. Ve a tu repositorio en GitHub
2. Click en **Settings** → **Secrets and variables** → **Actions**
3. Click en **New repository secret**
4. Nombre: `MAPS_API_KEY`
5. Valor: Tu Google Maps API Key
6. Click en **Add secret**

**Obtener la API Key:**
- Ve a [Google Cloud Console](https://console.cloud.google.com/google/maps-apis)
- Crea/selecciona un proyecto
- Habilita "Maps SDK for Android"
- Crea una API Key en "Credenciales"
- Copia la key y pégala en GitHub Secrets

### 2. FIREBASE_APP_ID (Opcional - para deploy automático)

**Descripción:** ID de la app en Firebase App Distribution

**Valor:** `1:51182576457:android:ed2d0e4242487f39cfb098`

**Cómo configurarlo:**
- Settings → Secrets → Actions → New secret
- Nombre: `FIREBASE_APP_ID`
- Valor: El ID mencionado arriba

### 3. CREDENTIAL_FILE_CONTENT (Opcional - para deploy)

**Descripción:** Contenido del archivo JSON de service account de Firebase

**Cómo obtenerlo:**
1. Ve a [Firebase Console](https://console.firebase.google.com/)
2. Proyecto → Settings → Service Accounts
3. Click en "Generate new private key"
4. Se descarga un archivo JSON
5. Copia **todo el contenido** del archivo
6. Pégalo en GitHub Secrets

**Cómo configurarlo:**
- Settings → Secrets → Actions → New secret
- Nombre: `CREDENTIAL_FILE_CONTENT`
- Valor: Pega el contenido JSON completo

## ✅ Verificar Configuración

Después de configurar los secrets:

1. Ve a **Actions** en tu repositorio
2. Revisa el último workflow run
3. Verifica que los pasos con 🔒 se ejecuten correctamente
4. Si hay errores, revisa los logs (los secrets no se muestran)

## 🔒 Seguridad

- ✅ Los secrets **nunca** se muestran en los logs
- ✅ Solo están disponibles durante la ejecución del workflow
- ✅ Solo los colaboradores del repo pueden verlos/editarlos
- ⚠️ Si crees que un secret fue comprometido, **rótalo inmediatamente**

## 🚨 Troubleshooting

### Error: "MAPS_API_KEY not found"
- Verifica que el secret esté configurado correctamente
- Asegúrate de que el nombre sea exactamente `MAPS_API_KEY`
- Espera 5 minutos después de crear el secret

### Build falla en CI pero funciona localmente
- Verifica que todos los secrets necesarios estén configurados
- Compara el contenido de `local.properties` local con los secrets en GitHub

### Deploy a Firebase no funciona
- Verifica que `FIREBASE_APP_ID` esté configurado
- Verifica que `CREDENTIAL_FILE_CONTENT` sea el JSON completo
- Asegúrate de que el service account tenga permisos suficientes

## 📚 Recursos

- [GitHub Actions Secrets](https://docs.github.com/en/actions/security-guides/encrypted-secrets)
- [Google Cloud API Keys](https://cloud.google.com/docs/authentication/api-keys)
- [Firebase Service Accounts](https://firebase.google.com/docs/admin/setup#initialize-sdk)

## 📧 Soporte

Si tienes problemas configurando los secrets, contacta al equipo de desarrollo.
