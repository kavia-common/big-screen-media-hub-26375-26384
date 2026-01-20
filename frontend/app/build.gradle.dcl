androidApplication {
    namespace = "org.example.app"

    dependencies {
        implementation("org.apache.commons:commons-text:1.11.0")
        implementation(project(":utilities"))

        // AndroidX UI + navigation primitives (Fragments, RecyclerView)
        implementation("androidx.fragment:fragment-ktx:1.6.2")
        implementation("androidx.recyclerview:recyclerview:1.3.2")
        implementation("androidx.appcompat:appcompat:1.6.1")

        // ExoPlayer (Media3)
        implementation("androidx.media3:media3-exoplayer:1.2.1")
        implementation("androidx.media3:media3-ui:1.2.1")
    }
}
