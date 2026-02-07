import static net.grinder.script.Grinder.grinder
import static org.junit.Assert.*
import static org.hamcrest.Matchers.*
import net.grinder.script.GTest
import net.grinder.script.Grinder
import org.ngrinder.script.annotation.BeforeProcess
import org.ngrinder.script.annotation.BeforeThread
import org.ngrinder.script.annotation.Test
import org.ngrinder.http.HTTPRequest
import org.ngrinder.http.HTTPResponse
import org.apache.hc.core5.http.message.BasicHeader
import groovy.json.JsonOutput

/**
 * nGrinder 컴파일 에러 수정 버전
 */
class TestRunner {

    public static GTest test
    public static HTTPRequest request
    
    // 1. IP 주소와 설정 정보
    public static String baseUrl = "http://172.16.240.153:8080/api/reservations"
    public static String token = "eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiIzMiIsImlhdCI6MTc2NzY4MDk4MiwiZXhwIjoxNzY3NzY3MzgyfQ.2bF1lnIqONRVJRGAmd3dQoxf97hy7W7M72VfYJD73t4"

    @BeforeProcess
    public static void beforeProcess() {
        test = new GTest(1, "Reservation_Test")
        request = new HTTPRequest()
        test.record(request)
    }

    @BeforeThread
    public void beforeThread() {
        grinder.statistics.delayReports = true
    }

    @Test
    public void doTest() {
        // 2. 락 타입 설정 (낙관적 락: optimistic / 비관적 락: pessimistic)
        String lockType = "optimistic" 
        String url = baseUrl + "?type=" + lockType
        
        def body = [
            userId: 32,
            productId: 12,
            amount: 1
        ]
        
        // 3. 헤더 설정 (BasicHeader 클래스 명시적 사용)
        def headers = [
            new BasicHeader("Content-Type", "application/json"),
            new BasicHeader("Authorization", "Bearer " + token)
        ]
        
        // 4. 요청 실행
        HTTPResponse response = request.POST(url, JsonOutput.toJson(body).getBytes("UTF-8"), headers)

        // 5. 검증
        if (response.statusCode == 200 || response.statusCode == 201) {
            assertThat(response.statusCode, anyOf(is(200), is(201)))
        } else {
            grinder.logger.error("실패 응답: " + response.text)
            fail("상태 코드 오류: " + response.statusCode)
        }
    }
}