# ProGuard Configuration

# -allowaccessmodification: Allows access modification during obfuscation
-allowaccessmodification

# -flattenpackagehierarchy: Flattens the package hierarchy during obfuscation
-flattenpackagehierarchy

# -optimizationpasses 5: Specifies the number of optimization passes to be performed (in this case, 5 passes)
-optimizationpasses 5

# -optimizations: Specifies the optimizations to be applied during obfuscation
# !code/simplification/arithmetic: Excludes arithmetic code simplification
# !code/simplification/cast: Excludes casting code simplification
# !field/*: Excludes all field-related optimizations
# !class/merging/*: Excludes merging of classes
# method/removal/parameter: Removes unused method parameters
-optimizations !code/simplification/arithmetic,!code/simplification/cast,!field/*,!class/merging/*,method/removal/parameter

# -renamesourcefileattribute: Specifies the attributes to be renamed during obfuscation
# SourceFile: Renames the SourceFile attribute
# InnerClasses: Renames the InnerClasses attribute
# EnclosingMethod: Renames the EnclosingMethod attribute
-renamesourcefileattribute SourceFile,InnerClasses,EnclosingMethod

# This ProGuard option enables the obfuscation of class names in strings in the class files.
-adaptclassstrings

# This ProGuard option enables the adaptation of resource file names to reflect changes in class names after obfuscation
-adaptresourcefilenames

# This ProGuard option enables the adaptation of resource file contents, such as XML layouts, to reflect changes in class names after obfuscation
-adaptresourcefilecontents

# -overloadaggressively: Enables aggressive method overloading
-overloadaggressively

# Keep classes implementing GlideModule interface
-keep public class * implements com.bumptech.glide.module.GlideModule

# Keep classes extending AppGlideModule
-keep public class * extends com.bumptech.glide.module.AppGlideModule

# Keep enum classes and their values in com.bumptech.glide.load.ImageHeaderParser package
-keep public enum com.bumptech.glide.load.ImageHeaderParser$** {
  **[] $VALUES;
  public *;
}

# Keep all model classes and their members
-keep class br.zestski.owlvintage.models.**.** { *; }

# Keep all members annotated with SerializedName
-keepclassmembers @com.google.gson.annotations.SerializedName class * {
    <fields>;
}

# Keep all inner classes annotated with SerializedName
-keep class * {
    @com.google.gson.annotations.SerializedName <fields>;
}

# -ignorewarnings: Ignores ProGuard warnings during the obfuscation process
-ignorewarnings