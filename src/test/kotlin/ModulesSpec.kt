import com.google.gson.GsonBuilder
import com.kfwong.elvis.lapi.Modules
import io.kotlintest.matchers.beInstanceOf
import io.kotlintest.matchers.should
import io.kotlintest.matchers.shouldBe
import io.kotlintest.specs.BehaviorSpec

class ModulesSpec : BehaviorSpec() {
    init {
        val deserializedObject = GsonBuilder()
                .setDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS")
                .create()
                .fromJson(javaClass.getResourceAsStream("/modules.json").bufferedReader(), Modules::class.java)

        val module = deserializedObject.modules.first()

        Given("raw json") {
            When("download completed") {
                Then("it should deserialize into Modules instance") {
                    deserializedObject should beInstanceOf(Modules::class)
                    deserializedObject.modules.isNotEmpty() shouldBe true
                }
            }
        }

        Given("a module object") {
            When("accessing any of its member properties") {
                Then("it should return corresponding value") {
                    module.id shouldBe "68f27586-4647-4064-9a39-2e7804ffa231"
                    module.code shouldBe "CS3230"
                    module.name shouldBe "DESIGN AND ANALYSIS OF ALGORITHMS"
                    module.semester shouldBe "Semester 1"
                    module.year shouldBe "2017/2018"
                }
            }
        }
    }
}