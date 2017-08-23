import com.google.gson.GsonBuilder
import com.kfwong.elvis.lapi.Workbins
import io.kotlintest.matchers.*
import io.kotlintest.specs.BehaviorSpec


class WorkbinsSpec : BehaviorSpec() {

    init {
        val deserializedObject = GsonBuilder()
                .setDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS")
                .create()
                .fromJson(javaClass.getResourceAsStream("/workbins.json").bufferedReader(), Workbins::class.java)

        val workbin = deserializedObject.workbins.first()
        val folder = workbin.folders.first()
        val file = folder.files.first()

        Given("raw json") {
            When("download completed") {
                Then("it should deserialize into Workbins instance") {
                    deserializedObject should beInstanceOf(Workbins::class)
                    deserializedObject.workbins.isNotEmpty() shouldBe true
                }

                Then("it should recursively deserialize folders into list of Folder objects") {
                    deserializedObject.workbins.first().folders.isNotEmpty() shouldBe true
                }
            }
        }

        Given("a workbin object") {
            When("accessing any of its member properties") {
                Then("it should return corresponding value") {
                    workbin.id shouldBe "97c50740-8e6b-4f0e-bc3a-2712a9ea618c"
                    workbin.title shouldBe "NUMERICAL ANALYSIS I"
                    workbin.folders.size shouldEqual 2
                }
            }
        }

        Given("a folder object") {
            When("accessing any of its member properties") {
                Then("it should return corresponding value") {
                    folder.id shouldBe "9fb57f0d-27bb-4d88-a6c2-553405a77814"
                    folder.name shouldBe "Lecture Notes"
                    folder.fileCount shouldEqual 3
                    folder.folders should beInstanceOf(Collection::class)
                    folder.files should beInstanceOf(Collection::class)
                }
            }
        }

        Given("a file object"){
            When("accessing any of its member properties"){
                Then("it should return corresponding value"){
                    file.id shouldBe "66c7c5d3-a504-4dd7-9046-d4d30b3a10a1"
                    file.name shouldBe "Ch1-numerI.pdf"
                    file.description shouldBe "MA2213-Chapter 1"
                    file.type shouldBe "pdf"
                    file.isDownloaded shouldBe true
                }
            }
        }
    }
}