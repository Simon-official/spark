package spark.route;

import org.junit.*;
import org.powermock.reflect.Whitebox;
import spark.Request;
import spark.Service;
import spark.util.SparkTestUtil;

import java.io.IOException;
import java.util.concurrent.atomic.AtomicReference;

public class FilterPriorityTest {

    private static SparkTestUtil testUtil;
    private static Service service;

    @BeforeClass
    public static void setup() throws IOException {
        testUtil = new SparkTestUtil(4567);
        service = Service.ignite();
        service.init();

        service.awaitInitialization();
    }

    @AfterClass
    public static void tearDown() {
        service.stop();
    }

    @Before
    public void before() {
        Routes routes = Whitebox.getInternalState(service, "routes");
        routes.clear();
    }

    @Test
    public void testBeforePriority() throws Exception {
        service.before((request, response) -> {
            addToOrder(request, '2');
        });

        service.before((request, response) -> {
            addToOrder(request, '1');
        }, FilterPriority.highest());

        service.before((request, response) -> {
            addToOrder(request, '2');
        });

        service.before((request, response) -> {
            addToOrder(request, '3');
        }, FilterPriority.lowest());

        service.get("/test", (request, response) -> request.attribute("order"));
        SparkTestUtil.UrlResponse response = testUtil.doMethod("GET", "/test", null);

        Assert.assertEquals("1223", response.body);
    }

    @Test
    public void testAfterPriority() throws Exception {
        AtomicReference<String> order = new AtomicReference<>("");

        service.after((request, response) -> {
            order.getAndUpdate(s -> s + '2');
        });

        service.after((request, response) -> {
            order.getAndUpdate(s -> s + '1');
        }, FilterPriority.highest());

        service.after((request, response) -> {
            order.getAndUpdate(s -> s + '2');
        });

        service.after((request, response) -> {
            order.getAndUpdate(s -> s + '3');
        }, FilterPriority.lowest());

        service.get("/test", (request, response) -> {
            return request.attribute("order");
        });
        SparkTestUtil.UrlResponse response = testUtil.doMethod("GET", "/test", null);

        Assert.assertEquals("1223", order.get());
    }

    @Test
    public void testAfterAfterPriority() throws Exception {
        AtomicReference<String> order = new AtomicReference<>("");

        service.afterAfter((request, response) -> {
            order.getAndUpdate(s -> s + '2');
        });

        service.afterAfter((request, response) -> {
            order.getAndUpdate(s -> s + '1');
        }, FilterPriority.highest());

        service.afterAfter((request, response) -> {
            order.getAndUpdate(s -> s + '2');
        });

        service.afterAfter((request, response) -> {
            order.getAndUpdate(s -> s + '3');
        }, FilterPriority.lowest());

        service.get("/test", (request, response) -> {
            return request.attribute("order");
        });
        SparkTestUtil.UrlResponse response = testUtil.doMethod("GET", "/test", null);

        Assert.assertEquals("1223", order.get());
    }

    private static void addToOrder(Request request, char c) {
        String order = request.attribute("order");
        if(order == null) {
            order = "";
        }
        order += c;
        request.attribute("order", order);
    }

}
