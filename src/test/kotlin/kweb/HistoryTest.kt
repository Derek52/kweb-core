import io.github.bonigarcia.seljup.Arguments
import io.github.bonigarcia.seljup.SeleniumJupiter
import io.kotlintest.shouldBe
import kweb.*
import kweb.state.KVar
import kweb.state.render
import org.junit.jupiter.api.AfterAll
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.openqa.selenium.By
import org.openqa.selenium.WebDriver
import org.openqa.selenium.WebElement
import org.openqa.selenium.support.ThreadGuard

@ExtendWith(SeleniumJupiter::class)
class HistoryTest(@Arguments("--headless") private var driver: WebDriver) {

    init {
        //ThreadGuard.protect ensures that the webdriver can only be called by the thread that created it
        //This should make this test thread safe.
        driver = ThreadGuard.protect(driver)
    }

    companion object {
        private lateinit var historyTestApp: HistoryTestApp

        @JvmStatic
        @BeforeAll
        fun setupServer() {
            HistoryTest.historyTestApp = HistoryTestApp()
        }

        @JvmStatic
        @AfterAll
        fun tearDownServer() {
            HistoryTest.historyTestApp.server.close()
        }
    }

    @Test
    fun testBackButton() {
        historyTestApp.reloadCount.value shouldBe 0
        driver.get("http://localhost:7665/0")
        driver.findElement(By.tagName("a")).let { aElement ->
            historyTestApp.url.value shouldBe "/0"
            aElement.click()
            Thread.sleep(100)
            historyTestApp.url.value shouldBe "/1"
        }
        driver.findElement(By.tagName("a")).let { aElement ->
            aElement.click()
            Thread.sleep(100)
            historyTestApp.url.value shouldBe "/2"
            historyTestApp.reloadCount.value shouldBe 1
        }
        driver.navigate().back()
        Thread.sleep(100)
        historyTestApp.url.value shouldBe "/1"
        historyTestApp.reloadCount.value shouldBe 1

        driver.navigate().forward()
        Thread.sleep(100)
        historyTestApp.url.value shouldBe "/2"
        historyTestApp.reloadCount.value shouldBe 1
    }


}

fun main() {
    HistoryTestApp()
}

class HistoryTestApp {

    internal lateinit var url: KVar<String>

    val reloadCount = KVar(0)

    val server: Kweb = Kweb(port = 7665) {
        reloadCount.value++
        this@HistoryTestApp.url = this.url
        doc.body {
            route {
                path("/{num}") { p ->
                    render(p["num"]!!.toInt()) { num ->
                        a().apply {
                            text("Next ($num)")
                            href = "/${num + 1}"
                        }
                    }
                }
            }

        }
    }

}
