import io.restassured.builder.RequestSpecBuilder
import io.restassured.module.kotlin.extensions.Extract
import io.restassured.module.kotlin.extensions.Given
import io.restassured.module.kotlin.extensions.Then
import io.restassured.module.kotlin.extensions.When
import io.restassured.specification.RequestSpecification
import org.hamcrest.CoreMatchers.equalTo
import org.hamcrest.CoreMatchers.notNullValue
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers
import org.hamcrest.Matchers.*
import kotlin.test.Test

class ApiTests {

    private fun getRequestSpec(): RequestSpecification {
        return RequestSpecBuilder()
            .setBaseUri("https://api-adresse.data.gouv.fr/search")
            .build()
    }

    @Test
    fun checkResponse() {
        val response =
            Given {
                spec(getRequestSpec())
            } When { get("/?q=8+bd+du+port&postcode=44380")
            } Then {
                statusCode(200)
                body("type", equalTo("FeatureCollection"))
                body("version", equalTo("draft"))
                body("features.type[0]", equalTo("Feature"))
                body("features.properties.label[0]", equalTo("8 Boulevard du Port 44380 Pornichet"))
            } Extract {
                asString()
            }
    }

    @Test
    fun checkPostCodeResponseAndDefaultLimit() {
        val response =
            Given {
                spec(getRequestSpec())
            } When { get("/?q=8+bd+du+port&postcode=44380")
            } Then {
                statusCode(200)
                body("type", equalTo("FeatureCollection"))
                body("attribution", equalTo("BAN"))
                body("filters.postcode", equalTo("44380"))
                body("limit", equalTo(5))
            } Extract {
                asString()
            }
    }

    @Test
    fun checkNotFoundStatusCode() {
        val response =
            Given {
                spec(getRequestSpec())
            } When { get("/h=jj")
            } Then {
                statusCode(404)
              }
    }

    @Test
    fun checkLimitEntityResponse() {
        val response =
            Given {
                spec(getRequestSpec())
            } When { get("/?q=99890+la+su+port&limit=1")
            } Then {
                statusCode(200)
                body("type", equalTo("FeatureCollection"))
                body("features.properties.label[0]", equalTo("Rue de la Porte Sud 24540 Biron"))
                body("features.properties.city[0]", equalTo("Biron"))
                body("limit", equalTo(1))
            } Extract {
                asString()
            }
    }

    @Test
    fun checkStreetFiltersResponse() {
        val response =
            Given {
                spec(getRequestSpec())
            } When { get("/?q=paris&type=street&limit=10")
            } Then {
                statusCode(200)
                body("type", equalTo("FeatureCollection"))
                body("features.properties.label[0]", equalTo("Paris 83170 Brignoles"))
                body("features.properties.city[0]", equalTo("Brignoles"))
                body("features.properties.label[1]", equalTo("Paris 33880 Saint-Caprais-de-Bordeaux"))
                body("features.properties.city[1]", equalTo("Saint-Caprais-de-Bordeaux"))
                body("features.properties.label[2]", equalTo("Paris 40500 Saint-Sever"))
                body("features.properties.city[2]", equalTo("Saint-Sever"))
                body("features.properties.label[9]", equalTo("Paris Buton 37140 Bourgueil"))
                body("features.properties.city[9]", equalTo("Bourgueil"))
                body("limit", equalTo(10))
            } Extract {
                asString()
            }
    }

    @Test
    fun checkFullTeatSearchAndFullObject() {
        val response =
            Given {
                spec(getRequestSpec())
            } When { get("/?q=Champs+Elysees")
            } Then {
                statusCode(200)
                body("type", equalTo("FeatureCollection"))
                body("features.properties.label[0]", equalTo("Rue des Champs Elysées 31500 Toulouse"))
                body("features.properties.score[0]", equalTo(0.70968455F))
                body("features.properties.name[0]", equalTo("Rue des Champs Elysées"))
                body("features.properties.postcode[0]", equalTo("31500"))
                body("features.properties.citycode[0]", equalTo("31555"))
                body("features.properties.city[0]", equalTo("Toulouse"))
                body("features.properties.context[0]", equalTo("31, Haute-Garonne, Occitanie"))
                body("features.properties.type[0]", equalTo("street"))
                body("limit", equalTo(5))
            } Extract {
                asString()
            }
    }
}