# Add project specific ProGuard rules here.
# You can control the set of applied configuration files using the
# proguardFiles setting in build.gradle.
#
# For more details, see
#   http://developer.android.com/guide/developing/tools/proguard.html

# If your project uses WebView with JS, uncomment the following
# and specify the fully qualified class name to the JavaScript interface
# class:
#-keepclassmembers class fqcn.of.javascript.interface.for.webview {
#   public *;
#}

# Uncomment this to preserve the line number information for
# debugging stack traces.
-keepattributes SourceFile,LineNumberTable

# If you keep the line number information, uncomment this to
# hide the original source file name.
-renamesourcefileattribute SourceFile

# ===== REGLAS PARA SPACIUS =====

# Mantener información de debugging
-keepattributes *Annotation*
-keepattributes Signature
-keepattributes Exceptions

# Firebase
-keepattributes EnclosingMethod
-keepattributes InnerClasses
-keep class com.google.firebase.** { *; }
-keep class com.google.android.gms.** { *; }
-dontwarn com.google.firebase.**
-dontwarn com.google.android.gms.**

# Firestore models (CRÍTICO - evita crashes)
-keep class com.example.spacius.data.model.** { *; }
-keepclassmembers class com.example.spacius.data.model.** { *; }

# Firestore data classes (MUY IMPORTANTE)
-keep class com.example.spacius.data.LugarFirestore { *; }
-keep class com.example.spacius.data.ReservaFirestore { *; }
-keep class com.example.spacius.data.FavoritoFirestore { *; }
-keep class com.example.spacius.data.EstadisticaLugar { *; }
-keep class com.example.spacius.data.HistoryEventFirestore { *; }
-keep class com.example.spacius.data.BloqueHorario { *; }
-keep class com.example.spacius.data.Notification { *; }
-keep class com.example.spacius.model.Booking { *; }
-keep class com.example.spacius.HistoryEvent { *; }

# Mantener todos los campos y constructores de data classes
-keepclassmembers class com.example.spacius.data.** {
    <init>(...);
    <fields>;
}
-keepclassmembers class com.example.spacius.model.** {
    <init>(...);
    <fields>;
}

# Firebase Storage
-keep class com.google.firebase.storage.** { *; }
-dontwarn com.google.firebase.storage.**

# Glide
-keep public class * implements com.bumptech.glide.module.GlideModule
-keep class * extends com.bumptech.glide.module.AppGlideModule {
 <init>(...);
}
-keep public enum com.bumptech.glide.load.ImageHeaderParser$** {
  **[] $VALUES;
  public *;
}
-keep class com.bumptech.glide.load.data.ParcelFileDescriptorRewinder$InternalRewinder {
  *** rewind();
}

# Retrofit/OkHttp (si se usa en el futuro)
-dontwarn okhttp3.**
-dontwarn okio.**
-keepattributes Signature
-keepattributes *Annotation*

# Material Components
-keep class com.google.android.material.** { *; }
-dontwarn com.google.android.material.**

# ViewBinding
-keep class * implements androidx.viewbinding.ViewBinding {
    public static *** bind(android.view.View);
    public static *** inflate(android.view.LayoutInflater);
}

# Kotlin Coroutines
-keepnames class kotlinx.coroutines.internal.MainDispatcherFactory {}
-keepnames class kotlinx.coroutines.CoroutineExceptionHandler {}
-keepclassmembers class kotlinx.** { volatile <fields>; }

# Room Database
-keep class * extends androidx.room.RoomDatabase
-keep @androidx.room.Entity class *
-dontwarn androidx.room.paging.**

# Parcelize
-keepclassmembers class * implements android.os.Parcelable {
    static ** CREATOR;
}

# Navigation Component
-keepnames class androidx.navigation.fragment.NavHostFragment
-keep class * extends androidx.fragment.app.Fragment{}