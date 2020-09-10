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
#-keepattributes SourceFile,LineNumberTable

# If you keep the line number information, uncomment this to
# hide the original source file name.
#-renamesourcefileattribute SourceFile
-keep class net.sqlcipher.* {*;}
-keep class net.sqlcipher.database.* {*;}
#-keepclasseswithmembers class * {
#    public <init>(android.content.Context);
#    # PhotoPicker混淆
#    # Glide
#}
#-keep public class * implements com.bumptech.glide.module.GlideModule
#    -keep public enum com.bumptech.glide.load.resource.bitmap.ImageHeaderParser$** {
#        **[] $VALUES;
#        public *;
#    }
    # support-v7-appcompat
    -keep public class android.support.v7.widget.** { *; }
    -keep public class android.support.v7.internal.widget.** { *; }
    -keep public class android.support.v7.internal.view.menu.** { *; }
    -keep public class * extends android.support.v4.view.ActionProvider {
        public <init>(android.content.Context);
    }
    # support-design
    -dontwarn android.support.design.**
    -keep class android.support.design.** { *; }
    -keep interface android.support.design.** { *; }
    -keep public class android.support.design.R$* { *; }

    -dontusemixedcaseclassnames
    -dontskipnonpubliclibraryclasses
    -verbose

    #depend libraryjars
    # Optimization is turned off by default. Dex does not like code run
    # through the ProGuard optimize and preverify steps (and performs some
    # of these optimizations on its own).
    -dontoptimize
    #-dontpreverify
    # Note that if you want to enable optimization, you cannot just
    # include optimization flags in your own project configuration file;
    # instead you will need to point to the
    # "proguard-android-optimize.txt" file instead of this one from your
    # project.properties file.

    -keepattributes *Annotation*
    -keep public class com.google.vending.licensing.ILicensingService
    -keep public class com.android.vending.licensing.ILicensingService

    # For native methods, see http://proguard.sourceforge.net/manual/examples.html#native
    -keepclasseswithmembernames class * {
        native <methods>;
    }
    #tf
    # use reflect


    # sdk
    -keep class com.talkfun.**{
    	*;
    }

    -keep class fi.iki.elonen.**{
    	 *;
    }

    -keep class tv.danmaku.ijk.media.**{
    	 *;
    }

    -keep class io.agora.**{*;}

    #yunfan
    -dontwarn com.yunfan.net.**
    -keep class com.yunfan.net.**{*;}
    -keep interface com.yunfan.net.**{*;}


    #io.socket
    -keep class io.socket.**{*;}
    -keep interface io.socket.** { *; }
    -keep class org.apache.commons.net.**{*;}

#    #retrofit2
#    -dontwarn retrofit2.**
#    -keep class retrofit2.** { *; }
#    -keep interface retrofit2.** { *; }
#    -keepattributes Signature
#    -keepattributes Exceptions
#
#    #okhttp3
#    -keepattributes Signature
#    -keepattributes Annotation
#    -keep class okhttp3.** { *; }
#    -keep interface okhttp3.** { *; }
#    -dontwarn okhttp3.**
#    -dontwarn okio.**

## ---------Retrofit混淆方法---------------
-dontwarn javax.annotation.**
-dontwarn javax.inject.**
# OkHttp3
-dontwarn okhttp3.logging.**
-keep class okhttp3.internal.**{*;}
-dontwarn okio.**
# Retrofit
-dontwarn retrofit2.**
-keep class retrofit2.** { *; }
-keepattributes Signature
-keepattributes Exceptions
# RxJava RxAndroid
-dontwarn sun.misc.**
-keepclassmembers class rx.internal.util.unsafe.*ArrayQueue*Field* {
    long producerIndex;
    long consumerIndex;
}
-keepclassmembers class rx.internal.util.unsafe.BaseLinkedQueueProducerNodeRef {
    rx.internal.util.atomic.LinkedQueueNode producerNode;
}
-keepclassmembers class rx.internal.util.unsafe.BaseLinkedQueueConsumerNodeRef {
    rx.internal.util.atomic.LinkedQueueNode consumerNode;
}

# Gson
-keep class com.google.gson.stream.** { *; }
-keepattributes EnclosingMethod

# Gson
#-keep class com.demo.demo1.service.bean.**{*;} # 自定义数据模型的bean目录



    #RxJava RxAndroid
    -dontwarn sun.misc.**
    -keepclassmembers class rx.internal.util.unsafe.*ArrayQueue*Field* {
    	  long producerIndex;
    	  long consumerIndex;
    }
    -keepclassmembers class rx.internal.util.unsafe.BaseLinkedQueueProducerNodeRef {
        rx.internal.util.atomic.LinkedQueueNode producerNode;
    }
    -keepclassmembers class rx.internal.util.unsafe.BaseLinkedQueueConsumerNodeRef {
    	 rx.internal.util.atomic.LinkedQueueNode consumerNode;
    }

    #glide
    -keep public class * implements com.bumptech.glide.module.GlideModule
    -keep public class * extends com.bumptech.glide.module.AppGlideModule
    -keep public enum com.bumptech.glide.load.ImageHeaderParser$** {
    **[] $VALUES;
    public *;
    }



     # Dagger
     -keep class com.talkfun.cloudlive.di.**{
      *;
    }
        -dontwarn dagger.internal.codegen.**
        -keepclassmembers,allowobfuscation class * {
            @javax.inject.* *;
            @dagger.* *;
            <init>();
        }
        -keep class dagger.* { *; }
        -keep class javax.inject.* { *; }
        -keep class * extends dagger.internal.Binding
        -keep class * extends dagger.internal.ModuleAdapter
        -keep class * extends dagger.internal.StaticInjection


    #butterknife
    -keep class butterknife.** { *; }
    -dontwarn butterknife.internal.**
    -keep class **$$ViewBinder { *; }

    -keepclasseswithmembernames class * {
        @butterknife.* <fields>;
    }

    -keepclasseswithmembernames class * {
        @butterknife.* <methods>;
    }

    #eventbus
    -keepattributes *Annotation*
    -keepclassmembers class * {
        @org.greenrobot.eventbus.Subscribe <methods>;
    }
    -keep enum org.greenrobot.eventbus.ThreadMode { *; }

    # Only required if you use AsyncExecutor
    -keepclassmembers class * extends org.greenrobot.eventbus.util.ThrowableFailureEvent {
        <init>(java.lang.Throwable);
    }


    -keep public class * extends android.support.v4.app.FragmentActivity
    -keep public class * extends android.support.v4.app.Fragment

    -keep public class * extends android.app.Fragment
    -keep public class * extends android.app.Activity
    -keep public class * extends android.app.Application
    -keep public class * extends android.app.Service
    -keep public class * extends android.content.BroadcastReceiver
    -keep public class * extends android.content.ContentProvider
    -keep public class * extends android.app.backup.BackupAgentHelper
    -keep public class * extends android.preference.Preference
    -keep public class * extends android.support.v4.**
    -keep public class com.android.vending.licensing.ILicensingService
    -keep public class * extends android.os.Handler

    #endtf



    # keep setters in Views so that animations can still work.
    # see http://proguard.sourceforge.net/manual/examples.html#beans
    -keepclassmembers public class * extends android.view.View {
       void set*(***);
       *** get*();
    }

    # We want to keep methods in Activity that could be used in the XML attribute onClick
    -keepclassmembers class * extends android.app.Activity {
       public void *(android.view.View);
    }

    -keepclassmembers class * extends android.support.v4.app.FragmentActivity {
       public void *(android.view.View);
    }

    # For enumeration classes, see http://proguard.sourceforge.net/manual/examples.html#enumerations
    -keepclassmembers enum * {
        public static **[] values();
        public static ** valueOf(java.lang.String);
    }

    -keep class * implements android.os.Parcelable {
      public static final android.os.Parcelable$Creator *;
    }

    -keepclassmembers class **.R$* {
        public static <fields>;
    }

    # The support library contains references to newer platform versions.
    # Don't warn about those in case this app is linking against an older
    # platform version.  We know about them, and they are safe.
    -dontwarn android.support.**

    -ignorewarnings


-keep class com.alivc.**{*;}
-keep class com.aliyun.**{*;}
-dontwarn com.alivc.**
-dontwarn com.aliyun.**

-keep class com.android.jwjy.jkjyproduct.**{*;}
-dontwarn com.android.jwjy.jkjyproduct.**
