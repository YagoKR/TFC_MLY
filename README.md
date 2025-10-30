<img width="1867" height="980" alt="image" src="https://github.com/user-attachments/assets/3e3b3aa7-4440-4202-bcd5-86f097cb6928" />TFC Yago Martínez Loureda

La aplicación está pensada para funcionar con Android 13 (API 33). 
Será desarrollada con el emulador Pixel 7 Pro en mente.

Para clonar el repositorio es necesario tener instalado git y utilizar git clone en la carpeta que se quiera descargar el proyecto.

Además de instalar estas dependencias:

dependencies {
    implementation(libs.volley)
    implementation(libs.legacy.support.v4)
    implementation(libs.lifecycle.livedata.ktx)
    implementation(libs.lifecycle.viewmodel.ktx)
}

Esto es necesario en el fichero libs.version.toml:

volley = { group = "com.android.volley", name = "volley", version.ref = "volley" }
legacy-support-v4 = { group = "androidx.legacy", name = "legacy-support-v4", version.ref = "legacySupportV4" }
lifecycle-livedata-ktx = { group = "androidx.lifecycle", name = "lifecycle-livedata-ktx", version.ref = "lifecycleLivedataKtx" }
lifecycle-viewmodel-ktx = { group = "androidx.lifecycle", name = "lifecycle-viewmodel-ktx", version.ref = "lifecycleViewmodelKtx" }
