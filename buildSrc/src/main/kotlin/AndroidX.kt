/**
@Author zhang tao
@Date   2/9/23 11:32 PM
@Desc
 */
object AndroidX {
    object Version {
        const val appCompatVersion = "1.2.0"
        const val lifecycleExtensionVersion = "2.2.0"
        const val constraintlayoutVersion = "2.0.2"
        const val recyclerViewVersion = "1.0.0"
    }

    const val appCompat = "androidx.appcompat:appcompat:${Version.appCompatVersion}"
    const val lifecycleExtension = "androidx.lifecycle:lifecycle-extensions:${Version.lifecycleExtensionVersion}"
    const val constraintlayout = "androidx.constraintlayout:constraintlayout:${Version.constraintlayoutVersion}"
    const val recyclerView = "androidx.recyclerview:recyclerview:${Version.recyclerViewVersion}"
}